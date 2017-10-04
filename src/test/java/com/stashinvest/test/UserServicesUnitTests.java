package com.stashinvest.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.stashinvest.http.HttpRequests;
import com.stashinvest.http.StatusCode;
import com.stashinvest.rest.DBErrors;
import com.stashinvest.rest.User;
import com.stashinvest.rest.Users;
import com.stashinvest.rest.UsersService;
import com.stashinvest.util.VerificationUtil;

public class UserServicesUnitTests {
    private static Logger log = Logger.getLogger(UserServicesUnitTests.class);
    private static Faker faker;
    private static UsersService restService;
    private static final String EMPTY_STRING = "";

    @BeforeClass
    public static void setUp() {
	faker = new Faker();
	restService = new UsersService();
    }

    @Test
    public void addUserWithMissingOrInvalidParameters() throws IOException {
	User user = new User();
	user.setEmail(EMPTY_STRING);
	user.setPhoneNumber(faker.phoneNumber().cellPhone());
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
	user.setPhoneNumber(faker.phoneNumber().cellPhone());
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
	user.setPhoneNumber(faker.phoneNumber().cellPhone());
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
	HttpRequests<Users> request = new HttpRequests<>();
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
	HttpRequests<Users> request = new HttpRequests<>();
	request.httpGet(
		UsersServiceHelper.generateUsersServiceQuery(EMPTY_STRING),
		Users.class);
	Assert.assertTrue(
		"Can't get users with an empty query",
		request.getHttpConnection().getResponseCode() == StatusCode.UNPROCESSED_ENTITY
			.code());
    }

    @Test
    public void createUserWithAdditionalFieldsAndVerifyErrors()
	    throws IOException, InterruptedException {
	log.info("Verify a user can't be created with a key value");
	User user = new User();
	user.setEmail(faker.internet().emailAddress());
	user.setPhoneNumber(faker.phoneNumber().cellPhone());
	user.setFullName(faker.name().fullName());
	user.setPassword(faker.crypto().sha1());
	user.setMetadata(faker.address().fullAddress());
	user.setKey(faker.book().title());
	HttpRequests<Users> request = new HttpRequests<>();
	request.httpPost(new Gson().toJson(user), Users.class);
	Assert.assertTrue(
		"Allowed fields for creating a user are email, phone number, full_name, password, metadata",
		request.getHttpConnection().getResponseCode() == StatusCode.BAD_REQUEST
			.code());
    }

    @Test
    public void createUsersInMultiThreadedFashionVerifyNoError()
	    throws InterruptedException, ExecutionException {
	log.info("Create 100 users, verify their account_key is not null");
	List<Callable<Users>> callables = new ArrayList<>();
	for (int i = 0; i < 100; i++) {
	    Callable<Users> callable = () -> {
		User user = new User();
		user.setEmail(faker.internet().emailAddress());
		user.setPhoneNumber(faker.phoneNumber().cellPhone());
		user.setFullName(faker.name().fullName());
		user.setPassword(faker.crypto().sha1());
		user.setMetadata(faker.address().fullAddress());
		HttpRequests<Users> request = new HttpRequests<>();
		return request.httpPost(new Gson().toJson(user), Users.class);
	    };
	    callables.add(callable);
	}
	ExecutorService executorService = Executors.newCachedThreadPool();
	List<Future<Users>> futures = executorService.invokeAll(callables);
	for (Future<Users> f : futures) {
	    f.get();
	}
	executorService.shutdown();
	executorService.awaitTermination(2, TimeUnit.MINUTES);
	executorService.shutdownNow();
	HttpRequests<Users> request = new HttpRequests<>();
	Users users = request
		.httpGet(UsersServiceHelper.generateUsersServiceQuery(null),
			Users.class);
	for (User u : users.getUsers()) {
	    Assert.assertTrue("Key value shouldn't be empty",
		    u.getKey() != null);
	    Assert.assertTrue("Account key value shouldn't be empty",
		    u.getAccountKey() != null);
	}
    }
}
