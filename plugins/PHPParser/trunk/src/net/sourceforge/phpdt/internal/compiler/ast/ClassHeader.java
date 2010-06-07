/*
* ClassHeader.java
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

import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.parser.PHPParser;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import org.gjt.sp.jedit.GUIUtilities;

/**
 * The ClassHeader is that : class ClassName [extends SuperClassName].
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class ClassHeader extends AstNode implements PHPItem, Serializable
{
	/**
	 * The path of the file containing this class.
	 */
	private String path;

	private final String namespace;

	/**
	 * The name of the class.
	 */
	private String className;

	private String nameLowerCase;

	/**
	 * The name of the superclass.
	 */
	private String superClassName;

	/**
	 * The implemented interfaces. It could be null.
	 */
	private List interfaceNames;

	/**
	 * The methodsHeaders of the class.
	 */
	private final List methodsHeaders = new ArrayList();

	/**
	 * The constants of the class (for php5).
	 */
	private final List constants = new ArrayList();

	private List modifiers = new ArrayList(3);
	/**
	 * The fields of the class.
	 * It contains {@link FieldDeclaration}
	 */
	private final List fields = new ArrayList();

	private static transient Icon icon;

	private transient String cachedToString;
	private static final long serialVersionUID = 8213003151739601011L;

	public ClassHeader(String namespace,
			   String path,
			   String className,
			   String superClassName,
			   List interfaceNames,
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
		this.className = className;
		this.superClassName = superClassName;
		this.interfaceNames = interfaceNames;
	}

	public String getNamespace()
	{
		return namespace;
	}

	@Override
	public String toString(int tab)
	{
		StringBuilder buff = new StringBuilder(200);
		buff.append(tabString(tab));
		buff.append("class ");
		buff.append(className);
		if (superClassName != null)
		{
			buff.append(" extends ");
			buff.append(superClassName);
		}
		if (interfaceNames != null)
		{
			buff.append(" implements ");
			for (int i = 0; i < interfaceNames.size(); i++)
			{
				if (i != 0)
					buff.append(", ");
				buff.append(interfaceNames.get(i));
			}
		}
		return buff.toString();
	}

	public String toString()
	{
		if (cachedToString == null)
		{
			StringBuilder buff = new StringBuilder(200);
			buff.append(className);
			if (superClassName != null)
			{
				buff.append(':');
				buff.append(superClassName);
			}
			cachedToString = buff.toString();
		}
		return cachedToString;
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

	public void addModifier(Modifier modifier)
	{
		modifiers.add(modifier);
	}

	/**
	 * Returns the name of the class.
	 *
	 * @return the name of the class
	 */
	public String getName()
	{
		return className;
	}

	public String getNameLowerCase()
	{
		if (nameLowerCase == null)
		{
			nameLowerCase = className.toLowerCase();
		}
		return nameLowerCase;
	}

	/**
	 * Returns the name of the superclass.
	 *
	 * @return the name of the superclass
	 */
	public String getSuperClassName()
	{
		return superClassName;
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof ClassHeader)) return false;
		return ((ClassHeader) obj).getName().equals(className);
	}

	public String getPath()
	{
		return path;
	}

	public Icon getIcon()
	{
		if (icon == null)
		{
			icon = GUIUtilities.loadIcon(ClassHeader.class.getResource("/gatchan/phpparser/icons/class.png").toString());
		}
		return icon;
	}

	/**
	 * Add a method to the class.
	 *
	 * @param method the method declaration
	 */
	public void addMethod(MethodHeader method)
	{
		methodsHeaders.add(method);
	}

	/**
	 * Add a method to the class.
	 *
	 * @param field the method declaration
	 */
	public void addField(FieldDeclaration field)
	{
		fields.add(field);
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

	public List getMethodsHeaders()
	{
		return methodsHeaders;
	}

	/**
	 * Returns the list of the field of this class.
	 * It contains {@link FieldDeclaration}
	 *
	 * @return the list of fields of the class
	 */
	public List getFields()
	{
		return fields;
	}

	public int getItemType()
	{
		return CLASS;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
	}

	public List getInterfaceNames()
	{
		return interfaceNames;
	}
}
