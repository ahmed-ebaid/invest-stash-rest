package com.stashinvest.http;

import java.util.concurrent.Callable;

import com.stashinvest.rest.AccountKeyServiceRequest;
import com.stashinvest.rest.AccountKeyServiceResponse;

//A callable task for returning the account key
public class AccountKeyCallableTask implements
		Callable<AccountKeyServiceResponse> {
	private AccountKeyServiceRequest accountKeyServiceRequest;

	public AccountKeyCallableTask(AccountKeyServiceRequest accountKeyServiceRequest) {
		this.accountKeyServiceRequest = accountKeyServiceRequest;
	}

	@Override
	public AccountKeyServiceResponse call() throws Exception {
		return AccountKeyRequest.post(accountKeyServiceRequest);
	}

}
