import { Request } from '../../lib/Connection/Request'
import { Method } from '../../lib/Connection/Connection'
import { IPatient, ILabTest, IPrescription } from '../../lib/Interfaces';

const LAB_TESTS = 'lab_tests';
const PRESCRIPTIONS = 'prescriptions';

const PATIENT_BASE = 'patients';
const PATIENT_INFO = (id: string) => `${PATIENT_BASE}/${id}`;

export class PatientInfoReq extends Request<null, IPatient> {
	method = Method.Get;
	endpoint = PATIENT_INFO(this.id);
	constructor(public id: string, public grant: string) {
		super();
	}
};

export class PatientListReq extends Request<null, IPatient[]> {
	method = Method.Get;
	endpoint = PATIENT_BASE;
	constructor(public grant: string) {
		super();
	}
};

export class GetLabTest extends Request<null, ILabTest[]> {
	method = Method.Get;
	endpoint = LAB_TESTS;
};

export class PrescReq extends Request<null, IPrescription[]> {
	method = Method.Get;
	endpoint = PRESCRIPTIONS;
};

export class ChangePatientInfo extends Request<IPatient, null> {
	method = Method.Post;
	get endpoint() {
		return PATIENT_INFO(this.id);
	}
	constructor(private id: string, data: IPatient) {
		super();
		this.params = data;
	}
};
