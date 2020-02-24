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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.exception.EncryptionException;
import com.virgilsecurity.demo.purekit.server.exception.NotFoundException;
import com.virgilsecurity.demo.purekit.server.mapper.PhysicianAssignmentsMapper;
import com.virgilsecurity.demo.purekit.server.mapper.PhysicianMapper;
import com.virgilsecurity.demo.purekit.server.model.SharedRole;
import com.virgilsecurity.demo.purekit.server.model.db.PhysicianEntity;
import com.virgilsecurity.demo.purekit.server.model.http.Physician;
import com.virgilsecurity.demo.purekit.server.model.http.UserRegistration;
import com.virgilsecurity.demo.purekit.server.utils.Utils;
import com.virgilsecurity.purekit.pure.AuthResult;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.model.PureGrant;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PhysicianService {

	@Autowired
	private PhysicianMapper mapper;

	@Autowired
	private PhysicianAssignmentsMapper assignmentsMapper;

	@Autowired
	private Pure pure;

	public UserRegistration register(Physician physician, String password) {
		String userId = Utils.generateId();
		try {
			// Register Pure user
			this.pure.registerUser(userId, password);
			AuthResult authResult = this.pure.authenticateUser(userId, password);
			PureGrant pureGrant = authResult.getGrant();

			// Encrypt sensitive data
			byte[] encryptedLicenseNo = null;
			if (StringUtils.isNotEmpty(physician.getLicenseNo())) {
				encryptedLicenseNo = pure.encrypt(pureGrant.getUserId(), SharedRole.LICENSE_NO.getCode(),
						physician.getLicenseNo().getBytes());
			}

			// Store physician in a database
			PhysicianEntity physicianEntity = new PhysicianEntity(userId, physician.getName(), encryptedLicenseNo);
			this.mapper.insert(physicianEntity);

			return new UserRegistration(userId, authResult.getEncryptedGrant());
		} catch (PureException e) {
			log.debug("PhysicianEntity License No can't be encrypted", e);
			throw new EncryptionException();
		}
	}

	public List<Physician> findAll(PureGrant grant) {
		List<PhysicianEntity> physicians = this.mapper.findAll();
		List<Physician> result = new LinkedList<Physician>();
		physicians.forEach(entity -> {
			result.add(decryptPhysician(entity, grant));
		});
		return result;
	}

	public List<Physician> findByPatient(String patientId, PureGrant grant) {
		List<PhysicianEntity> physicians = this.mapper.findByPatient(patientId);
		List<Physician> result = new LinkedList<Physician>();
		physicians.forEach(entity -> {
			result.add(decryptPhysician(entity, grant));
		});
		return result;
	}

	public Physician get(String physicianId, PureGrant grant) {
		PhysicianEntity physicianEntity = this.mapper.findById(physicianId);
		if (physicianEntity == null) {
			throw new NotFoundException();
		}

		return decryptPhysician(physicianEntity, grant);
	}

	public void assignPatient(String patientId, String physicianId) {
		this.assignmentsMapper.assignPhysician(patientId, physicianId);
	}

	public void reset() {
		this.mapper.deleteAll();
	}

	private Physician decryptPhysician(PhysicianEntity physicianEntity, PureGrant grant) {
		String licenseNo = null;
		try {
			byte[] decryptedLicenseNo = this.pure.decrypt(grant, physicianEntity.getId(),
					SharedRole.LICENSE_NO.getCode(), physicianEntity.getLicenseNo());
			licenseNo = new String(decryptedLicenseNo);
		} catch (PureException e) {
			log.debug("Physician's {} License No can't be decrypted for {}", physicianEntity.getId(), grant.getUserId(),
					e);
		}
		return new Physician(physicianEntity.getId(), physicianEntity.getName(), licenseNo);
	}

}
