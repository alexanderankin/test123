/*
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

//{{{ Imports
import java.util.List;

import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.Token;
//}}}

/**
 * A variable. It could be a simple variable, or contains another variable.
 *
 * @author Matthieu Casanova
 */
public class Variable extends AbstractVariable
{
	/**
	 * The name of the variable.
	 */
	private String name;

	/**
	 * A variable inside ($$varname).
	 */
	private AbstractVariable variable;

	/**
	 * the variable is defined like this ${expression}.
	 */
	private Expression expression;

	private static final String _GET = "_GET";
	private static final String _POST = "_POST";
	private static final String _REQUEST = "_REQUEST";
	private static final String _SERVER = "_SERVER";
	private static final String _SESSION = "_SESSION";
	private static final String _this = "this";
	private static final String GLOBALS = "GLOBALS";
	private static final String _COOKIE = "_COOKIE";
	private static final String _FILES = "_FILES";
	private static final String _ENV = "_ENV";

	/**
	 * Here is an array of all superglobals variables and the special "this".
	 */
	public static final String[] SPECIAL_VARS = {_GET,
		_POST,
		_REQUEST,
		_SERVER,
		_SESSION,
		_this,
		GLOBALS,
		_COOKIE,
		_FILES,
		_ENV};

	//{{{ Variable constructors

	/**
	 * Create a new simple variable.
	 *
	 * @param name	the name
	 * @param sourceStart the starting position
	 * @param sourceEnd   the ending position
	 */
	public Variable(String name,
			int sourceStart,
			int sourceEnd,
			int beginLine,
			int endLine,
			int beginColumn,
			int endColumn)
	{
		super(Type.UNKNOWN, sourceStart, sourceEnd, beginLine,
			endLine, beginColumn, endColumn);
		this.name = name;
	}

	/**
	 * Create a new simple variable.
	 *
	 * @param token the token
	 */
	public Variable(Token token)
	{
		super(Type.UNKNOWN, token.sourceStart, token.sourceEnd,
			token.beginLine, token.endLine, token.beginColumn,
			token.endColumn);
		this.name = token.image;
	}

	/**
	 * Create a special variable ($$toto for example).
	 *
	 * @param variable    the variable contained
	 * @param sourceStart the starting position
	 * @param sourceEnd   the ending position
	 */
	public Variable(AbstractVariable variable,
			int sourceStart,
			int sourceEnd,
			int beginLine,
			int endLine,
			int beginColumn,
			int endColumn)
	{
		super(Type.UNKNOWN, sourceStart, sourceEnd, beginLine, endLine,
			beginColumn, endColumn);
		this.variable = variable;
	}

	/**
	 * Create a special variable ($$toto for example).
	 *
	 * @param expression  the variable contained
	 * @param sourceStart the starting position
	 * @param sourceEnd   the ending position
	 */
	public Variable(Expression expression,
			int sourceStart,
			int sourceEnd,
			int beginLine,
			int endLine,
			int beginColumn,
			int endColumn)
	{
		super(Type.UNKNOWN, sourceStart, sourceEnd, beginLine, endLine,
			beginColumn, endColumn);
		this.expression = expression;
	} //}}}

	//{{{ toStringExpression() method

	/**
	 * Return the expression as String.
	 *
	 * @return the expression
	 */
	@Override
	public String toStringExpression()
	{
		return '$' + getName();
	} //}}}

	//{{{ getName() method

	@Override
	public String getName()
	{
		if (name != null)
		{
			return name;
		}
		if (variable != null)
		{
			return variable.toStringExpression();
		}
		return '{' + expression.toStringExpression() + '}';
	} //}}}

	//{{{ getOutsideVariable() method

	/**
	 * This method will return the current variable.
	 *
	 * @param list we will add the current method to the list
	 */
	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
		getUsedVariable(list);
	} //}}}

	//{{{ getModifiedVariable() method

	/**
	 * This method will return the current variable.
	 *
	 * @param list we will add the current method to the list
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
		getUsedVariable(list);
	} //}}}

	//{{{ getUsedVariable() mehod

	/**
	 * This method will return the current variable.
	 *
	 * @param list we will add the current method to the list
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		String varName;
		if (name != null)
		{
			varName = name;
		}
		else if (variable != null)
		{
			varName = variable.getName();
		}
		else
		{
			varName = expression.toStringExpression();//todo : do a better thing like evaluate this ??
		}
		if (!arrayContains(SPECIAL_VARS, name))
		{
			list.add(new VariableUsage(type,
				varName,
				sourceStart,
				sourceEnd,
				beginLine,
				endLine,
				beginColumn,
				endColumn));
		}
	} //}}}

	//{{{ subNodeAt() method

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (variable != null && variable.isAt(line, column)) return variable;
		if (expression != null && expression.isAt(line, column)) return expression;
		return null;
	} //}}}

	//{{{ analyzeCode() method

	@Override
	public void analyzeCode(PHPParser parser)
	{
		if (name == null)
		{
			if (variable != null)
			{
				variable.analyzeCode(parser);
			}
			else
			{
				expression.analyzeCode(parser);
			}
		}
	} //}}}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Variable variable1 = (Variable) o;

		if (expression != null ? !expression.equals(variable1.expression) : variable1.expression != null)
			return false;
		if (name != null ? !name.equals(variable1.name) : variable1.name != null) return false;
		if (variable != null ? !variable.equals(variable1.variable) : variable1.variable != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (variable != null ? variable.hashCode() : 0);
		result = 31 * result + (expression != null ? expression.hashCode() : 0);
		return result;
	}
}
