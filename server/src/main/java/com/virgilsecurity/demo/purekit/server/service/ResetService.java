package com.virgilsecurity.demo.purekit.server.service;

import java.sql.SQLException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.mapper.LabTestMapper;
import com.virgilsecurity.demo.purekit.server.mapper.PatientMapper;
import com.virgilsecurity.demo.purekit.server.mapper.PhysicianAssignmentsMapper;
import com.virgilsecurity.demo.purekit.server.mapper.PhysicianMapper;
import com.virgilsecurity.demo.purekit.server.mapper.PrescriptionMapper;
import com.virgilsecurity.demo.purekit.server.model.http.Patient;
import com.virgilsecurity.demo.purekit.server.model.http.Physician;
import com.virgilsecurity.demo.purekit.server.model.http.ResetData;
import com.virgilsecurity.demo.purekit.server.utils.Utils;
import com.virgilsecurity.purekit.pure.storage.MariaDbPureStorage;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ResetService {

	@Autowired
	private PatientMapper patientMapper;

	@Autowired
	private PhysicianMapper physicianMapper;

	@Autowired
	private PrescriptionMapper prescriptionMapper;

	@Autowired
	private LabTestMapper labTestMapper;

	@Autowired
	private PhysicianAssignmentsMapper physicianAssignmentsMapper;

	@Autowired
	private MariaDbPureStorage pureStorage;

	@Autowired
	private PatientService patientService;
	
	@Autowired
	private PhysicianService physicianService;

	public ResetData reset() {
		clearTables();
		return fillTables();
	}

	private void clearTables() {
		this.labTestMapper.deleteAll();
		this.prescriptionMapper.deleteAll();
		this.physicianAssignmentsMapper.deleteAll();
		this.patientMapper.deleteAll();
		this.physicianMapper.deleteAll();

//FIXME
//		try {
//			this.pureStorage.cleanDb();
//		} catch (SQLException e) {
//			log.error("Can't reset Pure Storage data", e);
//		}
	}

	private ResetData fillTables() {
		ResetData resetData = new ResetData();
		String password = UUID.randomUUID().toString();

		// Register patients
		Patient patient = new Patient("PatientEntity 1", Utils.generateSsn());
		String patientGrant = this.patientService.registerPatient(patient, password);
		resetData.getPatients().add(patientGrant);
		
		patient = new Patient("PatientEntity 2", Utils.generateSsn());
		patientGrant = this.patientService.registerPatient(patient, password);
		resetData.getPatients().add(patientGrant);

		// Register physician
		Physician physician = new Physician("PhysicianEntity 1", 1L);
		String physicianGrant = this.physicianService.registerPhysician(physician, password);
		resetData.getPhysicians().add(physicianGrant);
		
		// Create laboratory tests
		
				
		return resetData;
	}

}
