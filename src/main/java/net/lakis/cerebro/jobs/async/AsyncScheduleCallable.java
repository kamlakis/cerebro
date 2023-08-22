package net.lakis.cerebro.jobs.async;

import java.util.concurrent.Callable;

public class AsyncScheduleCallable<T> implements Callable<T> {

	private Callable<T> callable;

	public AsyncScheduleCallable(Callable<T> callable) {
		this.callable = callable;
	}

	@Override
	public T call() throws Exception {
		return callable.call();
	}

}
