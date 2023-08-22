package net.lakis.cerebro.jobs.prosumer;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.lakis.cerebro.jobs.prosumer.config.ProsumerConfig;
import net.lakis.cerebro.jobs.prosumer.consumer.Consumer;
import net.lakis.cerebro.jobs.prosumer.consumer.units.FunctionConsumer;
import net.lakis.cerebro.jobs.prosumer.poller.Poller;
import net.lakis.cerebro.jobs.prosumer.poller.units.BlockingQueuePoller;
import net.lakis.cerebro.jobs.prosumer.poller.units.ConcurrentQueuePoller;
import net.lakis.cerebro.jobs.prosumer.poller.units.HybridQueuePoller;
import net.lakis.cerebro.jobs.prosumer.producer.Producer;
import net.lakis.cerebro.jobs.prosumer.producer.units.BlockingQueueProducer;
import net.lakis.cerebro.jobs.prosumer.producer.units.ConcurrentQueueProducer;

 
public class ProsumerFactory {
	/****************************************/
	/********** GENERAL PROSUMERS ***********/
	/****************************************/

	public static <T> Prosumer<T> createProsumer(Producer<T> producer, Poller<T> poller, Consumer<T> consumer) {
		return new Prosumer<T>(producer, poller, consumer);
	}

	public static <T> Prosumer<T> createProsumer(Producer<T> producer, Poller<T> poller,
			java.util.function.Consumer<T> consumer) {
		return createProsumer(producer, poller, FunctionConsumer.create(consumer));

	}

	public static <T> Prosumer<T> createProsumer(Poller<T> poller, Consumer<T> consumer) {
		return new Prosumer<T>(poller, consumer);
	}

	public static <T> Prosumer<T> createProsumer(Poller<T> poller, java.util.function.Consumer<T> consumer) {
		return createProsumer(poller, FunctionConsumer.create(consumer));

	}

