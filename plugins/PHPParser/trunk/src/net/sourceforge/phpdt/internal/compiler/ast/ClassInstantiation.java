/*
 * ClassInstantiation.java
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
 * a class instantiation.
 *
 * @author Matthieu Casanova
 */
public class ClassInstantiation extends PrefixedUnaryExpression
{

	private final boolean reference;

	public ClassInstantiation(Expression expression,
				  boolean reference,
				  int sourceStart,
				  int beginLine,
				  int beginColumn)
	{
		super(expression, PHPParserConstants.NEW, sourceStart, beginLine, beginColumn);
		if (expression instanceof FunctionCall)
		{
			FunctionCall call = (FunctionCall) expression;
			setType(new Type(Type.OBJECT_INT, call.getFunctionName().toStringExpression()));
		}
		else
			setType(Type.OBJECT);
		this.reference = reference;
	}

	private String _toString()
	{
	        return "new " + expression.toStringExpression();
	}

	@Override
	public String toStringExpression()
	{
		if (!reference)
		{
			return _toString();
		}
		return '&' + "new " + _toString();
	}
}
