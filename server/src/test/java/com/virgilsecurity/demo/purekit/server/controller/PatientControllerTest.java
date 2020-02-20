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

import com.virgilsecurity.demo.purekit.server.model.http.Patient;
import com.virgilsecurity.demo.purekit.server.model.http.ResetData;
import com.virgilsecurity.demo.purekit.server.model.http.UserRegistration;
import com.virgilsecurity.demo.purekit.server.utils.Constants;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PatientControllerTest extends RestDocTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private Set<UserRegistration> registeredPatients;

	@BeforeEach
	void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		super.setup(webApplicationContext, restDocumentation);

		ResetData resetData = this.restTemplate.postForObject("/reset", null, ResetData.class);
		this.registeredPatients = resetData.getPatients();
	}

	@Test
	void get() throws Exception {
		UserRegistration registeredPatient = this.registeredPatients.iterator().next();

		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.GRANT_HEADER, registeredPatient.getGrant());

		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<Patient> response = this.restTemplate.exchange("/patients/" + registeredPatient.getUserId(),
				HttpMethod.GET, entity, Patient.class);
		assertEquals(200, response.getStatusCodeValue());

		Patient patient = response.getBody();
		assertNotNull(patient);
		validate(patient);

		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/patients/" + registeredPatient.getUserId())
						.header(Constants.GRANT_HEADER, registeredPatient.getGrant()))
				.andDo(document("patient/get", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	void list() throws Exception {
		for (UserRegistration registeredPatient : this.registeredPatients) {
			// Get patients from REST
			HttpHeaders headers = new HttpHeaders();
			headers.set(Constants.GRANT_HEADER, registeredPatient.getGrant());

			HttpEntity<?> entity = new HttpEntity<>(headers);

			ResponseEntity<Patient[]> response = this.restTemplate.exchange("/patients", HttpMethod.GET, entity,
					Patient[].class);
			assertEquals(200, response.getStatusCodeValue());

			// Extract patients from request
			Patient[] patients = response.getBody();
			assertNotNull(patients);
			assertEquals(1, patients.length);

			// Verify patients data
			for (Patient patient : patients) {
				if (registeredPatient.getUserId().equals(patient.getId())) {
					validate(patient);
				} else {
					assertNull(patient.getSsn());
				}
			}

			this.mockMvc
					.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/patients/").header(Constants.GRANT_HEADER,
							registeredPatient.getGrant()))
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
	void assignedPhysicians() throws Exception {
		UserRegistration registeredPatient = this.registeredPatients.iterator().next();

		this.mockMvc
				.perform(MockMvcRequestBuilders
						.request(HttpMethod.GET, "/patients/" + registeredPatient.getUserId() + "/physicians")
						.header(Constants.GRANT_HEADER, registeredPatient.getGrant()))
				.andDo(document("patient/physicians", preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
	}

	public static void validate(Patient patient) {
		assertEquals("Alice", patient.getName());
		assertEquals("12345678901", patient.getSsn());
	}

}
