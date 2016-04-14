package com.house.research;

public class Appliance {
	
	double kwh;
	int[] consumptionSchedule;
	double[] energyToConsume;
	String name;
	
	public double getKwh() {
		return kwh;
	}
	public void setKwh(double kwh) {
		this.kwh = kwh;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public int[] getConsumptionSchedule() {
		return consumptionSchedule;
	}
	public void setConsumptionSchedule(int[] consumptionSchedule) {
		this.consumptionSchedule = consumptionSchedule;
	}
	public double[] getEnergyToConsume() {
		return energyToConsume;
	}
	public void setEnergyToConsume(double[] energyToConsume) {
		this.energyToConsume = energyToConsume;
	}
	

}
