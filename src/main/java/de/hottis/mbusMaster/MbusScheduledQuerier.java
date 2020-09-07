package de.hottis.mbusMaster;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MbusScheduledQuerier {
	static final Logger logger = LogManager.getRootLogger();

  private ArrayList<MbusDevice> devices;

  public MbusScheduledQuerier() {
    this.devices = new ArrayList<>();

  }



}