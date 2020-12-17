package net.lakis.cerebro.socket.server;

import java.io.File;
import java.io.IOException;

import com.etsy.net.JUDS;
import com.etsy.net.UnixDomainSocketServer;

public class UnixSocketServer implements ISocketServer {
	private UnixDomainSocketServer serverSocket;

	public UnixSocketServer(String path) throws IOException {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		this.serverSocket = new UnixDomainSocketServer(path, JUDS.SOCK_STREAM, 10);
	}

	public ISocket accept() throws IOException {
		return new UnixSocket(this.serverSocket.accept());
	}

	public void close() throws IOException {
		this.serverSocket.close();
	}

}
