package com.virgilsecurity.demo.purekit.server.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.exception.NotFoundException;
import com.virgilsecurity.demo.purekit.server.exception.PermissionDeniedException;
import com.virgilsecurity.demo.purekit.server.mapper.PhysicianMapper;
import com.virgilsecurity.demo.purekit.server.mapper.PrescriptionMapper;
import com.virgilsecurity.demo.purekit.server.model.db.PhysicianEntity;
import com.virgilsecurity.demo.purekit.server.model.db.PrescriptionEntity;
import com.virgilsecurity.demo.purekit.server.model.http.Prescription;
import com.virgilsecurity.demo.purekit.server.utils.Utils;
import com.virgilsecurity.purekit.pure.model.PureGrant;

@Service
public class PrescriptionService {

	@Autowired
	private PrescriptionMapper prescriptionMapper;

	@Autowired
	private PhysicianMapper physicianMapper;

	public String create(Prescription prescription, PureGrant grant) {
		validatePermissions(grant);

		String id = Utils.generateId();
		PrescriptionEntity entity = new PrescriptionEntity(id, prescription.getPatientId(), grant.getUserId(),
				prescription.getNotes(), prescription.getAssingDate(), prescription.getReleaseDate());
		this.prescriptionMapper.insert(entity);

		return id;
	}

	public List<Prescription> findAll(PureGrant grant) {
		List<PrescriptionEntity> prescriptions = this.prescriptionMapper.findAll(grant.getUserId());
		List<Prescription> result = new LinkedList<Prescription>();
		prescriptions.forEach(entity -> {
			result.add(convert(entity));
		});
		return result;
	}

	public Prescription get(String prescriptionId, PureGrant grant) {
		PrescriptionEntity entity = this.prescriptionMapper.findById(prescriptionId);
		if (entity == null) {
			throw new NotFoundException();
		}
		validateReadPermissions(entity, grant);

		return convert(entity);
	}

	public void update(Prescription prescription, PureGrant grant) {
		PrescriptionEntity entity = this.prescriptionMapper.findById(prescription.getId());
		if (entity == null) {
			throw new NotFoundException();
		}
		validateWritePermissions(entity, grant);

		entity.setNotes(prescription.getNotes());
		entity.setAssingDate(prescription.getAssingDate());
		entity.setReleaseDate(prescription.getReleaseDate());

		this.prescriptionMapper.update(entity);
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

	private Prescription convert(PrescriptionEntity entity) {
		return new Prescription(entity.getId(), entity.getPatientId(), entity.getPhysicianId(), entity.getNotes(),
				entity.getAssingDate(), entity.getReleaseDate());
	}

}
