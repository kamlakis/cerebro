
package net.lakis.cerebro;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.util.Strings;
import org.reflections.Reflections;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.lakis.cerebro.annotations.Config;
import net.lakis.cerebro.annotations.ConsoleKey;
import net.lakis.cerebro.annotations.ExecuteFirst;
import net.lakis.cerebro.annotations.ExecuteLast;
import net.lakis.cerebro.annotations.ExecuteSecond;
import net.lakis.cerebro.annotations.ExecuteThird;
import net.lakis.cerebro.annotations.InjectDepency;
import net.lakis.cerebro.annotations.Service;
import net.lakis.cerebro.annotations.ServletPath;
import net.lakis.cerebro.cli.ConsoleServer;
import net.lakis.cerebro.config.AppConfig;
import net.lakis.cerebro.config.ConfigLoader;
import net.lakis.cerebro.jobs.NamedThreadFactory;
import net.lakis.cerebro.log.Log4j2Handler;
import net.lakis.cerebro.web.WebJsonProvider;
import net.lakis.cerebro.web.WebServer;
import net.lakis.cerebro.web.config.WebServerConfig;

@Log4j2
@ConsoleKey("cerebro")
public class Cerebro implements Runnable {
	private @InjectDepency AppConfig appConfig;
	private @Setter @Getter String userDir;
	private @Setter @Getter String lookupPackage;

	private Map<Class<?>, Object> injectMap;

	private Reflections reflections;

	private @Getter HashMap<Class<?>, Object> nativeServlets;

	private @Getter HashMap<Class<?>, Object> jerseyServlets;

	private @Getter HashMap<Class<?>, Object> configs;

	private HashMap<Class<?>, Object> services;
	private HashMap<String, Set<Object>> namedServices;

	private @Getter HashMap<Class<?>, Object> consoleKeys;
	private @Getter ScheduledThreadPoolExecutor scheduler;
	private int threads;

	public static void main(String[] args) {
		Cerebro cerebro = new Cerebro();
		cerebro.run();
	}

	public Cerebro() {

		userDir = System.getProperty("user.dir");
		if (userDir.endsWith("/"))
			userDir = userDir.substring(0, userDir.length() - 1);
		lookupPackage = "net.lakis";
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		context.setConfigLocation(new File(getFilePath("conf", "log4j2.xml")).toURI());

		File fi = new File(getFilePath("conf", "app.properties"));
		if (fi.exists()) {
			try (InputStream input = new FileInputStream(fi)) {
				if (input != null) {

					Properties props = new Properties();
					props.load(input);

					lookupPackage = props.getProperty("package");

					String obj = props.getProperty("threads");
					if (StringUtils.isNotBlank(obj)) {
						obj = obj.replaceAll("[^\\-0-9]", "");
						if (StringUtils.isNotBlank(obj)) {
							threads = Integer.parseInt(obj);
						}
					}

				}

			} catch (IOException e) {
			}
		}

//		try (InputStream input = Cerebro.class.getClassLoader().getResourceAsStream("app.properties")) {

	}

	public String getFilePath(String file) {
		String format = file.startsWith("/") ? "%s%s" : "%s/%s";
		return String.format(format, userDir, file);
	}

	public String getFilePath(String directory, String file) {
		String format = file.startsWith("/") ? "%s/%s%s" : "%s/%s/%s";
		return String.format(format, userDir, directory, file);
	}

	@Override
	public void run() {
		try {
			this.injectMap = new HashMap<Class<?>, Object>();
			this.configs = new HashMap<Class<?>, Object>();
			this.services = new HashMap<Class<?>, Object>();
			this.consoleKeys = new HashMap<Class<?>, Object>();
			this.nativeServlets = new HashMap<Class<?>, Object>();
			this.jerseyServlets = new HashMap<Class<?>, Object>();
			this.namedServices = new HashMap<String, Set<Object>>();

			log.info("loading lookup package {}", lookupPackage);
			// Reflections.log = null;// disable log warnings on classes search
			this.reflections = new Reflections(lookupPackage);

			injectMap.put(Cerebro.class, this);
			consoleKeys.put(Cerebro.class, this);

			loadClass(Log4j2Handler.class, services, "service");
			loadClass(ConsoleServer.class, services, "service");
			loadClass(AppConfig.class, configs, "config");
			loadClass(WebServerConfig.class, configs, "config");
			loadClass(ConfigLoader.class, consoleKeys, "console");
			loadClass(WebJsonProvider.class, services, "service");
			loadClass(WebServer.class, consoleKeys, "service");

			Set<Class<?>> configClasses = reflections.getTypesAnnotatedWith(Config.class);
			configClasses.forEach(c -> loadClass(c, configs, "config"));

			Set<Class<?>> nativeServletsClasses = reflections.getTypesAnnotatedWith(ServletPath.class);
			nativeServletsClasses.forEach(c -> loadClass(c, nativeServlets, "servlet"));

			Set<Class<?>> jerseyServletsClasses = reflections.getTypesAnnotatedWith(javax.ws.rs.Path.class);
			jerseyServletsClasses.forEach(c -> loadClass(c, jerseyServlets, "servlet"));

			Set<Class<?>> ServicesClasses = reflections.getTypesAnnotatedWith(Service.class);
			ServicesClasses.forEach(c -> loadClass(c, services, "service"));

			Set<Class<?>> consoleKeysClasses = reflections.getTypesAnnotatedWith(ConsoleKey.class);
			consoleKeysClasses.forEach(c -> loadClass(c, consoleKeys, "console"));

			this.injectDependencies();
			this.startThreads();
			this.executeMethods();

		} catch (Exception e) {
			log.error("Exception", e);
			System.exit(1);
		}

	}

