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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import com.virgilsecurity.demo.purekit.server.model.db.PatientEntity;
import com.virgilsecurity.demo.purekit.server.utils.Utils;

import ch.qos.logback.core.encoder.ByteArrayUtil;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PatientMapperTest {

	public static final String PATIENT1_ID = "780b28cb531c4e4fb1513529b09b8a34";

	@Autowired
	private PatientMapper patientMapper;

	@Autowired
	private PhysicianAssignmentsMapper assignmentsMapper;

	@Test
	void findAllTest() {
		List<PatientEntity> patients = this.patientMapper.findAll();
		assertNotNull(patients);
		assertEquals(1, patients.size());
	}

	@Test
	void insertTest() {
		String id = Utils.generateId();
		PatientEntity patient = new PatientEntity(id, "patient2", Utils.generateSsn().getBytes());
		this.patientMapper.insert(patient);

		List<PatientEntity> patients = this.patientMapper.findAll();
		assertNotNull(patients);
		assertEquals(2, patients.size());
	}

	@Test
	void updateTest() {
		PatientEntity patient = this.patientMapper.findById(PATIENT1_ID);
		assertNotNull(patient);

		byte[] ssn = Utils.generateSsn().getBytes();
		patient.setName("new name");
		patient.setSsn(ssn);
		this.patientMapper.update(patient);

		patient = this.patientMapper.findById(PATIENT1_ID);
		assertNotNull(patient);
		assertEquals(PATIENT1_ID, patient.getId());
		assertEquals("new name", patient.getName());
		assertArrayEquals(ssn, patient.getSsn());
	}

	@Test
	void findById() {
		PatientEntity patient = this.patientMapper.findById(PATIENT1_ID);
		assertNotNull(patient);
		assertEquals(PATIENT1_ID, patient.getId());
		assertEquals("patient1", patient.getName());
		assertArrayEquals(ByteArrayUtil.hexStringToByteArray("123456789012"), patient.getSsn());
	}

	@Test
	void findById_notExists() {
		PatientEntity patient = this.patientMapper.findById("0");
		assertNull(patient);
	}

	@Test
	void findByPhysician() {
		List<PatientEntity> patients = this.patientMapper.findByPhysician(PhysicianMapperTest.PHYSICIAN1_ID);
		assertNotNull(patients);
		assertTrue(patients.isEmpty());

		this.assignmentsMapper.assignPhysician(PATIENT1_ID, PhysicianMapperTest.PHYSICIAN1_ID);

		patients = this.patientMapper.findByPhysician(PhysicianMapperTest.PHYSICIAN1_ID);
		assertEquals(1, patients.size());

		PatientEntity patient = patients.get(0);
		assertNotNull(patient);
		assertEquals(PATIENT1_ID, patient.getId());
		assertEquals("patient1", patient.getName());
		assertArrayEquals(ByteArrayUtil.hexStringToByteArray("123456789012"), patient.getSsn());
	}

	@Test
	void findByPhysician_notExists() {
		List<PatientEntity> patients = this.patientMapper.findByPhysician("0");
		assertTrue(patients.isEmpty());
	}

	@Test
	void deleteAll() {
		this.patientMapper.deleteAll();
		List<PatientEntity> patients = this.patientMapper.findAll();
		assertTrue(patients.isEmpty());
	}

}
