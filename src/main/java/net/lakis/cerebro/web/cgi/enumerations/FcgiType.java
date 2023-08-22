package net.lakis.cerebro.web.cgi.enumerations;

import java.util.HashMap;

public enum FcgiType {

	BEGIN(1), //
	ABORT(2), //
	END(3), //
	PARAMS(4), //
	STDIN(5), //
	STDOUT(6), //
	STDERR(7), //
	DATA(8), //
	GET_VALUES(9), //
	GET_VALUES_RESULT(10), //
	UNKNOWN(11);

	private int id;
	private static final HashMap<Integer, FcgiType> lookup = new HashMap<Integer, FcgiType>();
	static {
		for (FcgiType td : FcgiType.values()) {
			lookup.put(td.id, td);
		}
	}

	public int getId() {
		return id;
	}

	public static FcgiType get(int id) {
		FcgiType ret = lookup.get(id);
		if (ret == null) {
			FcgiType[] values = FcgiType.values();
			return values[values.length - 1];
		}
		return ret;
	}

	private FcgiType(int _id) {
		id = _id;
	}
}
