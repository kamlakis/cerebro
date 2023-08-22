package net.lakis.cerebro.jobs.prosumer.poller.units;

import java.util.concurrent.LinkedBlockingQueue;

import lombok.Getter;
import net.lakis.cerebro.jobs.prosumer.poller.Poller;
 
public class BlockingQueuePoller<T> extends Poller<T> {

	private @Getter LinkedBlockingQueue<T> queue;

	public BlockingQueuePoller(LinkedBlockingQueue<T> queue) {
		this.queue = queue;
	}

	@Override
	public T poll() throws InterruptedException {
		return queue.take();
	}
	
	public void clear() {
		queue.clear();
	}
}