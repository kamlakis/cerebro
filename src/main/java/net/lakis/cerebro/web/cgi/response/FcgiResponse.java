package net.lakis.cerebro.web.cgi.response;

import java.io.ByteArrayOutputStream;

import net.lakis.cerebro.web.cgi.enumerations.FcgiType;

public class FcgiResponse {
	private int version;
	private FcgiType type;
	private int requestID;
	private int contentLength;
	private int paddingLength;
	private int reserverd;
	private long appStatus;
	private int protocolStatus;

	private ByteArrayOutputStream outputStream;

	public FcgiResponse() {
		outputStream = new ByteArrayOutputStream();

	}

	public void decodeHeader(byte[] header) {
		version = header[0];
		type = FcgiType.get(header[1]);
		requestID = ((header[2] << 8) & 0xFF00) + (header[3] & 0xFF);
		contentLength = ((header[4] << 8) & 0x00FF00) + (header[5] & 0xFF);
		paddingLength = header[6];
		reserverd = header[7];
	}

	public void decodeEnd(byte[] end) {
		appStatus = ((end[0] << 24) & 0xFF000000) + ((end[1] << 16) & 0xFF0000) + //
				((end[2] << 8) & 0xFF00) + (end[3] & 0xFF);
		protocolStatus = end[4];
	}

	public void appendContent(byte[] contentData) {
		if (contentData != null && contentData.length > 0) {
			outputStream.write(contentData, 0, contentData.length);
		}
	}

	public byte[] getContent() {
		return outputStream.toByteArray();
	}

	public int getVersion() {
		return version;
	}

	public FcgiType getType() {
		return type;
	}

	public int getRequestID() {
		return requestID;
	}

	public int getContentLength() {
		return contentLength;
	}

	public int getPaddingLength() {
		return paddingLength;
	}

	public int getReserverd() {
		return reserverd;
	}

	public long getAppStatus() {
		return appStatus;
	}

	public int getProtocolStatus() {
		return protocolStatus;
	}

}
