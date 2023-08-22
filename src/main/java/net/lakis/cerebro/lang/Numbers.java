package net.lakis.cerebro.lang;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
public class Numbers {
	// Default is Big Indian
	private @Getter @Setter boolean bigEndian = true;
	private long value = 0;
	private @Getter @Setter int bytesCount;

 
	private Numbers() {

	}

	public static Numbers of(byte b) {
		Numbers instance = new Numbers();
		instance.value = b & 0xff;
		instance.bytesCount = 1;
		return instance;
	}

	public static Numbers of(short b) {
		Numbers instance = new Numbers();
		instance.value = b & 0xffff;
		instance.bytesCount = 2;
		return instance;
	}

	public static Numbers of(int b) {
		Numbers instance = new Numbers();
		instance.value = b & 0xffffffff;
		instance.bytesCount = 4;
		return instance;
	}

	public static Numbers of(long b) {
		Numbers instance = new Numbers();
		instance.value = b;
		instance.bytesCount = 8;
		return instance;
	}

	public static Numbers decode(String message, int off, int len) {
		return decode(message.substring(off, off + len));
	}

	public static Numbers decode(String message) {
		long value = Long.decode(message);
		return fromString(value);
	}

	public static Numbers parse(String message, int off, int len) {
		return parse(message, off, len, 10);
	}

	public static Numbers parse(String message) {
		return parse(message, 10);
	}

	public static Numbers parse(String message, int off, int len, int radix) {
		return parse(message.substring(off, off + len), radix);
	}

	public static Numbers parse(String message, int radix) {
		long value = Long.parseLong(message, radix);
		return fromString(value);
	}

	private static Numbers fromString(long value) {
		short bytesCount = 1;
		for (long j = 0xFF; j > 0 && j < value; j = (j + 1) * j + j)
			bytesCount *= 2;

		Numbers instance = new Numbers();
		instance.value = value;
		instance.bytesCount = bytesCount;
		return instance;
	}

	public static Numbers of(byte... data) {
		return of(true, data);
	}

	public static Numbers of(boolean bigEndian, byte... data) {
		long value = 0;
		for (int i = 0; i < data.length; i++) {
			if (bigEndian)
				value = (value << 8) | data[i] & 0xFF;
			else
				value = (value << 8) | data[data.length - i - 1] & 0xFF;
		}

		short bytesCount = 1;
		while (bytesCount < data.length)
			bytesCount *= 2;

		Numbers instance = new Numbers();
		instance.value = value;
		instance.bigEndian = bigEndian;
		instance.bytesCount = bytesCount;
		return instance;
	}
 

	
	
	
	public byte[] toDigits() {
		if (value == 0)
			return new byte[] { 0 };
		int digitsCount = (int) Math.ceil(Math.log10(value + 1));
		byte[] ret = new byte[digitsCount];

		long n = value;
		for (int i = digitsCount; i > 0; i--) {
			ret[i - 1] = (byte) (n % 10);
			n /= 10;
		}
		return ret;
	}

	public byte getByte(int i) {
		if (bigEndian)
			return (byte) ((value >> 8 * (this.bytesCount - i - 1)) & 0xFF);
		else
			return (byte) ((value >> 8 * i) & 0xFF);
	}

	public byte[] getBytes() {
		byte[] bytes = new byte[bytesCount];
		for (int i = 0; i < bytesCount; i++)
			bytes[i] = getByte(i);
		return bytes;
	}

	public long getLong() {
		return value;

	}

	public int getInt() {
		return (int) (value & 0xFFFFFFFF);
	}

	public short getShort() {
		return (short) (value & 0xFFFF);
	}

	public byte getByte() {
		return (byte) (value & 0xFF);
	}

}
