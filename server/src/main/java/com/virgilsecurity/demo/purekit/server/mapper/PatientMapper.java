package com.virgilsecurity.demo.purekit.server.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.virgilsecurity.demo.purekit.server.model.db.PatientEntity;

@Mapper
public interface PatientMapper {

	@Select("SELECT id, full_name, ssn FROM patients")
	List<PatientEntity> findAll();

	@Select("SELECT p.id, p.full_name, p.ssn "
			+ "FROM patients p JOIN physician_assignments pa ON p.id = pa.patient_id "
			+ "WHERE pa.physician_id = #{physicianId}")
	List<PatientEntity> findByPhysician(@Param("physicianId") String physicianId);

	@Select("SELECT id, full_name, ssn FROM patients WHERE id = #{id}")
	PatientEntity findById(@Param("id") String id);

	@Insert("INSERT INTO patients (id, full_name, ssn) VALUES (#{id}, #{name}, #{ssn})")
	void insert(PatientEntity patient);

	@Update("UPDATE patients SET full_name = #{name}, ssn = #{ssn} WHERE id = #{id}")
	void update(PatientEntity patient);

	@Delete("DELETE FROM patients")
	void deleteAll();

}
