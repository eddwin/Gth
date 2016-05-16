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
		int[] shifTeable = {5, 9, 7, 13, 6};
		int[] nonShiftTeable = {2,3,6, 4, 5};
		double[] randomBudget = {140, 130, 180, 170,120,160,340,145,183,200};
		try{
			//Get container
			ContainerController container = (ContainerController) getContainerController();
			for ( int i = 0; i < 5; i++){
				Random random = new Random();
				//randomBudget = random.nextInt(310);
				mArgs[1] = 180.0;
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
