package com.house.research;

public class PowerVariable {
	
	
	public double[] getLoads() {
		return loads;
	}

	public void setLoads(double[] loads) {
		this.loads = loads;
	}
	double [] loads;
	double[] mHourlyPriceArray;
	public double[] getForeignLoad() {
		return foreignLoad;
	}

	public void setForeignLoad(double[] foreignLoad) {
		this.foreignLoad = foreignLoad;
	}
	double [] foreignLoad;

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
