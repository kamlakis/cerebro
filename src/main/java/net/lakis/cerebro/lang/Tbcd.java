package net.lakis.cerebro.lang;

public class Tbcd {

	private final static String TBCDSymbolString = "0123456789*#abcF";
	private final static char[] TBCDSymbols = TBCDSymbolString.toCharArray();
	private boolean odd = false;
	private byte filler = 0xf;
	private byte[] tbcd;
	private int start;
	private int length;
	private String number;

	private Tbcd(byte[] tbcd, int start, int length) {
		this.tbcd = tbcd;
		this.start = start;
		this.length = length;
	}

	private Tbcd(String number) {
		this.number = number;
	}

	public static Tbcd from(byte[] tbcd, int start, int length) {
		return new Tbcd(tbcd, start, length);
	}

	public static Tbcd from(byte[] tbcd, int start) {
		return new Tbcd(tbcd, start, tbcd.length - start);

	}

	public static Tbcd from(byte[] tbcd) {
		return new Tbcd(tbcd, 0, tbcd.length);

	}

	public static Tbcd from(String tbcd) {
		return new Tbcd(tbcd);

	}

	public Tbcd odd(boolean odd) {
		this.odd = odd;
		return this;
	}

	public Tbcd filler(byte filler) {
		this.filler = filler;
		return this;
	}

	@Override
	public String toString() {

		if (number == null) {
			if (tbcd == null)
				return "";
			if (start + length >= tbcd.length)
				length = tbcd.length - start;

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < length; i++) {
				int n1 = (tbcd[start + i] & 0xF0) >> 4;
				int n2 = tbcd[start + i] & 0x0F;

				sb.append(TBCDSymbols[n2]);
				if (i == length - 1 && this.odd)
					break;

				sb.append(TBCDSymbols[n1]);

			}
			number = sb.toString();
		}
		return number;
	}

	public byte[] toBytes() {
		if (tbcd == null) {
			int length = (number == null ? 0 : number.length());
			if (length == 0)
				return new byte[0];

			this.odd = length % 2 == 1;

			this.tbcd = new byte[(length + 1) / 2];

			int i = 0;
			int j = 0;
			while (i < length) {
				int n2 = this.getTbcdSymbol(i++);
				int n1 = this.getTbcdSymbol(i++);
				tbcd[j++] = (byte) (((n1 << 4) & 0xF0) | (n2 & 0x0F));
			}

		}
		return tbcd;

	}

	private int getTbcdSymbol(int pos) {

		if (pos == number.length())
			return this.filler;

		int ret = TBCDSymbolString.indexOf(number.charAt(pos));
		if (ret < 0)
			throw new NumberFormatException("Bad character '" + number.charAt(pos) + "' at position " + pos);
		return ret;
	}
}