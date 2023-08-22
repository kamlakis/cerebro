package net.lakis.cerebro.jobs;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Workers implements IWorker {
	private Worker[] workers;

	private String name;
	private int workersCount;

	public Workers(int workersCount) {
		this(null, workersCount);
	}

	public Workers(String name, int workersCount) {
		this.name = name;
		this.workersCount = workersCount;
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

		workers = new Worker[workersCount];
		for (int i = 0; i < workers.length; i++) {

			Worker w = this.createWorker(StringUtils.isBlank(name) ? null : name + "-" + i);

			if (workersCount > 0 && sleep > 0) {
				Thread.sleep(sleep);
			}
			w.start();
			workers[i] = w;
		}
		return true;
	}

	protected Worker createWorker(String name) {
		return new Worker(name) {
			@Override
			public void work() throws Exception {
				Workers.this.work();
			}
		};
	}

	public boolean stopWorkers() {
		if (workers == null)
			return false;
		for (int i = 0; i < workers.length; i++) {
			if (workers[i] != null)
				workers[i].stop();
		}
		workers = null;
		return true;
	}


	public boolean isRunning() {
		return this.workers != null;
	}
}
