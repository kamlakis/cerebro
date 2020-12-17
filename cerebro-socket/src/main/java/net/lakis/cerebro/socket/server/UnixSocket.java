package net.lakis.cerebro.socket.server;

import com.etsy.net.UnixDomainSocket;
import net.lakis.cerebro.socket.net.lakis.cerebro.socket.io.SocketInputStream;
import net.lakis.cerebro.socket.net.lakis.cerebro.socket.io.SocketOutputStream;

public class UnixSocket implements ISocket {
    private UnixDomainSocket socket;
    private SocketOutputStream out;
    private SocketInputStream in;

    public UnixSocket(UnixDomainSocket socket) {
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
