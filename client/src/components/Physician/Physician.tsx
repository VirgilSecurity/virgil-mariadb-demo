import React, { useEffect, useState } from 'react';
import { useGlobalStyles } from '../../lib/styles';
import { AddLabTestReq, PhysicianInfoReq, AddPrescriptionsReq } from './PhysicianEndpoint';
import { IPhysician, IPatient, IPrescription, ILabTest, ICredentials, IPrescriptionPost, ILabTestPost } from '../../lib/Interfaces';
import { makeStyles } from '@material-ui/core';
import Prescriptions from '../../lib/components/Prescription/Prescription';
import SimpleModal from '../../lib/components/Modal/Modal';
import AddPrescription from './AddPrescription';
import LabTest from './LabTest';
import AddTest from './AddTest';
import { PatientListReq } from '../Patient/PatientEndpoint';
import { PrescriptionsListReq, LabTestListReq, ShareReq } from '../../lib/Connection/Endpoints';
import { Connection } from '../../lib/Connection/Connection';
import { reloadPage } from '../../lib/utils';

const useStyles = makeStyles(() => ({
    containerBtn: {
        display: 'flex',
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: '10px',
    }
}));

export interface PhysicianProps {
    physicianCred: ICredentials;
}

const Physician:React.FC<PhysicianProps> = ({ physicianCred }) => {
    const gCss = useGlobalStyles();
    const css = useStyles();

    const connection: Connection = new Connection();

    const [physician, setPhysician] = useState<IPhysician | undefined>();
    const [patient, setPatient] = useState<IPatient | undefined>();
    const [prescriptions, setPrescriptions] = useState<IPrescription[] | undefined>();
    const [labTests, setLabTests] = useState<ILabTest[] | undefined>();

    useEffect(() => {
        // get physician info
        connection.send(new PhysicianInfoReq(physicianCred.id, physicianCred.grant).onSuccess((resp)=>{
            setPhysician(resp);
        }));
        // get patient info
        connection.send(new PatientListReq(physicianCred.grant).onSuccess((resp) => {
            setPatient(resp[1]);
        }));
        // get prescriptions
        connection.send(new PrescriptionsListReq(physicianCred.grant).onSuccess((resp) => {
            setPrescriptions(resp);
        }));
        // get labsTest
        connection.send(new LabTestListReq(physicianCred.grant).onSuccess((resp) => {
            setLabTests(resp);
        }));
    }, [physicianCred]);

    const AddPrescriptions = (data: IPrescriptionPost) => {
        connection.send(new AddPrescriptionsReq(data, physicianCred.grant).onSuccess(reloadPage));
    };

    const AddLabTest = (data: ILabTestPost) => {
        connection.send(new AddLabTestReq(data, physicianCred.grant).onSuccess(reloadPage));
    };

    const handelShareInfo = () => {
        if (patient) {
            connection.send(new ShareReq({
                data_id: 'license_no',
                share_with: [patient.id],
                roles: null
            }, physicianCred.grant).onSuccess(() => {
                localStorage.setItem('sharePhysician', 'true');
                reloadPage();
            }));
        }
    };

    const renderInfo = (person: IPhysician) => (
        <div className={gCss.container}>
            <div className={gCss.name}>{person.name}</div>
            <div className={gCss.addInfoContainer}>
                <div className={gCss.label}>license number:</div>
                <div className={gCss.addInfo}>{person.license_no}</div>
                {!localStorage.getItem('sharePhysician') &&  patient &&
                <div onClick={handelShareInfo} className={gCss.share}>
                    {`share to ${patient.name}`}
                </div>}
            </div>
        </div>
    );

    const renderPatient = (person: IPatient) => (
        <>
            <div className={gCss.sectionTitle}>Patient:</div>
            <div className={gCss.container}>
                <div className={gCss.surName}>{person.name}</div>
                {person.ssn ?
                <div className={gCss.addInfoContainer}>
                    <div className={gCss.label}>Social security number:</div>
                    <div className={gCss.addInfo}>{person.ssn}</div>
                </div> : null }
            </div>
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

            {patient && <div className={css.containerBtn}>
                <SimpleModal value="Add prescription">
                    <AddPrescription patient_id={patient.id} onSubmit={AddPrescriptions}/>
                </SimpleModal>
                <SimpleModal value="Add lab test">
                    <AddTest patient_id={patient.id} onSubmit={AddLabTest}/>
                </SimpleModal>
            </div>}
            {prescriptions && renderPrescription(prescriptions)}
            {labTests && renderLabTest(labTests)}
        </>
    );
};

export default Physician;
