package com.virgilsecurity.demo.purekit.server.model.http;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private Set<String> otherUserIds;

	@JsonProperty("roles")
	private List<String> roles;

	public SharingData(String dataId, String otherUserId) {
		this.dataId = dataId;
		this.otherUserIds = new HashSet<String>(Arrays.asList(otherUserId));
	}

}
