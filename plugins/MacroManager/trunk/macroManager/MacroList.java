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
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;
import java.util.zip.GZIPInputStream;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.*;

/**
 * Macro list downloaded from server.
 */
class MacroList
{
	Vector macros;
	Vector macroSets;

	MacroList() throws Exception
	{
		macros = new Vector();
		macroSets = new Vector();

		String path = jEdit.getProperty("macro-manager.url");
		MacroListHandler handler = new MacroListHandler(this,path);
		XmlParser parser = new XmlParser();
		parser.setHandler(handler);

//		parser.parse(null,null,new BufferedReader(new InputStreamReader(
//			new GZIPInputStream(new URL(path).openStream()),"UTF8")));
		parser.parse(null,null,new BufferedReader(new InputStreamReader(
			(new URL(path).openStream()),"ISO-8859-1")));
	}

	void addMacro(MacroList.Macro macro)
	{
		macros.addElement(macro);
	}

	void addMacroSet(MacroSet set)
	{
		macroSets.addElement(set);
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
