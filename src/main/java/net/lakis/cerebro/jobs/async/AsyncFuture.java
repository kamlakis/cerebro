package net.lakis.cerebro.jobs.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class AsyncFuture<T> implements Future<T> {

	private AsyncExecutor<T> executor;
	private AsyncHolder<T> holder;

	public AsyncFuture(AsyncExecutor<T> executor, AsyncHolder<T> holder) {
		this.executor = executor;
		this.holder = holder;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return this.executor.remove(holder.getId()) != null;
	}

	@Override
	public boolean isCancelled() {
		return this.executor.get(holder.getId()) != null && !holder.isResponded();
	}

	@Override
	public boolean isDone() {
		return this.executor.get(holder.getId()) != null;
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		synchronized (holder) {
			if (holder.isResponded())
				return holder.getResponse();
			holder.wait();
		}
		return holder.getResponse();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if (timeout == 0)
			return get();
		synchronized (holder) {
			if (holder.isResponded())
				return holder.getResponse();
			holder.wait(unit.toMillis(timeout));

		}
		if (!holder.isResponded()) {
			executor.remove(holder.getId());
			throw new TimeoutException("AsyncFuture Timeout");
		}
		T response = holder.getResponse();
		return response;
	}

}
