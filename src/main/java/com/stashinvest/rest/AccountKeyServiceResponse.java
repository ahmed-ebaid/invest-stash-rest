package com.stashinvest.rest;

import java.util.Objects;


public class AccountKeyServiceResponse {
	private String email;
	private String accountKey;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof AccountKeyServiceResponse)) {
			return false;
		}
		AccountKeyServiceResponse response = (AccountKeyServiceResponse) o;
		return this.email.equalsIgnoreCase(response.email)
				&& this.accountKey.equalsIgnoreCase(response.accountKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.email, this.accountKey);
	}

	@Override
	public String toString() {
		return "User [email=" + email + "accountKey=" + accountKey + "]";
	}
}
