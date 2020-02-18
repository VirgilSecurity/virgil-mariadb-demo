import React from 'react';
import { IPatient, IPhysician, IPrescription } from '../../lib/Interfaces';
import { Connection } from '../../lib/Connection/Connection';

export interface StoreContextProps {
    connection: Connection;
    // patientCred: ICredentials | undefined;



    patient: IPatient | undefined;
    setPatient: (patient: IPatient) => void;
    physician: IPhysician | undefined;
    setPhysician: (physician: IPhysician) => void;
    prescription: IPrescription[] | undefined;
    setPrescription: (prescription: IPrescription[]) => void;
    nextPrescriptionId: () => number;
    nextTestId: () => number;
}

const StoreContext = React.createContext<StoreContextProps>({
    connection: new Connection(),
    // patientCred: undefined,


    
    patient: undefined,
    setPatient: () => {},
    physician: undefined,
    setPhysician: () => {},
    prescription: undefined,
    setPrescription: () => {},
    nextPrescriptionId: () => 0,
    nextTestId: () => 0,
});

export default StoreContext;
