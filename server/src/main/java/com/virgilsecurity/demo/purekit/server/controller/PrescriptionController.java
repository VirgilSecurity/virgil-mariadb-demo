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

	/**
	 * List all prescriptions.
	 * 
	 * @param grant the pure grant.
	 * @return the {@code List} of {@code Prescription}s.
	 */
	@GetMapping
	public List<Prescription> list(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant) {
		return this.prescriptionService.findAll(grant);
	}

	/**
	 * Create new prescription.
	 * 
	 * @param grant        the pure grant.
	 * @param prescription the prescription data.
	 * @return the identifier of created prescription.
	 */
	@PostMapping
	public String create(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@RequestBody Prescription prescription) {
		return this.prescriptionService.create(prescription, grant);
	}

	/**
	 * Get prescription by identifier.
	 * 
	 * @param grant          the pure grant.
	 * @param prescriptionId the identifier of prescription.
	 * @return the prescription data.
	 */
	@GetMapping("/{id}")
	public Prescription get(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String prescriptionId) {
		return this.prescriptionService.get(prescriptionId, grant);
	}

	/**
	 * Update prescription.
	 * 
	 * @param grant          the pure grant.
	 * @param prescriptionId the identifier of prescription.
	 * @param prescription   new prescription data.
	 */
	@PutMapping("/{id}")
	public void update(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant,
			@PathVariable("id") String prescriptionId, @RequestBody Prescription prescription) {
		if (StringUtils.isNotBlank(prescription.getId()) && !StringUtils.equals(prescriptionId, prescription.getId())) {
			throw new BadRequestException("Prescription id is not valid");
		}
		this.prescriptionService.update(prescription, grant);
	}

}
