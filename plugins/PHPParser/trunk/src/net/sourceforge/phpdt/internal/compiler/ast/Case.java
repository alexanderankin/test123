/*
 * Case.java
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
 * A Case statement for a Switch.
 *
 * @author Matthieu Casanova
 */
public class Case extends AbstractCase
{
	private final Expression value;

	public Case(Expression value,
		    Statement[] statements,
		    int sourceStart,
		    int sourceEnd,
		    int beginLine,
		    int endLine,
		    int beginColumn,
		    int endColumn)
	{
		super(statements, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.value = value;
	}

	/**
	 * Return the object into String.
	 *
	 * @param tab how many tabs (not used here
	 * @return a String
	 */
	@Override
	public String toString(int tab)
	{
		StringBuilder buff = new StringBuilder(tabString(tab));
		buff.append("case ");
		buff.append(value.toStringExpression());
		buff.append(" :\n");
		if (statements != null)
		{
			for (Statement statement : statements)
			{
				buff.append(statement.toString(tab + 1));
			}
		}
		return buff.toString();
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
		super.getModifiedVariable(list);
		value.getModifiedVariable(list);
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		super.getUsedVariable(list);
		value.getUsedVariable(list);
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (value.isAt(line, column)) return value;
		return super.subNodeAt(line, column);
	}


	@Override
	public void analyzeCode(PHPParser parser)
	{
		value.analyzeCode(parser);
		for (Statement statement : statements)
		{
			statement.analyzeCode(parser);
		}
	}
}
