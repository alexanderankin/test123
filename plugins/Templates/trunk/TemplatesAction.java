// $Id$
/*
 * TemplatesAction.java - The EditAction called after selecting "Templates"
 * from the jEdit "Plugins" menu. Coordinates import of selected template
 * into the current buffer.
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

import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.util.Enumeration;
import java.util.Hashtable;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

/**
 * This class is designed for use with jEdit and will insert a code
 * template at the current position within the current document.
 */
public class TemplatesAction extends EditAction implements ActionListener
{
	private static TemplateDir templates;
	private static Hashtable templateMenus = new Hashtable();

	//Constructors
	public TemplatesAction() {
		super("TemplatesAction");
	}

	// Accessors & Mutators
	/**
	 * Returns the directory where templates are stored
	 */
	public String getTemplateDir() {
		return jEdit.getProperty("plugin.TemplatesPlugin.templateDir.0");
	}
	
	/** 
	 * Change the directory where templates are stored
	 * @param templateDirVal The new templates directory
	 */
	public void setTemplateDir(String templateDirVal) {
		jEdit.setProperty("plugin.TemplatesPlugin.templateDir.0",templateDirVal);
	}
	
	/**
	 * Returns a copy of the Templates menu
	 * @param view The view whose Templates menu we wish to retrieve.
	 * @return A JMenu object which is the Templates menu for the given view.
	 */
	public JMenu getMenu(View view) {
		JMenu menu;
		if (templateMenus.containsKey(view)) {
			menu = (JMenu)templateMenus.get(view);
		} else {
			menu = new JMenu(jEdit.getProperty("TemplatesPlugin.menu.label"));
			buildMenu(menu);
			templateMenus.put(view,menu);
		}
		return menu;
	}

	// Implementors
	/**
	 * Determines which menu item was selected from the
	 * "Templates" menu, and responds appropriately.
	 * @param evt The ActionEvent for the menu selection.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		String command = evt.getActionCommand();
		if (command.equals(jEdit.getProperty("TemplatesPlugin.menu.refresh.label"))) {
			this.refreshTemplates();
		}
		if (command.equals(jEdit.getProperty("TemplatesPlugin.menu.edit.label"))) {
			this.loadTemplateForEdit(EditAction.getView(evt));
		}
	}
	
	/**
	 * Scans the templates directory and creates a Hashtable
	 * mapping template names to template files. Backup files are ignored
	 * based on the values of the backup prefix and suffix in the "Global
	 * Options" settings.
	 */
	public void refreshTemplates() {
		String templateDirStr = getTemplateDir();
		File templateDir = new File(templateDirStr);
		try {
			if (!templateDir.exists()) {	// If the template directory doesn't exist
				templateDir.mkdir();		// then create it
				if (!templateDir.exists())	// If insufficent privileges to create it
					throw new java.lang.SecurityException();
			}
			this.templates = new TemplateDir(templateDir);
			this.templates.refreshTemplates();
			buildAllMenus();
		} catch (java.lang.SecurityException se) {
			Log.log(Log.ERROR,this,jEdit.getProperty("plugin.TemplatesPlugin.error.create-dir") + templateDir);
			// System.out.println("Templates: Unable to create directory ");
		}
	}
	
	private void buildMenu(JMenu menu) {
		// Create menu items for the "Refresh" option and a separator
		JMenuItem mi = new JMenuItem(jEdit.getProperty("TemplatesPlugin.menu.refresh.label"));
		mi.addActionListener(this);
		menu.add(mi);
		mi = new JMenuItem(jEdit.getProperty("TemplatesPlugin.menu.edit.label"));
		mi.addActionListener(this);
		menu.add(mi);
		menu.addSeparator();
		this.templates.createMenus(menu);		
	}
	
	private void buildAllMenus() {
		Enumeration e = templateMenus.elements();
		JMenu menu;
		while (e.hasMoreElements()) {
			menu = (JMenu)e.nextElement();
			menu.removeAll();
			buildMenu(menu);
		}
	}
	
	/**
	 * Prompt the user for a template file and load it into the view from
	 * which the request was initiated.
	 * @param view The view from which the "Edit Template" request was made.
	 */
	private void loadTemplateForEdit(View view) {
		JFileChooser chooser = new JFileChooser(
				jEdit.getProperty("plugin.TemplatesPlugin.templateDir.0","."));
		int retVal = chooser.showOpenDialog(view);
		if(retVal == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			if(file != null)
			{
				try
				{
					// Load file into jEdit
					jEdit.openFile(view, file.getCanonicalPath());
				}
				catch(IOException e)
				{
					// shouldn't happen
				}
			}
		}
	}

}
	/*
	 * Change Log:
	 * $Log$
	 * Revision 1.3  2001/02/23 19:31:39  sjakob
	 * Added "Edit Template" function to Templates menu.
	 * Some Javadoc cleanup.
	 *
	 * Revision 1.2  2000/12/04 19:56:11  sjakob
	 * Modified TemplatesAction to implement ActionListener as the signature of its
	 * superclass, org.gjt.sp.jedit.EditAction, had changed.
	 *
	 * Revision 1.1.1.1  2000/04/21 05:05:48  sjakob
	 * Initial import of rel-1.0.0
	 *
	 * Revision 1.8  2000/03/08 15:46:49  sjakob
	 * Updated README, CHANGES, to-do files.
	 * Use properties for error messages, rather than hard-coded strings.
	 *
	 * Revision 1.7  2000/03/08 06:55:46  sjakob
	 * Use org.gjt.sp.util.Log instead of System.out.println.
	 * Update documentation.
	 * Add sample template files to project.
	 *
	 * Revision 1.6  2000/03/03 06:25:43  sjakob
	 * Redesigned the plugin to fix a bug where only the most recent view had a
	 * Templates menu. Added TemplateFile and TemplateDir classes to handle
	 * files and directories in the Templates directory tree. Templates menus for
	 * all views are refreshed simultaneously.
	 *
	 * Revision 1.5  2000/02/15 21:34:04  sjakob
	 * Fixed problem whereby end-of-line markers were not handled
	 * correctly (as reported by Juha Lindfors)
	 *
	 * Revision 1.4  1999/12/21 05:00:52  sjakob
	 * Added options pane for "Plugin options" to allow user to select template directory.
	 * Recursively scan templates directory and subdirectories.
	 * Add subdirectories to "Templates" menu as submenus.
	 * Added online documentation, as well as README.txt and CHANGES.txt.
	 *
	 * Revision 1.3  1999/12/14 06:05:33  sjakob
	 * Subdirectories within the templates directory are now scanned recursively.
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

