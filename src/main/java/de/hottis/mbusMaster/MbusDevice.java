package de.hottis.mbusMaster;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.jmbus.DataRecord;
import org.openmuc.jmbus.MBusMessage;
import org.openmuc.jmbus.VariableDataStructure;
import org.openmuc.jmbus.DecodingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


abstract public class MbusDevice {
	static final protected Logger logger = LogManager.getRootLogger();

  private String name;
  private byte address;
  private int queryPeriod; // in seconds

  protected List<DataRecord> dataRecords;
  protected boolean validlyParsed;

  static protected class DataPoint {
    private String name;
    private int index;
    
    protected DataPoint(String name, int index) {
      this.name = name;
      this.index = index;
    }
  }

  protected ArrayList<DataPoint> dataPoints;

  protected MbusDevice(String name, byte address, int queryPeriod) {
    this.name = name;
    this.address = address;
    this.queryPeriod = queryPeriod;
    this.validlyParsed = false;
    this.dataPoints = new ArrayList<>();
  }

  public void parse(byte[] frame) throws MbusException {
    try {
      MBusMessage mbusMsg = MBusMessage.decode(frame, frame.length);
      VariableDataStructure variableDataStructure = mbusMsg.getVariableDataResponse();
      variableDataStructure.decode();
      this.dataRecords = variableDataStructure.getDataRecords();
      logger.debug("Datarecords parsed");
      this.validlyParsed = true;
    } catch (IOException | DecodingException e) {
      this.validlyParsed = false;
      String msg = "Error while coding frame: " + e.toString();
      logger.error(msg);
      throw new MbusException(msg);
    }
  }

  public String dataToString() {
    StringBuffer sb = new StringBuffer();
    for (DataPoint dp : this.dataPoints) {
      sb.append("{");
      sb.append(dp.name + ":" + this.dataRecords.get(dp.index).getScaledDataValue());
      sb.append("}");
    }
    return sb.toString();
  }

  public String toString() {
    return this.innerString(true);
  }

  public String shortString() {
    return this.innerString(false);
  }

  private String innerString(boolean longOutput) {
    StringBuffer sb = new StringBuffer();
    sb.append(this.getClass().getName() + " [");
    sb.append("<name=" + this.getName() + "><address=" + this.getAddress() + ">");
    if (longOutput && this.validlyParsed) {
      sb.append(this.dataToString());
    }
    sb.append("]");
    return sb.toString();
  }

  public String getName() {
    return this.name;
  }

  public byte getAddress() {
    return this.address;
  }

  public double getValue(String dataPointName) throws MbusException {
    if (! validlyParsed) {
      throw new MbusException("trying to get value before valid parsing");
    }
    for (DataPoint dp : this.dataPoints) {
      if (dataPointName.equals(dp.name)) {
        return this.dataRecords.get(dp.index).getScaledDataValue();
      }
    }
    throw new MbusException("dataPoint " + dataPointName + " in getValue not found");
  }

  abstract public ADataObject getDataObject() throws MbusException;
}