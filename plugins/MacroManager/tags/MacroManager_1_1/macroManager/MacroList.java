/*
 * MacroList.java - Macro list
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
import java.net.*;
import java.util.*;
import java.util.zip.*;
import org.gjt.sp.util.*;
import org.gjt.sp.jedit.*;

import javax.swing.*;

/**
 * Macro list downloaded from server.
 */
class MacroList implements Comparator
{
	Vector macros;
	Vector macroSets;

	public static final int SORT_BY_NAME = 0;
	public static final int SORT_BY_DATE = 1;

	private boolean sortByName;
	public static String timestamp;

	MacroList() throws Exception
	{
		this(true);
	}

	MacroList(boolean refresh) throws Exception
	{
		init(refresh);
	}
	
	public void init(boolean refresh) throws Exception
	{
		macros = new Vector();
		macroSets = new Vector();

		File cache = new File(MiscUtilities.constructPath(
			jEdit.getSettingsDirectory(), "macros", ".macroManagerCache"));
		if(!cache.exists() || refresh)
		{
			StringBuffer data = getListFromServer();
			StringBuffer tag = new StringBuffer();
			tag.append("<timestamp>").append(new Date()).append("</timestamp>");
			data.insert(data.indexOf("<macros>") + 8, tag);
			FileWriter fw = new FileWriter(cache);
			fw.write(data.toString());
			fw.close();
		}
		
		parseList(cache);
	}
	
	private void parseList(File file) throws Exception
	{
		MacroListHandler handler = new MacroListHandler(this, file.getAbsolutePath());
		XmlParser parser = new XmlParser();
		parser.setHandler(handler);

		parser.parse(null, null, new BufferedInputStream(new FileInputStream(file)), null);
	}

	private StringBuffer getListFromServer() throws Exception
	{
		String path = jEdit.getProperty("macro-manager.url");
		StringBuffer sb = new StringBuffer();
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(new URL(path).openStream())));
			String line = null;
			while((line = in.readLine()) != null)
			{
				sb.append(line).append("\n");
			}
		}
		catch(Exception e)
		{
			// stream isn't zipped yet
			BufferedReader in = new BufferedReader(new InputStreamReader(
				new URL(path).openStream()));
			String line = null;
			while((line = in.readLine()) != null)
			{
				sb.append(line).append("\n");
			}
		}
		return sb;
	}

	void addMacro(MacroList.Macro macro)
	{
		macros.addElement(macro);
	}

	void addMacroSet(MacroSet set)
	{
		macroSets.addElement(set);
	}

	public void sortMacroList(int constraint)
	{
		sortByName = (constraint == SORT_BY_NAME);
		Collections.sort(macros, this);
	}

	public Vector searchMacroList(String text)
	{
		text = text.toLowerCase();
		Vector results = new Vector();
		StringTokenizer st = new StringTokenizer(text);
		String[] terms = new String[st.countTokens()];
		int i = 0;
		while(st.hasMoreTokens())
		{
			terms[i++] = st.nextToken();
		}
		for(i = 0; i < macros.size(); i++)
		{
			MacroList.Macro mac = (MacroList.Macro)macros.elementAt(i);
			boolean match = true;
			for(int j = 0; j < terms.length; j++)
			{
				if(mac.name.toLowerCase().indexOf(terms[j]) == -1 
					&& mac.description.toLowerCase().indexOf(terms[j]) == -1)
				{
					match = false;
				}
			}
			if(match)
			{
				results.add(mac);
			}
		}
		return results;
	}

	public int compare(Object o1, Object o2)
	{
		if(sortByName)
			return ((MacroList.Macro)o1).name.compareTo(((MacroList.Macro)o2).name);
		else
			return -((MacroList.Macro)o1).date.compareTo(((MacroList.Macro)o2).date);
	}

	void finished()
	{
	}

	void dump()
	{
		for(int i = 0; i < macros.size(); i++)
		{
			System.err.println((MacroList.Macro)macros.elementAt(i));
			System.err.println();
		}
	}

	static class MacroSet
	{
		String name;
		String description;
		Vector macros = new Vector();

/*		void install(Roster roster, String installDirectory,
			boolean downloadSource)
		{
			String settings = jEdit.getSettingsDirectory();
			String userMacroPath = null;
			if(settings != null)
			{
				userMacroPath = MiscUtilities.constructPath(settings, "macros");
			}

			for(int i = 0; i < macros.size(); i++)
			{
				// save to disk?
			}
		}  */

		public String toString()
		{
			return macros.toString();
		}
	}

	static class Macro
	{
		String name;
		String description;
		String author;
		int size;
		String version;
		String download;
		String date;

		void install(Roster roster, String installDirectory)
		{
			roster.addOperation(new Roster.Install(download, installDirectory));
		}

		public String toString()
		{
			return name;
		}
	}
}
