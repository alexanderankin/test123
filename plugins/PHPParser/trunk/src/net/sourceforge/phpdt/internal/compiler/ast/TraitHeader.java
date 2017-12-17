/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2017 Matthieu Casanova
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
import gatchan.phpparser.parser.Token;
import gatchan.phpparser.project.itemfinder.PHPItem;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import org.gjt.sp.jedit.GUIUtilities;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Trait header is that : trait TraitName.
 *
 * @author Matthieu Casanova
 * @version $Id: ClassHeader.java 20078 2011-10-14 17:32:32Z kpouer $
 */
public class TraitHeader extends AstNode implements PHPItem, Serializable
{
	/**
	 * The path of the file containing this trait.
	 */
	private final String path;

	private final String namespace;

	/**
	 * The name of the trait.
	 */
	private final ObjectIdentifier traitName;

	private String nameLowerCase;

	/**
	 * The methodsHeaders of the trait.
	 */
	private final List<MethodHeader> methodsHeaders = new ArrayList<>();

	private static transient Icon icon;

	private transient String cachedToString;
	private static final long serialVersionUID = 8213003151739601011L;

	public TraitHeader(String namespace, String path, Token traitToken, ObjectIdentifier traitName)
	{
		super(traitToken.sourceStart, traitName.sourceStart, traitToken.beginLine, traitName.endLine, traitToken.beginColumn, traitName.endColumn);
		this.namespace = namespace;
		this.path = path;
    this.traitName = traitName;
  }

	@Override
  public String getNamespace()
	{
		return namespace;
	}

	@Override
	public String toString(int tab)
	{
    return tabString(tab) + "trait " + traitName;
	}

	public String toString()
	{
		if (cachedToString == null)
		{
      cachedToString = String.valueOf(traitName);
		}
		return cachedToString;
	}

	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
	}

	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
	}

	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
	}

	/**
	 * Returns the name of the trait.
	 *
	 * @return the name of the trait
	 */
	@Override
  public String getName()
	{
		if (traitName == null)
			return PHPParser.SYNTAX_ERROR_CHAR;
		return traitName.toString();
	}

	@Override
  public String getNameLowerCase()
	{
		if (nameLowerCase == null)
		{
			nameLowerCase = getName().toLowerCase();
		}
		return nameLowerCase;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof TraitHeader))
			return false;
		return ((TraitHeader) obj).getName().equals(getName());
	}

  @Override
  public int hashCode()
  {
    return Objects.hash(namespace, traitName, methodsHeaders);
  }

  @Override
  public String getPath()
	{
		return path;
	}

	@Override
  public Icon getIcon()
	{
		if (icon == null)
		{
			icon = GUIUtilities.loadIcon(TraitHeader.class.getResource("/gatchan/phpparser/icons/class.png").toString());
		}
		return icon;
	}

	/**
	 * Add a method to the trait.
	 *
	 * @param method the method declaration
	 */
	public void addMethod(MethodHeader method)
	{
		methodsHeaders.add(method);
	}

	@Override
  public int getItemType()
	{
		return TRAIT;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
	  // do nothing
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (traitName != null)
		{
			if (traitName.isAt(line+1, column))
				return traitName;
		}
		return null;
	}
}
