package net.lakis.cerebro.jobs.prosumer.consumer.units;

import java.util.List;

import net.lakis.cerebro.jobs.prosumer.consumer.Consumer;

public abstract class SingleConsumer<T> extends Consumer<T> {
	public void handle(List<T> objects) throws Exception {
		for (T object : objects) {
			this.handle(object);
		}
	}
}
