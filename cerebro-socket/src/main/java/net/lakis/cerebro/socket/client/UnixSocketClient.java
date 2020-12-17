package net.lakis.cerebro.socket.client;

import com.etsy.net.JUDS;
import com.etsy.net.UnixDomainSocketClient;
import net.lakis.cerebro.socket.net.lakis.cerebro.socket.io.SocketInputStream;
import net.lakis.cerebro.socket.net.lakis.cerebro.socket.io.SocketOutputStream;
import net.lakis.cerebro.socket.server.ISocket;

import java.io.IOException;


public class UnixSocketClient implements ISocket {
    private UnixDomainSocketClient socket;
    private SocketOutputStream out;
    private SocketInputStream in;

    public UnixSocketClient(String path) throws IOException {
        this.socket = new UnixDomainSocketClient(path, JUDS.SOCK_STREAM);
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
