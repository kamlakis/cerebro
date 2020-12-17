package net.lakis.cerebro.socket.net.lakis.cerebro.socket.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public class SocketOutputStream implements Closeable {

    private final OutputStream outputStream;

    public SocketOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }


    @Override
    public void close() throws IOException {
        this.outputStream.close();
    }
}
