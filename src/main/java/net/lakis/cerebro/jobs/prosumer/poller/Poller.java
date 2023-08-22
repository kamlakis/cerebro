package net.lakis.cerebro.jobs.prosumer.poller;

import java.util.ArrayList;
import java.util.List;

public class Poller<T> {
	public List<T> poll(int count) throws InterruptedException {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < count; i++) {
			T obj = this.poll();
			if (obj == null)
				break;
			list.add(obj);
		}
		return list;
	}

	public T poll() throws InterruptedException {
		return null;
	}
	
	public void clear() {
		
	}
}
