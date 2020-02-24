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

package com.virgilsecurity.demo.purekit.server.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.exception.EncryptionException;
import com.virgilsecurity.demo.purekit.server.exception.NotFoundException;
import com.virgilsecurity.demo.purekit.server.exception.PermissionDeniedException;
import com.virgilsecurity.demo.purekit.server.mapper.LabTestMapper;
import com.virgilsecurity.demo.purekit.server.mapper.PhysicianMapper;
import com.virgilsecurity.demo.purekit.server.model.TestStatus;
import com.virgilsecurity.demo.purekit.server.model.db.LabTestEntity;
import com.virgilsecurity.demo.purekit.server.model.db.PhysicianEntity;
import com.virgilsecurity.demo.purekit.server.model.http.LabTest;
import com.virgilsecurity.demo.purekit.server.utils.Utils;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.model.PureGrant;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class LabTestService {

	@Autowired
	private LabTestMapper mapper;

	@Autowired
	private PhysicianMapper physicianMapper;

	@Autowired
	private Pure pure;

	public String create(LabTest labTest, PureGrant grant) {
		validatePermissions(grant);

		String id = Utils.generateId();
		LabTestEntity entity = new LabTestEntity(id, labTest.getName(), labTest.getPatientId(), grant.getUserId(),
				labTest.getTestDate(), null, new Date());
		this.mapper.insert(entity);

		return id;
	}

	public List<LabTest> findAll(PureGrant grant) {
		List<LabTestEntity> labTests = this.mapper.findAll();
		List<LabTest> result = new LinkedList<LabTest>();
		labTests.forEach(entity -> {
			result.add(decryptEntity(entity, grant));
		});
		return result;
	}

	public LabTest get(String labTestId, PureGrant grant) {
		LabTestEntity entity = this.mapper.findById(labTestId);
		if (entity == null) {
			throw new NotFoundException();
		}
		validateReadPermissions(entity, grant);

		return decryptEntity(entity, grant);
	}

	public void update(LabTest labTest, PureGrant grant) {
		LabTestEntity entity = this.mapper.findById(labTest.getId());
		if (entity == null) {
			throw new NotFoundException();
		}
		validateWritePermissions(entity, grant);

		byte[] encryptedResults;
		try {
			encryptedResults = this.pure.encrypt(labTest.getPhysicianId(), labTest.getId(),
					labTest.getResults().getBytes());
		} catch (PureException e) {
			log.error("Laboratory test results encryption failed", e);
			throw new EncryptionException();
		}
		entity.setResults(encryptedResults);

		this.mapper.update(entity);
	}

	public void reset() {
		this.mapper.deleteAll();
	}

	private void validatePermissions(PureGrant grant) {
		PhysicianEntity physician = this.physicianMapper.findById(grant.getUserId());
		if (physician == null) {
			throw new PermissionDeniedException();
		}
	}

	private void validateReadPermissions(LabTestEntity labTest, PureGrant grant) {
		if (StringUtils.equals(labTest.getPatientId(), grant.getUserId())
				|| StringUtils.equals(labTest.getPhysicianId(), grant.getUserId())) {
			return;
		}
//		throw new PermissionDeniedException();
	}

	private void validateWritePermissions(LabTestEntity labTest, PureGrant grant) {
//		if (!StringUtils.equals(labTest.getPhysicianId(), grant.getUserId())) {
//			throw new PermissionDeniedException();
//		}
	}

	private LabTest decryptEntity(LabTestEntity entity, PureGrant grant) {
		String results = null;
		TestStatus status = TestStatus.NOT_READY;
		if (entity.getResults() != null) {
			try {
				results = new String(
						this.pure.decrypt(grant, entity.getPhysicianId(), entity.getId(), entity.getResults()));
				status = TestStatus.OK;
			} catch (PureException e) {
				status = TestStatus.PERMISSION_DENIED;
			}
		}
		return new LabTest(entity.getId(), entity.getName(), entity.getPatientId(), entity.getPhysicianId(),
				entity.getTestDate(), results, status, entity.getCreatedAt());
	}

}
