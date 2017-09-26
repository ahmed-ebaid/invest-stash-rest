package com.stashinvest.util;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stashinvest.rest.User;

//A class containing verificatino utility methods
/**
 * @author ahmedebaid
 *
 */
public class VerificationUtil {
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
		if (!errorMessages.isEmpty()) {
			throw new SQLException("Invalid user parameters");
		}
	}
}
