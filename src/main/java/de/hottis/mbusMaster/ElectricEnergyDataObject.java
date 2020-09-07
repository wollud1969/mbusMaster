package de.hottis.mbusMaster;

import java.util.HashMap;

public class ElectricEnergyDataObject extends ADataObject {
	private static final long serialVersionUID = 1L;
	static final String ENERGY_KEY = "energy";
	static final String POWER_KEY = "power";
	static final String TABLE_NAME = "ElectricEnergy";
  static final String KIND_NAME = "ElectricEnergy";
	
	public ElectricEnergyDataObject(String name, double energy, double power) {
		super(name, KIND_NAME);
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(ENERGY_KEY, energy);
		values.put(POWER_KEY, power);
		setValues(values);
	}
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
}
