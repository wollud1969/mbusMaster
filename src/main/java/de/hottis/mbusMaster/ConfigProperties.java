package de.hottis.mbusMaster;


import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ConfigProperties extends Properties {
	static final String DEFAULT_PROPS_FILENAME = "mbusMaster.props";
  static final String PROPS_VERBOSE = "verbose";
  static final String PROPS_MAINCONFIGFILE = "mainConfigFile";
  static final String PROPS_ERRORRATIOTHRESHOLD = "errorRatioThreshold";
  static final String PROPS_ERRORRATIOCHECKTHRESHOLD = "errorRatioCheckThreshold";
  static final String PROPS_LOOPSHUTDOWNDELAY = "loopShutdownDelay";
	static final String PROPS_DEVICES = "mbus.device";
	static final String PROPS_MQTT_BROKER = "mqtt.broker";
	static final String PROPS_MQTT_CLIENTID = "mqtt.clientid";
	static final String PROPS_MQTT_USERNAME = "mqtt.username";
	static final String PROPS_MQTT_PASSWORD = "mqtt.password";
  static final String PROPS_MQTT_OUT_TOPIC = "mqtt.topic.out";

  static final Logger logger = LogManager.getRootLogger();

  private boolean overwriteVerbose;


  public ConfigProperties() throws ConfigPropertiesException {
    super();
    String propsFilename = System.getProperty(PROPS_MAINCONFIGFILE, DEFAULT_PROPS_FILENAME); 
    try {
      try (FileInputStream propsFileInputStream = new FileInputStream(propsFilename)) {
        load(propsFileInputStream);
      }
    logger.debug("Configuration loaded");
    } catch (FileNotFoundException e) {
      String msg = "Config file " + propsFilename + " not found";
      logger.error(msg);
      throw new ConfigPropertiesException(msg);
    } catch (IOException e) {
      String msg = "Error when reading config file " + propsFilename;
      logger.error(msg);
      throw new ConfigPropertiesException(msg, e);
    }

    String overwriteVerboseStr = System.getProperty(PROPS_VERBOSE, "no");
    this.overwriteVerbose = ("yes".equalsIgnoreCase(overwriteVerboseStr) || "true".equalsIgnoreCase(overwriteVerboseStr));
  }

  public String getStringProperty(String key) throws ConfigPropertiesException {
    String returnValue = this.getProperty(key);
    if (returnValue == null) {
      throw new ConfigPropertiesException(key + "not found");
    }
    return returnValue;
  }

  String getStringProperty(String key, String def) {
    String returnValue;
    try {
      returnValue = this.getStringProperty(key);
    } catch (ConfigPropertiesException  e) {
      returnValue = def;
    }
    return returnValue;
  }

  public boolean getBooleanProperty(String key) throws ConfigPropertiesException {
    String v = this.getStringProperty(key);
    return ("yes".equalsIgnoreCase(v) || "true".equalsIgnoreCase(v));    
  }

  public boolean getBooleanProperty(String key, boolean def) {
    boolean returnValue;
    try {
      returnValue = this.getBooleanProperty(key);
    } catch (ConfigPropertiesException e) {
      returnValue = def;
    }
    return returnValue;
  }

  public boolean isVerbose() {
    return this.getBooleanProperty("verbose", false) || this.overwriteVerbose;
  }

  
  public double getDoubleProperty(String key) throws ConfigPropertiesException {
    String v = this.getStringProperty(key);
    try {
      double o = Double.parseDouble(v);
      return o;
    } catch (NullPointerException | NumberFormatException e) {
      throw new ConfigPropertiesException("Error in getDoubleProperty", e);
    }
  }

  public double getDoubleProperty(String key, double def) {
    double returnValue;
    try {
      returnValue = this.getDoubleProperty(key);
    } catch (ConfigPropertiesException e) {
      returnValue = def;
    }
    return returnValue;
  }

  public int getIntProperty(String key) throws ConfigPropertiesException {
    String v = this.getStringProperty(key);
    try {
      int o = Integer.parseInt(v);
      return o;
    } catch (NullPointerException | NumberFormatException e) {
      throw new ConfigPropertiesException("Error in getIntProperty", e);
    }
  }

  public int getIntProperty(String key, int def) {
    int returnValue;
    try {
      returnValue = this.getIntProperty(key);
    } catch (ConfigPropertiesException e) {
      returnValue = def;
    }
    return returnValue;
  }
}