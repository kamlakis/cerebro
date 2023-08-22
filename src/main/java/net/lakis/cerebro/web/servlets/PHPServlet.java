package net.lakis.cerebro.web.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

import net.lakis.cerebro.web.cgi.CGIExecutor;
import net.lakis.cerebro.web.cgi.FastCGIExecutor;

public class PHPServlet extends StaticFileServlet  {

	private String host;
	private int port;
	private String cgiExecutor = "php-cgi";

	protected String[] getIndexFiles() {
		return new String[] { "index.php", "index.html", "index.htm" };
	}

	protected boolean handleFile(String uri, File resource, Request request, Response response) throws IOException {
		if (!resource.exists())
			return false;

		if (resource.getAbsolutePath().endsWith(".php")) {
			handlePHP(uri, resource, request, response);
		} else {
			addToFileCache(request, response, resource);
			sendFile(response, resource);
		}
		return true;
	}

	protected void handlePHP(String uri, File resource, Request request, Response response)
			throws UnknownHostException, IOException {

		String filename = resource.getAbsolutePath();

		Map<String, String> params = new HashMap<String, String>();
		params.put("GATEWAY_INTERFACE", "FastCGI/1.0");
		params.put("REQUEST_METHOD", request.getMethod().getMethodString());
		params.put("SCRIPT_FILENAME", filename);
		params.put("SCRIPT_NAME", uri);
		params.put("QUERY_STRING", request.getQueryString());
		params.put("REQUEST_URI", uri);
		params.put("DOCUMENT_ROOT", rootPath);
		params.put("REMOTE_ADDR", request.getRemoteAddr());
		params.put("REMOTE_PORT", String.valueOf(request.getRemotePort()));
		params.put("SERVER_ADDR", request.getLocalAddr());
		params.put("SERVER_NAME", request.getLocalName());
		params.put("SERVER_PORT", String.valueOf(request.getLocalPort()));
		params.put("SERVER_PROTOCOL", request.getProtocol().getProtocolString());
		params.put("CONTENT_TYPE", request.getContentType());

		params.put("CONTENT_LENGTH", String.valueOf(request.getContentLength()));

		for (String headerName : request.getHeaderNames()) {
			params.put("HTTP_" + headerName.toUpperCase().replaceAll("-", "_"), request.getHeader(headerName));

		}
		byte[] payload = null;
		if (request.getContentLength() > 0)
			payload = readContent(request);

		byte[] data = null;
		if (port > 0 && StringUtils.isNotBlank(host)) {
			data = new FastCGIExecutor(host, port).execute(params, payload);
		} else if(StringUtils.isNotBlank(cgiExecutor)) {
			data = new CGIExecutor(cgiExecutor).execute(params, payload);
		} else 
			throw new IOException("host and port are not set for FastCGI and cgiExecutor is not set for cgiExecutor");

		
		int j = 0;
		int i = 0;
		for (; i < data.length; i++) {
			if (data[i] == '\r' && data[i + 1] == '\n') {
				if (i == j) {
					i = i + 2;
					break;
				}
				String header = new String(data, j, i - j);
				int ind = header.indexOf(":");
				String key = header.substring(0, ind);
				String value = header.substring(ind + 1);

				if (j == 0) {
					if (key.equalsIgnoreCase("Status")) {
						response.setStatus(Integer.parseInt(header.substring(ind + 1).replaceAll("[^0-9]*", "")));
						continue;
					} else {
						response.setStatus(HttpStatus.OK_200);
					}
				}

				if (key.equalsIgnoreCase("Content-type")) {
					response.setContentType(value);
				} else {
					response.addHeader(key, value);
				}

				i = i + 1;
				j = i + 1;
			}
		}
		response.getOutputStream().write(data, i, data.length - i);
		response.getOutputStream().flush();
	}

	public byte[] readContent(Request request) throws IOException {
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
		return bytes;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getCgiExecutor() {
		return cgiExecutor;
	}

	public void setCgiExecutor(String cgiExecutor) {
		this.cgiExecutor = cgiExecutor;
	}

}
