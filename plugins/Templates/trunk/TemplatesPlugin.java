// $Id$
/*
 * TemplatesPlugin.java - Plugin for importing code templates
 * Copyright (C) 1999 Steve Jakob
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

import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;

public class TemplatesPlugin extends EditPlugin
{
	private static TemplatesAction myAction = null;
	
	/**
	 * Initializes the TemplatesAction and registers it with jEdit.
	 */
	public void start()
	{
		if (myAction == null) {
			myAction = new TemplatesAction();
			String templateDir = jEdit.getProperty("plugin.TemplatesPlugin.templateDir.0","");
			String sepChar = System.getProperty("file.separator");
			if (templateDir.equals("")) {
				templateDir = jEdit.getSettingsDirectory() + sepChar +
								"templates" + sepChar;
				jEdit.setProperty("plugin.TemplatesPlugin.templateDir.0",templateDir);
			}
			// myAction.setTemplateDir(templateDir);
			myAction.refreshTemplates();
			jEdit.addAction(myAction);
		}
	}

	/**
	 * Not used.
	 */
	public void stop()
	{
	}

	/**
	 * Create the "Templates" menu item.
	 * @param view The current view
	 * @param menus Used to add submenus
	 * @param menuItems Used to add menu items
	 */
	public void createMenuItems(View view, Vector menus, Vector menuItems) {
		// The TemplatesAction object is responsible for maintaining
		// the Code Templates menu.
		menus.addElement(myAction.getMenu(view));
	}
	
	/**
	 * Create the plugins option pane
	 * @param optionsDialog The dialog in which the OptionPane is to be displayed.
	 */
	public void createOptionPanes(OptionsDialog optionsDialog) {
		optionsDialog.addOptionPane(new TemplatesOptionPane(myAction));
	}

}
	/*
	 * Change Log:
	 * $Log$
	 * Revision 1.1  2000/04/21 05:05:51  sjakob
	 * Initial revision
	 *
	 * Revision 1.6  2000/03/08 06:55:47  sjakob
	 * Use org.gjt.sp.util.Log instead of System.out.println.
	 * Update documentation.
	 * Add sample template files to project.
	 *
	 * Revision 1.5  2000/03/03 06:25:43  sjakob
	 * Redesigned the plugin to fix a bug where only the most recent view had a
	 * Templates menu. Added TemplateFile and TemplateDir classes to handle
	 * files and directories in the Templates directory tree. Templates menus for
	 * all views are refreshed simultaneously.
	 *
	 * Revision 1.4  2000/01/10 21:16:55  sjakob
	 * Made changes suggested by Mike Dillon (Plugin Central maintainer):
	 * - changed comments style in Templates.props
	 * - Templates submenu now added to menus vector, not menuItems
	 *
	 * Revision 1.3  1999/12/21 05:00:52  sjakob
	 * Added options pane for "Plugin options" to allow user to select template directory.
	 * Recursively scan templates directory and subdirectories.
	 * Add subdirectories to "Templates" menu as submenus.
	 * Added online documentation, as well as README.txt and CHANGES.txt.
	 *
	 * Revision 1.2  1999/12/12 06:38:37  sjakob
	 * Modified TemplatesPlugin.java to fix strange Windows ClassCastException.
	 * Cleanup of Javadoc comments in prep for posting source.
	 * Updated web page.
	 *
	 * Revision 1.1  1999/12/12 05:21:04  sjakob
	 * Renamed files CodeTemplates*.* to Templates*.*
	 * New files are Templates.props, TemplatesPlugin.java, TemplatesAction.java
	 *
	 * Revision 1.4  1999/12/10 21:39:56  sjakob
	 * Removed hard-coded string for templates directory.
	 * Using jEdit.getSettingDirectory() instead.
	 * Check for existence of templates directory and, if not present, create it.
	 * Filter out jEdit backup files when scanning templates directory.
	 *
	 * Revision 1.3  1999/12/09 18:52:33  sjakob
	 * Changed menu label "Code Templates" to "Templates".
	 * Now use dedicated templates directory ($HOME/.jedit/templates).
	 *
	 * Revision 1.2  1999/12/09 06:45:48  sjakob
	 * Changed menu labels from hard-coded strings to properties.
	 * Implemented basic template import facility.
	 *
	 * Revision 1.1.1.1  1999/12/09 05:22:21  sjakob
	 * Basic code template plugin framework.
	 * Implemented dynamic menus.
	 *
	 */

