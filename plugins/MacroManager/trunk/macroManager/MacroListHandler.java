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

import com.microstar.xml.*;
import java.io.*;
import java.util.Stack;
import org.gjt.sp.util.Log;

class MacroListHandler extends HandlerBase
{
	MacroListHandler(MacroList macroList, String path)
	{
		this.macroList = macroList;
		this.path = path;
		stateStack = new Stack();
	}

	public Object resolveEntity(String publicId, String systemId)
	{
		if("macros.dtd".equals(systemId))
		{
			// this will result in a slight speed up, since we
			// don't need to read the DTD anyway, as AElfred is
			// non-validating
			return new StringReader("<!-- -->");
		}

		return null;
	}

	public void attribute(String aname, String value, boolean isSpecified)
	{
		aname = (aname == null) ? null : aname.intern();
		value = (value == null) ? null : value.intern();

		if(aname == "name")
			name = value;
		else if(aname == "version")
			version = value;
		else if(aname == "date")
			date = value;
		else if(aname == "size")
			size = Integer.parseInt(value);
		else if(aname == "hits")
			hits = value;
	}

	public void doctypeDecl(String name, String publicId,
		String systemId) throws Exception
	{
		if("macros".equals(name))
			return;

		Log.log(Log.ERROR,this,path + ": DOCTYPE must be macros");
	}

	public void charData(char[] c, int off, int len)
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

	public void startElement(String tag)
	{
		tag = pushElement(tag);

		if(tag == "macro_set")
		{
			description = null;
			macroSet = new MacroList.MacroSet();
		}
		else if(tag == "macro")
		{
			description = null;
			author = null;
			macro = new MacroList.Macro();
		}
		else if(tag == "download")
			downloadSize = size;
	}

	public void endElement(String tag)
	{
		if(tag == null)
			return;
		else
			tag = tag.intern();

		popElement();

		if(tag == "macro_set")
		{
			macroList.addMacroSet(macroSet);
			macroSet = null;
			macroSetEntry = null;
		}
		else if(tag == "macro_set_entry")
		{
			macroSet.macros.addElement(macroSetEntry);
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

	public void startDocument()
	{
		try
		{
			pushElement(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void endDocument()
	{
		macroList.finished();
	}
	// end HandlerBase implementation

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

	private boolean obsolete;
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
