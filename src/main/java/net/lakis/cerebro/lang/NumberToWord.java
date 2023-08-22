package net.lakis.cerebro.lang;

public class NumberToWord {
	private static String TEN_TO_NINETEEN[] = { //
			"ten", "eleven", "twelve", //
			"thirteen", "fourteen", "fifteen", //
			"sixteen", "seventeen", "eighteen", //
			"nineteen" };

	private static String ZERO_TO_NINTY[] = { //
			"zero", "ten", "twenty", //
			"thirty", "fourty", "fifty", //
			"sixty", "seventy", "eighty", //
			"ninety" };

	private static String ZERO_TO_NINE[] = { //
			"zero", "one", "two", //
			"three", "four", "five", //
			"six", "seven", "eight", //
			"nine" };

	public static String convert(int in) {
		return convert(String.valueOf(in));
	}

	public static String convert(String in) {
		return new NumberToWord(in).convert().toString();
	}

	private StringBuilder sb;
	private char[] chars;

	private NumberToWord(String in) {
		this.sb = new StringBuilder();
		this.chars = in.toCharArray();
	}

	private boolean isNumeric(int i) {
		if (i < 0 || i >= chars.length)
			return false;
		return chars[i] >= '0' && chars[i] <= '9';
	}

	private NumberToWord convert() {

		int i = 0;
		while (i < chars.length) {
			if (!isNumeric(i)) {
				sb.append(chars[i++]);
				continue;
			}
			int j = i + 1;
			while (isNumeric(j)) {
				j++;
			}
			convertDigits(i, j - i);

			i = j;
			if (i >= chars.length)
				break;

			if (chars[i] == '.' && isNumeric(i + 1)) {
				sb.append(" dot ");
				i++;
			}

			if (chars[i] == ',' && isNumeric(i + 1)) {
				sb.append(" comma ");
				i++;
			}

		}

		return this;
	}

	private boolean convertDigits(int offset, int length) {
		while (length > 0 && chars[offset] == '0') {
			offset++;
			length--;
		}
		if (length == 0) {
			zeroToNine('0');
			return true;
		}

		if (length > 12) {
			for (int i = offset; i < length; i++) {
				if (i > offset)
					sb.append(' ');
				zeroToNine(i);
			}

			return true;
		}

		if (length > 9) {
			if (zeroToNineHundredNintynine(offset, length - 9)) {
				sb.append(" billion ");
			}
			offset = offset + length - 9;
			length = 9;
		}
		if (length > 6) {
			if (zeroToNineHundredNintynine(offset, length - 6)) {
				sb.append(" million ");
			}
			offset = offset + length - 6;
			length = 6;
		}
		if (length > 3) {
			if (zeroToNineHundredNintynine(offset, length - 3)) {
				sb.append(" thousand ");
			}
			offset = offset + length - 3;
			length = 3;
		}

		zeroToNineHundredNintynine(offset, length);

		return true;

	}

	private boolean zeroToNineHundredNintynine(int offset, int length) {
		while (length > 0 && chars[offset] == '0'  ) {
			offset++;
			length--;
		}

		if (length == 0)
			return false;

		if (length == 1)
			return zeroToNine(offset);
		else if (length == 2)
			return tenToNintynine(offset);
		zeroToNine(offset);
		sb.append(" hundred ");
		if (chars[offset + 1] == '0') {
			if (chars[offset + 2] != '0')
				zeroToNine(offset + 2);
			return true;
		}

		tenToNintynine(offset + 1);
		return true;
	}

	private boolean zeroToNine(int i) {
		return zeroToNine(chars[i]);
	}

	private boolean zeroToNine(char c) {
		sb.append(ZERO_TO_NINE[c - '0']);

		return true;
	}

	private boolean tenToNintynine(int i) {
		return tenToNintynine(chars[i], chars[i + 1]);
	}

	private boolean tenToNintynine(char c1, char c2) {
		if (c1 == '1') {
			sb.append(TEN_TO_NINETEEN[c2 - '0']);
			return true;
		}

		sb.append(ZERO_TO_NINTY[c1 - '0']);

		if (c2 != '0') {
			sb.append("-");
			zeroToNine(c2);
		}

		return true;
	}

	@Override
	public String toString() {
		return sb.toString();
	}

}
