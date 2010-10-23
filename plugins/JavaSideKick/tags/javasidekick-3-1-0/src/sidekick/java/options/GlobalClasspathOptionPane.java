package sidekick.java.options;

// imports
import common.gui.FileTextField;
import common.gui.pathbuilder.PathBuilder;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;

import projectviewer.vpt.VPTProject;

import sidekick.java.PVHelper;
import sidekick.java.util.Locator;


/**
 * Option pane for setting the classpath via global options.  Borrowed a lot of
 * this code from the Java Core plugin by Robert Fletcher.
 */
public class GlobalClasspathOptionPane extends AbstractOptionPane {
    // instance fields
    private PathBuilder classpathBuilder;
    private View view;
	private JCheckBox includeSystem;
	private JCheckBox includePlugins;
    public static String PREFIX = "sidekick.java.global.";


    public GlobalClasspathOptionPane() {
        super( "sidekick.java.global.classpath" );
    }

    /** Initialises the option pane. */
    protected void _init() {
        setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );

		includeSystem = new JCheckBox(jEdit.getProperty("sidekick.java.classpathIncludeSystem.label"),
				jEdit.getBooleanProperty("sidekick.java.classpathIncludeSystem", true));
		includePlugins = new JCheckBox(jEdit.getProperty("sidekick.java.classpathIncludePlugins.label"),
				jEdit.getBooleanProperty("sidekick.java.classpathIncludePlugins", true));

        includeSystem.setToolTipText( System.getProperty("java.class.path") );

		addComponent(includeSystem);
		addComponent(includePlugins);

		classpathBuilder = new PathBuilder();
		classpathBuilder.setPath( jEdit.getProperty("sidekick.java.classpath", "") );
		addComponent(classpathBuilder);

    }

    // #_save() : void
    /** Saves properties from the option pane. */
    protected void _save() {
		jEdit.setProperty("sidekick.java.classpath", classpathBuilder.getPath());
		jEdit.setBooleanProperty("sidekick.java.classpathIncludePlugins", includePlugins.isSelected());
		jEdit.setBooleanProperty("sidekick.java.classpathIncludeSystem", includeSystem.isSelected());
		Locator.getInstance().refreshGlobal();
    }

    private static class ClasspathFilter extends FileFilter {
        public boolean accept( File file ) {
            if ( file.isDirectory() ) {
                return true;
            }

            String filename = file.getName();
            if ( ".classpath".equals( filename ) ) {
                return true;
            }
            int idx = filename.lastIndexOf( '.' );
            if ( idx >= 0 ) {
                String ext = filename.substring( idx );
                if ( ext.equalsIgnoreCase( ".jar" ) || ext.equalsIgnoreCase( ".zip" ) ) {
                    return true;
                }
            }
            return false;
        }

        public String getDescription() {
            return "Classpath elements (directories, *.jar, *.zip, .classpath)";
        }
    }
}
