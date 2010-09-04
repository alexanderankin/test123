/*
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

import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;

import java.util.List;

import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.parser.PHPParser;
import sidekick.IAsset;

import javax.swing.text.Position;
import javax.swing.*;

import org.gjt.sp.jedit.GUIUtilities;

/**
 * A variable declaration.
 *
 * @author Matthieu Casanova
 */
public class VariableDeclaration extends Expression implements Outlineable, IAsset
{
	private final AbstractVariable variable;

	/**
	 * The value for variable initialization.
	 */
	private Expression initialization;

	private final transient Outlineable parent;
	private boolean reference;


	private String operator;

	private transient Position start;
	private transient Position end;
	private Icon icon;

	private String cachedToString;
	private static final long serialVersionUID = 4707939646109273633L;


	/**
	 * Create a variable.
	 *
	 * @param variable       the name of the variable
	 * @param initialization the initialization (it could be null when you have a parse error)
	 * @param operator       the assign operator
	 * @param sourceStart    the start point
	 * @param sourceEnd      the end point
	 */
	public VariableDeclaration(Outlineable parent,
				   AbstractVariable variable,
				   Expression initialization,
				   String operator,
				   int sourceStart,
				   int sourceEnd,
				   int beginLine,
				   int endLine,
				   int beginColumn,
				   int endColumn)
	{
		super(initialization == null ? Type.UNKNOWN : initialization.getType(),
			sourceStart,
			sourceEnd,
			beginLine,
			endLine,
			beginColumn,
			endColumn);
		this.initialization = initialization;
		this.variable = variable;
		variable.setType(type);
		this.operator = operator;
		this.parent = parent;
	}

	/**
	 * Create a variable.
	 *
	 * @param variable    a variable (in case of $$variablename)
	 * @param sourceStart the start point
	 */
	public VariableDeclaration(Outlineable parent,
				   AbstractVariable variable,
				   int sourceStart,
				   int sourceEnd,
				   int beginLine,
				   int endLine,
				   int beginColumn,
				   int endColumn)
	{
		super(Type.NULL, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.variable = variable;
		this.parent = parent;
	}

	public void setReference(boolean reference, int sourceStart, int beginLine, int beginColumn)
	{
		this.reference = reference;
		this.sourceStart = sourceStart;
		this.beginLine = beginLine;
		this.beginColumn = beginColumn;
	}

	/**
	 * Return the variable into String.
	 *
	 * @return a String
	 */
	@Override
	public String toStringExpression()
	{
		if (cachedToString == null)
		{
			String variableString = variable.toStringExpression();
			if (initialization == null)
			{
				if (reference) return '&' + variableString;
				else return variableString;
			}
			else
			{
				//  final String operatorString = operatorToString();
				String initString = initialization.toStringExpression();
				StringBuilder buff = new StringBuilder(variableString.length() +
					operator.length() +
					initString.length() +
					1);
				buff.append(variableString);
				buff.append(operator);
				buff.append(initString);
				cachedToString = buff.toString();
			}
		}
		return cachedToString;
	}

	public Outlineable getParent()
	{
		return parent;
	}

	public String toString()
	{
		return toStringExpression();
	}


	/**
	 * Get the variables from outside (parameters, globals ...)
	 */
	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
	}

	/**
	 * get the modified variables.
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
		variable.getUsedVariable(list);
		if (initialization != null)
		{
			initialization.getModifiedVariable(list);
		}
	}

	/**
	 * Get the variables used.
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		if (initialization != null)
		{
			initialization.getUsedVariable(list);
		}
	}

	public String getName()
	{
		return variable.getName();
	}

	public Expression getInitialization()
	{
		return initialization;
	}

	public int getItemType()
	{
		return PHPItem.VARIABLE;
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
		if (icon == null)
		{
			icon = GUIUtilities.loadIcon(ClassHeader.class.getResource("/gatchan/phpparser/icons/field.png").toString());
		}
		return icon;
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
		if (variable.isAt(line, column)) return variable;
		if (initialization != null && initialization.isAt(line, column)) return initialization;
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
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
