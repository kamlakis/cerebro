package net.lakis.cerebro.jobs.timeout;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
public class TimeoutRunnable implements Runnable {

	private AtomicBoolean responded;
	private ScheduledFuture<?> timeoutFuture;
	private @Setter Runnable runnable;
	private @Setter Runnable onTimeout;

	public TimeoutRunnable() {
		this.responded = new AtomicBoolean(false);
	}

	public void scheduleTimeout(ScheduledExecutorService executor, long ms) {
		this.timeoutFuture = executor.schedule(this::onTimeoutRun, ms, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {

		if (responded.compareAndSet(false, true)) {
			if (timeoutFuture != null)
				timeoutFuture.cancel(true);
			runnable.run();
		}
	}

	public void onTimeoutRun() {
		if (responded.compareAndSet(false, true)) {
			onTimeout.run();
		}

	}

	
}
