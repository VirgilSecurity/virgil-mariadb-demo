import { Request } from '../../lib/Connection/Request'
import { Method } from '../../lib/Connection/Connection'
import { IPhysician, IPrescriptionPost, ILabTestPost } from '../../lib/Interfaces';

const LAB_TESTS = 'lab-tests';

const PHYSICIAN_BASE = 'physicians';
const PHYSICIAN_INFO = (id: string) => `${PHYSICIAN_BASE}/${id}`;

const PRESCRIPTION_BASE = 'prescriptions';

export class PhysicianInfoReq extends Request<null, IPhysician> {
	method = Method.Get;
	endpoint = PHYSICIAN_INFO(this.id);
	constructor(public id: string, public grant: string) {
		super();
	}
};

export class PhysicianListReq extends Request<null, IPhysician[]> {
	method = Method.Get;
	endpoint = PHYSICIAN_BASE;
	constructor(public grant: string) {
		super();
	}
};

export class AddPrescriptionsReq extends Request<IPrescriptionPost, null> {
	method = Method.Post;
	endpoint = PRESCRIPTION_BASE;
	isEmptyResponse = true;
	constructor(data: IPrescriptionPost, public grant: string) {
		super();
		this.params = data;
	}
};

export class AddLabTestReq extends Request<ILabTestPost, null> {
	method = Method.Post;
	endpoint = LAB_TESTS;
	isEmptyResponse = true;
	constructor(data: ILabTestPost, public grant: string) {
		super();
		this.params = data;
	}
};
