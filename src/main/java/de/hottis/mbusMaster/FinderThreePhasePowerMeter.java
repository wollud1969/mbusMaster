package de.hottis.mbusMaster;

public class FinderThreePhasePowerMeter extends MbusDevice {
 
  public FinderThreePhasePowerMeter(String name, Byte address, Integer queryPeriod) {
    this(name, address.byteValue(), queryPeriod.intValue());
  }

  public FinderThreePhasePowerMeter(String name, byte address, int queryPeriod) {
    super(name, address, queryPeriod);
    this.dataPoints.add(new DataPoint("energy", 0));
    this.dataPoints.add(new DataPoint("voltage1", 4));
    this.dataPoints.add(new DataPoint("current1", 5));
    this.dataPoints.add(new DataPoint("activePower1", 6));
    this.dataPoints.add(new DataPoint("reactivePower1", 7));
    this.dataPoints.add(new DataPoint("voltage2", 8));
    this.dataPoints.add(new DataPoint("current2", 9));
    this.dataPoints.add(new DataPoint("activePower2", 10));
    this.dataPoints.add(new DataPoint("reactivePower2", 11));
    this.dataPoints.add(new DataPoint("voltage3", 12));
    this.dataPoints.add(new DataPoint("current3", 13));
    this.dataPoints.add(new DataPoint("activePower3", 14));
    this.dataPoints.add(new DataPoint("reactivePower3", 15));
    this.dataPoints.add(new DataPoint("activePowerTotal", 17));
    this.dataPoints.add(new DataPoint("reactivePowerTotal", 18));
  }

  public ADataObject getDataObject() throws MbusException {
    return new ElectricEnergyDataObject(this.getName(), this.getValue("energy"), this.getValue("activePowerTotal"), this.getErrorRatio());
  }
}