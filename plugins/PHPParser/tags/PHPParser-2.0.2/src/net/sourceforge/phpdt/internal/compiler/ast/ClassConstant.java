/*
* ClassConstant.java
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

import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.parser.PHPParser;
import sidekick.IAsset;

import javax.swing.*;
import javax.swing.text.Position;
import java.util.List;

import org.gjt.sp.jedit.GUIUtilities;

/**
 * A class constant.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class ClassConstant extends Statement implements Outlineable, PHPItem, IAsset
{
	private final String path;

	private final String name;

	/**
	 * The value can be null when there are parse errors.
	 */
	private final Expression value;

	private final Outlineable parent;

	private static transient Icon icon;

	private transient Position start;
	private transient Position end;

	private final String namespace;
	private static final long serialVersionUID = 6115937167801653273L;


	/**
	 * Create a node.
	 *
	 * @param namespace the namespace
	 * @param path	the path
	 * @param parent      the parent class
	 * @param name	the name of the constant
	 * @param value       the value (it could be null in case of parse error)
	 * @param sourceStart starting offset
	 * @param sourceEnd   ending offset
	 * @param beginLine   begin line
	 * @param endLine     ending line
	 * @param beginColumn begin column
	 * @param endColumn   ending column
	 */
	public ClassConstant(String namespace, String path,
			     Outlineable parent,
			     String name,
			     Expression value,
			     int sourceStart,
			     int sourceEnd,
			     int beginLine,
			     int endLine,
			     int beginColumn,
			     int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.namespace = namespace;
		this.path = path;
		this.name = name;
		this.value = value;
		this.parent = parent;
	}

	public String getNamespace()
	{
		return namespace;
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		return null;
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
		return tabString(tab) + "const " + name + " = " + (value == null ? "?" : value.toStringExpression());
	}

	/**
	 * Get the variables from outside (parameters, globals ...)
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
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
	 * This method will analyze the code. by default it will do nothing
	 */
	@Override
	public void analyzeCode(PHPParser parser)
	{
	}

	/**
	 * Returns the parent of the item.
	 *
	 * @return the parent
	 */
	public Outlineable getParent()
	{
		return parent;
	}

	/**
	 * Give the name of the item.
	 *
	 * @return the name of the item
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the item type.
	 * in {@link PHPItem#CLASS},{@link PHPItem#FIELD}, {@link PHPItem#INTERFACE}, {@link PHPItem#METHOD}
	 *
	 * @return the item type
	 */
	public int getItemType()
	{
		return CLASS_CONSTANT;
	}

	public String getNameLowerCase()
	{
		return name.toLowerCase();
	}

	public String getPath()
	{
		return path;
	}

	public Icon getIcon()
	{
		if (icon == null)
		{
			icon = GUIUtilities.loadIcon(ClassHeader.class.getResource("/gatchan/phpparser/icons/field.png").toString());
		}
		return icon;
	}

	/**
	 * Returns a brief description of the asset to be shown in the tree.
	 */
	public String getShortString()
	{
		return toString();
	}

	/**
	 * Returns a full description of the asset to be shown in the view's
	 * status bar on when the mouse is over the asset in the tree.
	 */
	public String getLongString()
	{
		return toString();
	}

	/**
	 * Set the name of the asset
	 */
	public void setName(String name)
	{
	}

	/**
	 * Set the start position
	 */
	public void setStart(Position start)
	{
		this.start = start;
	}

	/**
	 * Returns the starting position.
	 */
	public Position getStart()
	{
		return start;
	}

	/**
	 * Set the end position
	 */
	public void setEnd(Position end)
	{
		this.end = end;
	}

	/**
	 * Returns the end position.
	 */
	public Position getEnd()
	{
		return end;
	}

	public String toString()
	{
		return getName();
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
