package com.virgilsecurity.demo.purekit.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virgilsecurity.demo.purekit.server.model.http.Physician;
import com.virgilsecurity.demo.purekit.server.service.PhysicianService;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
import com.virgilsecurity.purekit.pure.model.PureGrant;

@RequestMapping("/physicians")
@RestController
public class PhysicianController {

	@Autowired
	private PhysicianService physicianService;

	@GetMapping
	public List<Physician> list(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant) {
		return this.physicianService.findAll(grant);
	}

	@GetMapping("/{id}")
	public Physician get(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String physicianId) {
		return this.physicianService.get(physicianId, grant);
	}

	@PutMapping("/share/{patientId}")
	public void share(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant, @PathVariable("patientId") String patient) {
		this.physicianService.shareLicense(patient, grant);
	}

}
