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

import com.google.gson.Gson;
import com.house.research.Function;
import com.house.research.House;

public class HouseAgent extends Agent {
	private AID[] fogAgent;
	private House casa;
	private Logger myLogger = Logger.getMyLogger(getClass().getName());

	protected void setup() {
		
		//create a house and its appliances
		casa = new House();
		
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
					myLogger.log(Logger.INFO, "Agent " + getLocalName() + " reported " + json);
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
				 // Based on hourlyPrices, see what appliances it can be turned off given preference
					Function fu = new Function();

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
					
					double dailyConsumption = fu.dailyHouseConsumption(casa);
					reply.setPerformative(ACLMessage.INFORM);
					gson = new Gson();
					String json = gson.toJson(dailyConsumption); //JSONfy
					reply.setContent(json);
					reply.setContent(String.valueOf(dailyConsumption));
					reply.setConversationId("prices");
					myLogger.log(Logger.INFO, "Agent " + getLocalName() + " reported " + json);
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
					myLogger.log(Logger.INFO, "Agent " + getLocalName() + " reported " + dailyConsumption + " khw for the next day");
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
