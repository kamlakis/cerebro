package net.lakis.cerebro.cli;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.lakis.cerebro.annotations.ConsoleKey;
import net.lakis.cerebro.annotations.ConsoleUsage;
import net.lakis.cerebro.lang.Strings;
import net.lakis.cerebro.lang.Strings.Arguments;

public class ConsoleHandler {
	private Map<String, Method> map;
	private Object obj;
	private String name;

	public ConsoleHandler(String name, Class<? extends Object> class1, Object obj) {
		this.name = name;
		this.obj = obj;
		this.map = fetchMethods(class1);

	}

	private Map<String, Method> fetchMethods(Class<? extends Object> class1) {
		map = new HashMap<String, Method>();
		for (Method method : class1.getMethods()) {

			ConsoleKey consoleKey = method.getAnnotation(ConsoleKey.class);

			if (consoleKey == null)
				continue;

			map.put(consoleKey.value(), method);
		}
		return map;
	}

	public boolean hasMethods() {
		return !map.isEmpty();
	}

	public Set<String> getkeys() {
		return map.keySet();
	}

	public String handle(ConsoleWriter consoleWriter, Arguments args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String func = args.getString();
		if (Strings.isBlank(func)) {
			return this.handleNoMethodSelected(null);
		}

		Method method = map.get(func);
		if (method == null) {
			return this.handleNoMethodSelected(func);
		}

		Class<?>[] params = method.getParameterTypes();

		int count = params.length == 0 ? 0 : params[0] == ConsoleWriter.class ? params.length - 1 : params.length;

		if (count != args.remaining()) {
			String usageStr = null;
			ConsoleUsage usage = method.getAnnotation(ConsoleUsage.class);
			if (usage != null)
				usageStr = usage.value();

			return this.handleInvalidParametersCount(func, count, usageStr);
		}

		return this.handle(consoleWriter, method, params, args);
	}

	private String handle(ConsoleWriter consoleWriter, Method method, Class<?>[] params, Arguments args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] parameters = new Object[params.length];

		for (int i = 0; i < parameters.length; i++) {
			if (params[i] == ConsoleWriter.class)
				parameters[i] = consoleWriter;
			else if (params[i] == byte.class)
				parameters[i] = args.getByte();
			else if (params[i] == short.class)
				parameters[i] = args.getShort();
			else if (params[i] == int.class)
				parameters[i] = args.getInt();
			else if (params[i] == long.class)
				parameters[i] = args.getLong();
			else if (params[i] == float.class)
				parameters[i] = args.getFloat();
			else if (params[i] == double.class)
				parameters[i] = args.getDouble();
			else if (params[i] == char.class)
				parameters[i] = args.getString().charAt(0);
			else if (params[i] == boolean.class)
				parameters[i] = args.getBoolean();
			else
				parameters[i] = args.getString();
		}

		if (params.length > 0 && params[0] == ConsoleWriter.class) {
			method.invoke(obj, parameters);
		} else {
			return (String) method.invoke(obj, parameters);
		}
		return null;
	}

	private String handleInvalidParametersCount(String func, int count, String usage) {

		StringBuilder sb = new StringBuilder(name);
		sb.append(" ");
		sb.append(func);

		if (count != 1)
			sb.append(" requires ").append(count).append(" parameters\n");
		else
			sb.append(" requires 1 parameter\n");

		if (usage != null) {

			sb.append("\nUsage: ").append(name).append(" ").append(func).append(" ").append(usage).append("\n");
		}
		return sb.toString();
	}

	private String handleNoMethodSelected(String func) {

		StringBuilder sb = new StringBuilder();
		if (func != null) {
			sb.append("invalid method: ").append(func).append("\n\n");
		}
		sb.append(name).append(" requires a method: \n");

		map.keySet().forEach(s -> sb.append(s).append(' '));
		sb.append('\n');

		return sb.toString();
	}

}
