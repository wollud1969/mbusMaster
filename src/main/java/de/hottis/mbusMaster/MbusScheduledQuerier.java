package de.hottis.mbusMaster;


import java.io.IOException;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MbusScheduledQuerier extends Thread {
	static final Logger logger = LogManager.getRootLogger();

  private ArrayList<MbusDevice> devices;
  private boolean stopSignal = false;
  private ConfigProperties config;
  private BlockingQueue<String> queue;

  public MbusScheduledQuerier(ConfigProperties config, BlockingQueue<String> queue) {
    super("MbusScheduledQuerier");

    this.config = config;
    this.queue = queue;

    this.devices = new ArrayList<>();
		this.devices.add(new FinderThreePhasePowerMeter("Total Electricity", (byte)80, 0));
		this.devices.add(new FinderOnePhasePowerMeter("Dryer", (byte)81, 0));
		this.devices.add(new FinderOnePhasePowerMeter("Laundry", (byte)82, 0));
		this.devices.add(new FinderOnePhasePowerMeter("Dishwasher", (byte)83, 0));
		this.devices.add(new FinderOnePhasePowerMeter("Light", (byte)84, 0));
		this.devices.add(new FinderOnePhasePowerMeter("Computer", (byte)85, 0));
		this.devices.add(new FinderOnePhasePowerMeter("Freezer", (byte)86, 0));
		this.devices.add(new FinderOnePhasePowerMeter("Fridge", (byte)87, 0));
  }

  public void setStopSignal() {
    this.stopSignal = true;
  }

  public void run() {
    try {
      MbusgwChild mbusgw = new MbusgwChild(config);
      mbusgw.start();

      int cnt = 0;
      int errCnt = 0;
      int successCnt = 0;

      while (! this.stopSignal) {
        cnt++;
        for (MbusDevice device : this.devices) {
          logger.info("Querying " + device.getName() + " meter");
          try {
            mbusgw.sendRequest((byte)0x5b, device.getAddress());
            byte[] frame = mbusgw.collectResponse();
            device.parse(frame);

            logger.info("Got: " + device.toString());
            this.queue.add(device.toString());
            
            successCnt++;
          } catch (IOException e) {
            errCnt++;
            logger.error("Error " + e.toString() + " in Meterbus dialog for device " + device.shortString());
          }
        }
        logger.info("Cnt: " + cnt + ", SuccessCnt: " + successCnt + ", ErrCnt: " + errCnt);
        try {
          Thread.sleep(5*1000);
        } catch (InterruptedException e) {
        }
      }

      logger.info("Stopping mbusgw process");
      try {
        mbusgw.stop();
      } catch (InterruptedException | IOException e) {
        logger.error("Problems to stop Meterbus Gateway process: " + e.toString() + ", however, terminating anyway");
      }
    } catch (IOException e) {
      logger.error("Unable to start Meterbus Gateway process");
    }
  }
}
