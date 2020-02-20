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
