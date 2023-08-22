package net.lakis.cerebro.cli;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.google.gson.Gson;

import lombok.extern.log4j.Log4j2;
import net.lakis.cerebro.Cerebro;
import net.lakis.cerebro.annotations.ConsoleKey;
import net.lakis.cerebro.annotations.ExecuteLast;
import net.lakis.cerebro.annotations.InjectDepency;
import net.lakis.cerebro.annotations.Service;
import net.lakis.cerebro.config.AppConfig;
import net.lakis.cerebro.io.ByteArrayInputStream;
import net.lakis.cerebro.jobs.Worker;
import net.lakis.cerebro.jobs.WorkersFactory;
import net.lakis.cerebro.lang.Strings;
import net.lakis.cerebro.lang.Strings.Arguments;

@Service
@Log4j2
public class ConsoleServer {
	private @InjectDepency AppConfig appConfig;
	private @InjectDepency Cerebro cerebro;

	private Worker worker;
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;

	private String getOptionsResponse;
	private Map<String, ConsoleHandler> functionsMap;

	@ExecuteLast
	public void onStart() throws IOException {
		int port = appConfig.getAsInt("console.port");
		if (port <= 0) {
			log.info("invalid console.port in app.properties, disabling console server.");
			return;
		}

		this.fetchFunctions();

		InetSocketAddress inetSocketAddress = new InetSocketAddress(port);

		this.selector = Selector.open();

		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.configureBlocking(false);
		this.serverSocketChannel.bind(inetSocketAddress);
		this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		this.worker = WorkersFactory.createWorker(this::work);
		this.worker.start();
		log.info("console started on port {}", port);

	}

	private Map<String, ConsoleHandler> fetchFunctions() {
		this.functionsMap = new HashMap<String, ConsoleHandler>();
		for (Entry<Class<?>, Object> entry : cerebro.getConsoleKeys().entrySet()) {
			ConsoleKey consoleKey = entry.getKey().getAnnotation(ConsoleKey.class);
			if (consoleKey == null)
				continue;
			ConsoleHandler ConsoleHandler = new ConsoleHandler(consoleKey.value(), entry.getKey(), entry.getValue());
			if (ConsoleHandler.hasMethods())
				functionsMap.put(consoleKey.value(), ConsoleHandler);
		}
		return null;
	}

	public void work() throws Exception {
		int ret = selector.select();
		if (ret <= 0) {
			return;
		}
		Set<SelectionKey> selectedKeys = selector.selectedKeys();

		Iterator<SelectionKey> iterator = selectedKeys.iterator();
		while (iterator.hasNext()) {
			SelectionKey key = iterator.next();
			iterator.remove();

			if (key.isAcceptable()) {
				SocketChannel sc = serverSocketChannel.accept();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			}

			if (key.isReadable()) {
				SocketChannel sc = (SocketChannel) key.channel();
				ByteBuffer bb = ByteBuffer.allocate(1024);
				int bytesRead = sc.read(bb);
				if (bytesRead < 0) {
					sc.close();
				} else if (bytesRead > 0) {

					bb.flip();
					byte[] data = new byte[bytesRead];
					bb.get(data);

					this.onRead(new ConsoleWriter(sc), data);
				}
			}
		}

	}

	private void onRead(ConsoleWriter consoleWriter, byte[] array) throws IOException {
		try (ByteArrayInputStream baos = new ByteArrayInputStream(array)) {
			while (baos.hasMoreData()) {
				String message = baos.readPString().trim();
				if (message.equalsIgnoreCase("getoptions"))
					this.getOptions(consoleWriter);
				else
					this.handle(consoleWriter, message);
			}
		}

	}

	private void getOptions(ConsoleWriter consoleWriter) {
		if (getOptionsResponse == null)
			generateGetOptionsResponse();

		consoleWriter.write(String.format("getoptions%s", getOptionsResponse));

	}

	private void generateGetOptionsResponse() {
		ArrayList<List<Collection<String>>> list = new ArrayList<List<Collection<String>>>();

		for (Entry<String, ConsoleHandler> entry : functionsMap.entrySet()) {
			List<Collection<String>> classOptions = new ArrayList<Collection<String>>();
			classOptions.add(Arrays.asList(entry.getKey()));
			classOptions.add(entry.getValue().getkeys());
			list.add(classOptions);
		}

		this.getOptionsResponse = new Gson().toJson(list);
	}

	private void handle(ConsoleWriter consoleWriter, String message) {
		try {
			Arguments args = Strings.parseArgs(message);
			String clazz = args.getString();
			ConsoleHandler handler = functionsMap.get(clazz);

			String resp;
			if (handler == null) {
				resp = String.format("Command not found: %s\n", message);
			} else {
				resp = handler.handle(consoleWriter, args);
			}

			if (resp != null)
				consoleWriter.write(resp);

		} catch (Exception e) {
			consoleWriter.write(ExceptionUtils.getFullStackTrace(e));
		}
	}

}
