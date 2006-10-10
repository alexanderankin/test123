// $Id$

/*
 * TemplateFile.java - Represents a file within the templates 
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

import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.tree.TreeNode;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import gnu.regexp.*;
/** 
 * A TemplateFile is similar to a java.io.File object as it acts as a reference
 * to a template file, but it also contains information describing the template 
 * (eg. a label to be used on the Templates menu).
 */
public class TemplateFile implements TreeNode, Comparable
{
	private static final String labelRE =
			"(\\s*##\\s*)(TEMPLATE)(\\s*=\\s*)(\\S+.*)";
	protected String label;
	protected File templateFile;
	private static RE ctpragmaLabelFilter = null;
	private TemplateDir parent;

	//Constructors
	public TemplateFile(TemplateDir parent, File templateFile) {
		super();
		this.parent = parent;
		this.templateFile = templateFile;
		this.label = templateFile.getName();
		createREs();
		if (!this.isDirectory()) {
			String s = null;
			try {
				s = readTemplateLabel(this.getBufferedReader());
			} catch (Exception e) {
				Log.log(Log.ERROR,this,jEdit.getProperty("plugin.TemplatesPlugin.error.template-label")
							+ templateFile.getName());
				Log.log(Log.ERROR,this,e);
			}
			if (s != null)
				label = s;
		}
	}

	//Accessors & Mutators
	public String getLabel() { return label; }
	public void setLabel(String labelVal) { label = labelVal; }
	public String getPath() { return templateFile.getPath(); }
   /**
    * Determine the relative path of the file from the templates directory, 
	* given the file's absolute path.
    */
	public String getRelativePath() {
		String absolutePath = templateFile.getPath();
		if (absolutePath.startsWith(TemplatesPlugin.getTemplateDir())) {
			return absolutePath.substring(TemplatesPlugin.getTemplateDir().length());
		}
		return absolutePath;
	}


	//Implementors
	public boolean isDirectory() { return false; }

	
	/**
	 * Convenience method to create a BufferedReader to the template file.
	 * @return A BufferedReader object corresponding to the underlying file.
	 */
	public BufferedReader getBufferedReader() throws FileNotFoundException {
		return new BufferedReader(new FileReader(this.templateFile));
	}
	
	private static String readTemplateLabel(BufferedReader in) throws IOException{
		String templateLabel = null;
		try {
			String line;
			if ((line = in.readLine()) != null) {
				REMatch labelMatch = ctpragmaLabelFilter.getMatch(line);
				if (labelMatch != null) {
					templateLabel = labelMatch.toString(4);
				}
			}
		} catch (IOException e) {
			throw e;
		}			// In case of problems, throw the exception to the caller
		finally {	// but close the file, also.
			try {
				in.close();
			} catch (IOException ioe) { }
		}
		return templateLabel;
	}

	/**
	* Creates a RE to parse #ctpragma directives. Each directive is composed of 4 parts:<P>
	* <LI> #ctpragma
	* <LI> the directive type (eg. LABEL, NAME, etc.)
	* <LI> an equals ("=") sign
	* <LI> the value to assign for this directive type
	*/
	private static void createREs() {
		try {
			ctpragmaLabelFilter = new RE(labelRE,RE.REG_ICASE);
		} catch (gnu.regexp.REException e) { }		// this shouldn't happen
	}
	
	public int compareTo(Object o)
	{
		TemplateFile t2 = (TemplateFile)o;
		return this.toString().compareToIgnoreCase(t2.toString());
	}
	
	public String toString() { return label; }
	
	//
	// The next seven methods satisfy the TreeNode interface requirements.
	//
	public Enumeration children() {
		return null;
	}
	
	public boolean getAllowsChildren() {
		return false;
	}
	
	public TreeNode getChildAt(int index) {
		return null;
	}
	
	public int getChildCount() {
		return 0;
	}
	
	public int getIndex(TreeNode child) {
		return -1;
	}
	
	public TreeNode getParent() {
		return parent;
	}
	
	public boolean isLeaf() {
		return true;
	}
	
}
	/*
	 * Change Log:
	 * $Log$
	 * Revision 1.5  2006/10/10 01:57:11  sjakob
	 * Sort the list of templates (case-insensitive).
	 *
	 * Revision 1.4  2002/08/09 18:53:56  sjakob
	 * BUG FIX: removed String.replaceFirst( ) method to retain JDK1.3 compatibility.
	 *
	 * Revision 1.3  2002/07/29 14:14:58  sjakob
	 * Changed template label-matching regular expression for new Velocity format:
	 * ## template = <label>
	 *
	 * Revision 1.2  2002/05/07 03:28:10  sjakob
	 * Added support for template labelling via "#template=" command.
	 *
	 * Revision 1.1  2002/04/30 19:26:10  sjakob
	 * Integrated Calvin Yu's Velocity plugin into Templates to support dynamic templates.
	 *
	 * Revision 1.5  2002/02/22 02:34:36  sjakob
	 * Updated Templates for jEdit 4.0 actions API changes.
	 * Selection of template menu items can now be recorded in macros.
	 *
	 * Revision 1.4  2001/02/23 19:31:39  sjakob
	 * Added "Edit Template" function to Templates menu.
	 * Some Javadoc cleanup.
	 *
	 * Revision 1.3  2000/05/08 04:45:52  sjakob
	 * Abstracted template processing to new Template class.
	 * TemplateFile will now act merely as a proxy for a Template.
	 *
	 * Revision 1.2  2000/05/02 03:22:51  sjakob
	 * Added TemplateVar.java to template variable info.
	 * Modified TemplateFile to parse new #ctpragma directives, and to create
	 * template variables.
	 *
	 * Revision 1.1.1.1  2000/04/21 05:05:45  sjakob
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


