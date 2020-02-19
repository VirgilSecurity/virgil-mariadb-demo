package com.virgilsecurity.demo.purekit.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virgilsecurity.demo.purekit.server.model.TestStatus;
import com.virgilsecurity.demo.purekit.server.model.http.LabTest;
import com.virgilsecurity.demo.purekit.server.model.http.ResetData;
import com.virgilsecurity.demo.purekit.server.model.http.UserRegistration;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
import com.virgilsecurity.demo.purekit.server.utils.Utils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LabTestControllerTest extends RestDocTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ObjectMapper jackson2ObjectMapper;

	private Set<UserRegistration> registeredPatients;
	private UserRegistration registeredPhysician;
	private UserRegistration laboratory;
	private Set<String> labTests;

	@BeforeEach
	void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		super.setup(webApplicationContext, restDocumentation);

		ResetData resetData = this.restTemplate.postForObject("/reset", null, ResetData.class);
		this.registeredPatients = resetData.getPatients();
		this.registeredPhysician = resetData.getPhysicians().iterator().next();
		this.laboratory = resetData.getLaboratories().iterator().next();
		this.labTests = resetData.getLabTests();
	}

	@Test
	void create() throws Exception {
		String patientId = this.registeredPatients.iterator().next().getUserId();

		// Create laboratory test
		LabTest labTest = new LabTest("Lab test No8", patientId, Utils.today());

		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		HttpEntity<LabTest> entity = new HttpEntity<>(labTest, headers);
		ResponseEntity<String> createResponse = this.restTemplate.exchange("/lab-tests", HttpMethod.POST, entity,
				String.class);
		assertEquals(200, createResponse.getStatusCodeValue());

		String labTestId = createResponse.getBody();

		// Verify laboratory test
		entity = new HttpEntity<>(headers);
		ResponseEntity<LabTest> response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity,
				LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		LabTest readLabTest = response.getBody();
		assertNotNull(readLabTest);
		assertEquals(labTest.getName(), readLabTest.getName());
		assertEquals(labTest.getPatientId(), readLabTest.getPatientId());
		assertEquals(this.registeredPhysician.getUserId(), readLabTest.getPhysicianId());
		assertEquals(labTest.getTestDate(), readLabTest.getTestDate());
		assertNull(readLabTest.getResults());
		assertEquals(TestStatus.NOT_READY, readLabTest.getStatus());

		// Document REST
		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/lab-tests")
						.content(jackson2ObjectMapper.writeValueAsString(labTest))
						.header(Constants.GRANT_HEADER, this.registeredPhysician.getGrant())
						.header(HttpHeaders.CONTENT_TYPE, "application/json"))
				.andDo(document("labtest/create", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	void get_byPhysician() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		HttpEntity<?> entity = new HttpEntity<>(headers);

		for (String labTestId : this.labTests) {
			ResponseEntity<LabTest> response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET,
					entity, LabTest.class);
			assertEquals(200, response.getStatusCodeValue());

			LabTest labTest = response.getBody();
			assertNotNull(labTest);
			assertEquals(labTestId, labTest.getId());
			assertEquals(this.registeredPhysician.getUserId(), labTest.getPhysicianId());
			assertNull(labTest.getResults());
			assertEquals(TestStatus.NOT_READY, labTest.getStatus());
		}

		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/lab-tests/" + this.labTests.iterator().next())
						.header(Constants.GRANT_HEADER, this.registeredPhysician.getGrant()))
				.andDo(document("labtest/get", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	void list() throws Exception {
		// Get labTests from REST
		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<LabTest[]> response = this.restTemplate.exchange("/lab-tests", HttpMethod.GET, entity,
				LabTest[].class);
		assertEquals(200, response.getStatusCodeValue());

		LabTest[] retrievedLabTests = response.getBody();
		assertNotNull(retrievedLabTests);
		assertEquals(2, retrievedLabTests.length);

		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/lab-tests").header(Constants.GRANT_HEADER,
						this.registeredPhysician.getGrant()))
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
		headers.set(Constants.GRANT_HEADER, this.laboratory.getGrant());
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
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		LabTest updatedLabTest = response.getBody();
		assertNotNull(updatedLabTest);
		assertEquals(labTest.getResults(), updatedLabTest.getResults());
		assertEquals(TestStatus.OK, updatedLabTest.getStatus());

		// Document REST
		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.PUT, "/lab-tests/" + labTestId)
						.content(jackson2ObjectMapper.writeValueAsBytes(labTest))
						.header(Constants.GRANT_HEADER, this.registeredPhysician.getGrant())
						.header(HttpHeaders.CONTENT_TYPE, "application/json"))
				.andDo(document("labtest/update", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

}
