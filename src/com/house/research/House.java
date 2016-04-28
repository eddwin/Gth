package com.house.research;

import java.util.*;

public class House  {
	//TODO: variables
	
	List<Appliance> appliances;
	List<Appliance> randomAppliances;
	

	double consumo;
	
	//Getters and Setters
	
	public List<Appliance> getAppliances() {
		
		return appliances;
	}
	
	public double getConsumo() {
		return consumo;
	}


	public void setConsumo(double consumo) {
		this.consumo = consumo;
	}
	//////
	
	
	
	
	public House (){
		
		this.appliances = loadAllAppliances();
			
	}
	
	public int[] alwaysOn(){

		int[] encendidoArray = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		return encendidoArray;
		
	}
	
	public int[] eightHoursOn(){
		int[] encendidoArray = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0};
		return encendidoArray;
	}
	
	public int[] sixHoursOn(){
		int[] encendidoArray = {0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0};
		return encendidoArray;
	}
	
	public double[] setConsumptionArray(double kwh, int[] array){
		
		double consumtpionArray[] = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			consumtpionArray[i] = (array[i] * kwh);
		}
		return consumtpionArray;
		
	}
	
	public int randomWeight(){
		Random random = new Random();
		return random.nextInt(1);
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
	
	private int[] shuffleArray(int[] array)
	{
	    int index, temp;
	    Random random = new Random();
	    for (int i = array.length - 1; i > 0; i--)
	    {
	        index = random.nextInt(i + 1);
	        temp = array[index];
	        array[index] = array[i];
	        array[i] = temp;
	    }
		return array;
	}
	
	
	public List<Appliance> loadShiftAppliances() {
		List<Appliance> shiftAppliances = new ArrayList<Appliance>();

		//////////////////////////////////////////////
		Appliance aparato = new Appliance();
		aparato.setName("Lights_40");
		aparato.setKwh(0.04);
		aparato.setConsumptionSchedule(sixHoursOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		aparato.setWeight(randomWeight());
		shiftAppliances.add(aparato);
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Lights_100");
		aparato.setKwh(0.10);
		aparato.setConsumptionSchedule(eightHoursOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		aparato.setWeight(randomWeight());
		shiftAppliances.add(aparato);
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Fridge");
		aparato.setKwh(1.2);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		aparato.setWeight(randomWeight());
		shiftAppliances.add(aparato);
		//////////////////////////////////////////////
	
	
		return shiftAppliances;
	}
	
	public List<Appliance> loadNonInterruptiveAppliances(){
		
		List<Appliance> shiftAppliances = new ArrayList<Appliance>();


		//////////////////////////////////////////////
		Appliance aparato = new Appliance();
		aparato.setName("washing_machine");
		aparato.setKwh(0.512);
		aparato.setConsumptionSchedule(shuffleArray(doNotInteruptMe(2)));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		aparato.setWeight(randomWeight());
		shiftAppliances.add(aparato);
		//////////////////////////////////////////////

		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("dryer");
		aparato.setKwh(2.5);
		aparato.setConsumptionSchedule(shuffleArray(doNotInteruptMe(2)));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		aparato.setWeight(randomWeight());
		shiftAppliances.add(aparato);
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Coffee Maker");
		aparato.setKwh(1.2);
		aparato.setConsumptionSchedule(doNotInteruptMe(1));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		aparato.setWeight(randomWeight());
		shiftAppliances.add(aparato);
		
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Dishwasher");
		aparato.setKwh(1.3);
		aparato.setConsumptionSchedule(doNotInteruptMe(2));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		aparato.setWeight(randomWeight());
		shiftAppliances.add(aparato);
		
		
		return shiftAppliances;
		
		
	}
	
	
	
	public List<Appliance> loadInterruptiveAppliances() {
		List<Appliance> interruptiveAppliances = new ArrayList<Appliance>();
		
		//////////////////////////////////////////////
		Appliance aparato = new Appliance();
		aparato.setName("air_conditioner");
		aparato.setKwh(0.512);
		aparato.setConsumptionSchedule(shuffleArray(doNotInteruptMe(9)));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		interruptiveAppliances.add(aparato);
		//////////////////////////////////////////////
		 aparato = new Appliance();
		aparato.setName("EV");
		aparato.setKwh(2.9);
		aparato.setConsumptionSchedule(shuffleArray(doNotInteruptMe(5)));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		interruptiveAppliances.add(aparato);
		//////////////////////////////////////////////

				
		
		return interruptiveAppliances;
		
	}
	
	public List<Appliance> loadAllAppliances() {
		List<Appliance> appliances = new ArrayList<Appliance>();
		
		//All ShiftAppliances
		List<Appliance> shiftAppliances = loadShiftAppliances();
		
		for (Appliance item:shiftAppliances){
			appliances.add(item);
		}
		
		//All NonInterruptive Appliances
		List<Appliance> nonInterruptive = loadNonInterruptiveAppliances();
		
		for (Appliance item:nonInterruptive){
			appliances.add(item);
		}
		
		//All NonShiftAppliances Appliances
		List<Appliance> nonShiftAppliances = loadInterruptiveAppliances();

		for (Appliance item:nonShiftAppliances){
			appliances.add(item);
		}
		
		return appliances;
		
	}
	
	
	

}
