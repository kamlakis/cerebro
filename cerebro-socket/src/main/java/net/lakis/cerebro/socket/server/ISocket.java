package net.lakis.cerebro.socket.server;

import net.lakis.cerebro.socket.net.lakis.cerebro.socket.io.SocketInputStream;
import net.lakis.cerebro.socket.net.lakis.cerebro.socket.io.SocketOutputStream;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.PrintWriter;

public interface ISocket extends Closeable {
	public SocketOutputStream getOutput();

	public SocketInputStream getInput();

}
