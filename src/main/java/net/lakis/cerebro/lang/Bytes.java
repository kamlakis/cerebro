package net.lakis.cerebro.lang;

public class Bytes {

	public static boolean isEmpty(byte[] data) throws ArrayIndexOutOfBoundsException {
		return isEmpty(data, 0, data.length);
	}

	public static boolean isEmpty(byte[] data, final int offset) throws ArrayIndexOutOfBoundsException {
		return isEmpty(data, offset, data.length - offset);
	}

	public static boolean isEmpty(byte[] data, int offset, int length) throws ArrayIndexOutOfBoundsException {
		if (data == null)
			return true;
		if (offset < 0 || offset >= data.length) {
			throw new ArrayIndexOutOfBoundsException(
					"illegal offset: " + offset + " into array of length " + data.length);
		}
		if (length < 0 || offset + length > data.length) {
			length = data.length - offset;
		}

		for (int i = offset; i < length + offset; i++)
			if (data[i] != (byte) 0)
				return false;
		return true;
	}

	public static byte[] reverse(byte[] data) {
		if (data == null || data.length == 0)
			return data;
		byte[] res = new byte[data.length];
		for (int i = data.length - 1; i >= 0; i--)
			res[data.length - i - 1] = data[i];
		return res;
	}

}
