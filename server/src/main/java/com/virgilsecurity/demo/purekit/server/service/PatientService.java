package com.virgilsecurity.demo.purekit.server.service;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.common.util.Base64;
import com.virgilsecurity.demo.purekit.server.exception.EncryptionException;
import com.virgilsecurity.demo.purekit.server.exception.NotFoundException;
import com.virgilsecurity.demo.purekit.server.mapper.PatientMapper;
import com.virgilsecurity.demo.purekit.server.model.db.PatientEntity;
import com.virgilsecurity.demo.purekit.server.model.http.Patient;
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

	private static final String SSN_DATA_ID = "ssn";

	@Autowired
	private PatientMapper patientMapper;

	@Autowired
	private Pure pure;

	public String registerPatient(Patient patient, String password) {
		String userId = Utils.generateId();
		try {
			// Register Pure user
			this.pure.registerUser(userId, password);
			AuthResult authResult = this.pure.authenticateUser(userId, password);
			PureGrant pureGrant = authResult.getGrant();

			// Encrypt sensitive data
			byte[] encryptedSsn = pure.encrypt(pureGrant.getUserId(), SSN_DATA_ID,
					patient.getSsn().getBytes(StandardCharsets.UTF_8));

			// Store patient in a database
			PatientEntity patientEntity = new PatientEntity(userId, patient.getName(), encryptedSsn);
			this.patientMapper.insert(patientEntity);

			return authResult.getEncryptedGrant();
		} catch (PureException e) {
			log.debug("PatientEntity SSN can't be encrypted", e);
			throw new EncryptionException();
		}
	}

	public void shareSsn(String physicianId, PureGrant pureGrant) {
		try {
			pure.share(pureGrant, SSN_DATA_ID, physicianId);
		} catch (PureException e) {
			log.debug("PatientEntity SSN sharing failed", e);
			throw new EncryptionException();
		}
	}

	public Patient readPatient(String patientId, PureGrant grant) {
		PatientEntity patient = this.patientMapper.findById(patientId);
		if (patient == null) {
			throw new NotFoundException();
		}

		return decryptPatient(patient, grant);
	}

	private Patient decryptPatient(PatientEntity patientEntity, PureGrant grant) {
		String ssn = null;
		if (patientEntity.getSsn() != null) {
			try {
				byte[] decryptedSsn = this.pure.decrypt(grant, patientEntity.getId(), SSN_DATA_ID,
						Base64.decode(patientEntity.getSsn()));
				ssn = new String(decryptedSsn);
			} catch (PureException e) {
				ssn = Constants.NO_PERMISSIONS_TEXT;
			}
		}
		return new Patient(patientEntity.getId(), patientEntity.getName(), ssn);
	}

}
