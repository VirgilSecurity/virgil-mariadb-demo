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
