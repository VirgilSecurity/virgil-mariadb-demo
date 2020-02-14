package com.virgilsecurity.demo.purekit.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
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

import com.virgilsecurity.demo.purekit.server.model.http.Patient;
import com.virgilsecurity.demo.purekit.server.model.http.ResetData;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.model.PureGrant;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PatientControllerTest extends RestDocTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private Pure pure;

	private Set<String> patientGrants;
	private String physicianGrant;

	@BeforeEach
	void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		super.setup(webApplicationContext, restDocumentation);

		ResetData resetData = this.restTemplate.postForObject("/reset", null, ResetData.class);
		this.patientGrants = resetData.getPatients();
		this.physicianGrant = resetData.getPhysicians().iterator().next();
	}

	@Test
	void get() throws Exception {
		String grant = this.patientGrants.iterator().next();
		PureGrant pureGrant = this.pure.decryptGrantFromUser(grant);

		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, grant);

		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<Patient> response = this.restTemplate.exchange("/patients/" + pureGrant.getUserId(),
				HttpMethod.GET, entity, Patient.class);
		assertEquals(200, response.getStatusCodeValue());

		Patient patient = response.getBody();
		assertNotNull(patient);
		assertTrue(isSsnValid(patient));

		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/patients/" + pureGrant.getUserId())
						.header(Constants.GRANT_HEADER, grant))
				.andDo(document("patient/get", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	void list() throws Exception {
		for (String grant : this.patientGrants) {
			// Get patients from REST
			HttpHeaders headers = new HttpHeaders();
			headers.set(Constants.GRANT_HEADER, grant);

			HttpEntity<?> entity = new HttpEntity<>(headers);

			ResponseEntity<Patient[]> response = this.restTemplate.exchange("/patients", HttpMethod.GET, entity,
					Patient[].class);
			assertEquals(200, response.getStatusCodeValue());

			// Extract patients from request
			Patient[] patients = response.getBody();
			assertNotNull(patients);
			assertEquals(2, patients.length);

			// Verify patients data
			PureGrant pureGrant = this.pure.decryptGrantFromUser(grant);
			for (Patient patient : patients) {
				if (pureGrant.getUserId().equals(patient.getId())) {
					assertTrue(isSsnValid(patient));
				} else {
					assertEquals(Constants.Texts.NO_PERMISSIONS, patient.getSsn());
				}
			}

			this.mockMvc
					.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/patients/").header(Constants.GRANT_HEADER,
							grant))
					.andDo(document("patient/list", preprocessRequest(prettyPrint()),
							preprocessResponse(prettyPrint())));
		}
	}

	@Test
	void list_noGrant() {
		ResponseEntity<String> response = this.restTemplate.getForEntity("/patients", String.class);
		assertEquals(400, response.getStatusCodeValue());
	}

	@Test
	void share() throws Exception {
		String grant = this.patientGrants.iterator().next();
		PureGrant pureGrant = this.pure.decryptGrantFromUser(grant);
		PureGrant physicianPureGrant = this.pure.decryptGrantFromUser(this.physicianGrant);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> entity;
		ResponseEntity<Patient> response;
		Patient patient;

		// Ensure physician can't read parent's ssn
		headers.set(Constants.GRANT_HEADER, this.physicianGrant);
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/patients/" + pureGrant.getUserId(), HttpMethod.GET, entity,
				Patient.class);
		assertEquals(200, response.getStatusCodeValue());

		patient = response.getBody();
		assertNotNull(patient);
		assertEquals(Constants.Texts.NO_PERMISSIONS, patient.getSsn());

		// Share SSN with physician
		headers.set(Constants.GRANT_HEADER, grant);
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/patients/share/" + physicianPureGrant.getUserId(), HttpMethod.PUT,
				entity, Patient.class);
		assertEquals(200, response.getStatusCodeValue());

		// Get patient info by physician
		headers.set(Constants.GRANT_HEADER, this.physicianGrant);
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/patients/" + pureGrant.getUserId(), HttpMethod.GET, entity,
				Patient.class);
		assertEquals(200, response.getStatusCodeValue());

		patient = response.getBody();
		assertNotNull(patient);
		assertTrue(isSsnValid(patient));

		// Document share ssn REST
		this.mockMvc
				.perform(MockMvcRequestBuilders
						.request(HttpMethod.PUT, "/patients/share/" + physicianPureGrant.getUserId())
						.header(Constants.GRANT_HEADER, grant))
				.andDo(document("patient/share", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	private boolean isSsnValid(Patient patient) {
		return ("1234567890" + StringUtils.substring(patient.getName(), -1)).equals(patient.getSsn());
	}

}
