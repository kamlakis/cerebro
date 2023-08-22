package net.lakis.cerebro.jobs.prosumer.consumer.units;

import java.util.Arrays;

import net.lakis.cerebro.jobs.prosumer.consumer.Consumer;

 
public abstract class BulkConsumer<T>  extends Consumer<T> {
	public void handle(T object) throws Exception {
		this.handle(Arrays.asList(object));
	}
}
