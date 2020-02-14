package com.virgilsecurity.demo.purekit.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.virgilsecurity.demo.purekit.server.model.http.Physician;
import com.virgilsecurity.demo.purekit.server.model.http.ResetData;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.model.PureGrant;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PhysicianControllerTest extends RestDocTest {

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
		PureGrant pureGrant = this.pure.decryptGrantFromUser(this.physicianGrant);

		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.physicianGrant);
		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<Physician> response = this.restTemplate.exchange("/physicians/" + pureGrant.getUserId(),
				HttpMethod.GET, entity, Physician.class);
		assertEquals(200, response.getStatusCodeValue());

		Physician physician = response.getBody();
		assertNotNull(physician);
		assertTrue(isLicenseValid(physician));

		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/physicians/" + pureGrant.getUserId())
						.header(Constants.GRANT_HEADER, this.physicianGrant))
				.andDo(document("physician/get", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	void list() throws Exception {
		// Get physicians from REST
		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.physicianGrant);
		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<Physician[]> response = this.restTemplate.exchange("/physicians", HttpMethod.GET, entity,
				Physician[].class);
		assertEquals(200, response.getStatusCodeValue());

		// Extract patients from request
		Physician[] physicians = response.getBody();
		assertNotNull(physicians);
		assertEquals(1, physicians.length);

		// Verify patients data
		PureGrant pureGrant = this.pure.decryptGrantFromUser(this.physicianGrant);
		Physician physician = physicians[0];
		assertEquals(pureGrant.getUserId(), physician.getId());
		assertEquals("PhysicianEntity 1", physician.getName());
		assertTrue(isLicenseValid(physician));

		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/physicians/").header(Constants.GRANT_HEADER,
						this.physicianGrant))
				.andDo(document("physician/list", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	void list_noGrant() {
		ResponseEntity<String> response = this.restTemplate.getForEntity("/physicians", String.class);
		assertEquals(400, response.getStatusCodeValue());
	}

	@Test
	void share() throws Exception {
		String patientGrant = this.patientGrants.iterator().next();
		PureGrant patientPureGrant = this.pure.decryptGrantFromUser(patientGrant);
		PureGrant physicianPureGrant = this.pure.decryptGrantFromUser(this.physicianGrant);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> entity;
		ResponseEntity<Physician> response;
		Physician physician;

		// Ensure patient can't read physician's license number
		headers.set(Constants.GRANT_HEADER, patientGrant);
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/physicians/" + physicianPureGrant.getUserId(), HttpMethod.GET, entity,
				Physician.class);
		assertEquals(200, response.getStatusCodeValue());

		physician = response.getBody();
		assertNotNull(physician);
		assertEquals(0L, physician.getLicenseNo());

		// Share license number with patient
		headers.set(Constants.GRANT_HEADER, this.physicianGrant);
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/physicians/share/" + patientPureGrant.getUserId(), HttpMethod.PUT,
				entity, Physician.class);
		assertEquals(200, response.getStatusCodeValue());

		// Get physician't info by a patient
		headers.set(Constants.GRANT_HEADER, patientGrant);
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/physicians/" + physicianPureGrant.getUserId(), HttpMethod.GET, entity,
				Physician.class);
		assertEquals(200, response.getStatusCodeValue());

		physician = response.getBody();
		assertNotNull(physician);
		assertTrue(isLicenseValid(physician));

		// Document share license number REST
		this.mockMvc
				.perform(MockMvcRequestBuilders
						.request(HttpMethod.PUT, "/physicians/share/" + patientPureGrant.getUserId())
						.header(Constants.GRANT_HEADER, physicianGrant))
				.andDo(document("physician/share", preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
	}

	private boolean isLicenseValid(Physician physician) {
		return 1001L == physician.getLicenseNo();
	}

}
