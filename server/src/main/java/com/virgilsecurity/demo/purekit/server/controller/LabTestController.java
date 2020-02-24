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
import com.virgilsecurity.demo.purekit.server.model.http.LabTest;
import com.virgilsecurity.demo.purekit.server.service.LabTestService;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
import com.virgilsecurity.purekit.pure.model.PureGrant;

/**
 * Laboratory test controller.
 */
@RequestMapping("/lab-tests")
@RestController
public class LabTestController {

	@Autowired
	private LabTestService labTestService;

	/**
	 * List all laboratory tests.
	 * 
	 * @param grant the pure grant.
	 * @return the list of laboratory tests.
	 */
	@GetMapping
	public List<LabTest> list(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant) {
		return this.labTestService.findAll(grant);
	}

	/**
	 * Create new laboratory test.
	 * 
	 * @param grant
	 * @param labTest the laboratory test data.
	 * @return the identifier of created laboratory test.
	 */
	@PostMapping
	public String create(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant, @RequestBody LabTest labTest) {
		return this.labTestService.create(labTest, grant);
	}

	/**
	 * Retrieve laboratory test by identifier.
	 * 
	 * @param grant     the pure grant.
	 * @param labTestId the laboratory test identifier.
	 * @return the laboratory test data.
	 */
	@GetMapping("/{id}")
	public LabTest get(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant, @PathVariable("id") String labTestId) {
		return this.labTestService.get(labTestId, grant);
	}

	/**
	 * Update laboratory test.
	 * 
	 * @param grant     the pure grant.
	 * @param labTestId the laboratory test identifier.
	 * @param labTest   the laboratory test data.
	 */
	@PutMapping("/{id}")
	public void update(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant, @PathVariable("id") String labTestId,
			@RequestBody LabTest labTest) {
		if (StringUtils.isNotBlank(labTest.getId()) && !StringUtils.equals(labTestId, labTest.getId())) {
			throw new BadRequestException("LabTest id is not valid");
		}
		this.labTestService.update(labTest, grant);
	}

}
