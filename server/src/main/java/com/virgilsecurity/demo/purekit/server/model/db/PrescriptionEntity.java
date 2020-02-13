package com.virgilsecurity.demo.purekit.server.model.db;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class PrescriptionEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	@JsonProperty("patient_id")
	private String patientId;

	@JsonProperty("physician_id")
	private String physicianId;

	private String notes;

	@JsonProperty("assign_date")
	private Date assingDate;

	@JsonProperty("release_date")
	private Date releaseDate;

}
