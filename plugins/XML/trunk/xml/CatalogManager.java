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
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.io.*;
import java.net.*;
import java.util.*;
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

		if(publicId != null && publicId.length() == 0)
			publicId = null;

		if(systemId != null && systemId.length() == 0)
			systemId = null;

		String newSystemId = null;

		if(publicId == null && systemId != null && current != null)
		{
			current = MiscUtilities.getParentOfPath(current);
			if(systemId.startsWith(current))
			{
				// first, try resolving a relative name,
				// to handle jEdit built-in DTDs
				newSystemId = resolveSystem(systemId.substring(
					current.length()));
			}
		}

		// next, try resolving full path name
		if(newSystemId == null)
		{
			if(publicId == null)
				newSystemId = resolveSystem(systemId);
			else
				newSystemId = resolvePublic(publicId);
		}

		// well, the catalog can't help us, so just assume the
		// system id points to a file
		if(newSystemId == null)
		{
			if(systemId == null)
				return null;
			else if(MiscUtilities.isURL(systemId))
				newSystemId = systemId;
			else if(systemId.startsWith("/"))
				newSystemId = "file://" + systemId;
			else if(current != null && !MiscUtilities.isURL(current))
				newSystemId = current + systemId;
		}

		if(!(newSystemId.startsWith("file:")
			|| newSystemId.startsWith("jeditresource:")))
		{
			final String _newSystemId = newSystemId;
			final String[] inputSystemId = new String[1];
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						inputSystemId[0] = showUnknownDTDDialog(
							jEdit.getActiveView(),_newSystemId);
					}
				});
			}
			catch(Exception e)
			{
				Log.log(Log.ERROR,CatalogManager.class,e);
			}

			newSystemId = inputSystemId[0];
		}

		if(newSystemId == null)
			return null;

		// Xerces has a bug where an InputSource without a byte
		// stream is loaded incorrectly.
		InputSource source = new InputSource(newSystemId);
		source.setByteStream(new URL(newSystemId).openStream());
		return source;
	} //}}}

	//{{{ propertiesChanged() method
	public static void propertiesChanged()
	{
		loaded = false;
	} //}}}

	//{{{ save() method
	public static void save()
	{
		if(!loaded)
			return;

		int systemCount = 0;
		int publicCount = 0;

		Iterator keys = userCatalog.keySet().iterator();
		while(keys.hasNext())
		{
			Entry entry = (Entry)keys.next();
			if(entry.type == Entry.PUBLIC)
			{
				jEdit.setProperty("xml.user.public-id." + publicCount,entry.id);
				jEdit.setProperty("xml.user.public-id." + publicCount
					+ ".uri",(String)userCatalog.get(entry));
				publicCount++;
			}
			else
			{
				jEdit.setProperty("xml.user.system-id." + systemCount,entry.id);
				jEdit.setProperty("xml.user.system-id." + systemCount
					+ ".uri",(String)userCatalog.get(entry));
				systemCount++;
			}
		}

		jEdit.unsetProperty("xml.user.public-id." + publicCount);
		jEdit.unsetProperty("xml.user.public-id." + publicCount + ".uri");
		jEdit.unsetProperty("xml.user.system-id." + systemCount);
		jEdit.unsetProperty("xml.user.system-id." + systemCount + ".uri");
	} //}}}

	//{{{ Private members

	//{{{ Static variables
	private static boolean loaded;
	private static HashMap defaultCatalog;
	private static HashMap userCatalog;
	//}}}

	//{{{ resolvePublic() method
	private static String resolvePublic(String id)
	{
		Entry e = new Entry(Entry.PUBLIC,id);
		String uri = (String)userCatalog.get(e);
		if(uri == null)
			uri = (String)defaultCatalog.get(e);
		return uri;
	} //}}}

	//{{{ resolveSystem() method
	private static String resolveSystem(String id)
	{
		Entry e = new Entry(Entry.SYSTEM,id);
		String uri = (String)userCatalog.get(e);
		if(uri == null)
			uri = (String)defaultCatalog.get(e);
		return uri;
	} //}}}

	//{{{ showUnknownDTDDialog() method
	private static String showUnknownDTDDialog(Component comp, String systemId)
	{
		return null;
	} //}}}

	//{{{ load() method
	private synchronized static void load()
	{
		if(loaded)
			return;

		defaultCatalog = loadCatalogFromProperties("default");
		userCatalog = loadCatalogFromProperties("user");

		loaded = true;
	} //}}}

	//{{{ loadCatalogFromProperties() method
	private static HashMap loadCatalogFromProperties(String prefix)
	{
		HashMap returnValue = new HashMap();

		int i;
		String id, prop, uri;

		i = 0;
		while((id = jEdit.getProperty(prop = "xml." + prefix
			+ ".public-id." + i++)) != null)
		{
			try
			{
				returnValue.put(new Entry(Entry.PUBLIC,id),
					jEdit.getProperty(prop + ".uri"));
			}
			catch(Exception ex2)
			{
				Log.log(Log.ERROR,CatalogManager.class,ex2);
			}
		}

		i = 0;
		while((id = jEdit.getProperty(prop = "xml." + prefix
			+ ".system-id." + i++)) != null)
		{
			try
			{
				returnValue.put(new Entry(Entry.SYSTEM,id),
					jEdit.getProperty(prop + ".uri"));
			}
			catch(Exception ex2)
			{
				Log.log(Log.ERROR,CatalogManager.class,ex2);
			}
		}

		return returnValue;
	} //}}}

	//}}}

	//{{{ Entry class
	static class Entry
	{
		static final int SYSTEM = 0;
		static final int PUBLIC = 1;

		int type;
		String id;

		Entry(int type, String id)
		{
			this.type = type;
			this.id = id;
		}

		public boolean equals(Object o)
		{
			if(o instanceof Entry)
			{
				Entry e = (Entry)o;
				return e.type == type && e.id.equals(id);
			}
			else
				return false;
		}

		public int hashCode()
		{
			return id.hashCode();
		}
	} //}}}
}
