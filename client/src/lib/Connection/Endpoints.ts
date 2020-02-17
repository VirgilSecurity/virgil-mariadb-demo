import { Request } from '../../lib/Connection/Request'
import { Method } from '../../lib/Connection/Connection'
import { IPrescription } from '../../lib/Interfaces';

const PRESCRIPTION = 'prescriptions';

export class PrescriptionsReq extends Request<null, IPrescription[]> {
	method = Method.Get;
	endpoint = PRESCRIPTION;
};
