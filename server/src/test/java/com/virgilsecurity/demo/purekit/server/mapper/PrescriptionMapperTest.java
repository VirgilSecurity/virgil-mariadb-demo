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

import com.virgilsecurity.demo.purekit.server.model.db.PrescriptionEntity;
import com.virgilsecurity.demo.purekit.server.utils.Utils;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PrescriptionMapperTest {

	@Autowired
	private PrescriptionMapper prescriptionMapper;

	@Test
	void findAll_byPhysician() {
		List<PrescriptionEntity> prescriptions = this.prescriptionMapper.findAll(PhysicianMapperTest.PHYSICIAN1_ID);
		assertNotNull(prescriptions);
		assertEquals(2, prescriptions.size());
	}

	@Test
	void findAll_byPatient() {
		List<PrescriptionEntity> prescriptions = this.prescriptionMapper.findAll(PatientMapperTest.PATIENT1_ID);
		assertNotNull(prescriptions);
		assertEquals(2, prescriptions.size());
	}

	@Test
	void findAll_byWrongUserId() {
		List<PrescriptionEntity> prescriptions = this.prescriptionMapper.findAll("780b28cb531c4e4fb1513529b09b8a33");
		assertNotNull(prescriptions);
		assertTrue(prescriptions.isEmpty());
	}

	@Test
	void insertTest() {
		String id = Utils.generateId();
		insertPrescription(id);

		List<PrescriptionEntity> prescriptions = this.prescriptionMapper.findAll("0e1ddb5ff64941e382b36018f1ee8663");
		assertNotNull(prescriptions);
		assertEquals(3, prescriptions.size());
	}

	@Test
	void updateTest() {
		String id = Utils.generateId();
		PrescriptionEntity prescription = insertPrescription(id);

		Date date = Utils.today();
		prescription.setNotes("New notes".getBytes());
		prescription.setReleaseDate(date);
		prescription.setAssingDate(date);
		this.prescriptionMapper.update(prescription);

		PrescriptionEntity prescription2 = this.prescriptionMapper.findById(id);
		assertNotNull(prescription2);
		assertEquals(prescription.getId(), prescription2.getId());
		assertEquals(prescription.getPatientId(), prescription2.getPatientId());
		assertEquals(prescription.getPhysicianId(), prescription2.getPhysicianId());
		assertArrayEquals("New notes".getBytes(), prescription2.getNotes());
		assertEquals(prescription.getReleaseDate(), prescription2.getReleaseDate());
		assertEquals(prescription.getAssingDate(), prescription2.getAssingDate());

	}

	@Test
	void findById() {
		String id = Utils.generateId();
		PrescriptionEntity prescription = insertPrescription(id);
		assertNotNull(prescription);
		assertEquals(id, prescription.getId());
		assertEquals(PatientMapperTest.PATIENT1_ID, prescription.getPatientId());
		assertEquals(PhysicianMapperTest.PHYSICIAN1_ID, prescription.getPhysicianId());
		assertArrayEquals("notes as a text".getBytes(), prescription.getNotes());
		assertNotNull(prescription.getReleaseDate());
		assertNotNull(prescription.getAssingDate());
	}

	@Test
	void findById_notExists() {
		PrescriptionEntity prescription = this.prescriptionMapper.findById("0");
		assertNull(prescription);
	}

	@Test
	void deleteAll() {
		this.prescriptionMapper.deleteAll();
		List<PrescriptionEntity> prescriptions = this.prescriptionMapper.findAll(PatientMapperTest.PATIENT1_ID);
		assertTrue(prescriptions.isEmpty());
	}

	private PrescriptionEntity insertPrescription(String id) {
		Date date = Utils.today();
		PrescriptionEntity prescription = new PrescriptionEntity(id, PatientMapperTest.PATIENT1_ID,
				PhysicianMapperTest.PHYSICIAN1_ID, "notes as a text".getBytes(), date, date, Utils.yesterday());
		this.prescriptionMapper.insert(prescription);

		return prescription;
	}

}
