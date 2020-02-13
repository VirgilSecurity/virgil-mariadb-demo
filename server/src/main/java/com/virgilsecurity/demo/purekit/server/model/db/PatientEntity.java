package com.virgilsecurity.demo.purekit.server.model.db;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private byte[] ssn;

}
