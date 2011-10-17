/*
* MethodHeader.java
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

//{{{ Imports
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.project.itemfinder.PHPItem;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import org.gjt.sp.jedit.GUIUtilities;

import javax.swing.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
//}}}

/**
 * @author Matthieu Casanova
 * @version $Id$
 */
public class MethodHeader extends Statement implements PHPItem, Serializable
{
	private final List<Modifier> modifiers;
	/**
	 * The path of the file containing this class.
	 */
	private final String path;
	private final String namespace;
	/**
	 * The name of the method.
	 */
	private final String name;

	/**
	 * Indicate if the method returns a reference.
	 */
	private final boolean reference;

	private final ArgumentList uses;
	/**
	 * The arguments.
	 */
	private final List<FormalParameter> arguments;

	private String cachedToString;

	private transient Icon icon;

	private String nameLowerCase;
	private static final long serialVersionUID = -8681675454927194940L;

	//{{{ MethodHeader constructor
	public MethodHeader(String namespace, String path,
			    List<Modifier> modifiers,
			    String name,
			    boolean reference,
			    ArgumentList uses,
			    List<FormalParameter> arguments,
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
		this.name = name;
		this.reference = reference;
		this.uses = uses;
		this.arguments = arguments;
	} //}}}

	public String getNamespace()
	{
		return namespace;
	}

	//{{{ getName() method

	public String getName()
	{
		return name;
	} //}}}

	//{{{ getNameLowerCase() method

	public String getNameLowerCase()
	{
		if (nameLowerCase == null)
		{
			nameLowerCase = name.toLowerCase();
		}
		return nameLowerCase;
	} //}}}

	//{{{ toString() methods

	public String toString()
	{
		if (cachedToString == null)
		{
			StringBuilder buff = new StringBuilder(100);
			if (reference) buff.append('&');
			buff.append(name);
			buff.append('(');
			if (arguments != null)
			{
				for (int i = 0; i < arguments.size(); i++)
				{
					FormalParameter o = arguments.get(i);
					buff.append(o.toStringExpression());
					if (i != (arguments.size() - 1))
					{
						buff.append(", ");
					}
				}
			}
			buff.append(')');
			cachedToString = buff.toString();
		}
		return cachedToString;
	}

	@Override
	public String toString(int tab)
	{
		return tabString(tab) + toString();
	} //}}}

	//{{{ getOutsideVariable() method

	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
	} //}}}

	//{{{ getModifiedVariable() method

	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
	} //}}}

	//{{{ getUsedVariable() method

	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
	} //}}}

	//{{{ getArgumentsCount() method

	public int getArgumentsCount()
	{
		return arguments.size();
	} //}}}

	//{{{ getParameters() method
	public void getParameters(Collection<VariableUsage> list)
	{
		if (arguments != null)
		{
			for (FormalParameter variable : arguments)
			{
				VariableUsage variableUsage = new VariableUsage(Type.UNKNOWN,
					variable.getName(),
					variable.getSourceStart(),
					variable.getSourceEnd(),
					variable.getBeginLine(),
					variable.getEndLine(),
					variable.getBeginColumn(),
					variable.getEndColumn());
				list.add(variableUsage);
			}
		}
	} //}}}

	//{{{ getPath() method

	public String getPath()
	{
		return path;
	} //}}}

	//{{{ getItemType() method

	public int getItemType()
	{
		return METHOD;
	} //}}}

	//{{{ getIcon() method

	public Icon getIcon()
	{
		if (icon == null)
		{
			icon = GUIUtilities.loadIcon(MethodHeader.class.getResource("/gatchan/phpparser/icons/method.png").toString());
		}
		return icon;
	} //}}}

	//{{{ subNodeAt() method

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (arguments != null)
		{
			for (FormalParameter formalParameter : arguments)
			{
				if (formalParameter.isAt(line, column))
					return formalParameter;
			}
		}
		return null;
	} //}}}

	//{{{ analyzeCode() method

	@Override
	public void analyzeCode(PHPParser parser)
	{
		checkModifiers(parser);
	} //}}}

	//{{{checkModifiers() method
	private void checkModifiers(PHPParser parser)
	{
		if (modifiers == null)
			return;

		Collection<String> modifierKinds = new HashSet<String>(5);
		for (int i = 0; i < modifiers.size(); i++)
		{
			Modifier modifier = modifiers.get(i);
			if (modifier.isVisibilityModifier())
			{
				if (!modifierKinds.add(Integer.toString(-1)))
				{
					// il y avait déjà un modifier de visibility
					parser.fireParseError("You already have a visibility modifier", modifier);
				}
			}
			else if (!modifierKinds.add(modifier.toString()))
			{
				parser.fireParseError("Duplicate modifier " + modifier.toString(), modifier);
			}
			else
				modifier.checkCompatibility(parser, modifiers);
		}
	} //}}}
}
