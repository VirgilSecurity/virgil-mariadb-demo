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

package com.virgilsecurity.demo.purekit.server.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import com.virgilsecurity.demo.purekit.server.model.db.LaboratoryEntity;
import com.virgilsecurity.demo.purekit.server.utils.Utils;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LaboratoryMapperTest {

	public static final String LABORATORY1_ID = "0e1ddb5ff64941e382b36018f1ee8664";

	@Autowired
	private LaboratoryMapper mapper;

	@Test
	void findAllTest() {
		List<LaboratoryEntity> laboratories = this.mapper.findAll();
		assertNotNull(laboratories);
		assertEquals(1, laboratories.size());
	}

	@Test
	void insertTest() {
		String id = Utils.generateId();
		LaboratoryEntity laboratory = new LaboratoryEntity(id, "laboratory2");
		this.mapper.insert(laboratory);

		List<LaboratoryEntity> laboratories = this.mapper.findAll();
		assertNotNull(laboratories);
		assertEquals(2, laboratories.size());
	}

	@Test
	void findById() {
		LaboratoryEntity laboratory = this.mapper.findById(LABORATORY1_ID);
		assertNotNull(laboratory);
		assertEquals(LABORATORY1_ID, laboratory.getId());
		assertEquals("laboratory1", laboratory.getName());
	}

	@Test
	void findById_notExists() {
		LaboratoryEntity laboratory = this.mapper.findById("0");
		assertNull(laboratory);
	}

	@Test
	void deleteAll() {
		this.mapper.deleteAll();
		List<LaboratoryEntity> laboratories = this.mapper.findAll();
		assertTrue(laboratories.isEmpty());
	}

}
