/*
 * InterfaceDeclaration.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003-2010 Matthieu Casanova
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

import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;

import java.util.List;
import java.util.ArrayList;

import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.parser.PHPParser;

import javax.swing.*;
import javax.swing.text.Position;

import org.gjt.sp.jedit.GUIUtilities;
import sidekick.IAsset;

/**
 * An interface declaration.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class InterfaceDeclaration extends Statement implements OutlineableWithChildren, PHPItem, IAsset
{
	private final String path;
	private final transient OutlineableWithChildren parent;
	private final String name;
	private final String namespace;

	private final List<Outlineable> children = new ArrayList<Outlineable>();

	/**
	 * The constants of the class (for php5).
	 */
	private final List<ClassConstant> constants = new ArrayList<ClassConstant>();

	private static transient Icon icon;
	private String nameLowerCase;

	private transient Position start;
	private transient Position end;

	/**
	 * The list of the super interfaces names. This list could be null
	 */
	private final List<String> superInterfaces;

	/**
	 * The methodsHeaders of the class.
	 */
	private final List<MethodHeader> methodsHeaders = new ArrayList<MethodHeader>();

	private static final long serialVersionUID = -6768547707320365598L;

	public InterfaceDeclaration(String namespace,
				    String path,
				    OutlineableWithChildren parent,
				    String name,
				    List<String> superInterfaces,
				    int sourceStart,
				    int beginLine,
				    int beginColumn)
	{
		this.namespace = namespace;
		this.path = path;
		this.parent = parent;
		this.name = name;
		this.superInterfaces = superInterfaces;
		this.sourceStart = sourceStart;
		this.beginLine = beginLine;
		this.beginColumn = beginColumn;
	}

	public String getNamespace()
	{
		return namespace;
	}

	@Override
	public String toString(int tab)
	{
		return null;
	}

	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("interface ");
		buf.append(name);
		if (superInterfaces != null)
		{
			buf.append(" extends ");
			for (int i = 0; i < superInterfaces.size(); i++)
			{
				if (i != 0)
					buf.append(", ");
				buf.append(superInterfaces.get(i));
			}
		}
		return buf.toString();
	}

	@Override
	public void getOutsideVariable(List list)
	{
	}

	@Override
	public void getModifiedVariable(List list)
	{
	}

	@Override
	public void getUsedVariable(List list)
	{
	}

	public boolean add(Outlineable o)
	{
		return children.add(o);
	}

	/**
	 * Add a constant to the class.
	 *
	 * @param constant the constant
	 */
	public void addConstant(ClassConstant constant)
	{
		constants.add(constant);
	}

	/**
	 * Add a method to the interface.
	 *
	 * @param method the method declaration
	 */
	public void addMethod(MethodDeclaration method)
	{
		methodsHeaders.add(method.getMethodHeader());
		add(method);
	}

	public Outlineable get(int index)
	{
		return children.get(index);
	}

	public int size()
	{
		return children.size();
	}

	public OutlineableWithChildren getParent()
	{
		return parent;
	}

	public String getName()
	{
		return name;
	}

	public String getNameLowerCase()
	{
		if (nameLowerCase == null)
		{
			nameLowerCase = name.toLowerCase();
		}
		return nameLowerCase;
	}

	public int getItemType()
	{
		return INTERFACE;
	}

	public String getPath()
	{
		return path;
	}

	public Icon getIcon()
	{
		// todo an interface icon
		if (icon == null)
		{
			icon = GUIUtilities.loadIcon(ClassHeader.class.getResource("/gatchan/phpparser/icons/class.png").toString());
		}
		return icon;
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
		return name;
	}

	public String getLongString()
	{
		return name;
	}

	public void setName(String name)
	{
	}

	@Override
	public Expression expressionAt(int line, int column)
	{
		//todo : fix interface declaration
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		// todo : analyze the interface
	}

	public List<MethodHeader> getMethodsHeaders()
	{
		return methodsHeaders;
	}

	public List<String> getSuperInterfaces()
	{
		return superInterfaces;
	}
}
