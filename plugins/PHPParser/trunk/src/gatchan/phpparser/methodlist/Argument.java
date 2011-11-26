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

	private final boolean reference;

	private final String defaultValue;

	public Argument(Type type, boolean reference, String defaultValue)
	{
		this.type = type;
		this.reference = reference;
		this.defaultValue = defaultValue;
	}

	public Type getType()
	{
		return type;
	}

	public boolean isReference()
	{
		return reference;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		if (reference)
			builder.append('&');
		builder.append(type);
		if (defaultValue != null)
			builder.append('=').append(defaultValue);
		return builder.toString();
	}
}
