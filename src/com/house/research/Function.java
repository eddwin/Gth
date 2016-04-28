package com.house.research;

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
	
}
