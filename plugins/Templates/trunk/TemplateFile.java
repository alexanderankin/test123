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
import java.util.*;
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
	private StringBuffer templateText;
	private Hashtable vars = new Hashtable();
	private TemplateVar lastVar = null;
	private static RE ctpragmaFilter = null;
	private static RE ctpragmaLabelFilter = null;
	private static RE ctpragmaTest = null;
	private org.gjt.sp.jedit.View view = null;

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
	public View getView() { return view; }

	//Implementors
	public boolean isDirectory() { return false; }
	
	public void actionPerformed(ActionEvent evt) {
		view = EditAction.getView(evt);
		view.getTextArea().setSelectedText(getTemplateText(this.templateFile));
	}
	
	private String getTemplateText(File f) {
		try {
			BufferedReader in = null;
			in = new BufferedReader(new FileReader(f));
			templateText = new StringBuffer();
			parsePragmas(in);
			processTemplateFile();
		} catch (FileNotFoundException fe) { }
		catch (IOException ie) { }
		return this.templateText.toString();
	}
	
	/**
	 * Scan the file for #ctpragma directives, and compile a list. 
	 * All lines which are not #ctpragma directives are inserted into 
	 * the templateText StringBuffer for later processing.
	 * @param in The BufferedReader from which the template is read.
	 */
	private void parsePragmas(BufferedReader in) throws IOException{
		String line;
		while ((line = in.readLine()) != null)
			if (containsCtpragma(line)) {
				REMatch pragmaMatch = ctpragmaFilter.getMatch(line);
				if (pragmaMatch != null) {
					String pragmaType = pragmaMatch.toString(2);
					String pragmaValue = pragmaMatch.toString(4);
					processPragma(pragmaType.toUpperCase(), pragmaValue);
				}
			}
			else {
				templateText.append(line + "\n");
			}
	}
	
	private void processPragma(String type, String value) {
		if (type.equals("VAR")) {
			this.lastVar = new TemplateVar(value);
			this.vars.put(value, this.lastVar);
		} else if (type.equals("VAR_PROMPT") && this.lastVar != null) {
			this.lastVar.setPrompt(value);
		} else if (type.equals("VAR_DEFAULT") && this.lastVar != null) {
			this.lastVar.setDefaultValue(value);
		} else if (type.equals("VAR_HELP") && this.lastVar != null) {
			this.lastVar.setHelpText(value);
		}
	}
	
	public TemplateVar lookupVar(String key) {
		return (TemplateVar)vars.get(key);
	}
	
	/**
	 * Process the template file, removing #ctpragma directives, and 
	 * replacing substitution variables.
	 */
	private void processTemplateFile() {
		Enumeration e = vars.elements();
		while (e.hasMoreElements()) {
			System.out.println(e.nextElement());
		}
	}
	
	private static String readTemplateLabel(File f) throws IOException{
		String templateLabel = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(f));
			String line;
			if ((line = in.readLine()) != null) {
				REMatch labelMatch = ctpragmaLabelFilter.getMatch(line);
				if (labelMatch != null) {
					// templateLabel = line.substring(labelMatch.getSubStartIndex(4));
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
			String exp = "(\\s*#ctpragma\\s*)(LABEL|NAME)(\\s*=\\s*)(\\S+.*)";
			ctpragmaLabelFilter = new RE(exp,RE.REG_ICASE);
			exp = "(\\s*#ctpragma\\s*)(VAR|VAR_PROMPT|VAR_DEFAULT|VAR_HELP)(\\s*=\\s*)(\\S+.*)";
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


