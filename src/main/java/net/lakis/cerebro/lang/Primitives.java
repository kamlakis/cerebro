package net.lakis.cerebro.lang;

public class Primitives {

	////////////////////// STRING
	public static String get(String value) {
		return get(value, "");
	}

	private static String get(String value, String defaultValue) {
		try {
			if (value != null)
				return value;
		} catch (Exception e) {
		}
		return defaultValue;
	}
	
	////////////////////// Character
	public static char get(Character value) {
		return get(value, (char) 0);
	}

	private static char get(Character value, char defaultValue) {
		try {
			if (value != null)
				return value.charValue();
		} catch (Exception e) {
		}
		return defaultValue;
	}
	
	////////////////////// BOOLEAN
	public static boolean get(Boolean value) {
		return get(value, false);
	}

	private static boolean get(Boolean value, boolean defaultValue) {
		try {
			if (value != null)
				return value.booleanValue();
		} catch (Exception e) {
		}
		return defaultValue;
	}

	////////////////////// BYTE
	public static byte get(Byte value) {
		return get(value, (byte) 0);
	}

	private static byte get(Byte value, byte defaultValue) {
		try {
			if (value != null)
				return value.byteValue();
		} catch (Exception e) {
		}
		return defaultValue;
	}

	////////////////////// SHORT
	public static short get(Short value) {
		return get(value, (short) 0);
	}

	private static short get(Short value, short defaultValue) {
		try {
			if (value != null)
				return value.shortValue();
		} catch (Exception e) {
		}
		return defaultValue;
	}

	////////////////////// INTEGER
	public static int get(Integer value) {
		return get(value, 0);
	}

	private static int get(Integer value, int defaultValue) {
		try {
			if (value != null)
				return value.intValue();
		} catch (Exception e) {
		}
		return defaultValue;
	}
	
	
	////////////////////// LONG
	public static long get(Long value) {
		return get(value, 0);
	}

	private static long get(Long value, long defaultValue) {
		try {
			if (value != null)
				return value.longValue();
		} catch (Exception e) {
		}
		return defaultValue;
	}

	
	////////////////////// FLOAT
	public static float get(Float value) {
		return get(value, 0);
	}

	private static float get(Float value, float defaultValue) {
		try {
			if (value != null)
				return value.floatValue();
		} catch (Exception e) {
		}
		return defaultValue;
	}

	
	
	////////////////////// LONG
	public static double get(Double value) {
		return get(value, 0);
	}

	private static double get(Double value, double defaultValue) {
		try {
			if (value != null)
				return value.doubleValue();
		} catch (Exception e) {
		}
		return defaultValue;
	}

}
