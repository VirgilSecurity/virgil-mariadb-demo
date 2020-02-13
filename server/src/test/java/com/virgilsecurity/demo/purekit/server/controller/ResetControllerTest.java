package com.virgilsecurity.demo.purekit.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.virgilsecurity.demo.purekit.server.model.http.ResetData;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResetControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void reset() {
		ResetData body = this.restTemplate.getForObject("/reset", ResetData.class);
		assertNotNull(body);
		assertEquals(2, body.getPatients().size());
		assertEquals(1, body.getPhysicians().size());
		assertEquals(0, body.getLabs().size());
	}

}
