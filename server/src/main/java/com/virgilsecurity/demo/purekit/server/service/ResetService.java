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
		PureGrant laboratoryPureGrant;
		try {
			laboratoryPureGrant = this.pure.decryptGrantFromUser(registeredLaboratory.getGrant());
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
