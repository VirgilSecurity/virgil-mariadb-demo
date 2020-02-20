package com.virgilsecurity.demo.purekit.server.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.virgilsecurity.demo.purekit.server.model.db.PrescriptionEntity;

@Mapper
public interface PrescriptionMapper {

	@Select("SELECT id, patient_id, physician_id, notes, assign_date, release_date, created_at "
			+ "FROM prescriptions "
			+ "WHERE patient_id = #{userId} or physician_id = #{userId}"
			+ "ORDER BY created_at ASC")
	List<PrescriptionEntity> findAll(String userId);

	@Select("SELECT id, patient_id, physician_id, notes, assign_date, release_date, created_at "
			+ "FROM prescriptions "
			+ "WHERE id = #{id} ")
	PrescriptionEntity findById(@Param("id") String id);

	@Insert("INSERT INTO prescriptions (id, patient_id, physician_id, notes, assign_date, release_date, created_at)"
			+ "VALUES (#{id}, #{patientId}, #{physicianId}, #{notes}, #{assingDate}, #{releaseDate}, #{createdAt})")
	void insert(PrescriptionEntity prescription);
	
	@Update("UPDATE prescriptions "
			+ "SET patient_id = #{patientId}, physician_id = #{physicianId}, notes = #{notes}, assign_date = #{assingDate}, release_date = #{releaseDate} "
			+ "WHERE id = #{id}")
	void update(PrescriptionEntity prescription);
	
	@Delete("DELETE FROM prescriptions")
	void deleteAll();
	
}
