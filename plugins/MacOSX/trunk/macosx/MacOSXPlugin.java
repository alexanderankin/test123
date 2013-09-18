/* 
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * MacOSXPlugin.java - Main class Mac OS X Plugin
 * Copyright (C) 2008 Seph M. Soliman
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package macosx;

//{{{ Imports
import javax.swing.*;
import java.util.regex.Pattern;
import java.io.File;
import java.awt.Window;
import java.lang.reflect.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.options.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;
import org.gjt.sp.util.ThreadUtilities;
//}}}

public class MacOSXPlugin extends EBPlugin
{
	//{{{ Variables
	private boolean osok = false;
	private static final Pattern ctrlPat = Pattern.compile("\\bctrl\\b");
	//}}}
	
	public void MacOSXPlugin()
	{
	}
	
	//{{{ start() method
	public void start()
	{
		if(osok())
		{
			Runnable setup = new Runnable() {
				public void run()
				{
					try
					{
						MacOSXPlugin listener = MacOSXPlugin.this;
						Class theClass = listener.getClass();
						
						// Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
						// use as delegates for various com.apple.eawt.ApplicationListener methods
						OSXAdapter.setQuitHandler(listener, theClass.getDeclaredMethod("handleQuit", (Class[])null));
						OSXAdapter.setAboutHandler(listener, theClass.getDeclaredMethod("handleAbout", (Class[])null));
						OSXAdapter.setPreferencesHandler(listener, theClass.getDeclaredMethod("handlePreferences", (Class[])null));
						OSXAdapter.setFileHandler(listener, theClass.getDeclaredMethod("handleOpenFile", new Class[] { String.class }));
						OSXAdapter.setReOpenApplicationHandler(listener, theClass.getDeclaredMethod("handleReOpenApplication", (Class[])null));
						
						String lf = jEdit.getProperty("lookAndFeel");
						if(lf != null && lf.length() != 0)
						{
							// Fix key bindings for OS X for anything other than Aqua LNF
							// See: http://lists.apple.com/archives/java-dev/2008/Apr/msg00209.html
							if (!UIManager.getLookAndFeel().isNativeLookAndFeel())
							{
								Log.log(Log.DEBUG, this, "Fixing keybindingds on current LNF");
								UIDefaults uid = UIManager.getLookAndFeelDefaults();
								fixMacKeyBindings(uid);
							}
						}
						
					}
					catch (Exception e)
					{
						System.err.println("Error while loading the OSXAdapter:" + e);
						e.printStackTrace();
					}
				}
			};
			
			ThreadUtilities.runInDispatchThread(setup);
			
			// Set global keyboard options from local properties
			Debug.ALTERNATIVE_DISPATCHER = jEdit.getBooleanProperty("plugin.MacOSXPlugin.altDispatcher");
			Debug.ALT_KEY_PRESSED_DISABLED = jEdit.getBooleanProperty("plugin.MacOSXPlugin.disableOption");
		}
	} //}}}
	
	public void stop()
	{
		// TODO: Only show it on reload - plugin does not support reloading
		//JOptionPane.showMessageDialog(null, jEdit.getProperty("MacOSXPlugin.dialog.unload.message"), jEdit.getProperty("MacOSXPlugin.dialog.unload.title"), 1);
	}
	
	// General quit handler; fed to the OSXAdapter as the method to call when a system quit event occurs
	// A quit event is triggered by Cmd-Q, selecting Quit from the application or Dock menu, or logging out
	public boolean handleQuit()
	{
		jEdit.exit(jEdit.getActiveView(), true);
		return false;
	}
	
	public void handlePreferences()
	{
		new GlobalOptions(jEdit.getActiveView());
	}
	
	// General info dialog; fed to the OSXAdapter as the method to call when 
	// "About OSXAdapter" is selected from the application menu
	public void handleAbout()
	{
		new AboutDialog(jEdit.getActiveView());
	}
	
	public void handleOpenFile(String filepath)
	{
		File file = new File(filepath);
		if(file.exists())
		{
			if(file.isDirectory())
			{
				// TODO: What to do with dirs?
				//VFSBrowser.browseDirectory(jEdit.getActiveView(), file.getPath());
				return;
			}
			
			if (jEdit.isStartupDone())
			{
				View view = jEdit.getActiveView();
				if (view == null)
				{
					view = PerspectiveManager.loadPerspective(
						jEdit.getBooleanProperty("restore") &&
						jEdit.getBooleanProperty("restore.cli"));
				}
				
				if (jEdit.openFile(view, file.getPath()) == null)
					Log.log(Log.ERROR, this, "Unable to open file: " + filepath);
			}
			else
			{
				jEdit.openFileAfterStartup(file.getPath());
			}
		}
		else
		{
			Log.log(Log.ERROR, this, "Cannot open non-existing file: " + filepath);
		}
		
	}
	
	public void handleReOpenApplication()
	{
		if(jEdit.getActiveView() != null)
		{
			jEdit.getActiveView().requestFocus();
		}
		else
		{
			PerspectiveManager.loadPerspective(jEdit.getBooleanProperty("restore"));
		}
	}
	
	public void handleMessage(EBMessage message)
	{
		if(message instanceof BufferUpdate)
		{
			BufferUpdate msg = (BufferUpdate)message;
			
			Buffer buffer = msg.getBuffer();
			View[] views = jEdit.getViews();
			for (View view : views)
			{
				if (view.getBuffer() == buffer)
				{
					refreshProxyIcon(view);
				}
			}	
		}
		else if(message instanceof ViewUpdate)
		{
			ViewUpdate msg = (ViewUpdate)message;
			refreshProxyIcon(msg.getView());
			
			if (msg.getWhat() == ViewUpdate.CREATED)
				enableFullScreenMode(msg.getView());
		}
		else if(message instanceof EditPaneUpdate)
		{
			EditPaneUpdate msg = (EditPaneUpdate)message;
			refreshProxyIcon(msg.getEditPane().getView());
		}
	}
	
	public void refreshProxyIcon(View view)
	{
		if (view == null) return;
		
		Buffer buffer = view.getBuffer();
		if (buffer == null)
		{
			view.getRootPane().putClientProperty("Window.documentModified", Boolean.FALSE);
			view.getRootPane().putClientProperty("windowModified", Boolean.FALSE); // * support for Tiger
			view.getRootPane().putClientProperty("Window.documentFile", null);
			return;
		}
		
		// Turn on/off the "document modified" dot
		if(buffer.isDirty())
		{
			view.getRootPane().putClientProperty("Window.documentModified", Boolean.TRUE);
			view.getRootPane().putClientProperty("windowModified", Boolean.TRUE); // * support for Tiger
		}
		else
		{
			view.getRootPane().putClientProperty("Window.documentModified", Boolean.FALSE);
			view.getRootPane().putClientProperty("windowModified", Boolean.FALSE); // * support for Tiger
		}
		
		// Set the path to the proxy icon
		if (buffer.isNewFile())
		{
			view.getRootPane().putClientProperty("Window.documentFile", null);
		}
		else
		{
			view.getRootPane().putClientProperty("Window.documentFile", new File(buffer.getPath()));
		}
	}
	
	public static void fixMacKeyBindings(UIDefaults uiDefaults)
	{
		Object[] keys = uiDefaults.keySet().toArray(); // Copied to prevent concurrent modification issues.
		
		for (Object key : keys)
		{
			Object  value = uiDefaults.get(key);
			
			if (value instanceof InputMap)
			{
				InputMap map = (InputMap) value;
				KeyStroke[] keyStrokes = map.keys();
				
				if (keyStrokes != null)
				{
					for (KeyStroke keyStroke : keyStrokes)
					{
						String  keyString = keyStroke.toString();
						
						if (keyString.indexOf("ctrl ") >= 0)
						{
							Object  action = map.get(keyStroke);
							
							keyString = ctrlPat.matcher(keyString).replaceAll("meta");
							map.remove(keyStroke);
							keyStroke = KeyStroke.getKeyStroke(keyString);
							map.put(keyStroke, action);
						}
					}
				}
			}
		}
	}
	
	public static void setAlternativeDispatcher(boolean state)
	{
		jEdit.setBooleanProperty("plugin.MacOSXPlugin.altDispatcher", state);
		Debug.ALTERNATIVE_DISPATCHER = state;	
	}
	
	public static boolean getAlternativeDispatcher()
	{
		// This is a public variable, so it can be changed at any time
		boolean state = Debug.ALTERNATIVE_DISPATCHER;
		if (jEdit.getBooleanProperty("plugin.MacOSXPlugin.altDispatcher") != state)
		{
			jEdit.setBooleanProperty("plugin.MacOSXPlugin.altDispatcher", state);
		}
		
		return state;
	}
	
	public static void setDisableOption(boolean state)
	{
		jEdit.setBooleanProperty("plugin.MacOSXPlugin.disableOption", state);
	}
	
	public static boolean getDisableOption()
	{
		// This is a public variable, so it can be changed at any time
		return jEdit.getBooleanProperty("plugin.MacOSXPlugin.disableOption");
	}
	
	public void enableFullScreenMode(View view)
	{
		if (fullScreenFailed)
			return;
		
		try
		{
			// FullScreenUtilities.setWindowCanFullScreen(view, true);
			Class<?> Util = Class.forName("com.apple.eawt.FullScreenUtilities");
			Class arguments[] = new Class[] { java.awt.Window.class, Boolean.TYPE };
			Method setWindowCanFullScreen = Util.getMethod("setWindowCanFullScreen", arguments);
			setWindowCanFullScreen.invoke(Util, view, true);
		}
		catch (Exception e)
		{
			Log.log(Log.DEBUG, this, "Unable to enable OS X native full screen mode: " + e);
			fullScreenFailed = true;
		}
	}

	//{{{ osok() method
	private boolean osok()
	{	
		if (!OperatingSystem.isMacOS())
		{
			// According to Slava this is better
			Log.log(Log.ERROR,this,jEdit.getProperty("MacOSXPlugin.dialog.osname.message"));
			return false;
		}

		return true;
	}//}}}

	//{{{ Instance variables
	
	// If unable to enable full screen mode (e.g., running on OSX 10.6 or earlier), don't keep trying
	private boolean fullScreenFailed = false;
	//}}}
}
