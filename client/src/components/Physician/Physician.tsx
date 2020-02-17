import React, { useEffect, useState } from 'react';
import { useGlobalStyles } from '../../lib/styles';
import { GetPhysicianInfo, ChangePhysicianReq, AddPrescriptionsReq, GetLabTest, AddLabTestReq } from './PhysicianEndpoint';
import StoreContext from '../StoreContext/StoreContext';
import { IPhysician, IPatient, IPrescription, ILabTest } from '../../lib/Interfaces';
import { makeStyles } from '@material-ui/core';
import Prescriptions from '../../lib/components/Prescription/Prescription';
import SimpleModal from '../../lib/components/Modal/Modal';
import AddPrescription from './AddPrescription';
import LabTest from './LabTest';
import AddTest from './AddTest';

const useStyles = makeStyles(() => ({
    containerBtn: {
        display: 'flex',
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: '10px',
    }
  }));

const Physician = () => {
    const css = useStyles();
    const gCss = useGlobalStyles();
    const { physician, setPhysician, patient, prescription, connection } = React.useContext(StoreContext);
    const [labTests, setLabsTests] = useState<undefined | ILabTest[]>();

    useEffect(() => {
        connection.send(new GetPhysicianInfo().onSuccess((resp)=>{
            setPhysician(resp);
        }));
        connection.send(new GetLabTest().onSuccess((resp)=>{
            setLabsTests(resp);
        }));
    }, []);

    const ChangePhysician = () => {
        if (physician) {
            connection.send(new ChangePhysicianReq({
                ...physician,
                share: !physician.share
            }).onSuccess(()=>{
                // eslint-disable-next-line no-restricted-globals
                location.reload();
            }));
        }
    };

    const AddPrescriptions = (data: IPrescription) => {
        if (prescription) {
            connection.send(new AddPrescriptionsReq(data).onSuccess(()=>{
                // eslint-disable-next-line no-restricted-globals
                location.reload();
            }));
        }
    };

    const AddLabTest = (data: ILabTest) => {
        connection.send(new AddLabTestReq(data).onSuccess(()=>{
            // eslint-disable-next-line no-restricted-globals
            location.reload();
        }));
    };

    const renderInfo = (person: IPhysician) => (
        <div className={gCss.container}>
            <div className={gCss.name}>{person.full_name}</div>
            <div className={gCss.addInfoContainer}>
                <div className={gCss.label}>license number:</div>
                <div className={gCss.addInfo}>{person.license_no}</div>
                <div onClick={ChangePhysician} className={gCss.share}>
                    {!person.share && `share to ${patient?.full_name}`}
                </div>
            </div>
        </div>
    );

    const renderSecondInfo = (person: IPatient) => (
        <>
            <div className={gCss.sectionTitle}>Patient:</div>
            <div className={gCss.container}>
                <div className={gCss.surName}>{person.full_name}</div>
                {person.share &&
                <div className={gCss.addInfoContainer}>
                    <div className={gCss.label}>Social security number:</div>
                    <div className={gCss.addInfo}>{person.ssn}</div>
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
            {physician && renderInfo(physician)}
            {patient && renderSecondInfo(patient)}
            <div className={css.containerBtn}>
                <SimpleModal value="Add prescription">
                    <AddPrescription onSubmit={AddPrescriptions}/>
                </SimpleModal>
                <SimpleModal value="Add lab test">
                    <AddTest onSubmit={AddLabTest}/>
                </SimpleModal>
            </div>
            {prescription && renderPrescription(prescription)}
            {labTests && renderLabTest(labTests)}
        </>
    );
};

export default Physician;
