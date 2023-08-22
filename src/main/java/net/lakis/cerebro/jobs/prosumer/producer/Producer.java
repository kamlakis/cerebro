package net.lakis.cerebro.jobs.prosumer.producer;

import java.util.Collection;

public class Producer<T> {

	public void handle(T[] objects) {
		for (T object : objects) {
			this.handle(object);
		}
	}

	public void handle(Collection<T> objects) {
		for (T object : objects) {
			this.handle(object);
		}
	}

	public void handle(T object) {

	}
	
	public int pendingJobs() {
		return 0;
	}
}
