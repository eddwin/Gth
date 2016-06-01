/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package benchmark.roundTripTime;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;


/**
 * Receiver agent for round trip benchmark.<br>
 * Test of the messaging and scalability.
 * When receiver agent receive the message form sender agent, it send back the same message.
 * <br>Just run:<br>
 * <code>java jade.Boot -conf receiver.conf</code><br>
 * where <i>receiver.conf</i> is a text file where the agent find parameter values.<br>
 * 
 * @author Elisabetta Cortese - TiLab
 */

 public class RoundTripReceiver extends Agent {

 /**
  * Wait and send back message.
  */
    void roundTripTime() {
        ACLMessage msg = blockingReceive();
        msg = msg.createReply();
        send(msg);
    }

    /**
     * Set the receiver's behaviour. with a <code>CyclicBehaviour</code>
     */
    public void setup() {

        addBehaviour( new CyclicBehaviour(this)
        {
            public void action() {
                roundTripTime();
            }
        });
    }
}
