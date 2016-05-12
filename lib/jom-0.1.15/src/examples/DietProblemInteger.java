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
 * and the total cost is minimum. The problem has the extra constraint that the food can only be bought in packages. A package of food of type 1 has
 * 1 ton of food, type 2, 2 tons of food, type 3, 5 tons of food, type 4, 2.8 tons of food, type 5, 1 ton of food. It is only possible to buy an integer 
 * number of packages of food of each type. 
 * The problem is solved in a linear integer formulation, where the number of packages of food of each type are the decision variables.
 * @author Pablo Pavon Mariño
 */
public class DietProblemInteger 
{
	public static void main(String[] args)
	{
		/* Create the optimization problem object */
		OptimizationProblem op = new OptimizationProblem();

		/* Add the decision variables to the problem */
		op.addDecisionVariable("x", true, new int[] { 1 , 5 }, 0, Double.MAX_VALUE);

		/* Sets the objective function */
		op.setObjectiveFunction("minimize", "[90*1 ; 81*2 ; 40*5 ; 24*2.8 ; 50*1] * x'");
		 
		/* Add the constraints */
  	op.addConstraint(" [200*1 ; 150*2 ; 100*5 ; 45*2.8 ; 100*1 ;; 300*1 ; 270*2 ; 140*5 ; 90*2.8 ; 400*1]  * x' >= [5500 ;; 8700]");

		/* Call the solver to solve the problem */
  	op.solve("glpk");

		if (!op.solutionIsOptimal ()) throw new RuntimeException ("An optimal solution was not found");
		
		System.out.println("x: " + op.getPrimalSolution("x"));
		System.out.println("Total cost obtained: " + op.getOptimalCost());
	}
}


