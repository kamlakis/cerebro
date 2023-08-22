package net.lakis.cerebro.jobs.prosumer.poller.units;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.Getter;
import net.lakis.cerebro.jobs.prosumer.poller.Poller;
 
public class HybridQueuePoller<T> extends Poller<T> {

	private @Getter LinkedBlockingQueue<T> queue;

	public HybridQueuePoller(LinkedBlockingQueue<T> queue) {
		this.queue = queue;
	}

	@Override
	public List<T> poll(int count) throws InterruptedException {
		T item = poll();
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
		return queue.take();
	}

	public void clear() {
		queue.clear();
	}
}