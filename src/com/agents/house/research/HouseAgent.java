package com.agents.house.research;
import jade.util.Logger;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.house.research.Function;
import com.house.research.House;


public class HouseAgent extends Agent {
	
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
		
		//Add the behavior serving requests from power company
		addBehaviour(new ConsumptionSchedule());
		
		
	}

	
	//Behaviour to server requests regarding scheduled consumption for the next interval of time
	
	private class ConsumptionSchedule extends CyclicBehaviour {

		@Override
		public void action() {
			//Template matching the correct type of message (RQST)
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
		
			if (msg != null){
				
				//Message received, process it.
				String content = msg.getContent();
				ACLMessage reply  = msg.createReply();

			   if ((content!= null) && (content.indexOf("consumption") != -1)){
				   
				   myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Received Request from "+msg.getSender().getLocalName());
				
				   //Get consumption for the next hour for this house
					Function fu = new Function();
					double hourlyConsumption = fu.houseConsumption(casa);
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(String.valueOf(hourlyConsumption));
					myLogger.log(Logger.INFO, "Agent " + getLocalName() + " reported " + hourlyConsumption + " khw for the next hour");
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
			// TODO Auto-generated method stub
			
		}
		
	}
}
