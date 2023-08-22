package net.lakis.cerebro.jobs.async;

public class AsyncTimeoutRunnable implements Runnable {

	private AsyncHolder<?> holder;

	public AsyncTimeoutRunnable(AsyncHolder<?> holder) {
		this.holder = holder;
	}

	@Override
	public void run() {
		holder.timeout();
	}

}
