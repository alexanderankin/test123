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

import java.awt.event.*;
import java.io.*;
import gnu.regexp.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
/** 
 * A TemplateFile is similar to a java.io.File object as it acts as a reference
 * to a template file, but it also contains information describing the template 
 * (eg. a label to be used on the Templates menu).
 */
public class TemplateFile implements ActionListener
{
	protected String label;
	protected File templateFile;
	private static RE ctpragmaFilter = null;
	private static RE ctpragmaTest = null;

	//Constructors
	public TemplateFile(File templateFile) {
		super();
		this.templateFile = templateFile;
		this.label = templateFile.getName();
		if (!this.isDirectory()) {
			String s = null;
			try {
				s = readTemplateLabel(templateFile);
			} catch (IOException e) {
				Log.log(Log.ERROR,this,jEdit.getProperty("plugin.TemplatesPlugin.error.template-label")
							+ templateFile.getName());
				// System.out.println("Error creating template label for file: " + f.getName());
			}
			if (s != null)
				label = s;
		}
		createREs();
	}

	//Accessors & Mutators
	public String getLabel() { return label; }
	public void setLabel(String labelVal) { label = labelVal; }

	//Implementors
	public boolean isDirectory() { return false; }
	
	public void actionPerformed(ActionEvent evt) {
		EditAction.getView(evt).getTextArea().setSelectedText(getTemplateText(this.templateFile));
	}
	
	private static String getTemplateText(File f) {
		StringBuffer templateText = new StringBuffer();
		try {
			BufferedReader in = null;
			in = new BufferedReader(new FileReader(f));
			String line;
			while ((line = in.readLine()) != null)
				if (!containsCtpragma(line))
					templateText.append(line + "\n");
		} catch (FileNotFoundException fe) { }
		catch (IOException ie) { }
		return templateText.toString();
	}
	
	private static String readTemplateLabel(File f) throws IOException{
		String templateLabel = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(f));
			String line;
			if ((line = in.readLine()) != null) {
				REMatch labelMatch = ctpragmaFilter.getMatch(line);
				if (labelMatch != null) {
					templateLabel = line.substring(labelMatch.getSubStartIndex(4));
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
			String exp = "(\\s*#ctpragma\\s*)(LABEL|NAME)(\\s*=\\s*)(\\S+.*)";
			ctpragmaFilter = new RE(exp,RE.REG_ICASE);
			exp = "\\s*#ctpragma.*";
			ctpragmaTest = new RE(exp,RE.REG_ICASE);
		} catch (gnu.regexp.REException e) { }		// this shouldn't happen
	}
	
	private static boolean containsCtpragma(String testString) {
		return ctpragmaTest.isMatch(testString);
	}
	
}
	/*
	 * Change Log:
	 * $Log$
	 * Revision 1.1  2000/04/21 05:05:45  sjakob
	 * Initial revision
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