	public static <T> Prosumer<T> createProsumer(String name, Producer<T> producer, Poller<T> poller,
			Consumer<T> consumer) {
		Prosumer<T> prosumer = createProsumer(producer, poller, consumer);
		prosumer.setWorkersName(name);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(String name, Producer<T> producer, Poller<T> poller,
			java.util.function.Consumer<T> consumer) {
		return createProsumer(name, producer, poller, FunctionConsumer.create(consumer));

	}

	public static <T> Prosumer<T> createProsumer(String name, Poller<T> poller, Consumer<T> consumer) {
		Prosumer<T> prosumer = createProsumer(poller, consumer);
		prosumer.setWorkersName(name);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(String name, Poller<T> poller,
			java.util.function.Consumer<T> consumer) {
		return createProsumer(name, poller, FunctionConsumer.create(consumer));

	}

	/****************************************/
	/********** GENERAL PROSUMERS ***********/
	/*********** MULTIPLE WORKERS ***********/
	/****************************************/

	public static <T> Prosumer<T> createProsumer(Producer<T> producer, Poller<T> poller, Consumer<T> consumer,
			int workersCount) {
		Prosumer<T> prosumer = createProsumer(producer, poller, consumer);
		prosumer.setWorkersCount(workersCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(Producer<T> producer, Poller<T> poller,
			java.util.function.Consumer<T> consumer, int workersCount) {
		return createProsumer(producer, poller, FunctionConsumer.create(consumer), workersCount);

	}

	public static <T> Prosumer<T> createProsumer(Poller<T> poller, Consumer<T> consumer, int workersCount) {
		Prosumer<T> prosumer = createProsumer(poller, consumer);
		prosumer.setWorkersCount(workersCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(Poller<T> poller, java.util.function.Consumer<T> consumer,
			int workersCount) {
		return createProsumer(poller, FunctionConsumer.create(consumer), workersCount);

	}

	public static <T> Prosumer<T> createProsumer(String name, Producer<T> producer, Poller<T> poller,
			Consumer<T> consumer, int workersCount) {
		Prosumer<T> prosumer = createProsumer(name, producer, poller, consumer);
		prosumer.setWorkersCount(workersCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(String name, Producer<T> producer, Poller<T> poller,
			java.util.function.Consumer<T> consumer, int workersCount) {
		return createProsumer(name, producer, poller, FunctionConsumer.create(consumer), workersCount);

	}

	public static <T> Prosumer<T> createProsumer(String name, Poller<T> poller, Consumer<T> consumer,
			int workersCount) {
		Prosumer<T> prosumer = createProsumer(name, poller, consumer);
		prosumer.setWorkersCount(workersCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(String name, Poller<T> poller, java.util.function.Consumer<T> consumer,
			int workersCount) {
		return createProsumer(name, poller, FunctionConsumer.create(consumer), workersCount);

	}

	/****************************************/
	/********** GENERAL PROSUMERS ***********/
	/************ SINGLE THREAD *************/
	/***************** BULK *****************/
	/****************************************/

	public static <T> Prosumer<T> createProsumer(int bulkCount, Producer<T> producer, Poller<T> poller,
			Consumer<T> consumer) {
		Prosumer<T> prosumer = createProsumer(producer, poller, consumer);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(int bulkCount, Producer<T> producer, Poller<T> poller,
			java.util.function.Consumer<T> consumer) {
		return createProsumer(bulkCount, producer, poller, FunctionConsumer.create(consumer));

	}

	public static <T> Prosumer<T> createProsumer(int bulkCount, Poller<T> poller, Consumer<T> consumer) {
		Prosumer<T> prosumer = createProsumer(poller, consumer);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(int bulkCount, Poller<T> poller,
			java.util.function.Consumer<T> consumer) {
		return createProsumer(bulkCount, poller, FunctionConsumer.create(consumer));

	}

	public static <T> Prosumer<T> createProsumer(String name, int bulkCount, Producer<T> producer, Poller<T> poller,
			Consumer<T> consumer) {
		Prosumer<T> prosumer = createProsumer(name, producer, poller, consumer);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(String name, int bulkCount, Producer<T> producer, Poller<T> poller,
			java.util.function.Consumer<T> consumer) {
		return createProsumer(name, bulkCount, producer, poller, FunctionConsumer.create(consumer));

	}

	public static <T> Prosumer<T> createProsumer(String name, int bulkCount, Poller<T> poller, Consumer<T> consumer) {
		Prosumer<T> prosumer = createProsumer(name, poller, consumer);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(String name, int bulkCount, Poller<T> poller,
			java.util.function.Consumer<T> consumer) {
		return createProsumer(name, bulkCount, poller, FunctionConsumer.create(consumer));

	}

	/****************************************/
	/********** GENERAL PROSUMERS ***********/
	/*********** MULTIPLE WORKERS ***********/
	/***************** BULK *****************/
	/****************************************/

	public static <T> Prosumer<T> createProsumer(int bulkCount, Producer<T> producer, Poller<T> poller,
			Consumer<T> consumer, int workersCount) {
		Prosumer<T> prosumer = createProsumer(producer, poller, consumer, workersCount);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(int bulkCount, Producer<T> producer, Poller<T> poller,
			java.util.function.Consumer<T> consumer, int workersCount) {
		return createProsumer(bulkCount, producer, poller, FunctionConsumer.create(consumer), workersCount);

	}

	public static <T> Prosumer<T> createProsumer(int bulkCount, Poller<T> poller, Consumer<T> consumer,
			int workersCount) {
		Prosumer<T> prosumer = createProsumer(poller, consumer, workersCount);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(int bulkCount, Poller<T> poller,
			java.util.function.Consumer<T> consumer, int workersCount) {
		return createProsumer(bulkCount, poller, FunctionConsumer.create(consumer), workersCount);

	}

	public static <T> Prosumer<T> createProsumer(String name, int bulkCount, Producer<T> producer, Poller<T> poller,
			Consumer<T> consumer, int workersCount) {
		Prosumer<T> prosumer = createProsumer(name, producer, poller, consumer, workersCount);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(String name, int bulkCount, Producer<T> producer, Poller<T> poller,
			java.util.function.Consumer<T> consumer, int workersCount) {
		return createProsumer(name, bulkCount, producer, poller, FunctionConsumer.create(consumer), workersCount);

	}

	public static <T> Prosumer<T> createProsumer(String name, int bulkCount, Poller<T> poller, Consumer<T> consumer,
			int workersCount) {
		Prosumer<T> prosumer = createProsumer(name, poller, consumer, workersCount);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createProsumer(String name, int bulkCount, Poller<T> poller,
			java.util.function.Consumer<T> consumer, int workersCount) {
		return createProsumer(name, bulkCount, poller, FunctionConsumer.create(consumer), workersCount);

	}

	/****************************************/
	/*********** CONFIG PROSUMERS ***********/
	/****************************************/

	public static <T> Prosumer<T> createProsumer(ProsumerConfig config, Consumer<T> consumer) {
		if (config.getSleepTime() > 0) { // CONCURENT
			return createConcurentProsumer(config.getName(), config.getBulkCount(), consumer, config.getSleepTime(),
					config.getWorkersCount());
		} else if (config.isWaitForAll()) {// BLOCKING
			return createBlockingProsumer(config.getName(), config.getBulkCount(), consumer, config.getWorkersCount());
		} else {// HYBRID
			return createHybridProsumer(config.getName(), config.getBulkCount(), consumer, config.getWorkersCount());
		}
	}

	public static <T> Prosumer<T> createProsumer(ProsumerConfig config, java.util.function.Consumer<T> consumer) {
		return createProsumer(config, FunctionConsumer.create(consumer));

	}

	/****************************************/
	/********* CONCURENT PROSUMERS **********/
	/************ SINGLE THREAD *************/
	/****************************************/

	public static <T> Prosumer<T> createConcurentProsumer(Consumer<T> consumer, int sleepTime) {
		ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<T>();
		Producer<T> producer = new ConcurrentQueueProducer<T>(queue);
		Poller<T> poller = new ConcurrentQueuePoller<T>(queue, sleepTime);
		return createProsumer(producer, poller, consumer);
	}

	public static <T> Prosumer<T> createConcurentProsumer(java.util.function.Consumer<T> consumer, int sleepTime) {
		return createConcurentProsumer(FunctionConsumer.create(consumer), sleepTime);

	}

	public static <T> Prosumer<T> createConcurentProsumer(String name, Consumer<T> consumer, int sleepTime) {
		Prosumer<T> prosumer = createConcurentProsumer(consumer, sleepTime);
		prosumer.setWorkersName(name);
		return prosumer;
	}

	public static <T> Prosumer<T> createConcurentProsumer(String name, java.util.function.Consumer<T> consumer,
			int sleepTime) {
		return createConcurentProsumer(name, FunctionConsumer.create(consumer), sleepTime);

	}

	/****************************************/
	/********* CONCURENT PROSUMERS **********/
	/*********** MULTIPLE WORKERS ***********/
	/****************************************/

	public static <T> Prosumer<T> createConcurentProsumer(Consumer<T> consumer, int sleepTime, int workersCount) {
		Prosumer<T> prosumer = createConcurentProsumer(consumer, sleepTime);
		prosumer.setWorkersCount(workersCount);
		return prosumer;

	}

	public static <T> Prosumer<T> createConcurentProsumer(java.util.function.Consumer<T> consumer, int sleepTime,
			int workersCount) {
		return createConcurentProsumer(FunctionConsumer.create(consumer), sleepTime, workersCount);

	}

	public static <T> Prosumer<T> createConcurentProsumer(String name, Consumer<T> consumer, int sleepTime,
			int workersCount) {
		Prosumer<T> prosumer = createConcurentProsumer(name, consumer, sleepTime);
		prosumer.setWorkersCount(workersCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createConcurentProsumer(String name, java.util.function.Consumer<T> consumer,
			int sleepTime, int workersCount) {
		return createConcurentProsumer(name, FunctionConsumer.create(consumer), sleepTime, workersCount);

	}

	/****************************************/
	/********* CONCURENT PROSUMERS **********/
	/************ SINGLE THREAD *************/
	/***************** BULK *****************/
	/****************************************/

	public static <T> Prosumer<T> createConcurentProsumer(int bulkCount, Consumer<T> consumer, int sleepTime) {
		Prosumer<T> prosumer = createConcurentProsumer(consumer, sleepTime);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createConcurentProsumer(int bulkCount, java.util.function.Consumer<T> consumer,
			int sleepTime) {
		return createConcurentProsumer(bulkCount, FunctionConsumer.create(consumer), sleepTime);

	}

	public static <T> Prosumer<T> createConcurentProsumer(String name, int bulkCount, Consumer<T> consumer,
			int sleepTime) {
		Prosumer<T> prosumer = createConcurentProsumer(name, consumer, sleepTime);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createConcurentProsumer(String name, int bulkCount,
			java.util.function.Consumer<T> consumer, int sleepTime) {
		return createConcurentProsumer(name, bulkCount, FunctionConsumer.create(consumer), sleepTime);

	}

	/****************************************/
	/********* CONCURENT PROSUMERS **********/
	/*********** MULTIPLE WORKERS ***********/
	/***************** BULK *****************/
	/****************************************/

	public static <T> Prosumer<T> createConcurentProsumer(int bulkCount, Consumer<T> consumer, int sleepTime,
			int workersCount) {
		Prosumer<T> prosumer = createConcurentProsumer(consumer, sleepTime, workersCount);
		prosumer.setBulkCount(bulkCount);
		return prosumer;

	}

	public static <T> Prosumer<T> createConcurentProsumer(int bulkCount, java.util.function.Consumer<T> consumer,
			int sleepTime,int workersCount) {
		return createConcurentProsumer(bulkCount, FunctionConsumer.create(consumer), sleepTime, workersCount);

	}
	public static <T> Prosumer<T> createConcurentProsumer(String name, int bulkCount, Consumer<T> consumer,
			int sleepTime, int workersCount) {
		Prosumer<T> prosumer = createConcurentProsumer(name, consumer, sleepTime, workersCount);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}
	
	public static <T> Prosumer<T> createConcurentProsumer(String name, int bulkCount,
			java.util.function.Consumer<T> consumer, int sleepTime,int workersCount) {
		return createConcurentProsumer(name, bulkCount, FunctionConsumer.create(consumer), sleepTime, workersCount);

	}


	/****************************************/
	/********** BLOCKING PROSUMERS **********/
	/************ SINGLE THREAD *************/
	/****************************************/

	public static <T> Prosumer<T> createBlockingProsumer(Consumer<T> consumer) {
		LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>();
		Producer<T> producer = new BlockingQueueProducer<T>(queue);
		Poller<T> poller = new BlockingQueuePoller<T>(queue);
		return createProsumer(producer, poller, consumer);
	}

	public static <T> Prosumer<T> createBlockingProsumer(java.util.function.Consumer<T> consumer) {
		return createBlockingProsumer(FunctionConsumer.create(consumer));

	}

	public static <T> Prosumer<T> createBlockingProsumer(String name, Consumer<T> consumer) {
		Prosumer<T> prosumer = createBlockingProsumer(consumer);
		prosumer.setWorkersName(name);
		return prosumer;
	}

	public static <T> Prosumer<T> createBlockingProsumer(String name, java.util.function.Consumer<T> consumer) {
		return createBlockingProsumer(name, FunctionConsumer.create(consumer));
	}

	/****************************************/
	/********** BLOCKING PROSUMERS **********/
	/*********** MULTIPLE WORKERS ***********/
	/****************************************/

	public static <T> Prosumer<T> createBlockingProsumer(Consumer<T> consumer, int workersCount) {
		Prosumer<T> prosumer = createBlockingProsumer(consumer);
		prosumer.setWorkersCount(workersCount);
		return prosumer;

	}

	public static <T> Prosumer<T> createBlockingProsumer(java.util.function.Consumer<T> consumer, int workersCount) {
		return createBlockingProsumer(FunctionConsumer.create(consumer), workersCount);

	}

	public static <T> Prosumer<T> createBlockingProsumer(String name, Consumer<T> consumer, int workersCount) {
		Prosumer<T> prosumer = createBlockingProsumer(name, consumer);
		prosumer.setWorkersCount(workersCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createBlockingProsumer(String name, java.util.function.Consumer<T> consumer,
			int workersCount) {
		return createBlockingProsumer(name, FunctionConsumer.create(consumer), workersCount);

	}

	/****************************************/
	/********** BLOCKING PROSUMERS **********/
	/************ SINGLE THREAD *************/
	/***************** BULK *****************/
	/****************************************/

	public static <T> Prosumer<T> createBlockingProsumer(int bulkCount, Consumer<T> consumer) {
		Prosumer<T> prosumer = createBlockingProsumer(consumer);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createBlockingProsumer(int bulkCount, java.util.function.Consumer<T> consumer) {
		return createBlockingProsumer(bulkCount, FunctionConsumer.create(consumer));

	}

	public static <T> Prosumer<T> createBlockingProsumer(String name, int bulkCount, Consumer<T> consumer) {
		Prosumer<T> prosumer = createBlockingProsumer(name, consumer);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createBlockingProsumer(String name, int bulkCount,
			java.util.function.Consumer<T> consumer) {
		return createBlockingProsumer(name, bulkCount, FunctionConsumer.create(consumer));

	}

	/****************************************/
	/********** BLOCKING PROSUMERS **********/
	/*********** MULTIPLE WORKERS ***********/
	/***************** BULK *****************/
	/****************************************/

	public static <T> Prosumer<T> createBlockingProsumer(int bulkCount, Consumer<T> consumer, int workersCount) {
		Prosumer<T> prosumer = createBlockingProsumer(consumer, workersCount);
		prosumer.setBulkCount(bulkCount);
		return prosumer;

	}

	public static <T> Prosumer<T> createBlockingProsumer(int bulkCount, java.util.function.Consumer<T> consumer,
			int workersCount) {
		return createBlockingProsumer(bulkCount, FunctionConsumer.create(consumer), workersCount);

	}

	public static <T> Prosumer<T> createBlockingProsumer(String name, int bulkCount, Consumer<T> consumer,
			int workersCount) {
		Prosumer<T> prosumer = createBlockingProsumer(name, consumer, workersCount);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createBlockingProsumer(String name, int bulkCount,
			java.util.function.Consumer<T> consumer, int workersCount) {
		return createBlockingProsumer(name, bulkCount, FunctionConsumer.create(consumer), workersCount);

	}

	/****************************************/
	/*********** HYBRID PROSUMERS ***********/
	/************ SINGLE THREAD *************/
	/***************** BULK *****************/
	/****************************************/

	public static <T> Prosumer<T> createHybridProsumer(int bulkCount, Consumer<T> consumer) {
		LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>();
		Producer<T> producer = new BlockingQueueProducer<T>(queue);
		Poller<T> poller = new HybridQueuePoller<T>(queue);
		Prosumer<T> prosumer = createProsumer(producer, poller, consumer);
		prosumer.setBulkCount(bulkCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createHybridProsumer(int bulkCount, java.util.function.Consumer<T> consumer) {
		return createHybridProsumer(bulkCount, FunctionConsumer.create(consumer));

	}

	public static <T> Prosumer<T> createHybridProsumer(String name, int bulkCount, Consumer<T> consumer) {
		Prosumer<T> prosumer = createHybridProsumer(bulkCount, consumer);
		prosumer.setWorkersName(name);
		return prosumer;
	}

	public static <T> Prosumer<T> createHybridProsumer(String name, int bulkCount,
			java.util.function.Consumer<T> consumer) {
		return createHybridProsumer(name, bulkCount, FunctionConsumer.create(consumer));

	}

	/****************************************/
	/*********** HYBRID PROSUMERS ***********/
	/*********** MULTIPLE WORKERS ***********/
	/***************** BULK *****************/
	/****************************************/

	public static <T> Prosumer<T> createHybridProsumer(int bulkCount, Consumer<T> consumer, int workersCount) {
		Prosumer<T> prosumer = createHybridProsumer(bulkCount, consumer);
		prosumer.setWorkersCount(workersCount);
		return prosumer;

	}

	public static <T> Prosumer<T> createHybridProsumer(int bulkCount, java.util.function.Consumer<T> consumer,
			int workersCount) {
		return createHybridProsumer(bulkCount, FunctionConsumer.create(consumer), workersCount);

	}

	public static <T> Prosumer<T> createHybridProsumer(String name, int bulkCount, Consumer<T> consumer,
			int workersCount) {
		Prosumer<T> prosumer = createHybridProsumer(name, bulkCount, consumer);
		prosumer.setWorkersCount(workersCount);
		return prosumer;
	}

	public static <T> Prosumer<T> createHybridProsumer(String name, int bulkCount,
			java.util.function.Consumer<T> consumer, int workersCount) {
		return createHybridProsumer(name, bulkCount, FunctionConsumer.create(consumer), workersCount);

	}

}