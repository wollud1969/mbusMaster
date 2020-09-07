package de.hottis.mbusMaster;

import java.util.HashMap;

public class MbusStatisticsDataObject extends ADataObject {
	private static final long serialVersionUID = 1L;
	static final String TOTAL_CNT_KEY = "total";
	static final String ERROR_CNT_KEY = "error";
	static final String SUCCESS_CNT_KEY = "success";
	static final String TABLE_NAME = "Statistics";
  static final String KIND_NAME = "Statistics";
	
	public MbusStatisticsDataObject(String name, int total, int error, int success) {
		super(name, KIND_NAME);
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(TOTAL_CNT_KEY, total);
		values.put(ERROR_CNT_KEY, error);
		values.put(SUCCESS_CNT_KEY, success);
		setValues(values);
	}
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
}
