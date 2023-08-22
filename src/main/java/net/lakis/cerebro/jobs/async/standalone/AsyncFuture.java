package net.lakis.cerebro.jobs.async.standalone;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncFuture<T> implements Future<T> {

	private AsyncHolder<T> holder;

	public AsyncFuture() {
		this.holder = new AsyncHolder<T>();
	}

	public void responded(T response) {
		holder.responded(true);
		holder.response(response);
		synchronized (holder) {
			holder.notifyAll();
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		holder.timedout(true);
		synchronized (holder) {
			holder.notifyAll();
		}
		return true;
	}

	@Override
	public boolean isCancelled() {
		return holder.timedout();
	}

	@Override
	public boolean isDone() {
		return holder.timedout() || holder.responded();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		synchronized (holder) {
			if (holder.responded())
				return holder.response();
			if (holder.timedout())
				throw new ExecutionException("AsyncFuture Timeout", null);
			holder.wait();

		}
		if (holder.responded())
			return holder.response();
		throw new ExecutionException("AsyncFuture Timeout", null);

	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if (timeout == 0)
			return get();
		synchronized (holder) {
			if (holder.responded())
				return holder.response();
			if (holder.timedout())
				throw new TimeoutException("AsyncFuture Timeout");

			holder.wait(unit.toMillis(timeout));
		}
		if (!holder.responded()) {
			holder.timedout(true);
			throw new TimeoutException("AsyncFuture Timeout");
		}
		return holder.response();
	}

}
