/*
 * Statement.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003-2011 Matthieu Casanova
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
 * A Statement.
 *
 * @author Matthieu Casanova
 */
public abstract class Statement extends AstNode
{
	protected Statement()
	{
	}

	/**
	 * Create a node.
	 *
	 * @param sourceStart starting offset
	 * @param sourceEnd   ending offset
	 * @param beginLine   begin line
	 * @param endLine     ending line
	 * @param beginColumn begin column
	 * @param endColumn   ending column
	 */
	protected Statement(int sourceStart,
			    int sourceEnd,
			    int beginLine,
			    int endLine,
			    int beginColumn,
			    int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
	}

	/**
	 * Tell if the block is empty.
	 *
	 * @return a statement is not empty by default
	 */
	public boolean isEmptyBlock()
	{
		return false;
	}

	public abstract AstNode subNodeAt(int line, int column);

	//public abstract void propagateType(List list);
}
