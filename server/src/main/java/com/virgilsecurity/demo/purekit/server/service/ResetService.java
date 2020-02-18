package com.virgilsecurity.demo.purekit.server.service;

import java.util.UUID;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

	public ResetData reset() {
		clearTables();
		return fillTables();
	}

	private void clearTables() {
		this.labTestService.reset();
		this.prescriptionService.reset();
		this.laboratoryService.reset();
		this.patientService.reset();
		this.physicianService.reset();
		;

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
		UserRegistration registeredPatient = this.patientService.register(patient, password);
		resetData.getPatients().add(registeredPatient);
		PureGrant patient1PureGrant;
		try {
			patient1PureGrant = this.pure.decryptGrantFromUser(registeredPatient.getGrant());
		} catch (PureException e) {
			log.error("Patient1 grant decryption failed", e);
			throw new RuntimeException();
		}

		patient = new Patient("PatientEntity 2", "12345678902");
		registeredPatient = this.patientService.register(patient, password);
		resetData.getPatients().add(registeredPatient);
		PureGrant patient2PureGrant;
		try {
			patient2PureGrant = this.pure.decryptGrantFromUser(registeredPatient.getGrant());
		} catch (PureException e) {
			log.error("Patient2 grant decryption failed", e);
			throw new RuntimeException();
		}

		// Register physician
		Physician physician = new Physician("PhysicianEntity 1", "1001");
		UserRegistration registeredPhysician = this.physicianService.register(physician, password);
		resetData.getPhysicians().add(registeredPhysician);
		PureGrant physicianPureGrant;
		try {
			physicianPureGrant = this.pure.decryptGrantFromUser(registeredPhysician.getGrant());
		} catch (PureException e) {
			log.error("Physician grant decryption failed", e);
			throw new RuntimeException();
		}

		// Register laboratory
		Laboratory laboratory = new Laboratory("Laboratory 1");
		UserRegistration registeredLaboratory = this.laboratoryService.register(laboratory, password);
		resetData.getLaboratories().add(registeredLaboratory);
		PureGrant laboratoryPureGrant;
		try {
			laboratoryPureGrant = this.pure.decryptGrantFromUser(registeredLaboratory.getGrant());
		} catch (PureException e) {
			log.error("Laboratory grant decryption failed", e);
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
