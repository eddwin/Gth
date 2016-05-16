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
		int[] shifTeable = {5,9,7,13,6,14,21,18,19,5,9,13,15};
		int[] nonShiftTeable = {2,3,6,4,5,6,4,5,2,4,5,2,4,5,3,4};
		double[] randomBudget = {110, 140, 180, 210,300,190,340,145,183,200,210,300,160,180,190,390,450,269};
		try{
			//Get container
			ContainerController container = (ContainerController) getContainerController();
			for ( int i = 0; i < 13; i++){
				//randomBudget = random.nextInt(310);
				mArgs[1] = 40.0;
				mArgs[2] = shifTeable[i];
				mArgs[3] = nonShiftTeable[i];
				a1 = container.createNewAgent("house"+i,"com.agents.house.research.HouseAgent", mArgs);
				a1.start();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
