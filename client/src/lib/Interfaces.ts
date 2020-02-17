
export interface IPatient {
    id: string;
    full_name: string;
    ssn: string;
    share: boolean;
};

export interface IPhysician {
    id: string;
    full_name: string;
    license_no: string;
    share: boolean;
};

export interface ILabTest {
    id: string;
    test_name: string;
    patient_id: string;
    physician_id: string;
    test_date: string;
    results: string | null;
    share: boolean;
};

export interface IPrescription {
    id: string;
    patient_id: string;
    physician_id: string;
    notes: string;
    assign_date: string;
    release_date: string;
};

export interface InfoResponse extends IPatient, IPhysician {}
