package net.lakis.cerebro.lang;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

public class Strings {

	public static String replaceProperties(final String string) {
		return replaceProperties(string, System.getProperties());
	}

	public static String replaceProperties(final String string, final Properties props) {
		StringBuilder sb = new StringBuilder();
		StringBuilder key = new StringBuilder();

		final char[] c = string.toCharArray();
		int i = 0;
		boolean inBrackets = false;

		while (i < c.length) {
			if (!inBrackets) {
				if (i > c.length - 3 || c[i] != '$' || c[i + 1] != '{') {
					sb.append(c[i++]);
				} else if (c[i + 2] == '}') {
					sb.append("${}");
					i += 3;
				} else {
					inBrackets = true;
					i += 2;
				}
			} else {
				if (c[i] == '}') {
					sb.append(getValue(props, key.toString()));
					i++;
					inBrackets = false;
					key.setLength(0);
				} else {
					key.append(c[i++]);
				}
			}
		}

		if (key.length() > 0)
			sb.append(key);

		return sb.toString();

	}

	private static String getValue(Properties props, String key) {
		Arguments args = split(':', key);
		Arguments keys = split(',', args.getString());

		while (keys.remaining() > 0) {
			key = keys.getString();
			if (props.containsKey(key))
				return props.getProperty(key);
		}

		return args.getString();
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static boolean equals(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equals(str2);
	}

	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
	}

	public static Joiner joiner(char seperator) {
		return new Joiner(seperator);
	}

	public static String join(char seperator, String... strings) {
		if (strings.length == 0)
			return "";
		Joiner joiner = new Joiner(seperator);
		for (String str : strings)
			joiner.add(str);
		return joiner.toString();
	}

	public static String join(char seperator, Object... objects) {
		if (objects.length == 0)
			return "";
		Joiner joiner = new Joiner(seperator);
		joiner.add(objects);
		return joiner.toString();
	}

	public static class Joiner {
		private char seperator;
		private StringBuilder sb;

		public Joiner(char seperator) {
			this.seperator = seperator;
		}

		public Joiner add(Object... objs) {
			for (Object obj : objs)
				add(obj);
			return this;
		}

		public Joiner add(Object obj) {
			String value;
			if (obj == null)
				value = "";
			else if (obj instanceof byte[])
				value = Base64.getEncoder().encodeToString((byte[]) obj);
			else if (obj instanceof Boolean)
				value = (Boolean) obj ? "1" : "0";
			else
				value = obj.toString();

			if (sb == null) {
				sb = new StringBuilder(value);
			} else {
				sb.append(seperator);
				sb.append(value);
			}
			return this;
		}

		@Override
		public String toString() {
			return sb == null ? "" : sb.toString();
		}
	}

	public static Arguments split(char seperator, String str) {
		return split(seperator, str, true);
	}

	public static Arguments split(char seperator, String str, boolean preserveAllTokens) {
		List<String> list = new ArrayList<String>();
		if (isBlank(str)) {
			return new Arguments(list);
		}
		int len = str.length();
		int i = 0, start = 0;
		boolean match = false;
		boolean lastMatch = false;
		while (i < len) {
			if (str.charAt(i) == seperator) {
				if (match || preserveAllTokens) {
					list.add(str.substring(start, i));
					match = false;
					lastMatch = true;
				}
				start = ++i;
				continue;
			}
			lastMatch = false;
			match = true;
			i++;
		}
		if (match || (preserveAllTokens && lastMatch)) {
			list.add(str.substring(start, i));
		}
		return new Arguments(list);
	}

	public static Arguments parseArgs(String line) {

		return parseArgs(line, false);
	}

	public static Arguments parseArgs(String line, boolean keepQuotes) {
		char[] chars = line.toCharArray();
		List<String> list = new ArrayList<String>();
		State state = State.noQuote;
		StringBuilder sb = new StringBuilder();
		State lstate = State.noQuote;
		for (char c : chars) {
			switch (state) {
			case noQuote:
				if (Character.isWhitespace(c)) {
					if (sb.length() > 0) {
						list.add(sb.toString());
						sb = new StringBuilder();
					}
					continue;
				}
				if (c == '\'') {
					lstate = State.noQuote;
					state = State.singleQuote;
					if (keepQuotes)
						sb.append(c);
				} else if (c == '"') {
					lstate = State.noQuote;
					state = State.doubleQuote;
					if (keepQuotes)
						sb.append(c);
				} else if (c == '\\')
					state = State.backSlash;
				else
					sb.append(c);
				break;

			case backSlash:
				switch (c) {
				case 'n':
					sb.append('\n');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 't':
					sb.append('\t');
					break;
				case '"':
					sb.append('"');
					break;
				case '\'':
					sb.append('\'');
					break;
				case ' ':
					sb.append(' ');
					break;
				case '\\':
					sb.append('\\');
					break;
				default:// this is a mistake \ should not occurs
					sb.append(c);
					break;
				}
				state = State.noQuote;
				break;

			case singleQuote:
				if (c == '\'') {
					if (lstate == State.noQuote)// this is an empty string
						list.add("");
					state = State.noQuote;
					if (keepQuotes)
						sb.append(c);
				} else if (c == '\\')
					state = State.singleQuoteBackSlash;
				else
					sb.append(c);
				lstate = State.singleQuote;

				break;

			case singleQuoteBackSlash:// only single quote and backslash need to be parsed
				switch (c) {
				case '\'':
					sb.append('\'');
					break;
				case '\\':
					sb.append('\\');
					break;
				default:// this is a mistake \ should not occurs
					sb.append(c);
					break;
				}
				state = State.singleQuote;
				break;

			case doubleQuote:
				if (c == '"') {
					if (lstate == State.noQuote)// this is an empty string
						list.add("");

					state = State.noQuote;
					if (keepQuotes)
						sb.append(c);
				} else if (c == '\\')
					state = State.doubleQuoteBackSlash;
				else
					sb.append(c);
				lstate = State.doubleQuote;

				break;

			case doubleQuoteBackSlash:// end line, double quote and backslash need to be parsed
				switch (c) {
				case 'n':
					sb.append('\n');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 't':
					sb.append('\t');
					break;
				case '"':
					sb.append('"');
					break;
				case '\\':
					sb.append('\\');
					break;
				default:// this is a mistake \ should not occurs
					sb.append(c);
					break;
				}
				state = State.doubleQuote;
				break;
			}
		}
		if (sb.length() > 0) {
			list.add(sb.toString());
		}
		return new Arguments(list);

	}

