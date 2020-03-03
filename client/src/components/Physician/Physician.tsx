import React, { useEffect, useState } from "react";
import {
  AddLabTestReq,
  PhysicianInfoReq,
  AddPrescriptionsReq
} from "./PhysicianEndpoint";
import {
  IPhysician,
  IPatient,
  IPrescription,
  ILabTest,
  ICredentials,
  IPrescriptionPost,
  ILabTestPost
} from "../../lib/Interfaces";
import Prescriptions from "../../lib/components/Prescription/Prescription";
import SimpleModal from "../../lib/components/Modal/Modal";
import AddPrescription from "./AddPrescription";
import LabTest from "./LabTest";
import AddTest from "./AddTest";
import { PatientListReq } from "../Patient/PatientEndpoint";
import {
  PrescriptionsListReq,
  LabTestListReq,
  ShareReq
} from "../../lib/Connection/Endpoints";
import { Connection } from "../../lib/Connection/Connection";
import { reloadPage, debounce } from "../../lib/utils";
import {
  Container,
  Name,
  AddInfoContainer,
  Label,
  AddInfo,
  Share,
  SectionTitle,
  SurName
} from "../../lib/styles";
import styled from "styled-components";

const ContainerButton = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 10px;
`;

export interface PhysicianProps {
  physicianCred: ICredentials;
};

const Physician: React.FC<PhysicianProps> = ({ physicianCred }) => {
  const connection: Connection = new Connection();

  const [physician, setPhysician] = useState<IPhysician | undefined>();
  const [patient, setPatient] = useState<IPatient | undefined>();
  const [prescriptions, setPrescriptions] = useState<
    IPrescription[] | undefined
  >();
  const [labTests, setLabTests] = useState<ILabTest[] | undefined>();

  useEffect(() => {
    // get physician info
    connection.send(
      new PhysicianInfoReq(physicianCred.id, physicianCred.grant).onSuccess(
        resp => {
          setPhysician(resp);
        }
      )
    );
    // get patient info
    connection.send(
      new PatientListReq(physicianCred.grant).onSuccess(resp => {
        setPatient(resp[0]);
      })
    );
    // get prescriptions
    connection.send(
      new PrescriptionsListReq(physicianCred.grant).onSuccess(resp => {
        setPrescriptions(resp);
      })
    );
    // get labsTest
    connection.send(
      new LabTestListReq(physicianCred.grant).onSuccess(resp => {
        setLabTests(resp);
      })
    );
  }, [physicianCred]);

  const AddPrescriptions = debounce((data: IPrescriptionPost) => {
    connection.send(
      new AddPrescriptionsReq(data, physicianCred.grant).onSuccess(reloadPage)
    );
  }, 500);

  const AddLabTest = debounce((data: ILabTestPost) => {
    connection.send(
      new AddLabTestReq(data, physicianCred.grant).onSuccess(reloadPage)
    );
  }, 500);

  const handelShareInfo = debounce(() => {
    if (patient) {
      connection.send(
        new ShareReq(
          {
            data_id: "license_no",
            share_with: [patient.id],
            roles: null
          },
          physicianCred.grant
        ).onSuccess(() => {
          sessionStorage.setItem("sharePhysician", "true");
          reloadPage();
        })
      );
    }
  }, 500);

  const renderInfo = (person: IPhysician) => (
    <Container>
      <Name>{person.name}</Name>
      <AddInfoContainer>
        <Label>license number:</Label>
        <AddInfo>{person.license_no}</AddInfo>
        {!sessionStorage.getItem("sharePhysician") && patient && (
          <Share onClick={handelShareInfo}>{`share to ${patient.name}`}</Share>
        )}
      </AddInfoContainer>
    </Container>
  );

  const renderPatient = (person: IPatient) => (
    <>
      <SectionTitle>Patient:</SectionTitle>
      <Container>
        <SurName>{person.name}</SurName>
        {person.ssn ? (
          <AddInfoContainer>
            <Label>Social security number:</Label>
            <AddInfo>{person.ssn}</AddInfo>
          </AddInfoContainer>
        ) : null}
      </Container>
    </>
  );

  const renderPrescription = (prescriptions: IPrescription[]) => {
    return <Prescriptions data={prescriptions} />;
  };

  const renderLabTest = (tests: ILabTest[]) => {
    return <LabTest grant={physicianCred.grant} data={tests} />;
  };

  return (
    <>
      {physician && renderInfo(physician)}
      {patient && renderPatient(patient)}

      {patient && (
        <ContainerButton>
          <SimpleModal value="Add prescription">
            <AddPrescription
              patient_id={patient.id}
              onSubmit={AddPrescriptions}
            />
          </SimpleModal>
          <SimpleModal value="Add lab test">
            <AddTest patient_id={patient.id} onSubmit={AddLabTest} />
          </SimpleModal>
        </ContainerButton>
      )}
      {prescriptions && renderPrescription(prescriptions)}
      {labTests && renderLabTest(labTests)}
    </>
  );
};

export default Physician;
