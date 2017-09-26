package com.stashinvest.http;

import java.util.concurrent.Callable;

import com.stashinvest.rest.AccountKeyServiceRequest;
import com.stashinvest.rest.AccountKeyServiceResponse;

public class AccountKeyCallableTask implements
		Callable<AccountKeyServiceResponse> {
	private AccountKeyServiceRequest request;

	public AccountKeyCallableTask(AccountKeyServiceRequest request) {
		this.request = request;
	}

	@Override
	public AccountKeyServiceResponse call() throws Exception {
		ServiceRequest serviceRequest = new ServiceRequest();
		return serviceRequest.post(request);
	}

}
