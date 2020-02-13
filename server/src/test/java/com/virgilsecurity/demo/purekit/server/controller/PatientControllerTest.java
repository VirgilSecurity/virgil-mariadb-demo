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

	private Set<String> grants;

	@BeforeEach
	void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		super.setup(webApplicationContext, restDocumentation);
		this.grants = this.restTemplate.getForObject("/reset", ResetData.class).getPatients();
	}

	@Test
	void get() throws Exception {
		String grant = this.grants.iterator().next();
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
		for (String grant : this.grants) {
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
					assertEquals(Constants.NO_PERMISSIONS_TEXT, patient.getSsn());
				}
			}

			this.mockMvc
					.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/patient/").header(Constants.GRANT_HEADER,
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

	private boolean isSsnValid(Patient patient) {
		return ("1234567890" + StringUtils.substring(patient.getName(), -1)).equals(patient.getSsn());
	}

}
