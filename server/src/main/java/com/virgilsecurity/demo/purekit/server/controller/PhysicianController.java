package com.virgilsecurity.demo.purekit.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virgilsecurity.demo.purekit.server.mapper.PhysicianMapper;

@RequestMapping("/physicians")
@RestController
public class PhysicianController {

	private final PhysicianMapper physicianMapper;

	public PhysicianController(PhysicianMapper physicianMapper) {
		this.physicianMapper = physicianMapper;
	}

}
