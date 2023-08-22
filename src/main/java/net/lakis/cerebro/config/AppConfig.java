package net.lakis.cerebro.config;

import net.lakis.cerebro.annotations.Config;
import net.lakis.cerebro.collections.VariablesStore;

@Config(value = "app.properties", format = false)
public class AppConfig extends VariablesStore {

}
