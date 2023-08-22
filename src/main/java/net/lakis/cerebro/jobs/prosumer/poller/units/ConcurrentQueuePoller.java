package net.lakis.cerebro.jobs.prosumer.poller.units;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.Getter;
import net.lakis.cerebro.jobs.prosumer.poller.Poller;
 
public class ConcurrentQueuePoller<T> extends Poller<T> {

	private @Getter ConcurrentLinkedQueue<T> queue;
	private @Getter int sleepTime;

	public ConcurrentQueuePoller(ConcurrentLinkedQueue<T> queue, int sleepTime) {
		this.queue = queue;
		this.sleepTime = sleepTime;
	}

	@Override
	public List<T> poll(int count) throws InterruptedException {
		T item = poll();
		if (item == null)
			return null;

		List<T> itemList = new ArrayList<T>();
		itemList.add(item);
		for (int i = 1; i < count; i++) {
			item = queue.poll();
			if (item == null)
				break;
			itemList.add(item);
		}
		return itemList;
	}

	@Override
	public T poll() throws InterruptedException {
		T item = queue.poll();
		if (item == null)
			Thread.sleep(sleepTime);
		return item;
	}

	public void clear() {
		queue.clear();
	}
}
