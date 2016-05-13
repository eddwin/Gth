package com.house.research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Function {

	
	public Function() {
		
		
	}
	
	public double houseConsumption(House home){
		Billing bill = new Billing();
		bill.billingFunction(home);	
		return bill.getHourlyBill();
		
	}
	
	public double dailyHouseConsumption(House home) {
		Billing bill = new Billing();
		bill.getDailyConsumption(home);	
		return bill.getDailyConsumption();
	}
	
	public double[] getHourlyHouseConsumption (House home){
		double [] hourlyLoad = new double[24];
		Arrays.fill(hourlyLoad, 0);
		Billing bill = new Billing();
		double[] casaLoad = bill.getHourlyConsumption(home);
			for (int i = 0; i < 24; i++){
				hourlyLoad[i] = hourlyLoad[i] + casaLoad[i];
			}
		return hourlyLoad;	
	}
	
	public double CalculateNonShiftBudget(List<Appliance> appliances){
		double globalConsum = 0;
		for (Appliance app: appliances){
			if (app.isOn() && (app.isShifteable() == false)){
				globalConsum+= app.getDailyCost();
			}
			
		}
		return 0;
		
	}
	
	public void IWantConvenience(List<Appliance>  appliances, double[] energyPrices, double budget){
		/*
		 * In here, weight is the most important thing
		 */
		
		
		
		double cost = 0;
		List<Appliance> shifteable = new ArrayList<Appliance>();
		shifteable.addAll(getShifteableDevices(appliances));
		Map<String, Appliance> order = new HashMap<String, Appliance>();

		
		//Create a dictionary of weights
		for(int i = 0; i<shifteable.size();i++ ){
			int weight = shifteable.get(i).getWeight();
			order.put(Integer.toString(weight), shifteable.get(i));
		}
		
		//Get max weight
		
		int importance = 8;
		double totalConsumption = 0;
		switch(importance){
			
		case 8:
			//If the highest prioritize appliance can't be turned on, then nothing can
			if ((order.get(Integer.toString(importance)).getDailyCost() + totalConsumption) > budget){
				for (int i = 0 ; i < importance + 1; i++){
					order.get(Integer.toString(i)).setOn(false);; //Turn off everything
				}
				break;
			}else{
				//If it can, then evaluate the next one
				totalConsumption += order.get(Integer.toString(importance)).getDailyCost();
				importance = importance - 1;
			}
			
		case 7:
			//Evaluate the second most important appliance
			if ((order.get(Integer.toString(importance)).getDailyCost() + totalConsumption) > budget){
				for (int i = 0 ; i < importance + 1; i++){
					order.get(Integer.toString(i)).setOn(false);; //Turn off everything
				}
				break;
			}else{
				//If it can, then evaluate the next one
				totalConsumption += order.get(Integer.toString(importance)).getDailyCost();
				importance = importance - 1;
			}
		case 6:
			//Evaluate the third most important appliance
			if ((order.get(Integer.toString(importance)).getDailyCost() + totalConsumption) > budget){
				for (int i = 0 ; i < importance + 1; i++){
					order.get(Integer.toString(i)).setOn(false);; //Turn off everything
				}
				break;
			}else{
				//If it can, then evaluate the next one
				totalConsumption += order.get(Integer.toString(importance)).getDailyCost();
				importance = importance - 1;
			}
		case 5:
			//Evaluate the fourth most important appliance
			if ((order.get(Integer.toString(importance)).getDailyCost() + totalConsumption) > budget){
				for (int i = 0 ; i < importance + 1; i++){
					order.get(Integer.toString(i)).setOn(false);; //Turn off everything
				}
				break;
			}else{
				//If it can, then evaluate the next one
				totalConsumption += order.get(Integer.toString(importance)).getDailyCost();
				importance = importance - 1;
			}
		case 4:
			//Evaluate the fifth most important appliance
			if ((order.get(Integer.toString(importance)).getDailyCost() + totalConsumption) > budget){
				for (int i = 0 ; i < importance + 1; i++){
					order.get(Integer.toString(i)).setOn(false);; //Turn off everything
				}
				break;
			}else{
				//If it can, then evaluate the next one
				totalConsumption += order.get(Integer.toString(importance)).getDailyCost();
				importance = importance - 1;
			}
		case 3:
			//Evaluate the sixth most important appliance
			if ((order.get(Integer.toString(importance)).getDailyCost() + totalConsumption) > budget){
				for (int i = 0 ; i < importance + 1; i++){
					order.get(Integer.toString(i)).setOn(false);; //Turn off everything
				}
				break;
			}else{
				//If it can, then evaluate the next one
				totalConsumption += order.get(Integer.toString(importance)).getDailyCost();
				importance = importance - 1;
			}
		case 2:
			//Evaluate the last most important appliance
			if ((order.get(Integer.toString(importance)).getDailyCost() + totalConsumption) > budget){
				for (int i = 0 ; i < importance + 1; i++){
					order.get(Integer.toString(i)).setOn(false);; //Turn off everything
				}
				break;
			}else{
				//If it can, then evaluate the next one
				totalConsumption += order.get(Integer.toString(importance)).getDailyCost();
				importance = importance - 1;
			}
		case 1:
			//Evaluate the 0 most important appliance
			if ((order.get(Integer.toString(importance)).getDailyCost() + totalConsumption) > budget){
				for (int i = 0 ; i < importance + 1; i++){
					order.get(Integer.toString(i)).setOn(false);; //Turn off everything
				}
				break;
			}else{
				totalConsumption += order.get(Integer.toString(importance)).getDailyCost();
				break;
			}
		}
		if (totalConsumption == 0) { //Nothing has changed
			
			
		}
		
	}
	
	public List<Appliance> getNonShifteableDevices (List<Appliance>  allAppliances) {
		List<Appliance> shifteable = new ArrayList<Appliance>();
		
		for (int i = 0; i< allAppliances.size(); i++){
			if (allAppliances.get(i).isShifteable()==false){ //If it is shifteable
				shifteable.add(allAppliances.get(i));//add it to our list
			}
		}
		
		return shifteable;
		
	}
	
	public List<Appliance> getShifteableDevices (List<Appliance>  allAppliances) {
		List<Appliance> nonShifteable = new ArrayList<Appliance>();
		
		for (int i = 0; i< allAppliances.size(); i++){
			if (allAppliances.get(i).isShifteable()){ //If it is non shifteable
				nonShifteable.add(allAppliances.get(i)); //add it to our list
			}
		}
		
		return nonShifteable;
		
	}
	
}
