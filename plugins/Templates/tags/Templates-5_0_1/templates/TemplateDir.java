/*
 *  TemplateDir.java - Represents a directory within the templates
 *  directory hierarchy.
 *  Copyright (C) 1999 Steve Jakob
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package templates;
import java.io.File;
import java.util.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
/**
 * A TemplateDir is a type of TemplateFile which is a container for other
 * TemplateFiles. In this way we can create a tree of TemplateFiles similar to a
 * directory tree or menu hierarchy.
 *
 * @author   Steve Jakob
 */
public class TemplateDir extends TemplateFile
{
	private Vector templateFiles;
	// private static RE backupFilter;
	public static Pattern backupPattern;

	//Constructors
	public TemplateDir(TemplateDir parent, File templateFile)
	{
		super(parent, templateFile);
	}

	//Accessors & Mutators

	//Implementors
	public boolean isDirectory()
	{
		return true;
	}

	/**
	 * Scans the templates directory and creates a Hashtable mapping template names
	 * to template files. Backup files are ignored based on the values of the
	 * backup prefix and suffix in the "Global Options" settings.
	 */
	public void refreshTemplates()
	{
		File f;
		this.templateFiles = new Vector();
		try
		{
			// Make sure we always have an up to date backup filter
			createBackupFilter();

			String[] files = this.templateFile.list();
			for (int i = 0; i < files.length; i++)
			{
				f = new File(this.templateFile, files[i]);
				if (f.isDirectory())
				{
					// Add subdirectory as a TemplateDir
					TemplateDir submenu = new TemplateDir(this, f);
					this.templateFiles.addElement(submenu);
					submenu.refreshTemplates();
				}
				else
				{
					Matcher m = backupPattern.matcher(files[i]);
					if (!m.matches())
					{// if not a backup file
						TemplateFile tf = new TemplateFile(this, f);
						this.templateFiles.addElement(tf);
					}
				}
			}
		}
		catch (PatternSyntaxException pe)
		{
			Log.log(Log.ERROR, this, jEdit.getProperty(
						"plugin.TemplatesPlugin.error.bad-backup-filter"));
		}
		catch (IllegalArgumentException iae)
		{
			// This won't happen if the programmer did his job right.
			Log.log(Log.ERROR, this, "IllegalArgumentException in backup RE");
		}
		Collections.sort(this.templateFiles);
	}

	private static void createBackupFilter()
	throws PatternSyntaxException, IllegalArgumentException
	{
		String exp = jEdit.getProperty("backup.prefix") +
				"\\S+" +
				jEdit.getProperty("backup.suffix");// RE for jEdit backups
		if (exp.equals("\\S+"))
		{
			exp = "";
		}
		// backupFilter = new RE(exp, RE.REG_ICASE);
		backupPattern = Pattern.compile(exp, Pattern.CASE_INSENSITIVE);
	}

	/**
	 * Add a menu item to the given menu object for each TemplateFile contained
	 * within this TemplateDir. Recursively process any TemplateDir objects
	 * contained within this TemplateDir.
	 *
	 * @param menu    The menu to which the new JMenuItem objects will be added.
	 * @param parent  The path of the parent directory
	 */
	public void createMenus(JMenu menu, String parent)
	{
		Object o;
		JMenu submenu;
		TemplateAction myAction;
		TemplateDir td;
		TemplateFile tf;
		Enumeration e;
		if (!parent.endsWith(File.separator))
		{
			// check for root of templates tree
			if (!"".equals(parent))
			{
				parent = parent + File.separator;
			}
		}
		if (templateFiles == null)
		{
			this.refreshTemplates();
		}
		// Create ActionSet for the items in this TemplateDir
		StringBuffer sb = new StringBuffer("Templates - ");
		if ("".equals(parent))	// Top-level menu
			sb.append("(Top-level menu)");
		else
			sb.append(this.getFQLabel());
		ActionSet myActions = new ActionSet(sb.toString());
		// PROBLEM #1: Templates actions are not showing up in 
		// Utilities->Global Options->Shortcuts until after "Refresh Templates"
		// is selected.
		// PROBLEM #2: When "Refresh Templates" is selected, the old actions
		// are not removed.
		
		e = this.templateFiles.elements();
		while (e.hasMoreElements())
		{
			o = e.nextElement();
			if (o instanceof TemplateDir)
			{
				td = (TemplateDir) o;
				submenu = new JMenu(td.getLabel());
				menu.add(submenu);// Add subdirectory as a sub-menu
				td.createMenus(submenu, parent + td.getLabel());
			} else
			{
				tf = (TemplateFile) o;
				String actionName = TemplateAction.getUniqueActionName();
				myAction = new TemplateAction(actionName,
						tf.getLabel(), tf.getRelativePath());
				myActions.addAction(myAction);
				menu.add(GUIUtilities.loadMenuItem(jEdit.getActionContext(), actionName, false));
			}
		}
		jEdit.addActionSet(myActions);
	}

	public Enumeration children()
	{
		return templateFiles.elements();
	}

	public boolean getAllowsChildren()
	{
		return true;
	}

	public TreeNode getChildAt(int index)
	{
		return (TreeNode) templateFiles.elementAt(index);
	}

	public int getChildCount()
	{
		return templateFiles.size();
	}

	public int getIndex(TreeNode child)
	{
		return templateFiles.indexOf(child);
	}

	public boolean isLeaf()
	{
		return false;
	}

	/**
	 * Generates a string representation of the template hierarchy. This method is
	 * intended as a debugging tool.
	 *
	 * @return   A String representation of the template hierarchy, with each
	 *      directory and filename on a separate line.
	 */
	public String printDir()
	{
		String newLine = System.getProperty("line.separator");
		StringBuffer retStr = new StringBuffer("Dir: " + this.getLabel() + newLine);
		Enumeration kids = this.children();
		while (kids.hasMoreElements())
		{
			TemplateFile f = (TemplateFile) kids.nextElement();
			if (f.isDirectory())
			{
				retStr.append(((TemplateDir) f).printDir());
			} else
			{
				retStr.append(f.getRelativePath() + newLine);
			}
		}
		return retStr.toString();
	}

}

