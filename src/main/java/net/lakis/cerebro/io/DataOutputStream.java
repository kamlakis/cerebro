package net.lakis.cerebro.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import lombok.Getter;
import net.lakis.cerebro.lang.Numbers;
import net.lakis.cerebro.lang.Strings;
 
public class DataOutputStream implements Closeable {
	private @Getter final OutputStream outputStream;

	public DataOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void writeByte(byte value) throws IOException {
		this.outputStream.write(value);
	}

	public void writeBytes(byte... array) throws IOException {
		this.outputStream.write(array);
	}

	public void writeBytes(byte[] array, int off, int len) throws IOException {
		this.outputStream.write(array, off, len);
	}

	public void writeLine(String value) throws IOException {
		this.writeString(value);
		this.writeByte((byte) 0x0A);
	}

	public void writeWLine(String value) throws IOException {
		this.writeString(value);
		this.writeBytes((byte) 0x0D, (byte) 0x0A);
	}

	public void writeChar(char value) throws IOException {
		this.writeBytes(Character.toString(value).getBytes());
	}

	public void writeChars(char... array) throws IOException {
		this.writeBytes(new String(array).getBytes());
	}

	public void writeChars(char[] array, int off, int len) throws IOException {
		this.writeBytes(new String(array, off, len).getBytes());
	}

	public void writeShort(short value) throws IOException {
		this.writeShort(value, true);
	}

	public void writeShort(short value, boolean bigEndian) throws IOException {
		this.writeBytes(Numbers.of(value).bigEndian(bigEndian).getBytes());
	}

	public void writeInt(int value) throws IOException {
		this.writeInt(value, true);
	}

	public void writeInt(int value, boolean bigEndian) throws IOException {
		this.writeBytes(Numbers.of(value).bigEndian(bigEndian).getBytes());
	}

	public void writeLong(long value) throws IOException {
		this.writeLong(value, true);
	}

	public void writeLong(long value, boolean bigEndian) throws IOException {
		this.writeBytes(Numbers.of(value).bigEndian(bigEndian).getBytes());
	}

	public void writeNumber(long value, int bytesCount) throws IOException {
		this.writeNumber(value, bytesCount, true);
	}

	public void writeNumber(long value, int bytesCount, boolean bigEndian) throws IOException {
		this.writeBytes(Numbers.of(value).bigEndian(bigEndian).bytesCount(bytesCount).getBytes());
	}

	public void writeString(String value) throws IOException {
		if (Strings.isNotEmpty(value))
			this.writeBytes(value.getBytes());
	}

	public void writeCString(String value) throws IOException {
		this.writeString(value);
		this.writeByte((byte) 0x00);
	}

	public void writeLength(long len) throws IOException {
		for (;;) {
			byte b = (byte) (len & 0x7F);

			len = (len - b) >> 7;

			if (len == 0) {
				this.writeByte(b |= 0x80);
				break;
			} else {
				this.writeByte(b);
			}
		}
	}

	public void writePBytes(byte... array) throws IOException {
		if (array != null && array.length > 0) {
			this.writeLength(array.length);
			this.writeBytes(array);
		} else
			this.writeLength(0);
	}

	public void writePString(String value) throws IOException {
		if (Strings.isNotEmpty(value))
			this.writePBytes(value.getBytes());
		else
			this.writeLength(0);
	}

	public void flush() throws IOException {
		this.outputStream.flush();
	}

	@Override
	public void close() throws IOException {
		this.outputStream.close();
	}
}
