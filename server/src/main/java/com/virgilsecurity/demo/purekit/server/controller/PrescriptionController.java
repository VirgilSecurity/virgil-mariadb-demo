package com.virgilsecurity.demo.purekit.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virgilsecurity.demo.purekit.server.mapper.PrescriptionMapper;

@RequestMapping("/prescriptions")
@RestController
public class PrescriptionController {

	private final PrescriptionMapper prescriptionMapper;

	public PrescriptionController(PrescriptionMapper prescriptionMapper) {
		this.prescriptionMapper = prescriptionMapper;
	}

}
