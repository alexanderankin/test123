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

import javax.swing.*;
import java.io.File;
import java.util.*;
import gnu.regexp.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
/**
 * A TemplateDir is a type of TemplateFile which is a container for other 
 * TemplateFiles. In this way we can create a tree of TemplateFiles similar 
 * to a directory tree or menu hierarchy.
 * @author Steve Jakob
 */
public class TemplateDir extends TemplateFile
{
	private Hashtable templateFiles;
	private static RE backupFilter;
	
	//Constructors
	public TemplateDir(File templateFile) {
		super(templateFile);
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
		this.templateFiles = new Hashtable();
		try {
			if (backupFilter == null)
				createBackupFilter();
			String[] files = this.templateFile.list();
			for (int i = 0; i < files.length; i++) {
				f = new File(this.templateFile, files[i]);
				if (f.isDirectory()) {
					TemplateDir submenu = new TemplateDir(f);
					this.templateFiles.put(files[i], submenu);	// Add subdirectory as a TemplateDir
					submenu.refreshTemplates();
				}
				else if (!backupFilter.isMatch(files[i])) {	// if not a backup file
					TemplateFile tf = new TemplateFile(f);
					this.templateFiles.put(files[i], tf);
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
		backupFilter = new RE(exp,RE.REG_ICASE);
	}
	
	public void createMenus(JMenu menu) {
		Object o;
		JMenu submenu;
		JMenuItem mi;
		TemplateDir td;
		TemplateFile tf;
		Enumeration e;
		e = this.templateFiles.elements();
		while (e.hasMoreElements()) {
			o = e.nextElement();
			if (o instanceof TemplateDir) {
				td = (TemplateDir) o;
				submenu = new JMenu(td.getLabel());
				menu.add(submenu);	// Add subdirectory as a sub-menu
				td.createMenus(submenu);
			}
			else {
				tf = (TemplateFile) o;
				mi = new JMenuItem(tf.getLabel());
				mi.addActionListener(tf);
				menu.add(mi);
			}
		}
	}

}
	/*
	 * Change Log:
	 * $Log$
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


