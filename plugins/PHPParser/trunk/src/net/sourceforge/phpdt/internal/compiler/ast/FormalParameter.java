/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2003-2011 Matthieu Casanova
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
import java.io.Serializable;

/**
 * @author Matthieu Casanova
 */
public class FormalParameter extends Expression implements Serializable
{
	private ObjectIdentifier typeHint;
	private String name;

	private boolean reference;

	private String defaultValue;

	public FormalParameter()
	{
	}

	public FormalParameter(ObjectIdentifier typeHint,
			       String name,
			       boolean reference,
			       String defaultValue,
			       int sourceStart,
			       int sourceEnd,
			       int beginLine,
			       int endLine,
			       int beginColumn,
			       int endColumn)
	{
		super(Type.UNKNOWN, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.typeHint = typeHint;
		this.name = name;
		this.reference = reference;
		this.defaultValue = defaultValue;
	}

	public FormalParameter(ObjectIdentifier typeHint,
			       String name,
			       boolean reference,
			       int sourceStart,
			       int sourceEnd,
			       int beginLine,
			       int endLine,
			       int beginColumn,
			       int endColumn)
	{
		this(typeHint, name, reference, null, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
	}

	@Override
	public String toStringExpression()
	{
		StringBuilder buff = new StringBuilder(200);
		if (typeHint != null)
		{
			buff.append(typeHint);
		}
		if (reference)
		{
			buff.append('&');
		}
		buff.append('$').append(name);
		if (defaultValue != null)
		{
			buff.append('=');
			buff.append(defaultValue);
		}
		return buff.toString();
	}

	public String toString()
	{
		return toStringExpression();
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

	public String getName()
	{
		return name;
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (typeHint != null && typeHint.isAt(line, column))
			return typeHint;
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
	}
}
