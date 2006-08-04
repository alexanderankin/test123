/*
 * GlobalOptionGroup.java -
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1998, 2003 Slava Pestov
 * Portions copyright (C) 1999 mike dillon
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

package optional;

import org.gjt.sp.jedit.OptionGroup;

/**
 * A model for all of the Global Options.
 * 
 */

public class GlobalOptionGroup extends OptionGroup
{
	OptionGroup root;
	public GlobalOptionGroup()
	{
		this(null);
	}

	public GlobalOptionGroup(OptionGroup rootGroup)
	{
		super("Global Options");
		root = rootGroup;
		OptionGroup jedit = new OptionGroup("jedit");
		
		jedit.addOptionPane("general");
		jedit.addOptionPane("textarea");
		jedit.addOptionPane("abbrevs");
		jedit.addOptionPane("appearance");
		jedit.addOptionPane("context");
		jedit.addOptionPane("docking");
		jedit.addOptionPane("editing");
		jedit.addOptionPane("encodings");
		jedit.addOptionPane("gutter");
		jedit.addOptionPane("mouse");
		jedit.addOptionPane("plugin-manager");
		jedit.addOptionPane("print");
		jedit.addOptionPane("firewall");
		jedit.addOptionPane("save-back");
		jedit.addOptionPane("shortcuts");
		jedit.addOptionPane("status");
		jedit.addOptionPane("syntax");
		jedit.addOptionPane("toolbar");
		jedit.addOptionPane("view");
		addGroup(jedit);
		OptionGroup browserGroup = new OptionGroup("browser");
		browserGroup.addOptionPane("browser.general");
		browserGroup.addOptionPane("browser.colors");
		addGroup(browserGroup);
	}
	
	void addGroup(OptionGroup group) {
		if (root != null)
		{
			root.addOptionGroup(group);
		}
		else
		{
			addOptionGroup(group);
		}
	}
}
