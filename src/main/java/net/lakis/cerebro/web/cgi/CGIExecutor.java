package net.lakis.cerebro.web.cgi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class CGIExecutor {
	private String cgiExecutor;

	public CGIExecutor(String cgiExecutor) {
		this.cgiExecutor = cgiExecutor;
	}

	public byte[] execute(Map<String, String> params, byte[] payload) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(this.cgiExecutor);
		builder.environment().putAll(params);
		builder.environment().put("REDIRECT_STATUS", "1");
		Process process = builder.start();
		try (InputStream is = process.getInputStream(); //
				OutputStream os = process.getOutputStream()) {
			if (payload != null && payload.length > 0)
				os.write(payload);
			os.close();

 			return IOUtils.toByteArray(is);

		} finally {
			process.destroy();
		} 
	}
}
