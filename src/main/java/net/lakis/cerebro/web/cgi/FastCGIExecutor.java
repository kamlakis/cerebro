package net.lakis.cerebro.web.cgi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import net.lakis.cerebro.web.cgi.enumerations.FcgiFlags;
import net.lakis.cerebro.web.cgi.enumerations.FcgiRoles;
import net.lakis.cerebro.web.cgi.enumerations.FcgiType;
import net.lakis.cerebro.web.cgi.enumerations.FcgiVersion;
import net.lakis.cerebro.web.cgi.request.BeginRequest;
import net.lakis.cerebro.web.cgi.request.BodyRequest;
import net.lakis.cerebro.web.cgi.request.NameValueRequest;
import net.lakis.cerebro.web.cgi.response.FcgiResponse;

public class FastCGIExecutor {
	private String host;
	private int port;

	public FastCGIExecutor(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public byte[] execute(Map<String, String> params, byte[] payload) throws IOException {
		try (Socket socket = new Socket(host, port);
				InputStream is = socket.getInputStream();
				OutputStream os = socket.getOutputStream();) {
			socket.setKeepAlive(true);

			BeginRequest beginRequest = new BeginRequest(FcgiVersion.FCGI_VERSION_1, FcgiType.BEGIN, 1,
					FcgiRoles.FCGI_RESPONDER, FcgiFlags.FCGI_KEEP_CONN);
			beginRequest.writeTo(os);

			NameValueRequest nameValueRequest = new NameValueRequest(FcgiVersion.FCGI_VERSION_1, FcgiType.PARAMS, 1);
			nameValueRequest.setParams(params);

			nameValueRequest.writeTo(os);

			nameValueRequest = new NameValueRequest(FcgiVersion.FCGI_VERSION_1, FcgiType.PARAMS, 1);
			nameValueRequest.writeTo(os);

			BodyRequest bodyRequest;
			if (payload != null && payload.length > 0) {
				bodyRequest = new BodyRequest(FcgiVersion.FCGI_VERSION_1, FcgiType.STDIN, 1, payload);
				bodyRequest.writeTo(os);
			}
			bodyRequest = new BodyRequest(FcgiVersion.FCGI_VERSION_1, FcgiType.STDIN, 1, null);
			bodyRequest.writeTo(os);

			FcgiResponse fcgiResponse = new FcgiResponse();

			while (true) {

				byte header[] = new byte[8];
				is.read(header, 0, 8);
				fcgiResponse.decodeHeader(header);
				int len = fcgiResponse.getContentLength();

				byte data[] = new byte[len];
				is.read(data, 0, len);

				if (fcgiResponse.getPaddingLength() > 0) {
					is.skip(fcgiResponse.getPaddingLength());
				}

				if (fcgiResponse.getType() == FcgiType.STDOUT)// || fcgiResponse.getType() == FcgiType.STDOUT)
				{
					fcgiResponse.appendContent(data);
				} else if (fcgiResponse.getType() == FcgiType.END) {
					fcgiResponse.decodeEnd(data);
					break;
				}

			}

			return fcgiResponse.getContent();
		}
	}
}
