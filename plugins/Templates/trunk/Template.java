// $Id$
/*
 * Template.java - 
 * Copyright (C) 2000 Steve Jakob
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
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import gnu.regexp.*;
/**
 * This class is the main Template model class. It takes a TemplateFile, parses its 
 * contents, and finally processes it.
 */
public class Template
{
	private String name;
	private File templateFile;
	private StringBuffer templateText;
	private Hashtable vars = new Hashtable();
	private TemplateVar lastVar = null;
	private static RE ctpragmaFilter = null;
	private static RE ctpragmaTest = null;

	//Constructors
	public Template(File file) {
		if (!file.isDirectory()) {		// should never happen, but ...
			this.templateFile = file;
			createREs();
		}
	}

	//Accessors & Mutators
	
	//Implementors
	/**
	 * Process the template file, and insert the template text into the 
	 * current view.
	 * @param view The current jEdit view.
	 */
	public void processTemplate(org.gjt.sp.jedit.View view) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(templateFile));
			parsePragmas(in);
			// *** This section is for testing purposes only ***
			/*
			Enumeration e = vars.elements();
			while (e.hasMoreElements()) {
				System.out.println(e.nextElement());
			}
			*/
			// *** End of test section ***
			processTemplateText(view);
		} catch (IOException e) {
			Log.log(Log.ERROR, this, "Error reading template file: " + templateFile.getPath());
		} finally {
			try {
				in.close();		// make sure the file gets closed
			} catch (IOException ee) { }
		}
	}

	/**
	 * Scan the file for #ctpragma directives, and compile a list. 
	 * All lines which are not #ctpragma directives are inserted into 
	 * the templateText StringBuffer for later processing.
	 * Close the reader when finished.
	 * @param in The BufferedReader from which the template is read.
	 */
	private void parsePragmas(BufferedReader in) throws IOException{
		String line;
		templateText = new StringBuffer();
		while ((line = in.readLine()) != null) {
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
	
	private void processTemplateText(org.gjt.sp.jedit.View view) {
		view.getTextArea().setSelectedText(templateText.toString());

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
			String exp = "(\\s*#ctpragma\\s*)(VAR|VAR_PROMPT|VAR_DEFAULT|VAR_HELP)(\\s*=\\s*)(\\S+.*)";
			ctpragmaFilter = new RE(exp,RE.REG_ICASE);
			exp = "\\s*#ctpragma.*";
			ctpragmaTest = new RE(exp,RE.REG_ICASE);
		} catch (gnu.regexp.REException e) { }		// this shouldn't happen
	}
	
	private static boolean containsCtpragma(String testString) {
		return ctpragmaTest.isMatch(testString);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		return sb.toString();
	}
	
}
	/*
	 * Change Log:
	 * $Log$
	 * Revision 1.2  2002/02/22 02:34:36  sjakob
	 * Updated Templates for jEdit 4.0 actions API changes.
	 * Selection of template menu items can now be recorded in macros.
	 *
	 * Revision 1.1  2000/05/08 04:45:52  sjakob
	 * Abstracted template processing to new Template class.
	 * TemplateFile will now act merely as a proxy for a Template.
	 *
	 */

