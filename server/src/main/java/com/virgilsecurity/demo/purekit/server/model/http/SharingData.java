package com.virgilsecurity.demo.purekit.server.model.http;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SharingData {

	@JsonProperty("data_id")
	private String dataId;
	
	@JsonProperty("share_with")
	private List<String> otherUserIds;

	@JsonProperty("roles")
	private List<String> roles;

	public SharingData(String otherUserId, String dataId) {
		this.dataId = dataId;
		this.otherUserIds = Arrays.asList(otherUserId);
	}

}
