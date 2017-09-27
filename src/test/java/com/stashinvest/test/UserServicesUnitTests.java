package com.stashinvest.test;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.stashinvest.db.DBErrors;
import com.stashinvest.http.HttpRequests;
import com.stashinvest.http.StatusCode;
import com.stashinvest.rest.User;
import com.stashinvest.rest.Users;
import com.stashinvest.rest.UsersService;
import com.stashinvest.util.VerificationUtil;

public class UserServicesUnitTests {
	private static Logger log = Logger.getLogger(UserServicesUnitTests.class);
	private static Faker faker;
	private static UsersService restService;
	private static final String EMPTY_STRING = "";
	private static HttpRequests<Users> request;

	@BeforeClass
	public static void setUp() {
		faker = new Faker();
		restService = new UsersService();
		request = new HttpRequests<>();
	}

	@Test
	public void addUserWithMissingOrInvalidParameters() throws IOException {
		User user = new User();
		user.setEmail(EMPTY_STRING);
		user.setPhoneNumber(faker.phoneNumber().phoneNumber());
		user.setFullName(faker.name().fullName());
		user.setPassword(faker.crypto().sha1());
		user.setMetadata(faker.address().fullAddress());
		Response respone = restService.createUser(user);
		log.info("Adding a user with missig email");
		Assert.assertTrue("User can't be added with missing email",
				respone.getStatus() == StatusCode.UNPROCESSED_ENTITY.code());
		DBErrors errors = (DBErrors) respone.getEntity();
		Assert.assertTrue("Invalid error message for email", UsersServiceHelper
				.checkDBErrorsContains(errors,
						VerificationUtil.MISSING_EMAIL_ADDRESS));

		log.info("Adding a user with missing phone number");
		user.setEmail(faker.internet().emailAddress());
		user.setPhoneNumber(EMPTY_STRING);
		respone = restService.createUser(user);
		Assert.assertTrue("User can't be added with missing phone number",
				respone.getStatus() == StatusCode.UNPROCESSED_ENTITY.code());
		errors = (DBErrors) respone.getEntity();
		Assert.assertTrue("Invalid error message for phone number",
				UsersServiceHelper.checkDBErrorsContains(errors,
						VerificationUtil.MISSING_PHONE_NUMBER));

		log.info("Adding a user with missing password");
		user.setPhoneNumber(faker.phoneNumber().phoneNumber());
		user.setPassword(EMPTY_STRING);
		respone = restService.createUser(user);
		Assert.assertTrue("User can't be added with missing password",
				respone.getStatus() == StatusCode.UNPROCESSED_ENTITY.code());
		errors = (DBErrors) respone.getEntity();
		Assert.assertTrue("Invalid error message for password",
				UsersServiceHelper.checkDBErrorsContains(errors,
						VerificationUtil.MISSING_PASSWORD));

		log.info("Adding a user with missing email and long phone number");
		user.setEmail(EMPTY_STRING);
		user.setPhoneNumber(String.join("",
				Collections.nCopies(10, faker.phoneNumber().toString())));
		respone = restService.createUser(user);
		Assert.assertTrue(
				"User can't be added with missing password or a long phone number",
				respone.getStatus() == StatusCode.UNPROCESSED_ENTITY.code());
		errors = (DBErrors) respone.getEntity();
		Assert.assertTrue("Invalid error message for email", UsersServiceHelper
				.checkDBErrorsContains(errors,
						VerificationUtil.MISSING_EMAIL_ADDRESS));
		Assert.assertTrue("Invalid error message for phone number",
				UsersServiceHelper.checkDBErrorsContains(errors,
						VerificationUtil.PHONE_NUMBER_TOO_LONG));

		log.info("Adding a user with long phone number");
		user.setEmail(faker.internet().emailAddress());
		user.setPhoneNumber(String.join("",
				Collections.nCopies(10, faker.phoneNumber().toString())));
		respone = restService.createUser(user);
		Assert.assertTrue(
				"User can't be added with missing password or a long phone number",
				respone.getStatus() == StatusCode.UNPROCESSED_ENTITY.code());
		errors = (DBErrors) respone.getEntity();
		Assert.assertTrue("Invalid error message for phone number",
				UsersServiceHelper.checkDBErrorsContains(errors,
						VerificationUtil.PHONE_NUMBER_TOO_LONG));
	}

	@Test
	public void addUserVerifyKeyAndAccountKeyExistAfterCreation()
			throws IOException, InterruptedException {
		User user = new User();
		String email = faker.internet().emailAddress();
		user.setEmail(email);
		user.setPhoneNumber(faker.phoneNumber().phoneNumber());
		user.setFullName(faker.name().fullName());
		user.setPassword(faker.crypto().sha1());
		user.setMetadata(faker.address().fullAddress());
		Response respone = restService.createUser(user);
		log.info("Verifying user is created");
		Assert.assertTrue(
				String.format("User %s should have been created", user),
				respone.getStatus() == StatusCode.CREATED.code());
		Users users = (Users) respone.getEntity();
		Assert.assertTrue("Key value shouldn't be empty", UsersServiceHelper
				.checkUsersObjectContainsUserWithGivenEmail(users, email));
		UsersServiceHelper.sleep(TimeUnit.MINUTES, 1);
		log.info("Verifying account key is not null");
		users = request.httpGet(
				UsersServiceHelper.generateUsersServiceQuery(email),
				Users.class);
		Assert.assertTrue("Account Key value shouldn't be null",
				UsersServiceHelper.checkUsersObjectContainsUserWithGivenEmail(
						users, email));
	}

	@Test
	public void getUsersWithInvalidQueryAndVerifyError() throws IOException,
			InterruptedException {
		log.info("Verifying account key is not null");
		Users users = request.httpGet(
				UsersServiceHelper.generateUsersServiceQuery(EMPTY_STRING),
				Users.class);
		Assert.assertTrue(
				"Can't get users with an empty query",
				request.getConnection().getResponseCode() == StatusCode.UNPROCESSED_ENTITY
						.code());
	}

	@Test
	public void createUserWithAdditionalFieldsAndVerifyErrors()
			throws IOException, InterruptedException {
		User user = new User();
		user.setEmail(faker.internet().emailAddress());
		user.setPhoneNumber(faker.phoneNumber().phoneNumber());
		user.setFullName(faker.name().fullName());
		user.setPassword(faker.crypto().sha1());
		user.setMetadata(faker.address().fullAddress());
		user.setKey(faker.book().title());
		Users users = request.httpPost(new Gson().toJson(user), Users.class);
		Assert.assertTrue(
				"Allowed fields for creating a user are email, phone number, full_name, password, metadata",
				request.getConnection().getResponseCode() == StatusCode.BAD_REQUEST
						.code());
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {

	}
}
