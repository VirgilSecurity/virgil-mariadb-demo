import { Request } from './Request';

const URL = 'http://127.0.0.1:3001/';

export enum Method {
	Get = 'GET',
	Post = 'POST',
	Put = 'PUT',
	Delete = 'DELETE',
}

export interface IConnection {
	send<ReqParams, RespParams>(request: Request<ReqParams, RespParams>): void;
}

export class Connection implements IConnection {
	send = <ReqParams, RespParams>(request: Request<ReqParams, RespParams>): void => {
		const url = URL + request.endpoint;
		fetch(url, request.getOptions())
			.then(response => this.handleResponse(request, response))
			.then(request.handleSuccess)
			.catch(request.handleError);
	};

	private handleResponse<ReqParams, RespParams>(
		req: Request<ReqParams, RespParams>,
		res: Response,
	) {
		if (res.ok) {
			return req.getResponseBody(res);
		}

		throw new Error(`Error: ${res.status}`);
	}
};
