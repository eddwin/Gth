package com.agents.house.research;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class TheCreator extends Agent {
	
	protected void setup() {
		AgentController a1 = null;
		System.out.println(getLocalName()+" STARTED");
		
		try{
			//Get container
			ContainerController container = (ContainerController) getContainerController();
			for ( int i = 0; i <= 5; i++){
				a1 = container.createNewAgent("house"+i,"com.agents.house.research.HouseAgent", null);
				a1.start();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
