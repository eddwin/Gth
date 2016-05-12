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
package com.jom;

import com.jom.javaluator.Operator;

class _INTERNAL_OperatorInfo
{
	private final String functionClassName;

	private final Operator operator;

	_INTERNAL_OperatorInfo(String symbol, int operandCount, Operator.Associativity associativity, int precedence, String functionClassName)
	{
		this.operator = new Operator(symbol, operandCount, associativity, precedence); 
		this.functionClassName = functionClassName;
	}

	String getFunctionClassName()
	{
		return this.functionClassName;
	}

	Operator getOperator()
	{
		return this.operator;
	}

}
