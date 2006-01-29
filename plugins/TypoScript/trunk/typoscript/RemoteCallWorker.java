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
 * This handles the communication with the TYPO3 server.
 * This is accomplished through XML-RPC calls (using Apache's
 * XML-RPC library) to the jeditvfs plugin on the TYPO3 side.
 *
 * @author      Neil Bertram <neil@tasmanstudios.co.nz>
 */
package typoscript;


import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.gjt.sp.util.Log;

/**
 * This class handles the gruntwork of communicating with the TYPO3 plugin over XML-RPC
 * 
 */
public class RemoteCallWorker {
	public static final short JEDITVFS_ERROR_AUTHFAIL = 0;
	public static final short JEDITVFS_ERROR_UNKNOWNMETHOD = 1;
	public static final short JEDITVFS_ERROR_NOSUCHTEMPLATE = 2;
	private T3Site site;
	private XmlRpcClient xmlrpc;
	
	public RemoteCallWorker(T3Site parent) {
		site = parent;
		xmlrpc = new XmlRpcClient(site.getUrlFull());
		xmlrpc.setBasicAuthentication(site.getUsername(), site.getPassword());
	}
	
	public Hashtable getPageTree() throws XmlRpcException, IOException {
		Hashtable result = null;
		result = (Hashtable)remoteCall("vfs.getList", null);
		return result;
	}
	
	public String getSiteTitle() throws IOException, XmlRpcException {
		String result = null;
		result = (String)remoteCall("vfs.getSiteTitle", null);
		return result;
	}
	
	public String getConstants(Integer templateuid) throws IOException, XmlRpcException {
		String result = null;
		Vector params = new Vector(1);
		params.add(templateuid);
		result = (String)remoteCall("vfs.getConstants", params);
		return result;
	}
	
	public String getSetup(Integer templateuid) throws IOException, XmlRpcException {
		String result = null;
		Vector params = new Vector(1);
		params.add(templateuid);
		result = (String)remoteCall("vfs.getSetup", params);
		return result;
	}
	
	public void putConstants(Integer templateuid, String constants) throws IOException, XmlRpcException {
		Vector params = new Vector(3);
		params.add(templateuid);
		params.add(constants);
		Boolean clearCache = new Boolean(site.clearCacheOnSave());
		params.add(clearCache);
		remoteCall("vfs.putConstants", params);
	}
	
	public void putSetup(Integer templateuid, String setup) throws IOException, XmlRpcException {
		Vector params = new Vector(3);
		params.add(templateuid);
		params.add(setup);
		Boolean clearCache = new Boolean(site.clearCacheOnSave());
		params.add(clearCache);
		remoteCall("vfs.putSetup", params);
	}
	
	private Object remoteCall(String methodName, Vector params) throws IOException, XmlRpcException {
		Log.log(Log.DEBUG, this, "Sending remote call " + methodName  + " params " + params);
		if (params == null) params = new Vector(0);
		Object result = xmlrpc.execute(methodName, params);
		if (result == null) throw new XmlRpcException(-1, "No result - Server plugin missing?");
		if (result instanceof XmlRpcException) throw (XmlRpcException)result; // bug in xmlrpc?
		Log.log(Log.DEBUG, this, "Result: " + result);
		return result;
	}
}
