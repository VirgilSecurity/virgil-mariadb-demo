import { Request } from '../../lib/Connection/Request'
import { Method } from '../../lib/Connection/Connection'
import { InfoResponse, IPatient, ILabTest, IPrescription } from '../../lib/Interfaces';

const PATIENT_INFO = 'patient_info';
const LAB_TESTS = 'lab_tests';
const PRESCRIPTIONS = 'prescriptions';

export class GetPatientInfo extends Request<null, InfoResponse> {
	method = Method.Get;
	endpoint = PATIENT_INFO;
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
	endpoint = PATIENT_INFO;
	constructor(data: IPatient) {
		super();
		this.params = data;
	}
};
