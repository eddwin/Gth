package com.agents.house.research;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.house.research.Appliance;
import com.house.research.Function;
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
	double Lh;
	double alpha;
	double [] cargaHora = new double[24];
	int round;
	int initCounter;
	int responseCounter;
	int numOfAcceptance;
	int numOfChanges;
	Map<String, double[]> preferences = new HashMap<String, double[]>();
	 public final static String DAILY = "daily-consumption";
	 public final static String PRICE = "prices";

	
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	protected void setup() {
		System.out.print("Hey there! Fog-Agent " + getAID().getName() + " is ready");
		//TODO: Servicio para buscar agentes de casas
		//TODO: Comportamiento para pedir schedules 
		
		//Initialize variables
		pv = new PowerVariable();
		Arrays.fill(clearArray, 0);
		
		pv.setHourlyLoadArray(clearArray);
		 initCounter = 0;
		 alpha = 0.02;
		 round = 0;
		 numOfAcceptance = 0;
		 numOfChanges = 0;
		 Lh = 0;
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
				//Step 1: Gather initial schedules from all houses
				ACLMessage request = new ACLMessage(ACLMessage.REQUEST);//RQST Message
				for (int i = 0; i < houseAgents.length; i++){ //To all houses
					request.addReceiver(houseAgents[i]);
				}
				request.setContent("initialSchedule"); //Asking for initial schedule
				request.setConversationId("daily-consumption");
				request.setReplyWith("Request" + System.currentTimeMillis());
				myAgent.send(request); //Send messsage
				myAgent.addBehaviour(new HearMessages());
			}
		});
		
	}
	
	
	private class HearMessages extends CyclicBehaviour {

		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage msg = receive();
			if (msg != null){
				if (msg.getPerformative() == ACLMessage.INFORM){
					
					if (DAILY.equals(msg.getConversationId())) {
						gatherInitSchedules(msg);
					}
					else if (PRICE.equals(msg.getConversationId())){
						//TODO: Gather schedule and calculate new price (Game)
						numOfChanges++;
						gatherNewSchedule(msg);
					}
				}
				else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
					numOfAcceptance++;
					responseCounter++;
					agentChecker();
				}
			}
			else{
				block();
			}
			
		}
		
	}
	
	public void sendStatusOfEndGame(){
		for (int i =0; i <houseAgents.length;i++){
			
		}
	}
	
	public void gatherInitSchedules(ACLMessage msg){
		
		if (initCounter <= houseAgents.length ){ 
			initCounter++;
			String json = msg.getContent();
			Gson gson = new Gson();
			//cargaHora = pv.getHourlyLoadArray();
			double [] casaLoad = gson.fromJson(json, double[].class);
			for (int i = 0; i < 24; i++){
				cargaHora[i] = cargaHora[i] + casaLoad[i];
			}
			
			storeHousePreferences(casaLoad, msg.getSender().getLocalName()); //store House Preference
			
			if (initCounter == houseAgents.length){
				//calculate price
				calculateHourlyPrices(alpha, cargaHora);
				for (double d: cargaHora){
					Lh+=d;
				}
				round++;
				System.out.println("Round: " + round);
				System.out.println("Initial Load is: " + Lh);
				//send price to house agent
				senderToNextAgent();
				
			}
		}
			
	}

	public void senderToNextAgent(){
		System.out.println("Response aqui: " + responseCounter);
		if (responseCounter < houseAgents.length ){	
			sendPrice(houseAgents[responseCounter]);			
		}
		else{
			System.out.println("Check");
			agentChecker();
		}
	}
	public void agentChecker(){
		
		if (numOfAcceptance == houseAgents.length && (numOfChanges == 0)){				
			System.out.println("End of game");		
			System.out.println("number of houses: " + houseAgents.length);
			System.out.println("Number of acceptance: " + numOfAcceptance );
			System.out.println("Number of changes: " + numOfChanges);
			System.out.println("Number of Responses: " + responseCounter);
			System.out.println("Current Load is: " + Lh);
			double avgPrice = averagePrice(cargaHora);
			System.out.println("Average price is: " + avgPrice );
		} else if(numOfChanges > 0){
			if(numOfChanges <= houseAgents.length && responseCounter == houseAgents.length){
				round++;
				System.out.println("number of houses: " + houseAgents.length);
				System.out.println("Number of acceptance: " + numOfAcceptance );
				System.out.println("Number of changes: " + numOfChanges);
				System.out.println("Number of Responses: " + responseCounter);
				System.out.println("Current Load is: " + Lh);
				double avgPrice = averagePrice(cargaHora);
				System.out.println("Average price is: " + avgPrice );
				System.out.println("Round number: " + round) ;
			numOfAcceptance = 0;
			numOfChanges = 0;
			responseCounter = 0;
			senderToNextAgent();
			System.out.println("Next");
			}else{
			//Still playing
				senderToNextAgent();
			}
		}	
	}
		
		

	public void  sendPrice(AID Agent){
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);//RQST Message
		Gson gson = new Gson();
		if(pv.getPriceArray()!= null){
			String json = gson.toJson(pv.getPriceArray());
			message.addReceiver(Agent);
			message.setConversationId(PRICE);
			message.setReplyWith("Request" + System.currentTimeMillis());
			message.setContent(json);
			send(message);
		}
	}
	
	public void storeHousePreferences(double[] houseLoad, String houseName){
		preferences.put(houseName, houseLoad);
	}
	
	public void storeHousesPreferenceAndUpdate(double[] houseLoad, String houseName){
		//check if house has already submitted a preference
			preferences.put(houseName, houseLoad);
			calculateLoad(); //Recalculate Load and price
	}
	
	public void calculateLoad(){
		//double [] cargaHora = new double[24];
		Arrays.fill(cargaHora, 0);
		Lh = 0;
		for (Map.Entry<String, double[]> entry:preferences.entrySet()){
			double[] schedule = entry.getValue();
			for (int i = 0; i < 23; i++){
				cargaHora[i]+=schedule[i];
				
			}
		}
		for (double d: cargaHora){
			Lh+=d;
		}
	//	alpha = calculateAlpha(Lh);
		calculateHourlyPrices(alpha, cargaHora);
		
	}
	
	
	
	public void gatherNewSchedule(ACLMessage msg){
		
		responseCounter++;
		String json = msg.getContent();
		Gson gson = new Gson();
		double [] casaLoad = gson.fromJson(json, double[].class);
		storeHousesPreferenceAndUpdate(casaLoad, msg.getSender().getLocalName());
		agentChecker();
	}
	
	
	
	
	public void calculateHourlyPrices( double alpha, double[] hourlyLoad) {
		double[] hourlyPrices = new double[24];
		//alpha*Lh*Log10(lh+1)
		//double alpha = 0.2;
		for (int i = 0; i< 24; i++){
			hourlyPrices[i] = hourlyLoad[i] * alpha * Math.log10(hourlyLoad[i] + 1);
		}
		
		pv.setPriceArray(hourlyPrices);
		
		
	}
	
	public double averagePrice (double[] hourlyConsumption){
		double averagePrice = 0;
		
		for(double d:hourlyConsumption ){
			averagePrice+=d;
		}
		averagePrice = averagePrice/hourlyConsumption.length;

		return averagePrice;

	}
	
	
	
	public double calculateAlpha(double dailyLoad){
		double p = 2.02;
		double alpha = 0;
		
		double upper = (p*dailyLoad);
		double lower = (Math.pow(dailyLoad, 2))*(Math.log10(dailyLoad+1));
		alpha = upper/lower;
		return alpha;
		
	}
	
	
	
	
	

	
}
	
	

