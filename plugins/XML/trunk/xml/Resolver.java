package xml;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.xerces.util.XMLCatalogResolver;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;



/**
 * Resolver grabs and caches DTDs and xml schemas.
 * It also serves as a resource resolver for jeditresource: links
 * It is meant to replace CatalogManager
 * 
 * @author ezust
 *
 */
public class Resolver implements XMLEntityResolver
{

	public static final String NETWORK_PROPS = "xml.general.network";

	/**
	 * Ask before downloading remote files
	 */
	public static final String ASK = NETWORK_PROPS + ".ask";
	/**
	 * Cache downloaded remote files
	 */
	public static final String CACHE = NETWORK_PROPS + ".cache";
	/**
	 * Local and Catalog Only
	 */
	public static final String LOCAL = NETWORK_PROPS + ".local";
	
	// {{{ static variables
	/** Internal catalog for DTDs which are packaged in 
	 * XML.jar and jEdit.jar */
	public static final String INTERNALCATALOG = 
		"jeditresource:XML.jar!/xml/dtds/catalog";
	private static Object IGNORE = new Object();
	private static Resolver singleton = null;
	private static String resourceDir;
	
	// }}}
	
	// {{{ Instance Variables 
	/** Internal catalog for DTDs which are packaged in 
	 * XML.jar and jEdit.jar 
	   Parses and manages the catalog files 
	   */
	private XMLCatalogResolver catalog = null;

	/** Mapping of URLs to public IDs */
	private HashMap reverseResourceCache;
	
	/** Mapping from public ID to URLs */
	private HashMap resourceCache;
	
	/** List of catalog files to load */
	private List catalogFiles;
	
	
	// }}}
	
	// {{{ instance()
	
	/**
	 * 
	 * @return a global catalog resolver object you can use as an
	 * LSResourceResolver or EntityResolver.
	 */
	public static synchronized Resolver instance() {
		if (singleton == null)
			singleton = new Resolver();
		return singleton;
	}
	// }}}
	
	/**
	 * You can't create an object directly.
	 * use @ref instance() to get a singleton instance.
	 *
	 */
	private Resolver() {
		catalog = new XMLCatalogResolver();
		catalogFiles = new LinkedList();
		catalogFiles.add(INTERNALCATALOG);
		int i = 0;
		String uri = null;
		do { 
			String prop = "xml.catalog." + i++;
			uri = jEdit.getProperty(prop);
			if (uri != null) {
				catalogFiles.add(uri);
			}
		} while (uri != null);
		
		
		String[] catalogs = new String[catalogFiles.size()];
		for (i=0; i<catalogs.length; ++i) 
			catalogs[i] = catalogFiles.get(i).toString();
		
			
		
		catalog.setPreferPublic(true);
		catalog.setCatalogList(catalogs);
		if (isUsingCache()) 
			
		{
			resourceDir = MiscUtilities.constructPath(
				jEdit.getSettingsDirectory(),"dtds");
			
		}
		
	}

	public XMLInputSource resolveEntity(XMLResourceIdentifier rid) throws XNIException, IOException {
		return resolve(rid.getBaseSystemId(), rid.getPublicId(), rid.getExpandedSystemId());
	}
	
