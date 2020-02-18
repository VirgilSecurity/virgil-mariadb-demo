
export interface ICredentials {
	grant: string;
	id: string;
};

export interface IReset {
	patients: ICredentials[];
	physicians: ICredentials[];
	laboratories: ICredentials[];
};

export interface IPatient {
    id: string;
    name: string;
    ssn: string;
};

export interface IPhysician {
    id: string;
    name: string;
    license_no: string;
};

export interface ILabTest {
    id: string;
    name: string;
    results: string | null;
    status: string;
    patient_id: string;
    physician_id: string;
    test_date: string;
};

export interface ILabTestPost {
    id: null;
    name: string;
    results: null;
    status: null;
    patient_id: string;
    physician_id: null;
    test_date: string;
};

export interface IPrescription {
    id: string;
    notes: string | null;
    patient_id: string;
    physician_id: string;
    assign_date: string;
    release_date: string;
};

export interface IPrescriptionPost {
    id: null;
    notes: string;
    patient_id: string;
    physician_id: null;
    assign_date: string;
    release_date: string;
};

export interface IShare {
    data_id: string;
    share_with: string[];
    roles: null;
};

export enum Status {
    notReady = "NOT_READY",
    ok = "OK",
    notAllow = "PERMISSION_DENIED"
};
