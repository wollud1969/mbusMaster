package de.hottis.mbusMaster;

import java.io.IOException;

import java.util.Properties;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openmuc.jmbus.DataRecord;
import org.openmuc.jmbus.MBusMessage;
import org.openmuc.jmbus.VariableDataStructure;

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

		MbusDevice[] devices = {
			new FinderThreePhasePowerMeter("Total Electricity", (byte)80, 0),
			new FinderOnePhasePowerMeter("Dryer", (byte)81, 0),
			new FinderOnePhasePowerMeter("Laundry", (byte)82, 0),
			new FinderOnePhasePowerMeter("Dishwasher", (byte)83, 0),
			new FinderOnePhasePowerMeter("Light", (byte)84, 0),
			new FinderOnePhasePowerMeter("Computer", (byte)85, 0),
			new FinderOnePhasePowerMeter("Freezer", (byte)86, 0),
			new FinderOnePhasePowerMeter("Fridge", (byte)87, 0)
		};
		
		
		int cnt = 0;
		int errCnt = 0;
		int successCnt = 0;
		while (! stopSignal) {
			cnt++;
			for (MbusDevice device : devices) {
				System.out.println("Querying " + device.getName() + " meter");
				try {
					mbusgw.sendRequest((byte)0x5b, device.getAddress());
					byte[] frame = mbusgw.collectResponse();
					device.parse(frame);

					System.out.println(device);

					successCnt++;
				} catch (IOException e) {
					errCnt++;
					logger.error("Error " + e.toString() + " in Meterbus dialog for device " + device.shortString());
				}
			}
			// if (cnt >= 10) {
			//	break;
			//}
			System.out.println("--- " + cnt + " - " + successCnt + " - " + errCnt + " ---------------------------------------------------");
			Thread.sleep(5*1000);
		}

		logger.info("Stopping mbusgw process");
		mbusgw.stop();

	}		
}
