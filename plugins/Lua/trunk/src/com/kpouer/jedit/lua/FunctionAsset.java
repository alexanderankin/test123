/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2013 Matthieu Casanova
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

package com.kpouer.jedit.lua;

import java.util.List;
import javax.swing.Icon;
import javax.swing.text.Position;

import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.ParList;
import sidekick.Asset;

/**
 * @author Matthieu Casanova
 */
public class FunctionAsset extends Asset
{
	private final String toString;

	public FunctionAsset(String name, Position start, Position end, ParList parlist)
	{
		super(name);
		this.start = start;
		this.end = end;

		StringBuilder builder = new StringBuilder();
		builder.append(name).append('(');

		List names = parlist.names;
		for (int i = 0; i < names.size(); i++)
		{
			if (i != 0)
				builder.append(", ");
			Name argName = (Name) names.get(i);
			builder.append(argName.name);
		}
		builder.append(')');
		toString = builder.toString();
	}

	@Override
	public Icon getIcon()
	{
		return null;
	}

	@Override
	public String getShortString()
	{
		return toString;
	}

	@Override
	public String getLongString()
	{
		return toString;
	}
}
