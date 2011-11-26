/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
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

package gatchan.phpparser.methodlist;

import java.util.List;

import net.sourceforge.phpdt.internal.compiler.ast.Type;

/**
 * @author Matthieu Casanova
 */
public class Function
{
	private final Type returnType;

	private final String name;

	private final Argument[] arguments;
	private boolean varargs;

	public Function(Type returnType, String name, Argument[] arguments, boolean varargs)
	{
		this.returnType = returnType;
		this.name = name;
		this.arguments = arguments;
		this.varargs = varargs;
	}

	public Type getReturnType()
	{
		return returnType;
	}

	public String getName()
	{
		return name;
	}

	public Argument getArgument(int pos)
	{
		if (pos >= arguments.length)
			return null;
		return arguments[pos];
	}

	public int getArgumentCount()
	{
		return arguments.length;
	}

	public boolean isVarargs()
	{
		return varargs;
	}

	public Argument getVarargs()
	{
		if (varargs)
			return arguments[arguments.length-1];
		return null;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(returnType).append(' ');
		builder.append(name).append('(');
		for (int i = 0; i < arguments.length; i++)
		{
			Argument argument = arguments[i];
			if (i != 0)
				builder.append(", ");
			builder.append(argument);
		}
		if (varargs)
			builder.append("...");
		builder.append(')');
		return builder.toString();
	}
}
