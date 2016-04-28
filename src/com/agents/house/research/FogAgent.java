package com.agents.house.research;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class FogAgent extends Agent {

	private AID[] houseAgents;
	private double dailyConsumption;
	private double hourlyConsumption = 0;

	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	protected void setup() {
		System.out.print("Hey there! Fog-Agent " + getAID().getName() + " is ready");
		//TODO: Servicio para buscar agentes de casas
		//TODO: Comportamiento para pedir schedules 
		
		//Register Fog Agent in YEllow Pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("fog-agent");
		sd.setName("fog1");
		dfd.addServices(sd);
		try{
			DFService.register(this, dfd);
		}
		catch (FIPAException fe ){
			fe.printStackTrace();
		}
		
		//Listen for initial schedules 
		addBehaviour (new TickerBehaviour(this, 10000){

			@Override
			protected void onTick() {
				myAgent.addBehaviour(new requestDailySchedule());
				
			}
			
		});
		addBehaviour (new requestDailySchedule());
		
		
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
		private int repliesCnt = 0;
		private double costLoad = 0;

		
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
						double kwh = Double.parseDouble(reply.getContent());
						hourlyConsumption = hourlyConsumption + kwh;		
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
			case 2: // Calculate price of energy based on current load
				//Ch(Lh) = tetLhLog(lh+1)
				
				/*double p = 2.02;
				double Lh2 = Math.pow(globalConsumption, 2);
				myLogger.log(Logger.INFO, "Lh2 is " + Lh2);
				double tet = (p*globalConsumption)/((Lh2*Math.log10(globalConsumption+1)));
				myLogger.log(Logger.INFO, "tet " + tet);
				costLoad = ((tet*globalConsumption)*Math.log10(globalConsumption+1));
				*/
				
				//Ch(Lh) = ahLh2
				//ah = Hourly wholesale price
				costLoad = 2.02*(Math.pow(hourlyConsumption, 2));
				step = 3;
				break;
				
			case 3: 
				//Billing function
				
				
				
				myLogger.log(Logger.INFO, "Ch(Lh) is " + costLoad + "NTD" );
				myLogger.log(Logger.INFO, "Global consumption is " + hourlyConsumption + " KwH" );
				step = 4;
				break;
				
			}
			
			
		}

		@Override
		public boolean done() {
			
			return (step == 4);
		}
		
	}
	
	private class requestDailySchedule extends OneShotBehaviour {
		private int repliesCnt = 0;
		private MessageTemplate mt;
		private double dailyConsumption = 0;
		
		@Override
		public void action() {
			
			
				//Send request to All agents
				ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
				for (int i = 0; i < houseAgents.length; i++){
					request.addReceiver(houseAgents[i]);
				}
				request.setContent("dailySchedule");
				request.setConversationId("daily-consumption");
				request.setReplyWith("Request" + System.currentTimeMillis());
				myAgent.send(request);
				
				//Prepare template to get schedules
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("daily-consumption"), MessageTemplate.MatchInReplyTo(request.getReplyWith()));
				//Recieve all daily consumption schedules
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null){			
					if (reply.getPerformative() == ACLMessage.INFORM){
						double kwh = Double.parseDouble(reply.getContent());
						dailyConsumption = dailyConsumption + kwh;		
					}
					repliesCnt++;
					if (repliesCnt >= houseAgents.length){
						myLogger.log(Logger.INFO, "SUM Lh is " + dailyConsumption + " kwh" );
						done();
					}	
				}
				else {
					block();
				}
			
			
		}
		
	}
	
	
	private class getDailySchedules extends CyclicBehaviour {
		private ACLMessage mMsg;
		private int step = 0;
		@Override
		public void action() {
			mMsg = receive(MessageTemplate.and(MessageTemplate.MatchConversationId("daily-schedule"), MessageTemplate.MatchPerformative(ACLMessage.INFORM)));
            if (mMsg != null){
            	String content = mMsg.getContent();	
            	if (content != null){
            		//Get Consumption Schedule and add it to the global total
        			double kwh = Double.parseDouble(mMsg.getContent());
        			dailyConsumption = dailyConsumption + kwh;
        			
        			
        			
            	}else{
                	ACLMessage reply  = mMsg.createReply();
                	reply.setPerformative(ACLMessage.FAILURE);
    				reply.setContent("Content not understood");
    				
            	}
            }else{
            	block();
            }
            
		}
	}
	
	private class gameOne extends Behaviour {
		private int step = 0;
		@Override
		public void action() {
			switch (step){
			case 0:
				//Calculate Alpha
				//a = P(SumLh)/SumLh^2(log(Lh+1))
				double p = 2.02; //lowest NTD per kWH in Taiwan
				double dcp2 = Math.pow(dailyConsumption, 2);
				double alpha = ((p*dailyConsumption)/(dcp2*(Math.log10(hourlyConsumption+1))));
				step =1;
				myLogger.log(Logger.INFO, "Ch(Lh) is " + alpha + "NTD" );
			break;
			}
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return (step == 1);
		}
		
	}
}
