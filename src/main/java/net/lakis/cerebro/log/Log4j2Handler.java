package net.lakis.cerebro.log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import lombok.extern.log4j.Log4j2;
import net.lakis.cerebro.annotations.Service;

@Service
@Log4j2
public class Log4j2Handler extends Handler {
	// private SimpleFormatter formatter = new SimpleFormatter();
	private LogFormatter formatter = new LogFormatter();

	public Log4j2Handler() {
		LogManager.getLogManager().reset();

		Logger.getGlobal().addHandler(this);
		Logger.getLogger("").addHandler(this);
	}

	@Override
	public void publish(LogRecord record) {
		String msg = formatter.format(record);
		if (record.getLevel().equals(Level.SEVERE))
			log.error(msg);
		else if (record.getLevel().equals(Level.WARNING))
			log.warn(msg);

		else if (record.getLevel().equals(Level.INFO))
			log.info(msg);

		else
			log.debug(msg);

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws SecurityException {
		// TODO Auto-generated method stub

	}

}
