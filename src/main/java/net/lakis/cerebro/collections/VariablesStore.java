package net.lakis.cerebro.collections;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;

public class VariablesStore implements Map<String, String> {
	private static final Pattern KEYS_PATERN = Pattern.compile("\\$\\{[^\\{\\}\\$]*\\}");
	private static final Pattern JAVASCRIPT_PATERN = Pattern.compile("\\$\\[[^\\[\\]\\$]*\\]");
	private static final Pattern FUNCTIONS_PATERN = Pattern.compile("\\$\\([^\\(\\)\\$]*\\)");
	private Map<String, String> map;

	public VariablesStore() {
		this(new HashMap<String, String>());
	}

	public VariablesStore(Map<String, String> map) {
		this.map = map;
	}

	public void set(String key, String value) {
		try {
			value = this.formatValue(value);
			this.put(key, value);
		} catch (Exception e) {

		}
	}

	public boolean checkCondition(String condition) {
		condition = this.formatValue(condition);
		return this.evalBoolean(condition);
	}

	public long getAsLong(String key) {
		String obj = this.get(key);
		if (obj == null)
			return 0;

		if (StringUtils.isBlank(obj))
			return 0;

		obj = obj.replaceAll("[^\\-0-9]", "");
		if (StringUtils.isBlank(obj))
			return 0;

		return Long.decode(obj);

	}

	public int getAsInt(String key) {
		String obj = this.get(key);
		if (obj == null)
			return 0;

		if (StringUtils.isBlank(obj))
			return 0;

		obj = obj.replaceAll("[^\\-0-9]", "");
		if (StringUtils.isBlank(obj))
			return 0;

		return Integer.parseInt(obj);

	}

	public Boolean getAsBool(String key) {
		Object obj = this.get(key);
		if (obj == null)
			return false;

		if (obj instanceof Boolean)
			return ((Boolean) obj).booleanValue();

		if (!(obj instanceof String))
			return false;

		String value = (String) obj;
		if (StringUtils.isBlank(value))
			return false;

		return Boolean.parseBoolean(value);

	}

	public String formatValue(String value) {
		while (true) {
			Matcher m = KEYS_PATERN.matcher(value);
			if (!m.find())
				break;
			String subKey = m.group();
			subKey = subKey.substring(2, subKey.length() - 1).trim();
			String[] args = StringUtils.split(subKey, ':');
			String replace = this.getOrDefault(args[0], "");

			if (args.length > 1) {

				if (args.length > 2)
					replace = substring(replace, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				else
					replace = substring(replace, Integer.parseInt(args[1]), replace.length());

			}

			value = value.substring(0, m.start()) + replace //
					+ value.substring(m.end());
		}

		while (true) {
			Matcher m = JAVASCRIPT_PATERN.matcher(value);
			if (!m.find())
				break;
			String subKey = m.group();
			subKey = subKey.substring(2, subKey.length() - 1).trim();

			value = value.substring(0, m.start()) + evalStr(subKey) //
					+ value.substring(m.end());
		}

		while (true) {
			Matcher m = FUNCTIONS_PATERN.matcher(value);
			if (!m.find())
				break;
			String subKey = m.group();
			subKey = subKey.substring(2, subKey.length() - 1).trim();
//			String[] args = subKey.split("\\|+");
			String[] args = StringUtils.split(subKey, '|');

			value = value.substring(0, m.start()) + handleFunctions(args) //
					+ value.substring(m.end());
		}
		return value;
	}

	private String substring(String value, int beginIndex, int endIndex) {
		if (beginIndex > value.length())
			return "";
		if (beginIndex < 0)
			beginIndex = 0;

		if (endIndex <= 0) {
			endIndex = value.length() + endIndex;
		} else {
			endIndex++;
		}

		if (endIndex > value.length())
			endIndex = value.length();

		return value.substring(beginIndex, endIndex);
	}

	private String handleFunctions(String[]  args) {
		try {
			String cmd = args[0].trim().toLowerCase();
			switch (cmd) {
			case "time":
				return String.valueOf(System.currentTimeMillis());
			case "dt": {
				if (args.length == 1)
					return LocalDateTime.now().toString();

				DateTimeFormatter pattern = DateTimeFormatter.ofPattern(args[1]);

				if (args.length == 2)
					return pattern.format(LocalDateTime.now());

				String v = args[2];
				long t;
				if (StringUtils.isNumeric(v)) {
					t = Long.decode(v);
				} else {
					t = this.getAsLong(v);
				}

				if (t <= 0)
					return "";

				Instant instant = Instant.ofEpochMilli(t);

				return pattern.format(LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId()));

			}
			case "this":
				return this.toString();

			}
		} catch (Exception e) {
 		}
		return "";
	}

	private Object eval(String expression) throws ScriptException {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");
		return engine.eval(expression);
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public String evalStr(String expression) {
		try {

			Object ret = eval(expression);
			if (ret == null)
				return "";
			return String.valueOf(ret);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public Number evalInt(String expression) {
		try {

			Object ret = eval(expression);
			if (ret == null || !(ret instanceof Number))
				return 0;
			return (Number) ret;
		} catch (Exception e) {
			return 0;
		}
	}

	private boolean evalBoolean(String condition) {
		try {
			Object ret = eval(condition);
			if (ret == null || !(ret instanceof Boolean))
				return false;
			return ((Boolean) ret).booleanValue();
		} catch (Exception e) {
			return false;
		}
	}

	public void serialize(Object obj) throws IllegalArgumentException, IllegalAccessException {

		Class<?> clazz = obj.getClass();
		serialize(obj, clazz);

	}

	private void serialize(Object object, Class<?> clazz) throws IllegalArgumentException, IllegalAccessException {

		for (Field field : clazz.getDeclaredFields()) {
			if (Modifier.isTransient(field.getModifiers()))
				continue;
			field.setAccessible(true);
			Object value = field.get(object);
			if (value == null)
				continue;
			this.put(field.getName(), String.valueOf(value));
		}

		clazz = clazz.getSuperclass();
		if (clazz != Object.class)
			serialize(object, clazz);

	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public String get(Object key) {
		return map.get(key);
	}

	@Override
	public String put(String key, String value) {
		return map.put(key, value);
	}

	@Override
	public String remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<String> values() {
		return map.values();
	}

	@Override
	public Set<Entry<String, String>> entrySet() {
		return map.entrySet();
	}

}
