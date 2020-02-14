package com.virgilsecurity.demo.purekit.server.model.http;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public class ResetData {

	private Set<String> patients;

	private Set<String> physicians;
	
	private Set<String> prescriptions;

	private Set<String> labTests;

	public ResetData() {
		this.patients = new HashSet<String>();
		this.physicians = new HashSet<String>();
		this.prescriptions = new HashSet<String>();
		this.labTests = new HashSet<String>();
	}
	
	public static class GrantedUser {
		public String id;
		public String grant;
	}

}
