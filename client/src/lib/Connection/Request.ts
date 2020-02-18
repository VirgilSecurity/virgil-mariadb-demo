import { Method } from './Connection';

export type SuccessHandler<T> = (respParams: T) => void;
export type ErrorHandler = (error: Error) => void;

export abstract class Request<ReqParams = unknown, RespParams = unknown> {
	abstract endpoint: string;
	abstract method: Method;

	params?: ReqParams;
	eventName?: string;
	grant?: string;
	headers: Headers = new Headers();

	prefix?: string;
	isEmptyResponse: boolean = false;

	private successHandlers: SuccessHandler<RespParams>[] = [];
	private errorHandler: ErrorHandler[] = [];

	getOptions(): RequestInit {
		const options: RequestInit = { method: this.method.toString() };

		if (this.params) {
			options.body = JSON.stringify(this.params);
			this.headers.set('Content-Type', 'application/json');
		}

		if (this.grant) this.headers.append('X-Virgil-Pure-Grant', this.grant);

		if (this.headers) options.headers = this.headers;

		return options;
	}

	getResponseBody(response: Response): Promise<RespParams> {
		return this.isEmptyResponse ? Promise.resolve({} as RespParams) : response.json();
	}

	onSuccess(handler: SuccessHandler<RespParams>) {
		this.successHandlers.push(handler);
		return this;
	}

	onError(errorHandler: ErrorHandler) {
		this.errorHandler.push(errorHandler);
		return this;
	}

	handleSuccess = (result: RespParams) => {
		this.successHandlers.forEach(handler => handler(result));
	};

	handleError = (error: Error) => {
		const promise = this.errorHandler.reduce<Promise<void>>(
			(prevHandler, nextHandler) => prevHandler.catch(nextHandler),
			Promise.reject(error),
		);

		return promise;
	};
};
