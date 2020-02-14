package com.virgilsecurity.demo.purekit.server.service;

import java.util.UUID;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.mapper.LabTestMapper;
import com.virgilsecurity.demo.purekit.server.mapper.PatientMapper;
import com.virgilsecurity.demo.purekit.server.mapper.PhysicianAssignmentsMapper;
import com.virgilsecurity.demo.purekit.server.mapper.PhysicianMapper;
import com.virgilsecurity.demo.purekit.server.mapper.PrescriptionMapper;
import com.virgilsecurity.demo.purekit.server.model.http.LabTest;
import com.virgilsecurity.demo.purekit.server.model.http.Patient;
import com.virgilsecurity.demo.purekit.server.model.http.Physician;
import com.virgilsecurity.demo.purekit.server.model.http.Prescription;
import com.virgilsecurity.demo.purekit.server.model.http.ResetData;
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
	private Pure pure;

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

	@Autowired
	private PrescriptionService prescriptionService;

	@Autowired
	private LabTestService labTestService;

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
		Patient patient = new Patient("PatientEntity 1", "12345678901");
		String patientGrant = this.patientService.register(patient, password);
		resetData.getPatients().add(patientGrant);
		PureGrant patient1PureGrant;
		try {
			patient1PureGrant = this.pure.decryptGrantFromUser(patientGrant);
		} catch (PureException e) {
			log.error("Patient1 grant decryption failed", e);
			throw new RuntimeException();
		}

		patient = new Patient("PatientEntity 2", "12345678902");
		patientGrant = this.patientService.register(patient, password);
		resetData.getPatients().add(patientGrant);
		PureGrant patient2PureGrant;
		try {
			patient2PureGrant = this.pure.decryptGrantFromUser(patientGrant);
		} catch (PureException e) {
			log.error("Patient2 grant decryption failed", e);
			throw new RuntimeException();
		}

		// Register physician
		Physician physician = new Physician("PhysicianEntity 1", 1001L);
		String physicianGrant = this.physicianService.register(physician, password);
		resetData.getPhysicians().add(physicianGrant);
		PureGrant physicianPureGrant;
		try {
			physicianPureGrant = this.pure.decryptGrantFromUser(physicianGrant);
		} catch (PureException e) {
			log.error("Physician grant decryption failed", e);
			throw new RuntimeException();
		}

		// Create prescriptions
		Prescription prescription = new Prescription(patient1PureGrant.getUserId(), "Pills 1 tablet per day",
				Utils.today(), DateUtils.addDays(Utils.today(), 1));
		resetData.getPrescriptions().add(this.prescriptionService.create(prescription, physicianPureGrant));

		prescription = new Prescription(patient1PureGrant.getUserId(), "Pills 2 tablet per day", Utils.today(),
				DateUtils.addDays(Utils.today(), 2));
		resetData.getPrescriptions().add(this.prescriptionService.create(prescription, physicianPureGrant));

		prescription = new Prescription(patient2PureGrant.getUserId(), "Pills 8 tablet per day", Utils.today(),
				DateUtils.addDays(Utils.today(), 10));
		resetData.getPrescriptions().add(this.prescriptionService.create(prescription, physicianPureGrant));

		// Create laboratory tests
		LabTest labTest = new LabTest("Bood test 1", patient1PureGrant.getUserId(), Utils.today());
		resetData.getLabTests().add(this.labTestService.create(labTest, physicianPureGrant));

		labTest = new LabTest("Bood test 2", patient1PureGrant.getUserId(), Utils.today());
		resetData.getLabTests().add(this.labTestService.create(labTest, physicianPureGrant));

		labTest = new LabTest("Bood test 3", patient2PureGrant.getUserId(), Utils.today());
		resetData.getLabTests().add(this.labTestService.create(labTest, physicianPureGrant));

		return resetData;
	}

}
