package net.lakis.cerebro.socket.client;

import net.lakis.cerebro.socket.net.lakis.cerebro.socket.io.SocketInputStream;
import net.lakis.cerebro.socket.net.lakis.cerebro.socket.io.SocketOutputStream;
import net.lakis.cerebro.socket.server.ISocket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class TcpSocketClient implements ISocket {
    private Socket socket;
    private SocketOutputStream out;
    private SocketInputStream in;

    public TcpSocketClient(String host, int port) throws UnknownHostException, IOException {
        this.socket = new Socket(host, port);
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
