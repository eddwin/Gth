package com.house.research;

import java.util.List;

public class Initialization {
	
	public static void main (String[] args){
		
		
		//Load Houses
		FreeExperiment vecindario = new FreeExperiment();
		List<House> listaCasas = vecindario.create_neighborhood();
		
		
		System.out.println("Neighborhood global consumption is " + vecindario.neighborhood_consumption(listaCasas) + " KWH for the next hour");		
				
	
	}

}
