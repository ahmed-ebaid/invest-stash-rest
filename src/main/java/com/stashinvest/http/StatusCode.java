package com.stashinvest.http;


public enum StatusCode {
	UNPROCESSED_ENTITY(422, "Unprocessable Entity");
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
