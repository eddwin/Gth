package com.agents.house.research;
import jade.util.Logger;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;

import com.google.gson.Gson;
import com.house.research.Appliance;
import com.house.research.Function;
import com.house.research.House;

public class HouseAgent extends Agent {
	private String strategy;
	private AID[] fogAgent;
	private House casa;
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	private double[] lastPriceArray;
	private double initialCost;
	private double finalCost;

	protected void setup() {
		
		//create a house and its appliances
		casa = new House();
		initialCost = 0;
		finalCost = 0;
		
		//Get Arguments
		Object[] args = getArguments();
		if (args != null && args.length > 0 ){
			strategy = (String) args[0];
			casa.setBudget((Double) args[1]);
		}
		
		//Register agent in Yellow Pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("house-consumption");
		sd.setName("energy-game");
		dfd.addServices(sd);
		try{
			DFService.register(this, dfd);
		}
		catch (FIPAException fe ){
			fe.printStackTrace();
		}
		
		
		
		//Greeting
		System.out.println("Hello! House-agent "+getAID().getName() +  " is ready.");
		
		//Look for Fog Agent and send daily schedule
		addBehaviour(new dailyConsumptionSchedule());
		addBehaviour(new optimizeHouseSchedule());
		
		
		//Add the behavior serving requests from power company
		
		
	}

	
	
	
	private class dailyConsumptionSchedule extends CyclicBehaviour {
		
		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchConversationId("daily-consumption"));
			ACLMessage msg = myAgent.receive(mt);
			
			
			if (msg != null){
				
				//Message received, process it.
				String content = msg.getContent();
				ACLMessage reply  = msg.createReply();

			   if ((content!= null) && (content.indexOf("initialSchedule") != -1)){
				   

				   //Get consumption  for this house
					Function fu = new Function();
					double[] dailyConsumption = fu.getHourlyHouseConsumption(casa);
					
					reply.setPerformative(ACLMessage.INFORM);
					Gson gson = new Gson();
					String json = gson.toJson(dailyConsumption); //JSONfy
					reply.setContent(json);
				} 
				else {
					//MEssage is incorrect
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					reply.setContent("Content not understood");
				}
				
				myAgent.send(reply);
			}
			
