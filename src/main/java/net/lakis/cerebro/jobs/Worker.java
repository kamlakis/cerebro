package net.lakis.cerebro.jobs;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class Worker implements Runnable, IWorker {
	private @Getter String name;
	private Thread thread;

	public Worker() {
		this(null);
	}

	public Worker(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		while (isRunning() && !Thread.currentThread().isInterrupted()) {
			try {
				this.work();
			} catch (InterruptedException e) {
				log.warn("InterruptedException on {}, will stop thread", this.getName());
				this.thread = null;
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				log.error("Exception on " + this.getName() + ": ", e);
			}
		}
	}


	public boolean start() {
		if (isRunning())
			return false;
		if (StringUtils.isBlank(name))
			thread = new Thread(this);
		else
			thread = new Thread(null, this, name, 0);
		if (thread.isDaemon())
			thread.setDaemon(false);
		if (thread.getPriority() != Thread.NORM_PRIORITY)
			thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();
		return true;
	}

	public boolean stop() {
		if (!isRunning())
			return false;

		thread.interrupt();
		thread = null;
		return true;
	}

	public boolean isRunning() {
		return this.thread != null;
	}
}
