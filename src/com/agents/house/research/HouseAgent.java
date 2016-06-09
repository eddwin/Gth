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
	private double initialVirginCost;
	private double initialAlgorithmCost;
	private double finalCost;
	private int shifteable;
	private int nonShifteable;
	double initialLoad;
	double finalLoad;
	double lastCost;
	private double budget;
	private double startSentInit;
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
				initialVirginCost = 0;
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
				}else if (msg.getPerformative()==ACLMessage.AGREE){
					//TODO final messages
					showFinalTally(msg);
				}
			}
			else{
				block();
			}
		}
		
		
	}
	
public void showFinalTally(ACLMessage msg){
	String content = msg.getContent();
	Gson gson = new Gson();
	Function fu = new Function();
	double [] hourlyPrices = gson.fromJson(content, double[].class);
	fu.calculateCostOfAllAppliances(casa, hourlyPrices);
	double[] consumptionSchedule = fu.calculateHourlyLoad(casa);
	finalLoad = fu.calculateHouseLoad(consumptionSchedule);
	finalCost = fu.calculateOverallCost(casa);
	double savings =  initialAlgorithmCost - finalCost;
	int appsOff = fu.countOffAppliances(casa.getAppliances());
	double index = fu.ComfortIndex(casa.getAppliances());
	msg.createReply();
	msg.setPerformative(ACLMessage.CONFIRM);
	msg.setConversationId("index");
	msg.setReplyWith("Request" + System.currentTimeMillis());
	String json = gson.toJson(index, double.class);
	msg.setContent(json);
	send(msg);
	System.out.println("For: " + getAID().getLocalName());
	System.out.println("Initial Load was: " + initialLoad + " Final Load: " + finalLoad );
	System.out.println("Cost without preferences: " + initialVirginCost + " Final Cost: " + finalCost );
	System.out.println("Initial  Cost: " + initialVirginCost + " Final Cost: " + finalCost );
	System.out.println("Savings were: " +  savings);
	System.out.println("Apps off were: " + appsOff);
	//TODO: happiness index

}

