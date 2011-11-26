/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010, 2011 Matthieu Casanova
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
 * An argument list
 *
 * @author Matthieu Casanova
 */
public class ArgumentList extends AstNode
{
	private final Expression[] args;

	public ArgumentList(Expression[] args,
			    int sourceStart,
			    int sourceEnd,
			    int beginLine,
			    int endLine,
			    int beginColumn,
			    int endColumn)
	{
		super(sourceStart,
			sourceEnd,
			beginLine,
			endLine,
			beginColumn,
			endColumn);
		this.args = args;
	}

	Expression[] getArgs()
	{
		return args;
	}

	@Override
	public String toString(int tab)
	{
		return toString();
	}

	@Override
	public String toString()
	{
		if (args == null)
			return "()";
		StringBuilder buff = new StringBuilder();
		buff.append('(');
		for (int i = 0; i < args.length; i++)
		{
			Expression arg = args[i];
			if (i != 0)
			{
				buff.append(',');
			}
			buff.append(arg.toStringExpression());
		}
		buff.append(')');
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
		if (args != null)
		{
			for (Expression arg : args)
			{
				arg.getModifiedVariable(list);
			}
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
		if (args != null)
		{
			for (Expression arg : args)
			{
				arg.getUsedVariable(list);
			}
		}
	}

	public Expression expressionAt(int line, int column)
	{
		if (args == null)
		{
			return null;
		}
		for (int i = 0; i < args.length; i++)
		{
			Expression arg = args[i];
			if (arg.isAt(line, column)) return arg;
		}
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
	}
}
