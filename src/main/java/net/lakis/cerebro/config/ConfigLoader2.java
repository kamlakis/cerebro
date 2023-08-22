//
//package net.lakis.cerebro.config;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Field;
//import java.util.Map.Entry;
//import java.util.Properties;
//import java.util.regex.Pattern;
//
//import org.apache.commons.lang.StringUtils;
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.fasterxml.jackson.annotation.JsonInclude.Include;
//import com.fasterxml.jackson.annotation.PropertyAccessor;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.MapperFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//
//import lombok.extern.log4j.Log4j2;
//import net.lakis.cerebro.Cerebro;
//import net.lakis.cerebro.annotations.Config;
//import net.lakis.cerebro.annotations.ConsoleKey;
//import net.lakis.cerebro.annotations.InjectDepency;
//import net.lakis.cerebro.collections.VariablesStore;
//
//@Log4j2
//@ConsoleKey("config")
//public class ConfigLoader2 {
//
//	private static final Pattern configFilePattern = Pattern.compile("(.json)|(.properties)$");
//
//	private @InjectDepency Cerebro cerebro;
//	private @InjectDepency AppConfig appConfig;
//
//	private ObjectMapper objectMapper;
//
//	void loadJsonMapper() {
//		this.objectMapper = new ObjectMapper();
//		this.objectMapper.setSerializationInclusion(Include.ALWAYS);
//		this.objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
//
//		this.objectMapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
//
//		this.objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
//		this.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//
//		this.objectMapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
//		this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//
//	}
//
//	@ConsoleKey("reload")
//	public String reloadConfig() {
//		StringBuilder sb = new StringBuilder();
//		if (objectMapper == null)
//			this.loadJsonMapper();
//
//		for (Entry<Class<?>, Object> entry : cerebro.getConfigs().entrySet()) {
//			if (entry.getValue() == appConfig)
//				continue;
//			String file = entry.getKey().getAnnotation(Config.class).value();
//
//			try {
//				boolean ret = load(entry.getValue(), file);
//				sb.append(file).append(ret ? " successfully loaded!\n" : " failed to load.\n");
//			} catch (Exception e) {
//				log.error("Exception", e);
//
//				sb.append(file).append(" failed to load: ").append(e.getMessage()).append("\n");
//			}
//		}
//		return sb.toString();
//
//	}
//
//	private boolean load(Object t, String file)
//			throws IOException, NumberFormatException, IllegalArgumentException, IllegalAccessException {
//		if (!configFilePattern.matcher(file).find()) {
//			log.error("unsupported file format: {}", file);
//			return false;
//		}
//
//		try (InputStream input = ConfigLoader2.class.getClassLoader().getResourceAsStream(file)) {
//			if (input == null) {
//				return false;
//			}
//			if (file.endsWith(".json")) {
//				return loadJson(t, input);
//			} else {
//				return loadProperties(t, input);
//			}
//		}
//	}
//
//	public static void main(String[] args) {
//		AppConfig config = new AppConfig();
//		if (config instanceof VariablesStore) {
//			System.out.println("variable store");
//		} else {
//			System.out.println("not");
//		}
//
//	}
//
//	private boolean loadProperties(Object obj, InputStream input)
//			throws IOException, NumberFormatException, IllegalArgumentException, IllegalAccessException {
//		if (obj instanceof Properties) {
//			((Properties) obj).load(input);
//			return true;
//		}
//		Properties props = new Properties();
//		props.load(input);
//
//		if (obj instanceof VariablesStore) {
//			VariablesStore store = (VariablesStore) obj;
//			store.clear();
//			for (Entry<Object, Object> entry : props.entrySet()) {
//				store.put(entry.getKey().toString(), entry.getValue().toString());
//			}
//			return true;
//		}
//
//		for (Field field : obj.getClass().getDeclaredFields()) {
//
//			String value = (String) props.get(field.getName());
//			if (value == null)
//				continue;
//			value = value.trim();
//			if (value.length() == 0)
//				continue;
//
//			field.setAccessible(true);
//			if (field.getType() == String.class) {
//
//				field.set(obj, value);
//
//			} else if (field.getType() == char.class) {
//
//				field.set(obj, value.charAt(0));
//
//			} else if (field.getType() == boolean.class) {
//
//				field.set(obj,
//						"true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value));
//
//			} else if (field.getType() == byte.class) {
//
//				field.set(obj, Byte.parseByte(value));
//
//			} else if (field.getType() == byte[].class) {
//
//				String[] array1 = StringUtils.split(value, '|');
//				byte[] array2 = new byte[array1.length];
//				for (int i = 0; i < array1.length; i++) {
//					array2[i] = Byte.parseByte(array1[i]);
//				}
//				field.set(obj, array2);
//
//			} else if (field.getType() == short.class) {
//
//				field.set(obj, Short.parseShort(value));
//
//			} else if (field.getType() == short[].class) {
//
//				String[] array1 = StringUtils.split(value, '|');
//				short[] array2 = new short[array1.length];
//				for (int i = 0; i < array1.length; i++) {
//					array2[i] = Short.parseShort(array1[i]);
//				}
//				field.set(obj, array2);
//
//			} else if (field.getType() == int.class) {
//
//				field.set(obj, Integer.parseInt(value));
//
//			} else if (field.getType() == int[].class) {
//
//				String[] array1 = StringUtils.split(value, '|');
//				int[] array2 = new int[array1.length];
//				for (int i = 0; i < array1.length; i++) {
//					array2[i] = Integer.parseInt(array1[i]);
//				}
//				field.set(obj, array2);
//
//			} else if (field.getType() == long.class) {
//
//				field.set(obj, Long.parseLong(value));
//
//			} else if (field.getType() == long[].class) {
//
//				String[] array1 = StringUtils.split(value, '|');
//				long[] array2 = new long[array1.length];
//				for (int i = 0; i < array1.length; i++) {
//					array2[i] = Long.parseLong(array1[i]);
//				}
//				field.set(obj, array2);
//			} else if (field.getType() == float.class) {
//
//				field.set(obj, Float.parseFloat(value));
//
//			} else if (field.getType() == float[].class) {
//
//				String[] array1 = StringUtils.split(value, '|');
//				float[] array2 = new float[array1.length];
//				for (int i = 0; i < array1.length; i++) {
//					array2[i] = Float.parseFloat(array1[i]);
//				}
//				field.set(obj, array2);
//			} else if (field.getType() == double.class) {
//
//				field.set(obj, Double.parseDouble(value));
//
//			} else if (field.getType() == double[].class) {
//
//				String[] array1 = StringUtils.split(value, '|');
//				double[] array2 = new double[array1.length];
//				for (int i = 0; i < array1.length; i++) {
//					array2[i] = Double.parseDouble(array1[i]);
//				}
//				field.set(obj, array2);
//			}
//		}
//		return true;
//
//	}
//
//	private boolean loadJson(Object t, InputStream input) throws JsonProcessingException, IOException {
//		objectMapper.readerForUpdating(t).readValue(input);
//		return false;
//	}
//
//}
