package com.virgilsecurity.demo.purekit.server.model.http;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class LabTest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String name;

	@JsonProperty("patient_id")
	private String patientId;

	@JsonProperty("physician_id")
	private String physicianId;

	@JsonProperty("test_date")
	private Date testDate;

	private String results;

	private String status;

}
