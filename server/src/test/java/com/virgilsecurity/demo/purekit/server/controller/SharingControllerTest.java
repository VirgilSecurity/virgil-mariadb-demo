/*
 * Copyright (c) 2015-2020, Virgil Security, Inc.
 *
 * Lead Maintainer: Virgil Security Inc. <support@virgilsecurity.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     (1) Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *     (2) Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *     (3) Neither the name of virgil nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import com.virgilsecurity.demo.purekit.server.model.SharedRole;
import com.virgilsecurity.demo.purekit.server.model.TestStatus;
import com.virgilsecurity.demo.purekit.server.model.http.LabTest;
import com.virgilsecurity.demo.purekit.server.model.http.Patient;
import com.virgilsecurity.demo.purekit.server.model.http.Physician;
import com.virgilsecurity.demo.purekit.server.model.http.ResetData;
import com.virgilsecurity.demo.purekit.server.model.http.SharingData;
import com.virgilsecurity.demo.purekit.server.model.http.UserRegistration;
import com.virgilsecurity.demo.purekit.server.utils.Constants;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SharingControllerTest extends RestDocTest {

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
	void share_patient() throws Exception {
		UserRegistration registeredPatient = this.registeredPatients.iterator().next();

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
		assertNull(patient.getSsn());

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
		PatientControllerTest.validate(patient);

		// Document share REST
		this.mockMvc
				.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/share")
						.header(Constants.GRANT_HEADER, registeredPatient.getGrant())
						.header(HttpHeaders.CONTENT_TYPE, "application/json")
						.content(jackson2ObjectMapper.writeValueAsString(sharingData)))
				.andDo(document("share", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	void share_physician() throws Exception {
		UserRegistration registeredPatient = this.registeredPatients.iterator().next();

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> entity;
		ResponseEntity<Physician> response;
		Physician physician;

		// Ensure patient can't read physician's License Number
		headers.set(Constants.GRANT_HEADER, registeredPatient.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/physicians/" + this.registeredPhysician.getUserId(), HttpMethod.GET,
				entity, Physician.class);
		assertEquals(200, response.getStatusCodeValue());

		physician = response.getBody();
		assertNotNull(physician);
		assertNull(physician.getLicenseNo());

		// Share License Number with physician
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		SharingData sharingData = new SharingData(SharedRole.LICENSE_NO.getCode(), registeredPatient.getUserId());
		entity = new HttpEntity<>(sharingData, headers);
		ResponseEntity<?> sharingResponse = this.restTemplate.exchange("/share", HttpMethod.POST, entity, Void.class);
		assertEquals(200, sharingResponse.getStatusCodeValue());

		// Get physician info by patient
		headers.set(Constants.GRANT_HEADER, registeredPatient.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/physicians/" + this.registeredPhysician.getUserId(), HttpMethod.GET,
				entity, Physician.class);
		assertEquals(200, response.getStatusCodeValue());

		physician = response.getBody();
		assertNotNull(physician);
		PhysicianControllerTest.validate(physician);
	}

	@Test
	void share_labTest() throws Exception {
		String labTestId = this.labTests.iterator().next();

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> entity;
		ResponseEntity<LabTest> response;
		LabTest labTest;

		// Set lab test results by laboratory
		headers.set(Constants.GRANT_HEADER, this.laboratory.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());
		labTest = response.getBody();
		labTest.setResults("New results");

		entity = new HttpEntity<>(labTest, headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.PUT, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		// Ensure physician can read lab test results
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		labTest = response.getBody();
		assertNotNull(labTest);
		assertEquals("New results", labTest.getResults());
		assertEquals(TestStatus.OK, labTest.getStatus());

		// Ensure patient can't read lab test results
		final String patientId = labTest.getPatientId();
		UserRegistration registeredPatient = this.registeredPatients.stream().filter(it -> {
			return it.getUserId().equals(patientId);
		}).findFirst().get();

		headers.set(Constants.GRANT_HEADER, registeredPatient.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		labTest = response.getBody();
		assertNotNull(labTest);
		assertNull(labTest.getResults());
		assertEquals(TestStatus.PERMISSION_DENIED, labTest.getStatus());

		// Share lab test results with patient
		headers.set(Constants.GRANT_HEADER, this.registeredPhysician.getGrant());
		SharingData sharingData = new SharingData(labTest.getId(), registeredPatient.getUserId());
		entity = new HttpEntity<>(sharingData, headers);
		ResponseEntity<?> sharingResponse = this.restTemplate.exchange("/share", HttpMethod.POST, entity, Void.class);
		assertEquals(200, sharingResponse.getStatusCodeValue());

		// Get lab test results by patient
		headers.set(Constants.GRANT_HEADER, registeredPatient.getGrant());
		entity = new HttpEntity<>(headers);
		response = this.restTemplate.exchange("/lab-tests/" + labTestId, HttpMethod.GET, entity, LabTest.class);
		assertEquals(200, response.getStatusCodeValue());

		labTest = response.getBody();
		assertNotNull(labTest);
		assertEquals("New results", labTest.getResults());
		assertEquals(TestStatus.OK, labTest.getStatus());
	}

}
