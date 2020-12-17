package net.lakis.cerebro.socket.server;

import net.lakis.cerebro.socket.net.lakis.cerebro.socket.io.SocketInputStream;
import net.lakis.cerebro.socket.net.lakis.cerebro.socket.io.SocketOutputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class TcpSocket implements ISocket {
	private Socket socket;
	private SocketOutputStream out;
	private SocketInputStream in;

	public TcpSocket(Socket socket) throws IOException {
		this.socket = socket;
		this.out = new SocketOutputStream(socket.getOutputStream());
		this.in = new SocketInputStream(socket.getInputStream());

	}

	@Override
	public SocketOutputStream getOutput() {
		return this.out;
	}

	@Override
	public SocketInputStream getInput() {
		return this.in;
	}
	
	@Override
	public void close() {
		try {
			socket.close();
		} catch (Exception e) {
		}
		try {
			if (out != null)
				out.close();
		} catch (Exception e) {
		}

		try {
			if (in != null)
				in.close();
		} catch (Exception e) {
		}
	}


}
