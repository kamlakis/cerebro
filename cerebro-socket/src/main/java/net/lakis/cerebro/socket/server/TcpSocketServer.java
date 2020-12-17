package net.lakis.cerebro.socket.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class TcpSocketServer implements ISocketServer {
    private ServerSocket serverSocket;

    public TcpSocketServer(String host, int port) throws IOException {

        this.serverSocket = new ServerSocket();
        this.serverSocket.setReuseAddress(true);
        if (host == null)
            this.serverSocket.bind(new InetSocketAddress(port));
        else
            this.serverSocket.bind(new InetSocketAddress(host, port));
    }

    public ISocket accept() throws IOException {
        return new TcpSocket(this.serverSocket.accept());
    }

    public void close() throws IOException {
        this.serverSocket.close();
    }

}
