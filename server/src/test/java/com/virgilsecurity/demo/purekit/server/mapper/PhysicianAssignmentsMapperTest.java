package com.virgilsecurity.demo.purekit.server.mapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PhysicianAssignmentsMapperTest {

	@Autowired
	private PatientMapper patientMapper;

	@Autowired
	private PhysicianMapper physicianMapper;

	@Autowired
	private PhysicianAssignmentsMapper mapper;

	@Test
	void assignPhysician() {
		assertFalse(this.mapper.isAssigned(PatientMapperTest.PATIENT1_ID, PhysicianMapperTest.PHYSICIAN1_ID));

		this.mapper.assignPhysician(PatientMapperTest.PATIENT1_ID, PhysicianMapperTest.PHYSICIAN1_ID);

		assertTrue(this.mapper.isAssigned(PatientMapperTest.PATIENT1_ID, PhysicianMapperTest.PHYSICIAN1_ID));
	}

	@Test
	void deleteAll() {
		this.mapper.assignPhysician(PatientMapperTest.PATIENT1_ID, PhysicianMapperTest.PHYSICIAN1_ID);
		this.mapper.deleteAll();
		assertFalse(this.mapper.isAssigned(PatientMapperTest.PATIENT1_ID, PhysicianMapperTest.PHYSICIAN1_ID));
	}

	@Test
	void deletePatient() {
		this.mapper.assignPhysician(PatientMapperTest.PATIENT1_ID, PhysicianMapperTest.PHYSICIAN1_ID);
		assertTrue(this.mapper.isAssigned(PatientMapperTest.PATIENT1_ID, PhysicianMapperTest.PHYSICIAN1_ID));

		this.patientMapper.deleteAll();
		assertFalse(this.mapper.isAssigned(PatientMapperTest.PATIENT1_ID, PhysicianMapperTest.PHYSICIAN1_ID));
	}

	@Test
	void deletePhysician() {
		this.mapper.assignPhysician(PatientMapperTest.PATIENT1_ID, PhysicianMapperTest.PHYSICIAN1_ID);
		assertTrue(this.mapper.isAssigned(PatientMapperTest.PATIENT1_ID, PhysicianMapperTest.PHYSICIAN1_ID));

		this.physicianMapper.deleteAll();
		assertFalse(this.mapper.isAssigned(PatientMapperTest.PATIENT1_ID, PhysicianMapperTest.PHYSICIAN1_ID));
	}

}
