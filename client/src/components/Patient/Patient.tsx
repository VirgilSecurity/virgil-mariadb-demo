import React, { useEffect, useState } from 'react';
import { GetPatientInfo, ChangePatientInfo, GetLabTest } from './PatientEndpoint';
import StoreContext from '../StoreContext/StoreContext';
import { useGlobalStyles } from '../../lib/styles';
import { IPatient, IPhysician, IPrescription, ILabTest } from '../../lib/Interfaces';
import Prescriptions from '../../lib/components/Prescription/Prescription';
import LabTest from './LabTest';

const Patient = () => {
    const gCss = useGlobalStyles();
    const { patient, setPatient, physician, prescription, connection } = React.useContext(StoreContext);
    const [labTests, setLabsTests] = useState<undefined | ILabTest[]>();
    
    useEffect(() => {
        connection.send(new GetPatientInfo().onSuccess((resp)=>{
            setPatient(resp);
        }));
        connection.send(new GetLabTest().onSuccess((resp)=>{
            setLabsTests(resp);
        }));

    }, []);

    const handelClick = () => {
        if (patient) {
            connection.send(new ChangePatientInfo({
                ...patient,
                share: !patient.share
            }).onSuccess(()=>{
                // eslint-disable-next-line no-restricted-globals
                location.reload();
            }));
        }
    };

    const renderInfo = (person: IPatient) => (
        <div className={gCss.container}>
            <div className={gCss.name}>{person.full_name}</div>
            <div className={gCss.addInfoContainer}>
                <div className={gCss.label}>Social security number:</div>
                <div className={gCss.addInfo}>{person.ssn}</div>
                <div onClick={handelClick} className={gCss.share}>
                    {!person.share && `share to ${physician?.full_name}`}
                </div>
            </div>
        </div>
    );

    const renderSecondInfo = (person: IPhysician) => (
        <>
            <div className={gCss.sectionTitle}>Physician:</div>
            <div className={gCss.container}>
                <div className={gCss.surName}>{person.full_name}</div>
                {person.share &&
                <div className={gCss.addInfoContainer}>
                    <div className={gCss.label}>license number:</div>
                    <div className={gCss.addInfo}>{person.license_no}</div>
                </div>}
            </div>
        </>
    );

    const renderPrescription = (prescription: IPrescription[]) => {
        return <Prescriptions data={prescription} />;
    };
    
    const renderLabTest = (tests: ILabTest[]) => {
        return <LabTest data={tests} />;
    };

    return (
        <>
            {patient && renderInfo(patient)}
            {physician && renderSecondInfo(physician)}
            <div style={{marginTop: '61px'}}/>
            {prescription && renderPrescription(prescription)}
            {labTests && renderLabTest(labTests)}
        </>
    );
};

export default Patient;
