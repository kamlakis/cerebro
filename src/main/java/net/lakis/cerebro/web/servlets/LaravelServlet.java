package net.lakis.cerebro.web.servlets;

import java.io.File;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public class LaravelServlet extends PHPServlet  {

	public void service(final Request request, final Response response) throws Exception {
		String uri = getRelativeURI(request);
		if (uri == null)
			uri = "/";

		if (!handle(uri, request, response)) {
			File ressource = new File(rootPath, "/index.php") ; 			
			handlePHP( uri, ressource, request, response);
		}

	}

}
