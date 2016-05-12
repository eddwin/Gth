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
/**
 * 
 */
package com.jom;

import java.util.HashSet;
import java.util.LinkedHashMap;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;


/** @author Pablo */
class _OPERATOR_NEG extends Expression
{
	private final Expression a;
	private final boolean isLinear;
	private final boolean isConstant;

	_OPERATOR_NEG(OptimizationProblem model, Expression a)
	{
		super(model);
		
		/* compute size */
		this.resize(a.getSize());

		this.isLinear = (a.isLinear());
		this.isConstant = (a.isConstant());
		this.affineExp = (isLinear)? a.getAffineExpression().operator_neg() : null;

		this.a = (this.isLinear())? null : a; // if linear do not store the expressions. This info is already in the linear coefs matrix
	}

	@Override
	DoubleMatrixND nl_evaluate(double[] valuesDVs)
	{
		return a.evaluate_internal(valuesDVs).assign(DoubleFunctions.neg);
	}

	@Override
	DoubleMatrix2D nl_evaluateJacobian(double[] valuesDVs)
	{
		return a.evaluateJacobian_internal(valuesDVs).assign(DoubleFunctions.neg);
	}

	@Override
	boolean isLinear()
	{
		return this.isLinear;
	}

	/* (non-Javadoc)
	 * @see com.jom.Expression#isConstant()
	 */
	@Override
	boolean isConstant()
	{
		return this.isConstant;
	}

	@Override
	LinkedHashMap<Integer,HashSet<Integer>> nl_getActiveVarIds()
	{
		return a.getActiveVarIds();
	}


}
