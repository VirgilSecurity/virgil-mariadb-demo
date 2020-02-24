/*
 * Copyright (c) 2015-2020, Virgil Security, Inc.
 *
 * Lead Maintainer: Virgil Security Inc. <support@virgilsecurity.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     (1) Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *     (2) Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *     (3) Neither the name of virgil nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

	@Select("SELECT id, test_name, patient_id, physician_id, test_date, results, created_at "
			+ "FROM lab_tests "
			+ "ORDER BY created_at ASC")
	List<LabTestEntity> findAll();

	@Select("SELECT id, test_name, patient_id, physician_id, test_date, results,created_at "
			+ "FROM lab_tests "
			+ "WHERE id = #{id}")
	LabTestEntity findById(@Param("id") String id);

	@Insert("INSERT INTO lab_tests (id, test_name, patient_id, physician_id, test_date, results, created_at) "
			+ "VALUES (#{id}, #{name}, #{patientId}, #{physicianId}, #{testDate}, #{results}, #{createdAt})")
	void insert(LabTestEntity labTest);

	@Update("UPDATE lab_tests "
			+ "SET test_name = #{name}, patient_id = #{patientId}, physician_id = #{physicianId}, test_date = #{testDate}, results = #{results} "
			+ "WHERE id = #{id}")
	void update(LabTestEntity labTest);

	@Delete("DELETE FROM lab_tests")
	void deleteAll();

}
