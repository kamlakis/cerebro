package net.lakis.cerebro.commons;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtils {

 

	private static Field getPrivateField(Class<?> clazz, String attribue) throws NoSuchFieldException {
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

	public static void set(Object object, String attribue, Object value)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getPrivateField(object.getClass(), attribue);
		field.set(object, value);
	}

	public static Object get(Object object, String attribue)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getPrivateField(object.getClass(), attribue);
		return field.get(object);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Object object, String attribue, Class<T> clazz)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		return (T) get(object, attribue);
	}
	
	
	
	public static void set(Class<?> clazz, String attribue, Object value)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getPrivateField(clazz, attribue);
		field.set(null, value);
	}

	public static Object get(Class<?> clazz, String attribue)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getPrivateField(clazz, attribue);
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
