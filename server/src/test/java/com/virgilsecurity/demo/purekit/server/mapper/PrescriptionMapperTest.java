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

import com.virgilsecurity.demo.purekit.server.model.db.PrescriptionEntity;
import com.virgilsecurity.demo.purekit.server.utils.Utils;

@MybatisTest
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
				PhysicianMapperTest.PHYSICIAN1_ID, "notes as a text".getBytes(), date, date);
		this.prescriptionMapper.insert(prescription);

		return prescription;
	}

}
