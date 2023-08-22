package net.lakis.cerebro.jobs.async;

public interface AsyncResponseHandler<T> {
	public void onResponse(T response);

	public void onTimeout();
}
