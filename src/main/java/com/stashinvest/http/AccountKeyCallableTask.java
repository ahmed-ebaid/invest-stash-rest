package com.stashinvest.http;

import java.util.concurrent.Callable;

import com.google.gson.Gson;
import com.stashinvest.rest.User;

//A callable task for returning the account key
public class AccountKeyCallableTask implements
		Callable<User> {
	private User user;

	public AccountKeyCallableTask(
			User user) {
		this.user = user;
	}

	@Override
	public User call() throws Exception {
		HttpRequests<User> requests = new HttpRequests<>();
		return requests.httpsPost(new Gson().toJson(user),
				User.class);
	}
}
