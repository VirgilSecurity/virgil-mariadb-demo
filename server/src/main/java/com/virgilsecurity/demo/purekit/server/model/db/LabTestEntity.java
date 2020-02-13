package com.virgilsecurity.demo.purekit.server.model.db;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class LabTestEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String name;

	private String patientId;

	private String physicianId;

	private Date testDate;

	private byte[] results;
}
