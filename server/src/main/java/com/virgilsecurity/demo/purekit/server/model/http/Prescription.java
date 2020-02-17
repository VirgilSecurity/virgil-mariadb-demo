package com.virgilsecurity.demo.purekit.server.model.http;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription implements Serializable {

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

	public Prescription(String patientId, String notes, Date assingDate, Date releaseDate) {
		super();
		this.patientId = patientId;
		this.notes = notes;
		this.assingDate = assingDate;
		this.releaseDate = releaseDate;
	}

}
