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

import java.nio.ByteBuffer;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import com.virgilsecurity.demo.purekit.server.model.db.PhysicianEntity;
import com.virgilsecurity.demo.purekit.server.utils.Utils;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PhysicianMapperTest {

	public static final String PHYSICIAN1_ID = "0e1ddb5ff64941e382b36018f1ee8663";

	@Autowired
	private PhysicianMapper physicianMapper;

	@Autowired
	private PhysicianAssignmentsMapper assignmentsMapper;

	@Test
	void findAllTest() {
		List<PhysicianEntity> physicians = this.physicianMapper.findAll();
		assertNotNull(physicians);
		assertEquals(1, physicians.size());
	}

	@Test
	void insertTest() {
		String id = Utils.generateId();
		byte[] licenseNo = ByteBuffer.allocate(8).putLong(2).array();
		PhysicianEntity physician = new PhysicianEntity(id, "physician2", licenseNo);
		this.physicianMapper.insert(physician);

		List<PhysicianEntity> physicians = this.physicianMapper.findAll();
		assertNotNull(physicians);
		assertEquals(2, physicians.size());
	}

	@Test
	void updateTest() {
		byte[] licenseNo = ByteBuffer.allocate(8).putLong(12345).array();

		PhysicianEntity physician = this.physicianMapper.findById(PHYSICIAN1_ID);
		assertNotNull(physician);

		physician.setName("new name");
		physician.setLicenseNo(licenseNo);
		this.physicianMapper.update(physician);

		physician = this.physicianMapper.findById(PHYSICIAN1_ID);
		assertNotNull(physician);
		assertEquals(PHYSICIAN1_ID, physician.getId());
		assertEquals("new name", physician.getName());
		assertArrayEquals(licenseNo, physician.getLicenseNo());
	}

	@Test
	void findById() {
		PhysicianEntity physician = this.physicianMapper.findById(PHYSICIAN1_ID);
		assertNotNull(physician);
		assertEquals(PHYSICIAN1_ID, physician.getId());
		assertEquals("physician1", physician.getName());
		assertArrayEquals(new byte[] { 1 }, physician.getLicenseNo());
	}

	@Test
	void findByPatient() {
		List<PhysicianEntity> physicians = this.physicianMapper.findByPatient(PatientMapperTest.PATIENT1_ID);
		assertNotNull(physicians);
		assertTrue(physicians.isEmpty());

		this.assignmentsMapper.assignPhysician(PatientMapperTest.PATIENT1_ID, PHYSICIAN1_ID);

		physicians = this.physicianMapper.findByPatient(PatientMapperTest.PATIENT1_ID);
		assertEquals(1, physicians.size());

		PhysicianEntity physician = physicians.get(0);
		assertNotNull(physician);
		assertEquals(PHYSICIAN1_ID, physician.getId());
		assertEquals("physician1", physician.getName());
		assertArrayEquals(new byte[] { 1 }, physician.getLicenseNo());
	}

	@Test
	void findByPatient_notExists() {
		List<PhysicianEntity> physicians = this.physicianMapper.findByPatient("0");
		assertTrue(physicians.isEmpty());
	}

	@Test
	void findById_notExists() {
		PhysicianEntity physician = this.physicianMapper.findById("0");
		assertNull(physician);
	}

	@Test
	void deleteAll() {
		this.physicianMapper.deleteAll();
		List<PhysicianEntity> physicians = this.physicianMapper.findAll();
		assertTrue(physicians.isEmpty());
	}

}
