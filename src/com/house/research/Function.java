package com.house.research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Function {

	
	public Function() {
		
		
	}
	

	public double[] getHourlyConsumption(List<Appliance>appliances){
		double [] hourlyLoad = new double[24];
		Arrays.fill(hourlyLoad, 0);
		for (Appliance a:appliances ){
			if (a.isOn()){
				int[] consumptionSchedule = a.getConsumptionSchedule();
				double kwh = a.getKwh();
				for (int i =0; i<24; i++){
					hourlyLoad[i]+=  consumptionSchedule[i]*kwh;
				}
			}
			
		}
		return hourlyLoad;
	}
	
	public double[] getHourlyHouseConsumption (House home){
		double [] hourlyLoad = new double[24];
		Arrays.fill(hourlyLoad, 0);
		Billing bill = new Billing();
		double[] casaLoad = bill.getHourlyConsumption(home);
			for (int i = 0; i < 24; i++){
				hourlyLoad[i] = hourlyLoad[i] + casaLoad[i];
			}
			double cargaCasa = 0;
			for (int i = 0; i<24; i++){
				cargaCasa+=hourlyLoad[i];
			}
			home.setHouseLoad(cargaCasa);
			
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
	public int counOnAppliances(List<Appliance> apps){
		int ctr = 0;
		
		for(Appliance a: apps){
			if(a.isShifteable() && a.isOn()){
				ctr++;
			}
		}
		return ctr;
	}
	public void IWantConvenience(List<Appliance>  appliances, double[] foreignLoad, double[] localLoad, double budget, double alpha){
		
		
	
		List<Appliance> shifteable = new ArrayList<Appliance>();
		shifteable.addAll(getShifteableDevices(appliances));
		Map<String, Appliance> order = new HashMap<String, Appliance>();
		double [] energyPrices = projectHourlyPrices(foreignLoad, localLoad, alpha);		
		//Create a dictionary of weights
		for(int i = 0; i< shifteable.size();i++ ){
				int weight = shifteable.get(i).getWeight();
				order.put(Integer.toString(weight), shifteable.get(i));
		}
		//Get max weight
		int onCtr = 0;
		int importance = order.size();
		double totalConsumption = 0;
		//Get onAppliances 
		int[] newSchedule = new int[24];
		
			totalConsumption = 0;
			int costctr = 0;
			for (int i = importance; i > 0; i--){
			double lastCost = order.get(Integer.toString(i)).getDailyCost();	
				//Allocate resource for highest appliance
				newSchedule = BestAllocate(energyPrices, order.get(Integer.toString(i)).getOperationalHours(), 0, order.get(Integer.toString(i)).getDailyCost(),order.get(Integer.toString(i)).getConsumptionSchedule());
				//Calculate consumption
			
				double dailyCost = calcuateDailyCost(order.get(Integer.toString(i)).getKwh(), energyPrices, newSchedule);
				
				
				
				
				//Check if the added cost of this appliance overuns the budget for this house
				//System.out.println("daily cost is " + dailyCost + " total consumption is " +totalConsumption + " budget is " + budget);
				if ((dailyCost + totalConsumption) <= budget){
					//If it does, then it can be afforded to be on during the day
					order.get(Integer.toString(i)).setConsumptionSchedule(newSchedule);
				
					//Turn it on
					order.get(Integer.toString(i)).setOn(true);
					//Update new consumption total for this round
				//	System.out.println("old daily cost" + order.get(Integer.toString(i)).getDailyCost() + "new Daily Cost: " + dailyCost);
					if (order.get(Integer.toString(i)).getDailyCost() < dailyCost ){
						costctr++;

					}
					order.get(Integer.toString(i)).setDailyCost(dailyCost);
					if (lastCost < dailyCost){
						System.out.println("Stored a more expensive cost");
					}
					else{
						System.out.println("Saved money!");

					}
					//Update needed energy
					double load = 0;
					for (int k = 0; k<24;k++){
						load += (newSchedule[k] *order.get(Integer.toString(i)).getKwh());
					}
					order.get(Integer.toString(i)).setDailyEnergy(load);
					
					
					//Update consumption total for this round
					totalConsumption += dailyCost;
					//Project next prices
					
					//recalculateHourlyLoad and project new prices
					localLoad =calculateHourlyConsumption(appliances);
					energyPrices = projectHourlyPrices(foreignLoad, localLoad, alpha );
					
					
				}
				else{
					//It can't be afforded, turn it off
					order.get(Integer.toString(i)).setOn(false); //Turn off		
					System.out.println("Turned off");
					
					
				}
			}
		
	

				
				
			
					
	}
				
	
	
public void hypo(List<Appliance> appliances, double[]foreignLoad, double[]localLoad, double budget, double alpha, int AppOn){
	List<Appliance> shifteable = new ArrayList<Appliance>();

	for (Appliance app: appliances){
		Appliance nApp = new Appliance();
		nApp = app;
	}
	
	
	
}/*
public String IWantConvenience(List<Appliance>  appliances, double[] foreignLoad, double[] localLoad, double budget, double alpha){
		
		
		// In here, weight is the most important thing
		 String result = "not-change";
		List<Appliance> shifteable = new ArrayList<Appliance>();
		shifteable.addAll(getShifteableDevices(appliances));
		Map<String, Appliance> order = new HashMap<String, Appliance>();
		double [] energyPrices = projectHourlyPrices(foreignLoad, localLoad, alpha);		
		//Create a dictionary of weights
		for(int i = 0; i< shifteable.size();i++ ){
				int weight = shifteable.get(i).getWeight();
				order.put(Integer.toString(weight), shifteable.get(i));
		}
		
		
		
		
		//Get max weight
		int onCtr = 0;
		int importance = order.size();
		double totalConsumption = 0;
		//Get onAppliances 
		int[] newSchedule = new int[24];
		int numOn = counOnAppliances(appliances);
		for (int i = importance; i > 0; i--){
			double[] hypoLocalLoad =calculateHourlyConsumption(appliances);
			double [] hypoEnergyPrices = projectHourlyPrices(foreignLoad, hypoLocalLoad, alpha );
			newSchedule = BestAllocate(hypoEnergyPrices, order.get(Integer.toString(i)).getOperationalHours(), 0, 20000.0);
			//Calculate consumption
			double dailyCost = calcuateDailyCost(order.get(Integer.toString(i)).getKwh(), hypoEnergyPrices, newSchedule);
			if ((dailyCost + totalConsumption) <= budget){
				//Would have to be turned on
				onCtr++;	
				totalConsumption += dailyCost;

				//recalculateHourlyLoad and project new prices
				hypoLocalLoad =calculateHourlyConsumption(appliances);
				hypoEnergyPrices = projectHourlyPrices(foreignLoad, hypoLocalLoad, alpha );
				
			}
		}
		System.out.println("aparatos encendiso antes: " + numOn + " encendidos ahora: " + onCtr);
		if (onCtr > numOn){
			result = "change";
			int otherCtr = 0;
			
			totalConsumption = 0;
			int costctr = 0;
			for (int i = importance; i > 0; i--){
				int originalCtr = 0;
				int newCtr =0;
				//Allocate resource for highest appliance
				newSchedule = BestAllocate(energyPrices, order.get(Integer.toString(i)).getOperationalHours(), 0, 20000.0);
				//Calculate consumption
				double dailyCost = calcuateDailyCost(order.get(Integer.toString(i)).getKwh(), energyPrices, newSchedule);
				int[] originalSchedule = order.get(Integer.toString(i)).getConsumptionSchedule();
				
				
				for (int d:originalSchedule ){
					if (d == 1){
						originalCtr++;
					}
				}
				for (int d:newSchedule ){
					if (d == 1){
						newCtr++;
					}
				}
				
			
				
				
				//Check if the added cost of this appliance overuns the budget for this house
				System.out.println("daily cost is " + dailyCost + " total consumption is " +totalConsumption + " budget is " + budget);
				if ((dailyCost + totalConsumption) <= budget){
					//If it does, then it can be afforded to be on during the day
					order.get(Integer.toString(i)).setConsumptionSchedule(newSchedule);
				
					//Turn it on
					order.get(Integer.toString(i)).setOn(true);
					otherCtr++;
					//Update new consumption total for this round
					System.out.println("old daily cost" + order.get(Integer.toString(i)).getDailyCost() + "new Daily Cost: " + dailyCost);
					if (order.get(Integer.toString(i)).getDailyCost() < dailyCost ){
						costctr++;

					}
					order.get(Integer.toString(i)).setDailyCost(dailyCost);
					//Update needed energy
					double load = 0;
					System.out.println(order.get(Integer.toString(i)).getName());
					for (int k = 0; k<24;k++){
						load += (newSchedule[k] *order.get(Integer.toString(i)).getKwh());
					}
					order.get(Integer.toString(i)).setDailyEnergy(load);
					
					
					//Update consumption total for this round
					totalConsumption += dailyCost;
					//Project next prices
					
					//recalculateHourlyLoad and project new prices
					localLoad =calculateHourlyConsumption(appliances);
					energyPrices = projectHourlyPrices(foreignLoad, localLoad, alpha );
					
					
				//	energyPrices = projectHourlyPrices(energyPrices, alpha);
				}
				else{
					//It can't be afforded, turn it off
					order.get(Integer.toString(i)).setOn(false); //Turn off		
					System.out.println("Turned off");
					
					
				}
			}
			System.out.println(otherCtr);
			System.out.println("overpriced for " + costctr);
		}
		else{
			result = "not-change";
		}

		return result;
				
				
			
					
	}
	
	*/
	public double calcuateDailyCost(double energy, double[] energyPrices, int[] schedule){
		double dailyCost = 0;
		
		for (int i = 0; i < 24; i++){
			double cost = (schedule[i]*energy*energyPrices[i]);
			dailyCost += cost;
		}
		
		return dailyCost;
	}
	
	
	
	
	public  int [] BestAllocate (double[] energyPrices, int hours, int index, double bestCost, int[] originalSchedule ){
		int [] bestAllocate = new int[24];
		bestAllocate = originalSchedule;
    	int [] bestResponse = new int[24];
		double [] bestPeriod = new double[24];
		double cost = 0;
		if (index < (24-hours)){
			if (hours == 1) {
				//Simplex
				double small = energyPrices[0];
				for (int i = 0; i < energyPrices.length-1; i++){
					if (energyPrices[i] < small){
						small = energyPrices[i];
						index = i;		
					}
				}
				Arrays.fill(bestAllocate, 0);
				bestAllocate[index] = 1;		
			}else if (hours > 1){
				//Allocate 
				for (int i = index; i < 24 - hours; i++ ){
					cost = 0;
					Arrays.fill(bestPeriod, 0);
					Arrays.fill(bestResponse, 0);
					for (int j = 0; j < hours ; j++){
						bestPeriod[j+i] =  energyPrices[i + j];
						bestResponse[i+j] = 1;
					}
					for (double k:bestPeriod ){
						cost += k;
					}
					if (cost < bestCost  ){
						bestCost = cost;
						for (int k = 0; k < 23; k++ ){
							if (bestResponse[k] > 0){
								bestAllocate[k] = 1;
							}
							else{
								bestAllocate[k] = 0;
							}
						}
						
					}
				}		
		//	return BestAllocate(energyPrices, hours, index+1, bestCost);
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
			else{
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
	
	
	
	public void calculateHourlyMoneyConsumption(List<Appliance>allAppliances, double[]hourlyPrices){
		double dailyCost = 0;
		for(int i =0; i < allAppliances.size(); i++){
			dailyCost = calcuateDailyCost(allAppliances.get(i).getKwh(), hourlyPrices, allAppliances.get(i).getConsumptionSchedule());
			allAppliances.get(i).setDailyCost(dailyCost);
			System.out.println(dailyCost);
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
	public double calculateHouseLoad(List<Appliance> allAppliances){
		 	double total = 0;
		 	double sub = 0;
		 	for (Appliance a: allAppliances){
				if (a.isOn()){
	 				double kwh = a.getKwh();
					int[] schedule = a.getConsumptionSchedule();
	 				for (int i =0; i < 24; i++){
	 					sub = kwh * schedule[i];
	 					total+=sub;
	 				}	
	 			}	 			
		 	}
		 		
		 	return total;
		 }
	
	public double getTypeOfApplianceConsumption(List<Appliance> allAppliances, String type){
		double cost = 0;
		List<Appliance> appliances = new ArrayList<Appliance>();
		if (type == "shifteable"){
			appliances = getShifteableDevices(allAppliances);
		}
		else if(type == "nonShifteable"){
			appliances = getNonShifteableDevices(allAppliances);
		}
		for(Appliance a: appliances){
			cost += a.getDailyCost();
		}
		
		return cost;
	}
	///////
	public double[] calculateHourlyLoad(House casa){
		List<Appliance> appliances = new ArrayList<Appliance>();
		appliances = casa.getAppliances();
		double[]consumption = new double[27];
		for (Appliance a: appliances){
			if (a.isOn()){
				int[]schedule = a.getConsumptionSchedule();
				double kwh = a.getKwh();		
				for (int i = 0; i < 24; i++){
					consumption[i]+=schedule[i]*kwh;
					
				}
			}		
		}
		return consumption;
	}
	
	public void calculateApplianceConsumption(List<Appliance> appliances){

		for (Appliance a:appliances){
			double load  = 0;
			int[] schedule = a.getConsumptionSchedule();
			double kwh = a.getKwh();
			for (int i = 0; i < 24; i++){
				load+= (schedule[i] * kwh);
			}
			a.setDailyEnergy(load);
		//	System.out.println("Load for " + a.getName() + " is " + a.getDailyEnergy());
		}
	}
	
	public double calculateHoraLoad(List<Appliance> appliances){
		//TODO
		double load = 0;
		for (Appliance a: appliances){
			if (a.isOn()){
				load+=a.getDailyEnergy();
			}
		
		}
		
		return load;
	}
	
	public double[] calculateHourlyConsumption(List<Appliance> appliances){
		double[]consumption = new double[24];
		for (Appliance a: appliances){
			if (a.isOn()){
				int[]schedule = a.getConsumptionSchedule();
				double kwh = a.getKwh();		
				for (int i = 0; i < 24; i++){
					consumption[i]+=schedule[i]*kwh;
					
				}
			}
			
			
		}
		return consumption;
	}
	
	public double calculateHouseLoad(double[] houseConsumption){
		double load = 0;
		for (double d:houseConsumption){
			load+=d;
		}
		
		return load;
		
	}
	
	public double estimateNewCost(List<Appliance> appliances, double[] hourlyPrices){
		double cost = 0;
		for (Appliance a:appliances){
			int[] currentSchedule = a.getConsumptionSchedule();
			double kwh = a.getKwh();
			for(int i = 0; i < 24; i++){
				cost+= (currentSchedule[i] * kwh * hourlyPrices[i] );
			}
		}
		return cost;
	}
	
	public void calculateCostOfAllAppliances(House casa, double[]costPerHour){
		/*
		 * Solo para todos
		 */
		List<Appliance> appliances = new ArrayList<Appliance>();
		appliances = casa.getAppliances();
		for (Appliance a:appliances){
			double overalCost = 0;
			double khw = a.getKwh();
			int[] schedule = a.getConsumptionSchedule();
			for (int i = 0; i < 24; i++){
				  double result = schedule[i]*costPerHour[i]*khw;
				  overalCost+=result;
			}
			a.setDailyCost(overalCost);	
		}			
	}
	
	public void calculateCostOfOnAppliances(House casa, double[]costPerHour){
		/*
		 * Solo para encendidos
		 */
		List<Appliance> appliances = new ArrayList<Appliance>();
		appliances = casa.getAppliances();
		for (Appliance a:appliances){
			if (a.isOn()){
				double overalCost = 0;
				double khw = a.getKwh();
				int[] schedule = a.getConsumptionSchedule();
				for (int i = 0; i < 24; i++){
					  double result = schedule[i]*costPerHour[i]*khw;
					  overalCost+=result;
				}
				a.setDailyCost(overalCost);
			}
			else{
				a.setDailyCost(0);
			}
		}
			
	}
	
	public double calculateOverallCost(House casa){
		List<Appliance> appliances = new ArrayList<Appliance>();
		appliances = casa.getAppliances();
		double overalCost = 0;
		for (Appliance a:appliances){
			if (a.isOn()){
				overalCost+=a.getDailyCost();
			}
		//	System.out.println(overalCost);
		}	
		return overalCost;
	}
	
	public double getCostOfAppByCategory(List<Appliance> appliances, String type){
		List<Appliance> catAppliances = new ArrayList<Appliance>();
		double cost = 0;
		if (type == "shifteable"){
			catAppliances = getShifteableDevices(appliances);
		}
		else if (type == "nonShifteable"){
			catAppliances = getNonShifteableDevices(appliances);
		}
		for (Appliance a:catAppliances){
			cost += a.getDailyCost();
		}	
		return cost;
	}

	
	public double getLoadOfAppByCategory(List<Appliance> appliances, String type){
		List<Appliance> catAppliances = new ArrayList<Appliance>();
		double cost = 0;
		if (type == "shifteable"){
			catAppliances = getShifteableDevices(appliances);
		}
		else if (type == "nonShifteable"){
			catAppliances = getNonShifteableDevices(appliances);
		}
		
		for (Appliance a:catAppliances){
			double load = 0;
			int [] schedule = a.getConsumptionSchedule();
			double kwh = a.getKwh();
			for (int i = 0; i< 24; i++){
				load+= (schedule[i]*kwh);
			}
			cost+=load;
			
		}	
		return cost;
	}
	
	public int countOffAppliances(List<Appliance> appliances){
		int ctr = 0 ;
		for (Appliance a:appliances){
			if (a.isOn()== false){
				ctr++;
			}
		}
		return ctr;
	}
	
	public void turnOffAllShifteables(List<Appliance> appliances){
		for (Appliance a: appliances){
			if (a.isShifteable()){
				a.setOn(false);
			}
		}
	}
	
	public double[] projectHourlyPrices(double[] foreignLoad, double[] localLoad, double alpha){
		double[] totalLoad = new double[24];
		double [] newLoad = new double[24];
		Arrays.fill(newLoad, 0);
		Arrays.fill(totalLoad, 0);
		for (int i = 0; i< 24; i++){
			totalLoad[i] = foreignLoad[i] + localLoad[i];
		}
		for (int i = 0; i< 24; i++){
			newLoad[i] = totalLoad[i] * alpha * Math.log10(totalLoad[i] + 1);
			
		}
		
		return newLoad;
		
	}
	
	public void turnOffAllShiftAppliances(List<Appliance> allApp){
		for (Appliance a:allApp){
			if (a.isShifteable()){
				a.setOn(false);
			}
		}
		
	}
	
	public double ComfortIndex (List<Appliance> allApp){
		int i = 0;
		
		for (Appliance a:allApp){
			if (a.isOn()){
				i++;
			}
		}
		return i/allApp.size();
	}
	
	
	
	
}
