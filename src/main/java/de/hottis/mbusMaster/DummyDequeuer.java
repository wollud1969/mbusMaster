package de.hottis.mbusMaster;

import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DummyDequeuer extends Thread {
	static final Logger logger = LogManager.getRootLogger();

  private BlockingQueue<String> queue;

  public DummyDequeuer(BlockingQueue<String> queue) {
    super("DummyDequeuer");

    this.queue = queue;
  }

  public void run() {
    while(true) {
      try {
        String o = this.queue.take();
        System.out.println("DummyDequeuer: " + o);
      } catch (InterruptedException e) {
      }
    }
  }
}