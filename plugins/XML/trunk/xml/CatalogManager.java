/*
 * CatalogManager.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2002 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

//{{{ Imports
import java.io.*;
import java.net.*;
import java.util.*;
import com.arbortext.catalog.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.xml.sax.*;
//}}}

public class CatalogManager
{
	//{{{ resolve() method
	public static InputSource resolve(String current,
		String publicId, String systemId)
		throws SAXException, IOException
	{
		load();

		String newSystemId = null;

		if(publicId == null && systemId != null && current != null)
		{
			current = MiscUtilities.getParentOfPath(current);
			if(systemId.startsWith(current))
			{
				// first, try resolving a relative name,
				// to handle jEdit built-in DTDs
				newSystemId = catalog.resolveSystem(
					systemId.substring(current.length()));
			}
		}

		// next, try resolving full path name
		if(newSystemId == null)
		{
			if(publicId == null)
				newSystemId = catalog.resolveSystem(systemId);
			else
				newSystemId = catalog.resolvePublic(publicId,systemId);
		}

		// well, the catalog can't help us, so just assume the
		// system id points to a file
		if(newSystemId == null)
		{
			if(systemId == null)
				return null;
			else
				newSystemId = systemId;
		}

		if(!(networkOK || newSystemId.startsWith("file:")
			|| newSystemId.startsWith("jeditresource:")))
			throw new SAXException(jEdit.getProperty("xml.network.error"));

		// Xerces has a bug where an InputSource without a byte
		// stream is loaded incorrectly.
		InputSource source = new InputSource(newSystemId);
		source.setByteStream(new URL(newSystemId).openStream());
		return source;
	} //}}}

	//{{{ addCatalog() method
	public static void addCatalog(String uri)
	{
		addedCatalogs.addElement(uri);
	} //}}}

	//{{{ propertiesChanged() method
	public static void propertiesChanged()
	{
		loaded = false;
		networkOK = jEdit.getBooleanProperty("xml.network-ok");
	} //}}}

	//{{{ Private members

	//{{{ Static variables
	private static Catalog catalog;
	private static boolean loaded;
	private static Vector addedCatalogs = new Vector();
	private static boolean networkOK;
	//}}}

	//{{{ load() method
	private synchronized static void load()
	{
		if(loaded)
			return;

		loaded = true;

		catalog = new Catalog();
		catalog.setParserClass("org.apache.xerces.parsers.SAXParser");

		int i;
		String id, prop, uri;

		try
		{
			catalog.loadSystemCatalogs();

			catalog.parseCatalog("jeditresource:XML.jar!/xml/dtds/catalog");

			i = 0;
			while((uri = jEdit.getProperty(
				prop = "xml.catalog." + i++)) != null)
			{
				try
				{
					catalog.parseCatalog(uri);
				}
				catch(Exception ex2)
				{
					Log.log(Log.ERROR,CatalogManager.class,ex2);
				}
			}

			i = 0;
			while((id = jEdit.getProperty(
				prop = "xml.public-id." + i++)) != null)
			{
				try
				{
					catalog.addEntry(new CatalogEntry(
						CatalogEntry.PUBLIC,id,
						jEdit.getProperty(prop + ".uri")));
				}
				catch(Exception ex2)
				{
					Log.log(Log.ERROR,CatalogManager.class,ex2);
				}
			}

			i = 0;
			while((id = jEdit.getProperty(
				prop = "xml.system-id." + i++)) != null)
			{
				try
				{
					catalog.addEntry(new CatalogEntry(
						CatalogEntry.SYSTEM,id,
						jEdit.getProperty(prop + ".uri")));
				}
				catch(Exception ex2)
				{
					Log.log(Log.ERROR,CatalogManager.class,ex2);
				}
			}

			Enumeration e = addedCatalogs.elements();
			while (e.hasMoreElements())
			{
				try
				{
					uri = (String)e.nextElement();
					catalog.parseCatalog(uri);
				}
				catch(Exception ex2)
				{
					Log.log(Log.ERROR,CatalogManager.class,ex2);
				}
			}
		}
		catch(Exception ex1)
		{
			Log.log(Log.ERROR,CatalogManager.class,ex1);
		}
	} //}}}

	//}}}
}
