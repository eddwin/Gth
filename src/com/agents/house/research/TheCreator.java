package com.agents.house.research;

import java.util.Random;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class TheCreator extends Agent {
	
	private Object[] mArgs;

	protected void setup() {
		AgentController a1 = null;
		System.out.println(getLocalName()+" STARTED");
		mArgs = new Object[4];
		mArgs[0] = "convenience";
		double[] randomBudget = {110, 140, 180, 210,300,190,340,145,183,200,210,300,160,180,190,390,450,269};
		try{
			//Get container
			ContainerController container = (ContainerController) getContainerController();
			for ( int i = 0; i < 13; i++){
				//randomBudget = random.nextInt(310);
				mArgs[1] = randomBudget[i];
				mArgs[2] = randomNumOfShifteable();
				mArgs[3] = randomNumOfNonShifteable();
				a1 = container.createNewAgent("house"+i,"com.agents.house.research.HouseAgent", mArgs);
				a1.start();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public int randomNumOfShifteable (){
		int min = 1;
		int max = 22;

		Random r = new Random();
		int i1 = r.nextInt(max - min + 1) + min;
		return i1;
	}
	
	public int randomNumOfNonShifteable (){
		int min = 1;
		int max = 6;

		Random r = new Random();
		int i1 = r.nextInt(max - min + 1) + min;
		return i1;
	}
}
