package net.lakis.cerebro.web;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.jws.WebService;
import javax.ws.rs.container.ContainerRequestFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.jaxws.JaxwsHandler;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.grizzly.strategies.WorkerThreadIOStrategy;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainerProvider;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;

import freemarker.cache.FileTemplateLoader;
import lombok.extern.log4j.Log4j2;
import net.lakis.cerebro.Cerebro;
import net.lakis.cerebro.annotations.ConsoleKey;
import net.lakis.cerebro.annotations.ExecuteSecond;
import net.lakis.cerebro.annotations.InjectDepency;
import net.lakis.cerebro.annotations.ServletPath;
import net.lakis.cerebro.web.config.ServletNetworkListener;
import net.lakis.cerebro.web.config.WebServerConfig;
import net.lakis.cerebro.web.servlets.StaticFileServlet;
import net.lakis.cerebro.web.sessions.AbstractSessionProvider;
import net.lakis.cerebro.web.sessions.SessionsFilter;

@Log4j2
@ConsoleKey("web")
public class WebServer {
	private @InjectDepency Cerebro cerebro;
	private @InjectDepency WebServerConfig config;
	private @InjectDepency WebJsonProvider jsonProvider;
	private @InjectDepency("WebResource") Set<Object> resources;
	private @InjectDepency AbstractSessionProvider<?> sessionProvider;

	private HttpServer server;

	@ConsoleKey("reload")
	public String reload() {
		jsonProvider.reload(config);
		return "web server reloaded";
	}

	// execute second because json provider should execute first
	@ExecuteSecond
	@ConsoleKey("start")
	public String start() {

		if (config == null || config.getNetworkListeners() == null || config.getNetworkListeners().length == 0)
			return "no network listener, server is disabled";

		if (this.server != null && this.server.isStarted())
			return "server already started";

		try {
			this.reload();
			this.server = new HttpServer();

			ServerConfiguration serverConfiguration = this.server.getServerConfiguration();

			serverConfiguration.setAllowPayloadForUndefinedHttpMethods(true);
			this.addNetworkListeners();

			this.addServlets(serverConfiguration);

			if (config.isEnableAssets()) {
				StaticFileServlet servlet = new StaticFileServlet();
				servlet.setRootPath(cerebro.getFilePath("/assets/"));
				servlet.setAutoIndex(true);
				servlet.setFileCacheEnabled(config.isCacheAssets());
				serverConfiguration.addHttpHandler(servlet, "/assets");

			}

			serverConfiguration.setJmxEnabled(true);

			this.server.start();
			return "web server started!";

		} catch (Exception e) {
			log.error("Exception", e);
			return ExceptionUtils.getFullStackTrace(e);
		}

	}

