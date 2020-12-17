package net.lakis.cerebro.commons;

public class IntegerUtils {

	public static int[] toDigits(int n) {
		if (n == 0)
			return new int[] { 0 };
		int digitsCount = (int) Math.ceil(Math.log10(n + 1));
		int[] ret = new int[digitsCount];

		for (int i = digitsCount; i > 0; i--) {
			ret[i - 1] = n % 10;
			n /= 10;
		}
		return ret;
	}

	public static byte get1stByte(int i) {
		return (byte) ((i >> 24) & 0xFF);

	}

	public static byte get2ndByte(int i) {
		return (byte) ((i >> 16) & 0xFF);

	}

	public static byte get3rdByte(int i) {
		return (byte) ((i >> 8) & 0xFF);

	}

	public static byte get4thByte(int i) {
		return (byte) (i & 0xFF);

	}

	public static byte[] getBytes(int i) {
		return new byte[] { get1stByte(i), get2ndByte(i), get3rdByte(i), get4thByte(i) };
	}

	public static int getInt(byte... data) {
		int ret = 0;
		for (int i = 0; i < data.length; i++) {
			ret = (ret << 8) | data[i] & 0xFF;
		}
		return ret;

	}

}
