package com.virgilsecurity.demo.purekit.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.virgilsecurity.demo.purekit.server.model.http.ResetData;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResetControllerTest extends RestDocTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void reset() throws Exception {
		ResetData body = this.restTemplate.postForObject("/reset", null, ResetData.class);
		assertNotNull(body);
		assertEquals(2, body.getPatients().size());
		assertEquals(1, body.getPhysicians().size());
		assertEquals(3, body.getLabTests().size());

		this.mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/reset"))
				.andDo(document("reset", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

}
