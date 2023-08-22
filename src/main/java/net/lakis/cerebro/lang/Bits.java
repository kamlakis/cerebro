package net.lakis.cerebro.lang;

public class Bits {

	public static String getBitsString(byte[] input) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length; i++) {
			getBitsString(sb, input[i]);
		}
		return sb.toString();
	}
	

	public static String getBitsString(byte input) {
		StringBuilder sb = new StringBuilder();
		getBitsString(sb, input);
		return sb.toString();
	}
	

	private static void getBitsString(StringBuilder sb, byte input) {
		sb.append((input & 0x80) > 0 ? 1 : 0);
		sb.append((input & 0x40) > 0 ? 1 : 0);
		sb.append((input & 0x20) > 0 ? 1 : 0);
		sb.append((input & 0x10) > 0 ? 1 : 0);
		sb.append((input & 0x08) > 0 ? 1 : 0);
		sb.append((input & 0x04) > 0 ? 1 : 0);
		sb.append((input & 0x02) > 0 ? 1 : 0);
		sb.append((input & 0x01) > 0 ? 1 : 0);		
	}



	
	

	public static boolean[] getBitsArray(byte[] input) {
		boolean[] out = new boolean[input.length * 8]; // byte = 8 bits
		for (int i = 0; i < input.length; i++) {
			getBitsArray(input[i], out, i*8);
		}
		return out;
	}

	

	public static boolean[] getBitsArray(byte input) {
		boolean[] out = new boolean[8];
		getBitsArray(input, out, 0);
		return out;
	}
	
	
	private static void getBitsArray(byte input, boolean[] out, int i) {
		out[i] = (input & 0x80) > 0;
		out[i+1] = (input & 0x40) > 0;
		out[i+2] = (input & 0x20) > 0;
		out[i+3] = (input & 0x10) > 0;
		out[i+4] = (input & 0x08) > 0;
		out[i+5] = (input & 0x04) > 0;
		out[i+6] = (input & 0x02) > 0;
		out[i+7] = (input & 0x01) > 0;
	}

	public static int requiredBits(int input) {
		return (int) Math.ceil(Math.log10(input + 1) / Math.log10(2));
	}

	

	public static boolean[] reverse(boolean[] data) {
		boolean[] res = new boolean[data.length];
		for (int i = data.length - 1; i >= 0; i--)
			res[data.length - i - 1] = data[i];
		return res;
	}
}
