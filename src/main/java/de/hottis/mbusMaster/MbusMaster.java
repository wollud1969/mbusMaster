package de.hottis.mbusMaster;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openmuc.jmbus.DataRecord;
import org.openmuc.jmbus.MBusMessage;
import org.openmuc.jmbus.VariableDataStructure;

public class MbusMaster {
	static final Logger logger = LogManager.getRootLogger();
	static boolean stopSignal = false;
	
	public static void main(String[] args) throws Exception {
		logger.info("MbusMaster starting");

		final ConfigProperties config = new ConfigProperties();

		/*
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			stopSignal = true;
    	}));
		logger.debug("Shutdown hook added");
		*/

		LinkedBlockingQueue<ADataObject> queue = new LinkedBlockingQueue<>();


		MbusScheduledQuerier querier = new MbusScheduledQuerier(config, queue);
		querier.loadDevices();
		querier.start();

		DummyDequeuer ddq = new DummyDequeuer(queue);
		ddq.start();


		querier.join();
		ddq.join();
		logger.info("MbusMaster terminating");

	}		
}
