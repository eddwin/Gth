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

/** This is a simple instance of the classical "diet problem". A farmer must choose the amount of food of 5 kinds that it should by. 
 * The cost of each ton of food of type i is given by the vector [90 ; 81 ; 40 ; 24 ; 50]. Food includes two types of nutrients M and N.
 * The amount of kg of nutrient of type M in each ton of food of each type is given by the vector [200 ; 150 ; 100 ; 45 ; 100].
 * The amount of kg of nutrient of type N in each ton of food of each type is given by: [300 ; 270 ; 140 ; 90 ; 400].
 * The problem consists of finding the amount of tons to buy of each type, so that we obtain in sum at least 5500 kg of nutrient M and 8700 of nutrient N,
 * and the total cost is minimum. The problem is solved in a linear formulation.
 * @author Pablo Pavon Mariño
 */
public class DietProblem 
{
	public static void main(String[] args)
	{
		
		/* Create the optimization problem object */
		OptimizationProblem op = new OptimizationProblem();

		/* Add the decision variables to the problem */
		op.addDecisionVariable("x", false, new int[] { 1 , 5 }, 0, Double.MAX_VALUE);

		/* Sets the objective function */
		op.setObjectiveFunction("minimize", "[90 ; 81 ; 40 ; 24 ; 50] * x'");
		 
		/* Add the constraints */
  	op.addConstraint("[200 ; 150 ; 100 ; 45 ; 100 ;; 300 ; 270 ; 140 ; 90 ; 400] * x' >= [5500 ;; 8700]", "r_1");

		/* Call the solver to solve the problem */
		op.solve("glpk");

		if (!op.solutionIsOptimal ()) throw new RuntimeException ("An optimal solution was not found");
		
		System.out.println("x: " + op.getPrimalSolution("x"));
		System.out.println("Total cost obtained: " + op.getOptimalCost());
		System.out.println("Multiplier constraint nutrient M: " + op.getMultipliersOfConstraint("r_1").get (0));
	}
}


