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

package com.virgilsecurity.demo.purekit.server.service;

import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.lang3.time.DateUtils;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.model.http.LabTest;
import com.virgilsecurity.demo.purekit.server.model.http.Laboratory;
import com.virgilsecurity.demo.purekit.server.model.http.Patient;
import com.virgilsecurity.demo.purekit.server.model.http.Physician;
import com.virgilsecurity.demo.purekit.server.model.http.Prescription;
import com.virgilsecurity.demo.purekit.server.model.http.ResetData;
import com.virgilsecurity.demo.purekit.server.model.http.UserRegistration;
import com.virgilsecurity.demo.purekit.server.utils.Utils;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.model.PureGrant;
import com.virgilsecurity.purekit.pure.storage.MariaDbPureStorage;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ResetService {

	@Autowired
	private Flyway flyway;

	@Autowired
	private Pure pure;

	@Autowired
	private MariaDbPureStorage pureStorage;

	@Autowired
	private PatientService patientService;

	@Autowired
	private PhysicianService physicianService;

	@Autowired
	private LaboratoryService laboratoryService;

	@Autowired
	private PrescriptionService prescriptionService;

	@Autowired
	private LabTestService labTestService;

	@Value("${virgil.purekit.cleanDb:true}")
	private Boolean cleanDb;

	public ResetData reset() {
		recreateDbStructure();
		return fillTables();
	}

	/**
	 * Remove all data from the database.
	 */
	public void clearTables() {
		this.labTestService.reset();
		this.prescriptionService.reset();
		this.laboratoryService.reset();
		this.patientService.reset();
		this.physicianService.reset();
		if (cleanDb) {
			try {
				this.pureStorage.cleanDb();
			} catch (SQLException e) {
				log.error("Can't reset Pure Storage data", e);
			}
		}
	}

	/**
	 * Recreate database structure.
	 */
	private void recreateDbStructure() {
		this.flyway.clean();
		this.flyway.migrate();
	}

	/**
	 * Fill database with initial data.
	 * 
	 * @return metadata about created entities.
	 */
	private ResetData fillTables() {
		ResetData resetData = new ResetData();
		String password = UUID.randomUUID().toString();

		// Register patients
		Patient patient = new Patient("Alice", "12345678901");
		UserRegistration registeredPatient = this.patientService.register(patient, password);
		resetData.getPatients().add(registeredPatient);
		PureGrant patientPureGrant;
		try {
			patientPureGrant = this.pure.decryptGrantFromUser(registeredPatient.getGrant());
		} catch (PureException e) {
			log.error("Patient grant decryption failed", e);
			throw new RuntimeException();
		}

		// Register physician
		Physician physician = new Physician("Bob", "77774444");
		UserRegistration registeredPhysician = this.physicianService.register(physician, password);
		resetData.getPhysicians().add(registeredPhysician);
		PureGrant physicianPureGrant;
		try {
			physicianPureGrant = this.pure.decryptGrantFromUser(registeredPhysician.getGrant());
		} catch (PureException e) {
			log.error("Physician grant decryption failed", e);
			throw new RuntimeException();
		}

		// Assing patient to physician
		this.physicianService.assignPatient(registeredPatient.getUserId(), registeredPhysician.getUserId());

		// Register laboratory
		Laboratory laboratory = new Laboratory("Lab");
		UserRegistration registeredLaboratory = this.laboratoryService.register(laboratory, password);
		resetData.getLaboratories().add(registeredLaboratory);
		try {
			this.pure.decryptGrantFromUser(registeredLaboratory.getGrant());
		} catch (PureException e) {
			log.error("Laboratory grant decryption failed", e);
			throw new RuntimeException();
		}

		// Create prescriptions
		Prescription prescription = new Prescription(patientPureGrant.getUserId(), "Pills X 1 tablet per day",
				Utils.today(), DateUtils.addDays(Utils.today(), 1));
		resetData.getPrescriptions().add(this.prescriptionService.create(prescription, physicianPureGrant));

		prescription = new Prescription(patientPureGrant.getUserId(), "Pills X 2 tablet per day", Utils.today(),
				DateUtils.addDays(Utils.today(), 2));
		resetData.getPrescriptions().add(this.prescriptionService.create(prescription, physicianPureGrant));

		// Create laboratory tests
		LabTest labTest = new LabTest("Blood test 1", patientPureGrant.getUserId(), Utils.today());
		resetData.getLabTests().add(this.labTestService.create(labTest, physicianPureGrant));

		labTest = new LabTest("Blood test 2", patientPureGrant.getUserId(), Utils.today());
		resetData.getLabTests().add(this.labTestService.create(labTest, physicianPureGrant));

		return resetData;
	}

}
