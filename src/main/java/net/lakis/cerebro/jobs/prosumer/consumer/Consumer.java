package net.lakis.cerebro.jobs.prosumer.consumer;

import java.util.List;

public abstract class Consumer<T> {
	public abstract void handle(List<T> objects) throws Exception;

	public abstract void handle(T object) throws Exception;
	
	protected void stopWorker() throws InterruptedException {
		throw new InterruptedException("stopWorker");
	}
}
