package com.stashinvest.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stashinvest.rest.User;

public class VerificationUtil {
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
			"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	public static boolean isValidEmail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

	public static boolean isValidQuery(String query) {
		return !query.isEmpty() && (query.length() <= 2000);
	}
	
	public static void verifyValidUserParameters(User user, List<String> errorMessages) {
		String email = user.getEmail();
		if (email == null || email.isEmpty()) {
			errorMessages.add("Email is missing");
		} else {
			if (!isValidEmail(email)) {
				errorMessages.add("Invalid email address");
			}
		}
		String phoneNumber = user.getPhoneNumber();
		if (phoneNumber == null || phoneNumber.isEmpty()) {
			errorMessages.add("Phone Number is missing");
		} else {
			if (phoneNumber.length() > 20) {
				errorMessages.add("Phone Number is too long");
			}
		}
		String fullName = user.getFullName();
		if (fullName != null && fullName.length() > 200) {
			errorMessages.add("Full Name is too long");
		}
		String password = user.getPassword();
		if (password == null || password.isEmpty()) {
			errorMessages.add("Password cannot be empty");
		}
	}
}