	private void addNetworkListeners() {

		if (config.getNetworkListeners() == null || config.getNetworkListeners().length == 0) {
			throw new IllegalStateException("No network listener exist");
		}
		WebSocketAddOn webSocketAddOn = new WebSocketAddOn();

		for (ServletNetworkListener servletNetworkListener : config.getNetworkListeners()) {
			if (servletNetworkListener.getPort() == 0) {
				throw new IllegalStateException("Port parameter can not be equal to zero");
			}

			NetworkListener listener = new NetworkListener(servletNetworkListener.getName(),
					servletNetworkListener.getHost(), new PortRange(servletNetworkListener.getPort(),
							servletNetworkListener.getPort() + servletNetworkListener.getPortRange()));
			listener.registerAddOn(webSocketAddOn);

			this.server.addListener(listener);

			TCPNIOTransport transport = listener.getTransport();

			ThreadPoolConfig conf = ThreadPoolConfig.defaultConfig().setPoolName("worker-thread-")
					.setCorePoolSize(servletNetworkListener.getCorePoolSize())
					.setMaxPoolSize(servletNetworkListener.getMaxPoolSize())
					.setQueueLimit(servletNetworkListener.getQueueLimit())/* same as default */;

			transport.configureBlocking(false);
			transport.setSelectorRunnersCount(servletNetworkListener.getRunnersCount());
			transport.setWorkerThreadPoolConfig(conf);
			transport.setIOStrategy(WorkerThreadIOStrategy.getInstance());
			transport.setTcpNoDelay(true);
			transport.setReadBufferSize(servletNetworkListener.getReadBufferSize());
			transport.setReuseAddress(servletNetworkListener.isReuseAddress());

			if (StringUtils.isNotBlank(servletNetworkListener.getKeyStorePath())) {

				SSLContextConfigurator sslContextConfigurator = new SSLContextConfigurator();

				sslContextConfigurator.setKeyStoreFile(servletNetworkListener.getKeyStorePath());
				sslContextConfigurator.setKeyStorePass(servletNetworkListener.getKeyStorePassword());

				sslContextConfigurator.setTrustStoreFile(servletNetworkListener.getTrustStorePath());
				sslContextConfigurator.setTrustStorePass(servletNetworkListener.getTrustStorePassword());

				SSLEngineConfigurator sslEngineConfigurator = new SSLEngineConfigurator(sslContextConfigurator);

				sslEngineConfigurator.setClientMode(servletNetworkListener.isClientMode());
				sslEngineConfigurator.setNeedClientAuth(servletNetworkListener.isNeedClientAuth());

				listener.setSecure(true);
				listener.setSSLEngineConfig(sslEngineConfigurator);

			}
		}
	}

	private void addServlets(ServerConfiguration serverConfiguration)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchMethodException, SecurityException, IOException {

		for (Entry<Class<?>, Object> entry : cerebro.getNativeServlets().entrySet()) {
			ServletPath servletPath = entry.getKey().getAnnotation(ServletPath.class);
			Object servlet = entry.getValue();
			if (servlet instanceof WebSocketApplication) {
				WebSocketEngine.getEngine().register("", servletPath.value(), (WebSocketApplication) servlet);
			} else if (entry.getKey().getAnnotation(WebService.class) == null) {
				serverConfiguration.addHttpHandler((HttpHandler) servlet, servletPath.value());
			} else {
				HttpHandler httpHandler = new JaxwsHandler(servlet);
				serverConfiguration.addHttpHandler(httpHandler, servletPath.value());
			}
		}

		HashMap<Class<?>, Object> jerseyServlets = cerebro.getJerseyServlets();
		if (jerseyServlets != null && jerseyServlets.size() > 0) {

			final AbstractBinder binder = new AbstractBinder() {
				@Override
				public void configure() {
//					bind(jsonProvider).to(WebJsonProvider.class);
					for (Entry<Class<?>, Object> entry : jerseyServlets.entrySet())
						bind(entry.getValue()).to(entry.getKey());
				}
			};
			
			String path =  cerebro.getFilePath("ftl");
			if(path.charAt(1) == ':')
				path = path.substring(2);
			
			final ResourceConfig rc = new ResourceConfig(jerseyServlets.keySet())//
					.register(binder).register(JacksonFeature.class)//
					.register(jsonProvider)//
					.property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, path/* cerebro.getFilePath("ftl") */)// "templates/freemarker")
//					.register(FreemarkerMvcFeature.TEMPLATE_OBJECT_FACTORY, new FileTemplateLoader(new File(cerebro.getFilePath("ftl"))))
					.register(FreemarkerMvcFeature.class);//

			if (sessionProvider != null) {
				rc.register(new SessionsFilter(sessionProvider));
			}
			if (resources.size() > 0) {
				for (Object resource : resources) {
					rc.register(resource);
				}
			}
			GrizzlyHttpContainer httpContainer = new GrizzlyHttpContainerProvider()
					.createContainer(GrizzlyHttpContainer.class, rc);
			serverConfiguration.addHttpHandler(httpContainer, "/");
		}

	}

	@ConsoleKey("stop")
	public String stop() {
		if (server == null || !server.isStarted())
			return "server is not running";
		server.shutdownNow();

		return "server stopped!";
	}

	public HttpServer getServer() {
		return server;
	}

}
