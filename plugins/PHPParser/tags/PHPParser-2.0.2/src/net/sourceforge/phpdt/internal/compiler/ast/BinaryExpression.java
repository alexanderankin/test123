/*
 * BinaryExpression.java - A Binary Expression
 * Copyright (C) 2004-2010 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
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

import gatchan.phpparser.parser.PHPParserConstants;
import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * a binary expression is a combination of two expressions with an operator.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class BinaryExpression extends OperatorExpression
{
	/**
	 * The left expression.
	 */
	private final Expression left;
	/**
	 * The right expression.
	 */
	private final Expression right;

	public BinaryExpression(Expression left,
				Expression right,
				int operator,
				int sourceStart,
				int sourceEnd,
				int beginLine,
				int endLine,
				int beginColumn,
				int endColumn)
	{
		super(Type.UNKNOWN, operator, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.left = left;
		this.right = right;
		switch (operator)
		{
			case PHPParserConstants.OR_OR:
			case PHPParserConstants.AND_AND:
			case PHPParserConstants._ORL:
			case PHPParserConstants.XOR:
			case PHPParserConstants._ANDL:
			case PHPParserConstants.EQUAL_EQUAL:
			case PHPParserConstants.GT:
			case PHPParserConstants.LT:
			case PHPParserConstants.LE:
			case PHPParserConstants.GE:
			case PHPParserConstants.NOT_EQUAL:
			case PHPParserConstants.DIF:
			case PHPParserConstants.BANGDOUBLEEQUAL:
			case PHPParserConstants.TRIPLEEQUAL:
				type = Type.BOOLEAN;
				break;
			case PHPParserConstants.DOT:
				type = Type.STRING;
				break;
			case PHPParserConstants.BIT_AND:
			case PHPParserConstants.BIT_OR:
			case PHPParserConstants.BIT_XOR:
			case PHPParserConstants.LSHIFT:
			case PHPParserConstants.RSIGNEDSHIFT:
			case PHPParserConstants.RUNSIGNEDSHIFT:
			case PHPParserConstants.PLUS:
			case PHPParserConstants.MINUS:
			case PHPParserConstants.STAR:
			case PHPParserConstants.SLASH:
			case PHPParserConstants.REMAINDER:
				type = Type.INTEGER;
				break;
			case PHPParserConstants.INSTANCEOF:
				type = Type.BOOLEAN;
				break;
		}
	}

	@Override
	public String toStringExpression()
	{
		StringBuilder buff = new StringBuilder();
		buff.append(left.toStringExpression());
		buff.append(operatorToString());
		if (right != null)
			buff.append(right.toStringExpression());
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
		left.getModifiedVariable(list);
		if (right != null)
		{
			right.getModifiedVariable(list);
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
		left.getUsedVariable(list);
		if (right != null)
		{
			right.getUsedVariable(list);
		}
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (left.isAt(line, column)) return left;
		if (right != null && right.isAt(line, column)) return right;
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		left.analyzeCode(parser);
		if (right != null) right.analyzeCode(parser);
		// todo analyze binary expression
	}
}
