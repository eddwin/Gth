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

/** We must decide the location (X,Y) of a storage facility. This facility is targeted to serve different amounts of material to 4 different destinations 1, 2, 3, 4.
 * The amount of material to serve to each destination is given by the vector [9 , 3 , 12 , 5].
 * The (x,y) location of these destinations is known and given by { ( 7 , 2) , (3 , 8) , (15 , 4) , (1 , 13) } 
 * The storage facility cannot be further than 3 (euclidean) distance units, from the point (5 , 5).
 * We are interested on minimizing the transportation costs associated to the storage facilities, given by the sum for each destination, 
 * of the amount of material to be transported there, multiplied by the distance.
 * The problem is convex, and solved with the IPOPT solver. Since the problem is convex we know that 
 * the local optimum solution provided by IPOPT solver, is also a global optimum. 
 * @author Pablo Pavon Mariño
 */
public class LocationStorage 
{
	public static void main(String[] args)
	{
		/* Create the optimization problem object */
		OptimizationProblem op = new OptimizationProblem();

		/* Add the decision variables to the problem */
		op.addDecisionVariable("x", false, new int[] { 1 , 1 }, -Double.MAX_VALUE , Double.MAX_VALUE);
		op.addDecisionVariable("y", false, new int[] { 1 , 1 }, -Double.MAX_VALUE , Double.MAX_VALUE);

		/* Sets the objective function */
		op.setObjectiveFunction("minimize", "9 * sqrt((7-x)^2+(2-y)^2) + 3 * sqrt((3-x)^2+(8-y)^2) + 12 * sqrt((15-x)^2+(4-y)^2) + 5 * sqrt((1-x)^2+(13-y)^2)");
		
		/* Add the constraints */
  	op.addConstraint("sqrt((x-5)^2 + (y-5)^2) <= 3", "c_1");
  	

		/* Set initial solution (optional) */
		op.setInitialSolution("x", 0);
		op.setInitialSolution("y", 0);

		/* Call the solver to solve the problem */
		op.solve("ipopt");

		if (!op.solutionIsOptimal ()) throw new RuntimeException ("An optimal solution was not found");
		
		double x = op.getPrimalSolution("x").toValue();
		double y = op.getPrimalSolution("y").toValue();
		System.out.println("x: " + x);
		System.out.println("y: " + y);
		System.out.println("Distance to (5,5):" + Math.sqrt(Math.pow(x-5, 2) + Math.pow(y-5, 2)));
		System.out.println("Total cost obtained: " + op.getOptimalCost());
		System.out.println("Multiplier of constraint: " + op.getMultipliersOfConstraint("c_1").toValue());
	}
}


