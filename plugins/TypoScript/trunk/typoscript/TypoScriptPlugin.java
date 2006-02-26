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
 * The main plugin class for the TypoScript jEdit plugin
 * This handles loading and saving of configuration, and
 * handling of EditBus events that may require us to reload
 * the site browser dockable.
 *
 * @author      Neil Bertram <neil@tasmanstudios.co.nz>
 */

package typoscript;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Vector;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;

/**
 * Main plugin class, handles configuration serialisation to the file in $HOME/typoscriptplugin/sitesconfig.obj
 * Also checks for EditBus messages that may require us to reload any open site browsers
 */
public class TypoScriptPlugin extends EBPlugin {
	public static final String NAME = "typoscript";
	public static final String MENU = "typoscript.menu";
	public static final String PROPERTY_PREFIX = "plugin.typoscript.TypoScriptPlugin.";
	public static final String OPTION_PREFIX = "options.typoscript";
	
	protected static TypoScriptPlugin instance;
	protected static Vector siteConfig;
	
	public TypoScriptPlugin() {
		super();
		instance = this;
	}
	
	public void start() {
		super.start();
		loadConfiguration();
		if (siteConfig == null) {
			Log.log(Log.ERROR, this, "Configuration null??");
			siteConfig = new Vector();
		}
	}
	
