package com.xss.xsscommon.util;


public class ObjectUtil {
	public static Byte toByte(Object o) {
		try {
			if (o instanceof Number) {
				return ((Number) o).byteValue();
			} else if (o instanceof String && ((String) o).contains(".")) {
				return Double.valueOf((String) o).byteValue();
			}
			return Byte.valueOf(o.toString());
		} catch (Exception e) {
			return null;
		}
	}

	public static Integer toInteger(Object o) {
		try {
			if (o instanceof Number) {
				return ((Number) o).intValue();
			} else if (o instanceof String && ((String) o).contains(".")) {
				return Double.valueOf((String) o).intValue();
			}
			return Integer.valueOf(o.toString());
		} catch (Exception e) {
			return null;
		}
	}

	public static Long toLong(Object o) {
		try {
			if (o instanceof Number) {
				return ((Number) o).longValue();
			} else if (o instanceof String && ((String) o).contains(".")) {
				return Double.valueOf((String) o).longValue();
			}
			return Long.valueOf(o.toString());
		} catch (Exception e) {
			return null;
		}
	}

	public static Double toDouble(Object o) {
		try {
			if (o instanceof Number) {
				return ((Number) o).doubleValue();
			}
			return Double.valueOf(o.toString());
		} catch (Exception e) {
			return null;
		}
	}

	public static Boolean toBoolean(Object o) {
		try {
			if (o instanceof Boolean) {
				return (Boolean) o;
			}
			return Boolean.valueOf(o.toString());
		} catch (Exception e) {
			return null;
		}
	}

	public static String toString(Object o) {
		if (null == o) {
			return null;
		}
		return o.toString();
	}
}
