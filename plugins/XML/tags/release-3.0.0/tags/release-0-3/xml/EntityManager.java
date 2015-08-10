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
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.apache.xerces.readers.XCatalog;
import org.xml.sax.*;

// This class is not finished and probably doesn't work.

public class EntityManager
{
	public static InputSource resolveSystemId(String current, String systemId)
		throws SAXException, IOException, MalformedURLException
	{
		if(!loaded)
			load();

		// first, try explicit id -> url map
		Entity entity = (Entity)systemIdMap.get(systemId);
		if(entity != null)
			return entity.getInputSource();

		// next, see if it exists in current buffer's directory
		VFS vfs = VFSManager.getVFSForPath(current);
		if(vfs instanceof FileVFS)
		{
			String directory = vfs.getParentOfPath(current);
			String path = vfs.constructPath(current,systemId);
			File file = new File(path);
			if(file.exists())
				return entity.getInputSource();
		}

		return null;
	}

	public static InputSource resolvePublicId(String current, String publicId)
		throws SAXException, IOException, MalformedURLException
	{
		if(!loaded)
			load();

		// first, try explicit id -> url map
		Entity entity = (Entity)publicIdMap.get(publicId);
		if(entity != null)
			return entity.getInputSource();

		// try using the XCatalog
		return catalog.resolveEntity(publicId,null);
	}

	public static void propertiesChanged()
	{
		loaded = false;
	}

	// private members
	private static Hashtable systemIdMap;
	private static Hashtable publicIdMap;
	private static XCatalog catalog;
	private static boolean loaded;

	private static void load()
	{
		systemIdMap = new Hashtable();

		systemIdMap.put("actions.dtd",new Entity(JEDIT_RESOURCE,
			"/org/gjt/sp/jedit/actions.dtd",null));
		systemIdMap.put("catalog.dtd",new Entity(JEDIT_RESOURCE,
			"/org/gjt/sp/jedit/catalog.dtd",null));
		systemIdMap.put("recent.dtd",new Entity(JEDIT_RESOURCE,
			"/org/gjt/sp/jedit/recent.dtd",null));
		systemIdMap.put("xmode.dtd",new Entity(JEDIT_RESOURCE,
			"/org/gjt/sp/jedit/xmode.dtd",null));
		systemIdMap.put("pluginmgr.dtd",new Entity(JEDIT_RESOURCE,
			"/org/gjt/sp/jedit/pluginmgr.dtd",null));

		systemIdMap.put("commando.dtd",new Entity(PLUGIN_RESOURCE,
			"console.ConsolePlugin","/console/commando/commando.dtd"));

		catalog = new XCatalog();

		loaded = true;
	}

	private static final int INVALID = 0;
	private static final int FILE = 1;
	private static final int URL = 2;
	private static final int JEDIT_RESOURCE = 3;
	private static final int PLUGIN_RESOURCE = 4;

	static class Entity
	{
		int type;
		String arg1;
		String arg2;

		Entity(int type, String arg1, String arg2)
		{
			this.type = type;
			this.arg1 = arg1;
			this.arg2 = arg2;
		}

		InputSource getInputSource()
			throws IOException, MalformedURLException
		{
			InputSource source = null;

			if(type == FILE || type == URL)
			{
				Buffer buffer = jEdit.getBuffer(arg1);
				if(buffer != null)
				{
					String text;
					try
					{
						text = buffer.getText(0,buffer.getLength());
					}
					catch(BadLocationException bl)
					{
						text = "";
					}
					source = new InputSource(new StringReader(text));
				}
			}

			if(source == null)
				source = new InputSource(getInputStream());

			source.setSystemId(getSystemId());

			return source;
		}

		InputStream getInputStream()
			throws IOException, MalformedURLException
		{
			switch(type)
			{
			case FILE:
				return new FileInputStream(arg1);
			case URL:
				return new URL(arg1).openStream();
			case JEDIT_RESOURCE:
				return jEdit.class.getResourceAsStream(arg1);
			case PLUGIN_RESOURCE:
				EditPlugin plugin = jEdit.getPlugin(arg1);
				if(plugin == null)
				{
					Log.log(Log.WARNING,this,"No such plugin: "
						+ arg1);
					return null;
				}

				return plugin.getJAR().getClassLoader()
					.getResourceAsStream(arg2);
			default:
				throw new InternalError();
			}
		}

		// funky: we return archive: URLs here so if an error occurs
		// while parsing a plugin or jEdit resource DTD, the user
		// will be able to click on the error in the error list if
		// the Archive plugin is installed.
		String getSystemId()
		{
			switch(type)
			{
			case FILE:
			case URL:
				return arg1;
			case JEDIT_RESOURCE:
				String jEditHome = jEdit.getJEditHome();
				if(jEditHome == null)
					return null;

				return "archive:" + MiscUtilities.constructPath(
					jEditHome,"jedit.jar") + "!" + arg1;
			case PLUGIN_RESOURCE:
				EditPlugin plugin = jEdit.getPlugin(arg1);
				if(plugin == null)
					return null;

				return "archive:" + plugin.getJAR().getPath()
					+ "!" + arg2;
			default:
				throw new InternalError();
			}
		}
	}
}
