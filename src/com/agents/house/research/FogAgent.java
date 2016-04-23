package com.agents.house.research;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class FogAgent extends Agent {

	private AID[] houseAgents;
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	protected void setup() {
		System.out.print("Hey there! Fog-Agent " + getAID().getName() + " is ready");
		//TODO: Servicio para buscar agentes de casas
		//TODO: Comportamiento para pedir schedules 
		
		
		// Add a TickerBehaviour that schedules a request to seller agents every minute
		addBehaviour(new TickerBehaviour(this, 60000) {
			protected void onTick() {	
				Date date = new Date();
				Calendar calendar = GregorianCalendar.getInstance();
				calendar.setTime(date);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				System.out.println("Requesting schedules for " + hour + " O'Clock");
				//Update list of house Agents from DF Agent
				
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("house-consumption");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					System.out.println("Found the following agents:" );
					houseAgents = new AID[result.length];
					for (int i = 0; i < houseAgents.length; i++){
						houseAgents[i] = result[i].getName();
						System.out.println(houseAgents[i].getName());
					}			
				} 
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
				
				//Perform the request
				myAgent.addBehaviour(new RequestSchedules());
			}
		} );
		
		
		
	}
	
	private class RequestSchedules extends Behaviour {

		private int step = 0;
		private MessageTemplate mt;
		private double globalConsumption = 0;
		private int repliesCnt = 0;

		
		@Override
		public void action() {
			switch (step) {
			case 0:
				//Send request to All agents
				ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
				for (int i = 0; i < houseAgents.length; i++){
					request.addReceiver(houseAgents[i]);
				}
				request.setContent("consumption");
				request.setConversationId("initial-consumption");
				request.setReplyWith("Request" + System.currentTimeMillis());
				myAgent.send(request);
				
				//Prepare template to get schedules
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("initial-consumption"), MessageTemplate.MatchInReplyTo(request.getReplyWith()));
				step = 1;
				break;
			case 1:
				//Recieve all initial consumption schedules
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null){			
					if (reply.getPerformative() == ACLMessage.INFORM){
						myLogger.log(Logger.INFO, "Received " + reply.getContent());
						double kwh = Double.parseDouble(reply.getContent());
						globalConsumption = globalConsumption + kwh;		
					}
					repliesCnt++;
					if (repliesCnt >= houseAgents.length){
						//All schedules received 
						step = 2;
					}	
				}
				else {
					block();
				}
				break;	
			case 2:
				myLogger.log(Logger.INFO, "Global consumption is " + globalConsumption + " KwH" );
				step = 3;
				break;
			}
			case 3:  // Calculate price of energy based on current load
				break;
			
			
		}

		@Override
		public boolean done() {
			
			return (step == 3);
		}
		
	}
}
