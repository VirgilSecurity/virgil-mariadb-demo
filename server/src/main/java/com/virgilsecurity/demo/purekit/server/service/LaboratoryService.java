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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.exception.EncryptionException;
import com.virgilsecurity.demo.purekit.server.exception.NotFoundException;
import com.virgilsecurity.demo.purekit.server.mapper.LaboratoryMapper;
import com.virgilsecurity.demo.purekit.server.model.db.LaboratoryEntity;
import com.virgilsecurity.demo.purekit.server.model.http.Laboratory;
import com.virgilsecurity.demo.purekit.server.model.http.UserRegistration;
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

	public UserRegistration register(Laboratory laboratory, String password) {
		String userId = Utils.generateId();
		try {
			// Register Pure user
			this.pure.registerUser(userId, password);
			AuthResult authResult = this.pure.authenticateUser(userId, password);

			// Store laboratory in a database
			LaboratoryEntity laboratoryEntity = new LaboratoryEntity(userId, laboratory.getName());
			this.mapper.insert(laboratoryEntity);

			return new UserRegistration(userId, authResult.getEncryptedGrant());
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
