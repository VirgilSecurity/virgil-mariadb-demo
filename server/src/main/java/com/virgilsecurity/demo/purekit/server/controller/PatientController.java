package com.virgilsecurity.demo.purekit.server.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virgilsecurity.demo.purekit.server.exception.PermissionDeniedException;
import com.virgilsecurity.demo.purekit.server.model.http.Patient;
import com.virgilsecurity.demo.purekit.server.model.http.Physician;
import com.virgilsecurity.demo.purekit.server.service.PatientService;
import com.virgilsecurity.demo.purekit.server.service.PhysicianService;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
import com.virgilsecurity.purekit.pure.model.PureGrant;

@RequestMapping("/patients")
@RestController
public class PatientController {

	@Autowired
	private PatientService patientService;

	@Autowired
	private PhysicianService physicianService;

	@GetMapping
	public List<Patient> list(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant) {
		return this.patientService.findAll(grant);
	}

	@GetMapping("/{id}")
	public Patient get(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant, @PathVariable("id") String patientId) {
		return this.patientService.get(patientId, grant);
	}

	@GetMapping("/{id}/physicians")
	public List<Physician> list(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String patientId) {
		if (!StringUtils.equals(patientId, grant.getUserId())) {
			throw new PermissionDeniedException();
		}
		return this.physicianService.findByPatient(patientId, grant);
	}

}
