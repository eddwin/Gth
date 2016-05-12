package com.house.research;

import java.util.List;

public class Optimization {

	
	public int[] newConsumptionSchedule(int[] currentSchedule, int[] prices) {
		
		//TODO: get price of each hour
		//TODO: find lowest period
		//TODO: return new schedule
		int minIndex = 0;
		int hours = 4;
		outerloop:
		for (int i = 0; i < currentSchedule.length; i++ ){
			for (int j = 0; j < prices.length; i++){
				if (currentSchedule[i] < prices[j]) { //if price is lower at this hour
					minIndex = i;		
				}
			}
		}
		
		return null;
		
	}
}
