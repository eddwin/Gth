package com.house.research;

public class PowerVariable {
	
	

	double[] mHourlyPriceArray;
	

	int change;

	public int getChanges() {
		return change;
	}

	public void setChanges(int responses) {
		this.change = responses;
	}
	double [] mHourlyLoadArray;
	public double[] getHourlyLoadArray() {
		return mHourlyLoadArray;
	}

	public void setHourlyLoadArray(double[] hourlyLoadArray) {
		mHourlyLoadArray = hourlyLoadArray;
	}
	public PowerVariable(){
		
	}
	
	 public double[] getPriceArray() {
			return mHourlyPriceArray;
		}

		public void setPriceArray(double[] priceArray) {
			mHourlyPriceArray = priceArray;
		}
	
	
	
}
