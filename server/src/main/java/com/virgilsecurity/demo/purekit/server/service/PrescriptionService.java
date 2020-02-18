package com.virgilsecurity.demo.purekit.server.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
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
				encryptedNotes, prescription.getAssingDate(), prescription.getReleaseDate());
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
				entity.getAssingDate(), entity.getReleaseDate());
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
