/*
 * BranchStatement.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2010 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * Here is a branchstatement : break or continue.
 *
 * @author Matthieu Casanova
 */
public abstract class BranchStatement extends Statement
{
	/**
	 * The label (if there is one).
	 */
	protected final Expression expression;

	protected BranchStatement(Expression expression, int sourceStart, int sourceEnd,
				  int beginLine,
				  int endLine,
				  int beginColumn,
				  int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.expression = expression;
	}


	/**
	 * Get the variables from outside (parameters, globals ...)
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public final void getOutsideVariable(List<VariableUsage> list)
	{
		if (expression != null)
		{
			expression.getOutsideVariable(list);
		}
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public final void getModifiedVariable(List<VariableUsage> list)
	{
		if (expression != null)
		{
			expression.getModifiedVariable(list);
		}
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public final void getUsedVariable(List<VariableUsage> list)
	{
		if (expression != null)
		{
			expression.getUsedVariable(list);
		}
	}

	@Override
	public Expression expressionAt(int line, int column)
	{
		return expression.isAt(line, column) ? expression : null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		if (expression != null)
		{
			expression.analyzeCode(parser);
		}
	}
}
