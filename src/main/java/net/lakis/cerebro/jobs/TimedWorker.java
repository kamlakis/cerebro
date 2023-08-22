package net.lakis.cerebro.jobs;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class TimedWorker {
	private Timer timer;
	private @Getter @Setter String name;
	private @Getter @Setter long maxDelay;
	private @Getter @Setter long firstTime;
	private @Getter @Setter long period;

	/****************************************/
	/************* ONE TIME RUN *************/
	/****************************************/

	public TimedWorker(long firstTime) {
		this(null, firstTime, 0, 0);
	}

	public TimedWorker(Date firstTime) {
		this(firstTime.getTime() - System.currentTimeMillis());
	}

	public TimedWorker(String name, long firstTime) {
		this(name, firstTime, 0, 0);
	}

	public TimedWorker(String name, Date firstTime) {
		this(name, firstTime.getTime() - System.currentTimeMillis());
	}

	/****************************************/
	/********** MULTIPLE TIMES RUN **********/
	/****************************************/

	public TimedWorker(long firstTime, long period) {
		this(null, firstTime, period, 0);
	}

	public TimedWorker(Date firstTime, long period) {
		this(firstTime.getTime() - System.currentTimeMillis(), period);
	}

	public TimedWorker(String name, long firstTime, long period) {
		this(name, firstTime, period, 0);
	}

	public TimedWorker(String name, Date firstTime, long period) {
		this(name, firstTime.getTime() - System.currentTimeMillis(), period);
	}

	/****************************************/
	/********** MULTIPLE TIMES RUN **********/
	/*** Skip this execution if too late ****/
	/****************************************/

	public TimedWorker(long firstTime, long period, long maxDelay) {
		this(null, firstTime, period, maxDelay);
	}

	public TimedWorker(Date firstTime, long period, long maxDelay) {
		this(firstTime.getTime() - System.currentTimeMillis(), period, maxDelay);
	}

	public TimedWorker(String name, Date firstTime, long period, long maxDelay) {
		this(name, firstTime.getTime() - System.currentTimeMillis(), period, maxDelay);
	}

	public TimedWorker(String name, long firstTime, long period, long maxDelay) {
		this.name = name;
		this.firstTime = firstTime;
		this.period = period;
		this.maxDelay = maxDelay;
	}

	/****************************************/
	/*************** CONTROL ****************/
	/****************************************/

	public void start() {
		if (timer != null)
			this.stop();
		if (StringUtils.isBlank(name))
			this.timer = new Timer();
		else
			this.timer = new Timer(name);

		TimerTask timerTask = new TimerTask() {
			public void run() {
				try {
					// Skip this execution if too late.
					if (maxDelay <= 0 || (System.currentTimeMillis() - scheduledExecutionTime()) < maxDelay) {
						TimedWorker.this.work();
					}
					// Single run
					if (period == 0)
						TimedWorker.this.stop();
				} catch (Exception e) {
					log.error("Exception: ", e);
				}
			}
		};
		// Single run
		if(period == 0)
			this.timer.schedule(timerTask, firstTime);
		else
			this.timer.scheduleAtFixedRate(timerTask, firstTime, period);
	}

	public void stop() {
		if (timer == null)
			return;
		this.timer.cancel();
		this.timer = null;
	}
	
	public boolean isDone() {
		return this.timer == null;
	}

	public abstract void work() throws Exception;

	 

}
