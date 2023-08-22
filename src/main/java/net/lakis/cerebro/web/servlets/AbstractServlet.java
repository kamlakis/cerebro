package net.lakis.cerebro.web.servlets;

import java.io.IOException;
import java.io.InputStream;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

import com.google.gson.Gson;

import lombok.extern.log4j.Log4j2;
import net.lakis.cerebro.web.exceptions.InServletException;

@Log4j2
public abstract class AbstractServlet extends HttpHandler  {

 
	@Override
	public void service(Request request, Response response) throws Exception {
		try {
			String methodName = request.getRequestURI().replaceAll("^(/)([^/]*)(/)([^/?]*)(.*)", "$4");
			String body = readRequest(request);
			Object result = process(methodName, body, request.getAuthorization());

			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(new Gson().toJson(result));
		} catch (InServletException e) {
			log.error("InServletException : ", e);
			response.sendError(e.getHttpStatus(), e.getHttpMessage());

		} catch (SecurityException e) {
			log.error("SecurityException : Forbidden", e);
			response.setStatus(HttpStatus.FORBIDDEN_403);
		} catch (Exception e) {
			log.error("Exception ", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
	}

	public String readRequest(Request req) throws IOException {
		byte[] bytes = new byte[req.getContentLength()];
		InputStream stream = req.getInputStream();
		int c;
		for (int i = 0; i < bytes.length; i++) {
			c = stream.read();
			if (c == -1) {
				break;
			}
			bytes[i] = (byte) c;
		}
		return new String(bytes);
	}

	protected abstract Object process(String methodName, String body, String authorization) throws Exception;
}
