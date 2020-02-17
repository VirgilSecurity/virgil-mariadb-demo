package com.virgilsecurity.demo.purekit.server.model.http;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class ResetData {

	private Set<UserRegitration> patients;

	private Set<UserRegitration> physicians;

	private Set<UserRegitration> laboratories;

	private Set<String> prescriptions;

	@JsonProperty("lab_tests")
	private Set<String> labTests;

	public ResetData() {
		this.patients = new HashSet<>();
		this.physicians = new HashSet<>();
		this.laboratories = new HashSet<>();
		this.prescriptions = new HashSet<>();
		this.labTests = new HashSet<String>();
	}

}
