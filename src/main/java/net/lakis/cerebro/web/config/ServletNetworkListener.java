package net.lakis.cerebro.web.config;

import lombok.Data;
 
@Data
public class ServletNetworkListener {
	private String name;
	private int port;
	private int portRange;
	private String host;
	// The maximum number threads that may be maintained by this
	private int maxPoolSize;
	// The initial number of threads that will be present with the thread pool is
	// created
	private int corePoolSize;
	// queueLimit: The maximum number of pending tasks that may be queued
	private int queueLimit;
	private int runnersCount;
	private int readBufferSize;
	private boolean reuseAddress;

	private String keyStorePath;
	private String keyStorePassword;

	private String trustStorePath;
	private String trustStorePassword;

	// Client mode when handshaking.
	private boolean clientMode;
	// Require client Authentication.
	private boolean needClientAuth;

}
