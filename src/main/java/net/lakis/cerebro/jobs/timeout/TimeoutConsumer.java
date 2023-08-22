package net.lakis.cerebro.jobs.timeout;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
public class TimeoutConsumer<T> implements Consumer<T> {

	private AtomicBoolean responded;
	private ScheduledFuture<?> timeoutFuture;
	private @Setter Runnable onTimeout;
	private @Setter Consumer<T> consumer;

	public TimeoutConsumer() {
		this.responded = new AtomicBoolean(false);
	}

	public void scheduleTimeout(ScheduledExecutorService executor, long ms) {
		this.timeoutFuture = executor.schedule(this::onTimeoutRun, ms, TimeUnit.MILLISECONDS);
	}

	public void onTimeoutRun() {
		if (responded.compareAndSet(false, true)) {
			onTimeout.run();
		}

	}

	@Override
	public void accept(T t) {
		if (responded.compareAndSet(false, true)) {
			if (timeoutFuture != null)
				timeoutFuture.cancel(true);
			consumer.accept(t);
		}
	}

}
