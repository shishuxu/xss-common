package com.xss.xsscommon.db;

import org.slf4j.Logger;

public class LoggerUtil {

	private static final String COLON_PREFIX = ":";

	private static String sourcePosi(int depth) {
		if (0 > depth) {
			return "";
		}
		Exception e = new Exception();
		StackTraceElement[] l = e.getStackTrace();
		if (null == l || depth >= l.length)
			return "";
		return "[" + l[depth].getFileName() + ":" + l[depth].getLineNumber() + " " + l[depth].getMethodName() + "]";
	}

	public static void debug(Logger log, String logMsg) {
		if (log.isDebugEnabled()) {
			log.debug(sourcePosi(2) + logMsg);
		}
	}

	public static void info(Logger log, String logMsg) {
		if (log.isInfoEnabled()) {
			log.info(sourcePosi(2) + logMsg);
		}
	}

	public static void warn(Logger log, String logMsg) {
		if (log.isWarnEnabled()) {
			log.warn(sourcePosi(2) + logMsg);
		}
	}

	public static void warn(Logger log, Throwable e) {
		if (log.isWarnEnabled()) {
			log.warn(sourcePosi(2) + ", ", e);
		}
	}

	public static void warn(Logger log, String logMsg, Throwable e) {
		if (log.isWarnEnabled()) {
			log.warn(sourcePosi(2) + logMsg + ", ", e);
		}
	}

	public static void error(Logger log, String logMsg) {
		if (log.isErrorEnabled()) {
			log.error(sourcePosi(2) + logMsg);
		}
	}

	public static void error(Logger log, Throwable e) {
		if (log.isErrorEnabled()) {
			log.error(sourcePosi(2) + ", " + e.getMessage(), e);
		}
	}

	public static void error(Logger log, String logMsg, Throwable e) {
		if (log.isErrorEnabled()) {
			log.error(sourcePosi(2) + logMsg + ", " + e.getMessage(), e);
		}
	}

}
