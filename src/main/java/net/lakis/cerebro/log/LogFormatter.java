package net.lakis.cerebro.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {

		String message = formatMessage(record);
		if (record.getThrown() == null) {
			return String.format("[%s] %s", record.getLevel().toString(), message);
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println();
		record.getThrown().printStackTrace(pw);
		pw.close();

		return String.format("[%s] %s%n%s%n", record.getLevel().toString(), message, sw.toString());
	}
}
