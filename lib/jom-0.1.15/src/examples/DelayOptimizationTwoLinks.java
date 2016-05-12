/*******************************************************************************
 * Copyright (c) 2013 Pablo Pavon Mariño.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     Pablo Pavon Mariño - initial API and implementation
 ******************************************************************************/
package examples;
import com.jom.OptimizationProblem;

/** Ten units of packet traffic must be distributed into two links, of capacities 10 and 5, so that the average packet delay is minimized. The delay T_e in 
 * a link e is given by the M/M/1: T_e = 1 / u_e - y_e, where u_e is the link capacity and y_e the offered traffic to the link. 
 * If x and y are the amounts of traffic directed to each link, the average blocking is given by: x T_1 + y T_2,  
 * where T_1 and T_2 are the delays in each link. The objective function is known to be convex, thus
 * the local optimum solution provided by IPOPT solver, is also a global optimum.     
 * @author Pablo Pavon Mariño
 */
public class DelayOptimizationTwoLinks 
{
	public static void main(String[] args)
	{
		
		/* Create the optimization problem object */
		OptimizationProblem op = new OptimizationProblem();

		/* Add the decision variables to the problem */
		op.addDecisionVariable("x", false, new int[] { 1 , 1 }, 0 , 10);
		op.addDecisionVariable("y", false, new int[] { 1 , 1 }, 0 , 5);

		/* Sets the objective function */
		op.setObjectiveFunction("minimize", " ( x / (10-x)) + (y / (5-y))");
		
		/* Add the constraints */
  	op.addConstraint("x + y == 10");

		/* Call the solver to solve the problem */
		op.solve("ipopt");

		if (!op.solutionIsOptimal ()) throw new RuntimeException ("An optimal solution was not found");
		
		System.out.println("x: " + op.getPrimalSolution("x").toValue());
		System.out.println("y: " + op.getPrimalSolution("y").toValue());
		System.out.println("Average delay obtained: " + op.getOptimalCost());
	}
}


