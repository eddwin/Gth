package com.house.research;

import java.util.*;

public class House {
	//TODO: variables
	
	List<Appliance> appliances;
	public void setAppliances(List<Appliance> appliances) {
		this.appliances = appliances;
	}



	
	
	
	double houseConsumption;
	double budget;
	String strategy;

	//Getters and Setters
	
	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
	
	public double getBudget() {
		return budget;
	}

	public void setBudget(double budget) {
		this.budget = budget;
	}
	
	public List<Appliance> getAppliances() {
		
		return appliances;
	}
	
	public double getHouseConsumption() {
		return houseConsumption;
	}


	public void setHouseConsumption(double consumo) {
		this.houseConsumption = consumo;
	}
	//////
	
	
	
	
	public House (){
		
		setAppliances(loadAllAppliances());
			
	}
	
	public int[] consumptionSchedule(boolean alwaysOn, int hoursOn, int kwh, int pos){
		int[] consumptionSchedule = new int[24];

		if (alwaysOn == true){ //Non-Shifteable
			Arrays.fill(consumptionSchedule, kwh);
		}else {
			Arrays.fill(consumptionSchedule, 0);
			for (int i = pos; i <= pos + hoursOn; i++){
				consumptionSchedule[i] = kwh;
			}
		}
		return consumptionSchedule;
	}
	
	public int[] alwaysOn(){

		int[] encendidoArray = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		return encendidoArray;
		
	}
	
	public double[] setConsumptionArray(double kwh, int[] array){
		
		double consumtpionArray[] = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			consumtpionArray[i] = (array[i] * kwh);
		}
		return consumtpionArray;
		
	}
	
	public int[] doNotInteruptMe(int hours){
		
		int[] nonInterruptiveArray = new int[24];
		
		for (int i = 0; i < hours;i++){
			nonInterruptiveArray[i] = 1;
		}
		return nonInterruptiveArray;
		
	}
	
	public int[] youCanInterruptMe(int hours){
		
		int[] interruptiveArray = new int[24];
		
		for (int i = 0; i < hours;i++){
			interruptiveArray[i] = 1;
		}
		return interruptiveArray;
		
	}
	
	private int[] shuffleArray(int [] array){
	    List<Integer> rnum = new ArrayList<Integer>();
	    int [] newArray = new int[array.length];
	    for (int i = 0; i < array.length ; i++){
	    	rnum.add(array[i]);
	    }
	    Collections.shuffle(rnum);
	    for (int i = 0; i < rnum.size() ; i++){
	    	newArray[i] = rnum.get(i);
	    }
		return newArray;
	}
	
	
	
	private int[] setOperationalHours(int sTime, int eTime){
		int[] operational = new int[24];
		Arrays.fill(operational, 0);
		
		for (int i = sTime; i <= eTime; i++){
			operational[i] = 1;
		}
		
		return operational;
	}
	
	public List<Appliance> loadnonShiftAppliances() {
		List<Appliance> nonShiftAppliances = new ArrayList<Appliance>();

		//////////////////////////////////////////////
		Appliance aparato = new Appliance();
		aparato.setName("Lights_40");
		aparato.setKwh(0.04);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShiftAppliances.add(aparato);
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Lights_100");
		aparato.setKwh(0.10);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShiftAppliances.add(aparato);
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Fridge");
		aparato.setKwh(0.15);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShiftAppliances.add(aparato);
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("air_conditioner");
		aparato.setKwh(1);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShiftAppliances.add(aparato);
		//////////////////////
		aparato = new Appliance();
		aparato.setName("Charger");
		aparato.setKwh(0.3);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShiftAppliances.add(aparato);
		//////////////////////
		aparato = new Appliance();
		aparato.setName("Security Camera");
		aparato.setKwh(0.15);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShiftAppliances.add(aparato);

				return nonShiftAppliances;
	}
	
	
	
	public List<Appliance> loadShifteableAppliances(){
		
		List<Appliance> shiftAppliances = new ArrayList<Appliance>();
		Random random = new Random();
		int [] num = {1,2,3,4,5,6,7,8};
		int [] importance = shuffleArray(num);

		//////////////////////////////////////////////
		Appliance aparato = new Appliance();
		aparato.setName("washing_machine");
		aparato.setKwh(0.5);
		aparato.setShifteable(true);
		aparato.setOperationalHours(2);
		int sTime = random.nextInt(22);
		aparato.setWeight(importance[0]);
		aparato.setOn(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		//////////////////////////////////////////////

		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("dryer");
		aparato.setKwh(1);
		aparato.setOperationalHours(2);
		sTime = random.nextInt(22);
		aparato.setOn(true);
		aparato.setWeight(importance[1]);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Coffee Maker");
		aparato.setKwh(0.8);
		aparato.setOperationalHours(1);
		sTime = random.nextInt(22);
		aparato.setOn(true);
		aparato.setWeight(importance[2]);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Dishwasher");
		aparato.setKwh(0.4);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setWeight(importance[3]);
		aparato.setOperationalHours(2);
		sTime = random.nextInt(21);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("EV");
		aparato.setKwh(0.3);
		aparato.setOperationalHours(8);
		sTime = random.nextInt(15);
		aparato.setOn(true);
		aparato.setWeight(importance[4]);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Microwave");
		aparato.setKwh(0.8);
		aparato.setOperationalHours(2);
		sTime = random.nextInt(21);
		aparato.setOn(true);
		aparato.setWeight(importance[5]);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);

		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("thing");
		aparato.setKwh(1.4);
		aparato.setOperationalHours(2);
		sTime = random.nextInt(21);
		aparato.setOn(true);
		aparato.setWeight(importance[6]);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);

		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Popcorn Popper");
		aparato.setKwh(1.4);
		aparato.setOperationalHours(1);
		sTime = random.nextInt(22);
		aparato.setOn(true);
		aparato.setWeight(importance[7]);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);

				
		
		return shiftAppliances;
		
		
	}
	
	
	
	
	public List<Appliance> loadAllAppliances() {
		List<Appliance> appliances = new ArrayList<Appliance>();
		
		//All ShiftAppliances
		List<Appliance> shiftAppliances = loadnonShiftAppliances();
		
		for (Appliance item:shiftAppliances){
			appliances.add(item);
		}
		
		//All NonInterruptive Appliances
		List<Appliance> nonInterruptive = loadShifteableAppliances();
		
		for (Appliance item:nonInterruptive){
			appliances.add(item);
		}
		
		
		
		return appliances;
		
	}
	
	

}