	private void executeMethods() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		getDependency(ConfigLoader.class).reloadConfig();

		for (Object obj : injectMap.values()) {
			for (Method method : obj.getClass().getMethods()) {
				if (method.getAnnotation(ExecuteFirst.class) != null) {
					method.invoke(obj);
				}
			}
		}

		for (Object obj : injectMap.values()) {
			for (Method method : obj.getClass().getMethods()) {
				if (method.getAnnotation(ExecuteSecond.class) != null) {
					method.invoke(obj);
				}
			}
		}

		for (Object obj : injectMap.values()) {
			for (Method method : obj.getClass().getMethods()) {
				if (method.getAnnotation(ExecuteThird.class) != null) {
					method.invoke(obj);
				}
			}
		}

		for (Object obj : injectMap.values()) {
			for (Method method : obj.getClass().getMethods()) {
				if (method.getAnnotation(ExecuteLast.class) != null) {
					method.invoke(obj);
				}
			}
		}

	}

	private void injectDependencies() throws IllegalArgumentException, IllegalAccessException {
		for (Object obj : injectMap.values()) {

			for (Field field : obj.getClass().getDeclaredFields()) {
				InjectDepency injectAnnotation = field.getAnnotation(InjectDepency.class);

				if (injectAnnotation == null)
					continue;
				field.setAccessible(true);
				if (Strings.isNotBlank(injectAnnotation.value())) {
					Set<Object> set = namedServices.get(injectAnnotation.value());
					field.set(obj, set != null ? set : new HashSet<Object>());

				} else {
					Object dependency = null;
					if (Modifier.isAbstract(field.getType().getModifiers())) {

						for (Entry<Class<?>, Object> entry : injectMap.entrySet()) {
							if (field.getType().isAssignableFrom(entry.getKey())) {
								dependency = entry.getValue();
								break;
							}
						}
					} else {
						dependency = injectMap.get(field.getType());
					}
					if (dependency != null)
						field.set(obj, dependency);
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	public <T> T getDependency(Class<T> classOfT) {
		return (T) injectMap.get(classOfT);
	}

	private void loadClass(Class<?> clazz, HashMap<Class<?>, Object> map, String objectType) {
		try {
			log.info("loading {} {}", objectType, clazz.getName());
			Object obj = injectMap.get(clazz);
			if (obj == null) {
				obj = clazz.getConstructor().newInstance();
				injectMap.put(clazz, obj);
			}
			map.put(clazz, obj);

			Service service = clazz.getAnnotation(Service.class);
			if (service != null && Strings.isNotBlank(service.value())) {
				Set<Object> set = namedServices.get(service.value());
				if (set == null) {
					set = new HashSet<Object>();
					namedServices.put(service.value(), set);
				}
				set.add(obj);
			}

		} catch (Exception e) {
			log.error("Exception", e);
			System.exit(1);
		}
	}

	private void startThreads() {
		if (threads > 0) {
			this.scheduler = new ScheduledThreadPoolExecutor(threads, new NamedThreadFactory("cerebro"));
		} else {
			this.scheduler = null;
		}
	}

	public <T> Future<T> execute(Callable<T> task) throws Exception {

		if (this.scheduler != null)
			return this.scheduler.submit(task);
		else {
			return CompletableFuture.completedFuture(task.call());
		}
	}

	public <T> Future<T> execute(Callable<T> task, long ms) throws Exception {
		if (ms <= 0) {
			return this.execute(task);
		} else {
			return this.scheduler.schedule(task, ms, TimeUnit.MILLISECONDS);
		}
	}

	public void execute(Runnable runnable) {
		if (this.scheduler != null)
			this.scheduler.execute(runnable);
		else
			runnable.run();
	}

	public ScheduledFuture<?> execute(Runnable runnable, long ms) {
		if (ms <= 0) {
			this.execute(runnable);
			return null;
		} else {
			return this.scheduler.schedule(runnable, ms, TimeUnit.MILLISECONDS);
		}
	}

	@ConsoleKey("threadsStats")
	public String threadsStats() {
		StringBuilder sb = new StringBuilder();

		if (this.scheduler != null && !this.scheduler.isTerminated()) {
			sb.append(this.scheduler.getQueue().size());
			sb.append(" tasks in scheduler queue\r\n");

			sb.append(this.scheduler.getActiveCount());
			sb.append(" active threads.\r\n");
			sb.append(this.scheduler.getCorePoolSize());
			sb.append(" core threads.\r\n\r\n");
		} else {
			sb.append("scheduler is not running.\r\n\r\n");
		}

		return sb.toString();
	}

	public String getPath(String file) {
		String format = file.startsWith("/") ? "%s%s" : "%s/%s";
		return String.format(format, userDir, file);

	}
}
