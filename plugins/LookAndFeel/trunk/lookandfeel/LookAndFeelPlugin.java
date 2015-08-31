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

import java.awt.Frame;
import java.awt.Font;
import java.awt.Window;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIDefaults;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

public class LookAndFeelPlugin extends EBPlugin {

    public static boolean loadedInitialLnF = false;
    private static String lnfClassname = null;
    
    // <name, installer>, a list of installers for the system provided look and feels.
    private static Map<String, LookAndFeelInstaller> systemInstallers;

    public void start() {
        try {
            loadSystemInstallers();
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

        } catch ( Exception e ) {
            Log.log( Log.ERROR, this, e );
        }
    }

    public void stop() {
        if ( lnfClassname != null ) {
            jEdit.setProperty( "lookAndFeel", lnfClassname );
        }
    }

    public void handleMessage( EBMessage msg ) {
        if ( loadedInitialLnF && isStillLoaded() ) {
            LookAndFeelInstaller installer = getInstaller( lnfClassname );
            if ( installer == null ) {
                return;
            }
            installLookAndFeel( installer );
        }
        if ( msg instanceof PluginUpdate ) {
            PluginUpdate pu = ( PluginUpdate ) msg;
            if ( PluginUpdate.LOADED.equals( pu.getWhat() ) ) {
                start();
            }
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
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                try {
                    // jEdit.unsetProperty("lookAndFeel");
                    installer.install();
                    UIDefaults uid = UIManager.getDefaults();

                    // this is a workaround for the SkinLF; it doesn't find the
                    // the UI classes for overridden components
                    uid.put( org.gjt.sp.jedit.menu.EnhancedMenu.class, new javax.swing.JMenu().getUI() );
                    uid.put( org.gjt.sp.jedit.menu.EnhancedMenuItem.class, new javax.swing.JMenuItem().getUI() );
                    uid.put( org.gjt.sp.jedit.menu.EnhancedCheckBoxMenuItem.class, new javax.swing.JCheckBoxMenuItem().getUI() );

                    if ( jEdit.getBooleanProperty( "lookandfeel.usejeditfont", false ) ) {
                        Font primaryFont = jEdit.getFontProperty( "metal.primary.font" );
                        Font secondaryFont = jEdit.getFontProperty( "metal.secondary.font" );

                        if ( primaryFont != null && secondaryFont != null ) {
                            uid.put( "Button.font", primaryFont );
                            uid.put( "CheckBox.font", primaryFont );
                            uid.put( "Menu.font", primaryFont );
                            uid.put( "MenuBar.font", primaryFont );
                            uid.put( "MenuItem.font", primaryFont );
                            uid.put( "Label.font", primaryFont );
                            uid.put( "CheckBoxMenuItem.font", primaryFont );
                            uid.put( "RadioButtonMenuItem.font", primaryFont );
                            uid.put( "ComboBox.font", primaryFont );
                            uid.put( "InternalFrame.font", primaryFont );
                            uid.put( "PopupMenu.font", primaryFont );
                            uid.put( "RadioButton.font", primaryFont );
                            uid.put( "TabbedPane.font", primaryFont );
                            uid.put( "TableHeader.font", primaryFont );
                            uid.put( "ToggleButton.font", primaryFont );
                            uid.put( "InternalFrame.titleFont", primaryFont );

                            uid.put( "ColorChooser.font", secondaryFont );
                            uid.put( "DesktopIcon.font", secondaryFont );
                            uid.put( "List.font", secondaryFont );
                            uid.put( "OptionPane.font", secondaryFont );
                            uid.put( "Panel.font", secondaryFont );
                            uid.put( "PasswordField.font", secondaryFont );
                            uid.put( "ProgressBar.font", secondaryFont );
                            uid.put( "ScrollPane.font", secondaryFont );
                            uid.put( "Table.font", secondaryFont );
                            uid.put( "TextArea.font", secondaryFont );
                            uid.put( "TextField.font", secondaryFont );
                            uid.put( "TextPane.font", secondaryFont );
                            uid.put( "TitledBorder.font", secondaryFont );
                            uid.put( "ToolBar.font", secondaryFont );
                            uid.put( "ToolTip.font", secondaryFont );
                            uid.put( "Tree.font", secondaryFont );
                            uid.put( "Viewport.font", secondaryFont );
                        }
                    }

                    updateAllComponentTreeUIs();

                    lnfClassname = UIManager.getLookAndFeel().getClass().getName();
                    jEdit.setProperty( "lookAndFeel", lnfClassname );
                } catch ( Exception e ) {
                    Log.log( Log.ERROR, LookAndFeelPlugin.class, e );
                }
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
                SwingUtilities.updateComponentTreeUI( window );
                updateAllDialogComponentTreeUIs( window );
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
            if ( win instanceof Frame ) {
                continue;
            }
            SwingUtilities.updateComponentTreeUI( win );
            updateAllDialogComponentTreeUIs( win );
        }
    }

    /**
     * Returns the list of the possible look and feel options.
     */
    public static String[] getAvailableLookAndFeels() {
        // look and feels provided by the system
        UIManager.LookAndFeelInfo[] systemLnfs = UIManager.getInstalledLookAndFeels();
        String[] systemNames = new String[systemLnfs.length];
        for ( int i = 0; i < systemLnfs.length; i++ ) {
            systemNames[i] = systemLnfs[i].getName();
        }

        // look and feels provided by this plugin or other plugins
        String[] pluginNames = ServiceManager.getServiceNames( LookAndFeelInstaller.SERVICE_NAME );

        String[] allNames = new String[ systemNames.length + pluginNames.length];
        System.arraycopy( systemNames, 0, allNames, 0, systemNames.length );
        System.arraycopy( pluginNames, 0, allNames, systemNames.length, pluginNames.length );

        Arrays.sort( allNames, new Comparator<String>() {
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
        systemInstallers = new HashMap<String, LookAndFeelInstaller>();
        UIManager.LookAndFeelInfo[] systemLnfs = UIManager.getInstalledLookAndFeels();
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
            installer = ( LookAndFeelInstaller ) ServiceManager.getService( LookAndFeelInstaller.SERVICE_NAME, name );
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