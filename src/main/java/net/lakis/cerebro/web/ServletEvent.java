package net.lakis.cerebro.web;

import java.io.IOException;
import java.io.InputStream;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import com.google.gson.Gson;

public class ServletEvent {
	private Request request;
	private Response response;

	public ServletEvent(Request request, Response response) {
		super();
		this.request = request;
		this.response = response;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public void writeStringResponse(String text) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(text);

		if (response.isSuspended()) {
			response.resume();
		}
	}

	public void writeJsonResponse(Object obj) throws IOException {
		response.setContentType("application/json;charset=UTF-8");

		response.getWriter().write(new Gson().toJson(obj));

		if (response.isSuspended()) {
			response.resume();
		}
	}

	public <T> T readJsonRequest(Class<T> classOfT) throws IOException {
		byte[] bytes = new byte[request.getContentLength()];
		InputStream stream = request.getInputStream();
		int c;
		for (int i = 0; i < bytes.length; i++) {
			c = stream.read();
			if (c == -1) {
				break;
			}
			bytes[i] = (byte) c;
		}
		return new Gson().fromJson(new String(bytes), classOfT);
	}

	public String readRequestAsString() throws IOException {

		return new String(readRequest());
	}

	private byte[] bytes;

	public byte[] readRequest() throws IOException {
		if (bytes == null) {
			bytes = new byte[request.getContentLength()];
			InputStream stream = request.getInputStream();
			int c;
			for (int i = 0; i < bytes.length; i++) {
				c = stream.read();
				if (c == -1) {
					break;
				}
				bytes[i] = (byte) c;
			}
		}
		return bytes;
	}

	public void suspend() {
		response.suspend();

	}

	public String getMethodName() {
		return request.getRequestURI().replaceAll("/$", "").replaceAll("^[^/]*/", "");
	}
}
