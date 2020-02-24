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
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.exception.EncryptionException;
import com.virgilsecurity.demo.purekit.server.exception.NotFoundException;
import com.virgilsecurity.demo.purekit.server.mapper.PatientMapper;
import com.virgilsecurity.demo.purekit.server.model.SharedRole;
import com.virgilsecurity.demo.purekit.server.model.db.PatientEntity;
import com.virgilsecurity.demo.purekit.server.model.http.Patient;
import com.virgilsecurity.demo.purekit.server.model.http.UserRegistration;
import com.virgilsecurity.demo.purekit.server.utils.Utils;
import com.virgilsecurity.purekit.pure.AuthResult;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.model.PureGrant;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PatientService {

	@Autowired
	private PatientMapper mapper;

	@Autowired
	private Pure pure;

	public UserRegistration register(Patient patient, String password) {
		String userId = Utils.generateId();
		try {
			// Register Pure user
			this.pure.registerUser(userId, password);
			AuthResult authResult = this.pure.authenticateUser(userId, password);
			PureGrant pureGrant = authResult.getGrant();

			// Encrypt sensitive data
			byte[] encryptedSsn = pure.encrypt(pureGrant.getUserId(), SharedRole.SSN.getCode(),
					patient.getSsn().getBytes(StandardCharsets.UTF_8));

			// Store patient in a database
			PatientEntity patientEntity = new PatientEntity(userId, patient.getName(), encryptedSsn);
			this.mapper.insert(patientEntity);

			return new UserRegistration(userId, authResult.getEncryptedGrant());
		} catch (PureException e) {
			log.debug("PatientEntity SSN can't be encrypted", e);
			throw new EncryptionException();
		}
	}

	public Patient get(String patientId, PureGrant grant) {
		PatientEntity patient = this.mapper.findById(patientId);
		if (patient == null) {
			throw new NotFoundException();
		}

		return decryptPatient(patient, grant);
	}

	public List<Patient> findAll(PureGrant grant) {
		List<PatientEntity> patients = this.mapper.findAll();
		List<Patient> result = new LinkedList<Patient>();
		patients.forEach(patientEntity -> {
			result.add(decryptPatient(patientEntity, grant));
		});
		return result;
	}

	/**
	 * Get all patients assigned to a physician.
	 * 
	 * @param physicianId physician identifier.
	 * @param grant       physician's grant.
	 * @return list of patients.
	 */
	public List<Patient> findByPhysician(String physicianId, PureGrant grant) {
		List<PatientEntity> patients = this.mapper.findByPhysician(physicianId);
		List<Patient> result = new LinkedList<Patient>();
		patients.forEach(patientEntity -> {
			result.add(decryptPatient(patientEntity, grant));
		});
		return result;
	}

	public void reset() {
		this.mapper.deleteAll();
	}

	private Patient decryptPatient(PatientEntity patientEntity, PureGrant grant) {
		String ssn = null;
		if (patientEntity.getSsn() != null) {
			try {
				byte[] decryptedSsn = this.pure.decrypt(grant, patientEntity.getId(), SharedRole.SSN.getCode(),
						patientEntity.getSsn());
				ssn = new String(decryptedSsn);
			} catch (PureException e) {
				log.debug("Patient's '{}' SSN can't be decrypted for '{}'", patientEntity.getId(), grant.getUserId(),
						e);
			}
		}
		return new Patient(patientEntity.getId(), patientEntity.getName(), ssn);
	}

}
