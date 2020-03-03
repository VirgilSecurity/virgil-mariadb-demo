import React, { useEffect, useState } from "react";
import {
  PrescriptionsListReq,
  LabTestListReq,
  ShareReq
} from "../../lib/Connection/Endpoints";
import { PatientInfoReq } from "./PatientEndpoint";
import {
  IPatient,
  IPhysician,
  IPrescription,
  ILabTest,
  ICredentials
} from "../../lib/Interfaces";
import Prescriptions from "../../lib/components/Prescription/Prescription";
import LabTest from "./LabTest";
import { PhysicianListReq } from "../Physician/PhysicianEndpoint";
import { Connection } from "../../lib/Connection/Connection";
import { reloadPage, debounce } from "../../lib/utils";
import {
  Container,
  Name,
  AddInfoContainer,
  AddInfo,
  Share,
  Label,
  SurName,
  SectionTitle,
  HR
} from "../../lib/styles";

export interface PatientProps {
  patientCred: ICredentials;
}

const Patient: React.FC<PatientProps> = ({ patientCred }) => {
  const connection: Connection = new Connection();
  const [patient, setPatient] = useState<IPatient | undefined>();
  const [physician, setPhysician] = useState<IPhysician | undefined>();
  const [prescriptions, setPrescriptions] = useState<
    IPrescription[] | undefined
  >();
  const [labTests, setLabTests] = useState<ILabTest[] | undefined>();

  useEffect(() => {
    // get patient info
    connection.send(
      new PatientInfoReq(patientCred.id, patientCred.grant).onSuccess(resp => {
        setPatient(resp);
      })
    );
    // get physician info
    connection.send(
      new PhysicianListReq(patientCred.grant).onSuccess(resp => {
        setPhysician(resp[0]);
      })
    );
    // get prescriptions
    connection.send(
      new PrescriptionsListReq(patientCred.grant).onSuccess(resp => {
        setPrescriptions(resp);
      })
    );
    // get labsTest
    connection.send(
      new LabTestListReq(patientCred.grant).onSuccess(resp => {
        setLabTests(resp);
      })
    );
  }, [patientCred]);

  const handelShareInfo = debounce(() => {
    if (physician) {
      connection.send(
        new ShareReq(
          {
            data_id: "ssn",
            share_with: [physician.id],
            roles: null
          },
          patientCred.grant
        ).onSuccess(() => {
          sessionStorage.setItem("sharePatient", "true");
          reloadPage();
        })
      );
    }
  }, 500);

  const renderInfo = (person: IPatient) => (
    <Container>
      <Name>{person.name}</Name>
      <AddInfoContainer>
        <Label>Social security number:</Label>
        <AddInfo>{person.ssn}</AddInfo>
        {!sessionStorage.getItem("sharePatient") && physician && (
          <Share onClick={handelShareInfo}>
            {`share to ${physician.name}`}
          </Share>
        )}
      </AddInfoContainer>
    </Container>
  );

  const renderPhysician = (person: IPhysician) => (
    <>
      <SectionTitle>Physician:</SectionTitle>
      <Container>
        <SurName>{person.name}</SurName>
        {person.license_no ? (
          <AddInfoContainer>
            <Label>license number:</Label>
            <AddInfo>{person.license_no}</AddInfo>
          </AddInfoContainer>
        ) : null}
      </Container>
    </>
  );

  const renderPrescription = (prescriptions: IPrescription[]) => {
    return <Prescriptions data={prescriptions} />;
  };

  const renderLabTest = (tests: ILabTest[]) => {
    return <LabTest data={tests} />;
  };

  return (
    <>
      {patient && renderInfo(patient)}
      {physician && renderPhysician(physician)}
      <HR />
      {prescriptions && renderPrescription(prescriptions)}
      {labTests && renderLabTest(labTests)}
    </>
  );
};

export default Patient;
