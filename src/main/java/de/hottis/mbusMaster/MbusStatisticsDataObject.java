package de.hottis.mbusMaster;

import java.util.HashMap;

public class MbusStatisticsDataObject extends ADataObject {
	private static final long serialVersionUID = 1L;
	static final String ERROR_CNT_KEY = "error";
	static final String SUCCESS_CNT_KEY = "success";
	static final String ERROR_RATIO_KEY = "errorRatio";
	static final String TIME_SINCE_LAST_SHUTDOWN_KEY = "timeSinceLastShutdown";
	static final String MEANTIME_BETWEEN_SHUTDOWNS_KEY = "meantimeBetweenShutdowns";
	static final String SHUTDOWNS_KEY = "shutdowns";
	static final String TABLE_NAME = "Statistics";
  static final String KIND_NAME = "Statistics";
	
	public MbusStatisticsDataObject(String name, int error, int success, int shutdowns,
	                                double errorRatio, int timeSinceLastShutdown, int meantimeBetweenShutdowns) {
		super(name, KIND_NAME);
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(ERROR_CNT_KEY, error);
		values.put(SUCCESS_CNT_KEY, success);
		values.put(ERROR_RATIO_KEY, errorRatio);
		values.put(TIME_SINCE_LAST_SHUTDOWN_KEY, timeSinceLastShutdown);
		values.put(MEANTIME_BETWEEN_SHUTDOWNS_KEY, meantimeBetweenShutdowns);
		values.put(SHUTDOWNS_KEY, shutdowns);
		setValues(values);
	}
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
}