public void sendInitialSchedule(ACLMessage msg){
	String content = msg.getContent();
	
	ACLMessage reply = msg.createReply();
	if (content != null){
		//Calculate consumption for this house
		Function fu = new Function();
		
		double[]consumptionSchedule = fu.calculateHourlyLoad(casa);
		 initialLoad = fu.calculateHouseLoad(consumptionSchedule);
		 double shiftLoad = fu.getLoadOfAppByCategory(casa.getAppliances(), "shifteable");
		 double nonShiftLoad = fu.getLoadOfAppByCategory(casa.getAppliances(), "nonShifteable");
		 System.out.println("Initial shiftLoad: " +shiftLoad );
		 System.out.println("Initial nonShiftLoad " + nonShiftLoad);
		 fu.calculateApplianceConsumption(casa.getAppliances());
		 double checkInitLoad = fu.calculateHoraLoad(casa.getAppliances());
		System.out.println(getAID().getLocalName() + " reported " + initialLoad + " checker " + checkInitLoad);
		//Prepare message
		reply.setPerformative(ACLMessage.INFORM);
		reply.setConversationId(DAILY);
		Gson gson = new Gson();
		startSentInit = System.currentTimeMillis();
		consumptionSchedule[24] = startSentInit;
		String json = gson.toJson(consumptionSchedule); //JSONfy
		reply.setContent(json);
		
		send(reply);
	}
}
public void optimizeSchedule(ACLMessage msg){
	//get content
	String content = msg.getContent();
	double receiptTime = System.currentTimeMillis();
	ACLMessage reply  = msg.createReply();
	if (content != null){
		//get hourly prices
		 Gson gson = new Gson();
		 double [] foreignLoad = gson.fromJson(content, double[].class);
		 Function fu = new Function();
		 double alpha = foreignLoad[24];
		 double[]localLoad = fu.calculateHourlyLoad(casa);
		 //Reconstruct
		 double [] hourlyPrices = fu.projectHourlyPrices(foreignLoad, localLoad, alpha);
		 
		 String result;
		 if (lastPriceArray == null){			 //first pass 
			 lastPriceArray = hourlyPrices;
			 //Calculate each appliance cost
			 fu.calculateCostOfAllAppliances(casa, hourlyPrices);
			 //Game on
			 //Get budgets
			 double shiftBudget = fu.getCostOfAppByCategory(casa.getAppliances(), "shifteable");
			 double nonShiftBudget = fu.getCostOfAppByCategory(casa.getAppliances(), "nonShifteable");
			 double availableBudget = casa.getBudget() -nonShiftBudget;
			 initialVirginCost = fu.calculateOverallCost(casa);	
			 double shiftLoad = fu.getLoadOfAppByCategory(casa.getAppliances(), "shifteable");
			 double nonShiftLoad = fu.getLoadOfAppByCategory(casa.getAppliances(), "nonShifteable");
			 System.out.println("Middle shiftLoad: " +shiftLoad );
			 System.out.println("Middle nonShiftLoad " + nonShiftLoad);
			
			 System.out.println("For " + getAID().getLocalName() + " Budget is " + casa.getBudget() + " shift: " + shiftBudget + " nonshift " + nonShiftBudget );
			 System.out.println("For " + getAID().getLocalName() + " Available budget is " +availableBudget);
			 if (availableBudget <= 0){ 
				 
				 //NO BUDGET
				 
				 
				 localLoad[25] = receiptTime;
				 String json = gson.toJson(localLoad);
				 myLogger.log(Logger.INFO, "Over budget for non shift");
				 reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				 System.out.println("sent acceptance");
				 fu.turnOffAllShiftAppliances(casa.getAppliances());
				 finalCost = fu.calculateOverallCost(casa);
				 System.out.println("For " + getAID().getName() + " Initial cost was: " + initialVirginCost + " Final cost is: " + finalCost );
				 send(reply);		
			 }
			 else{
				 //it can be afforded
				//Turn off shifteables for first pass
				 
				 
				 
				fu.turnOffAllShifteables(casa.getAppliances());
				 
				fu.IWantConvenience(casa.getAppliances(), foreignLoad, localLoad, availableBudget, alpha);
				int offApp = fu.countOffAppliances(casa.getAppliances());
				//Recalculate energy needs	
				//Get consumption schedule for the next hour for this house
				localLoad = fu.calculateHourlyLoad(casa);
				reply.setPerformative(ACLMessage.INFORM);
				gson = new Gson();
				lastCost = fu.calculateOverallCost(casa);
				initialAlgorithmCost = lastCost;
				localLoad[25] = receiptTime;
				String json = gson.toJson(localLoad); //JSONfy
				reply.setContent(json);
				System.out.println("Load of house is: " + fu.calculateHouseLoad(localLoad) );
				System.out.println(getAID().getLocalName() + " turned off " + offApp + " appliances in this round");
				reply.setConversationId("prices");
				send(reply); 	
			 } //end of checking game for the first round
			 
		 }
		 else{ //N pass
			 hourlyPrices = fu.projectHourlyPrices(foreignLoad, localLoad, alpha);
			 //check if prices are the same
			 boolean flag = false;
			 for (int i=0; i< 24; i++){
				 if (lastPriceArray[i] != hourlyPrices[i]){
					 flag = true;
				 }
			 }
			 
			 if (flag){//price has changed
				 //Rpoject changes
				double newCost= fu.estimateNewCost(casa.getAppliances(), hourlyPrices);
				 System.out.println("For " + getAID().getLocalName() + " New cost is: " + newCost + " last cost is " + lastCost );

				 if (newCost > lastCost){ //optimize
					 
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
					     localLoad = fu.calculateHourlyLoad(casa);
						 gson = new Gson();
						 localLoad[25] = receiptTime;

						 String json = gson.toJson(localLoad); //JSONfy
						 reply.setContent(json);
						 myLogger.log(Logger.INFO, "Over budget for non shift");
						 reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						 System.out.println("sent acceptance");
						 finalCost = fu.calculateOverallCost(casa);
						 System.out.println("For " + getAID().getName() + " Initial cost was: " + initialVirginCost + " Final cost is: " + finalCost );
						 send(reply);
						 lastPriceArray = hourlyPrices;
					 }else{
						//it can be afforded
						
							 fu.IWantConvenience(casa.getAppliances(), foreignLoad, localLoad, availableBudget, alpha);
							 
								 localLoad = fu.calculateHourlyLoad(casa);
								 localLoad[26] = fu.ComfortIndex(casa.getAppliances());
								 gson = new Gson();
								 localLoad[25] = receiptTime;
								localLoad[26] = fu.ComfortIndex(casa.getAppliances());

								 String json = gson.toJson(localLoad); //JSONfy
								 reply.setContent(json);
								 reply.setPerformative(ACLMessage.INFORM);
								 lastPriceArray = hourlyPrices;
								 double thisCost = fu.calculateOverallCost(casa);
								 System.out.println("For " + getAID().getName() + " Last cost was: " + lastCost + " New cost is : " + thisCost );
								 lastCost = thisCost;
								 send(reply); 
							 
							
					 } //end of checking budget
				 }
				 else{
					 localLoad = fu.calculateHourlyLoad(casa);
					 localLoad[25] = receiptTime;
					 localLoad[26] = fu.ComfortIndex(casa.getAppliances());
					 String json = gson.toJson(localLoad); //JSONfy
					 reply.setContent(json);
					 reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					 System.out.println("sent acceptance");
					 lastPriceArray = hourlyPrices;
					 send(reply);
				 }
				
				 
				 
				
				 
			 }
			 else{ //price didn't change
				  reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				  localLoad = fu.calculateHourlyLoad(casa);
					 System.out.println("sent acceptance");

				  gson = new Gson();
				  localLoad[25] = receiptTime;
				  localLoad[26] = fu.ComfortIndex(casa.getAppliances());

				  String json = gson.toJson(localLoad); //JSONfy
				  reply.setContent(json);
				  int on = 0;
				  int off = 0;
			//	  double[]consumptionSchedule = fu.calculateHourlyConsumption(casa); 
				 finalLoad = fu.calculateHouseLoad(localLoad);
				 System.out.println("For " + getAID().getName() + "Initial budget was : " + casa.getBudget() + " Initial consumption was: " + initialVirginCost + " Final consumption was: " + finalCost + " apps off : " + off + "  apps on : " + on + " initial load was: " + initialLoad + " Final house load is: " + finalLoad);
				 send(reply);

				 	 
			 }//end of checking if price has changed	
		 } //end of checking which round is it


	}
}
	
	
}
