package com.stashinvest.rest;

import java.util.Objects;

public class AccountKeyServiceRequest {
	private String email;
	private String key;

	public AccountKeyServiceRequest(String key, String email) {
		this.email = email;
		this.key = key;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof AccountKeyServiceRequest)) {
			return false;
		}
		AccountKeyServiceRequest request = (AccountKeyServiceRequest) o;
		return this.email.equalsIgnoreCase(request.email)
				&& this.key.equalsIgnoreCase(request.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.email, this.key);
	}

	@Override
	public String toString() {
		return "User [email=" + email + "key=" + key + "]";
	}

}
