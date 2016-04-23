package com.house.research;

import java.util.*;

public class Billing {
	
	public Billing() {
		
		
		
	}
	
	//
	double mHourlyBill;

	public double getHourlyBill() {
		return mHourlyBill;
	}

	public void setHourlyBill(double hourlyBill) {
		mHourlyBill = hourlyBill;
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
	
}
