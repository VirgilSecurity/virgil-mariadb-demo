import { Request } from '../../lib/Connection/Request'
import { Method } from '../../lib/Connection/Connection'
import { IPrescription, ILabTest, IReset } from '../../lib/Interfaces';

const RESET = 'reset';
const PRESCRIPTION_BASE = 'prescriptions';
const LAB_TEST_BASE = 'lab-tests';


export class ResetReq extends Request<null, IReset> {
	method = Method.Post;
	endpoint = RESET;
};

export class PrescriptionsListReq extends Request<null, IPrescription[]> {
	method = Method.Get;
	endpoint = PRESCRIPTION_BASE;
	constructor(public grant: string) {
		super();
	}
};

export class LabTestListReq extends Request<null, ILabTest[]> {
	method = Method.Get;
	endpoint = LAB_TEST_BASE;
	constructor(public grant: string) {
		super();
	}
};

export class PrescriptionsReq extends Request<null, IPrescription[]> {
	method = Method.Get;
	endpoint = PRESCRIPTION_BASE;
};
