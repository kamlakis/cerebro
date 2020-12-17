package net.lakis;

import net.lakis.cerebro.commons.HexUtils;
import net.lakis.cerebro.commons.IntegerUtils;
import net.lakis.cerebro.socket.net.lakis.cerebro.socket.io.SocketInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {

        String line = "مهندسو البقاع الشمالي";

        char t = line.charAt(1);
        System.out.println((int) t);
        byte[] data = IntegerUtils.getBytes((int) t);
        System.out.println(HexUtils.dump(data));

        t = 't';
        System.out.println((int) t);
        data = IntegerUtils.getBytes((int) t);
        System.out.println(HexUtils.dump(data));
        System.out.println(IntegerUtils.getInt(data));
        byte[] bytes = ByteBuffer.allocate(4).putInt((int) t).array();
        System.out.println(HexUtils.dump(bytes));
        bytes = ByteBuffer.allocate(4).putChar(t).array();
        System.out.println(HexUtils.dump(bytes));
        bytes = ByteBuffer.allocate(4).putChar(line.charAt(1)).array();
        System.out.println(HexUtils.dump(bytes));
        bytes = ByteBuffer.allocate(4).putChar('한').array();
        System.out.println(HexUtils.dump(bytes));
        System.out.println(HexUtils.dump("한".getBytes()));
        System.out.println(HexUtils.dump("ه".getBytes()));
        System.out.println(HexUtils.dump("t".getBytes()));
        int ch1 = 00;
        int ch2 = 00;
        int ch3 = 0x6;
        int ch4 = 0x47;
        System.out.println((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
        System.out.println(Utf8((byte) 0xED, (byte) 0x95, (byte) 0x9C));
        System.out.println(Utf8((byte) 0xD9, (byte) 0x87));
        System.out.println(Utf8((byte) 0x74));

        SocketInputStream socketInputStream= new SocketInputStream(
                (byte) 0xD9, (byte) 0x87, (byte) 0x9C);

        System.out.println(socketInputStream.readChar());

    }


    public static final char Utf8(byte... data) throws IOException {
        if ((data[0] & 0x80) == 0) {
            return (char) data[0];
        } else if ((data[0] & 0xe0) == 0xc0) {
            if ((data[1] & 0xc0) != 0x80)
                throw new UTFDataFormatException(
                        "malformed input around 2nd byte");
            return (char) (((data[0] & 0x1f) << 6) |
                    (data[1] & 0x3f));
        } else if ((data[0] & 0xf0) == 0xe0) {
            if ((data[1] & 0xc0) != 0x80 || (data[2] & 0xc0) != 0x80)
                throw new UTFDataFormatException(
                        "malformed input around 3rd byte");
            return (char) (((data[0] & 0x0f) << 12) |
                    ((data[1] & 0x3f) << 6) |
                    ((data[2] & 0x3f) << 0));

        } else {
            throw new UTFDataFormatException(
                    "malformed input around 1st byte");

        }


    }
}