	public XMLInputSource resolve(String current, String publicId, String systemId) throws XNIException, IOException 
	{

		if(publicId != null && publicId.length() == 0)
			publicId = null;

		if(systemId != null && systemId.length() == 0)
			systemId = null;

		String newSystemId = null;

		/* we need this hack to support relative path names inside
		 * cached files. we want them to be resolved relative to
		 * the original system ID of the cached resource, not the
		 * cache file name on disk. */
		String parent;
		if(current != null)
		{
			Entry entry = (Entry)reverseResourceCache.get(current);
			if(entry != null)
				parent = entry.uri;
			else
				parent = MiscUtilities.getParentOfPath(current);
		}
		else
			parent = null;

		if(publicId == null && systemId != null && parent != null)
		{
			if(systemId.startsWith(parent))
			{
				// first, try resolving a relative name,
				// to handle jEdit built-in DTDs
				newSystemId = systemId.substring(parent.length());
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
				newSystemId = resolvePublic(systemId,publicId);
		}

		// well, the catalog can't help us, so just assume the
		// system id points to a file
		if(newSystemId == null)
		{
			if(systemId == null)
				return null;
			else if(MiscUtilities.isURL(systemId))
				newSystemId = systemId;
			// XXX: is this correct?
			/* else if(systemId.startsWith("/"))
				newSystemId = "file://" + systemId;
			else if(parent != null && !MiscUtilities.isURL(parent))
				newSystemId = parent + systemId; */
		}

		if(newSystemId == null)
			return null;

		Buffer buf = jEdit.getBuffer(XmlPlugin.uriToFile(newSystemId));
		if(buf != null)
		{
			if(buf.isPerformingIO())
				VFSManager.waitForRequests();
			Log.log(Log.DEBUG, getClass(), "Found open buffer for " + newSystemId);
			XMLInputSource source = new XMLInputSource(publicId, newSystemId, current);
			try
			{
				buf.readLock();
				source.setCharacterStream(new StringReader(buf.getText(0,
					buf.getLength())));
			}
			finally
			{
				buf.readUnlock();
			}
			return source;
		}
		else if(newSystemId.startsWith("file:")
			|| newSystemId.startsWith("jeditresource:"))
		{
			// InputSource source = new InputSource(systemId);
			 
			// InputStream is = new URL(newSystemId).openStream();
			XMLInputSource is = new XMLInputSource(publicId, newSystemId, current);
			return is;
			
		}
		else if (isLocal())
			return null;
		else
		{
			final String _newSystemId = newSystemId;
			final VFS vfs = VFSManager.getVFSForPath(_newSystemId);
			// use a final array to pass a mutable value from the
			// invokeAndWait() call
			final Object[] session = new Object[1];
			Runnable run = new Runnable()
			{
				public void run()
				{
					View view = jEdit.getActiveView();
					if (isAskBeforeDownloading() && showDownloadResourceDialog(view,_newSystemId))
					{
						session[0] = vfs.createVFSSession(
							_newSystemId,view);
					}
				}
			};
			
			if(SwingUtilities.isEventDispatchThread())
				run.run();
			else
			{
				try
				{
					SwingUtilities.invokeAndWait(run);
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
					// Log.log(Log.ERROR,CatalogManager.class,e);
				}
			}

			if(session[0] != null)
			{
				// InputSource source = new InputSource(systemId);
				XMLInputSource source = new XMLInputSource(publicId, systemId, current);
				if(isUsingCache())
				{
					File file;
					try
					{
						file = copyToLocalFile(session[0],vfs,newSystemId);
					}
					finally
					{
						vfs._endVFSSession(session,null);
					}

					addUserResource(publicId,systemId,file.toURL().toString());
					source.setByteStream(new FileInputStream(file));
				}
				else
					source.setByteStream(vfs._createInputStream(session,newSystemId,false,null));

				return source;
			}
			else 
			{
				throw new IOException(jEdit.getProperty("xml.network-error"));
			}
		}
		
	} //}}}

	public void clearCache()
	{
		

		Iterator files = resourceCache.values().iterator();
		while(files.hasNext())
		{
			Object obj = files.next();
			if(obj instanceof String)
			{
				String file = (String)XmlPlugin.uriToFile((String)obj);
				Log.log(Log.NOTICE, getClass(), "Deleting " + file);
				new File(file).delete();
			}
		}
		resourceCache.clear();
	} //}}}

	
	private String resolveSystem(String id) throws IOException 
	{
		
		Entry e = new Entry(Entry.SYSTEM,id,null);
		String uri = (String)resourceCache.get(e);
		if(uri == null)
			return catalog.resolveSystem(id);
		else if(uri == IGNORE)
			return null;
		else
			return uri;
	} //}}}

	//{{{ copyToLocalFile() method
	private static File copyToLocalFile(Object session, VFS vfs, String path)
		throws IOException
	{
		if(jEdit.getSettingsDirectory() == null)
			return null;

//		String userDir = jEdit.getSettingsDirectory();

		File _resourceDir = new File(resourceDir);
		if (!_resourceDir.exists())
			_resourceDir.mkdir();

		// Need to put this "copy from one stream to another"
		// into a common method some day, since other parts
		// of jEdit need it too...
		BufferedInputStream in = new BufferedInputStream(
			vfs._createInputStream(session,path,false,null));

		File localFile = File.createTempFile("cache", ".xml", _resourceDir);

		BufferedOutputStream out = new BufferedOutputStream(
			new FileOutputStream(localFile));

		byte[] buf = new byte[4096];
		int count = 0;
		while ((count = in.read(buf)) != -1)
			out.write(buf,0,count);
		out.close();

		return localFile;
	} //}}}

	//{{{ resolvePublic() method
	private String resolvePublic(String systemId, String publicId) throws IOException
		
	{
		Entry e = new Entry(Entry.PUBLIC,publicId,null);
		String uri = (String)resourceCache.get(e);
		if(uri == null)
			return catalog.resolvePublic(publicId,null);
		else if(uri == IGNORE)
			return null;
		else
			return uri;
	} //}}}
	private boolean showDownloadResourceDialog(Component comp, String systemId)
	{
		Entry e = new Entry(Entry.SYSTEM,systemId,null);
		if(resourceCache.get(e) == IGNORE)
			return false;

		int result = GUIUtilities.confirm(comp,"xml.download-resource",
			new String[] { systemId },JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE);
		if(result == JOptionPane.YES_OPTION)
			return true;
		else
		{
			resourceCache.put(e,IGNORE);
			return false;
		}
	} //}}}

	//{{{ addUserResource() method
	/**
	 * Don't want this public because then invoking {@link clearCache()}
	 * will remove this file, not what you would expect!
	 */
	private void addUserResource(String publicId, String systemId, String url)
	{
		if(publicId != null)
		{
			Entry pe = new Entry( Entry.PUBLIC, publicId, url );
			resourceCache.put( pe, url );
		}

		Entry se = new Entry( Entry.SYSTEM, systemId, url );
		resourceCache.put( se, url );
		reverseResourceCache.put(url,se);
	} //}}}

	public static class Entry
	{
		public static final int SYSTEM = 0;
		public static final int PUBLIC = 1;

		public int type;
		public String id;
		public String uri;

		public Entry(int type, String id, String uri)
		{
			this.type = type;
			this.id = id;
			this.uri = uri;
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

	static public boolean isUsingCache() {
		if(jEdit.getSettingsDirectory() == null) return false;
		return jEdit.getBooleanProperty(CACHE);
	}
	
	static public void setUsingCache(boolean newCache) {
		jEdit.setBooleanProperty(CACHE, newCache);
	}
	
	static public boolean isAskBeforeDownloading() {
		return jEdit.getBooleanProperty(ASK);
	}
	static public void setAskBeforeDownloading(boolean ask) {
		jEdit.setBooleanProperty(ASK, ask);
	}
	static public boolean isLocal() {
		return jEdit.getBooleanProperty(LOCAL);
	}
	static public void setLocal(boolean local) {
		jEdit.setBooleanProperty(LOCAL, local);
	}
	
}
