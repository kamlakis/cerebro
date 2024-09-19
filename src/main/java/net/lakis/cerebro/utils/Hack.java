package net.lakis.cerebro.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Hack {
	public static Field getField(Class<?> clazz, String attribue) throws NoSuchFieldException {
		Field field = null;
		while (true) {
			try {
				field = clazz.getDeclaredField(attribue);
				break;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
				if (clazz == Object.class)
					throw e;
			}
		}
		field.setAccessible(true);
		return field;
	}

	public static Field getField(Object object, String attribute) throws NoSuchFieldException {
		return getField(object.getClass(), attribute);
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		Method method = clazz.getDeclaredMethod(name, parameterTypes);
		method.setAccessible(true);

		return method;
	}

	public static Method getMethod(Object object, String name, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		return getMethod(object.getClass(), name, parameterTypes);
	}

	public static Object invoke(Object object, String name, Object... params) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?>[] classes = new Class<?>[params.length];
		return getMethod(object, name, classes).invoke(object, params);
	}

	public static void set(Object object, String attribue, Object value)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getField(object.getClass(), attribue);
		field.set(object, value);
	}

	public static Object get(Object object, String attribue)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getField(object.getClass(), attribue);
		return field.get(object);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Object object, String attribue, Class<T> clazz)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		return (T) get(object, attribue);
	}

	public static void set(Class<?> clazz, String attribue, Object value)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getField(clazz, attribue);
		field.set(null, value);
	}

	public static Object get(Class<?> clazz, String attribue)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getField(clazz, attribue);
		return field.get(null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Class<?> clazz, String attribue, Class<T> clazz2F)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		return (T) get(clazz, attribue);
	}

	public static String listAttributes(Object object) throws IllegalArgumentException, IllegalAccessException {
		StringBuilder sb = new StringBuilder();
		Class<?> clazz = object.getClass();
		listAttributes(sb, object, clazz);
		return sb.toString();

	}

	private static void listAttributes(StringBuilder sb, Object object, Class<?> clazz)
			throws IllegalArgumentException, IllegalAccessException {

		sb.append(clazz.getName());
		sb.append(":\n\n");

		for (Field field : clazz.getDeclaredFields()) {
			int mod = field.getModifiers();
			if (mod != 0) {
				sb.append(Modifier.toString(mod));
				sb.append(" ");

			}
			sb.append(field.getType().getTypeName());
			sb.append(" ");
			sb.append(field.getName());
			sb.append(" = ");
			field.setAccessible(true);
			sb.append(field.get(object));
			sb.append(";\n");
		}
		sb.append("\n\n");

		clazz = clazz.getSuperclass();
		if (clazz != Object.class)
			listAttributes(sb, object, clazz);

	}

	public static String listAttributes(Class<?> clazz) throws IllegalArgumentException, IllegalAccessException {
		StringBuilder sb = new StringBuilder();
		listAttributes(sb, clazz);
		return sb.toString();

	}

	private static void listAttributes(StringBuilder sb, Class<?> clazz)
			throws IllegalArgumentException, IllegalAccessException {

		sb.append(clazz.getName());
		sb.append(":\n\n");

		for (Field field : clazz.getDeclaredFields()) {
			int mod = field.getModifiers();
			if (mod != 0) {
				sb.append(Modifier.toString(mod));
				sb.append(" ");

			}
			sb.append(field.getType().getTypeName());
			sb.append(" ");
			sb.append(field.getName());
			sb.append(";\n");
		}
		sb.append("\n\n");

		clazz = clazz.getSuperclass();
		if (clazz != Object.class)
			listAttributes(sb, clazz);

	}

}
