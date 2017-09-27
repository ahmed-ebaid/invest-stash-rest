package com.stashinvest.http;

public enum StatusCode {
	CREATED(201, "Created"), BAD_REQUEST(400, "Bad Request"), UNPROCESSED_ENTITY(
			422, "Unprocessable Entity");

	private final int code;
	private final String reason;

	StatusCode(int statusCode, String reasonPhrase) {
		this.code = statusCode;
		this.reason = reasonPhrase;
	}

	public int code() {
		return code;
	}

	public String reason() {
		return reason;
	}
}
