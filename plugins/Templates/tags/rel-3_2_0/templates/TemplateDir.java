// $Id$

/*
 * TemplateDir.java - Represents a directory within the templates
 * directory hierarchy.
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
package templates;

import javax.swing.*;
import java.io.File;
import java.util.*;
import javax.swing.tree.TreeNode;
import gnu.regexp.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.EnhancedMenuItem;
import org.gjt.sp.util.Log;
/**
 * A TemplateDir is a type of TemplateFile which is a container for other 
 * TemplateFiles. In this way we can create a tree of TemplateFiles similar 
 * to a directory tree or menu hierarchy.
 * @author Steve Jakob
 */
public class TemplateDir extends TemplateFile
{
	private Vector templateFiles;
	private static RE backupFilter;
	
	//Constructors
	public TemplateDir(TemplateDir parent, File templateFile) {
		super(parent, templateFile);
	}

	//Accessors & Mutators

	//Implementors
	public boolean isDirectory() { return true; }
	
	/**
	 * Scans the templates directory and creates a Hashtable
	 * mapping template names to template files. Backup files are ignored
	 * based on the values of the backup prefix and suffix in the "Global
	 * Options" settings.
	 */
	public void refreshTemplates() {
		File f;
		this.templateFiles = new Vector();
		try {
			// Make sure we always have an up to date backup filter
			createBackupFilter();
			
			String[] files = this.templateFile.list();
			for (int i = 0; i < files.length; i++) {
				f = new File(this.templateFile, files[i]);
				if (f.isDirectory()) {
					TemplateDir submenu = new TemplateDir(this, f);
					this.templateFiles.addElement(submenu);	// Add subdirectory as a TemplateDir
					submenu.refreshTemplates();
				}
				else if (!backupFilter.isMatch(files[i])) {	// if not a backup file
					TemplateFile tf = new TemplateFile(this, f);
					this.templateFiles.addElement(tf);
				}
			}
		} catch (gnu.regexp.REException ree) {
			Log.log(Log.ERROR,this,jEdit.getProperty("plugin.TemplatesPlugin.error.bad-backup-filter"));
			// System.out.println("Templates: Bad RegExp creating backup filter.");
		}
	}

	private static void createBackupFilter() throws gnu.regexp.REException {
		String exp = jEdit.getProperty("backup.prefix") +
				"\\S+" +
				jEdit.getProperty("backup.suffix");		// RE for jEdit backups
		if (exp.equals("\\S+")) {
			exp = "";
		}
		backupFilter = new RE(exp,RE.REG_ICASE);
	}
	
	/**
	 * Add a menu item to the given menu object for each TemplateFile contained
	 * within this TemplateDir. Recursively process any TemplateDir objects
	 * contained within this TemplateDir.
	 * @param menu The menu to which the new JMenuItem objects will be added.
	 */
	public void createMenus(JMenu menu, String parent) {
		Object o;
		JMenu submenu;
		EnhancedMenuItem mi;
		TemplateAction myAction;
		TemplateDir td;
		TemplateFile tf;
		Enumeration e;
		if (!parent.endsWith(File.separator)) {
			if (!"".equals(parent)) {	// check for root of templates tree
				parent = parent + File.separator;
			}
		}
		if (templateFiles == null) this.refreshTemplates();
		ActionSet myActions = new ActionSet();
		e = this.templateFiles.elements();
		while (e.hasMoreElements()) {
			o = e.nextElement();
			if (o instanceof TemplateDir) {
				td = (TemplateDir) o;
				submenu = new JMenu(td.getLabel());
				menu.add(submenu);	// Add subdirectory as a sub-menu
				td.createMenus(submenu, parent + td.getLabel());
			}
			else {
				tf = (TemplateFile) o;
				String actionName = TemplateAction.getUniqueActionName();
				myAction = new TemplateAction(actionName, 
						tf.getLabel(), tf.getRelativePath());
				myActions.addAction(myAction);
				mi = new EnhancedMenuItem(tf.getLabel(), actionName, 
						jEdit.getActionContext());
				menu.add(mi);
			}
		}
		jEdit.addActionSet(myActions);
	}

	public Enumeration children() {
		return templateFiles.elements();
	}
	
	public boolean getAllowsChildren() {
		return true;
	}
	
	public TreeNode getChildAt(int index) {
		return (TreeNode)templateFiles.elementAt(index);
	}
	
	public int getChildCount() {
		return templateFiles.size();
	}
	
	public int getIndex(TreeNode child) {
		return templateFiles.indexOf(child);
	}
	
	public boolean isLeaf() {
		return false;
	}
	
	/**
	 * Generates a string representation of the template hierarchy. This 
	 * method is intended as a debugging tool.
	 * @return A String representation of the template hierarchy, with each 
	 * directory and filename on a separate line.
	 */
	public String printDir() {
		String newLine = System.getProperty("line.separator");
		StringBuffer retStr = new StringBuffer("Dir: " + this.getLabel() + newLine);
		Enumeration kids = this.children();
		while (kids.hasMoreElements()) {
			TemplateFile f = (TemplateFile) kids.nextElement();
			if (f.isDirectory()) {
				retStr.append(((TemplateDir)f).printDir());
			} else {
				retStr.append(f.getRelativePath() + newLine);
			}
		}
		return retStr.toString();
	}
	
}
	/*
	 * Change Log:
	 * $Log$
	 * Revision 1.6  2003/05/23 17:07:23  sjakob
	 * Update Templates plugin for API changes in jEdit 4.2pre1.
	 *
	 * Revision 1.5  2002/08/16 15:15:12  sjakob
	 * Added debugging method printDir() which returns a String representation of the
	 * template hierarchy.
	 *
	 * Revision 1.4  2002/08/13 14:47:31  sjakob
	 * BUG FIX: If backup filename prefix and suffix were both blank, the regular expression used to
	 * filter backup files would filter all files.
	 *
	 * Revision 1.3  2002/05/07 04:08:33  sjakob
	 * BUG FIX: Fixed problem where template menu items stopped working
	 * when we started using relative, rather than absolute, file paths.
	 *
	 * Revision 1.2  2002/05/07 03:28:10  sjakob
	 * Added support for template labelling via "#template=" command.
	 *
	 * Revision 1.1  2002/04/30 19:26:10  sjakob
	 * Integrated Calvin Yu's Velocity plugin into Templates to support dynamic templates.
	 *
	 * Revision 1.3  2002/02/22 02:34:36  sjakob
	 * Updated Templates for jEdit 4.0 actions API changes.
	 * Selection of template menu items can now be recorded in macros.
	 *
	 * Revision 1.2  2001/02/23 19:31:39  sjakob
	 * Added "Edit Template" function to Templates menu.
	 * Some Javadoc cleanup.
	 *
	 * Revision 1.1.1.1  2000/04/21 05:05:44  sjakob
	 * Initial import of rel-1.0.0
	 *
	 * Revision 1.3  2000/03/08 15:46:49  sjakob
	 * Updated README, CHANGES, to-do files.
	 * Use properties for error messages, rather than hard-coded strings.
	 *
	 * Revision 1.2  2000/03/08 06:55:46  sjakob
	 * Use org.gjt.sp.util.Log instead of System.out.println.
	 * Update documentation.
	 * Add sample template files to project.
	 *
	 * Revision 1.1  2000/03/03 06:25:43  sjakob
	 * Redesigned the plugin to fix a bug where only the most recent view had a
	 * Templates menu. Added TemplateFile and TemplateDir classes to handle
	 * files and directories in the Templates directory tree. Templates menus for
	 * all views are refreshed simultaneously.
	 *
	 */


