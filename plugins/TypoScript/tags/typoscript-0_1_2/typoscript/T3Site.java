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
 * This object represents a remote site on a TYPO3 installation.
 * It contains the necessary URL and authentication information to
 * access site trees and template records.
 * These objects are serialiased within a Vector to save configuration
 * @see TypoScriptPlugin
 *
 * @author      Neil Bertram <neil@tasmanstudios.co.nz>
 */

package typoscript;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a T3 site in a serialisable form for saving to the configuration file in $HOME/typoscriptplugin/sitesconfig.obj
 */
public class T3Site implements Serializable, Comparable {
	private String name;
	private URL urlBase;
	private URL urlFull;
	private String username;
	private String password;
	private boolean clearCacheOnSave;
	transient private RemoteCallWorker worker;
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor used for deserialising and for creating a temporary site in the Add Site form
	 */
	public T3Site() {
		name = "";
		try {
			urlBase = new URL("http://www.example.com/siteroot/");
			urlFull = null;
		} catch (MalformedURLException e) {
			; // can't happen
		}
		username = "";
		password = "";
		clearCacheOnSave = true; //default
		worker = null;
	}
	
	/**
	 * Standard constructor - use this one to create a new site
	 * 
	 * @param newURLBase The URL base (URL to the site root)
	 * @param newURLFull The Full URL (URL to the responder page)
	 * @param user TYPO3 Administrator username
	 * @param pass TYPO3 Administrator password
	 * @param clearCache Whether to clear the TYPO3 page cache on each template save
	 */
	public T3Site(URL newURLBase, URL newURLFull, String user, String pass, boolean clearCache) {
		name = "UNKNOWN"; // will be fetched soon
		urlBase = newURLBase;
		urlFull = newURLFull;
		username = user;
		password = pass;
		clearCacheOnSave = clearCache;
		worker = new RemoteCallWorker(this);
	}

	public boolean clearCacheOnSave() {
		return clearCacheOnSave;
	}

	public void setClearCacheOnSave(boolean clearCacheOnSave) {
		this.clearCacheOnSave = clearCacheOnSave;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public URL getUrlBase() {
		return urlBase;
	}

	public void setUrlFull(URL url) {
		this.urlFull = url;
	}
	
	public URL getUrlFull() {
		return urlFull;
	}

	public void setUrlBase(URL url) {
		this.urlBase = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public RemoteCallWorker getWorker() {
		// If the worker isn't currently initialised (after deserialisation) we initialise it now
		if (worker == null) {
			worker = new RemoteCallWorker(this);
		}
		return worker;
	}
	
	public String toString() {
		return name;
	}
	
	// For sorting by name
	public int compareTo(Object other) {
		return this.toString().compareTo(other.toString());
	}
}
