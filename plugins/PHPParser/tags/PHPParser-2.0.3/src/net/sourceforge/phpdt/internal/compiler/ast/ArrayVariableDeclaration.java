/*
 * ArrayVariableDeclaration.java
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
 * a variable declaration in an array().
 * it could take Expression as key.
 *
 * @author Matthieu Casanova
 */
public class ArrayVariableDeclaration extends Expression
{

	/**
	 * the array key.
	 */
	private final Expression key;

	/**
	 * the array value.
	 */
	private Expression value;

	public ArrayVariableDeclaration(Expression key,
					Expression value,
					int sourceStart,
					int sourceEnd,
					int beginLine,
					int endLine,
					int beginColumn,
					int endColumn)
	{
		super(Type.UNKNOWN, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.key = key;
		this.value = value;
	}

	/**
	 * Create a new array variable declaration.
	 *
	 * @param key       the key
	 * @param sourceEnd the end position
	 */
	public ArrayVariableDeclaration(Expression key,
					int sourceEnd,
					int beginLine,
					int endLine,
					int beginColumn,
					int endColumn)
	{
		super(Type.UNKNOWN, key.getSourceStart(), sourceEnd, beginLine, endLine, beginColumn, endColumn);
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
		if (value == null)
		{
			return key.toStringExpression();
		}
		else
		{
			String keyString = key.toStringExpression();
			String valueString = value.toStringExpression();
			StringBuilder buff = new StringBuilder(keyString.length() + valueString.length() + 3);
			buff.append(keyString);
			buff.append(" => ");
			buff.append(valueString);
			return buff.toString();
		}
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
		key.getModifiedVariable(list);
		if (value != null)
		{
			value.getModifiedVariable(list);
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
		key.getUsedVariable(list);
		if (value != null)
		{
			value.getUsedVariable(list);
		}
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (key.isAt(line, column)) return key;
		if (value != null && value.isAt(line, column)) return value;
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		key.analyzeCode(parser);
		if (value != null)
		{
			value.analyzeCode(parser);
		}
	}
}
