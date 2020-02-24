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

	/**
	 * List all patients.
	 * 
	 * @param grant the pure grant.
	 * @return the list of patients that stored in database.
	 */
	@GetMapping
	public List<Patient> list(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant) {
		return this.patientService.findAll(grant);
	}

	/**
	 * Retrieve a patient by identifier.
	 * 
	 * @param grant     the pure grant.
	 * @param patientId the patient identifier.
	 * @return the patient.
	 */
	@GetMapping("/{id}")
	public Patient get(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant, @PathVariable("id") String patientId) {
		return this.patientService.get(patientId, grant);
	}

	/**
	 * List all physicians assigned to a patient.
	 * 
	 * @param grant     the pure grant.
	 * @param patientId the patient identifier.
	 * @return list of physicians.
	 */
	@GetMapping("/{id}/physicians")
	public List<Physician> list(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String patientId) {
		if (!StringUtils.equals(patientId, grant.getUserId())) {
			throw new PermissionDeniedException();
		}
		return this.physicianService.findByPatient(patientId, grant);
	}

}
