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

import cern.colt.Arrays;
import cern.colt.matrix.tint.IntMatrix1D;

/* IMPORTANT: VAR IDS ARE ALWAYS CONSECUTIVE */
class _INTERNAL_DecisionVariableArray 
{
	private final int firstVarId;

	private final boolean isInteger;
	private final int lastVarId;
	private final String name;
	private final IntMatrixND varIds;
	private final DoubleMatrixND x_l;
	private final DoubleMatrixND x_u;

	_INTERNAL_DecisionVariableArray(String name, boolean isInteger, IntMatrix1D size, DoubleMatrixND x_l, DoubleMatrixND x_u, int firstVarId)
	{
		this.name = name;
		this.isInteger = isInteger;
		this.firstVarId = firstVarId;
		this.lastVarId = firstVarId + IntMatrixND.prod(size) - 1;
		this.varIds = new IntMatrixND(size, "dense").ascending(firstVarId, 1);
		if (x_l != null)
			this.x_l = x_l;
		else
			this.x_l = new DoubleMatrixND(size.toArray(), -Double.MAX_VALUE, "dense");
		if (x_u != null)
			this.x_u = x_u;
		else
			this.x_u = new DoubleMatrixND(size.toArray(), Double.MAX_VALUE, "dense");

		/* check dimensions */
		if (firstVarId > lastVarId) throw new JOMException ("Unexpected error");
		if (varIds.getNumElements() != lastVarId - firstVarId + 1) throw new JOMException ("Unexpected error");
		if (!this.x_l.getSize().equals(this.x_u.getSize())) throw new JOMException ("Unexpected error");
		if (!size.equals(this.x_u.getSize())) throw new JOMException ("Unexpected error");
	}

	public String toString()
	{
		return "Dec variable array: " + name + " integer (" + isInteger + "), range varId: (" + firstVarId + "..." + lastVarId + ") Size: " + Arrays.toString(this.varIds.getSize().toArray());
	}

	DoubleMatrixND get_x_l()
	{
		return x_l;
	}

	DoubleMatrixND get_x_u()
	{
		return x_u;
	}
	
	int getFirstVarId()
	{
		return firstVarId;
	}

	boolean getIsInteger()
	{
		return isInteger;
	}

	int getLastVarId()
	{
		return lastVarId;
	}

	String getName()
	{
		return name;
	}

	int getNumDecVar()
	{
		return varIds.getNumElements();
	}

	int getNumDim () { return varIds.getNumDim(); }

	IntMatrix1D getSize()
	{
		return varIds.getSize();
	}

	IntMatrixND getVarIds()
	{
		return varIds;
	}

}
