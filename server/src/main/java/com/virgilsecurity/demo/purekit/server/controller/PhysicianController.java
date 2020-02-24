/*
 * Copyright (c) 2015-2020, Virgil Security, Inc.
 *
 * Lead Maintainer: Virgil Security Inc. <support@virgilsecurity.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     (1) Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *     (2) Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *     (3) Neither the name of virgil nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

/**
 * Physician controller.
 */
@RequestMapping("/physicians")
@RestController
public class PhysicianController {

	@Autowired
	private PhysicianService physicianService;

	@Autowired
	private PatientService patientService;

	/**
	 * List all physicians.
	 * 
	 * @param grant the pure grant.
	 * @return the {@code List} of {@code Physician}s.
	 */
	@GetMapping
	public List<Physician> list(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant) {
		return this.physicianService.findAll(grant);
	}

	/**
	 * Get physician by identifier.
	 * 
	 * @param grant       the pure grant.
	 * @param physicianId the identifier of a physician.
	 * @return physician data.
	 */
	@GetMapping("/{id}")
	public Physician get(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String physicianId) {
		return this.physicianService.get(physicianId, grant);
	}

	/**
	 * Get all patients assigned to a physician.
	 * 
	 * @param grant       the pure grant.
	 * @param physicianId the identifier of a physician.
	 * @return the list of patients who are assigned to a physician.
	 */
	@GetMapping("/{id}/patients")
	public List<Patient> patients(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String physicianId) {
		if (!StringUtils.equals(physicianId, grant.getUserId())) {
			throw new PermissionDeniedException();
		}
		return this.patientService.findByPhysician(physicianId, grant);
	}

	/**
	 * Assign patient to a physician.
	 * 
	 * @param grant       the pure grant.
	 * @param physicianId the identifier of a physician.
	 * @param patientId   the identifier of a patient.
	 */
	@PostMapping("/{id}/patients/{patientId}")
	public void assignPatient(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String physicianId, @PathVariable("patientId") String patientId) {
		if (!StringUtils.equals(physicianId, grant.getUserId())) {
			throw new PermissionDeniedException();
		}
		this.physicianService.assignPatient(patientId, physicianId);
	}

}
