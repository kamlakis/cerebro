package net.lakis.cerebro.web.cgi.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import net.lakis.cerebro.web.cgi.enumerations.FcgiType;
import net.lakis.cerebro.web.cgi.enumerations.FcgiVersion;

public class NameValueRequest extends FcgiRequest {

	private Map<String, String> params;

	public NameValueRequest(FcgiVersion version, FcgiType type, int requestId) {
		super(version, type, requestId);

	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	@Override
	protected byte[] toByteArray() throws IOException {
		if (params == null)
			return null;
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		for (String key : params.keySet()) {
			String value = params.get(key);
			if (value == null)
				continue;
			stream.write(encodeLength(key.length()));
			stream.write(encodeLength(value.length()));
			stream.write(key.getBytes());
			stream.write(value.getBytes());
		}
		return stream.toByteArray();
	}

	private byte[] encodeLength(int length) throws IOException {
		if (length < 128) {
			return new byte[] { (byte) (length & 0xFF) };
		} else {
			return new byte[] { (byte) ((length >> 24) & 0xFF | 0x80), //
					(byte) ((length >> 16) & 0xFF), //
					(byte) ((length >> 8) & 0xFF), //
					(byte) (length & 0xFF) };
		}
	}

}
