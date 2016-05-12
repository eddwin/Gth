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

import com.jom.DoubleMatrixND;
import com.jom.OptimizationProblem;


/** This code implements the 3-dimensional matching problem described in the JOM web site. Please, see more information there
 * @author Pablo Pavon Mariño
 *
 */
public class WWW_3DimMatching 
{
	public static void main(String[] args)
	{
		int N = 5; // number of elements in each set
		
		/* Create the optimization problem object */
		OptimizationProblem op = new OptimizationProblem();

		/* Add the decision variables to the problem */
		op.addDecisionVariable("x", true, new int[] { N , N , N }, 0, 1);  // name, isInteger, size , minValue, maxValue

		/* Set value for the input parameter c_{ijk} */
		op.setInputParameter("c", new DoubleMatrixND(new int [] { N , N , N} , "random"));
		
		/* Sets the objective function */
		op.setObjectiveFunction("maximize", "sum(x .* c)");
		 
		/* Add the constraints */
	  op.addConstraint(" sum(sum(x,3),2) <= 1");  // for each i \sum_{jk} x_{ijk} <= 1
	  op.addConstraint(" sum(sum(x,3),1) <= 1");  // for each j \sum_{ik} x_{ijk} <= 1
  	op.addConstraint(" sum(sum(x,2),1) <= 1");  // for each k \sum_{ij} x_{ijk} <= 1
  	
		/* Call the solver to solve the problem */
		op.solve("glpk");
		if (!op.solutionIsOptimal ()) throw new RuntimeException ("An optimal solution was not found");
		
		/* Print the solution */
		DoubleMatrixND sol = op.getPrimalSolution("x");
		for (int c1 = 0 ; c1 < N ; c1 ++)
			for (int c2 = 0 ; c2 < N ; c2 ++)
				for (int c3 = 0 ; c3 < N ; c3 ++)
					if (sol.get(new int [] { c1 , c2 , c3}) == 1)
						System.out.println (c1 + " - " + c2 + " - " + c3);
	}
}
