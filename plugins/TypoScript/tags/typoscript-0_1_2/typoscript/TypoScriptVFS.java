/***************************************************************
*  Copyright notice
*
*  (c) 2005,2006 Neil Bertram (neil@tasmanstudios.co.nz)
*  All rights reserved
*
*  This plugin is part of the Typo3 project. The Typo3 project is
*  free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*
*  The GNU General Public License can be found at
*  http://www.gnu.org/copyleft/gpl.html.
*  A copy is found in the textfile GPL.txt
*
*
*  This plugin is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  This copyright notice MUST APPEAR in all copies of the source!
***************************************************************/
/**
 * $Id$
 * 
 * This is the TypoScript over XML-RPC VFS for jEdit
 *
 * @author      Neil Bertram <neil@tasmanstudios.co.nz>
 * @credit		Ollie Rutherfurd <oliver@rutherfurd.net> for inspiration
 */

package typoscript;


import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.xmlrpc.XmlRpcException;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.util.Log;

/**
 * Implementation of the TypoScript VFS protocol
 * Requires the jeditvfs TYPO3 extension to be available on the server.
 * 
 * URLs for this VFS are of the form typoscript:Site Name::Path/To/Template.pageuid.templateuid.type.ts
 * type is either "constants" or "setup"
 * 
 * @author Neil Bertram <neil@tasmanstudios.co.nz>
 * @credit Ollie Rutherfurd <oliver@rutherfurd.net> for inspiration from his MoinMoin plugin
 *
 */
public class TypoScriptVFS extends VFS {
	private static HashMap pendingStreams;
	
	/**
	 * Constructs a new TypoScriptVFS instance, which has only READ and WRITE capabilities, not save-as etc
	 */
	public TypoScriptVFS() {
		super("typoscript", READ_CAP | WRITE_CAP);
	}
	
	/**
	 * jEdit calls this from the AWT thread when it wants to establish a VFS session
	 * for a particular path. We don't need to keep any persistent data, so we just
	 * return a dummy here
	 * @param path The VFS path
	 * @param comp The component of the parent window
	 * @return dummy session (empty object)
	 */
	public Object createVFSSession(String path, Component comp) {
		pendingStreams = new HashMap();
		
		// We don't need jEdit to keep session objects for us, return a "dummy" as per docs
		return new Object();
	}
	
	/**
	 * Gets the filename part of a path (Template.pageuid.templateuid.type.ts)
	 * @param path is the path provided by jEdit (typoscript:sitename::treepath)
	 */
	public String getFileName(String path) {
		String last = "";
		if (path.indexOf("/") != -1) { // this line used to read path.contains(), but has been changed to indexOf for JDK1.4 compliance
			// There's more than just the base site, strip back to the last /
			StringTokenizer st = new StringTokenizer(path, "/");
			while (st.hasMoreTokens()) last = st.nextToken();
		} else {
			// The template is in the root, return the part after the ::
			StringTokenizer st = new StringTokenizer(path, "::");
			while (st.hasMoreTokens()) last = st.nextToken();
		}
		return last;
	}
	
	public char getFileSeparator() {
		return '/';
	}
	
	public String getTwoStageSaveName(String path) {
		return null; // disable 2-stage save
	}
	
	public InputStream _createInputStream(Object session, String path, boolean ignoreErrors, Component comp) throws IOException, FileNotFoundException, MalformedURLException {
		if (path.endsWith(".marks")) throw new FileNotFoundException();
		
		Log.log(Log.DEBUG, this, "CREATE INPUT STREAM " + path);
		
		Object[] siteInfo = decodePath(path);
		T3Site site = (T3Site)siteInfo[0];
		Integer uid = (Integer)siteInfo[1];
		String type = (String)siteInfo[2];
		
		// Perform the XMLRPC request
		String templateContents = "";
		try {
			if (type.equals("constants")) {
				templateContents = site.getWorker().getConstants(uid);
			} else if (type.equals("setup")) {
				templateContents = site.getWorker().getSetup(uid);
			}
		} catch (XmlRpcException e) {
			switch(e.code) {
			case RemoteCallWorker.JEDITVFS_ERROR_AUTHFAIL: throw new IOException("Authentication with TYPO3 failed - Check your authentication settings in plugin options");
			case RemoteCallWorker.JEDITVFS_ERROR_NOSUCHTEMPLATE: throw new FileNotFoundException(e.getMessage());
			default: throw new IOException("Unexpected error while communicating with TYPO3: " + e.getMessage());
			}
		}
		
		return new ByteArrayInputStream(templateContents.getBytes("UTF-8"));
	}
	
