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
package templates;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
/**
 * A Swing JMenu containing menu items for use with the Templates plugin.
 */
public class TemplatesMenu extends JMenu implements EBComponent
{
	//Constructors
	public TemplatesMenu() {
		// Create the top-level menu item
		super(jEdit.getProperty("Templates.menu.label"));
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
		Log.log(Log.DEBUG,this,"... TemplatesMenu.update()");
		removeAll();
		// Create menu items for the "Refresh" option and a separator
		JMenuItem mi;
		mi = GUIUtilities.loadMenuItem("templates-tree");
		this.add(mi);
		this.addSeparator();
		mi = GUIUtilities.loadMenuItem("Templates.expand-accelerator");
		this.add(mi);
		this.addSeparator();
		mi = GUIUtilities.loadMenuItem("Templates.refresh-templates");
		this.add(mi);
		mi = GUIUtilities.loadMenuItem("Templates.edit-template");
		this.add(mi);
		mi = GUIUtilities.loadMenuItem("Templates.save-template");
		this.add(mi);
		this.addSeparator();
		// Add the templates menu items
		createMenus();		
	}
	
	private void createMenus() {
		TemplateDir templateDir = TemplatesPlugin.getTemplates();
		templateDir.createMenus(this, "");
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
	 * Revision 1.2  2002/05/07 03:32:21  sjakob
	 * Integrated Calvin Yu's dockable Templates Tree window into plugin.
	 *
	 * Revision 1.1  2002/04/30 19:26:10  sjakob
	 * Integrated Calvin Yu's Velocity plugin into Templates to support dynamic templates.
	 *
	 * Revision 1.2  2002/02/22 02:34:36  sjakob
	 * Updated Templates for jEdit 4.0 actions API changes.
	 * Selection of template menu items can now be recorded in macros.
	 *
	 * Revision 1.1  2001/07/16 19:10:13  sjakob
	 * BUG FIX: updated TemplatesPlugin to use createMenuItems(Vector menuItems),
	 * rather than the deprecated createMenuItems(View view, Vector menus,
	 * Vector menuItems), which caused startup errors.
	 * Added Mike Dillon's makefile.jmk.
	 *
	 */

