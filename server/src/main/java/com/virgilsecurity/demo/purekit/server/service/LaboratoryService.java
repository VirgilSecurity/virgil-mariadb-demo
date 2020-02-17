package com.virgilsecurity.demo.purekit.server.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.exception.EncryptionException;
import com.virgilsecurity.demo.purekit.server.exception.NotFoundException;
import com.virgilsecurity.demo.purekit.server.mapper.LaboratoryMapper;
import com.virgilsecurity.demo.purekit.server.model.db.LaboratoryEntity;
import com.virgilsecurity.demo.purekit.server.model.http.Laboratory;
import com.virgilsecurity.demo.purekit.server.model.http.UserRegitration;
import com.virgilsecurity.demo.purekit.server.utils.Utils;
import com.virgilsecurity.purekit.pure.AuthResult;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.model.PureGrant;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class LaboratoryService {

	@Autowired
	private LaboratoryMapper mapper;

	@Autowired
	private Pure pure;

	public UserRegitration register(Laboratory laboratory, String password) {
		String userId = Utils.generateId();
		try {
			// Register Pure user
			this.pure.registerUser(userId, password);
			AuthResult authResult = this.pure.authenticateUser(userId, password);

			// Store laboratory in a database
			LaboratoryEntity laboratoryEntity = new LaboratoryEntity(userId, laboratory.getName());
			this.mapper.insert(laboratoryEntity);

			return new UserRegitration(userId, authResult.getEncryptedGrant());
		} catch (PureException e) {
			log.debug("Laboratory user can't be registered", e);
			throw new EncryptionException();
		}
	}

	public Laboratory get(String laboratoryId, PureGrant grant) {
		LaboratoryEntity laboratory = this.mapper.findById(laboratoryId);
		if (laboratory == null) {
			throw new NotFoundException();
		}

		return convert(laboratory);
	}

	public List<Laboratory> findAll(PureGrant grant) {
		List<LaboratoryEntity> laboratorys = this.mapper.findAll();
		List<Laboratory> result = new LinkedList<Laboratory>();
		laboratorys.forEach(laboratoryEntity -> {
			result.add(convert(laboratoryEntity));
		});
		return result;
	}
	
	public void reset() {
		this.mapper.deleteAll();
	}

	private Laboratory convert(LaboratoryEntity entity) {
		return new Laboratory(entity.getId(), entity.getName());
	}

}
