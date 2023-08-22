package net.lakis.cerebro.jobs.async;

public class AsyncRunnable implements Runnable {

	private Runnable runnable;

	public AsyncRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		this.runnable.run();
	}

}
