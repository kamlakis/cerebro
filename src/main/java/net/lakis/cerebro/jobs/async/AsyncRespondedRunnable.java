package net.lakis.cerebro.jobs.async;

public class AsyncRespondedRunnable implements Runnable {

	private AsyncHolder<?> holder;

	public AsyncRespondedRunnable(AsyncHolder<?> holder) {
		this.holder = holder;
	}

	@Override
	public void run() {
		holder.responded();
	}

}
