package com.virgilsecurity.demo.purekit.server.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

@RequestMapping("/physicians")
@RestController
public class PhysicianController {

	@Autowired
	private PhysicianService physicianService;

	@Autowired
	private PatientService patientService;

	@GetMapping
	public List<Physician> list(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant) {
		return this.physicianService.findAll(grant);
	}

	@GetMapping("/{id}")
	public Physician get(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String physicianId) {
		return this.physicianService.get(physicianId, grant);
	}

	@GetMapping("/{id}/patients")
	public List<Patient> patients(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String physicianId) {
		if (!StringUtils.equals(physicianId, grant.getUserId())) {
			throw new PermissionDeniedException();
		}
		return this.patientService.findByPhysician(physicianId, grant);
	}

	@PostMapping("/{id}/patients/{patientId}")
	public void assignPatient(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String physicianId, @PathVariable("patientId") String patientId) {
		if (!StringUtils.equals(physicianId, grant.getUserId())) {
			throw new PermissionDeniedException();
		}
		this.physicianService.assignPatient(patientId, physicianId);
	}

}