	public static Arguments getArgs(String line) {
		return getArgs(line, false);
	}

	public static Arguments getArgs(String line, boolean keepQuotes) {
		char[] chars = line.toCharArray();
		List<String> list = new ArrayList<String>();
		State state = State.noQuote;
		StringBuilder sb = new StringBuilder();
		for (char c : chars) {
			switch (state) {

			case singleQuote:
				if (c == '\'') {
					state = State.noQuote;
					if (keepQuotes)
						sb.append(c);
				} else
					sb.append(c);
				break;

			case doubleQuote:
				if (c == '"') {
					state = State.noQuote;
					if (keepQuotes)
						sb.append(c);
				} else
					sb.append(c);
				break;

			default:
				if (Character.isWhitespace(c)) {
					if (sb.length() > 0) {
						list.add(sb.toString());
						sb = new StringBuilder();
					}
					continue;
				}
				if (c == '\'') {
					state = State.singleQuote;
					if (keepQuotes)
						sb.append(c);
				} else if (c == '"') {
					state = State.doubleQuote;
					if (keepQuotes)
						sb.append(c);
				} else
					sb.append(c);
				break;
			}

		}
		if (sb.length() > 0) {
			list.add(sb.toString());
		}
		return new Arguments(list);

	}

	private enum State {
		noQuote, backSlash, singleQuote, singleQuoteBackSlash, doubleQuote, doubleQuoteBackSlash
	}

	public static class Arguments {
		private List<String> list;
		private int idx;

		private Arguments(List<String> list) {
			this.list = list;
			this.idx = 0;
		}

		public void rewind() {
			this.idx = 0;
		}

		public int size() {
			return list.size();
		}

		public int remaining() {
			return list.size() - idx;
		}

		public boolean getBoolean() {
			return this.getBoolean(idx++);
		}

		public boolean getBoolean(int i) {
			if (i >= size())
				return false;

			return list.get(i).charAt(0) == '1' || Boolean.parseBoolean(list.get(i));
		}

		public byte getByte() {
			return this.getByte(idx++);
		}

		public byte getByte(int i) {
			if (i >= size())
				return 0;
			return Byte.decode(list.get(i));
		}

		public short getShort() {
			return this.getShort(idx++);
		}

		public short getShort(int i) {
			if (i >= size())
				return 0;
			return Short.decode(list.get(i));
		}

		public int getInt() {
			return this.getInt(idx++);
		}

		public int getInt(int i) {
			if (i >= size())
				return 0;
			return Integer.decode(list.get(i));
		}

		public long getLong() {
			return this.getLong(idx++);
		}

		public long getLong(int i) {
			if (i >= size())
				return 0;
			return Long.decode(list.get(i));
		}

		public float getFloat() {
			return this.getFloat(idx++);
		}

		public float getFloat(int i) {
			if (i >= size())
				return 0;
			return Float.parseFloat(list.get(i));
		}

		public double getDouble() {
			return this.getDouble(idx++);
		}

		public double getDouble(int i) {
			if (i >= size())
				return 0;
			return Double.parseDouble(list.get(i));
		}

		public String getString() {
			return this.getString(idx++);
		}

		public String getString(int i) {
			if (i >= size())
				return "";
			return list.get(i);
		}

		public byte[] getBytes() {
			return this.getBytes(idx++);
		}

		public byte[] getBytes(int i) {
			if (i >= size())
				return new byte[0];
			return Base64.getDecoder().decode(list.get(i));
		}

		public String[] getArray() {
			String[] ret = new String[size() - idx];

			for (int i = 0; i < ret.length; i++) {
				ret[i] = this.getString();
			}
			return ret;
		}

		public List<String> asList() {
			return this.list;
		}

	}

	public static byte[] getBytesUtf8(String string) {
		return getBytes(string, StandardCharsets.UTF_8);
	}

	public static boolean isNumeric(String string) {
		for (char c : string.toCharArray()) 
			if (c < '0' || c > '9')
				return false;
		
		return true;
	}

	/**
	 * Calls {@link String#getBytes(Charset)}
	 *
	 * @param string  The string to encode (if null, return null).
	 * @param charset The {@link Charset} to encode the {@code String}
	 * @return the encoded bytes
	 */
	private static byte[] getBytes(final String string, final Charset charset) {
		if (string == null) {
			return null;
		}
		return string.getBytes(charset);
	}

}
