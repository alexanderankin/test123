// $Id$
/*
 * TemplatesMenu.java - A Swing JMenu containing menu items for use with the 
 * Templates plugin.
 * Copyright (C) 2001 Steve Jakob
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

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.gjt.sp.jedit.*;
/**
 * A Swing JMenu containing menu items for use with the Templates plugin.
 */
public class TemplatesMenu extends JMenu implements EBComponent
{
	//Constructors
	public TemplatesMenu() {
		// Create the top-level menu item
		super(jEdit.getProperty("TemplatesPlugin.menu.label"));
		// Create sub-menus
		update();
	}
	
	/**
	 * Register this TemplateMenu as a receiver for EditBus messages, and 
	 * alert the parent JMenu that it now has a parent component.
	 */
	public void addNotify() {
		EditBus.addToBus(this);
		super.addNotify();
	}

	/**
	 * Remove this TemplateMenu as a receiver for EditBus messages, and 
	 * alert the parent JMenu that it no longer has a parent component.
	 */
	public void removeNotify() {
		EditBus.removeFromBus(this);
		super.removeNotify();
	}
	
	/**
	 * Re-create the TemplateMenu hierarchy.
	 */
	public void update() {
		removeAll();
		// Create menu items for the "Refresh" option and a separator
		JMenuItem mi = new JMenuItem(jEdit.getProperty("TemplatesPlugin.menu.refresh.label"));
		mi.addActionListener((TemplatesAction)jEdit.getAction("TemplatesAction"));
		this.add(mi);
		mi = new JMenuItem(jEdit.getProperty("TemplatesPlugin.menu.edit.label"));
		mi.addActionListener((TemplatesAction)jEdit.getAction("TemplatesAction"));
		this.add(mi);
		mi = new JMenuItem(jEdit.getProperty("TemplatesPlugin.menu.save.label"));
		mi.addActionListener((TemplatesAction)jEdit.getAction("TemplatesAction"));
		this.add(mi);
		this.addSeparator();
		TemplatesAction.getTemplates().createMenus(this);		
	}
	
	/**
	 * Handle messages received by the jEdit EditBus.
	 * At this time, TemplatesMenu objects will respond only to 
	 * TemplatesChanged messages.
	 * @param msg An EBMessage object sent by the jEdit EditBus, to which 
	 * the TemplatesMenu object may wish to respond.
	 */
	public void handleMessage(EBMessage msg) {
		if (msg instanceof TemplatesChanged)
			this.update();
	}

}
	/*
	 * Change Log:
	 * $Log$
	 * Revision 1.1  2001/07/16 19:10:13  sjakob
	 * BUG FIX: updated TemplatesPlugin to use createMenuItems(Vector menuItems),
	 * rather than the deprecated createMenuItems(View view, Vector menus,
	 * Vector menuItems), which caused startup errors.
	 * Added Mike Dillon's makefile.jmk.
	 *
	 */

