package com.agents.house.research;

import java.util.Random;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class TheCreator extends Agent {
	public final static String GATHER = "gather";

	private Object[] mArgs;
	private String[] listOfHouses;
	protected void setup() {
		AgentController a1 = null;
		
		System.out.println(getLocalName()+" STARTED");
		mArgs = new Object[4];
		mArgs[0] = "convenience";
		double[] randomBudget = {110, 140, 180, 210,300,190,340,145,183,200,210,300,160,180,190,390,450,269};
		try{
			//Get container
			ContainerController container = (ContainerController) getContainerController();
			for ( int i = 0; i < 50; i++){
				//randomBudget = random.nextInt(310);
				mArgs[1] = randomBudget();
				mArgs[2] = randomNumOfShifteable();
				mArgs[3] = randomNumOfNonShifteable();
				a1 = container.createNewAgent("house"+i,"com.agents.house.research.HouseAgent", mArgs);
				a1.start();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		addBehaviour(new listener());
		
	}
	
	public double randomBudget(){
		int min = 50;
		int max = 200;
		Random r = new Random();
		int i1 = r.nextInt(max - min + 1) + min;
		return (double) i1;
	}

	public int randomNumOfShifteable (){
		int min = 1;
		int max = 22;

		Random r = new Random();
		int i1 = r.nextInt(max - min + 1) + min;
		return i1;
	}
	
	public int randomNumOfNonShifteable (){
		int min = 1;
		int max = 6;

		Random r = new Random();
		int i1 = r.nextInt(max - min + 1) + min;
		return i1;
	}
	private class listener extends CyclicBehaviour {

		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage msg = receive();
			if (msg != null){
				System.out.println("Received a new Message" + " from " + msg.getSender().getLocalName());
				if (msg.getPerformative() == ACLMessage.REQUEST){
					
					if (GATHER.equals(msg.getConversationId())){ //gather all houses
						gatherHouses();
						System.out.println(myAgent.getLocalName() + " Received message from " + msg.getSender() );
						ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
						AID receiver = new AID("fog1@FogPlatform",AID.ISGUID);
						receiver.addAddresses("http://proxy70.yoics.net:35712");
						reply.addReceiver(receiver);
						reply.setConversationId("List");
						Gson gson = new Gson();
						String json = gson.toJson(listOfHouses);
						reply.setContent(json);
						send(reply);
						System.out.println("Sent names of agents to " + receiver);
					}
				}
				else{
					System.out.println("Not understood");
				}
			}
			else{
				block();	
			}
		}
		
	}
	
	public void gatherHouses(){
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("house-consumption");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			listOfHouses = new String[result.length];	
			for (int i = 0; i < listOfHouses.length; i++){
				listOfHouses[i] = result[i].getName().getLocalName();
			}
		} 
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	
}
