package com.house.research;

public class Appliance {
	
	double kwh;
	int[] consumptionSchedule;
	double[] energyToConsume;
	String name;
	
	int weight;
	boolean on;
	double[] costPerHour;
	double dailyCost;
	

	double dailyEnergy;

	public double getDailyEnergy() {
		return dailyEnergy;
	}
	public void setDailyEnergy(double dailyEnergy) {
		this.dailyEnergy = dailyEnergy;
	}
	
	public double getDailyCost() {
		return dailyCost;
	}
	public void setDailyCost(double dailyCost) {
		this.dailyCost = dailyCost;
	}
	public double[] getCostPerHour() {
		return costPerHour;
	}
	public void setCostPerHour(double[] costPerHour) {
		this.costPerHour = costPerHour;
	}
	public boolean isShifteable() {
		return shifteable;
	}
	public void setShifteable(boolean shifteable) {
		this.shifteable = shifteable;
	}

	boolean shifteable;
	
	int operationalHours;
	

	
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getOperationalHours() {
		return operationalHours;
	}
	public void setOperationalHours(int operationalHours) {
		this.operationalHours = operationalHours;
	}

	public boolean isOn() {
		return on;
	}
	public void setOn(boolean on) {
		this.on = on;
	}

	
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
	
	
	public int activeOrNot (int hour){
		
		if (this.consumptionSchedule[hour] == 1){
			return 1;
		} 
		else{
			return 0;
		}
				
	}
	
	public double getConsumption (int hour){
		
		return energyToConsume[hour];
		
	}
		
}