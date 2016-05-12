package com.house.research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptFunc {

	
	
	double[] mBestStrategy;
	
	public OptFunc(){
		
	}
	
	public double[] getBestStrategy() {
		return mBestStrategy;
	}


	public void setBestStrategy(double[] bestStrategy) {
		mBestStrategy = bestStrategy;
	}

	
	public double[] IWantConvenience(List<Appliance>  appliances, double[] energyPrices, double budget){
		List<Appliance> shifteable = new ArrayList<Appliance>();
		double[] costPerHourApp = new double[24];
		Arrays.fill(costPerHourApp, 0);
		
		//Get shifteables appliances
		for (int i = 0; i< appliances.size(); i++){
			if (shifteable.get(i).isShifteable()){ //If it is shifteable
				shifteable.add(i, appliances.get(i)); //add it to our list
				for (int j = 0; j < 24; j++){
					costPerHourApp[j] = ((shifteable.get(i).getConsumption(j)) * energyPrices[j]); 			
				}
				appliances.get(i).setCostPerHour(costPerHourApp);//Calculate daily of appliance
				double totalCost = 0;
				for (double h: costPerHourApp){
					totalCost+= h;
				}
				appliances.get(i).setDailyCost(totalCost); //Store cost
				
			}
		}
		
		
		
		
		/*
		 * Actual convenience game
		 * The objective is to prioritize weighted appliances
		 */
		
		//Step 1: calculate cost per hour of each appliance
		
		
		
		
		return energyPrices;
		
	}
	
	public double[] IWantHappiness(double[] consumptionSchedule, double[] energyPrices){
		return energyPrices;
		
	}
	
}
