package net.lakis.cerebro.web.cgi.enumerations;

import java.util.HashMap;

public enum FcgiVersion {
	
	FCGI_VERSION_1(1) ;

	private int id;
	private static final HashMap<Integer, FcgiVersion> lookup = new HashMap<Integer, FcgiVersion>();
	static {
		for (FcgiVersion td : FcgiVersion.values()) {
			lookup.put(td.id, td);
		}
	}

	public int getId() {
		return id;
	}

	public static FcgiVersion get(int id) {
		FcgiVersion ret = lookup.get(id);
		if (ret == null) {
			FcgiVersion[] values = FcgiVersion.values();
			return values[values.length - 1];
		}
		return ret;
	}

	private FcgiVersion(int _id) {
		id = _id;
	}	
}