	public void loadConfiguration() {
		if (jEdit.getSettingsDirectory() == null) return; // no settings directory
		// Attempt to load the configuration in from the user's config file
		String configPath = MiscUtilities.constructPath(jEdit.getSettingsDirectory(), "typoscriptplugin" + File.separatorChar + "sitesconfig.obj");
		File configFile = new File(configPath);
		if (!configFile.exists()) {
			// Check to see if the "typoscriptplugin" directory exists
			if (!configFile.getParentFile().exists()) {
				Log.log(Log.NOTICE, this, "Couldn't find configuration directory, creating directory");
				// Create it
				if (!configFile.getParentFile().mkdir()) {
					Log.log(Log.ERROR, this, "Couldn't create configuration directory in " + configFile.getParentFile().getPath());
					return;
				}
			}
			// By now the directory either already exists, or has been created, create the file
			Log.log(Log.NOTICE, this, "Creating configuration file");
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				Log.log(Log.ERROR, this, "Failed to create configuration file at " + configPath + ", an IOException occured:" + e.toString());
			}
			
			// Create an empty configuration vector and commit it to the new configuration file
			siteConfig = new Vector();
			saveConfiguration();
			Log.log(Log.NOTICE, this, "Configuration file created");
		} else {
			// File exists, de-serialise it
			try {
				FileInputStream fIn = new FileInputStream(configPath);
				ObjectInputStream objIn = new ObjectInputStream(fIn);
				siteConfig = (Vector)objIn.readObject();
			} catch (FileNotFoundException e) {
				// This technically can't happen... Well unless you're *very* quick to delete the file
				e.printStackTrace();
			} catch (IOException e) {
				Log.log(Log.ERROR, this, "An IOException occured while deserialising the configuration:" + e.toString());
			} catch (ClassNotFoundException e) {
				// Can this happen? We know what a vector is...
				Log.log(Log.ERROR, this, "Deserialise error (classnotfound)");
				e.printStackTrace();
			}
		}
	}
	
	public static void saveConfiguration() {
		if (jEdit.getSettingsDirectory() == null) return; // no settings directory
		String configPath = MiscUtilities.constructPath(jEdit.getSettingsDirectory(), "typoscriptplugin" + File.separatorChar + "sitesconfig.obj");
		try {
			FileOutputStream fOut = new FileOutputStream(configPath);
			ObjectOutputStream objOut = new ObjectOutputStream(fOut);
			objOut.writeObject(siteConfig);
		} catch (FileNotFoundException e) {
			Log.log(Log.ERROR, TypoScriptPlugin.instance, "Couldn't serialialise configuration because the output file at " + configPath + " doesn't exist!");
		} catch (IOException e) {
			Log.log(Log.ERROR, TypoScriptPlugin.instance, "An IOException occured while serialising the configuration to a file:" + e.toString());
		}
	}
	
	/**
	 * Handles EditBus messages that we care about
	 * We are interested in options saving (we ping the browser to reload, if it's up,
	 * and a buffer update so we can force the TypoScript editmode
	 * @param message The message from EditBus
	 * @credit Ollie Rutherfurd for his MoinMoin plugin
	 */
	public void handleMessage(EBMessage message) {
		if (message instanceof PropertiesChanged) {
			View[] openViews = jEdit.getViews();
			for (int i = 0; i < openViews.length; i++) {
				TypoScriptSiteBrowser browser = (TypoScriptSiteBrowser)openViews[i].getDockableWindowManager().getDockable("typoscriptsitebrowser");
				if (browser != null) {
					browser.checkForPossibleSitesUpdate();
				}
			}
		}
		if (message instanceof BufferUpdate) {
			BufferUpdate bu = (BufferUpdate)message;
			if (bu.getWhat() == BufferUpdate.LOADED) {
				Buffer buf = bu.getBuffer();
				VFS vfs = buf.getVFS();
				if (vfs instanceof TypoScriptVFS) {
					Mode mode = jEdit.getMode("typoscript");
					if (mode == null) {
						loadModeManually();
					}
					mode = jEdit.getMode("typoscript");
					if (mode != null) {
						buf.setMode(mode);
					} else {
						Log.log(Log.ERROR, TypoScriptPlugin.instance, "Failed to manually load mode!");
					}
				}
			}
		}
	}
	
	/**
	 * Manually load the typoscript edit mode for versions of jEdit that don't ship with it (eg. < 4.3.4)
	 */
	private void loadModeManually() {
		// Check if the typoscript.xml file exists in the users settings directory
		// (we can't link directly to a mode file inside the JAR, since the XML parser needs a string
		// filename, which can't represent a path to a JAR resource)
		Log.log(Log.NOTICE, TypoScriptPlugin.instance, "Manually loading TypoScript edit mode since your version of jEdit doesn't ship with it");
		if (jEdit.getSettingsDirectory() == null) return; // no settings directory
		String modePath = MiscUtilities.constructPath(jEdit.getSettingsDirectory(), "typoscriptplugin" + File.separatorChar + "typoscript.xml");
		File modeFile = new File(modePath);
		if (!modeFile.exists()) {
			Log.log(Log.NOTICE, TypoScriptPlugin.instance, "TypoScript edit mode not found, copying it into your user settings directory...");
			// Copy the file from our jar to there
			try {
				InputStream input = TypoScriptPlugin.class.getResource("/typoscript/typoscript.xml").openStream();
				OutputStream output = new FileOutputStream(modeFile);
				byte[] buffer = new byte[100000];
				int length;
				while ((length = input.read(buffer)) >= 0) {
					output.write(buffer, 0, length);
				}
				output.close();
				Log.log(Log.NOTICE, TypoScriptPlugin.instance, "Mode file copying completed succesfully");
			} catch (IOException e) {
				Log.log(Log.ERROR, TypoScriptPlugin.instance, "IOException while trying to copy typoscript.xml mode file to users settings dir:\n" + e.toString());
			}
		}
		
		Mode tsMode = new Mode("typoscript");
		Log.log(Log.DEBUG, TypoScriptPlugin.instance, "TS mode path: " + modePath);
		tsMode.setProperty("file", modePath);
		tsMode.setProperty("filenameGlob", "*.ts");
		tsMode.unsetProperty("firstlineGlob");
		
		// I'm aware the below method is not supposed to be called, but I think this is the only way to do it?
		jEdit.addMode(tsMode);
		
		tsMode.init();
	}
}
