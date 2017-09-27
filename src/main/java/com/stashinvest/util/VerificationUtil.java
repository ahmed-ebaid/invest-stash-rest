package com.stashinvest.util;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stashinvest.rest.User;

public class VerificationUtil {
	public static final String MISSING_EMAIL_ADDRESS = "Email is missing";
	public static final String INVALID_EMAIL_ADDRESS = "Invalid email address";
	public static final String MISSING_PHONE_NUMBER = "Phone Number is missing";
	public static final String PHONE_NUMBER_TOO_LONG = "Phone number can't be longer than 20 digits";
	public static final String FULL_NAME_TOO_LONG = "Full name can't be longer than 200 characters";
	public static final String MISSING_PASSWORD = "Password is missing";
	public static final String INVALID_USER_PARAMETERS = "Invalid user parameters";

	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
			"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * @param emailStr
	 *            email address of a user
	 * @return true if email is valid
	 */
	public static boolean isValidEmail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

	/**
	 * @param query
	 *            url query
	 * @return true of query is not empty and is not longer than 2000 characters
	 */
	public static boolean isValidQuery(String query) {
		return !query.isEmpty() && (query.length() <= 2000);
	}

	/**
	 * @param user
	 *            User object
	 * @param errorMessages
	 *            a reference to error message array
	 * @throws SQLException
	 */
	public static void verifyValidUserParameters(User user,
			List<String> errorMessages) throws SQLException {
		String email = user.getEmail();
		if (email == null || email.isEmpty()) {
			errorMessages.add(MISSING_EMAIL_ADDRESS);
		} else {
			if (!isValidEmail(email)) {
				errorMessages.add(INVALID_EMAIL_ADDRESS);
			}
		}
		String phoneNumber = user.getPhoneNumber();
		if (phoneNumber == null || phoneNumber.isEmpty()) {
			errorMessages.add(MISSING_PHONE_NUMBER);
		} else {
			if (phoneNumber.length() > 20) {
				errorMessages.add(PHONE_NUMBER_TOO_LONG);
			}
		}
		String fullName = user.getFullName();
		if (fullName != null && fullName.length() > 200) {
			errorMessages.add(FULL_NAME_TOO_LONG);
		}
		String password = user.getPassword();
		if (password == null || password.isEmpty()) {
			errorMessages.add(MISSING_PASSWORD);
		}
		if (!errorMessages.isEmpty()) {
			throw new SQLException(INVALID_USER_PARAMETERS);
		}
	}
}
