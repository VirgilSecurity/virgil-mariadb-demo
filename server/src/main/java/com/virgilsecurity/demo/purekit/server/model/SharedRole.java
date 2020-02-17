package com.virgilsecurity.demo.purekit.server.model;

public enum SharedRole {

	SSN("ssn"), LICENSE_NO("license_no"), TEST_RESULTS("test_result"), PRESCRIPTION("prescription");

	private String code;

	SharedRole(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
