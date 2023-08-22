package net.lakis.cerebro.jobs.prosumer;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.lakis.cerebro.jobs.Worker;
import net.lakis.cerebro.jobs.prosumer.consumer.Consumer;
import net.lakis.cerebro.jobs.prosumer.poller.Poller;
 
@Getter
@Setter
public class ConsumerWorker<T> extends Worker {
	private Poller<T> poller;
	private Consumer<T> consumer;
	private int bulkCount;
	private boolean working;

	public ConsumerWorker(String name, Poller<T> poller, Consumer<T> consumer, int bulkCount) {
		super(name);
		this.poller = poller;
		this.consumer = consumer;
		this.bulkCount = bulkCount;
	}

	@Override
	public void work() throws Exception {
		if (bulkCount > 1)
			this.handleMultiple();
		else
			this.handleOne();

	}

	private void handleOne() throws Exception {
		T object = this.poller.poll();
		if (object != null) {
			try {
				this.working = true;
				this.consumer.handle(object);

			} finally {
				this.working = false;
			}

		}
	}

	private void handleMultiple() throws Exception {
		List<T> objects = this.poller.poll(bulkCount);
		if (objects != null && objects.size() > 0) {
			try {
				this.working = true;
				this.consumer.handle(objects);
			} finally {
				this.working = false;
			}

		}
	}

}
