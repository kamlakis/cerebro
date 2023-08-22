package net.lakis.cerebro.jobs.async;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import net.lakis.cerebro.jobs.NamedThreadFactory;

 
public class AsyncExecutor<T> {
	private String name;
	private int threadsCount;
	private long defaultTimeout;

	private Map<String, AsyncHolder<T>> map;
	private ScheduledThreadPoolExecutor executor;

	public AsyncExecutor(String name) {
		this(name, 1, -1);
	}

	public AsyncExecutor() {
		this(null, 1, -1);
	}

	public AsyncExecutor(String name, int defaultTimeout) {
		this(name, 1, defaultTimeout);
	}

	public AsyncExecutor(int defaultTimeout) {
		this(null, 1, defaultTimeout);
	}

	public AsyncExecutor(int threadsCount, int defaultTimeout) {
		this(null, threadsCount, defaultTimeout);
	}

	public AsyncExecutor(String name, int threadsCount, int defaultTimeout) {
		this.name = name;
		this.defaultTimeout = defaultTimeout;
		this.threadsCount = threadsCount;
		this.map = Collections.synchronizedMap(new HashMap<String, AsyncHolder<T>>());
	}

	public Future<T> execute(Callable<T> callable) {
		return this.execute(callable, 0);
	}

	public Future<T> execute(Callable<T> callable, long ms) {
		if (this.executor == null)
			throw new IllegalStateException("AsyncExecutor is not started");
		return this.executor.schedule(new AsyncScheduleCallable<T>(callable), ms, TimeUnit.MILLISECONDS);
	}

	public void execute(Runnable runnable, long ms) {
		if (this.executor == null)
			throw new IllegalStateException("AsyncExecutor is not started");
		this.executor.schedule(new AsyncScheduleRunnable(runnable), ms, TimeUnit.MILLISECONDS);
	}

	public void execute(Runnable runnable) {
		if (this.executor == null)
			throw new IllegalStateException("AsyncExecutor is not started");
		this.executor.execute(new AsyncRunnable(runnable));
	}

	public Future<T> schedule(String id) {
		AsyncHolder<T> holder = new AsyncHolder<T>(this, id);
		this.map.put(id, holder);
		return new AsyncFuture<T>(this, holder);
	}

	public Future<T> schedule(long id) {
		return this.schedule(String.valueOf(id));
	}

	public void schedule(String id, AsyncResponseHandler<T> handler) {
		this.schedule(id, handler, defaultTimeout);
	}

	public void schedule(long id, AsyncResponseHandler<T> handler) {
		this.schedule(String.valueOf(id), handler);
	}

	public AsyncHolder<T> schedule(String id, AsyncResponseHandler<T> handler, long timeout) {
		if (this.executor == null)
			throw new IllegalStateException("AsyncExecutor is not started");
		AsyncHolder<T> holder = new AsyncHolder<T>(this, id);
		holder.setResponseHandler(handler);
		this.map.put(id, holder);

		if (timeout > 0) {
			Future<?> timeoutFuture = this.executor.schedule(new AsyncTimeoutRunnable(holder), timeout,
					TimeUnit.MILLISECONDS);
			holder.setTimeoutFuture(timeoutFuture);
		}
		return holder;
	}

	public void schedule(long id, AsyncResponseHandler<T> handler, long timeout) {
		this.schedule(String.valueOf(id), handler, timeout);
	}

	public void responded(long id, T response) {
		this.responded(String.valueOf(id), response);
	}

	public void responded(String id, T response) {
		AsyncHolder<T> holder = this.map.remove(id);
		this.responded(holder, response);
	}

	public void responded(AsyncHolder<T> holder, T response) {

		if (holder == null)
			return;
		holder.setResponded(true);
		holder.setResponse(response);
		synchronized (holder) {
			holder.notifyAll();
		}
		if (this.executor != null) {
			Future<?> timeoutFuture = holder.getTimeoutFuture();
			if (timeoutFuture != null)
				timeoutFuture.cancel(true);
			if (holder.getResponseHandler() != null)
				this.executor.execute(holder::responded);
		}
	}

	public void start() {
		if (StringUtils.isBlank(name))
			this.executor = new ScheduledThreadPoolExecutor(threadsCount);
		else
			this.executor = new ScheduledThreadPoolExecutor(threadsCount, new NamedThreadFactory(name));
	}

	public void stop() {
		if (this.executor == null)
			return;
		List<Runnable> list = this.executor.shutdownNow();
		for (Runnable runnable : list) {
			runnable.run();
		}
		this.executor = null;
	}

	AsyncHolder<T> remove(String id) {
		return this.map.remove(id);
	}

	AsyncHolder<T> get(String id) {
		return this.map.get(id);
	}

	public void clear() {
		this.map.clear();
	}

	public int size() {
		return this.map.size();
	}

	public int pendingJobs() {
		return this.executor.getQueue().size();
	}

	public int getActiveCount() {
		return this.executor.getActiveCount();
	}

	public void shutdown() {
		this.executor.shutdownNow();
		this.map.clear();
	}

	public boolean isShutdown() {
		return this.executor.isShutdown();
	}
}
