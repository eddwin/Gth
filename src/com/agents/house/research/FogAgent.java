package com.agents.house.research;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.google.gson.Gson;
import com.house.research.PowerVariable;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
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
	private PowerVariable pv;
	double[] clearArray = new double[24];
	
	
	
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	protected void setup() {
		System.out.print("Hey there! Fog-Agent " + getAID().getName() + " is ready");
		//TODO: Servicio para buscar agentes de casas
		//TODO: Comportamiento para pedir schedules 
		
		//Initialize variables
		pv = new PowerVariable();
		Arrays.fill(clearArray, 0);
		pv.setHourlyLoadArray(clearArray);
		
		
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
		addBehaviour(new WakerBehaviour (this, 20000){
			protected void handleElapsedTimeout(){
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("house-consumption");
				template.addServices(sd);
				
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					houseAgents = new AID[result.length];
					for (int i = 0; i < houseAgents.length; i++){
						houseAgents[i] = result[i].getName();
					}			
				} 
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
				//Perform the request
				
				myAgent.addBehaviour(new Optimize());
			}
		});
		
	}
	
	
	private class Optimize extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int step = 1;
		private MessageTemplate mt;
		private MessageTemplate mt2;
		private int repliesCnt = 0;
		private double costLoad = 0;
		
			@Override
			public void action() {
				
				switch(step){
				case 1: 
					//Step 1: Gather initial schedules from all houses
					ACLMessage request = new ACLMessage(ACLMessage.REQUEST);//RQST Message
					for (int i = 0; i < houseAgents.length; i++){ //To all houses
						request.addReceiver(houseAgents[i]);
					}
					request.setContent("initialSchedule"); //Asking for initial schedule
					request.setConversationId("daily-consumption");
					request.setReplyWith("Request" + System.currentTimeMillis());
					myAgent.send(request); //Send messsage
					//Prepare template to get schedules
					mt = MessageTemplate.and(MessageTemplate.MatchConversationId("daily-consumption"), MessageTemplate.MatchInReplyTo(request.getReplyWith()));
					step = 2;
					break;
					//End of step 1
				case 2:
					//Step 2: receive all  schedules from house agents
					ACLMessage reply = myAgent.receive(mt); //Receive schedule
					if (reply != null){			//If message has content (yay!)
						if (reply.getPerformative() == ACLMessage.INFORM){
							String json = reply.getContent();
							Gson gson = new Gson();
							double[] cargaHora = pv.getHourlyLoadArray();
							double [] casaLoad = gson.fromJson(json, double[].class);
							for (int i = 0; i < 24; i++){
								cargaHora[i] = cargaHora[i] + casaLoad[i];
							}
							
							pv.setHourlyLoadArray(cargaHora);
							
						}		
							repliesCnt++; //To check if everyone is here
							if (repliesCnt >= houseAgents.length){ //If all agents have submitted their schedules
								step = 3; //End of step 2
								pv.setPriceArray(calculateHourlyPrices(pv.getHourlyLoadArray()));

							}
							
					}
					else {
						block(); //Wait 
						
					}
					break;
					
				case 3:
					// Step 4: send prices to house agents
					ACLMessage response = new ACLMessage(ACLMessage.INFORM);//RQST Message
					for (int i = 0; i < houseAgents.length; i++){ //To all houses
						response.addReceiver(houseAgents[i]);
					}
					
					Gson gson = new Gson();
					String json = gson.toJson(pv.getPriceArray());
					response.setContent(json);
					response.setConversationId("prices");
					response.setReplyWith("Request" + System.currentTimeMillis());
					myAgent.send(response);
					//Prepare template to get schedules
					mt2 = MessageTemplate.and(MessageTemplate.MatchConversationId("prices"), MessageTemplate.MatchInReplyTo(response.getReplyWith()));
					//Prepare array
					pv.setHourlyLoadArray(clearArray);
					step = 4;
					break;
					//End of step 3
				case 4: 
						
					int positive = 0;
					int negative = 0;
					//Step 4: receive all  updated schedules from house agents
					ACLMessage replyGame = myAgent.receive(mt2); //Receive schedule
					if (replyGame != null){			//If message has content (yay!)
						if (replyGame.getPerformative() == ACLMessage.INFORM){ //Someone has changed their schedule
							positive++;
							json = replyGame.getContent();
							gson = new Gson();
							double[] cargaHora = pv.getHourlyLoadArray();
							double [] casaLoad = gson.fromJson(json, double[].class);
							for (int i = 0; i < 24; i++){
								cargaHora[i] = cargaHora[i] + casaLoad[i];
							}	
							pv.setHourlyLoadArray(cargaHora);
						}	else if (replyGame.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
							negative++;
						}
							repliesCnt++; //To check if everyone is here
							if (repliesCnt >= houseAgents.length){ //If all agents have submitted their schedules
								if (positive > 0 ) { //Someone changed their schedule
									System.out.println("One more round");		
									step = 3; //End of step 4, go back to previous step
									pv.setPriceArray(calculateHourlyPrices(pv.getHourlyLoadArray()));
								}else {
								System.out.println("End of game");
								step = 5;
								}

							}
							
					}
					else {
						block(); //Wait 
						
					}
					break;
				}
				
				
				
				
			}

			@Override
			public boolean done() {
				// TODO Auto-generated method stub
				return (step == 5);
			}
					
				}
	
	
	
	
	public double[] calculateHourlyPrices( double[] hourlyConsumption) {
		
		double[] hourlyPrices = new double[24];
		//alpha*Lh*Log10(lh+1)
		double alpha = 0.2;
		for (int i = 0; i< 24; i++){
			hourlyPrices[i] = hourlyConsumption[i] * alpha * Math.log10(hourlyConsumption[i] + 1);
		}
		return hourlyPrices;
		
		
	}
	
	
}
