/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2012 Matthieu Casanova
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

package com.kpouer.jedit.javascriptsidekick;

import javax.swing.Icon;
import javax.swing.text.Position;

import sidekick.Asset;

/**
 * @author Matthieu Casanova
 */
public class JSAsset extends Asset
{
	private Icon icon;

	public JSAsset(String name, Position start, Position end)
	{
		super(name);
		this.icon = icon;
		setStart(start);
		setEnd(end);
	}

	@Override
	public Icon getIcon()
	{
		return icon;
	}

	@Override
	public String getShortString()
	{
		return name;
	}

	@Override
	public String getLongString()
	{
		return name;
	}
}