			else {
				block();
			}
			
		}
		
		
	}
	
	private class optimizeHouseSchedule extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM), MessageTemplate.MatchConversationId("prices"));
			ACLMessage msg = myAgent.receive(mt);
			
			if (msg != null){
				//Message received, process it.
				String content = msg.getContent();
				ACLMessage reply  = msg.createReply();
			
			 if (content!= null) {
				 
				 Gson gson = new Gson();
				 double [] hourlyPrices = gson.fromJson(content, double[].class);
				 if (lastPriceArray == null){ //First Pass
					 lastPriceArray = hourlyPrices;	 
					// Based on hourlyPrices, see what appliances it can be turned off given preference
						Function fu = new Function();

						//First, calculate prices!
						fu.calculateHourlyMoneyConsumption(casa.getAppliances(), hourlyPrices);
						
					 	double nonShiftBudget = fu.CalculateNonShiftBudget(casa.getAppliances());
						double shiftBudget = casa.getBudget() - nonShiftBudget;
						double shiftConsumption = fu.getShiftConsumption(casa.getAppliances());
						 
						initialCost = fu.getTotalConsumption(casa.getAppliances());
						
						
						myLogger.log(Logger.INFO, "For " + getAID().getName() + " Budget : " + casa.getBudget()  );
						myLogger.log(Logger.INFO, "For " + getAID().getName() + " shift : " + shiftBudget  );
						myLogger.log(Logger.INFO, "For " + getAID().getName() + " nonShift : " + nonShiftBudget  );
						myLogger.log(Logger.INFO, "For " + getAID().getName() + " nonShift Cons : " + shiftConsumption  );


						if (shiftBudget <= 0){
							myLogger.log(Logger.INFO, "Over budget for non shift");
							 reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							 finalCost = fu.getTotalConsumption(casa.getAppliances());
							 System.out.println("For " + getAID().getName() + "Initial consumption was: " + initialCost + " Final consumption was: " + finalCost );
							 myAgent.send(reply);
							 block();
							
						}
						else{
							fu.IWantConvenience(casa.getAppliances(), hourlyPrices, shiftBudget);
							//Recalculate energy needs	
							   //Get consumption for the next hour for this house
								
								double[] dailyConsumption = fu.getHourlyHouseConsumption(casa);
								reply.setPerformative(ACLMessage.INFORM);
								gson = new Gson();
								String json = gson.toJson(dailyConsumption); //JSONfy
								reply.setContent(json);
								reply.setConversationId("prices");
						}
						
						
						 
					 
				 } else{ //N pass
						Function fu = new Function();

					//check if prices are the same
					 boolean flag = false;
					 for (int i = 0; i < 24; i++){
						 if (hourlyPrices[i] != lastPriceArray[i]){ //If they are, flag it
							 flag = true;
							 
						 }
					 } 
					 if (flag){ //If a price has changed, optimize again
						// Based on hourlyPrices, see what appliances it can be turned off given preference
						 
						 	for (Appliance app: casa.getAppliances()){
						 		if (app.isOn() == false){
						 			System.out.println(app.getName() + " of " + getAID().getName() + " is off");
						 		}
						 	}
						 	double nonShiftBudget = fu.CalculateNonShiftBudget(casa.getAppliances());
							double shiftBudget = casa.getBudget() - nonShiftBudget;
							
							if (shiftBudget <= 0){
								myLogger.log(Logger.INFO, "Over budget for non shift");
							}
							else{
								fu.IWantConvenience(casa.getAppliances(), hourlyPrices, shiftBudget);
							}
							
						//Recalculate energy needs	
						   //Get consumption for the next hour for this house
							
							double[] dailyConsumption = fu.getHourlyHouseConsumption(casa);
							
							reply.setPerformative(ACLMessage.INFORM);
							gson = new Gson();
							String json = gson.toJson(dailyConsumption); //JSONfy
							reply.setContent(json);
							reply.setConversationId("prices");
							//Update local
							lastPriceArray = hourlyPrices;
							
					 }else{ //If prices are the same, then inform of no change
						 reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						  fu = new Function();
						  int counter = 0;
							 for (Appliance app: casa.getAppliances()){
								 if (app.isOn() == false){
									 counter++;
								 }
							 }	 
						 finalCost = fu.getTotalConsumption(casa.getAppliances());
						 System.out.println("For " + getAID().getName() + "Initial consumption was: " + initialCost + " Final consumption was: " + finalCost + " and app of is: " + counter + " and apps on are: " + (8 - counter));
						 
						 
					 }
					 
					 
				 }
				 			 
				} 
				else {
					//MEssage is incorrect
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					reply.setContent("Content not understood");
				}
				
				myAgent.send(reply);
			}
			
			else {
				block();
			}
			
		}
		
	}
	private class dailyConsumptionSchedule2 extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchConversationId("daily-consumption"));
			ACLMessage msg = myAgent.receive(mt);
			
			if (msg != null){
				
				//Message received, process it.
				String content = msg.getContent();
				ACLMessage reply  = msg.createReply();

			   if ((content!= null) && (content.indexOf("dailySchedule") != -1)){
				   
				
				   //Get consumption for the next hour for this house
					Function fu = new Function();
					double dailyConsumption = fu.dailyHouseConsumption(casa);
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(String.valueOf(dailyConsumption));
				} 
				else {
					//MEssage is incorrect
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					reply.setContent("Content not understood");
				}
				
				myAgent.send(reply);
			}
			
			else {
				block();
			}
			
		}
		
		
		
	}
	
	
}
