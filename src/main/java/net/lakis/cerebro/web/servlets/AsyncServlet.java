package net.lakis.cerebro.web.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import net.lakis.cerebro.web.ServletEvent;

public abstract class AsyncServlet extends HttpHandler {

	private static final Logger log = LogManager.getLogger(AsyncServlet.class);

	@Override
	public void service(Request request, Response response) throws Exception {
		try {
			response.suspend();
			handle(new ServletEvent(request, response));
		} catch (Exception e) {
			log.error("Exception ", e);
			response.sendError(500, "Server Error");
		}
	}

	protected abstract void handle(ServletEvent servletEvent);

}
