package net.lakis.cerebro.web.cgi.request;

import java.io.IOException;

import net.lakis.cerebro.web.cgi.enumerations.FcgiType;
import net.lakis.cerebro.web.cgi.enumerations.FcgiVersion;

public class BodyRequest extends FcgiRequest {

	byte[] content;

	public BodyRequest(FcgiVersion version, FcgiType type, int requestId, byte[] content) {
		super(version, type, requestId);
		this.content = content;
	}

	@Override
	protected byte[] toByteArray() throws IOException {
		return content;
	}
}