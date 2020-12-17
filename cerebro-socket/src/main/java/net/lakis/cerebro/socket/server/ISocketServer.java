package net.lakis.cerebro.socket.server;

import java.io.Closeable;
import java.io.IOException;

public interface ISocketServer extends Closeable {

	public ISocket accept() throws IOException;

}
