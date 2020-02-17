import { Request } from '../../lib/Connection/Request'
import { Method } from '../../lib/Connection/Connection'
import { ILabTest, IPhysician, IPrescription } from '../../lib/Interfaces';

const PHYSICIAN_INFO = 'physician_info';
const PRESCRIPTION = 'prescriptions';
const LAB_TESTS = 'lab_tests';
const CHANGE_PERMISSION = 'lab_tests/';

export class GetLabTest extends Request<null, ILabTest[]> {
	method = Method.Get;
	endpoint = LAB_TESTS;
};

export class GetPhysicianInfo extends Request<null, IPhysician> {
	method = Method.Get;
	endpoint = PHYSICIAN_INFO;
};

export class ChangePhysicianReq extends Request<IPhysician, null> {
	method = Method.Put;
	endpoint = PHYSICIAN_INFO;
	constructor(data: IPhysician) {
		super();
		this.params = data;
	}
};

export class AddPrescriptionsReq extends Request<IPrescription, null> {
	method = Method.Post;
	endpoint = PRESCRIPTION;
	constructor(data: IPrescription) {
		super();
		this.params = data;
	}
};
export class AddLabTestReq extends Request<ILabTest, null> {
	method = Method.Post;
	endpoint = LAB_TESTS;
	constructor(data: ILabTest) {
		super();
		this.params = data;
	}
};

export class ChangePermissionToResultReq extends Request<ILabTest, null> {
	method = Method.Put;
	endpoint = CHANGE_PERMISSION;
	constructor(data: ILabTest, id: string) {
		super();
		this.params = data;
		this.endpoint += id;
	}
};
