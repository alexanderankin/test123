/*
 * Namespace.java - The namespace declaration
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009 Matthieu Casanova
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

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class Namespace extends Statement
{
	private final Expression name;

	public Namespace(Expression name, int sourceStart, int sourceEnd, int beginLine, int endLine, int beginColumn, int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.name = name;
	}

	public Expression expressionAt(int line, int column)
	{
		return name.isAt(line, column) ? name : null;
	}

	@Override
	public String toString()
	{
		return "namespace " + name + ';';
	}

	public String toString(int tab)
	{
		return tabString(tab) + toString();
	}

	public void getOutsideVariable(List list)
	{
		if (name != null)
			name.getOutsideVariable(list);
	}

	public void getModifiedVariable(List list)
	{
		if (name != null)
			name.getModifiedVariable(list);
	}

	public void getUsedVariable(List list)
	{
		if (name != null)
			name.getUsedVariable(list);
	}

	public void analyzeCode(PHPParser parser)
	{
		if (name != null)
			name.analyzeCode(parser);
	}
}
