package de.hottis.mbusMaster;


import java.io.IOException;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MbusScheduledQuerier extends Thread {
  static final double DEFAULT_ERRORRATIOTHRESHOLD = 0.5;
  static final int DEFAULT_ERRORRATIOCHECKTHRESHOLD = 100;
  static final int DEFAULT_LOOPSHUTDOWNDELAY = 300; // seconds

  static final Logger logger = LogManager.getRootLogger();

  private ArrayList<MbusDevice> devices;
  private boolean stopSignal = false;
  private ConfigProperties config;
  private BlockingQueue<ADataObject> queue;
  

  public MbusScheduledQuerier(ConfigProperties config, BlockingQueue<ADataObject> queue) {
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

  private int getCurrentTime() {
    return (int) (System.currentTimeMillis() / 1000);
  }

  public void run() {
    try {
      MbusgwChild mbusgw = new MbusgwChild(config);
      mbusgw.start();

      int cnt = 0;
      int errCnt = 0;
      int successCnt = 0;
      int shutdownCnt = 0;
      int lastShutdown = this.getCurrentTime();
      int timeSinceLastLoopShutdown = 0;
      int shutdownTimeSum = 0;
      int meantimeBetweenLoopShutdowns = 0;
    

      while (! this.stopSignal) {
        cnt++;
        double maxErrorRatio = 0;

        for (MbusDevice device : this.devices) {
          logger.info("Querying " + device.getName() + " meter");
          try {
            mbusgw.sendRequest((byte)0x5b, device.getAddress());
            byte[] frame = mbusgw.collectResponse();
            device.parse(frame);

            logger.info("Got: " + device.toString());
            device.incSuccessCnt();
            this.queue.add(device.getDataObject());
            
            successCnt++;
          } catch (IOException e) {
            device.incErrorCnt();
            errCnt++;
            logger.error("Error " + e.toString() + " in Meterbus dialog for device " + device.shortString());
          }
          maxErrorRatio = (maxErrorRatio > device.getErrorRatio()) ? maxErrorRatio : device.getErrorRatio();
        }
        
        logger.info("CycleCnt: " + cnt + ", SuccessCnt: " + successCnt + ", ErrCnt: " + errCnt + 
                    ", MaxErrorRatio: " + maxErrorRatio + ", MeanErrorRatio: " + ((double)errCnt / (double)(errCnt+successCnt)) +
                    ", ShutdownCnt: " + shutdownCnt +
                    ", TimeSinceLastLoopShutdown: " + timeSinceLastLoopShutdown + ", MeantimeBetweenLoopShutdowns: " + meantimeBetweenLoopShutdowns);
        this.queue.add(new MbusStatisticsDataObject("MbusgwChild", cnt, errCnt, successCnt, shutdownCnt,
                                                    maxErrorRatio, ((double)errCnt / (double)(errCnt+successCnt)),
                                                    timeSinceLastLoopShutdown, meantimeBetweenLoopShutdowns));

        if ((maxErrorRatio > config.getDoubleProperty(ConfigProperties.PROPS_ERRORRATIOTHRESHOLD, DEFAULT_ERRORRATIOTHRESHOLD)) &&
            (cnt % config.getIntProperty(ConfigProperties.PROPS_ERRORRATIOCHECKTHRESHOLD, DEFAULT_ERRORRATIOCHECKTHRESHOLD) == 0)) {
          logger.error("maxErrorRatio exceeds threshold and cycleCnt exceeds checkThreshold, request loop shutdown");

          // disable loop
          mbusgw.loopShutdown();

          // reset counters in devices
          for (MbusDevice device : this.devices) {
            device.resetCounter();
          }
          // reset local counters
          errCnt = 0;
          successCnt = 0;
          shutdownCnt++;

          // remember time of loop shutdown
          // calculate time since last shutdown
          // calculate mean time between shutdowns
          int currentTime = this.getCurrentTime();
          timeSinceLastLoopShutdown = currentTime - lastShutdown;
          lastShutdown = currentTime;
          shutdownTimeSum += timeSinceLastLoopShutdown;
          meantimeBetweenLoopShutdowns = shutdownTimeSum / shutdownCnt;

          // delay
          int delay = config.getIntProperty(ConfigProperties.PROPS_LOOPSHUTDOWNDELAY, DEFAULT_LOOPSHUTDOWNDELAY);
          logger.error("delaying for " + delay + "s");
          try {
            Thread.sleep(delay * 1000);
          } catch (InterruptedException e) {
          }
        }


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
