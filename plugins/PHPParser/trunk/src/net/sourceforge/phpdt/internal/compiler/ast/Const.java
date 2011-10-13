/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2010 jEdit contributors
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
import gatchan.phpparser.project.itemfinder.PHPItem;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import sidekick.IAsset;

import javax.swing.*;
import javax.swing.text.Position;
import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class Const extends Statement implements Outlineable, IAsset
{
	private final Expression defineName;
	private final Expression defineValue;

	private final transient Outlineable parent;

	private transient Position start;
	private transient Position end;
	private String cachedToString;

	public Const(Outlineable parent,
		      Expression defineName,
		      Expression defineValue,
		      int sourceStart,
		      int sourceEnd,
		      int beginLine,
		      int endLine,
		      int beginColumn,
		      int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.parent = parent;
		this.defineName = defineName;
		this.defineValue = defineValue;
	}

	@Override
	public String toString(int tab)
	{
		String nameString = defineName.toStringExpression();
		String valueString = defineValue.toStringExpression();
		StringBuilder buff = new StringBuilder(tab + 10 + nameString.length() + valueString.length());
		buff.append(tabString(tab));
		buff.append("const ");
		buff.append(nameString);
		buff.append(" = ");
		buff.append(valueString);
		return buff.toString();
	}

	public String toString()
	{
		if (cachedToString == null)
		{
			String nameString = defineName.toStringExpression();
			String valueString = defineValue.toStringExpression();
			cachedToString = nameString + " = " + valueString;
		}
		return cachedToString;
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
		list.add(new VariableUsage(Type.UNKNOWN,
			defineName.toStringExpression(),
			sourceStart,
			sourceEnd,
			beginLine,
			endLine,
			beginColumn,
			endColumn));//todo: someday : evaluate the defineName
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

	public String getName()
	{
		//todo : change this
		return defineName.toString();
	}

	public int getItemType()
	{
		return PHPItem.DEFINE;
	}

	public Position getStart()
	{
		return start;
	}

	public void setStart(Position start)
	{
		this.start = start;
	}

	public Position getEnd()
	{
		return end;
	}

	public void setEnd(Position end)
	{
		this.end = end;
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
		if (defineName.isAt(line, column)) return defineName;
		if (defineValue.isAt(line, column)) return defineValue;
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		// todo analyze define
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