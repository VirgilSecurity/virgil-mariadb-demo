import React, { useEffect, useState } from 'react';
import { useGlobalStyles } from '../../lib/styles';
import { GetPhysicianInfo, ChangePhysicianReq, AddPrescriptionsReq, GetLabTest, AddLabTestReq, PhysicianInfoReq } from './PhysicianEndpoint';
import StoreContext from '../StoreContext/StoreContext';
import { IPhysician, IPatient, IPrescription, ILabTest, ICredentials } from '../../lib/Interfaces';
import { makeStyles } from '@material-ui/core';
import Prescriptions from '../../lib/components/Prescription/Prescription';
import SimpleModal from '../../lib/components/Modal/Modal';
import AddPrescription from './AddPrescription';
import LabTest from './LabTest';
import AddTest from './AddTest';
import { PatientListReq } from '../Patient/PatientEndpoint';
import { PrescriptionsListReq, LabTestListReq } from '../../lib/Connection/Endpoints';

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
    const { connection } = React.useContext(StoreContext);
    const [physician, setPhysician] = useState<IPhysician | undefined>();
    const [patient, setPatient] = useState<IPatient | undefined>();
    const [prescriptions, setPrescriptions] = useState<IPrescription[] | undefined>();
    const [labTests, setLabTests] = useState<ILabTest[] | undefined>();

    // const css = useStyles();
    // const [labTests, setLabsTests] = useState<undefined | ILabTest[]>();

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

    // const ChangePhysician = () => {
    //     if (physician) {
    //         connection.send(new ChangePhysicianReq({
    //             ...physician,
    //             share: !physician.share
    //         }).onSuccess(()=>{
    //             // eslint-disable-next-line no-restricted-globals
    //             location.reload();
    //         }));
    //     }
    // };

    // const AddPrescriptions = (data: IPrescription) => {
    //     if (prescription) {
    //         connection.send(new AddPrescriptionsReq(data).onSuccess(()=>{
    //             // eslint-disable-next-line no-restricted-globals
    //             location.reload();
    //         }));
    //     }
    // };

    // const AddLabTest = (data: ILabTest) => {
    //     connection.send(new AddLabTestReq(data).onSuccess(()=>{
    //         // eslint-disable-next-line no-restricted-globals
    //         location.reload(); 
    //     }));
    // };

    const renderInfo = (person: IPhysician) => (
        <div className={gCss.container}>
            <div className={gCss.name}>{person.name}</div>
            <div className={gCss.addInfoContainer}>
                <div className={gCss.label}>license number:</div>
                <div className={gCss.addInfo}>{person.license_no}</div>
                {/* <div onClick={ChangePhysician} className={gCss.share}>
                    {!person.share && `share to ${patient?.full_name}`}
                </div> */}
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
        return <LabTest data={tests} />;
    };

    return (
        <>
            {physician && renderInfo(physician)}
            {patient && renderPatient(patient)}
            {/* <div className={css.containerBtn}>
                <SimpleModal value="Add prescription">
                    <AddPrescription onSubmit={AddPrescriptions}/>
                </SimpleModal>
                <SimpleModal value="Add lab test">
                    <AddTest onSubmit={AddLabTest}/>
                </SimpleModal>
            </div> */}
            {prescriptions && renderPrescription(prescriptions)}
            {labTests && renderLabTest(labTests)}
        </>
    );
};

export default Physician;
