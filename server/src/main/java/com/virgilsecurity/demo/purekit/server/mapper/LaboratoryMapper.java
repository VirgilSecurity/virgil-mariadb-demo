package com.virgilsecurity.demo.purekit.server.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.virgilsecurity.demo.purekit.server.model.db.LaboratoryEntity;

@Mapper
public interface LaboratoryMapper {

	@Select("SELECT id, full_name FROM laboratories")
	List<LaboratoryEntity> findAll();

	@Select("SELECT id, full_name FROM laboratories WHERE id = #{id}")
	LaboratoryEntity findById(@Param("id") String id);

	@Insert("INSERT INTO laboratories (id, full_name) VALUES (#{id}, #{name})")
	void insert(LaboratoryEntity entity);

	@Delete("DELETE FROM laboratories")
	void deleteAll();

}
