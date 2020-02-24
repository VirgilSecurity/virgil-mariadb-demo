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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import com.virgilsecurity.demo.purekit.server.model.db.LabTestEntity;
import com.virgilsecurity.demo.purekit.server.utils.Utils;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LabTestMapperTest {

	@Autowired
	private LabTestMapper labTestMapper;

	@Test
	void findAllTest() {
		List<LabTestEntity> labTests = this.labTestMapper.findAll();
		assertNotNull(labTests);
		assertTrue(labTests.isEmpty());
	}

	@Test
	void insertTest() {
		String id = Utils.generateId();
		insertLabTest(id);

		List<LabTestEntity> labTests = this.labTestMapper.findAll();
		assertNotNull(labTests);
		assertEquals(1, labTests.size());
	}

	@Test
	void updateTest() {
		String id = Utils.generateId();
		LabTestEntity labTest = insertLabTest(id);

		Date date = Utils.yesterday();
		labTest.setName("New name");
		labTest.setTestDate(date);
		labTest.setResults("New results".getBytes());
		this.labTestMapper.update(labTest);

		LabTestEntity labTest2 = this.labTestMapper.findById(id);
		assertNotNull(labTest2);
		assertEquals(labTest.getId(), labTest2.getId());
		assertEquals("New name", labTest2.getName());
		assertEquals(labTest.getPatientId(), labTest2.getPatientId());
		assertEquals(labTest.getPhysicianId(), labTest2.getPhysicianId());
		assertEquals(labTest.getTestDate(), labTest2.getTestDate());
		assertArrayEquals("New results".getBytes(), labTest2.getResults());
	}

	@Test
	void findById() {
		String id = Utils.generateId();
		LabTestEntity labTest = insertLabTest(id);
		assertNotNull(labTest);
		assertEquals(id, labTest.getId());
		assertEquals("Test_" + id, labTest.getName());
		assertEquals(PatientMapperTest.PATIENT1_ID, labTest.getPatientId());
		assertEquals(PhysicianMapperTest.PHYSICIAN1_ID, labTest.getPhysicianId());
		assertArrayEquals(("Results " + id).getBytes(), labTest.getResults());
	}

	@Test
	void findById_notExists() {
		LabTestEntity labTest = this.labTestMapper.findById("0");
		assertNull(labTest);
	}

	@Test
	void deleteAll() {
		this.labTestMapper.deleteAll();
		List<LabTestEntity> labTests = this.labTestMapper.findAll();
		assertTrue(labTests.isEmpty());
	}

	private LabTestEntity insertLabTest(String id) {
		LabTestEntity labTest = new LabTestEntity(id, "Test_" + id, PatientMapperTest.PATIENT1_ID,
				PhysicianMapperTest.PHYSICIAN1_ID, Utils.today(), ("Results " + id).getBytes(), Utils.yesterday());
		this.labTestMapper.insert(labTest);

		return labTest;
	}

}
