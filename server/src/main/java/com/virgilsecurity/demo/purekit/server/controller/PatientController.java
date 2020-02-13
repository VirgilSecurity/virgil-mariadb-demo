package com.virgilsecurity.demo.purekit.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virgilsecurity.demo.purekit.server.mapper.PatientMapper;

@RequestMapping("/patients")
@RestController
public class PatientController {

	private final PatientMapper patientMapper;

	public PatientController(PatientMapper patientMapper) {
		this.patientMapper = patientMapper;
	}

}
