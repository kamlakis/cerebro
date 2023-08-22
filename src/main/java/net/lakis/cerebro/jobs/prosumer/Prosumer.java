package net.lakis.cerebro.jobs.prosumer;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.Setter;
import net.lakis.cerebro.jobs.Worker;
import net.lakis.cerebro.jobs.prosumer.consumer.Consumer;
import net.lakis.cerebro.jobs.prosumer.poller.Poller;
import net.lakis.cerebro.jobs.prosumer.producer.Producer;
 
@Getter
@Setter
@SuppressWarnings("rawtypes")
public class Prosumer<T> {
	private Producer<T> producer;
	private Poller<T> poller;
	private Consumer<T> consumer;
	private ConsumerWorker[] workers;

	private String workersName;
	private int workersCount = 1;
	private int bulkCount = 1;

	public Prosumer(Producer<T> producer, Poller<T> poller, Consumer<T> consumer) {
		this.producer = producer;
		this.poller = poller;
		this.consumer = consumer;
	}

	public Prosumer(Poller<T> poller, Consumer<T> consumer) {
		this(null, poller, consumer);
	}

	public boolean startWorkers() {
		try {
			return startWorkers(0);
		} catch (InterruptedException e) {
			return false; // won't reach this
		}
	}

	public boolean startWorkers(int sleep) throws InterruptedException {
		if (workers != null) {
			boolean ret = false;
			for (Worker worker : workers) {
				if (worker.start()) {
					ret = true;
				}
			}
			return ret;
		}

//		if (StringUtils.isBlank(workersName))
//			workersName = "Prosumer-" + System.currentTimeMillis();
		workers = new ConsumerWorker[workersCount];
		for (int i = 0; i < workers.length; i++) {
			ConsumerWorker w = new ConsumerWorker<T>(StringUtils.isBlank(workersName) ? null : workersName + "-" + i, //
					poller, consumer, bulkCount);

			if (workersCount > 0 && sleep > 0) {
				Thread.sleep(sleep);
			}
			w.start();
			workers[i] = w;
		}
		return true;
	}

	public boolean stopWorkers() {
		if (workers == null)
			return false;
		for (int i = 0; i < workers.length; i++) {
			workers[i].stop();
		}
		workers = null;
		return true;
	}

	public boolean isRunning() {
		if (workers != null) {
			for (Worker worker : workers) {
				if (worker.isRunning()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean handle(T[] objects) {
		if (producer == null)
			return false;
		producer.handle(objects);
		return true;

	}

	public boolean handle(Collection<T> objects) {
		if (producer == null)
			return false;
		producer.handle(objects);
		return true;
	}

	public boolean handle(T object) {
		if (producer == null)
			return false;
		producer.handle(object);
		return true;
	}

	public boolean handleIfRunning(T[] objects) {
		if (producer == null && !this.isRunning())
			return false;

		producer.handle(objects);
		return true;
	}

	public boolean handleIfRunning(Collection<T> objects) {
		if (producer == null && !this.isRunning())
			return false;

		producer.handle(objects);
		return true;

	}

	public boolean handleIfRunning(T object) {
		if (producer == null && !this.isRunning())
			return false;

		producer.handle(object);
		return true;
	}

	public int pendingJobs() {
		if (producer != null)
			return producer.pendingJobs();
		return 0;
	}
	
	public int activeWorkers() {
		int  i= 0;
		for(ConsumerWorker worker : workers) {
			if(worker.isWorking())
				i++;
		}
		return i;
	}

	public void clear() {
		if (poller != null)
			poller.clear();
	}
}
