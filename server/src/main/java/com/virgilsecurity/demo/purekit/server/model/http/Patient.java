package com.virgilsecurity.demo.purekit.server.model.http;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Patient implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private String ssn;

	public Patient(String name, String ssn) {
		super();
		this.name = name;
		this.ssn = ssn;
	}

}
