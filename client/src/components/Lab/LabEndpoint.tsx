import { Request } from '../../lib/Connection/Request'
import { Method } from '../../lib/Connection/Connection'
import { ILabTest } from '../../lib/Interfaces';

const LAB_TESTS = 'lab-tests';
const ADD_RESULT = (id: string) => `${LAB_TESTS}/${id}`;


export class AddResultReq extends Request<ILabTest, null> {
	method = Method.Put;
	endpoint = ADD_RESULT(this.id);
	isEmptyResponse = true;
	constructor(data: ILabTest, public grant: string, public id: string) {
		super();
		this.params = data;
	}
};
