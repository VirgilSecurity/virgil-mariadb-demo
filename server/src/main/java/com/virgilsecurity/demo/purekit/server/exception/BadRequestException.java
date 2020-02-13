package com.virgilsecurity.demo.purekit.server.exception;

public class BadRequestException extends RuntimeException {

	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

}
