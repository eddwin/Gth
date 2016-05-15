package com.house.research;

import java.util.*;

public class Billing {
	
	public Billing() {
		
		
		
	}
	
	//
	double mHourlyBill;
	

	double mDailyConsumption;
	

	double[] mHourlyConsumption;
	
	public double[] getHourlyConsumption() {
		return mHourlyConsumption;
	}

	public void setHourlyConsumption(double[] hourlyConsumption) {
		mHourlyConsumption = hourlyConsumption;
	}
	
	public double getHourlyBill() {
		return mHourlyBill;
	}

	public void setHourlyBill(double hourlyBill) {
		mHourlyBill = hourlyBill;
	}
	
	public double getDailyConsumption() {
		return mDailyConsumption;
	}

	public void setDailyConsumption(double dailyConsumption) {
		mDailyConsumption = dailyConsumption;
	}
	
	
	//Returns consumption of one house
	
	public void billingFunction (House house){
			
		
		Date date = new Date();
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		//Billing = Sum of usage at next hour = [consumption schedule * consumption] for every device.
		double globalConsumo = 0;
		double consumo;
		List<Appliance>  appliances =  house.getAppliances();
		
		for (int i = 0; i < appliances.size(); i++){
			int status = appliances.get(i).activeOrNot(hour);
			double consumption = appliances.get(i).getConsumption(hour);
			consumo = status * consumption;
			globalConsumo = globalConsumo + consumo;
		}		
		setHourlyBill(globalConsumo);
			
		
	}
	
	public void getDailyConsumption(House house){
		
		//Daily Consumption = Sum of usage at every hour 
		double dailyConsumption = 0;
		double consumo = 0;
		List<Appliance>  appliances =  house.getAppliances();
		
		for (int i = 0; i < appliances.size() ;i++){
			for (int j = 0; j < 24; j++){
				int status = appliances.get(i).activeOrNot(j);
				double consumption = appliances.get(i).getConsumption(j);
				consumo = status * consumption;
				dailyConsumption = dailyConsumption + consumo;
			}
		}
		
		setDailyConsumption(dailyConsumption);
	}
	
	
	
	public double[] getHourlyConsumption (House casa){
		//Returns array of size 24 with each hourly consumption for this House
		double[] hourlyCon = new double[24];
		Arrays.fill(hourlyCon, 0); //Initialize array
		List <Appliance> appliances = casa.getAppliances(); //Get Appliances
		double hourlyConsumo = 0;
		
		for (int i = 0; i < appliances.size(); i++){ //For each appliance in the house
			if (appliances.get(i).isOn()){ //If its turned on 
				for (int j = 0; j < 24; j++){ //each hour 
					int status = appliances.get(i).activeOrNot(j); //Status at hour j
					double consumption = appliances.get(i).getConsumption(j);  //Energy needed
					hourlyConsumo = status * consumption; 
					hourlyCon[j] = hourlyCon[j] + hourlyConsumo; //Store it
				}
				
			}
			
			
		}
		
		
		return hourlyCon;
	}
	
}
