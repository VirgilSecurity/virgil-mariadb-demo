import { Request } from './Request';

const PORT = '8080';
const DB_PORT = '8080';
export const SITE_URL = `http://localhost:${PORT}/`;
export const DB_URL = `http://localhost:${DB_PORT}/db`;

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
		const url = SITE_URL + request.endpoint;
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
