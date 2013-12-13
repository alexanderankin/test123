/*
 * OperatorExpression.java 
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

/**
 * Any expression that have an operator.
 *
 * @author Matthieu Casanova
 */
public abstract class OperatorExpression extends Expression
{

	private final int operator;

	protected OperatorExpression(Type type,
				     int operator,
				     int sourceStart,
				     int sourceEnd,
				     int beginLine,
				     int endLine,
				     int beginColumn,
				     int endColumn)
	{
		super(type, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.operator = operator;
	}

	public String operatorToString()
	{
		return PHPParserConstants.tokenImage[operator];
	}
}
