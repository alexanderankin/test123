/*
 * AbstractVariable.java
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
 * An access to a key of an array.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class ArrayDeclarator extends AbstractVariable
{
	/**
	 * The name of the array.
	 */
	private final Expression prefix;

	/**
	 * The key.
	 */
	private final Expression key;
	private static final long serialVersionUID = 4303473470051707778L;


	/**
	 * Create an ArrayDeclarator.
	 *
	 * @param prefix    the prefix, it could be a variable.
	 * @param key       the key
	 * @param sourceEnd the end of the expression
	 * @param endLine   the end of the line
	 * @param endColumn the end of the column
	 */
	public ArrayDeclarator(Expression prefix,
			       Expression key,
			       int sourceEnd,
			       int endLine,
			       int endColumn)
	{
		super(Type.UNKNOWN,
			prefix.getSourceStart(),
			sourceEnd,
			prefix.getBeginLine(),
			endLine,
			prefix.getBeginColumn(),
			endColumn);
		this.prefix = prefix;
		this.key = key;
	}

	/**
	 * Return the expression as String.
	 *
	 * @return the expression
	 */
	@Override
	public String toStringExpression()
	{
		StringBuilder buff = new StringBuilder(prefix.toStringExpression());
		buff.append('[');
		if (key != null)
		{
			buff.append(key.toStringExpression());
		}
		buff.append(']');
		return buff.toString();
	}

	/**
	 * Return the name of the variable.
	 *
	 * @return the name of the functionName variable
	 */
	@Override
	public String getName()
	{
		return prefix.toStringExpression();
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
		prefix.getModifiedVariable(list);
		if (key != null)
		{
			key.getModifiedVariable(list);
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
		if (key != null)
		{
			key.getUsedVariable(list);
		}
	}

	@Override
	public Expression expressionAt(int line, int column)
	{
		if (prefix.isAt(line, column)) return prefix;
		if (key != null && key.isAt(line, column)) return key;
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		prefix.analyzeCode(parser);
		if (key != null)
		{
			key.analyzeCode(parser);
		}
	}
}
