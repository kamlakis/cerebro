package net.lakis.cerebro.jobs.prosumer.consumer.units;

import java.util.List;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.lakis.cerebro.jobs.prosumer.consumer.Consumer;

@Setter
@Accessors(chain = true, fluent = true)
public class FunctionConsumer<T> extends Consumer<T> {

	java.util.function.Consumer<T> consumer;

	public static <J> FunctionConsumer<J> create(java.util.function.Consumer<J> consumer) {
		return new FunctionConsumer<J>().consumer(consumer);

	}

	public void handle(List<T> objects) throws Exception {
		for (T object : objects) {
			this.handle(object);
		}
	}

	@Override
	public void handle(T object) throws Exception {
		consumer.accept(object);
	}
}
