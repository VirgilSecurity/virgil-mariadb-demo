package com.virgilsecurity.demo.purekit.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import java.util.Date;
import java.util.Set;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virgilsecurity.demo.purekit.server.model.http.Prescription;
import com.virgilsecurity.demo.purekit.server.model.http.ResetData;
import com.virgilsecurity.demo.purekit.server.model.http.UserRegistration;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
import com.virgilsecurity.demo.purekit.server.utils.Utils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PrescriptionControllerTest extends RestDocTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ObjectMapper jackson2ObjectMapper;

	private Set<UserRegistration> registeredPatients;
	private UserRegistration registeredPhysician;
	private Set<String> prescriptions;

	@BeforeEach
	void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		super.setup(webApplicationContext, restDocumentation);

		ResetData resetData = this.restTemplate.postForObject("/reset", null, ResetData.class);
		this.registeredPatients = resetData.getPatients();
		this.registeredPhysician = resetData.getPhysicians().iterator().next();
		this.prescriptions = resetData.getPrescriptions();
	}

	@Test
	void create() throws Exception {
		String patientId = this.registeredPatients.iterator().next().getUserId();

		// Create prescription
		Prescription prescription = new Prescription(patientId, "The notes", Utils.yesterday(), Utils.today());

		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		HttpEntity<Prescription> entity = new HttpEntity<>(prescription, headers);
		ResponseEntity<String> createResponse = this.restTemplate.exchange("/prescriptions", HttpMethod.POST, entity,
				String.class);
		assertEquals(200, createResponse.getStatusCodeValue());

		String prescriptionId = createResponse.getBody();

		// Verify prescription
		entity = new HttpEntity<>(headers);
		ResponseEntity<Prescription> response = this.restTemplate.exchange("/prescriptions/" + prescriptionId,
				HttpMethod.GET, entity, Prescription.class);
		assertEquals(200, response.getStatusCodeValue());

		Prescription readPrescription = response.getBody();
		assertNotNull(readPrescription);
		assertEquals(prescription.getNotes(), readPrescription.getNotes());
		assertEquals(prescription.getAssingDate(), readPrescription.getAssingDate());
		assertEquals(prescription.getReleaseDate(), readPrescription.getReleaseDate());

		// Document REST
		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/prescriptions")
						.content(jackson2ObjectMapper.writeValueAsString(prescription))
						.header(Constants.GRANT_HEADER, this.registeredPhysician.getGrant())
						.header(HttpHeaders.CONTENT_TYPE, "application/json"))
				.andDo(document("prescription/create", preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
	}

	@Test
	void get_byPhysician() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		HttpEntity<?> entity = new HttpEntity<>(headers);

		for (String prescriptionId : this.prescriptions) {
			ResponseEntity<Prescription> response = this.restTemplate.exchange("/prescriptions/" + prescriptionId,
					HttpMethod.GET, entity, Prescription.class);
			assertEquals(200, response.getStatusCodeValue());

			Prescription prescription = response.getBody();
			assertNotNull(prescription);
			assertEquals(prescriptionId, prescription.getId());
			assertEquals(this.registeredPhysician.getUserId(), prescription.getPhysicianId());
		}

		this.mockMvc
				.perform(MockMvcRequestBuilders
						.request(HttpMethod.GET, "/prescriptions/" + this.prescriptions.iterator().next())
						.header(Constants.GRANT_HEADER, this.registeredPhysician.getGrant()))
				.andDo(document("prescription/get", preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
	}

	@Test
	void list() throws Exception {
		// Get prescriptions from REST
		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<Prescription[]> response = this.restTemplate.exchange("/prescriptions", HttpMethod.GET, entity,
				Prescription[].class);
		assertEquals(200, response.getStatusCodeValue());

		Prescription[] retrievedPrescriptions = response.getBody();
		assertNotNull(retrievedPrescriptions);
		assertEquals(2, retrievedPrescriptions.length);

		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/prescriptions").header(Constants.GRANT_HEADER,
						this.registeredPhysician.getGrant()))
				.andDo(document("prescription/list", preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
	}

	@Test
	void list_noGrant() {
		ResponseEntity<String> response = this.restTemplate.getForEntity("/prescriptions", String.class);
		assertEquals(400, response.getStatusCodeValue());
	}

	@Test
	void update() throws Exception {
		String prescriptionId = this.prescriptions.iterator().next();

		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<Prescription> response;

		// Get prescription
		response = this.restTemplate.exchange("/prescriptions/" + prescriptionId, HttpMethod.GET, entity,
				Prescription.class);
		assertEquals(200, response.getStatusCodeValue());

		Prescription prescription = response.getBody();
		assertNotNull(prescription);

		String newNotes = "New notes";
		Date newAssignDate = Utils.yesterday();
		Date newReleaseDate = Utils.today();
		prescription.setNotes(newNotes);
		prescription.setAssingDate(newAssignDate);
		prescription.setReleaseDate(newReleaseDate);

		// Update prescription
		entity = new HttpEntity<>(prescription, headers);
		response = this.restTemplate.exchange("/prescriptions/" + prescriptionId, HttpMethod.PUT, entity,
				Prescription.class);
		assertEquals(200, response.getStatusCodeValue());

		// Verify prescription
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/prescriptions/" + prescriptionId, HttpMethod.GET, entity,
				Prescription.class);
		assertEquals(200, response.getStatusCodeValue());

		Prescription updatedPrescription = response.getBody();
		assertNotNull(updatedPrescription);
		assertEquals(newNotes, updatedPrescription.getNotes());
		assertEquals(newAssignDate, updatedPrescription.getAssingDate());
		assertEquals(newReleaseDate, updatedPrescription.getReleaseDate());

		// Document REST
		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.PUT, "/prescriptions/" + prescriptionId)
						.content(jackson2ObjectMapper.writeValueAsString(prescription))
						.header(Constants.GRANT_HEADER, this.registeredPhysician.getGrant())
						.header(HttpHeaders.CONTENT_TYPE, "application/json"))
				.andDo(document("prescription/update", preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
	}

}
