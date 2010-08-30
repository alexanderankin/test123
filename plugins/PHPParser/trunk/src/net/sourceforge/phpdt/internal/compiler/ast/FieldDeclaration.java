/*
* FieldDeclaration.java
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

import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.project.itemfinder.PHPItem;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;
import org.gjt.sp.jedit.GUIUtilities;
import sidekick.IAsset;

import javax.swing.*;
import javax.swing.text.Position;
import java.util.List;

/**
 * A Field declaration. This is a variable declaration for a php class In fact it's an array of VariableUsage, since a
 * field could contains several var : var $toto,$tata;
 *
 * @author Matthieu Casanova
 */
public class FieldDeclaration extends Statement implements Outlineable, PHPItem, IAsset
{
	private List<Modifier> modifiers;
	/**
	 * The path of the file containing this field.
	 */
	private String path;
	private final String namespace;
	/**
	 * The variables.
	 */
	private final VariableDeclaration variable;

	private String nameLowerCase;
	/**
	 * The parent do not need to be serialized.
	 */
	private final transient OutlineableWithChildren parent;

	private static transient Icon icon;

	private transient Position start;
	private transient Position end;

	private transient String cachedToString;

	private static final long serialVersionUID = 1573325853305553911L;


	/**
	 * Create a field. with public visibility
	 *
	 * @param path	the path
	 * @param variable    the variable of the field
	 * @param parent      the parent class
	 * @param sourceStart the sourceStart
	 * @param sourceEnd   source end
	 * @param beginLine   begin line
	 * @param endLine     end line
	 * @param beginColumn begin column
	 * @param endColumn   end column
	 */
	public FieldDeclaration(String namespace,
				String path,
				VariableDeclaration variable,
				OutlineableWithChildren parent,
				int sourceStart,
				int sourceEnd,
				int beginLine,
				int endLine,
				int beginColumn,
				int endColumn)
	{
		this(namespace,
			null,
			path,
			variable,
			parent,
			sourceStart,
			sourceEnd,
			beginLine,
			endLine,
			beginColumn,
			endColumn);
	}

	/**
	 * Create a field.
	 *
	 * @param modifiers   a list of {@link Modifier}
	 * @param path	the path
	 * @param variable    the variable of the field
	 * @param parent      the parent class
	 * @param sourceStart the sourceStart
	 * @param sourceEnd   source end
	 * @param beginLine   begin line
	 * @param endLine     end line
	 * @param beginColumn begin column
	 * @param endColumn   end column
	 */
	public FieldDeclaration(String namespace,
				List<Modifier> modifiers,
				String path,
				VariableDeclaration variable,
				OutlineableWithChildren parent,
				int sourceStart,
				int sourceEnd,
				int beginLine,
				int endLine,
				int beginColumn,
				int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.namespace = namespace;
		this.modifiers = modifiers;
		this.path = path;
		this.variable = variable;
		this.parent = parent;
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
		// todo : rewrite tostring method
		StringBuilder buff = new StringBuilder(tabString(tab));
		buff.append("var ");
		buff.append(variable.toStringExpression());
		return buff.toString();
	}


	public String toString()
	{
		return getName();
	}

	public OutlineableWithChildren getParent()
	{
		return parent;
	}

	/**
	 * Get the variables from outside (parameters, globals ...)
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getOutsideVariable(List list)
	{
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getModifiedVariable(List list)
	{
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List list)
	{
	}

	public String getName()
	{
		if (cachedToString == null)
		{
			cachedToString = variable.getName();
		}
		return cachedToString;
	}

	public String getNameLowerCase()
	{
		if (nameLowerCase == null)
		{
			nameLowerCase = variable.getName().toLowerCase();
		}
		return nameLowerCase;
	}


	public int getItemType()
	{
		return FIELD;
	}

	public String getPath()
	{
		return path;
	}

	public Icon getIcon()
	{
		if (icon == null)
			icon = GUIUtilities.loadIcon(ClassHeader.class.getResource("/gatchan/phpparser/icons/field.png").toString());
		return icon;
	}

	public VariableDeclaration getVariable()
	{
		return variable;
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
	public Expression expressionAt(int line, int column)
	{
		if (variable.isAt(line, column)) return variable.expressionAt(line, column);
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		// variable.analyzeCode(parser); no need VariableDeclaration is not used
	}

	public String getNamespace()
	{
		return namespace;
	}
}
