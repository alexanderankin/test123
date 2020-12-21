
/*
 * LookAndFeelPlugin.java - Look And Feel plugin
 * Copyright (C) 2001 Jamie LaScolea
 * Other contributors: David Huttleston Jr., Dirk Moebius, Calvin Yu
 *
 * :mode=java:tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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
package lookandfeel;


import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

// TODO: update jgoodies to 2.5.3, it's in the lib directory, source and binaries are in src/jgoodies-looks-2.5.3
public class LookAndFeelPlugin extends EBPlugin {

	public static boolean loadedInitialLnF = false;
	private static String lnfClassname = null;
	// <name, installer>, a list of installers for the system provided look and feels.
	private static Map<String, LookAndFeelInstaller> systemInstallers;
	// remember the system look and feels to be able to restore them when the
	// plugin is unloaded or removed
	private static UIManager.LookAndFeelInfo[] systemLnfs;

	public void start() {
		try {
			loadSystemInstallers();

			// update jEdit GlobalOptions/Appearance to let the user know to use this plugin
			// to adjust the look and feel
			// TODO: on reloading the plugin, this also gets added to the look and feel list in the option pane, need to avoid that
			UIManager.LookAndFeelInfo[] infos = new UIManager.LookAndFeelInfo [1];
			infos[0] = new UIManager.LookAndFeelInfo( jEdit.getProperty( "lookandfeel.useLookAndFeelPlugin", "Use Look And Feel plugin" ), "" );
			UIManager.setInstalledLookAndFeels( infos );

			String lnf = jEdit.getProperty( "lookandfeel.lookandfeel" );
			if ( LookAndFeelPlugin.isEmpty( lnf ) ) {
				return;
			}

			LookAndFeelInstaller installer = getInstaller( lnf );
			if ( installer == null ) {
				loadedInitialLnF = false;
				return;
			}
			installLookAndFeel( installer );
			loadedInitialLnF = true;
		}
		catch ( Exception e ) {
			Log.log( Log.ERROR, this, e );
		}
	}

	public void stop() {
		if ( lnfClassname != null ) {
			jEdit.setProperty( "lookAndFeel", lnfClassname );
		}
	}

	public void handleMessage( EBMessage msg ) {
		if ( msg instanceof PluginUpdate ) {
			PluginUpdate pu = ( PluginUpdate )msg;
			if ( PluginUpdate.LOADED.equals( pu.getWhat() ) ) {
				start();
			}
			else if ( PluginUpdate.UNLOADED.equals( pu.getWhat() ) || PluginUpdate.DEACTIVATED.equals( pu.getWhat() ) || PluginUpdate.REMOVED.equals( pu.getWhat() ) ) {

				// restore the system look and feels
				UIManager.setInstalledLookAndFeels( systemLnfs );
			}
		}
		else if ( loadedInitialLnF && isStillLoaded() ) {
			LookAndFeelInstaller installer = getInstaller( lnfClassname );
			if ( installer == null ) {
				return;
			}
			installLookAndFeel( installer );
		}
	}

	private boolean isStillLoaded() {
		String installed = UIManager.getLookAndFeel().getClass().getName();
		return installed == lnfClassname;
	}

	/**
	 * Install a look and feel based on the given installer.
	 */
	public static void installLookAndFeel( final LookAndFeelInstaller installer ) {
		if ( installer == null ) {
			return;
		}

		SwingUtilities.invokeLater( () -> {
			try {

				// jEdit.unsetProperty("lookAndFeel");
				installer.install();
				UIDefaults uid = UIManager.getDefaults();

				if ( jEdit.getBooleanProperty( "lookandfeel.usejeditfont", false ) ) {
					Font primaryFont = jEdit.getFontProperty( "metal.primary.font" );
					Font secondaryFont = jEdit.getFontProperty( "metal.secondary.font" );
					secondaryFont = secondaryFont == null ? primaryFont : secondaryFont;

					if ( primaryFont != null && secondaryFont != null ) {

						// This is the same as code in jEdit's Appearance option pane.
						// "primary" font, for buttons, labels, menus, etc, components that just 
						// display text
						uid.put( "Button.font", primaryFont );
						uid.put( "CheckBox.font", primaryFont );
						uid.put( "CheckBoxMenuItem.font", primaryFont );
						uid.put( "ColorChooser.font", primaryFont );
						uid.put( "DesktopIcon.font", primaryFont );
						uid.put( "Label.font", primaryFont );
						uid.put( "Menu.font", primaryFont );
						uid.put( "MenuBar.font", primaryFont );
						uid.put( "MenuItem.font", primaryFont );
						uid.put( "OptionPane.font", primaryFont );
						uid.put( "Panel.font", primaryFont );
						uid.put( "PopupMenu.font", primaryFont );
						uid.put( "ProgressBar.font", primaryFont );
						uid.put( "RadioButton.font", primaryFont );
						uid.put( "RadioButtonMenuItem.font", primaryFont );
						uid.put( "ScrollPane.font", primaryFont );
						uid.put( "Slider.font", primaryFont );
						uid.put( "TabbedPane.font", primaryFont );
						uid.put( "Table.font", primaryFont );
						uid.put( "TableHeader.font", primaryFont );
						uid.put( "TitledBorder.font", primaryFont );
						uid.put( "ToggleButton.font", primaryFont );
						uid.put( "ToolBar.font", primaryFont );
						uid.put( "ToolTip.font", primaryFont );
						uid.put( "Tree.font", primaryFont );
						uid.put( "Viewport.font", primaryFont );

						// secondary" font, for components the user can type into
						uid.put( "ComboBox.font", secondaryFont );
						uid.put( "EditorPane.font", secondaryFont );
						uid.put( "FormattedTextField.font", secondaryFont );
						uid.put( "List.font", secondaryFont );
						uid.put( "PasswordField.font", secondaryFont );
						uid.put( "Spinner.font", secondaryFont );
						uid.put( "TextArea.font", secondaryFont );
						uid.put( "TextField.font", secondaryFont );
						uid.put( "TextPane.font", secondaryFont );
					}
				}

				updateAllComponentTreeUIs();

				lnfClassname = UIManager.getLookAndFeel().getClass().getName();
				jEdit.setProperty( "lookAndFeel", lnfClassname );
			}
			catch ( Exception e ) {
				Log.log( Log.ERROR, LookAndFeelPlugin.class, e );
			}
		}
		);
	}

	/**
	 * Update the component trees of all windows.
	 */
	private static void updateAllComponentTreeUIs() {
		Window[] windows = Window.getWindows();
		for ( Window window : windows ) {
			if ( window != null ) {
				try {
					SwingUtilities.updateComponentTreeUI( window );
					updateAllDialogComponentTreeUIs( window );
				}
				catch ( Exception e ) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Update the component trees of all dialogs owned by the given window.
	 * <s>This method ignores frames because all frames should be handled from
	 * {@link updateAllComponentTreeUIs()}.</s>
	 */
	private static void updateAllDialogComponentTreeUIs( Window win ) {
		Window[] children = win.getOwnedWindows();
		for ( int i = 0; i < children.length; i++ ) {
			Window child = children[i];
			if ( child instanceof Frame ) {
				continue;
			}
			SwingUtilities.updateComponentTreeUI( child );
			updateAllDialogComponentTreeUIs( child );
		}
	}

	/**
	 * Returns the list of the possible look and feel options.
	 */
	public static String[] getAvailableLookAndFeels() {

		// look and feels provided by the system
		String[] systemNames = systemInstallers.keySet().toArray( new String [0]  );

		// look and feels provided by this plugin or other plugins
		String[] pluginNames = ServiceManager.getServiceNames( LookAndFeelInstaller.SERVICE_NAME );
		ArrayList<String> checkedNames = new ArrayList<String>();
		for (int i = 0; i < pluginNames.length; i++) {
			LookAndFeelInstaller installer = getInstaller(pluginNames[i]);
			if (installer != null) {
				checkedNames.add(pluginNames[i]);	
			}
		}
		pluginNames = checkedNames.toArray(new String[checkedNames.size()]);

		String[] allNames = new String [systemNames.length + pluginNames.length];
		System.arraycopy( systemNames, 0, allNames, 0, systemNames.length );
		System.arraycopy( pluginNames, 0, allNames, systemNames.length, pluginNames.length );

		Arrays.sort( allNames, new Comparator<String>(){

			public int compare( String a, String b ) {
				if ( "None".equals( a ) ) {
					return -1;
				}
				if ( "None".equals( b ) ) {
					return 1;
				}
				return a.compareTo( b );
			}
		} );
		return allNames;
	}

	private void loadSystemInstallers() {
		systemLnfs = UIManager.getInstalledLookAndFeels();
		systemInstallers = new HashMap<String, LookAndFeelInstaller>();
		for ( UIManager.LookAndFeelInfo info : systemLnfs ) {
			systemInstallers.put( info.getName(), new SystemLookAndFeelInstaller( info ) );
		}
	}

	/**
	 * Returns the installer for the named look and feel.
	 */
	public static LookAndFeelInstaller getInstaller( String name ) {
		LookAndFeelInstaller installer = systemInstallers.get( name );
		if ( installer == null ) {
			installer = ( LookAndFeelInstaller )ServiceManager.getService( LookAndFeelInstaller.SERVICE_NAME, name );
		}
		return installer;
	}

	/**
	 * Returns <code>true</code> if the given string is <code>null</code>
	 * or empty or contains only whitespace.
	 */
	protected static boolean isEmpty( String s ) {
		if ( s == null || s.length() == 0 ) {
			return true;
		}
		for ( int i = 0; i < s.length(); i++ ) {
			if ( !Character.isWhitespace( s.charAt( i ) ) ) {
				return false;
			}
		}
		return true;
	}
}
