package net.lakis.cerebro.jobs.prosumer.producer.units;

import java.util.concurrent.LinkedBlockingQueue;

import lombok.Getter;
import net.lakis.cerebro.jobs.prosumer.producer.Producer;

public class BlockingQueueProducer<T> extends Producer<T> {

	private @Getter LinkedBlockingQueue<T> queue;

	public BlockingQueueProducer(LinkedBlockingQueue<T> queue) {
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
