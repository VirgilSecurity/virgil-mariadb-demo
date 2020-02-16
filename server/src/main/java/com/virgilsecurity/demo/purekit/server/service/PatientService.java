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
import com.virgilsecurity.demo.purekit.server.model.http.UserRegitration;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
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

	public UserRegitration register(Patient patient, String password) {
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

			return new UserRegitration(userId, authResult.getEncryptedGrant());
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
				ssn = Constants.Texts.NO_PERMISSIONS;
			}
		}
		return new Patient(patientEntity.getId(), patientEntity.getName(), ssn);
	}

}
