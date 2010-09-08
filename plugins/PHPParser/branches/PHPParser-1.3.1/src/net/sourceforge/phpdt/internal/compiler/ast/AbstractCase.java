/*
 * AbstractCase.java
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

import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * Superclass of case statement that we can find in a switch.
 *
 * @author Matthieu Casanova
 */
public abstract class AbstractCase extends Statement
{
	/**
	 * The statements in the case.
	 */
	protected final Statement[] statements;

	/**
	 * Create a case statement.
	 *
	 * @param statements  the statements array
	 * @param sourceStart the beginning source offset
	 * @param sourceEnd   the ending offset
	 * @param beginLine   begin line
	 * @param endLine     end line
	 * @param beginColumn begin column
	 * @param endColumn   end column
	 */
	protected AbstractCase(Statement[] statements,
			       int sourceStart,
			       int sourceEnd,
			       int beginLine,
			       int endLine,
			       int beginColumn,
			       int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.statements = statements;
	}

	/**
	 * Get the variables from outside (parameters, globals ...).
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public final void getOutsideVariable(List<VariableUsage> list)
	{
		for (Statement statement : statements)
		{
			statement.getOutsideVariable(list);
		}
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
		for (Statement statement : statements)
		{
			statement.getModifiedVariable(list);
		}
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		for (Statement statement : statements)
		{
			statement.getUsedVariable(list);
		}
	}

	@Override
	public Expression expressionAt(int line, int column)
	{
		if (statements != null)
		{
			for (Statement statement : statements)
			{
				if (statement.isAt(line, column))
					return statement.expressionAt(line, column);
			}
		}
		return null;
	}
}
