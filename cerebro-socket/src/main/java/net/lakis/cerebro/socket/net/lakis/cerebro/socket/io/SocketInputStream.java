package net.lakis.cerebro.socket.net.lakis.cerebro.socket.io;

import java.io.*;

public class SocketInputStream implements Closeable {

    private final InputStream inputStream;

    public SocketInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public SocketInputStream(byte... data) {
        this(new ByteArrayInputStream(data));
    }


    private void readWord() throws IOException {
    }

    public void readMessage() throws IOException {

    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    public char readChar() throws IOException {
        int b1 = inputStream.read();
        if ((b1 & 0x80) == 0) {
            return (char) b1;
        } else if ((b1 & 0xe0) == 0xc0) {
            int b2 = inputStream.read();
            if ((b2 & 0xc0) != 0x80)
                throw new UTFDataFormatException(
                        "malformed input around 2nd byte");
            return (char) (((b1 & 0x1f) << 6) |
                    (b2 & 0x3f));
        } else if ((b1 & 0xf0) == 0xe0) {
            int b2 = inputStream.read();
            int b3 = inputStream.read();
            if ((b2 & 0xc0) != 0x80 || (b3 & 0xc0) != 0x80)
                throw new UTFDataFormatException(
                        "malformed input around 3rd byte");
            return (char) (((b1 & 0x0f) << 12) |
                    ((b2 & 0x3f) << 6) |
                    (b3 & 0x3f));
        } else {
            throw new UTFDataFormatException("malformed input around 1st byte");
        }
    }
}
