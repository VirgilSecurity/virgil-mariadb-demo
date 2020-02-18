import { Request } from '../../lib/Connection/Request'
import { Method } from '../../lib/Connection/Connection'
import { IPatient } from '../../lib/Interfaces';

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
