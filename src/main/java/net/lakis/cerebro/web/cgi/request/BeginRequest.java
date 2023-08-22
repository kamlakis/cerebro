package net.lakis.cerebro.web.cgi.request;

import java.io.IOException;

import net.lakis.cerebro.web.cgi.enumerations.FcgiType;
import net.lakis.cerebro.web.cgi.enumerations.FcgiVersion;

public class BeginRequest extends FcgiRequest {

	private int role;
	private int flags;// keepalive flags & FCGI_KEEP_CONN

	public BeginRequest(FcgiVersion version, FcgiType type, int requestId, int role) {
		this(version, type, requestId, role, 0);
	}

	public BeginRequest(FcgiVersion version, FcgiType type, int requestId, int role, int flags) {
		super(version, type, requestId);
		this.role = role;
		this.flags = flags;
	}

	@Override
	protected byte[] toByteArray() throws IOException {
		return new byte[] { (byte) (role >> 8 & 0xFF), (byte) (role & 0xFF), //
				(byte) flags, 0, 0, 0, 0, 0 };
	}
}
