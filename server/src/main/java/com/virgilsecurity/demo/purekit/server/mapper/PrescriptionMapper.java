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
