package com.house.research;

import java.util.Arrays;

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
	
}
