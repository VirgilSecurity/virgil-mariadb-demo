package com.virgilsecurity.demo.purekit.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virgilsecurity.demo.purekit.server.model.SharedRole;
import com.virgilsecurity.demo.purekit.server.model.http.LabTest;
import com.virgilsecurity.demo.purekit.server.model.http.Patient;
import com.virgilsecurity.demo.purekit.server.model.http.Physician;
import com.virgilsecurity.demo.purekit.server.model.http.ResetData;
import com.virgilsecurity.demo.purekit.server.model.http.SharingData;
import com.virgilsecurity.demo.purekit.server.model.http.UserRegitration;
import com.virgilsecurity.demo.purekit.server.utils.Constants;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SharingControllerTest extends RestDocTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ObjectMapper jackson2ObjectMapper;

	private Set<UserRegitration> registeredPatients;
	private UserRegitration registeredPhysician;
	private Set<String> labTests;

	@BeforeEach
	void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		super.setup(webApplicationContext, restDocumentation);

		ResetData resetData = this.restTemplate.postForObject("/reset", null, ResetData.class);
		this.registeredPatients = resetData.getPatients();
		this.registeredPhysician = resetData.getPhysicians().iterator().next();
		this.labTests = resetData.getLabTests();
	}

	@Test
	@Disabled
	void share_patient() throws Exception {
		UserRegitration registeredPatient = this.registeredPatients.iterator().next();

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> entity;
		ResponseEntity<Patient> response;
		Patient patient;

		// Ensure physician can't read parent's ssn
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/patients/" + registeredPatient.getUserId(), HttpMethod.GET, entity,
				Patient.class);
		assertEquals(200, response.getStatusCodeValue());

		patient = response.getBody();
		assertNotNull(patient);
		assertEquals(Constants.Texts.NO_PERMISSIONS, patient.getSsn());

		// Share SSN with physician
		headers.set(Constants.GRANT_HEADER, registeredPatient.getGrant());
		SharingData sharingData = new SharingData(SharedRole.SSN.getCode(), this.registeredPhysician.getUserId());
		entity = new HttpEntity<>(sharingData, headers);
		ResponseEntity<?> sharingResponse = this.restTemplate.exchange("/share", HttpMethod.POST, entity, Void.class);
		assertEquals(200, sharingResponse.getStatusCodeValue());

		// Get patient info by physician
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/patients/" + registeredPatient.getUserId(), HttpMethod.GET, entity,
				Patient.class);
		assertEquals(200, response.getStatusCodeValue());

		patient = response.getBody();
		assertNotNull(patient);
		PatientControllerTest.verifySsn(patient);

		// Document share REST
		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/share")
						.header(Constants.GRANT_HEADER, registeredPatient.getGrant())
						.header(HttpHeaders.CONTENT_TYPE, "application/json")
						.content(jackson2ObjectMapper.writeValueAsString(sharingData)))
				.andDo(document("share", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	@Disabled
	void share() throws Exception {
		UserRegitration registeredPatient = this.registeredPatients.iterator().next();

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> entity;
		ResponseEntity<Physician> response;
		Physician physician;

		// Ensure patient can't read physician's license number
		headers.set(Constants.GRANT_HEADER, registeredPatient.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/physicians/" + this.registeredPhysician.getUserId(), HttpMethod.GET,
				entity, Physician.class);
		assertEquals(200, response.getStatusCodeValue());

		physician = response.getBody();
		assertNotNull(physician);
		assertEquals(0L, physician.getLicenseNo());

		// Share license number with patient
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		SharingData sharingData = new SharingData(SharedRole.LICENSE_NO.getCode(), registeredPatient.getUserId());
		entity = new HttpEntity<>(sharingData, headers);
		ResponseEntity<?> sharingResponse = this.restTemplate.exchange("/share", HttpMethod.POST, entity,
				Physician.class);
		assertEquals(200, sharingResponse.getStatusCodeValue());

		// Get physician't info by a patient
		headers.set(Constants.GRANT_HEADER, registeredPatient.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/physicians/" + this.registeredPhysician.getUserId(), HttpMethod.GET,
				entity, Physician.class);
		assertEquals(200, response.getStatusCodeValue());

		physician = response.getBody();
		assertNotNull(physician);
		PhysicianControllerTest.validateLicense(physician);

		// Document share license number REST
//		this.mockMvc
//				.perform(MockMvcRequestBuilders
//						.request(HttpMethod.PUT, "/physicians/share/" + registeredPatient.getUserId())
//						.header(Constants.GRANT_HEADER, registeredPhysician))
//				.andDo(document("physician/share", preprocessRequest(prettyPrint()),
//						preprocessResponse(prettyPrint())));
	}

	@Test
	@Disabled
	void share_labTest() throws Exception {
		String labTestId = this.labTests.iterator().next();

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> entity;
		ResponseEntity<LabTest> response;
		LabTest labTest;

		// Set lab test results
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());
		labTest = response.getBody();
		labTest.setResults("New results");

		entity = new HttpEntity<>(labTest, headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.PUT, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		// Ensure patient can't read lab test results
		final String patientId = labTest.getPatientId();
		UserRegitration registeredPatient = this.registeredPatients.stream().filter(it -> {
			return it.getUserId().equals(patientId);
		}).findFirst().get();

		headers.set(Constants.GRANT_HEADER, registeredPatient.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		labTest = response.getBody();
		assertNotNull(labTest);
		assertEquals(Constants.Texts.NO_PERMISSIONS, labTest.getResults());

		// Share lab test results with patient
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		SharingData sharingData = new SharingData(SharedRole.LICENSE_NO.getCode(), registeredPatient.getUserId());
		entity = new HttpEntity<>(sharingData, headers);
		ResponseEntity<?> sharingResponse = this.restTemplate
				.exchange("/share", HttpMethod.POST, entity, LabTest.class);
		assertEquals(200, sharingResponse.getStatusCodeValue());

		// Get lab test results by patient
		headers.set(Constants.GRANT_HEADER, registeredPatient.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		labTest = response.getBody();
		assertNotNull(labTest);
		assertEquals("New results", labTest.getResults());

		// Document REST
//		this.mockMvc
//				.perform(MockMvcRequestBuilders
//						.request(HttpMethod.POST, "/share")
//						.header(Constants.GRANT_HEADER, registeredPhysician))
//				.andDo(document("labtest/share", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

}
