package de.hottis.mbusMaster;

import java.util.HashMap;

public class ElectricEnergyDataObject extends ADataObject {
	private static final long serialVersionUID = 1L;
	static final String ENERGY_KEY = "energy";
	static final String POWER_KEY = "power";
	static final String ERROR_RATIO_KEY = "errorRatio";
	static final String TABLE_NAME = "ElectricEnergy";
  static final String KIND_NAME = "ElectricEnergy";
	
	public ElectricEnergyDataObject(String name, String status, String statusText, double energy, double power, double errorRatio) {
		super(name, KIND_NAME, status, statusText);
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(ENERGY_KEY, energy);
		values.put(POWER_KEY, power);
		values.put(ERROR_RATIO_KEY, errorRatio);
		setValues(values);
	}
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
}
