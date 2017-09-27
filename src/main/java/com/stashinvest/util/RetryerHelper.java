package com.stashinvest.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;
import com.stashinvest.db.Updatable;

public class RetryerHelper<T> {
	private Updatable<T> updatabale;
	private static Logger log = Logger.getLogger(RetryerHelper.class);

	public RetryerHelper(Updatable<T> updatabale) {
		this.updatabale = updatabale;
	}

	public void retry(int numOfRetries, TimeUnit waitTimeUnit, int waitTime,
			Callable<T> task, Class<? extends Exception> exceptionType) {
		Retryer<T> retryer = RetryerBuilder
				.<T> newBuilder()
				.retryIfResult(Predicates.<T> isNull())
				.retryIfExceptionOfType(exceptionType)
				.withWaitStrategy(
						WaitStrategies.fixedWait(waitTime, waitTimeUnit))
				.withStopStrategy(StopStrategies.stopAfterAttempt(numOfRetries))
				.build();
		try {
			updatabale.update(retryer.call(task));
		} catch (RetryException e) {
			log.warn("Retry attempts have failed", e);
		} catch (ExecutionException e) {
			log.error("Retry attempts failed executing", e);
		}
	}

}
