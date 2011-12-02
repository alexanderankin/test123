// $Id$
/*
 * TemplatesMenuProvider.java - An implementation of jEdit's DynamicMenuProvider
 * which creates menu items dynamically at runtime, for use with the 
 * Templates plugin.
 * Copyright (C) 2003 Steve Jakob
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
package templates;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.menu.DynamicMenuProvider;
import org.gjt.sp.util.Log;
/**
 * An implementation of jEdit's DynamicMenuProvider to supply menu items at
 * runtime for the Templates plugin.
 */
public class TemplatesMenuProvider implements DynamicMenuProvider
{
	//Constructors
	public TemplatesMenuProvider() {
		super();
	}
	
	/**
	 * Re-create the Templates menu hierarchy.
	 */
	public void update(JMenu templatesMenu) {
		Log.log(Log.DEBUG,this,"... TemplatesMenu.update()");
		templatesMenu.removeAll();
		// Create menu items for the "Refresh" option and a separator
		JMenuItem mi;
		mi = GUIUtilities.loadMenuItem("templates-tree");
		templatesMenu.add(mi);
		templatesMenu.addSeparator();
		mi = GUIUtilities.loadMenuItem("Templates.expand-accelerator");
		templatesMenu.add(mi);
		templatesMenu.addSeparator();
		mi = GUIUtilities.loadMenuItem("Templates.refresh-templates");
		templatesMenu.add(mi);
		mi = GUIUtilities.loadMenuItem("Templates.edit-template");
		templatesMenu.add(mi);
		mi = GUIUtilities.loadMenuItem("Templates.save-template");
		templatesMenu.add(mi);
		templatesMenu.addSeparator();
		// Add the templates menu items
		TemplateDir templateDir = TemplatesPlugin.getTemplates();
		templateDir.createMenus(templatesMenu, "");		
	}
	
	/**
	 * The Templates menu will not be updated every time it is shown.
	 * @return Always returns <code>false</code>.
	 */
	public boolean updateEveryTime()
	{
		return false;
	}
	
}

