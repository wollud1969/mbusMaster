package de.hottis.mbusMaster;

import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DummyDequeuer extends Thread {
  static final String ERROR_RATIO_KEY = "errorRatio";

	static final Logger logger = LogManager.getRootLogger();

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BLACK = "\u001B[30m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_PURPLE = "\u001B[35m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String ANSI_WHITE = "\u001B[37m";
  private BlockingQueue<ADataObject> queue;

  public DummyDequeuer(BlockingQueue<ADataObject> queue) {
    super("DummyDequeuer");

    this.queue = queue;
  }

  public void run() {
    while(true) {
      try {
        ADataObject o = this.queue.take();
        if (o.hasKey(ERROR_RATIO_KEY) && ((Double)o.getValues().get(ERROR_RATIO_KEY)) < 0.001) {
          System.out.print(ANSI_GREEN);
        }
        if (o.hasKey(ERROR_RATIO_KEY) && ((Double)o.getValues().get(ERROR_RATIO_KEY)) > 0.25) {
          System.out.print(ANSI_RED);
        }
        if ("Statistics".equals(o.getKind())) {
          System.out.print(ANSI_CYAN);
        }
        System.out.print("DummyDequeuer: " + o.toString());
        System.out.println(ANSI_RESET);
      } catch (InterruptedException e) {
      }
    }
  }
}