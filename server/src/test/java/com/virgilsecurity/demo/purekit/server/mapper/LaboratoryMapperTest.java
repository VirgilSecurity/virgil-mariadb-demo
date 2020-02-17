package com.virgilsecurity.demo.purekit.server.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.virgilsecurity.demo.purekit.server.model.db.LaboratoryEntity;
import com.virgilsecurity.demo.purekit.server.utils.Utils;

@MybatisTest
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
