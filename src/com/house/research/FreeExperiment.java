package com.house.research;

import java.util.*;

public class FreeExperiment {

	public FreeExperiment(){
		
		
	}
	//Create a neighborhood
	
	public List<House> create_neighborhood(){
		

		List<House> casas = new ArrayList<House>();
		for (int i = 0; i < 100; i++){
			House casa = new House(); // Casa con random appliances
			casas.add(casa);
		}
		
		return casas;
		
	}

	
		
	
	public double neighborhood_consumption(List<House> houses){
		
		double globalKwH = 0;
		double khw;
		Billing bill = new Billing();
		
		for (int i = 0; i< houses.size(); i++){
			House casa = houses.get(i);
			bill.billingFunction(casa);
			khw = bill.getHourlyBill();
			globalKwH = globalKwH + khw; 
		}
		
		
		return globalKwH;
	}
	
	
	
	
	
	
}
