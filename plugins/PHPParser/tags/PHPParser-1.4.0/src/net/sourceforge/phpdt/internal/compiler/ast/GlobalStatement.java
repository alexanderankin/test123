/*
* GlobalStatement.java
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

import java.util.List;

import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.PHPParseMessageEvent;
import gatchan.phpparser.project.itemfinder.PHPItem;
import sidekick.IAsset;

import javax.swing.text.Position;
import javax.swing.*;

/**
 * A GlobalStatement statement in php.
 *
 * @author Matthieu Casanova
 */
public class GlobalStatement extends Statement implements Outlineable, IAsset
{

	/**
	 * An array of the variables called by this global statement.
	 */
	private final AbstractVariable[] variables;

	private final transient Outlineable parent;

	private transient Position start;
	private transient Position end;
	private String cachedToString;

	public GlobalStatement(Outlineable parent,
			       AbstractVariable[] variables,
			       int sourceStart,
			       int sourceEnd,
			       int beginLine,
			       int endLine,
			       int beginColumn,
			       int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.variables = variables;
		this.parent = parent;
	}

	public String toString()
	{
		if (cachedToString == null)
		{
			StringBuilder buff = new StringBuilder("global ");
			for (int i = 0; i < variables.length; i++)
			{
				if (i != 0)
				{
					buff.append(", ");
				}
				buff.append(variables[i].toStringExpression());
			}
			cachedToString = buff.toString();
		}
		return cachedToString;
	}

	@Override
	public String toString(int tab)
	{
		return tabString(tab) + toString();
	}

	public Outlineable getParent()
	{
		return parent;
	}

	/**
	 * Get the variables from outside (parameters, globals ...)
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
		for (AbstractVariable variable : variables)
		{
			variable.getUsedVariable(list);
		}
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
	}

	/**
	 * We will analyse the code. if we have in globals a special variable it will be reported as a warning.
	 *
	 * @see Variable#SPECIAL_VARS
	 */
	@Override
	public void analyzeCode(PHPParser parser)
	{
		for (AbstractVariable variable : variables)
		{
			if (arrayContains(Variable.SPECIAL_VARS, variable.getName()))
			{
				parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
					PHPParseMessageEvent.MESSAGE_UNNECESSARY_GLOBAL,
					parser.getPath(),
					"warning, you shouldn't request " + variable.getName() + " as global",
					variable.sourceStart,
					variable.sourceEnd,
					variable.getBeginLine(),
					variable.getEndLine(),
					variable.getBeginColumn(),
					variable.getEndColumn()));
			}
		}
	}

	public String getName()
	{
		//todo : change this
		return null;
	}

	public int getItemType()
	{
		return PHPItem.GLOBAL;
	}

	public Position getEnd()
	{
		return end;
	}

	public void setEnd(Position end)
	{
		this.end = end;
	}

	public Position getStart()
	{
		return start;
	}

	public void setStart(Position start)
	{
		this.start = start;
	}

	public Icon getIcon()
	{
		return null;
	}

	public String getShortString()
	{
		return toString();
	}

	public String getLongString()
	{
		return toString();
	}

	public void setName(String name)
	{
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		for (AbstractVariable variable : variables)
		{
			if (variable.isAt(line, column))
				return variable;
		}
		return null;
	}

	public boolean add(Outlineable o)
	{
		return false;
	}

	public Outlineable get(int index)
	{
		return null;
	}

	public int size()
	{
		return 0;
	}
}
