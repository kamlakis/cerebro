package net.lakis.cerebro.lang;

import java.util.ArrayList;

class BitsAndBytesUtils {

 
	public static String bytes2IPv4(byte[] data) {
		if (data != null) {
			return Integer.toString(data[0] & 0xff) + "." + Integer.toString(data[1] & 0xff) + "." + Integer.toString(data[2] & 0xff) + "."
					+ Integer.toString(data[3] & 0xff);
		}
		return null;
	}
	

	
	 

	public static byte[] num2bytes(long l) {
		int ttb = (int) Math.ceil(Math.log10((l > 0 ? l : 1) + 1) / Math.log10(2));
		int tl = (int) Math.ceil((double) ttb / 8);
		byte[] res = new byte[tl];

		for (int i = 0; i < tl; i++) {
			res[i] = (byte) (l >> (8 * (tl - i - 1)));
		}
		return res;
	}

	public static long bytes2num(byte[] data) {
		long res = 0;
		if (data != null) {
			int l = data.length;
			for (int i = 0; i < data.length; i++)
				res += (long) (data[i] & 0xFF) << ((l - i - 1) * 8);
		}
		return res;
	}

 
	public static byte[] bits2bytes(boolean[] input) {
		byte[] out = new byte[(int) Math.ceil((double) input.length / 8)];
		int tmp;
		int rc = 0;
		for (int i = 0; i < input.length; i += 8) {
			tmp = bits2int(input, i, 8);
			out[rc] = (byte) tmp;
			rc++;

		}
		return out;
	}

	public static byte[] bits2bytes_reverse(boolean[] input) {
		byte[] out = new byte[(int) Math.ceil((double) input.length / 8)];
		int tmp;
		int rc = 0;
		for (int i = 0; i < input.length; i += 8) {
			tmp = bits2int(input, input.length - i - 8, 8);
			out[rc] = (byte) tmp;
			rc++;

		}
		return out;
	}

	public static int bits2int(boolean[] source, int position, int length) {
		int res = 0;
		int pos;
		// System.out.println("L :" + source.length);
		// System.out.println("P :" + position);
		if (position < 0)
			position = 0;
		for (int i = 0; i < length; i++) {
			// power of 2
			pos = (int) Math.pow(2, length - i - 1);
			// bitwise OR for bit 1, 0 | 1 = 1
			// bitwise AND with inverted bits "~" for bit 0, 0110 & ~1001 = 0110
			// & 0110 = 0110
			if (position + i < source.length) {
				if (source[position + i])
					res = res | pos;
				else
					res = res & ~pos;
			}

		}
		return res;

	}

	public static boolean[] bitsCombine(boolean[] src1, boolean[] src2) {
		int src1_l = (src1 != null ? src1.length : 0);
		int src2_l = (src2 != null ? src2.length : 0);
		boolean[] res = new boolean[src1_l + src2_l];
		if (src1 != null) {
			for (int i = 0; i < src1_l; i++)
				res[i] = src1[i];
		}
		if (src2 != null) {
			for (int i = 0; i < src2_l; i++)
				res[i + src1_l] = src2[i];

		}
		return res;

	}

 

	public static boolean[] int2bits(int input, int max_bit_count) {
		boolean[] out = new boolean[max_bit_count];
		// num of octets
		int bits = (int) Math.ceil(Math.log10(input + 1) / Math.log10(2));
		int bc = bits;
		int pow;
		for (int i = max_bit_count - bits; i < max_bit_count; i++) {
			pow = (int) Math.pow(2, bc - 1);
			out[i] = (input & pow) == pow;
			bc--;
		}
		return out;
	}
 
	
	
	
	
	
	 
 
 
 
	public static byte[] list2array(ArrayList<Byte> input) {
		if (input != null) {
			byte[] res = new byte[input.size()];
			for (int i = 0; i < input.size(); i++)
				res[i] = input.get(i);
			return res;
		}
		return null;
	}
	
	 
}
