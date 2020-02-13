package com.virgilsecurity.demo.purekit.server.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.virgilsecurity.demo.purekit.server.model.db.PhysicianEntity;

@Mapper
public interface PhysicianMapper {

	@Select("SELECT id, full_name, license_no FROM physicians")
	List<PhysicianEntity> findAll();

	@Select("SELECT id, full_name, license_no FROM physicians WHERE id = #{id}")
	PhysicianEntity findById(@Param("id") String id);

	@Insert("INSERT INTO physicians (id, full_name, license_no) VALUES (#{id}, #{name}, #{licenseNo})")
	void insert(PhysicianEntity physician);

	@Update("UPDATE physicians SET full_name = #{name}, license_no = #{licenseNo} WHERE id = #{id}")
	void update(PhysicianEntity physician);

	@Delete("DELETE FROM physicians")
	void deleteAll();

}
