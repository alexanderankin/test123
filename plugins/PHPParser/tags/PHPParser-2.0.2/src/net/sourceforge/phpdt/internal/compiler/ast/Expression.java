/*
 * Expression.java
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


/**
 * An expression.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public abstract class Expression extends Statement
{

	protected Type type = Type.UNKNOWN;

	/**
	 * Create an expression giving starting and ending offset
	 *
	 * @param sourceStart starting offset
	 * @param sourceEnd   ending offset
	 */
	protected Expression(Type type,
			     int sourceStart,
			     int sourceEnd,
			     int beginLine,
			     int endLine,
			     int beginColumn,
			     int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.type = type;
	}

	/**
	 * Create an expression giving starting and ending offset
	 *
	 * @param type the type
	 * @param node the node
	 */
	protected Expression(Type type,
			     AstNode node)
	{
		super(node.getSourceStart(), node.getSourceEnd(),
			node.getBeginLine(), node.getEndLine(),
			node.getBeginColumn(), node.getEndColumn());
		this.type = type;
	}

	protected Expression()
	{
	}

	/**
	 * Return the expression with a number of spaces before.
	 *
	 * @param tab how many spaces before the expression
	 * @return a string representing the expression
	 */
	@Override
	public String toString(int tab)
	{
		return tabString(tab) + toStringExpression();
	}

	/**
	 * Return the expression as String.
	 *
	 * @return the expression
	 */
	public abstract String toStringExpression();

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public String toString()
	{
		return getClass().getName() + '[' + toStringExpression() + ',' + type + ']';
	}

}
