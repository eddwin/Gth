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
	private int shifteable;
	private int nonShifteable;
	double initialLoad;
	double finalLoad;
	private double budget;
	 public final static String DAILY = "daily-consumption";
	 public final static String PRICE = "prices";
	protected void setup() {
		
		
		
		//Get Arguments
		Object[] args = getArguments();
		if (args != null && args.length > 0 ){
			strategy = (String) args[0];
			budget = (Double) args[1];
			shifteable = (Integer) args[2];
			nonShifteable = (Integer) args[3];
		}
		//create a house and its appliances
				casa = new House(shifteable, nonShifteable);
				initialCost = 0;
				finalCost = 0;
				casa.setBudget(budget);
				
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
		addBehaviour(new HearForMessages());
		//addBehaviour(new dailyConsumptionSchedule());
		//addBehaviour(new optimizeHouseSchedule());
		
		
		//Add the behavior serving requests from power company
		
		
	}

	private class HearForMessages extends CyclicBehaviour {

		@Override
		public void action() {
			// 
			ACLMessage msg = receive();
			if (msg != null){
				if (msg.getPerformative() == ACLMessage.REQUEST){
					if (DAILY.equals(msg.getConversationId())) {
						sendInitialSchedule(msg);
					}
					
				}else if (msg.getPerformative()==ACLMessage.INFORM){
					 if(PRICE.equals(msg.getConversationId())){
						//OPTIMIZE
						optimizeSchedule(msg);
					}
				}
			}
			else{
				block();
			}
		}
		
		
	}

public void sendInitialSchedule(ACLMessage msg){
	String content = msg.getContent();
	ACLMessage reply = msg.createReply();
	if (content != null){
		//Calculate consumption for this house
		Function fu = new Function();
		double[]consumptionSchedule = fu.calculateHourlyConsumption(casa);
		 initialLoad = fu.calculateHouseLoad(consumptionSchedule);
		System.out.println(getAID().getLocalName() + " reported " + initialLoad);
		//Prepare message
		reply.setPerformative(ACLMessage.INFORM);
		reply.setConversationId(DAILY);
		Gson gson = new Gson();
		String json = gson.toJson(consumptionSchedule); //JSONfy
		reply.setContent(json);
		send(reply);
	}
}

