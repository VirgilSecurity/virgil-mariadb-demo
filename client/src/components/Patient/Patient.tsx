import React, { useEffect, useState } from 'react';
import { useGlobalStyles } from '../../lib/styles';
import { PrescriptionsListReq, LabTestListReq } from '../../lib/Connection/Endpoints';
import { PatientInfoReq, ChangePatientInfo, GetLabTest } from './PatientEndpoint';
import StoreContext from '../StoreContext/StoreContext';
import { IPatient, IPhysician, IPrescription, ILabTest, ICredentials } from '../../lib/Interfaces';
import Prescriptions from '../../lib/components/Prescription/Prescription';
import LabTest from './LabTest';
import { PhysicianListReq } from '../Physician/PhysicianEndpoint';

export interface PatientProps {
    patientCred: ICredentials;
};

const Patient: React.FC<PatientProps> = ({ patientCred }) => {
    const gCss = useGlobalStyles();
    const { connection } = React.useContext(StoreContext);
    const [patient, setPatient] = useState<IPatient | undefined>();
    const [physician, setPhysician] = useState<IPhysician | undefined>();
    const [prescriptions, setPrescriptions] = useState<IPrescription[] | undefined>();
    const [labTests, setLabTests] = useState<ILabTest[] | undefined>();

    useEffect(() => {
        // get patient info
        connection.send(new PatientInfoReq(patientCred.id, patientCred.grant).onSuccess((resp) => {
            setPatient(resp)
        }));
        // get physician info
        connection.send(new PhysicianListReq(patientCred.grant).onSuccess((resp) => {
            setPhysician(resp[0]);
        }));
        // get prescriptions
        connection.send(new PrescriptionsListReq(patientCred.grant).onSuccess((resp) => {
            setPrescriptions(resp);
        }));
        // get labsTest
        connection.send(new LabTestListReq(patientCred.grant).onSuccess((resp) => {
            setLabTests(resp);
        }));
        
    }, [patientCred]);

    // const { patient, setPatient, physician, prescription, connection } = React.useContext(StoreContext);
    // const [labTests, setLabsTests] = useState<undefined | ILabTest[]>();
    
    // useEffect(() => {
    //     connection.send(new GetPatientInfo().onSuccess((resp)=>{
    //         setPatient(resp);
    //     }));
    //     connection.send(new GetLabTest().onSuccess((resp)=>{
    //         setLabsTests(resp);
    //     }));

    // }, []);

    // const handelClick = () => {
    //     if (patient) {
    //         connection.send(new ChangePatientInfo({
    //             ...patient,
    //             share: !patient.share
    //         }).onSuccess(()=>{
    //             // eslint-disable-next-line no-restricted-globals
    //             location.reload();
    //         }));
    //     }
    // };

    const renderInfo = (person: IPatient) => (
        <div className={gCss.container}>
            <div className={gCss.name}>{person.name}</div>
            <div className={gCss.addInfoContainer}>
                <div className={gCss.label}>Social security number:</div>
                <div className={gCss.addInfo}>{person.ssn}</div>
                {/* <div onClick={handelClick} className={gCss.share}>
                    {!person.share && `share to ${physician?.full_name}`}
                </div> */}
            </div>
        </div>
    );

    const renderPhysician = (person: IPhysician) => (
        <>
            <div className={gCss.sectionTitle}>Physician:</div>
            <div className={gCss.container}>
                <div className={gCss.surName}>{person.name}</div>
                {person.license_no ?
                <div className={gCss.addInfoContainer}>
                    <div className={gCss.label}>license number:</div>
                    <div className={gCss.addInfo}>{person.license_no}</div>
                </div> : null }
            </div>
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
            <div style={{marginTop: '61px'}}/>
            {prescriptions && renderPrescription(prescriptions)}
            {labTests && renderLabTest(labTests)}
        </>
    );
};

export default Patient;
