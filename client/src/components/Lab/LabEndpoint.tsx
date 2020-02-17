import { Request } from '../../lib/Connection/Request'
import { Method } from '../../lib/Connection/Connection'
import { ILabTest } from '../../lib/Interfaces';

const LAB_TESTS = 'lab_tests/';

export class GetLabTest extends Request<null, ILabTest[]> {
	method = Method.Get;
	endpoint = LAB_TESTS;
};

export class AddResultReq extends Request<ILabTest, null> {
	method = Method.Put;
	endpoint = LAB_TESTS;
	constructor(data: ILabTest, id: string) {
		super();
		this.params = data;
		this.endpoint += id;
	}
};
