package com.house.research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ApplianceFactory {
	
	public ApplianceFactory(){
		
	}

	
	public  List<Appliance> createnonShifteableAppliance(int quantity){
		List<Appliance> nonShift = new ArrayList<Appliance>();
		//MAX is 6
		List<Appliance>allNonShifteable = nonShifteableAppliances();
		Collections.shuffle(allNonShifteable);
		for (int i = 0; i < quantity; i++){
			nonShift.add(allNonShifteable.get(i));
		}
		
		return nonShift;
	}
	
	public  List<Appliance> createShifteableAppliance(int quantity){
		List<Appliance> shift = new ArrayList<Appliance>();
		List<Appliance>allShifteable = ShifteableAppliances();

		//MAX is 22
		//TODO: DONT FORGET TO ADD THE WEIGHTS!!!!
		
		int[] weight = new int[quantity];
		
		Collections.shuffle(shift);
		for (int i = 0; i < quantity; i++){
			shift.add(allShifteable.get(i));
		}
		
		for (int i =0; i < shift.size(); i++ ){
			shift.get(i).setWeight(weight[i]);
		}
		
		
		return shift;
	}
	
	
	public  List<Appliance> nonShifteableAppliances(){
		List<Appliance> nonShift = new ArrayList<Appliance>();
		
		
		//////////////////////////////////////////////
		Appliance aparato = new Appliance();
		aparato.setName("Lights_40");
		aparato.setKwh(0.04);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShift.add(aparato);
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Lights_100");
		aparato.setKwh(0.10);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShift.add(aparato);
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Fridge");
		aparato.setKwh(0.15);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShift.add(aparato);
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("air_conditioner");
		aparato.setKwh(1);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShift.add(aparato);
		//////////////////////
		aparato = new Appliance();
		aparato.setName("Charger");
		aparato.setKwh(0.3);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShift.add(aparato);
		//////////////////////
		aparato = new Appliance();
		aparato.setName("Security Camera");
		aparato.setKwh(0.15);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShift.add(aparato);
		
		//////////////////////
		aparato = new Appliance();
		aparato.setName("Fish Tank");
		aparato.setKwh(0.004);
		aparato.setOn(true);
		aparato.setShifteable(false);
		aparato.setConsumptionSchedule(alwaysOn());
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		nonShift.add(aparato);
		
		
		return nonShift;
	}
	
	
	
public List<Appliance> ShifteableAppliances(){
		
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
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("CD Player");
		aparato.setKwh(0.085);
		aparato.setOperationalHours(2);
		sTime = random.nextInt(21);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);

		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Computers");
		aparato.setKwh(0.24);
		aparato.setOperationalHours(4);
		sTime = random.nextInt(19);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Floor polisher");
		aparato.setKwh(0.305);
		aparato.setOperationalHours(3);
		sTime = random.nextInt(20);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);

		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Hand Iron");
		aparato.setKwh(1.1);
		aparato.setOperationalHours(1);
		sTime = random.nextInt(22);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);

		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Humidifier");
		aparato.setKwh(0.177);
		aparato.setOperationalHours(6);
		sTime = random.nextInt(17);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
								
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Jacuzzi");
		aparato.setKwh(1.3);
		aparato.setOperationalHours(2);
		sTime = random.nextInt(21);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Jacuzzi");
		aparato.setKwh(5.76);
		aparato.setOperationalHours(2);
		sTime = random.nextInt(21);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Kiln");
		aparato.setKwh(5.76);
		aparato.setOperationalHours(2);
		sTime = random.nextInt(21);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("TV");
		aparato.setKwh(0.286);
		aparato.setOperationalHours(2);
		sTime = random.nextInt(21);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);

		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Xbox");
		aparato.setKwh(0.045);
		aparato.setOperationalHours(3);
		sTime = random.nextInt(20);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Vacuum Cleaner");
		aparato.setKwh(0.63);
		aparato.setOperationalHours(1);
		sTime = random.nextInt(23);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Garbage Disposal");
		aparato.setKwh(0.445);
		aparato.setOperationalHours(2);
		sTime = random.nextInt(21);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Hot Plate");
		aparato.setKwh(1.2);
		aparato.setOperationalHours(3);
		sTime = random.nextInt(20);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
		
		//////////////////////////////////////////////
		aparato = new Appliance();
		aparato.setName("Toaster");
		aparato.setKwh(1.146);
		aparato.setOperationalHours(1);
		sTime = random.nextInt(23);
		aparato.setOn(true);
		aparato.setShifteable(true);
		aparato.setConsumptionSchedule(setOperationalHours(sTime, sTime + aparato.getOperationalHours()));
		aparato.setEnergyToConsume(setConsumptionArray(aparato.getKwh(), aparato.getConsumptionSchedule()));
		shiftAppliances.add(aparato);
				
		
		
		return shiftAppliances;
		
		
		
		
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
	
	
	
	
	public int[] alwaysOn(){

		int[] encendidoArray = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		return encendidoArray;
		
	}
	
	public double[] setConsumptionArray(double kwh, int[] consumptionSchedule){
		
		double consumtpionArray[] = new double[consumptionSchedule.length];
		for (int i = 0; i < consumptionSchedule.length; i++) {
			consumtpionArray[i] = (consumptionSchedule[i] * kwh);
		}
		return consumtpionArray;
		
	}
}
