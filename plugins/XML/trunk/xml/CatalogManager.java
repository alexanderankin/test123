/*
 * CatalogManager.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2002 Slava Pestov
 * Portions copyright (C) 2002 Chris Stevenson
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
import javax.swing.*;
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
				newSystemId = systemId.substring(
					current.length());
				if(newSystemId.startsWith("/"))
					newSystemId = newSystemId.substring(1);
				newSystemId = resolveSystem(newSystemId);
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
			// use a final array to pass a mutable value from the
			// invokeAndWait() call
			final boolean[] ok = new boolean[1];
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						if(showDownloadDTDDialog(
							jEdit.getActiveView(),
							_newSystemId))
						{
							ok[0] = true;
						}
					}
				});
			}
			catch(Exception e)
			{
				Log.log(Log.ERROR,CatalogManager.class,e);
			}

			if(!ok[0])
				throw new IOException(jEdit.getProperty("xml.network-error"));

			URL url = copyToLocalFile(new URL(newSystemId)).toURL();
			InputSource source = new InputSource(newSystemId);
			source.setByteStream(url.openStream());
			return source;
		}
		else if(newSystemId == null)
			return null;
		else
		{
			// Xerces has a bug where an InputSource without a byte
			// stream is loaded incorrectly.
			InputSource source = new InputSource(newSystemId);
			source.setByteStream(new URL(newSystemId).openStream());
			return source;
		}
	} //}}}

	//{{{ addUserDTD() method
	public static void addUserDTD(String publicId, String systemId, String url)
	{
		load();

		Entry pe = new Entry( Entry.PUBLIC, publicId );
		userCatalog.put( pe, url );

		Entry se = new Entry( Entry.SYSTEM, systemId );
		userCatalog.put( se, url );
	} //}}}

	//{{{ removeUserDTD() method
	public static void removeUserDTD(String publicId, String systemId)
	{
		load();

		Entry pe = new Entry( Entry.PUBLIC, publicId );
		userCatalog.remove( pe );

		Entry se = new Entry( Entry.SYSTEM, systemId );
		userCatalog.remove( se );
	} //}}}

	//{{{ reload() method
	public static void reload(Entry e)
	{
		if(e.type == Entry.PUBLIC)
		{
			throw new RuntimeException(
				"Cannot reload a DTD from a PUBLIC id, only a SYSTEM id." );
		}

		try
		{

			if(isLocal(e))
			{

				File oldDtdFile = new File((String)userCatalog.get(e));

				File newFile = copyToLocalFile(new URL(e.id));

				oldDtdFile.delete();
				newFile.renameTo(oldDtdFile);

				/* JOptionPane.showMessageDialog(
					null,
					"Reloaded DTD to " + oldDtdFile ); */
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog( null, ex.getMessage() );
		}
	} //}}}

	//{{{ copyToLocalFile() method
	public static File copyToLocalFile(URL url)
		throws IOException
	{
		if(jEdit.getSettingsDirectory() == null)
			return null;

		BufferedReader in = new BufferedReader(
			new InputStreamReader(url.openStream()));

		String userDir = jEdit.getSettingsDirectory();

		File dtdDir = new File(userDir, "dtds");
		if (!dtdDir.exists())
			dtdDir.mkdir();

		File localFile = File.createTempFile("tmp", ".dtd", dtdDir);

		FileWriter out = new FileWriter(localFile);

		String line;
		while ((line = in.readLine()) != null)
			out.write(line);
		out.close();

		return localFile;
	} //}}}

	//{{{ isLocal() method
	public static boolean isLocal(Entry e)
	{
		if(e == null || jEdit.getSettingsDirectory() == null)
			return false;

		try
		{
			URL url = new File(jEdit.getSettingsDirectory()).toURL();
			String fileUrl = (String)userCatalog.get(e);
			return fileUrl.startsWith(url.toString());
		}
		catch (MalformedURLException ex)
		{
			return false;
		}
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

	//{{{ showDownloadDTDDialog() method
	private static boolean showDownloadDTDDialog(Component comp, String systemId)
	{
		return (GUIUtilities.confirm(comp,"xml.download-dtd",
			new String[] { systemId },JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION);
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
