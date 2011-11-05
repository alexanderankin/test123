/*
 * MacroListHandler.java - XML handler for the macro list
 * Copyright (C) 2002 Carmine Lucarelli
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

package macroManager;


import java.io.*;
import java.util.Stack;
import org.gjt.sp.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class MacroListHandler extends DefaultHandler
{
	MacroListHandler(MacroList macroList, String path)
	{
		this.macroList = macroList;
		this.path = path;
		stateStack = new Stack();
	}

	
	public InputSource resolveEntity (String publicId, String systemId)
		throws IOException, SAXException
	    {
		
		if("macros.dtd".equals(systemId))
		{
			// this will result in a slight speed up, since we
			// don't need to read the DTD anyway, as AElfred is
			// non-validating
			return new InputSource(new StringReader("<!-- -->"));
		}

		return null;
	}

	public void startElement (String uri, String tag,
	      String qName, Attributes attributes) throws SAXException 
    {	
		tag = pushElement(tag);

		if(tag == "macro_set")
		{
			description = null;
			macroSet = new MacroList.MacroSet();
			macroList.addMacroSet(macroSet);
		}
		else if(tag == "macro")
		{
			description = null;
			author = null;
			macro = new MacroList.Macro();
		}
		else if(tag == "download")
			downloadSize = size;
		
		if (attributes.getValue("name") != null)
			name = attributes.getValue("name");
		if (attributes.getValue("version") != null)
			version = attributes.getValue("version");
		if (attributes.getValue("date") != null)
			date = attributes.getValue("date");
		
		if (attributes.getValue("size") != null)
			size = Integer.parseInt(attributes.getValue("size"));
		if (attributes.getValue("hits") != null)
			hits = attributes.getValue("hits");
			
    }		
		

	/** Not used */
	public void doctypeDecl(String name, String publicId,
		String systemId) throws Exception
	{
		if("macros".equals(name))
			return;

		Log.log(Log.ERROR,this,path + ": DOCTYPE must be macros");
	} 

    public void characters (char c[], int off, int len)
	throws SAXException	
	{
		String tag = peekElement();
		String text = new String(c, off, len);

		if(tag == "description")
		{
			description = text;
		}
		else if(tag == "macro_set_entry")
			macroSetEntry = text;
		else if(tag == "author")
		{
			if(author != null && author.length() != 0)
				author = author + ", " + text;
			else
				author = text;
		}
		else if(tag == "download")
			download = text;
		else if(tag == "timestamp")
			MacroList.timestamp = text;
	}

    public void endElement (String uri, String tag, String qName)
    	throws SAXException
        {
		if(tag == null)
			tag = qName;

		popElement();

		if(tag == "macro_set")
		{
			macroSet.name = name;
			macroSet.description = description;
			macroSet = null;
			macroSetEntry = null;
		}
		else if(tag == "macro_set_entry")
		{
			macroSetEntry = searchAndReplace(macroSetEntry, "_", " ");
			macroSetEntry = searchAndReplace(macroSetEntry, ".bsh", "");
			macroList.addMacroSetEntry(macroSetEntry);
			//macroSet.macros.addElement(macroSetEntry);
			macroSetEntry = null;
		}
		else if(tag == "macro")
		{
			name = searchAndReplace(name, "_", " ");
			name = searchAndReplace(name, ".bsh", "");
			macro.name = name;
			macro.author = author;
			macro.description = description;
			macro.size = size;
			macro.hits = hits;
			macro.version = version;
			macro.download = download;
			macro.date = date;
			macroList.addMacro(macro);
			name = null;
			author = null;
			version = null;
		}
	}

    public void startDocument () throws SAXException {    	
    	pushElement(null);
	}

    public void endDocument () throws SAXException {
		macroList.finished();
	}

	/**
	  * Utility method to replace all occurences of 'old' with 'repl' in 'field'.
	  *
	  * @param field  The String to do replacements on
	  * @param old    The String to be replaced
	  * @param repl   The String to replace with
	  * @return String  New String
	  */
	public static String searchAndReplace(String field, String old, String repl)
	{
		// anything to do?
		int where;
		if((where = field.indexOf(old)) == -1)
		{
			return field;
		}
		while(where != -1)
		{
			String work = field.substring(0, where) + repl + field.substring(where + old.length());
			field = work;
			// don't search from the beginning just incase repl contains old!!
			where = field.indexOf(old, where + repl.length());
		}
		return field;
	}

	// private members
	private String path;

	private MacroList macroList;

	private MacroList.MacroSet macroSet;
	private String macroSetEntry;

	private MacroList.Macro macro;
	private String author;

	private String version;
	private String date;
	private String download;
	private String hits;
	private int downloadSize;
	private int size;

	private String name;
	private String description;

	private Stack stateStack;

	private String pushElement(String name)
	{
		name = (name == null) ? null : name.intern();

		stateStack.push(name);

		return name;
	}

	private String peekElement()
	{
		return (String) stateStack.peek();
	}

	private String popElement()
	{
		return (String) stateStack.pop();
	}
}
