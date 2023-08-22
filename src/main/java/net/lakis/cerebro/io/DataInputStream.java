package net.lakis.cerebro.io;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.util.Arrays;

import lombok.Getter;
import net.lakis.cerebro.lang.Numbers;
 
public class DataInputStream implements Closeable {
	private @Getter final InputStream inputStream;

	public DataInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void skip(long n) throws IOException {
		this.inputStream.skip(n);
	}

	public int read(byte b[], int off, int len) throws IOException {
		return this.inputStream.read(b, off, len);
	}

	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	public String readLine() throws IOException {
		StringBuilder sb = new StringBuilder();
		for (;;) {
			char c = this.readChar();
			// if
			if (c == '\n')
				break;

			if (c == '\r') {
				c = this.readChar();
				if (c == '\n')
					break;
				sb.append('\r');
			}

			sb.append(c);
		}
		return sb.toString();
	}

	public byte[] readFully(int length) throws IOException {
		byte[] result = new byte[length];
		for (int pos = 0; pos < length;) {
			int r = read(result, pos, length - pos);
			if (r == -1) {
				throw new EOFException("No byte is available because the end of the streamhas been reached");
			}
			pos += r;
		}
		return result;
	}

	// not recommended use readFullyOrTillTheEnd(int length)
	public byte[] readFullyTillTheEnd() throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			for (;;) {
				int ret = inputStream.read();
				if (ret == -1)
					return baos.toByteArray();
				baos.writeByte((byte) ret);
			}
		}
	}

	public byte[] readFullyOrTillTheEnd(int length) throws IOException {
		byte[] result = new byte[length];
		for (int pos = 0; pos < length;) {
			int r = read(result, pos, length - pos);
			if (r == -1) {
				return Arrays.copyOf(result, pos);
			}
			pos += r;
		}
		return result;
	}

	public int readFullyOrTillTheEnd(byte[] result) throws IOException {
		int pos = 0;
		while (pos < result.length) {
			int r = read(result, pos, result.length - pos);
			if (r == -1) {
				return pos;
			}
			pos += r;
		}
		return pos;
	}

	public char readChar() throws IOException {
		byte b1 = readByte();
		// b1 is 0xxxxxxx it's ASCII
		if ((b1 & 0b1000_0000) == 0)
			return (char) b1;

		// b1 can't be 10xxxxxx or 1111xxxx
		if ((b1 & 0b1100_0000) == 0b1000_0000 || (b1 & 0b1111_0000) == 0b1111_0000)
			throw new UTFDataFormatException("malformed input around 1st byte");

		byte b2 = readByte();
		// b2 should be 10xxxxxx
		if ((b2 & 0b1100_0000) != 0b1000_0000)
			throw new UTFDataFormatException("malformed input around 2nd byte");

		// 110xxxxx 10xxxxxx
		if ((b1 & 0b1100_0000) != 0b1000_0000)
			return (char) (((b1 & 0b0001_1111) << 6) | (b2 & 0b0011_1111));

		byte b3 = readByte();
		// b3 should be 10xxxxxx
		if ((b3 & 0b1100_0000) != 0b1000_0000)
			throw new UTFDataFormatException("malformed input around 3rd byte");

		// 1110xxxx 10xxxxxx 10xxxxxx
		return (char) (((b1 & 0b0000_1111) << 12) | ((b2 & 0b0011_1111) << 6) | (b3 & 0b0011_1111));

	}

	public byte readByte() throws IOException {
		int ret = inputStream.read();
		if (ret == -1)
			throw new EOFException("No byte is available because the end of the stream has been reached");
		return (byte) ret;
	}

	public short readUnsignedByte() throws IOException {
		return (short) (this.readByte() & 0xFF);
	}

	public short readShort() throws IOException {
		return this.readShort(true);
	}

	public short readShort(boolean bigEndian) throws IOException {
		byte b1 = this.readByte();
		byte b2 = this.readByte();
		return Numbers.of(bigEndian, b1, b2).getShort();
	}

	public int readUnsignedShort() throws IOException {
		return this.readUnsignedShort(true);
	}

	public int readUnsignedShort(boolean bigEndian) throws IOException {
		return (int) (this.readShort(bigEndian) & 0xFFFF);
	}

	public int readInt() throws IOException {
		return this.readInt(true);
	}

	public int readInt(boolean bigEndian) throws IOException {
		byte b1 = this.readByte();
		byte b2 = this.readByte();
		byte b3 = this.readByte();
		byte b4 = this.readByte();
		return Numbers.of(bigEndian, b1, b2, b3, b4).getInt();
	}

	public long readUnsignedInt() throws IOException {
		return this.readUnsignedInt(true);
	}

	public long readUnsignedInt(boolean bigEndian) throws IOException {
		return (long) (this.readInt(bigEndian) & 0xFFFFFFFFL);
	}

	public long readLong() throws IOException {
		return this.readLong(true);
	}

	public long readLong(boolean bigEndian) throws IOException {
		byte b1 = this.readByte();
		byte b2 = this.readByte();
		byte b3 = this.readByte();
		byte b4 = this.readByte();
		byte b5 = this.readByte();
		byte b6 = this.readByte();
		byte b7 = this.readByte();
		byte b8 = this.readByte();
		return Numbers.of(bigEndian, b1, b2, b3, b4, b5, b6, b7, b8).getLong();
	}

	public Numbers readNumber(int count) throws IOException {
		return this.readNumber(count, true);
	}

	public Numbers readNumber(int count, boolean bigEndian) throws IOException {
		byte[] data = new byte[count];
		for (int i = 0; i < count; i++)
			data[i] = this.readByte();
		return Numbers.of(bigEndian, data);
	}

	public String readString(int length) throws IOException {
		if (length == 0)
			return "";

		char[] carray = new char[length];
		for (int i = 0; i < length; ++i) {
			carray[i] = this.readChar();
		}
		return new String(carray);
	}

	public String readCString() throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			for (;;) {
				byte b = this.readByte();
				if (b == 0)
					break;
				baos.writeByte(b);
			}
			return baos.toString();
		}
	}

	public int readLength() throws IOException {
		int i = 0, len = 0;
		for (;;) {
			byte b = this.readByte();
			len |= (b & 0x7F) << (7 * i++);

			if ((b & 0x80) == 0x80) {// first bit is 1
				return len;
			}
		}
	}

	public byte[] readPBytes() throws IOException {
		int len = readLength();
		return this.readFullyOrTillTheEnd(len);
	}

	public String readPString() throws IOException {
		byte[] data = readPBytes();
		return new String(data, 0, data.length);
	}

	@Override
	public void close() throws IOException {
		inputStream.close();
	}
}
