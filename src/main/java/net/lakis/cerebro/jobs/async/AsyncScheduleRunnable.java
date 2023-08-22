package net.lakis.cerebro.jobs.async;

public class AsyncScheduleRunnable implements Runnable {

	private Runnable runnable;

	public AsyncScheduleRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		this.runnable.run();
	}

}
