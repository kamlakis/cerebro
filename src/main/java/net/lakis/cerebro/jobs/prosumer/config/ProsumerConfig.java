package net.lakis.cerebro.jobs.prosumer.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProsumerConfig {
	private int workersCount = 1;
	private int bulkCount = 1;
	private int sleepTime = 0;
	private boolean waitForAll = false;
	private String name = "Prosumer";

}
