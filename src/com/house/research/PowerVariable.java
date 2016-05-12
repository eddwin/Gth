package com.house.research;

public class PowerVariable {
	
	

	double[] mHourlyPriceArray;
	

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
