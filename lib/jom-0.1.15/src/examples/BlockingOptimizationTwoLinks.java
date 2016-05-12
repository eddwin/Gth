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

/** Ten Erlangs of traffic must be distributed into two links, of capacities 10 and 5, so that the average blocking observed is minimized. The blocking in 
 * a link is given by the Erlang-B formula. If x and y are the fractions of the traffic directed to each link, the average blocking is given by: x Eb(10x,10) + y Eb (10y,5), 
 * where Eb (p,c) is the Erlang-B blocking in a link of capacity c units, offered a traffic of p Erlangs. The objective function is known to be convex, thus
 * the local optimum solution provided by IPOPT solver, is also a global optimum.     
 * @author Pablo Pavon Mariño
 */
public class BlockingOptimizationTwoLinks 
{
	public static void main(String[] args)
	{
		/* Create the optimization problem object */
		OptimizationProblem op = new OptimizationProblem();

		/* Add the decision variables to the problem */
		op.addDecisionVariable("x", false, new int[] { 1 , 1 }, 0 , 1);
		op.addDecisionVariable("y", false, new int[] { 1 , 1 }, 0 , 1);

		/* Sets the objective function */
		op.setObjectiveFunction("minimize", " (x * erlangB(x*10 , 10)) + (y * erlangB(y*10 , 5))");
		
		/* Add the constraints */
		op.addConstraint("x + y == 1");
 
		/* Call the solver to solve the problem */
		op.solve("ipopt");

		if (!op.solutionIsOptimal ()) throw new RuntimeException ("An optimal solution was not found");
		
		System.out.println("x: " + op.getPrimalSolution("x").toValue ());
		System.out.println("y: " + op.getPrimalSolution("y").toValue());
		System.out.println("Total average blocking obtained: " + op.getOptimalCost());
		
	}
}


