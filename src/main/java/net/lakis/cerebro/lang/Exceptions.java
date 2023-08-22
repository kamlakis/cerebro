package net.lakis.cerebro.lang;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;

public class Exceptions {
	public static void log(Logger log, Exception e) {
		try {
			log.error("Exception", ExceptionUtils.getFullStackTrace(e));
		} catch (Exception e2) {
		}
	}

//	   
//	public static List<Throwable> getThrowableList(Throwable throwable) {
//		List<Throwable> list = new ArrayList<Throwable>();
//		while (throwable != null && list.contains(throwable) == false) {
//			list.add(throwable);
//			throwable = ExceptionUtils.getCause(throwable);
//		}
//		return list;
//	}
//
//	public static String getFullStackTrace(Throwable throwable) {
//		StringBuilder sb = new StringBuilder();
//		List<Throwable> list = getThrowableList(throwable);
//		for (int i = 0; i < 5 && i < list.size(); i++) {
//
//			if (i > 0)
//				sb.append("\ncaused by ");
//			Throwable ts = list.get(i);
//			sb.append(ts.getClass().getName());
//			if (ts.getMessage() == null)
//				sb.append(": null\n");
//			else {
//				sb.append(": ");
//				sb.append(ts.getMessage());
//				sb.append("\n");
//			}
//			StackTraceElement[] ret = ts.getStackTrace();
//			sb.append(ret[0].getClassName());
//			sb.append(".");
//			sb.append(ret[0].getMethodName());
//			sb.append(" line ");
//			sb.append(ret[0].getLineNumber());
//		}
//		return sb.toString();
//	}

}
