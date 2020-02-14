package com.virgilsecurity.demo.purekit.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

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

import com.google.gson.Gson;
import com.virgilsecurity.demo.purekit.server.model.http.LabTest;
import com.virgilsecurity.demo.purekit.server.model.http.ResetData;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.model.PureGrant;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LabTestControllerTest extends RestDocTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private Pure pure;

	private Set<String> patientGrants;
	private String physicianGrant;
	private Set<String> labTests;

	@BeforeEach
	void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		super.setup(webApplicationContext, restDocumentation);

		ResetData resetData = this.restTemplate.postForObject("/reset", null, ResetData.class);
		this.patientGrants = resetData.getPatients();
		this.physicianGrant = resetData.getPhysicians().iterator().next();
		this.labTests = resetData.getLabTests();
	}

	@Test
	void get_byPhysician() throws Exception {
		PureGrant pureGrant = this.pure.decryptGrantFromUser(this.physicianGrant);

		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.physicianGrant);
		HttpEntity<?> entity = new HttpEntity<>(headers);

		for (String labTestId : this.labTests) {
			ResponseEntity<LabTest> response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET,
					entity, LabTest.class);
			assertEquals(200, response.getStatusCodeValue());

			LabTest labTest = response.getBody();
			assertNotNull(labTest);
			assertEquals(labTestId, labTest.getId());
			assertEquals(pureGrant.getUserId(), labTest.getPhysicianId());
			assertEquals(Constants.Texts.NOT_READY, labTest.getResults());
		}

		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/lab-tests/" + this.labTests.iterator().next())
						.header(Constants.GRANT_HEADER, this.physicianGrant))
				.andDo(document("labtest/get", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	void list() throws Exception {
		// Get labTests from REST
		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.physicianGrant);
		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<LabTest[]> response = this.restTemplate.exchange("/lab-tests", HttpMethod.GET, entity,
				LabTest[].class);
		assertEquals(200, response.getStatusCodeValue());

		LabTest[] retrievedLabTests = response.getBody();
		assertNotNull(retrievedLabTests);
		assertEquals(3, retrievedLabTests.length);

		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/lab-tests").header(Constants.GRANT_HEADER,
						this.physicianGrant))
				.andDo(document("labtest/list", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	void list_noGrant() {
		ResponseEntity<String> response = this.restTemplate.getForEntity("/lab-tests", String.class);
		assertEquals(400, response.getStatusCodeValue());
	}

	@Test
	void update() throws Exception {
		String labTestId = this.labTests.iterator().next();

		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.physicianGrant);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<LabTest> response;

		// Get labTest
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		LabTest labTest = response.getBody();
		assertNotNull(labTest);

		labTest.setResults("Here are your results");

		// Update labTest
		entity = new HttpEntity<>(labTest, headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.PUT, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		// Verify labTest
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		LabTest updatedLabTest = response.getBody();
		assertNotNull(updatedLabTest);
		assertEquals(labTest.getResults(), updatedLabTest.getResults());

		// Document REST
		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.PUT, "/lab-tests/" + labTestId)
						.content(new Gson().toJson(labTest)).header(Constants.GRANT_HEADER, this.physicianGrant))
				.andDo(document("labtest/update", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	void share() throws Exception {
		String labTestId = this.labTests.iterator().next();

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> entity;
		ResponseEntity<LabTest> response;
		LabTest labTest;

		// Set lab test results
		headers.set(Constants.GRANT_HEADER, this.physicianGrant);
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
		String patientGrant = this.patientGrants.stream().filter(grant -> {
			try {
				return this.pure.decryptGrantFromUser(grant).getUserId().equals(patientId);
			} catch (PureException e) {
				return false;
			}
		}).findFirst().get();

		headers.set(Constants.GRANT_HEADER, patientGrant);
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		labTest = response.getBody();
		assertNotNull(labTest);
		assertEquals(Constants.Texts.NO_PERMISSIONS, labTest.getResults());

		// Share lab test results with patient
		headers.set(Constants.GRANT_HEADER, this.physicianGrant);
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId + "/share/" + patientId, HttpMethod.PUT, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		// Get lab test results by patient
		headers.set(Constants.GRANT_HEADER, patientGrant);
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		labTest = response.getBody();
		assertNotNull(labTest);
		assertEquals("New results", labTest.getResults());

		// Document REST
		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.PUT, "/lab-tests/" + labTestId + "/share/" + patientId)
						.header(Constants.GRANT_HEADER, physicianGrant))
				.andDo(document("labtest/share", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

}
