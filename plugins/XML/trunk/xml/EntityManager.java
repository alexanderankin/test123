/*
 * EntityManager.java
 * Copyright (C) 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

import javax.swing.text.BadLocationException;
import java.io.*;
import java.net.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.apache.xerces.readers.XCatalog;
import org.xml.sax.*;

class EntityManager
{
	public static InputSource resolveEntity(String current,
		String publicId, String systemId)
		throws SAXException, IOException
	{
		load();

		InputSource source = null;

		if(publicId == null && systemId != null && current != null)
		{
			current = MiscUtilities.getParentOfPath(current);
			if(systemId.startsWith(current))
			{
				// first, try resolving a relative name,
				// to handle jEdit built-in DTDs
				source = catalog.resolveEntity(null,
					systemId.substring(current.length()));
			}
		}

		// next, try resolving full path name
		if(source == null)
			source = catalog.resolveEntity(publicId,systemId);

		// well, the catalog can't help us, so just assume the
		// system id points to a file
		if(source == null)
		{
			if(systemId == null)
				return null;
			else
			{
				source = new InputSource();
				source.setSystemId(systemId);
			}
		}

		return source;
	}

	public static void propertiesChanged()
	{
		loaded = false;
	}

	// private members
	private static XCatalog catalog;
	private static boolean loaded;

	private synchronized static void load()
	{
		if(loaded)
			return;

		loaded = true;

		catalog = new XCatalog();

		
		try
		{
			int i = 0;
			String uri;
			while((uri = jEdit.getProperty("xml.xcatalog." + i)) != null)
			{
				catalog.loadCatalog(resolveEntity(null,null,uri));
				i++;
			}
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,EntityManager.class,e);
		}
	}
}
