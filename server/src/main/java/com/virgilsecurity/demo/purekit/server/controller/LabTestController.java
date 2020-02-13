package com.virgilsecurity.demo.purekit.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virgilsecurity.demo.purekit.server.mapper.LabTestMapper;

@RequestMapping("/lab-tests")
@RestController
public class LabTestController {

	private final LabTestMapper labTestMapper;

	public LabTestController(LabTestMapper labTestMapper) {
		this.labTestMapper = labTestMapper;
	}

}
