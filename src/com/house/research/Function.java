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
		return globalConsum;
		
	}
	
	public void IWantConvenience(List<Appliance>  appliances, double[] energyPrices, double budget){
		// In here, weight is the most important thing
		 
				List<Appliance> shifteable = new ArrayList<Appliance>();
				shifteable.addAll(getShifteableDevices(appliances));
				Map<String, Appliance> order = new HashMap<String, Appliance>();

				
				//Create a dictionary of weights
				for(int i = 0; i<shifteable.size();i++ ){
					int weight = shifteable.get(i).getWeight();
					order.put(Integer.toString(weight), shifteable.get(i));
				}
				
				//Get max weight
				
				int importance = shifteable.size();
				double totalConsumption = 0;
				Appliance[] app = null;
				int[] newSchedule = new int[24];

				for (int i = importance; i > 0; i--){
					//Allocate resource for highest appliance
					newSchedule = BestAllocate(order.get(Integer.toString(i)),energyPrices, order.get(Integer.toString(i)).getOperationalHours());
					//Calculate consumption
					double dailyCost = calcuateDailyCost(order.get(Integer.toString(i)), energyPrices, newSchedule);
					//Check if the added cost of this appliance overuns the budget for this house
					if ((dailyCost + totalConsumption) <= budget){
						//If it does, then it can be afforded to be on during the day
						order.get(Integer.toString(i)).setConsumptionSchedule(newSchedule);
						//Turn it on
						order.get(Integer.toString(i)).setOn(true);
						//Update new consumption total for this round
						
					}
				}
				
				
				
				
				
				
				int i = 0;
				switch(importance){
				case 8:
					
					newSchedule = BestAllocate(order.get(Integer.toString(importance)),energyPrices, order.get(Integer.toString(importance)).getOperationalHours());
					
					//Calculate consumption
					double dailyCost = calcuateDailyCost(order.get(Integer.toString(importance)), energyPrices, newSchedule);
					//if Daily cost of this appliance
					if ((dailyCost + totalConsumption) <= budget){
						//It can be afforded
						//Store new schedule
						order.get(Integer.toString(importance)).setConsumptionSchedule(newSchedule);
						//Store new daily cost
						order.get(Integer.toString(importance)).setDailyCost(dailyCost);
						//make it double sure
						order.get(Integer.toString(importance)).setOn(true);
						//update new consumption total for this round
						totalConsumption += dailyCost;
						//Go to next appliance
						importance = importance - 1;
						
					}else {	
						//It can't be afforded, turn it off
						order.get(Integer.toString(importance)).setOn(false);; //Turn off everything
						
						//Go to next appliance
						importance = importance - 1;	
						
						
					}
					
				case 7:	
					//Allocate resource for highest appliance
					Arrays.fill(newSchedule, 0);
					newSchedule = BestAllocate(order.get(Integer.toString(importance)),energyPrices, order.get(Integer.toString(importance)).getOperationalHours());
					
					//Calculate consumption
					dailyCost = 0;
					dailyCost = calcuateDailyCost(order.get(Integer.toString(importance)), energyPrices, newSchedule);
					
					//if Daily cost of this appliance
					if ((dailyCost + totalConsumption) <= budget){
						//It can be afforded
						//Store new schedule
						order.get(Integer.toString(importance)).setConsumptionSchedule(newSchedule);
						//Store new daily cost
						order.get(Integer.toString(importance)).setDailyCost(dailyCost);
						//make it double sure
						order.get(Integer.toString(importance)).setOn(true);
						//update new consumption total for this round
						totalConsumption += dailyCost;
						//Go to next appliance
						importance = importance - 1;
						
					}else {	
						//It can't be afforded, turn it off
						order.get(Integer.toString(importance)).setOn(false);; //Turn off everything
						//Go to next appliance
						importance = importance - 1;		
						
					}
				case 6:
					//Allocate resource for highest appliance
					Arrays.fill(newSchedule, 0);
					newSchedule = BestAllocate(order.get(Integer.toString(importance)),energyPrices, order.get(Integer.toString(importance)).getOperationalHours());
					
					//Calculate consumption
					dailyCost = 0;
					dailyCost = calcuateDailyCost(order.get(Integer.toString(importance)), energyPrices, newSchedule);
					
					//if Daily cost of this appliance
					if ((dailyCost + totalConsumption) <= budget){
						//It can be afforded
						//Store new schedule
						order.get(Integer.toString(importance)).setConsumptionSchedule(newSchedule);
						//Store new daily cost
						order.get(Integer.toString(importance)).setDailyCost(dailyCost);
						//make it double sure
						order.get(Integer.toString(importance)).setOn(true);
						//update new consumption total for this round
						totalConsumption += dailyCost;
						//Go to next appliance
						importance = importance - 1;
						
					}else {	
						//It can't be afforded, turn it off
						order.get(Integer.toString(importance)).setOn(false);; //Turn off everything
						
						//Go to next appliance
						importance = importance - 1;		
						
					}
				case 5:
					//Allocate resource for highest appliance
					Arrays.fill(newSchedule, 0);
					newSchedule = BestAllocate(order.get(Integer.toString(importance)),energyPrices, order.get(Integer.toString(importance)).getOperationalHours());
					
					//Calculate consumption
					dailyCost = 0;
					dailyCost = calcuateDailyCost(order.get(Integer.toString(importance)), energyPrices, newSchedule);
					
					//if Daily cost of this appliance
					if ((dailyCost + totalConsumption) <= budget){
						//It can be afforded
						//Store new schedule
						order.get(Integer.toString(importance)).setConsumptionSchedule(newSchedule);
						//Store new daily cost
						order.get(Integer.toString(importance)).setDailyCost(dailyCost);
						//make it double sure
						order.get(Integer.toString(importance)).setOn(true);
						//update new consumption total for this round
						totalConsumption += dailyCost;
						//Go to next appliance
						importance = importance - 1;
						
					}else {	
						//It can't be afforded, turn it off
						order.get(Integer.toString(importance)).setOn(false);; //Turn off everything
						
					
						//Go to next appliance
						importance = importance - 1;		
						
					}
				case 4:
					//Allocate resource for highest appliance
					Arrays.fill(newSchedule, 0);
					newSchedule = BestAllocate(order.get(Integer.toString(importance)),energyPrices, order.get(Integer.toString(importance)).getOperationalHours());
					
					//Calculate consumption
					dailyCost = 0;
					dailyCost = calcuateDailyCost(order.get(Integer.toString(importance)), energyPrices, newSchedule);
					
					//if Daily cost of this appliance
					if ((dailyCost + totalConsumption) <= budget){
						//It can be afforded
						//Store new schedule
						order.get(Integer.toString(importance)).setConsumptionSchedule(newSchedule);
						//Store new daily cost
						order.get(Integer.toString(importance)).setDailyCost(dailyCost);
						//make it double sure
						order.get(Integer.toString(importance)).setOn(true);
						//update new consumption total for this round
						totalConsumption += dailyCost;
						//Go to next appliance
						importance = importance - 1;
						
					}else {	
						//It can't be afforded, turn it off
						order.get(Integer.toString(importance)).setOn(false);; //Turn off everything
						
						
						//Go to next appliance
						importance = importance - 1;		
						
					}
				case 3:
					//Allocate resource for highest appliance
					Arrays.fill(newSchedule, 0);
					newSchedule = BestAllocate(order.get(Integer.toString(importance)),energyPrices, order.get(Integer.toString(importance)).getOperationalHours());
					
					//Calculate consumption
					dailyCost = 0;
					dailyCost = calcuateDailyCost(order.get(Integer.toString(importance)), energyPrices, newSchedule);
					
					//if Daily cost of this appliance
					if ((dailyCost + totalConsumption) <= budget){
						//It can be afforded
						//Store new schedule
						order.get(Integer.toString(importance)).setConsumptionSchedule(newSchedule);
						//Store new daily cost
						order.get(Integer.toString(importance)).setDailyCost(dailyCost);
						//make it double sure
						order.get(Integer.toString(importance)).setOn(true);
						//update new consumption total for this round
						totalConsumption += dailyCost;
						//Go to next appliance
						importance = importance - 1;
						
					}else {	
						//It can't be afforded, turn it off
						order.get(Integer.toString(importance)).setOn(false);; //Turn off everything
						
						
						//Go to next appliance
						importance = importance - 1;		
						
					}
				case 2:
					//Allocate resource for highest appliance
					Arrays.fill(newSchedule, 0);
					newSchedule = BestAllocate(order.get(Integer.toString(importance)),energyPrices, order.get(Integer.toString(importance)).getOperationalHours());
					
					//Calculate consumption
					dailyCost = 0;
					dailyCost = calcuateDailyCost(order.get(Integer.toString(importance)), energyPrices, newSchedule);
					
					//if Daily cost of this appliance
					if ((dailyCost + totalConsumption) <= budget){
						//It can be afforded
						//Store new schedule
						order.get(Integer.toString(importance)).setConsumptionSchedule(newSchedule);
						//Store new daily cost
						order.get(Integer.toString(importance)).setDailyCost(dailyCost);
						//make it double sure
						order.get(Integer.toString(importance)).setOn(true);
						//update new consumption total for this round
						totalConsumption += dailyCost;
						//Go to next appliance
						importance = importance - 1;
						
					}else {	
						//It can't be afforded, turn it off
						order.get(Integer.toString(importance)).setOn(false);; //Turn off everything
						
						
						//Go to next appliance
						importance = importance - 1;		
						
					}
				case 1:
					//Allocate resource for highest appliance
					Arrays.fill(newSchedule, 0);
					newSchedule = BestAllocate(order.get(Integer.toString(importance)),energyPrices, order.get(Integer.toString(importance)).getOperationalHours());
					
					//Calculate consumption
					dailyCost = 0;
					dailyCost = calcuateDailyCost(order.get(Integer.toString(importance)), energyPrices, newSchedule);
					
					//if Daily cost of this appliance
					if ((dailyCost + totalConsumption) <= budget){
						//It can be afforded
						//Store new schedule
						order.get(Integer.toString(importance)).setConsumptionSchedule(newSchedule);
						//Store new daily cost
						order.get(Integer.toString(importance)).setDailyCost(dailyCost);
						//make it double sure
						order.get(Integer.toString(importance)).setOn(true);
						//update new consumption total for this round
						totalConsumption += dailyCost;

						//Go to next appliance
						importance = importance - 1;
						
					}else {	
						//It can't be afforded, turn it off
						
						order.get(Integer.toString(importance)).setOn(false);; //Turn off everything
						
						//Go to next appliance
						importance = importance - 1;		
						
					}
					
					break;
					
				
				
				}
	}	
	
	
	
	
	public int [] BestAllocate (Appliance app, double[] energyPrices, int hours ){
		int [] bestResponse = new int[24];
		double [] bestPeriod = new double[hours];
		int [] bestAllocate = new int[24];
		double bestCost = 200000.0;
		double cost = 0;
		if (hours == 1) {
			//Simplex
			double small = energyPrices[0];
			int index = 0;
			for (int i = 0; i < energyPrices.length; i++){
				if (energyPrices[i] < small){
					small = energyPrices[i];
					index = i;
					
				}
			}
			Arrays.fill(bestAllocate, 0);
			bestAllocate[index] = 1;
			
			
			
		}else if (hours > 1){
			//Allocate 
			for (int i = 0; i < 24 - hours; i++ ){
				Arrays.fill(bestPeriod, 0);
				for (int j = 0; j < hours ; j++){
					bestPeriod[j] =  energyPrices[i + j];
					bestResponse[j] = i+j;
				}
				for (double k:bestPeriod ){
					cost += k;
				}
				if (cost < bestCost  ){
					bestCost = cost;
					for (int k = 0; k < bestResponse.length; k++ ){
						if (bestResponse[k] > 0){
							bestAllocate[k] = 1;
						}
						else{
							bestAllocate[k] = 0;
						}
					}
					
				}
			}
			
			
		}
		
		return bestAllocate;
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
	
	
	
	
	public double getShiftConsumption (List<Appliance> allApliances){
		List<Appliance> shifteable = new ArrayList<Appliance>();
		double[] schedule = new double[24];
		double shiftCon = 0;
		shifteable = getShifteableDevices(allApliances);
		for (Appliance a:shifteable){
			schedule = a.getEnergyToConsume();
			for (double d: schedule ){
				shiftCon+=d;
			}
		}
		return shiftCon;
	}
	
	public double getTotalConsumption (List<Appliance>allApliances){
		double cons = 0;
		for (Appliance a:allApliances){
			if (a.isOn()){
				cons+=a.getDailyCost();
			}
			
		}
		return cons;
	}
	
	public double getLh24(double[] hourlyLoad){
		double lh = 0;
		for(double h:hourlyLoad){
			lh+=h;
		}
		
		return lh;
	}
	
	public void storeDailyConsumption(Appliance app, double[]hourlyPrices){
		double dailyConsumption = 0;
		
		double energy = app.getKwh();
		int[] schedule = app.getConsumptionSchedule();
		for (int i = 0; i < 23; i++){
			dailyConsumption+= (schedule[i]*energy*hourlyPrices[i]);
		}
		app.setDailyCost(dailyConsumption);
		
		
	}
	
	public void calculateHourlyMoneyConsumption(List<Appliance>allAppliances, double[]hourlyPrices){
		double dailyCost = 0;
		for(int i =0; i < allAppliances.size(); i++){
			storeDailyConsumption(allAppliances.get(i), hourlyPrices);
			allAppliances.get(i).setDailyCost(dailyCost);
		}	
		
	}
	public void showOffAppliances(List<Appliance> allAppliances, String agentName){
		int flag = 0;
		
		for(Appliance a: allAppliances){
			if(a.isOn() == false){
				flag++;
				System.out.println(a.getName() + " is off for " + agentName);
			}
		}
		
		
	}

}
