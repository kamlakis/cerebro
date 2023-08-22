package net.lakis.cerebro.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Table {

	List<String[]> list;
	private int[] maxLength;

	public Table() {
		this.list = new ArrayList<String[]>();
		this.maxLength = new int[0];
	}

	public void addRow(Object... objects) {
		if (objects.length > maxLength.length) {

			if (maxLength != null) {
				maxLength = Arrays.copyOf(maxLength, objects.length);
			} else {
				maxLength = new int[objects.length];
			}

		}
		String[] strings = new String[objects.length];

		for (int i = 0; i < strings.length; i++) {
			String str = objects[i].toString();

			maxLength[i] = Math.max(maxLength[i], str.length());
			strings[i] = str;
		}

		list.add(strings);

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("+");

		for (int i = 0; i < maxLength.length; i++) {
			for (int j = 0; j < maxLength[i] + 2; j++) {
				sb.append("-");
			}
			sb.append("+");
		}
		String dashes = sb.toString();
		sb = new StringBuilder();

		sb.append(dashes);
		sb.append(System.lineSeparator());

		for (String[] row : list) {
			sb.append("|");
			for (int i = 0; i < maxLength.length; i++) {

				center(sb, i < row.length ? row[i] : null, maxLength[i] + 2);
				sb.append("|");
			}
			sb.append(System.lineSeparator());

			sb.append(dashes);

			sb.append(System.lineSeparator());
		}

		return sb.toString();
	}

 

	public static void center(StringBuilder sb, String s, int size) {
		int i = 0;
		if (s == null || s.length() == 0) {
			while (i++ < size) {
				sb.append(' ');
			}
			return;
		}

		char[] chars = s.toCharArray();
		if (chars.length >= size) {

			while (i < size) {
				sb.append(chars[i++]);
			}
			return;
		}

		int half = (size - s.length()) / 2;

		for (; i < half; i++) {
			sb.append(' ');
		}

		for (int j = 0; j < chars.length; j++, i++) {
			sb.append(chars[j]);
		}

		for (; i < size; i++) {
			sb.append(' ');
		}

	}

}
