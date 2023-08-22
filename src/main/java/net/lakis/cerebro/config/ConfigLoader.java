
package net.lakis.cerebro.config;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.GsonBuilder;

import lombok.extern.log4j.Log4j2;
import net.lakis.cerebro.Cerebro;
import net.lakis.cerebro.annotations.Config;
import net.lakis.cerebro.annotations.ConsoleKey;
import net.lakis.cerebro.annotations.InjectDepency;
import net.lakis.cerebro.collections.VariablesStore;
import net.lakis.cerebro.web.config.ServletNetworkListener;
import net.lakis.cerebro.web.config.WebServerConfig;

@Log4j2
@ConsoleKey("config")
public class ConfigLoader {

	private static final Pattern configFilePattern = Pattern.compile("(.json)|(.properties)$");

	private @InjectDepency Cerebro cerebro;
	private @InjectDepency AppConfig appConfig;

	private ObjectMapper objectMapper;

	void loadJsonMapper() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.setSerializationInclusion(Include.ALWAYS);
		this.objectMapper.disable(SerializationFeature.INDENT_OUTPUT);

		this.objectMapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);

		this.objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		this.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

		this.objectMapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
		this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	}

	@ConsoleKey("reload")
	public String reloadConfig() {
		StringBuilder sb = new StringBuilder();
		if (objectMapper == null)
			this.loadJsonMapper();

		String file = null;
		try {
			Config config = appConfig.getClass().getAnnotation(Config.class);
			file = config.value();

			boolean ret = load(appConfig, file, config.format());
			sb.append(file).append(ret ? " successfully loaded!\n" : " failed to load.\n");
		} catch (Exception e) {
			log.error("Exception", e);

			sb.append(file).append(" failed to load: ").append(e.getMessage()).append("\n");
		}

		for (Entry<Class<?>, Object> entry : cerebro.getConfigs().entrySet()) {
			if (entry.getValue() == appConfig)
				continue;
			Config config = entry.getKey().getAnnotation(Config.class);
			file = config.value();

			try {
				boolean ret = load(entry.getValue(), file, config.format());
				sb.append(file).append(ret ? " successfully loaded!\n" : " failed to load.\n");
			} catch (Exception e) {
				log.error("Exception", e);

				sb.append(file).append(" failed to load: ").append(e.getMessage()).append("\n");
			}
		}
		return sb.toString();

	}
	
	@ConsoleKey("printJson")
	public String printJson() {
		
		Map<String, Object> map = new HashMap<>();
		cerebro.getConfigs().forEach((c,o) -> {
			Config config = c.getAnnotation(Config.class);
			if(config == null)
				map.put(c.getName(), o);
			else 
				map.put(config.value(),o);
		});
				
		return new GsonBuilder().setPrettyPrinting().create().toJson(map);

	}

	private boolean load(Object obj, String file, boolean format) throws IOException, NumberFormatException,
			IllegalArgumentException, IllegalAccessException, URISyntaxException {
		if (!configFilePattern.matcher(file).find()) {
			log.error("unsupported file format: {}", file);
			return false;
		}
		
		/*
		URL url = ConfigLoader.class.getClassLoader().getResource(file);
		if (url == null) {
			if (obj instanceof WebServerConfig)
				return loadWebServerConfigFromApp((WebServerConfig) obj);

			return false;
		}
		byte[] content = Files.readAllBytes(Paths.get(url.toURI()));
*/
		File fi = new File(cerebro.getFilePath("conf", file));
		if(!fi.exists()) {
			if (obj instanceof WebServerConfig)
				return loadWebServerConfigFromApp((WebServerConfig) obj);

			return false;
		}
		
		byte[] content = Files.readAllBytes(fi.toPath());
		String str;
		if (format)
			str = appConfig.formatValue(new String(content));
		else
			str = new String(content);

		if (file.endsWith(".json")) {
			return loadJson(obj, str);
		} else {
			return loadProperties(obj, str);
		}

	}

	private boolean loadWebServerConfigFromApp(WebServerConfig config) {
		int port = appConfig.getAsInt("servlets.port");
		if (port <= 0)
			return false;
		ServletNetworkListener networkListener = new ServletNetworkListener();
		networkListener.setName("server");
		networkListener.setPort(port);

		networkListener.setPortRange(0);
		networkListener.setHost("0.0.0.0");
		networkListener.setMaxPoolSize(100);
		networkListener.setCorePoolSize(10);
		networkListener.setQueueLimit(-1);
		networkListener.setRunnersCount(4);
		networkListener.setReadBufferSize(1000000);
		networkListener.setReuseAddress(true);

		networkListener.setKeyStorePassword(null);
		config.setNetworkListeners(new ServletNetworkListener[] { networkListener });
		config.setEnableAssets(true);
		config.setCacheAssets(true);
		return false;
	}

	public static void main(String[] args) {
		AppConfig config = new AppConfig();
		if (config instanceof VariablesStore) {
			System.out.println("variable store");
		} else {
			System.out.println("not");
		}

	}

	private boolean loadProperties(Object obj, String str)
			throws IOException, NumberFormatException, IllegalArgumentException, IllegalAccessException {
		if (obj instanceof Properties) {
			((Properties) obj).load(new StringReader(str));
			return true;
		}
		Properties props = new Properties();
		props.load(new StringReader(str));

		if (obj instanceof VariablesStore) {
			VariablesStore store = (VariablesStore) obj;
			store.clear();
			for (Entry<Object, Object> entry : props.entrySet()) {
				store.put(entry.getKey().toString(), entry.getValue().toString());
			}
			return true;
		}

		for (Field field : obj.getClass().getDeclaredFields()) {

			String value = (String) props.get(field.getName());
			if (value == null)
				continue;
			value = value.trim();
			if (value.length() == 0)
				continue;

			field.setAccessible(true);
			if (field.getType() == String.class) {

				field.set(obj, value);

			} else if (field.getType() == char.class) {

				field.set(obj, value.charAt(0));

			} else if (field.getType() == boolean.class) {

				field.set(obj,
						"true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value));

			} else if (field.getType() == byte.class) {

				field.set(obj, Byte.parseByte(value));

			} else if (field.getType() == byte[].class) {

				String[] array1 = StringUtils.split(value, '|');
				byte[] array2 = new byte[array1.length];
				for (int i = 0; i < array1.length; i++) {
					array2[i] = Byte.parseByte(array1[i]);
				}
				field.set(obj, array2);

			} else if (field.getType() == short.class) {

				field.set(obj, Short.parseShort(value));

			} else if (field.getType() == short[].class) {

				String[] array1 = StringUtils.split(value, '|');
				short[] array2 = new short[array1.length];
				for (int i = 0; i < array1.length; i++) {
					array2[i] = Short.parseShort(array1[i]);
				}
				field.set(obj, array2);

			} else if (field.getType() == int.class) {

				field.set(obj, Integer.parseInt(value));

			} else if (field.getType() == int[].class) {

				String[] array1 = StringUtils.split(value, '|');
				int[] array2 = new int[array1.length];
				for (int i = 0; i < array1.length; i++) {
					array2[i] = Integer.parseInt(array1[i]);
				}
				field.set(obj, array2);

			} else if (field.getType() == long.class) {

				field.set(obj, Long.parseLong(value));

			} else if (field.getType() == long[].class) {

				String[] array1 = StringUtils.split(value, '|');
				long[] array2 = new long[array1.length];
				for (int i = 0; i < array1.length; i++) {
					array2[i] = Long.parseLong(array1[i]);
				}
				field.set(obj, array2);
			} else if (field.getType() == float.class) {

				field.set(obj, Float.parseFloat(value));

			} else if (field.getType() == float[].class) {

				String[] array1 = StringUtils.split(value, '|');
				float[] array2 = new float[array1.length];
				for (int i = 0; i < array1.length; i++) {
					array2[i] = Float.parseFloat(array1[i]);
				}
				field.set(obj, array2);
			} else if (field.getType() == double.class) {

				field.set(obj, Double.parseDouble(value));

			} else if (field.getType() == double[].class) {

				String[] array1 = StringUtils.split(value, '|');
				double[] array2 = new double[array1.length];
				for (int i = 0; i < array1.length; i++) {
					array2[i] = Double.parseDouble(array1[i]);
				}
				field.set(obj, array2);
			}
		}
		return true;

	}

	private boolean loadJson(Object obj, String str) throws JsonProcessingException, IOException {
		objectMapper.readerForUpdating(obj).readValue(str);
		return false;
	}

}
