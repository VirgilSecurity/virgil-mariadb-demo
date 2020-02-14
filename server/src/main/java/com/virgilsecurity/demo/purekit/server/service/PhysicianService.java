package com.virgilsecurity.demo.purekit.server.service;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.exception.EncryptionException;
import com.virgilsecurity.demo.purekit.server.exception.NotFoundException;
import com.virgilsecurity.demo.purekit.server.mapper.PhysicianMapper;
import com.virgilsecurity.demo.purekit.server.model.db.PhysicianEntity;
import com.virgilsecurity.demo.purekit.server.model.http.Physician;
import com.virgilsecurity.demo.purekit.server.utils.Utils;
import com.virgilsecurity.purekit.pure.AuthResult;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.model.PureGrant;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PhysicianService {

	private static final String LICENSE_NO_DATA_ID = "licence_no";

	@Autowired
	private PhysicianMapper physicianMapper;

	@Autowired
	private Pure pure;

	public String register(Physician physician, String password) {
		String userId = Utils.generateId();
		try {
			// Register Pure user
			this.pure.registerUser(userId, password);
			AuthResult authResult = this.pure.authenticateUser(userId, password);
			PureGrant pureGrant = authResult.getGrant();

			// Encrypt sensitive data
			byte[] encryptedLicenseNo = pure.encrypt(pureGrant.getUserId(), LICENSE_NO_DATA_ID,
					ByteBuffer.allocate(8).putLong(physician.getLicenseNo()).array());

			// Store physician in a database
			PhysicianEntity physicianEntity = new PhysicianEntity(userId, physician.getName(), encryptedLicenseNo);
			this.physicianMapper.insert(physicianEntity);

			return authResult.getEncryptedGrant();
		} catch (PureException e) {
			log.debug("PhysicianEntity License No can't be encrypted", e);
			throw new EncryptionException();
		}
	}

	public List<Physician> findAll(PureGrant grant) {
		List<PhysicianEntity> physicians = this.physicianMapper.findAll();
		List<Physician> result = new LinkedList<Physician>();
		physicians.forEach(entity -> {
			result.add(decryptPhysician(entity, grant));
		});
		return result;
	}

	public void shareLicense(String patientId, PureGrant pureGrant) {
		try {
			pure.share(pureGrant, LICENSE_NO_DATA_ID, patientId);
		} catch (PureException e) {
			log.debug("PhysicianEntity License No sharing failed", e);
			throw new EncryptionException();
		}
	}

	public Physician get(String physicianId, PureGrant grant) {
		PhysicianEntity physicianEntity = this.physicianMapper.findById(physicianId);
		if (physicianEntity == null) {
			throw new NotFoundException();
		}

		return decryptPhysician(physicianEntity, grant);
	}

	private Physician decryptPhysician(PhysicianEntity physicianEntity, PureGrant grant) {
		Long licenseNo = 0L;
		try {
			byte[] decryptedLicenseNo = this.pure.decrypt(grant, physicianEntity.getId(), LICENSE_NO_DATA_ID,
					physicianEntity.getLicenseNo());
			licenseNo = ByteBuffer.allocate(8).put(decryptedLicenseNo).flip().getLong();
		} catch (PureException e) {
			log.debug("Physician's {} License No can't be decrypted for {}", physicianEntity.getId(), grant.getUserId(),
					e);
		}
		return new Physician(physicianEntity.getId(), physicianEntity.getName(), licenseNo);
	}

}
