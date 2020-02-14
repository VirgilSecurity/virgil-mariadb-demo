package com.virgilsecurity.demo.purekit.server.service;

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
import com.virgilsecurity.demo.purekit.server.model.db.LabTestEntity;
import com.virgilsecurity.demo.purekit.server.model.db.PhysicianEntity;
import com.virgilsecurity.demo.purekit.server.model.http.LabTest;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
import com.virgilsecurity.demo.purekit.server.utils.Utils;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.model.PureGrant;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class LabTestService {

	private static final String RESULTS_DATA_ID = "results";

	@Autowired
	private LabTestMapper labTestMapper;

	@Autowired
	private PhysicianMapper physicianMapper;

	@Autowired
	private Pure pure;

	public String create(LabTest labTest, PureGrant grant) {
		validatePermissions(grant);

		String id = Utils.generateId();
		LabTestEntity entity = new LabTestEntity(id, labTest.getName(), labTest.getPatientId(), grant.getUserId(),
				labTest.getTestDate(), null);
		this.labTestMapper.insert(entity);

		return id;
	}

	public List<LabTest> findAll(PureGrant grant) {
		List<LabTestEntity> labTests = this.labTestMapper.findAll();
		List<LabTest> result = new LinkedList<LabTest>();
		labTests.forEach(entity -> {
			result.add(decryptEntity(entity, grant));
		});
		return result;
	}

	public LabTest get(String labTestId, PureGrant grant) {
		LabTestEntity entity = this.labTestMapper.findById(labTestId);
		if (entity == null) {
			throw new NotFoundException();
		}
		validateReadPermissions(entity, grant);

		return decryptEntity(entity, grant);
	}

	public void update(LabTest labTest, PureGrant grant) {
		LabTestEntity entity = this.labTestMapper.findById(labTest.getId());
		if (entity == null) {
			throw new NotFoundException();
		}
		validateWritePermissions(entity, grant);

		byte[] encryptedResults;
		try {
			encryptedResults = this.pure.encrypt(labTest.getPhysicianId(), RESULTS_DATA_ID,
					labTest.getResults().getBytes());
		} catch (PureException e) {
			log.error("Laboratory test results encryption failed", e);
			throw new EncryptionException();
		}
		entity.setResults(encryptedResults);

		this.labTestMapper.update(entity);
	}

	public void shareResults(String id, String userId, PureGrant grant) {
		try {
			pure.share(grant, RESULTS_DATA_ID, userId);
		} catch (PureException e) {
			log.debug("LabTest results sharing failed", e);
			throw new EncryptionException();
		}
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
		throw new PermissionDeniedException();
	}

	private void validateWritePermissions(LabTestEntity labTest, PureGrant grant) {
		if (!StringUtils.equals(labTest.getPhysicianId(), grant.getUserId())) {
			throw new PermissionDeniedException();
		}
	}

	private LabTest decryptEntity(LabTestEntity entity, PureGrant grant) {
		String results = Constants.Texts.NOT_READY;
		if (entity.getResults() != null) {
			try {
				results = new String(
						this.pure.decrypt(grant, entity.getPhysicianId(), RESULTS_DATA_ID, entity.getResults()));
			} catch (PureException e) {
				results = Constants.Texts.NO_PERMISSIONS;
			}
		}
		return new LabTest(entity.getId(), entity.getName(), entity.getPatientId(), entity.getPhysicianId(),
				entity.getTestDate(), results, "");
	}

}
