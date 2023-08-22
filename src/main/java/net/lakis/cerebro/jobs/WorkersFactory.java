package net.lakis.cerebro.jobs;

public class WorkersFactory {

	public static Worker createWorker(IWorker worker) {
		return new FactoryWorker(worker);

	}

	public static Workers createWorker(IWorker worker, int workersCount) {
		return new FactoryWorkers(worker, workersCount);

	}

	private static class FactoryWorker extends Worker {
		private IWorker worker;

		public FactoryWorker(IWorker worker) {
			this.worker = worker;
		}

		@Override
		public void work() throws Exception {
			this.worker.work();
		}

	}

	private static class FactoryWorkers extends Workers {
		private IWorker worker;

		public FactoryWorkers(IWorker worker, int workersCount) {
			super(workersCount);
			this.worker = worker;
		}

		@Override
		public void work() throws Exception {
			this.worker.work();
		}

	}
}
