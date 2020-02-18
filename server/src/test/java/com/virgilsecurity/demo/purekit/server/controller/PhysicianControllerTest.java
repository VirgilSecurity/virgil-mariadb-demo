package com.virgilsecurity.demo.purekit.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

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
import com.virgilsecurity.demo.purekit.server.model.http.UserRegistration;
import com.virgilsecurity.demo.purekit.server.utils.Constants;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PhysicianControllerTest extends RestDocTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private UserRegistration registeredPhysician;

	@BeforeEach
	void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		super.setup(webApplicationContext, restDocumentation);

		ResetData resetData = this.restTemplate.postForObject("/reset", null, ResetData.class);
		this.registeredPhysician = resetData.getPhysicians().iterator().next();
	}

	@Test
	void get() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<Physician> response = this.restTemplate.exchange(
				"/physicians/" + this.registeredPhysician.getUserId(), HttpMethod.GET, entity, Physician.class);
		assertEquals(200, response.getStatusCodeValue());

		Physician physician = response.getBody();
		assertNotNull(physician);
		validate(physician);

		this.mockMvc
				.perform(MockMvcRequestBuilders
						.request(HttpMethod.GET, "/physicians/" + this.registeredPhysician.getUserId())
						.header(Constants.GRANT_HEADER, this.registeredPhysician.getGrant()))
				.andDo(document("physician/get", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	void list() throws Exception {
		// Get physicians from REST
		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<Physician[]> response = this.restTemplate.exchange("/physicians", HttpMethod.GET, entity,
				Physician[].class);
		assertEquals(200, response.getStatusCodeValue());

		// Extract patients from request
		Physician[] physicians = response.getBody();
		assertNotNull(physicians);
		assertEquals(1, physicians.length);

		// Verify patients data
		Physician physician = physicians[0];
		assertEquals(this.registeredPhysician.getUserId(), physician.getId());
		assertEquals("PhysicianEntity 1", physician.getName());
		validate(physician);

		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/physicians/").header(Constants.GRANT_HEADER,
						this.registeredPhysician.getGrant()))
				.andDo(document("physician/list", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	void list_noGrant() {
		ResponseEntity<String> response = this.restTemplate.getForEntity("/physicians", String.class);
		assertEquals(400, response.getStatusCodeValue());
	}

	@Test
	void cors() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders
						.request(HttpMethod.OPTIONS, "/physicians/" + this.registeredPhysician.getUserId())
						.header(Constants.GRANT_HEADER, this.registeredPhysician.getGrant()))
				.andDo(document("physician/cors", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	public static void validate(Physician physician) {
		assertEquals("1001", physician.getLicenseNo());
	}

}
