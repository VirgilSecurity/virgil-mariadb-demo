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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.exception.EncryptionException;
import com.virgilsecurity.demo.purekit.server.exception.NotFoundException;
import com.virgilsecurity.demo.purekit.server.exception.PermissionDeniedException;
import com.virgilsecurity.demo.purekit.server.mapper.PhysicianMapper;
import com.virgilsecurity.demo.purekit.server.mapper.PrescriptionMapper;
import com.virgilsecurity.demo.purekit.server.model.SharedRole;
import com.virgilsecurity.demo.purekit.server.model.db.PhysicianEntity;
import com.virgilsecurity.demo.purekit.server.model.db.PrescriptionEntity;
import com.virgilsecurity.demo.purekit.server.model.http.Prescription;
import com.virgilsecurity.demo.purekit.server.utils.Utils;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.model.PureGrant;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PrescriptionService {

	@Autowired
	private PrescriptionMapper mapper;

	@Autowired
	private Pure pure;

	@Autowired
	private PhysicianMapper physicianMapper;

	public String create(Prescription prescription, PureGrant grant) {
		validatePermissions(grant);

		String id = Utils.generateId();
		byte[] encryptedNotes = encryptNotes(prescription, grant);
		PrescriptionEntity entity = new PrescriptionEntity(id, prescription.getPatientId(), grant.getUserId(),
				encryptedNotes, prescription.getAssingDate(), prescription.getReleaseDate(), new Date());
		this.mapper.insert(entity);

		return id;
	}

	public List<Prescription> findAll(PureGrant grant) {
		List<PrescriptionEntity> prescriptions = this.mapper.findAll(grant.getUserId());
		List<Prescription> result = new LinkedList<Prescription>();
		prescriptions.forEach(entity -> {
			result.add(decrypt(entity, grant));
		});
		return result;
	}

	public Prescription get(String prescriptionId, PureGrant grant) {
		PrescriptionEntity entity = this.mapper.findById(prescriptionId);
		if (entity == null) {
			throw new NotFoundException();
		}
		validateReadPermissions(entity, grant);

		return decrypt(entity, grant);
	}

	public void update(Prescription prescription, PureGrant grant) {
		PrescriptionEntity entity = this.mapper.findById(prescription.getId());
		if (entity == null) {
			throw new NotFoundException();
		}
		validateWritePermissions(entity, grant);

		byte[] encryptedNotes = encryptNotes(prescription, grant);
		entity.setNotes(encryptedNotes);
		entity.setAssingDate(prescription.getAssingDate());
		entity.setReleaseDate(prescription.getReleaseDate());

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

	private void validateReadPermissions(PrescriptionEntity prescription, PureGrant grant) {
		if (StringUtils.equals(prescription.getPatientId(), grant.getUserId())
				|| StringUtils.equals(prescription.getPhysicianId(), grant.getUserId())) {
			return;
		}
		throw new PermissionDeniedException();
	}

	private void validateWritePermissions(PrescriptionEntity prescription, PureGrant grant) {
		if (!StringUtils.equals(prescription.getPhysicianId(), grant.getUserId())) {
			throw new PermissionDeniedException();
		}
	}

	private Prescription decrypt(PrescriptionEntity entity, PureGrant grant) {
		String notes = null;
		if (entity.getNotes() != null) {
			try {
				byte[] decryptedNotes = this.pure.decrypt(grant, entity.getPhysicianId(),
						SharedRole.PRESCRIPTION.getCode(), entity.getNotes());
				notes = new String(decryptedNotes);
			} catch (PureException e) {
				log.debug("Prescription notes can't be decrypted", e);
			}
		}
		return new Prescription(entity.getId(), entity.getPatientId(), entity.getPhysicianId(), notes,
				entity.getAssingDate(), entity.getReleaseDate(), entity.getCreatedAt());
	}

	private byte[] encryptNotes(Prescription prescription, PureGrant grant) {
		if (prescription.getNotes() != null) {
			try {
				// Encrypt sensitive data
				return this.pure.encrypt(grant.getUserId(), SharedRole.PRESCRIPTION.getCode(),
						new HashSet<String>(Arrays.asList(prescription.getPatientId())), Collections.emptySet(),
						Collections.emptySet(), prescription.getNotes().getBytes(StandardCharsets.UTF_8));
			} catch (PureException e) {
				log.debug("Prescription '{}' can't be encrypted", prescription.getId(), e);
				throw new EncryptionException();
			}
		}
		return null;
	}

}