	/**
	 * Creates an output stream for jEdit to save into. jEdit is actually saving into a String, which we commit on _saveCompleted
	 */
	public OutputStream _createOutputStream(Object session, String path, Component comp) throws IOException, FileNotFoundException, MalformedURLException {		
		Log.log(Log.DEBUG, this, "CREATE OUTPUT STREAM");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		pendingStreams.put(path, out);
		return out;
	}
	
	/**
	 * Write the contents of the output stream out to the server
	 * @param Session The VFS session (ignored)
	 * @param buffer The buffer
	 * @param path The path the buffer was saved to - this is the path we <i>will</i> save to though
	 * @throws IOException upon an exception from the underlying XML-RPC interface
	 * @throws FileNotFoundException upon the remote template being deleted
	 * @throws MalformedURLException in the case of a URL not containing all the elements we require (site, template UID, type)
	 */
	public void _saveComplete(Object Session, Buffer buffer, String path, Component comp) throws IOException, FileNotFoundException, MalformedURLException {
		Log.log(Log.DEBUG, this, "SAVE COMPLETE, WRITING");
		
		Object[] siteInfo = decodePath(path);
		T3Site site = (T3Site)siteInfo[0];
		Integer uid = (Integer)siteInfo[1];
		String type = (String)siteInfo[2];
		
		ByteArrayOutputStream outStream = (ByteArrayOutputStream)pendingStreams.remove(path);
		try {
			if (type.equals("constants")) {
				site.getWorker().putConstants(uid, outStream.toString("UTF-8"));
			} else if (type.equals("setup")) {
				site.getWorker().putSetup(uid, outStream.toString("UTF-8"));
			}
		} catch (XmlRpcException e) {
			Log.log(Log.ERROR, this, "Exception during save " + e);
			switch(e.code) {
			case RemoteCallWorker.JEDITVFS_ERROR_AUTHFAIL: throw new IOException("Authentication with TYPO3 failed - Check you authentication settings in plugin options");
			case RemoteCallWorker.JEDITVFS_ERROR_NOSUCHTEMPLATE: throw new FileNotFoundException(e.getMessage());
			default: throw new IOException("Unexpected error while communicating with TYPO3: " + e.getMessage());
			}
		}
	}
	
	/**
	 * This takes a path of the form Site name::PageName/OtherPageName/OtherOtherPageName.pageuid.templateuid.type.ts and finds
	 * the particular site, template UID and request type
	 * @param path The path to the template, as provided by jEdit
	 * @return A 3-element Object array containing (in this order) the T3Site object, template UID, and type (constants/setup)
	 * @throws FileNotFoundException If the site doesn't exist in the plugin's list of configured sites
	 */
	private Object[] decodePath(String path) throws FileNotFoundException, MalformedURLException {
		path = path.replaceFirst("typoscript:", "");
		
		// First part of URL is the site. The server will ensure the sitename doesn't contain :: as of 1.0.1 to prevent issues!
		String[] parts = path.split("::");
		if (parts.length < 2) {
			throw new MalformedURLException("No site component found in TypoScript URL");
		}
		String siteName = parts[0];
		Log.log(Log.DEBUG, this, "LOCATE SITE " + siteName);
		
		Iterator iter = TypoScriptPlugin.siteConfig.iterator();
		while (iter.hasNext()) {
			T3Site test = (T3Site)iter.next();
			Log.log(Log.DEBUG, this, "CHECKING " + test.getName());
			if (test.getName().equals(siteName)) {
				// Found it! Now extract the UID
				String[] infoTokens = path.split("\\.");
				
				Log.log(Log.DEBUG, this, "Components " + infoTokens);
				
				// The last 3 elements of infoTokens will be (template) uid, type and ts, so take size - 3 for uid and size -2 for type
				Integer uid = new Integer(infoTokens[infoTokens.length - 3]);
				String type = infoTokens[infoTokens.length - 2];
				
				Log.log(Log.DEBUG, this, "UID is " + uid + "\nType is " + type);
				
				// Construct the return structure
				Object[] ret = new Object[3];
				ret[0] = test;
				ret[1] = uid;
				ret[2] = type;
				
				return ret;
			}
		}
		// If we reach here, the site couldn't be found, this is a FileNotFoundException
		throw new FileNotFoundException("Unable to locate TYPO3 site for path: " + path);
	}
}
