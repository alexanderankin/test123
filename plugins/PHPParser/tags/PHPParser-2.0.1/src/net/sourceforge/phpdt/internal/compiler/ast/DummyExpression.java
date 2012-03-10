/*
 * DummyExpression.java - A dummy expression
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2008 Matthieu Casanova
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
import gatchan.phpparser.parser.Token;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * A dummy expression. It is used when you have parse error and no expression to return
 *
 * @author Matthieu Casanova
 */
public class DummyExpression extends Expression
{
	/**
	 * Instantiate the dummy expression.
	 *
	 * @param sourceStart starting offset
	 * @param sourceEnd   ending offset
	 * @param beginLine   begin line
	 * @param endLine     ending line
	 * @param beginColumn begin column
	 * @param endColumn   ending column
	 */
	public DummyExpression(int sourceStart,
			       int sourceEnd,
			       int beginLine,
			       int endLine,
			       int beginColumn,
			       int endColumn)
	{
		super(Type.UNKNOWN, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
	}

	/**
	 * Instantiate the dummy expression.
	 *
	 * @param token the token where is the dummy expression
	 */
	public DummyExpression(Token token)
	{
		super(Type.UNKNOWN,
			token.sourceStart,
			token.sourceEnd,
			token.beginLine,
			token.endLine,
			token.beginColumn,
			token.endColumn);
	}

	/**
	 * Instantiate the dummy expression.
	 */
	public DummyExpression()
	{
	}

	/**
	 * Return the expression as String.
	 *
	 * @return an empty string
	 */
	@Override
	public String toStringExpression()
	{
		return "";
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
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
	}
}
