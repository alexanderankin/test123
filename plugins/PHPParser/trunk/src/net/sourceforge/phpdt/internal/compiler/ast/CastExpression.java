/*
 * CastExpression.java
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
 * This is a cast expression.
 *
 * @author Matthieu Casanova
 */
public class CastExpression extends Expression
{
	/**
	 * The castTarget in which we cast the expression.
	 */
	private final ConstantIdentifier castTarget;

	/**
	 * The expression to be casted.
	 */
	private final Expression expression;

	/**
	 * Create a cast expression.
	 *
	 * @param type	the castTarget
	 * @param expression  the expression
	 * @param sourceStart starting offset
	 * @param sourceEnd   ending offset
	 */
	public CastExpression(ConstantIdentifier type,
			      Expression expression,
			      int sourceStart,
			      int sourceEnd,
			      int beginLine,
			      int endLine,
			      int beginColumn,
			      int endColumn)
	{
		//todo find good type
		super(Type.UNKNOWN, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		castTarget = type;
		this.expression = expression;
	}

	/**
	 * Return the expression as String.
	 *
	 * @return the expression
	 */
	@Override
	public String toStringExpression()
	{
		StringBuilder buff = new StringBuilder("(");
		buff.append(castTarget.toStringExpression());
		buff.append(") ");
		buff.append(expression.toStringExpression());
		return buff.toString();
	}

	/**
	 * Get the variables from outside (parameters, globals ...)
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
		expression.getModifiedVariable(list);
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		expression.getUsedVariable(list);
	}

	@Override
	public Expression expressionAt(int line, int column)
	{
		return expression.isAt(line, column) ? expression : null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		expression.analyzeCode(parser);
	}
}
