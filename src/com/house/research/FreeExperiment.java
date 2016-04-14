package com.house.research;

import java.util.*;

public class FreeExperiment {

	public FreeExperiment(){
		
		
	}
	//Create a neighborhood
	
	public List<MainHouse> create_neighborhood(){
		

		List<MainHouse> casas = new ArrayList<MainHouse>();
		for (int i = 0; i < 5; i++){
			MainHouse casa = new MainHouse(); // Casa con random appliances
			casas.add(casa);
		}
		
		return casas;
		
	}

	public void show_appliances(List<MainHouse> casas){
		
		
		for (int i = 0; i < casas.size(); i++){
			MainHouse casa = casas.get(i);
			System.out.println("House consumes " + casa.kwh + " watts");
		}
		
		
	}
	
	public double neighborhood_consumption(List<MainHouse> houses){
		
		double globalKwH = 0;
		
		for (int i = 0; i< houses.size(); i++){
			MainHouse casa = houses.get(i);
			double kwh = casa.kwh;
			globalKwH = globalKwH + kwh; 
		}
		
		
		return globalKwH;
	}
	
	
	
	public static void main (String[] args){
		
		
		FreeExperiment vecindario = new FreeExperiment();
		List<MainHouse> listaCasas = vecindario.create_neighborhood();
		System.out.println("Neighborhood global consumption is " + vecindario.neighborhood_consumption(listaCasas) + " KWH");		
				
				
				
				//double consumo = casa.hour_consumption(RandomA);
		//System.out.println("Consumption for house is " + consumo + " KhW in the next hour");

		
	}
}
