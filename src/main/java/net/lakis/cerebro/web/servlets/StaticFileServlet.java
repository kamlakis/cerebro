package net.lakis.cerebro.web.servlets;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.http.util.HttpStatus;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class StaticFileServlet extends StaticHttpHandler   {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	protected String rootPath;
	protected boolean autoIndex;

	public String getRootPath() {
		return rootPath;
	}

	
 
	public void setRootPath(String rootPath) {
		super.getDocRoots().clear();
		super.addDocRoot(rootPath);
		this.rootPath = rootPath;
	}

	protected String getRelativeURI(final Request request) throws Exception {
		if(StringUtils.isBlank(request.getDecodedRequestURI()))
			return "/";
		return super.getRelativeURI(request);
	}
	
	protected boolean handle(final String uri, final Request request, final Response response) throws Exception {
		log.trace("recieved on StaticFileServlet {}", uri);
		File resource = new File(rootPath, URLDecoder.decode(uri, "UTF-8"));

		if (resource.isDirectory()) {
			return handleDirectory(uri, resource, request, response);
		} else {
			return handleFile(uri, resource, request, response);
		}
	}

	protected String[] getIndexFiles() {
		return new String[] { "index.html", "index.htm" };
	}

	protected boolean handleDirectory(String uri, File resource, Request request, Response response)
			throws IOException {
		for (String index : getIndexFiles()) {
			final File file = new File(resource, "/" + index);
			if (file.exists()) {
				return handleFile(uri, file, request, response);
			}
		}

		if (autoIndex) {
			handleAutoIndex(resource, request, response, "/".equals(uri));
			return true;
		}
		
		// file doesn't exist and autoIndex is not allowed return 404
		return false;

	}

	protected boolean handleFile(String uri, File resource, Request request, Response response) throws IOException {
		if (!resource.exists())
			return false;

		addToFileCache(request, response, resource);
		sendFile(response, resource);
		return true;
	}

	private void handleAutoIndex(File directory, Request request, Response response, boolean isRootDirectory)
			throws IOException {
		String url = request.getRequestURI().replaceAll("([^\\/])$", "$1/");

		StringBuilder sb = new StringBuilder("<html>\r\n<head><title>Index of /");
		sb.append(url);
		sb.append("</title></head>\r\n<body bgcolor=\"white\">\r\n<h1>Index of ");
		sb.append(url);
		if (isRootDirectory) {
			sb.append("</h1><hr><pre>\r\n");
		} else {
			sb.append("</h1><hr><pre><a href=\"");
			sb.append(url);
			sb.append("../\">../</a>\r\n");
		}

		for (File file : directory.listFiles()) {
			boolean isFile = file.isFile();

			if (!isFile && !file.isDirectory())
				continue;

			String dt = sdf.format(file.lastModified());

			sb.append("<a href=\"");
			sb.append(url);
			sb.append(URLEncoder.encode(file.getName(), "UTF-8"));
			sb.append(isFile ? "\">" : "/\">");
			sb.append(file.getName());
			sb.append("");
			sb.append(isFile ? "</a>" : "/</a>");

			for (int k = file.getName().length(); k < 44; k++)
				sb.append(' ');

			if (isFile)
				sb.append(' ');

			sb.append(dt);
			for (int k = dt.length(); k < 38; k++)
				sb.append(' ');

			sb.append(isFile ? file.length() : "-");

			sb.append("\r\n");

		}
		sb.append("</pre><hr></body>\r\n</html>\r\n");

		response.setStatus(HttpStatus.OK_200);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(sb.toString());

	}

	public boolean isAutoIndex() {
		return autoIndex;
	}

	public void setAutoIndex(boolean autoIndex) {
		this.autoIndex = autoIndex;
	}
}
