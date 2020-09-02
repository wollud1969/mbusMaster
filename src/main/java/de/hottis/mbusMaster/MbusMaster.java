package de.hottis.mbusMaster;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MbusMaster {
	static final String PROPS_FILENAME = "mbusMaster.props";
	static final Logger logger = LogManager.getRootLogger();
	
	
	public static void main(String[] args) throws Exception {
		logger.info("MbusMaster starting");

		/*
		final Properties config = new Properties();
		try (FileInputStream propsFileInputStream = new FileInputStream(PROPS_FILENAME)) {
			config.load(propsFileInputStream);
		}
		logger.debug("Configuration loaded");
		*/

		MbusgwChild mbusgw = new MbusgwChild(false);
		mbusgw.start();


		mbusgw.sendRequest((byte)0x5b, (byte)80);

		byte[] frame = mbusgw.collectResponse();
		for (byte x : frame) {
			System.out.print(Integer.toHexString(Byte.toUnsignedInt(x)) + " ");
		}
		System.out.println();

		System.out.println("Stopping mbusgw process");
		mbusgw.stop();

	}		
}
