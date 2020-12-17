package net.lakis.cerebro.socket;


import net.lakis.cerebro.socket.client.TcpSocketClient;
import net.lakis.cerebro.socket.client.UnixSocketClient;
import net.lakis.cerebro.socket.server.ISocket;
import net.lakis.cerebro.socket.server.ISocketServer;
import net.lakis.cerebro.socket.server.TcpSocketServer;
import net.lakis.cerebro.socket.server.UnixSocketServer;

public class SocketGenerator {


    public ISocketServer generateTcpServer(String host, int port) throws Exception {
        return new TcpSocketServer(host, port);
    }

    public ISocketServer generateTcpServer(int port) throws Exception {
        return new TcpSocketServer(null, port);
    }

    public ISocketServer generateUnixServer(String path) throws Exception {
        return new UnixSocketServer(path);
    }

    public ISocket generateTcpClient(String host, int port) throws Exception {
        return new TcpSocketClient(host, port);
    }

    public ISocket generateUnixClient(String path) throws Exception {
        return new UnixSocketClient(path);
    }

}
