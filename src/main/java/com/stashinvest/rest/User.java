package com.stashinvest.rest;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class User {
	private String email;
	private String phoneNumber;
	private String fullName;
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	@JsonProperty(access = Access.READ_ONLY)
	private String key;
	@JsonProperty(access = Access.READ_ONLY)
	private String accountKey;
	private String metadata;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof User)) {
			return false;
		}
		User user = (User) o;
		return this.email.equalsIgnoreCase(user.email)
				&& this.phoneNumber.equalsIgnoreCase(user.phoneNumber)
				&& this.fullName.equalsIgnoreCase(user.fullName)
				&& this.password.equalsIgnoreCase(user.password)
				&& this.key.equalsIgnoreCase(user.key)
				&& this.accountKey.equalsIgnoreCase(user.accountKey)
				&& this.metadata.equalsIgnoreCase(user.metadata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.email, this.phoneNumber, this.fullName,
				this.password, this.key, this.accountKey, this.metadata);
	}

	@Override
	public String toString() {
		return "User [email=" + email + ", phoneNumber=" + phoneNumber
				+ ", fullName=" + fullName + "password=" + password + "key="
				+ key + ", accountKey=" + accountKey + ", metadata=" + metadata
				+ "]";
	}

}