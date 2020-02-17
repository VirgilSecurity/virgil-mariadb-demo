package com.virgilsecurity.demo.purekit.server.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virgilsecurity.demo.purekit.server.exception.BadRequestException;
import com.virgilsecurity.demo.purekit.server.model.http.Prescription;
import com.virgilsecurity.demo.purekit.server.service.PrescriptionService;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
import com.virgilsecurity.purekit.pure.model.PureGrant;

@RequestMapping("/prescriptions")
@RestController
public class PrescriptionController {

	@Autowired
	private PrescriptionService prescriptionService;

	@GetMapping
	public List<Prescription> list(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant) {
		return this.prescriptionService.findAll(grant);
	}

	@PostMapping
	public String create(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@RequestBody Prescription prescription) {
		return this.prescriptionService.create(prescription, grant);
	}

	@GetMapping("/{id}")
	public Prescription get(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String prescriptionId) {
		return this.prescriptionService.get(prescriptionId, grant);
	}

	@PutMapping("/{id}")
	public void update(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String prescriptionId, @RequestBody Prescription prescription) {
		if (StringUtils.isNotBlank(prescription.getId()) && !StringUtils.equals(prescriptionId, prescription.getId())) {
			throw new BadRequestException("Prescription id is not valid");
		}
		this.prescriptionService.update(prescription, grant);
	}

}
