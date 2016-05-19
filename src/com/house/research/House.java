package com.house.research;

import java.util.*;

public class House {
	//TODO: variables
	
	List<Appliance> appliances;
	public void setAppliances(List<Appliance> appliances) {
		this.appliances = appliances;
	}



	
	
	double houseConsumption;
	double budget;
	String strategy;
	





	double houseLoad;

	//Getters and Setters
	
	public double getHouseLoad() {
		return houseLoad;
	}

	public void setHouseLoad(double houseLoad) {
		this.houseLoad = houseLoad;
	}
	
	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
	
	public double getBudget() {
		return budget;
	}

	public void setBudget(double budget) {
		this.budget = budget;
	}
	
	public List<Appliance> getAppliances() {
		
		return appliances;
	}
	
	public double getHouseConsumption() {
		return houseConsumption;
	}


	public void setHouseConsumption(double consumo) {
		this.houseConsumption = consumo;
	}
	//////
	
	
	
	
	public House (int numOfShiftApp, int numOfNonShiftApp){
		List<Appliance> appliances = new ArrayList<Appliance>(); 
		ApplianceFactory factory = new ApplianceFactory();
		
		List<Appliance> shifteable = factory.createShifteableAppliance(numOfShiftApp);
		for (Appliance app:shifteable ){
			appliances.add(app);
		}
		List<Appliance> nonShifteable = factory.createnonShifteableAppliance(numOfNonShiftApp);
		
		for (Appliance app:nonShifteable ){
			appliances.add(app);
		}
		setAppliances(appliances);
			
	}
	
	
	
	
	
	
	
	

}
