package net.lakis.cerebro.jobs.async;

import java.util.concurrent.Future;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class AsyncHolder<T> {
	private AsyncExecutor<T> executor;
	private String id;

	private AsyncResponseHandler<T> responseHandler;

	private Future<?> timeoutFuture;

	private T response;
	private boolean responded;

	public AsyncHolder(AsyncExecutor<T> executor, String id) {
		this.executor = executor;
		this.id = id;
	}

	public void timeout() {
		if (id == null && this.responded) {
			return;
		} else if (this.executor.remove(id) == null || this.responded)
			return;
		if (responseHandler != null)
			responseHandler.onTimeout();
	}

	public void responded() {
		if (responseHandler != null)
			responseHandler.onResponse(response);
	}

}
