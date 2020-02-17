package com.virgilsecurity.demo.purekit.server.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.virgilsecurity.demo.purekit.server.model.db.LabTestEntity;

@Mapper
public interface LabTestMapper {

	@Select("SELECT id, test_name, patient_id, physician_id, test_date, results FROM lab_tests")
	List<LabTestEntity> findAll();

	@Select("SELECT id, test_name, patient_id, physician_id, test_date, results FROM lab_tests WHERE id = #{id}")
	LabTestEntity findById(@Param("id") String id);

	@Insert("INSERT INTO lab_tests (id, test_name, patient_id, physician_id, test_date, results) "
			+ "VALUES (#{id}, #{name}, #{patientId}, #{physicianId}, #{testDate}, #{results})")
	void insert(LabTestEntity labTest);

	@Update("UPDATE lab_tests "
			+ "SET test_name = #{name}, patient_id = #{patientId}, physician_id = #{physicianId}, test_date = #{testDate}, results = #{results} "
			+ "WHERE id = #{id}")
	void update(LabTestEntity labTest);

	@Delete("DELETE FROM lab_tests")
	void deleteAll();

}
