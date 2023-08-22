package net.lakis.cerebro.jobs;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
	private final AtomicInteger threadNumber;
	private final String namePrefix;

	public NamedThreadFactory(String name) {
		threadNumber = new AtomicInteger(1);
		namePrefix = name + "-thread-";
	}

	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
}
