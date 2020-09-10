package de.hottis.mbusMaster;

import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MQTTDequeuer extends Thread {
  static final String ERROR_RATIO_KEY = "errorRatio";

  static final String DEFAULT_MQTT_BROKER = "localhost";
	static final String DEFAULT_MQTT_CLIENTID = "mbusMaster01";
	static final String DEFAULT_MQTT_USERNAME = null;
	static final String DEFAULT_MQTT_PASSWORD = null;
  static final String DEFAULT_MQTT_OUT_TOPIC = "IoT/ParsedData/MeterbusHub";

	static final Logger logger = LogManager.getRootLogger();

  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_BLACK = "\u001B[30m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";
  private static final String ANSI_BLUE = "\u001B[34m";
  private static final String ANSI_PURPLE = "\u001B[35m";
  private static final String ANSI_CYAN = "\u001B[36m";
  private static final String ANSI_WHITE = "\u001B[37m";

  private BlockingQueue<ADataObject> queue;
  private ConfigProperties config;
	private final String mqttBroker;
  private final String mqttClientId;
  private final String mqttUser;
  private final String mqttPassword;
  private final String mqttOutTopic;
	private final MqttConnectOptions mqttConnOpts;
	private MqttClient mqttClient;
	private final Callback mqttCallback = new Callback();


  public MQTTDequeuer(ConfigProperties config, BlockingQueue<ADataObject> queue) {
    super("MQTTDequeuer");

    this.config = config;
    this.queue = queue;

    this.mqttBroker = this.config.getProperty(ConfigProperties.PROPS_MQTT_BROKER, DEFAULT_MQTT_BROKER);
    this.mqttClientId = this.config.getProperty(ConfigProperties.PROPS_MQTT_CLIENTID, DEFAULT_MQTT_CLIENTID);
    this.mqttUser = this.config.getProperty(ConfigProperties.PROPS_MQTT_USERNAME, DEFAULT_MQTT_USERNAME);
    this.mqttPassword = this.config.getProperty(ConfigProperties.PROPS_MQTT_PASSWORD, DEFAULT_MQTT_PASSWORD);
    this.mqttOutTopic = this.config.getProperty(ConfigProperties.PROPS_MQTT_OUT_TOPIC, DEFAULT_MQTT_OUT_TOPIC);

		this.mqttConnOpts = new MqttConnectOptions();
		if (this.mqttUser != null && this.mqttPassword != null) {
			this.mqttConnOpts.setUserName(this.mqttUser);
			this.mqttConnOpts.setPassword(this.mqttPassword.toCharArray());
		}
  }
  
	class Callback implements MqttCallbackExtended {
		public void messageArrived(String topic, MqttMessage payload) {
			logger.warn("This is strange! Message received, topic: " + topic + ", payload: " + payload.toString());
		}
		
		public void connectComplete(boolean reconnect, java.lang.String serverURI) {
      logger.info("Connection established for " + serverURI);
      if (reconnect) {
        logger.info("Was a reconnect.");
      }
		}
		
		public void connectionLost(java.lang.Throwable cause) {
			logger.error("Connection lost, cause: " + cause.toString());
			MQTTDequeuer.this.reconnect();
		}
		
		public void deliveryComplete(IMqttDeliveryToken token) {
			
		}
	}

  public void initMqttClient() throws MbusException {
    this.connect();
  }

	private void connect() throws MbusException {
		try {
			this.mqttClient = new MqttClient(this.mqttBroker, this.mqttClientId);
			this.mqttClient.setCallback(this.mqttCallback);
			this.reconnect();
      logger.info("MQTT connection established");
		} catch (MqttException e) {
			throw new MbusException("Error when connecting the MQTT broker", e);
		}
	}

	private void reconnect() {
		logger.error("Trying to reconnect to MQTT broker");
		if (! this.mqttClient.isConnected()) {
      int cnt = 0;
			while (true) {
        cnt++;
        logger.error("Reconnection try count " + cnt);
				try {
					this.mqttClient.connect(this.mqttConnOpts);
					logger.error("reconnecting successfully completed");
					break;
				} catch (MqttException e) {
					logger.error("Exception during reconnection: " + e.toString());
					try {
						Thread.sleep(10*1000);
					} catch (InterruptedException e1) {
					}
				}
			}
		} else {
			logger.info("client is still connected");
		}
	}

  public void run() {
    while(true) {
      ADataObject o = null;
      try {
        o = this.queue.take();

        if (this.config.isVerbose()) {
          if (o.hasKey(ERROR_RATIO_KEY) && ((Double)o.getValues().get(ERROR_RATIO_KEY)) < 0.001) {
            System.out.print(ANSI_GREEN);
          }
          if (o.hasKey(ERROR_RATIO_KEY) && ((Double)o.getValues().get(ERROR_RATIO_KEY)) > 0.25) {
            System.out.print(ANSI_RED);
          }
          if ("Statistics".equals(o.getKind())) {
            System.out.print(ANSI_CYAN);
          }
          System.out.print("MQTTDequeuer: " + o.toString());
          System.out.println(ANSI_RESET);
        }

        MqttMessage message = new MqttMessage(o.toString().getBytes());
        this.mqttClient.publish(this.mqttOutTopic, message);
      } catch (InterruptedException e) {
      } catch (MqttException e) {
          String tmpO = (o == null) ? "<null>" : o.toString();
          this.logger.error("MqttException when trying to publish " + tmpO, e);
      }
    }
  }
}