package de.hottis.mbusMaster;

import java.io.IOException;

import java.util.Properties;

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

		MbusgwChild mbusgw = new MbusgwChild(true);
		mbusgw.start();

		byte[] devices = { (byte)84, (byte)87, (byte)82, (byte)83, (byte)80, (byte)85, (byte)86, (byte)81 };

		int cnt = 0;
		int errCnt = 0;
		int successCnt = 0;
		while (! stopSignal) {
			System.out.println("--- " + cnt + " - " + successCnt + " - " + errCnt + " ---------------------------------------------------");
			cnt++;
			for (byte device : devices) {
				System.out.println("Querying device " + device);
				try {
					mbusgw.sendRequest((byte)0x5b, device);

					byte[] frame = mbusgw.collectResponse();
					
					MBusMessage mbusMsg = MBusMessage.decode(frame, frame.length);
					VariableDataStructure variableDataStructure = mbusMsg.getVariableDataResponse();
					variableDataStructure.decode();
					List<DataRecord> dataRecords = variableDataStructure.getDataRecords();

					for (DataRecord dr : dataRecords) {
						System.out.println(dr);
					}

					/*
					for (byte x : frame) {
						System.out.print(Integer.toHexString(Byte.toUnsignedInt(x)) + " ");
					}
					*/
					System.out.println();
					successCnt++;
				} catch (IOException e) {
					errCnt++;
					logger.error("Error " + e.toString() + " in Meterbus dialog for device " + device);
				}
			}
			// if (cnt >= 10) {
			//	break;
			//}
			Thread.sleep(5*1000);
		}

		logger.info("Stopping mbusgw process");
		mbusgw.stop();

	}		
}
