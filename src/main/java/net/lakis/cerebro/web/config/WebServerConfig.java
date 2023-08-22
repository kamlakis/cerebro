package net.lakis.cerebro.web.config;

import lombok.Data;
import net.lakis.cerebro.annotations.Config;

@Data
@Config("servlets.json")
public class WebServerConfig {
	private ServletNetworkListener[] networkListeners;
	private boolean identJson;
	private boolean enableAssets;
	private boolean cacheAssets;

}
