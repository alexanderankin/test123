/*
 * StringLiteral.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2009 Matthieu Casanova
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

import java.util.List;

import gatchan.phpparser.parser.Token;
import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

public class StringLiteral extends Literal
{
	private String source;

	private Expression[] expressions;

	public StringLiteral(Token token)
	{
		this(token.image,
			token.sourceStart,
			token.sourceEnd,
			token.beginLine,
			token.endLine,
			token.beginColumn,
			token.endColumn,
			null);
	}

	public StringLiteral(String source,
			     int sourceStart,
			     int sourceEnd,
			     int beginLine,
			     int endLine,
			     int beginColumn,
			     int endColumn)
	{
		this(source, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn, null);
	}

	public StringLiteral(String source,
			     int sourceStart,
			     int sourceEnd,
			     int beginLine,
			     int endLine,
			     int beginColumn,
			     int endColumn,
			     Expression[] expressions)
	{
		super(Type.STRING, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.source = source;
		this.expressions = expressions;
	}

	/**
	 * Return the expression as String.
	 *
	 * @return the expression
	 */
	@Override
	public String toStringExpression()
	{
		return source;
	}

	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		if (expressions != null)
		{
			for (Expression expression : expressions)
			{
				expression.getUsedVariable(list);
			}
		}
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		if (expressions != null)
		{
			for (Expression expression : expressions)
			{
				expression.analyzeCode(parser);
			}
		}
	}

}
