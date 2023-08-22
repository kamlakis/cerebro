package net.lakis.cerebro.jobs.prosumer.producer.units;

import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.Getter;
import net.lakis.cerebro.jobs.prosumer.producer.Producer;

public class ConcurrentQueueProducer<T> extends Producer<T> {

	private @Getter ConcurrentLinkedQueue<T> queue;

	public ConcurrentQueueProducer(ConcurrentLinkedQueue<T> queue) {
		this.queue = queue;
	}

	@Override
	public void handle(T object) {
		this.queue.offer(object);
	}
	
	@Override
	public int pendingJobs() {
		return this.queue.size();
	}

}
