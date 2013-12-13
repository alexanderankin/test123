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

import net.sourceforge.phpdt.internal.compiler.ast.Type;

/**
 * @author Matthieu Casanova
 */
public class Argument
{
	private final Type type;

	private boolean reference;

	private String defaultValue;

	private boolean optional;

	private final String name;

	private boolean varargs;

	public Argument(String type, String name)
	{
		this.type = Type.fromString(type);
		this.name = name;
	}

	public boolean isVarargs()
	{
		return varargs;
	}

	public void setVarargs(boolean varargs)
	{
		this.varargs = varargs;
	}

	public void setAlternateType(String type)
	{
		// todo: store type
	}

	public Type getType()
	{
		return type;
	}

	public void setReference(boolean reference)
	{
		this.reference = reference;
	}

	public boolean isReference()
	{
		return reference;
	}

	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public boolean isOptional()
	{
		return optional;
	}

	public void setOptional(boolean optional)
	{
		this.optional = optional;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		if (optional)
			builder.append("[ ");
		if (reference)
			builder.append('&');
		builder.append(type);
		builder.append(' ');
		builder.append(name);
		if (defaultValue != null)
			builder.append('=').append(defaultValue);
		if (optional)
			builder.append(" ]");
		return builder.toString();
	}
}
