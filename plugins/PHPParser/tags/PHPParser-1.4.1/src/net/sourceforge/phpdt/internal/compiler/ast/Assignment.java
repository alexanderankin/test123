/*
 * Assignment.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2010 Matthieu Casanova
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
 * An assignment.
 *
 * @author Matthieu Casanova
 */
public class Assignment extends Expression
{
	private final Expression target;

	/**
	 * The value for variable initialization.
	 */
	private final Expression initialization;

	private boolean reference;

	private final int operator;

	/**
	 * Create a variable.
	 *
	 * @param variable       the name of the variable
	 * @param initialization the initialization (it could be null when you have a parse error)
	 * @param operator       the assign operator
	 * @param sourceStart    the start point
	 * @param sourceEnd      the end point
	 */
	public Assignment(Expression variable,
			  Expression initialization,
			  int operator,
			  int sourceStart,
			  int sourceEnd,
			  int beginLine,
			  int endLine,
			  int beginColumn,
			  int endColumn)
	{
		super(initialization.getType(), sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		variable.setType(initialization.getType());
		this.initialization = initialization;
		target = variable;
		this.operator = operator;
	}


	public void setReference(boolean reference,
				 int sourceStart,
				 int beginLine,
				 int beginColumn)
	{
		this.reference = reference;
		this.sourceStart = sourceStart;
		this.beginLine = beginLine;
		this.beginColumn = beginColumn;
	}

	/**
	 * Return the variable into String.
	 *
	 * @return a String
	 */
	@Override
	public String toStringExpression()
	{
		String variableString = target.toStringExpression();
		String initString = initialization.toStringExpression();
		String operatorImage = PHPParserConstants.tokenImage[operator].substring(1,
			PHPParserConstants.tokenImage[operator].length() - 1);
		StringBuilder buff = new StringBuilder(variableString.length() +
			operatorImage.length() +
			initString.length() +
			1);
		buff.append(variableString);
		buff.append(operatorImage);
		buff.append(initString);
		return buff.toString();
	}

	public String toString()
	{
		return toStringExpression();
	}

	/**
	 * Get the variables from outside (parameters, globals ...)
	 */
	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
	}

	/**
	 * get the modified variables.
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
		target.getModifiedVariable(list);
		initialization.getModifiedVariable(list);
	}

	/**
	 * Get the variables used.
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		if (!(target instanceof Variable))
		{
			target.getUsedVariable(list);
		}
		initialization.getUsedVariable(list);
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (target.isAt(line, column))
			return target;
		if (initialization.isAt(line, column))
			return initialization;
		return null;
	}

	@Override
	public void visitSubNodes(NodeVisitor visitor)
	{
		visitor.visit(target);
		visitor.visit(initialization);
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		/*initialization.analyzeCode(parser);
		    target.analyzeCode(parser);
		    Type targetType = target.getType();
		    Type initType = initialization.getType();
		    if (!targetType.isEmpty() && !initType.isEmpty() && targetType != initType) {
			    parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
								       PHPParseMessageEvent.MESSAGE_CONDITIONAL_EXPRESSION_CHECK,
								       parser.getPath(),
								       "Assignment : warning, you will change the type of the variable "+target.toStringExpression()+", it was "+targetType+" and will be "+initType,
								       sourceStart,
								       sourceEnd,
								       beginLine,
								       endLine,
								       beginColumn,
								       endColumn));
		    }*/
	}
}
