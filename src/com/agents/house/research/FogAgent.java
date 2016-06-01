package com.agents.house.research;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import  org.apache.poi.hssf.usermodel.HSSFSheet;
import  org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import  org.apache.poi.hssf.usermodel.HSSFCell;

import com.google.gson.Gson;
import com.house.research.Appliance;
import com.house.research.Function;
import com.house.research.Loads;
import com.house.research.PowerVariable;
import com.house.research.ResultsWarehouse;

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
	private double startRoundTime;
	private double endRoundTime;
	private double initTime;
	int numOfChanges;
	private ResultsWarehouse warehouse;
	
	Map<String, double[]> initSchedules  = new HashMap<String, double[]>();
	Map<String, double[]> roundTimers = new HashMap<String, double[]>();
	Map<String, double[]> preferences = new HashMap<String, double[]>();
	 public final static String DAILY = "daily-consumption";
	 public final static String PRICE = "prices";
	 public final static String ACCEPTANCE = "acceptance";

	
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	protected void setup() {
		System.out.print("Hey there! Fog-Agent " + getAID().getName() + " is ready");
		 
		
		//Initialize variables
		pv = new PowerVariable();
		warehouse = new ResultsWarehouse();
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
		addBehaviour(new WakerBehaviour (this, 10000){
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
			
			ACLMessage msg = receive();
			if (msg != null){
				if (msg.getPerformative() == ACLMessage.INFORM){
					
					if (DAILY.equals(msg.getConversationId())) {
						try {
							gatherInitSchedules(msg);
						} catch (Exception e) {
						
							e.printStackTrace();
						}
					}
					else if (PRICE.equals(msg.getConversationId())){
						
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
		ACLMessage message = new ACLMessage(ACLMessage.AGREE);//RQST Message
		for (int i =0; i <houseAgents.length;i++){
			message.addReceiver(houseAgents[i]);
		}
		message.setConversationId(ACCEPTANCE);
		message.setReplyWith("Request" + System.currentTimeMillis());
		//Send final pricing
		double[] precios = pv.getPriceArray();
		Gson gson = new Gson();
		String json = gson.toJson(precios);
		message.setContent(json);
		showHourlyLoad();
		send(message);
		Loads load = new Loads();
		load.setLoads(precios);
		PostIt(load);
	}
	
	public void gatherInitSchedules(ACLMessage msg) throws Exception{
		
		if (initCounter <= houseAgents.length ){ 
			double reception = System.currentTimeMillis();
			initCounter++;
			String json = msg.getContent();
			Gson gson = new Gson();
			//cargaHora = pv.getHourlyLoadArray();
			double [] casaLoad = gson.fromJson(json, double[].class);
			initScheduleTracker(msg.getSender().getLocalName(), reception, casaLoad[24]);
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
				writeToExcel("initSchedule");
				senderToNextAgent();
				
			}
		}
			
	}

	public void senderToNextAgent(){
		
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
			double avgPrice = averagePrice(pv.getPriceArray());
			System.out.println("Average price is: " + avgPrice );
			sendStatusOfEndGame();
		} else if(numOfChanges > 0){
			if(numOfChanges <= houseAgents.length && responseCounter == houseAgents.length){
				//End of round, still playing unless 70 (for now)
				round++;
				if (round == 70){
					sendStatusOfEndGame();
					//Print to Excel
					warehouse.printWareHouse();
				}
				else{
					//Store meassures!
					storeRoundTracker("Round"+round);
					
					
					System.out.println("number of houses: " + houseAgents.length);
					System.out.println("Number of acceptance: " + numOfAcceptance );
					System.out.println("Number of changes: " + numOfChanges);
					System.out.println("Number of Responses: " + responseCounter);
					System.out.println("Current Load is: " + Lh);
					double avgPrice = averagePrice(pv.getPriceArray());
					System.out.println("Average price is: " + avgPrice );
					System.out.println("Round number: " + round) ;
				numOfAcceptance = 0;
				numOfChanges = 0;
				responseCounter = 0;
				senderToNextAgent();
				}
				
			}else{
			//Still playing
				senderToNextAgent();
			}
		}
		else{
			//Still playing
			senderToNextAgent();
		}
	}
		
		

	public void  sendPrice(AID Agent){
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);//RQST Message
		Gson gson = new Gson();
		if(pv.getPriceArray()!= null){
			//prepare package
			double[] loadOfRestOfHouses = new double[25];
			loadOfRestOfHouses = getSchedulesOfRestOfHouses(Agent.getLocalName());
			
			loadOfRestOfHouses[24] = alpha;
			String json = gson.toJson(loadOfRestOfHouses);
			message.addReceiver(Agent);
			message.setConversationId(PRICE);
			message.setReplyWith("Request" + System.currentTimeMillis());
			message.setContent(json);
			showHourlyLoad();
			
			initTime = System.currentTimeMillis(); //Meassures
			send(message);
		}
	}
	public double[] getSchedulesOfRestOfHouses(String housename){
		
		Map<String, double[]> restOfHouses = new HashMap<String, double[]>();
		restOfHouses.putAll(preferences);
		restOfHouses.remove(housename);
		
		double[] others = restOfHousesLoad(restOfHouses);
		return others;
		
		
	}
	public void storeHousePreferences(double[] houseLoad, String houseName){
		preferences.put(houseName, houseLoad);
	}
	
	public void storeHousesPreferenceAndUpdate(double[] houseLoad, String houseName){
		//check if house has already submitted a preference
			preferences.put(houseName, houseLoad);
			calculateLoad(); //Recalculate Load and price
	}
	public double[] restOfHousesLoad(Map<String, double[]> restOfHouses){
		double [] restLoad = new double[26];
		Arrays.fill(restLoad, 0);
		
		for (Map.Entry<String, double[]> entry:restOfHouses.entrySet()){
			double[] schedule = entry.getValue();
			for (int i = 0; i < 24; i++){
				restLoad[i]+=schedule[i];			
			}
		}
		
		return restLoad;
		
	}
	public void calculateLoad(){
		//double [] cargaHora = new double[24];
		Arrays.fill(cargaHora, 0);
		Lh = 0;
		for (Map.Entry<String, double[]> entry:preferences.entrySet()){
			double[] schedule = entry.getValue();
			for (int i = 0; i < 24; i++){
				cargaHora[i]+=schedule[i];
				
			}
		}
		for (double d: cargaHora){
			Lh+=d;
		}
	   // alpha = calculateAlpha(Lh);
		calculateHourlyPrices(alpha, cargaHora);
		
	}
	
	public void showHourlyLoad(){
		for (double d: cargaHora){
		}
	}
	
	
	public void gatherNewSchedule(ACLMessage msg){
		double finalTime = System.currentTimeMillis();
		
		//reception, send and middle
		
		responseCounter++;
		String json = msg.getContent();
		Gson gson = new Gson();
		double [] casaLoad = gson.fromJson(json, double[].class);
		//TODO:STORE TIMES HERE
		messageRoundTracker(msg.getSender().getLocalName(), initTime, casaLoad[25], finalTime);
		storeHousesPreferenceAndUpdate(casaLoad, msg.getSender().getLocalName());
		agentChecker();
	}
	
	
	
	
	public void calculateHourlyPrices( double alpha, double[] hourlyLoad) {
		double[] hourlyPrices = new double[25];
		//alpha*Lh*Log10(lh+1)
		//double alpha = 0.2;
		for (int i = 0; i< 24; i++){
			hourlyPrices[i] = hourlyLoad[i] * alpha * Math.log10(hourlyLoad[i] + 1);
		}
		hourlyPrices[24] = alpha;
		
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
	
	public void roundScheduleTracker(String sender,double receptionTime, double sentTime ){
		
	}
	
	public void initScheduleTracker(String sender, double receptionTime, double sentTime){
		double[] times = {receptionTime, sentTime};
		initSchedules.put(sender, times);
		
	}
	
	public void storeRoundTracker(String round){
		warehouse.addToWarehouse(round, roundTimers);

	}
	
	public void messageRoundTracker(String sender, double sentTime, double middleTime, double receptionTime){
		double[] times = {sentTime, middleTime, receptionTime};
		roundTimers.put(sender, times);
	}
	
	public void writeToExcel(String type) throws Exception{
	
		if (type == "initSchedule" ) {
			try{
	    		String filename = "/Users/edwinmejia/Documents/workspace/Houses_With_Agents /result/resultFog.xls";
	            HSSFWorkbook workbook = new HSSFWorkbook();
	            HSSFSheet sheet = workbook.createSheet("InitSched");

	            HSSFRow rowhead = sheet.createRow((short)0);
	            rowhead.createCell(0).setCellValue("Name");
	            rowhead.createCell(1).setCellValue("receptionTime");
	            rowhead.createCell(2).setCellValue("sendTime");

	            int rowM = 0;     
	            for (Map.Entry<String, double[]> entry:initSchedules.entrySet()){
	            	rowM++;
	    			double[] times = entry.getValue();
	    			double receptionTime = times[0];
	    			double sendTime = times[1];
	    			String name = entry.getKey();
	    			HSSFRow row = sheet.createRow((short)rowM);
	    			row.createCell(0).setCellValue(name);
	    			row.createCell(1).setCellValue(receptionTime);
	    			row.createCell(2).setCellValue(sendTime);
	    		}
	            
	            
	            FileOutputStream fileOut = new FileOutputStream(filename);
	            workbook.write(fileOut);
	            fileOut.close();
	            System.out.println("Your excel file has been generated!");
	            
	    	}catch(Exception e){
	    		 System.out.println(e);
	    	}
		}
		
		
	}
	
	public void PostIt(Loads load){
		
		Gson gson = new Gson();
    	String json = gson.toJson(load);
		
			HttpClient httpClient = HttpClientBuilder.create().build();
		   HttpResponse response;
	   	
	   	try {
			HttpPost post = new HttpPost("http://powerapi.herokuapp.com/loads");
			 StringEntity se = new StringEntity(json);  
			 se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(se);
		 response = httpClient.execute(post);
		 
		 /*Checking response */
	     if(response!=null){
	         InputStream in = response.getEntity().getContent(); //Get the data in the entity
	     }
	   	}catch (Exception ex){
	   		ex.printStackTrace();
	   		System.out.println("Error, Cannot Estabilish Connection");

	   	}
		
	}

	

	
}
	
	