public void optimizeSchedule(ACLMessage msg){
	//get content
	String content = msg.getContent();
	ACLMessage reply  = msg.createReply();
	if (content != null){
		//get hourly prices
		 Gson gson = new Gson();
		 double [] hourlyPrices = gson.fromJson(content, double[].class);
		 Function fu = new Function();
		 
		 if (lastPriceArray == null){			 //first pass 
			 lastPriceArray = hourlyPrices;
			 //Calculate each appliance cost
			 fu.calculateCostOfAllAppliances(casa, hourlyPrices);
			 //Game on
			 //Get budgets
			 double shiftBudget = fu.getCostOfAppByCategory(casa.getAppliances(), "shifteable");
			 double nonShiftBudget = fu.getCostOfAppByCategory(casa.getAppliances(), "nonShifteable");
			 double availableBudget = casa.getBudget() -nonShiftBudget;
			 System.out.println("shift " + shiftBudget + " nonShift" + nonShiftBudget);
			 initialCost = fu.calculateOverallCost(casa);		 

			 System.out.println("For " + getAID().getLocalName() + " Budget is " + casa.getBudget() + " shift: " + shiftBudget + " nonshift " + nonShiftBudget );
			 if (availableBudget <= 0){
					 myLogger.log(Logger.INFO, "Over budget for non shift");
					 reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					 finalCost = fu.calculateOverallCost(casa);
					 System.out.println("For " + getAID().getName() + " Initial cost was: " + initialCost + " Final cost is: " + finalCost );
					 send(reply);		
			 }
			 else{
				 	//it can be afforded
					int offApp = fu.countOffAppliances(casa.getAppliances());
				 	fu.IWantConvenience(casa.getAppliances(), hourlyPrices, availableBudget, offApp);
				 	//Recalculate energy needs	
				 	//Get consumption schedule for the next hour for this house
				 	double[]consumptionSchedule = fu.calculateHourlyConsumption(casa);
					reply.setPerformative(ACLMessage.INFORM);
					gson = new Gson();
					String json = gson.toJson(consumptionSchedule); //JSONfy
					reply.setContent(json);
					System.out.println("Load of house is: " + fu.calculateHouseLoad(consumptionSchedule) );
					System.out.println(getAID().getLocalName() + " turned off " + offApp + " appliances in this round");
					reply.setConversationId("prices");
					send(reply);
			 } //end of checking game for the first round
			 
		 }
		 else{ //N pass
			 
			 //check if prices are the same
			 boolean flag = false;
			 for (int i=0; i< 24; i++){
				 if (lastPriceArray[i] != hourlyPrices[i]){
					 flag = true;
				 }
			 }
			 
			 if (flag){//price has changed
				//Recalculate appliance cost based on new prices
				 fu.calculateCostOfAllAppliances(casa, hourlyPrices);
				 
				 //calculate budgets
				 double shiftBudget = fu.getCostOfAppByCategory(casa.getAppliances(), "shifteable");
				 double nonShiftBudget = fu.getCostOfAppByCategory(casa.getAppliances(), "nonShifteable");
				 double availableBudget = casa.getBudget() - nonShiftBudget;
				 System.out.println("For " + getAID().getLocalName() + " Budget is " + casa.getBudget() + " shift: " + shiftBudget + " nonshift " + nonShiftBudget );
				 if (availableBudget <= 0){
					 //TODO: send original schedule
					 //TODO: cut
					 double[]consumptionSchedule = fu.calculateHourlyConsumption(casa);
					 gson = new Gson();
					 String json = gson.toJson(consumptionSchedule); //JSONfy
					 reply.setContent(json);
					 myLogger.log(Logger.INFO, "Over budget for non shift");
					 reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					 finalCost = fu.calculateOverallCost(casa);
					 System.out.println("For " + getAID().getName() + " Initial cost was: " + initialCost + " Final cost is: " + finalCost );
					 send(reply);
				 }else{
					//it can be afforded
						int offApp = fu.countOffAppliances(casa.getAppliances());
					 String result = fu.IWantConvenience(casa.getAppliances(), hourlyPrices, availableBudget, offApp);
					 if (result == "no-change"){
						 double[]consumptionSchedule = fu.calculateHourlyConsumption(casa);
						 gson = new Gson();
						 String json = gson.toJson(consumptionSchedule); //JSONfy
						 reply.setContent(json);
						 reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						 finalCost = fu.calculateOverallCost(casa);
						 finalLoad = fu.calculateHouseLoad(consumptionSchedule);
						 System.out.println("For " + getAID().getName() + " Initial cost was: " + initialCost + " Final cost is: " + finalCost  + "initial load was: " + initialLoad + "final load is" + finalLoad);
						 send(reply); 
					 }
					 else if(result == "change"){
						//Recalculate energy needs	
						 	//Get consumption schedule for the next hour for this house
						 	double[]consumptionSchedule = fu.calculateHourlyConsumption(casa);
							reply.setPerformative(ACLMessage.INFORM);
							gson = new Gson();
							String json = gson.toJson(consumptionSchedule); //JSONfy
							reply.setContent(json);
							System.out.println("Load of house is: " + fu.calculateHouseLoad(consumptionSchedule) );
							System.out.println(getAID().getLocalName() + " turned off " + offApp + " appliances in this round");
							reply.setConversationId("prices");
							send(reply);
					 }	 
				 } //end of checking budget
			 } 
			 else{
				  reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				  int on = 0;
				  int off = 0;
				  double[]consumptionSchedule = fu.calculateHourlyConsumption(casa); 
				 finalCost = fu.calculateOverallCost(casa); //$$
				 finalLoad = fu.calculateHouseLoad(consumptionSchedule);
				 System.out.println("For " + getAID().getName() + "Initial budget was : " + casa.getBudget() + " Initial consumption was: " + initialCost + " Final consumption was: " + finalCost + " apps off : " + off + "  apps on : " + on + " initial load was: " + initialLoad + " Final house load is: " + finalLoad);
				 

				 	 
			 }//end of checking if price has changed	
		 } //end of checking which round is it
		 
	}
}
	private class dailyConsumptionSchedule extends CyclicBehaviour {
		
		@Override
		public void action() {
			// 
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchConversationId("daily-consumption"));
			ACLMessage msg = myAgent.receive(mt);
			
			
			if (msg != null){
				
				//Message received, process it.
				String content = msg.getContent();
				ACLMessage reply  = msg.createReply();

			   if ((content!= null) && (content.indexOf("initialSchedule") != -1)){
				   

				   //Get consumption  for this house
					Function fu = new Function();
					double[] dailyConsumption = fu.getHourlyConsumption(casa.getAppliances());
					initialLoad = fu.calculateHouseLoad(casa.getAppliances());
					reply.setPerformative(ACLMessage.INFORM);
					reply.setConversationId("daily-consumption");
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
			// 
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
						
					 	double nonShiftBudget = fu.getTypeOfApplianceConsumption(casa.getAppliances(), "shifteable");
						double shiftBudget = casa.getBudget() - nonShiftBudget;
						double shiftConsumption = fu.getTypeOfApplianceConsumption(casa.getAppliances(), "nonShifteable");
						 
						initialCost = nonShiftBudget + shiftConsumption;
						
						System.out.println("For " + getAID().getLocalName() + " Budget is " + casa.getBudget() + " shift: " + shiftBudget + " nonshift " + nonShiftBudget );
						myLogger.log(Logger.INFO, " For " + getAID().getName() + " nonShift Cons : " + shiftConsumption  );


						if (shiftBudget <= 0){
							myLogger.log(Logger.INFO, "Over budget for non shift");
							 reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							 finalCost = fu.getTotalConsumption(casa.getAppliances());
							 System.out.println("For " + getAID().getName() + " Initial cost was: " + initialCost + " Final cost is: " + finalCost );
							 myAgent.send(reply);
							 block();
							
						}
						else{
				//			fu.IWantConvenience(casa.getAppliances(), hourlyPrices, shiftBudget);
							//Recalculate energy needs	
							   //Get consumption for the next hour for this house
								
								double[] dailyConsumption = fu.getHourlyConsumption(casa.getAppliances());
								reply.setPerformative(ACLMessage.INFORM);
								gson = new Gson();
								String json = gson.toJson(dailyConsumption); //JSONfy
								reply.setContent(json);
								reply.setConversationId("prices");
								myAgent.send(reply);
								 block();
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
						 	double nonShiftBudget = fu.getTypeOfApplianceConsumption(casa.getAppliances(), "shifteable");
							double shiftBudget = casa.getBudget() - nonShiftBudget;
							
							if (shiftBudget <= 0){
								myLogger.log(Logger.INFO, "Over budget for non shift");
								double[] dailyConsumption = fu.getHourlyConsumption(casa.getAppliances());
								reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
								gson = new Gson();
								String json = gson.toJson(dailyConsumption); //JSONfy
								reply.setContent(json);
								reply.setConversationId("prices");
								//Update local
								lastPriceArray = hourlyPrices;
								send(reply);
							}
							else{
						//		fu.IWantConvenience(casa.getAppliances(), hourlyPrices, shiftBudget);
								//Recalculate energy needs	
								//Get consumption for the next hour for this house
									
									double[] dailyConsumption = fu.getHourlyConsumption(casa.getAppliances());

									
									reply.setPerformative(ACLMessage.INFORM);
									gson = new Gson();
									String json = gson.toJson(dailyConsumption); //JSONfy
									reply.setContent(json);
									reply.setConversationId("prices");
									//Update local
									lastPriceArray = hourlyPrices;
							}
							
						
							
					 }else{ //If prices are the same, then inform of no change
						 reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						  fu = new Function();
						  int on = 0;
						  int off = 0;
							 
						 finalCost = fu.getTotalConsumption(casa.getAppliances()); //$$
						 finalLoad = fu.calculateHouseLoad(casa.getAppliances());
						 System.out.println("For " + getAID().getName() + "Initial budget was : " + casa.getBudget() + " Initial consumption was: " + initialCost + " Final consumption was: " + finalCost + " apps off : " + off + "  apps on : " + on + " initial load was: " + initialLoad + " Final house load is: " + finalLoad);
						 
						 
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
	
	
}
