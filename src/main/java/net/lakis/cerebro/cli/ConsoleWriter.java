package net.lakis.cerebro.cli;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import net.lakis.cerebro.io.ByteArrayOutputStream;

public class ConsoleWriter {

	private SocketChannel socket;

	public ConsoleWriter(SocketChannel socket) {
		this.socket = socket;
	}

	public synchronized void write(String message) {
		try {
			try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				baos.writePString(message);
				
		        ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
		        socket.write(buffer);
			}
		} catch (Exception e) {
		}

	}
	
	public boolean isConnected() {
		return socket != null ;
	}
	
	public void close() {
		this.socket = null;
	}
}
