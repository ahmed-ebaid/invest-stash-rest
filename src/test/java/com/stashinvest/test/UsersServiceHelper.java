package com.stashinvest.test;

import java.util.concurrent.TimeUnit;

import com.stashinvest.rest.DBErrors;
import com.stashinvest.rest.Users;

public class UsersServiceHelper {
	public static boolean checkDBErrorsContains(DBErrors errors, String error) {
		return errors.getErrors().contains(error);
	}

	public static void sleep(TimeUnit timeUnit, long timeout)
			throws InterruptedException {
		timeUnit.sleep(timeout);
	}

	public static String generateUsersServiceQuery(String query) {
		return String.format("?query=%s", query);
	}

	public static boolean checkUsersObjectContainsUserWithGivenEmail(
			Users users, String email) {
		if (users == null || users.getUsers().isEmpty()) {
			return false;
		}
		return users.getUsers().get(0).getEmail().equals(email);
	}

	public static boolean verifyAccountKeyNotNull(Users users) {
		if (users == null || users.getUsers().isEmpty()) {
			return false;
		}
		return users.getUsers().get(0).getAccountKey() != null;
	}

}
