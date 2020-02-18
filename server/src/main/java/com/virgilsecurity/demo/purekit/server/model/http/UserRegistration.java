package com.virgilsecurity.demo.purekit.server.model.http;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegistration {

	@JsonProperty("id")
	private String userId;

	private String grant;

}
