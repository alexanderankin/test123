/*
 * TryStatement.java
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
 * @author Matthieu Casanova
 */
public class TryStatement extends Statement
{
	private final Block block;

	private final List<Catch> catchs;

	public TryStatement(Block block,
			    List<Catch> catchs,
			    int sourceStart,
			    int sourceEnd,
			    int beginLine,
			    int endLine,
			    int beginColumn,
			    int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.block = block;
		this.catchs = catchs;
	}

	@Override
	public String toString(int tab)
	{
		return null; //todo implement toString of catch
	}

	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
		block.getOutsideVariable(list);
		for (Catch catched : catchs)
		{
			catched.getOutsideVariable(list);
		}
	}

	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
		block.getModifiedVariable(list);
		for (Catch catched : catchs)
		{
			catched.getModifiedVariable(list);
		}
	}

	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		block.getUsedVariable(list);
		for (Catch catched : catchs)
		{
			catched.getUsedVariable(list);
		}
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (block.isAt(line, column))
			return block.subNodeAt(line, column);
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		block.analyzeCode(parser);
		for (Catch catched : catchs)
		{
			catched.analyzeCode(parser);
		}
	}
}
