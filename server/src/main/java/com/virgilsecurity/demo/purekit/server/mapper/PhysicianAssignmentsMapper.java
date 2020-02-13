package com.virgilsecurity.demo.purekit.server.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PhysicianAssignmentsMapper {

	@Insert("INSERT INTO physician_assignments (patient_id, physician_id) VALUES (#{patientId}, #{physicianId})")
	void assignPhysician(@Param("patientId") String patientId, @Param("physicianId") String physicianId);

	@Select("SELECT count(*) FROM physician_assignments WHERE patient_id = #{patientId} AND physician_id = #{physicianId}")
	boolean isAssigned(@Param("patientId") String patientId, @Param("physicianId") String physicianId);
	
	@Delete("DELETE FROM physician_assignments")
	void deleteAll();
}
