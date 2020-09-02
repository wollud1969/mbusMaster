package de.hottis.mbusMaster;

import java.io.IOException;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MbusMaster {
	static final String PROPS_FILENAME = "mbusMaster.props";
	static final Logger logger = LogManager.getRootLogger();
	static boolean stopSignal = false;
	
	public static void main(String[] args) throws Exception {
		logger.info("MbusMaster starting");

		/*
		final Properties config = new Properties();
		try (FileInputStream propsFileInputStream = new FileInputStream(PROPS_FILENAME)) {
			config.load(propsFileInputStream);
		}
		logger.debug("Configuration loaded");
		*/

		/*
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			stopSignal = true;
    	}));
		logger.debug("Shutdown hook added");
		*/
		
		MbusgwChild mbusgw = new MbusgwChild(false);
		mbusgw.start();

		byte[] devices = { (byte)80, (byte)81, (byte)82, (byte)83, (byte)84, (byte)85, (byte)86, (byte)87 };

		int cnt = 0;
		while (! stopSignal) {
			System.out.println("--- " + cnt + " ----------------------------------------------------");
			cnt++;
			for (byte device : devices) {
				System.out.println("Querying device " + device);
				try {
					mbusgw.sendRequest((byte)0x5b, device);

					byte[] frame = mbusgw.collectResponse();
					for (byte x : frame) {
						System.out.print(Integer.toHexString(Byte.toUnsignedInt(x)) + " ");
					}
					System.out.println();
				} catch (IOException e) {
					logger.error("Error " + e.toString() + ", " + e.getMessage() + " in Meterbus dialog for device " + device);
				}
			}
			// if (cnt >= 10) {
			//	break;
			//}
			Thread.sleep(15*1000);
		}

		logger.info("Stopping mbusgw process");
		mbusgw.stop();

	}		
}
