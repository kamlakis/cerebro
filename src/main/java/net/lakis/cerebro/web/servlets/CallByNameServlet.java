package net.lakis.cerebro.web.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

import com.google.gson.Gson;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CallByNameServlet extends HttpHandler  {

	@Override
	public void service(Request req, Response resp) throws Exception {
		try {
			String methodName = req.getRequestURI().replaceAll("^(/)([^/]*)(/)([^/?]*)(.*)", "$4");

			Method[] methods = getClass().getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().equalsIgnoreCase(methodName)) {
					methods[i].invoke(this, req, resp);
					return;
				}
			}
			log.error("Exception : Method Not Found");
			
			resp.setStatus(HttpStatus.NOT_FOUND_404);
		} catch (SecurityException e) {
			log.error("SecurityException : Forbidden", e);
			resp.setStatus(HttpStatus.FORBIDDEN_403);
		} catch (Exception e) {
			log.error("Exception ", e);
			resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
	}

	public <T> T readJsonRequest(Request req, Class<T> classOfT) throws IOException {
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
		return new Gson().fromJson(new String(bytes), classOfT);
	}

	public void writeJsonResponse(Response resp, Object obj) throws IOException {
		resp.setContentType("application/json;charset=UTF-8");
		resp.getWriter().write(new Gson().toJson(obj));
	}
}
