package net.lakis.cerebro.jobs.timeout;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		new Test().consume();
	}

	private void consume() throws InterruptedException {
		this.threadpool = Executors.newScheduledThreadPool(10);
		TimeoutConsumer<String> timeoutRunnable = new TimeoutConsumer<>();

		timeoutRunnable.consumer(s -> {
			System.out.println(s);
		});

		timeoutRunnable.onTimeout(() -> {
			System.out.println("onTimeout");
		});

		timeoutRunnable.scheduleTimeout(threadpool, 3000);

		Thread.sleep(2800);

		timeoutRunnable.accept("kamal");
	}

	private ScheduledExecutorService threadpool;

	public void action() {
		this.threadpool = Executors.newScheduledThreadPool(10);
		TimeoutRunnable timeoutRunnable = new TimeoutRunnable();

		timeoutRunnable.runnable(() -> {
			System.out.println("action");
		});

		timeoutRunnable.onTimeout(() -> {
			System.out.println("onTimeout");
		});

		timeoutRunnable.scheduleTimeout(threadpool, 3000);

		threadpool.schedule(timeoutRunnable, 3000, TimeUnit.MILLISECONDS);

	}

}
