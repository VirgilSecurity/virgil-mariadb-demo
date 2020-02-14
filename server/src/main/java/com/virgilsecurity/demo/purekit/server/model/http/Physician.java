package com.virgilsecurity.demo.purekit.server.model.http;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Physician implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private Long licenseNo;
	
	public Physician(String name, Long licenseNo) {
		this.name = name;
		this.licenseNo = licenseNo;
	}

}
