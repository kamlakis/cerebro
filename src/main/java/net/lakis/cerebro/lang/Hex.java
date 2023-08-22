package net.lakis.cerebro.lang;

import java.io.IOException;

public class Hex {
	private static final String EOL = System.lineSeparator();
	private static final String HEX_STR = "0123456789abcdef";
	private static final String UPPER_HEX_STR = "0123456789ABCDEF";
	private static final char[] HEX_CODES = HEX_STR.toCharArray();

	/******************* Get Hex String From Integer *******************/

	public static char getLastHexCode(int k) {
		return HEX_CODES[k & 0xf];
	}

	public static char getFirstHexCode(int k) {
		return HEX_CODES[(k >> 4) & 0xf];
	}

//	public static String getHexString(int k) {
//
//		return getHexString(k, null);
//	}
//
//	public static String getHexString(int k, String separator) {
//
//		StringBuilder sb = new StringBuilder();
//		while (k > 0) {
//
//			sb.append(getLastHexCode(k));
//			if (separator != null || k > 0xF)
//				sb.append(getFirstHexCode(k));
//			k = k >> 8;
//			if (separator != null && k > 0)
//				sb.append(separator);
//		}
//		return sb.reverse().toString();
//	}

	public static String getHexString(byte... data) {

		return getHexString(data, null);
	}

	public static String getHexString(byte[] data, String separator) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			sb.append(getFirstHexCode(data[i]));
			sb.append(getLastHexCode(data[i]));

			if (separator != null && i < data.length - 1)
				sb.append(separator);
		}
		return sb.toString();
	}

	private static void getFourBytesHexString(StringBuilder sb, int k) {
		for (byte b : Numbers.of(k).getBytes()) {
			sb.append(getFirstHexCode(b));
			sb.append(getLastHexCode(b));
		}
	}

	public static String getFourBytesHexString(int k) {
		StringBuilder sb = new StringBuilder();
		getFourBytesHexString(sb, k);
		return sb.toString();
	}

	/******************* Get Integer From Hex Code *******************/
	public static int getInt(char h) {
		int ret = HEX_STR.indexOf(h);
		if (ret == -1)
			return UPPER_HEX_STR.indexOf(h);

		return ret;
	}

	public static int getInt(String str) {
		return getInt(str, 0, str.length());
	}

	public static int getInt(String str, int lenght) {
		return getInt(str, 0, lenght);

	}

	public static int getInt(String str, int offset, int lenght) {
		char[] chars = str.toCharArray();
		int ret = 0;
		for (int i = 0; i < lenght; i++) {
			int k = getInt(chars[offset + i]);
			ret = (ret << 4) | k;
		}
		return ret;
	}

	/******************* Get Bytes Array From Hex Code *******************/

	
	public static byte getByte(String str) {
		return getBytes(str, 0, str.length())[0];
	}
	
	public static byte[] getBytes(String str) {
		return getBytes(str, 0, str.length());
	}

	public static byte[] getBytes(String str, int lenght) {
		return getBytes(str, 0, lenght);

	}

	public static byte[] getBytes(String str, int offset, int lenght) {

		byte[] res = new byte[(lenght + 1) / 2];
		int i = 0;
		int j = offset;
		if (lenght % 2 == 1)
			res[i++] = (byte) getInt(str.charAt(j++));

		while (j < lenght + offset) {
			res[i++] = (byte) ((getInt(str.charAt(j++)) << 4) | getInt(str.charAt(j++)));
		}
		return res;
	}

	/******************* Dump Bytes Array *******************/

	public static String dump(byte... data)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IOException {
		return dump(data, 0, data.length);
	}

	public static String dump(byte[] data, final int offset)
			throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
		return dump(data, offset, data.length - offset);
	}

	public static void main(String[] args) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IOException {
		String message = "1234567890qwertyuiop";
		System.out.println(dump(message.getBytes(),0,message.length()));
	}
	public static String dump(byte[] data, final int offset, int length)
			throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
		StringBuilder sb = new StringBuilder(74);

		if (offset < 0 || offset >= data.length) {
			throw new ArrayIndexOutOfBoundsException(
					"illegal offset: " + offset + " into array of length " + data.length);
		}
		if (length < 0 || offset + length > data.length) {
			length = data.length - offset;
		}

		int bytesRead = 0;
		for (int i = offset; i < length + offset; i += 16) {
			int bytesToread = length + offset - i;

			if (bytesToread > 16) {
				bytesToread = 16;
			}

//			String bytesReadStr = Integer.toHexString(bytesRead);
//			for (int j = bytesReadStr.length(); j < 8; j++)
//				buffer.append('0');
//
//			buffer.append(bytesReadStr.toUpperCase());
			getFourBytesHexString(sb, bytesRead);
			sb.append(' ');

			for (int k = 0; k < 16; k++) {
				if (k < bytesToread) {
					sb.append(getFirstHexCode(data[k + i]));
					sb.append(getLastHexCode(data[k + i]));
				} else {
					sb.append("  ");
				}
				sb.append(' ');
			}
			for (int k = 0; k < bytesToread; k++) {

				if (data[k + i] >= ' ' && data[k + i] < 127) {
					sb.append((char) data[k + i]);
				} else {
					sb.append('.');
				}
			}
			sb.append(EOL);
			bytesRead += bytesToread;
		}

		return sb.toString();
	}

	public static String dumpHex(byte... data)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IOException {
		return dumpHex(data, 0, data.length);
	}

	public static String dumpHex(byte[] data, final int offset)
			throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
		return dumpHex(data, offset, data.length - offset);
	}

	public static String dumpHex(byte[] data, final int offset, int length)
			throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
		StringBuilder sb = new StringBuilder(74);

		if (offset < 0 || offset >= data.length) {
			throw new ArrayIndexOutOfBoundsException(
					"illegal offset: " + offset + " into array of length " + data.length);
		}
		if (length < 0 || offset + length > data.length) {
			length = data.length - offset;
		}

		int bytesRead = 0;
		for (int i = offset; i < length + offset; i += 16) {
			int bytesToread = length + offset - i;

			if (bytesToread > 16) {
				bytesToread = 16;
			}

			getFourBytesHexString(sb, bytesRead);
			sb.append(' ');

			for (int k = 0; k < bytesToread; k++) {
				sb.append(getFirstHexCode(data[k + i]));
				sb.append(getLastHexCode(data[k + i]));
				sb.append(' ');
			}
 			sb.append(EOL);
			bytesRead += bytesToread;
		}

		return sb.toString();
	}
}
