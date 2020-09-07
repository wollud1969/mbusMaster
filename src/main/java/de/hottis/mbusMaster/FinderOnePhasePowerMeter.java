package de.hottis.mbusMaster;

public class FinderOnePhasePowerMeter extends MbusDevice {
  public FinderOnePhasePowerMeter(String name, byte address, int queryPeriod) {
    super(name, address, queryPeriod);
    this.dataPoints.add(new DataPoint("energy", 0));
    this.dataPoints.add(new DataPoint("voltage", 2));
    this.dataPoints.add(new DataPoint("current", 3));
    this.dataPoints.add(new DataPoint("activePower", 4));
    this.dataPoints.add(new DataPoint("reactivePower", 5));
  }
}