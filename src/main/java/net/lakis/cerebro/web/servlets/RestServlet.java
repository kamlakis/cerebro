package net.lakis.cerebro.web.servlets;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.util.HtmlHelper;
import org.glassfish.grizzly.http.util.HttpStatus;

import net.lakis.cerebro.web.ServletEvent;

public abstract class RestServlet extends HttpHandler  {

	private static final Logger log = LogManager.getLogger(RestServlet.class);

	@Override
	public void service(Request request, Response response) throws Exception {
		try {

			Method method = request.getMethod();
			if (method == Method.GET) {
				doGet(new ServletEvent(request, response));
			} else if (method == Method.POST) {
				doPost(new ServletEvent(request, response));
			} else if (method == Method.HEAD) {
				doHead(new ServletEvent(request, response));
			} else if (method == Method.PUT) {
				doPut(new ServletEvent(request, response));
			} else if (method == Method.DELETE) {
				doDelete(new ServletEvent(request, response));
			} else if (method == Method.TRACE) {
				doTrace(new ServletEvent(request, response));
			} else if (method == Method.CONNECT) {
				doConnect(new ServletEvent(request, response));
			} else if (method == Method.OPTIONS) {
				doOptions(new ServletEvent(request, response));
			} else if (method == Method.PATCH) {
				doPatch(new ServletEvent(request, response));
			}
		} catch (SecurityException e) {
			log.error("SecurityException : Forbidden", e);
			response.setStatus(HttpStatus.FORBIDDEN_403);
		} catch (Exception e) {
			log.error("Exception ", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
	}

	protected void doTrace(ServletEvent servletEvent) throws IOException {
		unsupported(servletEvent);
	}

	protected void doPatch(ServletEvent servletEvent) throws IOException {
		unsupported(servletEvent);
	}

	protected void doConnect(ServletEvent servletEvent) throws IOException {
		unsupported(servletEvent);
	}

	protected void doDelete(ServletEvent servletEvent) throws IOException {
		unsupported(servletEvent);
	}

	protected void doPut(ServletEvent servletEvent) throws IOException {
		unsupported(servletEvent);
	}

	protected void doPost(ServletEvent servletEvent) throws IOException {
		unsupported(servletEvent);
	}

	protected void doHead(ServletEvent servletEvent) throws IOException {
		unsupported(servletEvent);
	}

	protected void doGet(ServletEvent servletEvent) throws IOException {
		unsupported(servletEvent);
	}

	protected void doOptions(ServletEvent servletEvent) throws IOException {
		unsupported(servletEvent);
	}

	protected void unsupported(ServletEvent servletEvent) throws IOException {
		Response res = servletEvent.getResponse();
		res.setStatus(HttpStatus.FORBIDDEN_403);

		final String bb = HtmlHelper.getErrorPage("Not Allowed", servletEvent.getRequest().getMethod().getMethodString()
				+ " request is not allowed on " + servletEvent.getRequest().getRequestURI(), "");
		res.setContentType("text/html");
		res.getWriter().write(bb);
	}

}
