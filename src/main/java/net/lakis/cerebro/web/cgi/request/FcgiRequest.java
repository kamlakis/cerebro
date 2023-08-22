package net.lakis.cerebro.web.cgi.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.lakis.cerebro.web.cgi.enumerations.FcgiType;
import net.lakis.cerebro.web.cgi.enumerations.FcgiVersion;

public abstract class FcgiRequest {

	private FcgiVersion version;
	private FcgiType type;
	private int requestId;

	public FcgiRequest(FcgiVersion version, FcgiType type, int requestId) {
		this.version = version;
		this.type = type;
		this.requestId = requestId;
	}

	public boolean writeTo(OutputStream os) {
		try {
			toOutputStream().writeTo(os);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public ByteArrayOutputStream toOutputStream() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] data = toByteArray();
		int len = data == null ? 0 : data.length;

		out.write(this.version.getId());
		out.write(this.type.getId());
		out.write((byte) ((this.requestId >> 8) & 0xFF));
		out.write((byte) (this.requestId & 0xFF));
		out.write((byte) ((len >> 8) & 0xFF));
		out.write((byte) (len & 0xFF));
		out.write(0);
		out.write(0);

		if (data != null) {
			out.write(data);
		}
		return out;
	}

	protected abstract byte[] toByteArray() throws IOException;

}
