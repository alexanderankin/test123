// $Id$
/*
 * TemplateVar.java - Represents a "substitution variable"
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

/**
 * This class represents a "substitution variable" which can be incorporated 
 * into a template document.
 */
public class TemplateVar
{
	private String name;
	private String prompt;
	private String helpText = "";
	private String defaultValue = "";
	private String value = "";

	//Constructors
	public TemplateVar(String name) {
		this.name = name;
		// Use the variable name for the prompt, just in case the user
		// forgets to enter one.
		this.prompt = name;
	}

	//Accessors & Mutators
	public String getName() { return name; }
	public void setName(String nameVal) { name = nameVal; }
	public String getPrompt() { return prompt; }
	public void setPrompt(String promptVal) { prompt = promptVal; }
	public String getHelpText() { return helpText; }
	public void setHelpText(String helpTextVal) { helpText = helpTextVal; }
	public String getDefaultValue() { return defaultValue; }
	public void setDefaultValue(String defaultValueVal) { defaultValue = defaultValueVal; }
	public String getValue() {
		if (value.equals("")) {
			// value = Wizard.prompt(this);
		}
		return value;
	}
	public void setValue(String valueVal) { value = valueVal; }
	
	//Implementors
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Name: " + name + "\n");
		sb.append("... Prompt: " + prompt + "\n");
		sb.append("... HelpText: " + helpText + "\n");
		sb.append("... DefaultValue: " + defaultValue + "\n");
		sb.append("... Value: " + value + "\n");
		return sb.toString();
	}
	
}
	/*
	 * Change Log:
	 * $Log$
	 * Revision 1.1  2000/05/02 03:22:51  sjakob
	 * Added TemplateVar.java to template variable info.
	 * Modified TemplateFile to parse new #ctpragma directives, and to create
	 * template variables.
	 *
	 * Revision 1.1  2000/03/20 06:45:39  sjakob
	 * Added basic functionality for parsing new #ctpragma directives
	 * related to substitution variables.
	 * Added TemplateVar class to handle substitution variables.
	 *
	 */


