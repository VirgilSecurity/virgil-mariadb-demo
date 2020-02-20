import { Request } from './Request';
import { reloadPage } from '../utils';

const PORT = '8080';
const DB_PORT = '8080';
export const REST_API = `http://localhost:${PORT}/api/v1/`;
export const DB_URL = `http://localhost:${DB_PORT}/api/v1/db`;

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
		const url = REST_API + request.endpoint;
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
		sessionStorage.clear();
		if (res.status === 400) {
			reloadPage();
		}
		throw new Error(`Error: ${res.status}`);
	}
};
