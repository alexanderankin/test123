package sidekick.java.options;

// imports
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;

import sidekick.java.PVHelper;
import projectviewer.config.ProjectOptions;


/**
 * Option pane for setting the classpath via ProjectViewer.  Borrowed a lot of
 * this code from the Java Core plugin by Robert Fletcher.
 */
public class PVClasspathOptionPane extends AbstractOptionPane {
    // instance fields
    private PathBuilder classpathBuilder;
    private PathBuilder sourcepathBuilder;
    private JCheckBox useJavaClassPath;
    private JTextField buildPath;

    public static String PREFIX = "sidekick.java.pv.";


    public PVClasspathOptionPane() {
        super( "sidekick.java" );
    }

    /** Initialises the option pane. */
    protected void _init() {
        String name = getProjectName();

        // Include java.class.path in classpath
        useJavaClassPath = new JCheckBox(
                    jEdit.getProperty( PREFIX + "useJavaClasspath.label" ),
                    jEdit.getBooleanProperty( PREFIX + name + ".useJavaClasspath" )
                );
        addComponent( useJavaClassPath );

        // Classpath components
        classpathBuilder = new PathBuilder(
                    jEdit.getProperty( PREFIX + "optionalClasspath.label" )
                );
        classpathBuilder.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        classpathBuilder.setFileFilter( new ClasspathFilter() );
        classpathBuilder.setPath(
            jEdit.getProperty( PREFIX + name + ".optionalClasspath", "" )
        );
        classpathBuilder.setStartDirectory( ProjectOptions.getProject().getRootPath() );
        classpathBuilder.setEnabled( true );
        addComponent( classpathBuilder );

        // Sourcepath components
        sourcepathBuilder = new PathBuilder(
                    jEdit.getProperty( PREFIX + "optionalSourcepath.label" )
                );
        sourcepathBuilder.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        sourcepathBuilder.setFileFilter( new SourceFileFilter() );
        sourcepathBuilder.setPath(
            jEdit.getProperty( PREFIX + name + ".optionalSourcepath", "" )
        );
        sourcepathBuilder.setStartDirectory( ProjectOptions.getProject().getRootPath() );
        sourcepathBuilder.setEnabled( true );
        addComponent( sourcepathBuilder );

        // build path components
        JLabel buildPathLabel = new JLabel( jEdit.getProperty( PREFIX + "buildOutputPath.label" ) );
        buildPath = new JTextField( 30 );
        buildPath.setText( jEdit.getProperty( PREFIX + name + ".optionalBuildpath", "" ) );
        JButton browse_btn = new JButton( "Browse" );
        browse_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        View view = GUIUtilities.getView( PVClasspathOptionPane.this );
                        String[] dirs = GUIUtilities.showVFSFileDialog( view, PVHelper.getProjectRoot( view ), VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false );
                        if ( dirs != null && dirs.length > 0 ) {
                            buildPath.setText( dirs[ 0 ] );
                        }
                    }
                }
                                    );
        JPanel buildPathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buildPathPanel.add( buildPathLabel );
        buildPathPanel.add( buildPath );
        buildPathPanel.add( browse_btn );
        addComponent( buildPathPanel );
    }

    // #_save() : void
    /** Saves properties from the option pane. */
    protected void _save() {
        String name = getProjectName();
        jEdit.setBooleanProperty(
            PREFIX + name + ".useJavaClasspath",
            useJavaClassPath.isSelected()
        );
        jEdit.setProperty(
            PREFIX + name + ".optionalClasspath",
            classpathBuilder.getPath()
        );
        jEdit.setProperty(
            PREFIX + name + ".optionalSourcepath",
            sourcepathBuilder.getPath()
        );
        jEdit.setProperty(
            PREFIX + name + ".optionalBuildpath",
            buildPath.getText()
        );
    }

    private String getProjectName() {
        String project_name = "";
        if ( ProjectOptions.getProject().getName() != null ) {
            project_name = ProjectOptions.getProject().getName();
        }
        return project_name;
    }

    public void addComponent( JComponent comp ) {
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridy = y++; // y is a protected member of AbstractOptionPane
        cons.gridheight = 1;
        cons.gridwidth = GridBagConstraints.REMAINDER;
        cons.fill = GridBagConstraints.BOTH;
        cons.anchor = GridBagConstraints.WEST;
        cons.weightx = 1.0f;
        cons.weighty = 1.0f;
        cons.insets = new Insets( 1, 0, 1, 0 );
        gridBag.setConstraints( comp, cons );
        add( comp );
    }

    private static class ClasspathFilter extends FileFilter {
        public boolean accept( File file ) {
            if ( file.isDirectory() ) {
                return true;
            }

            String filename = file.getName();
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
            return "Classpath elements (directories, *.jar, *.zip)";
        }
    }
    // -class _SourceFileFilter_
    private static class SourceFileFilter extends FileFilter {
        // +accept(File) : boolean
        public boolean accept( File file ) {
            if ( file.isDirectory() ) {
                return true;
            }

            String filename = file.getName();
            int idx = filename.lastIndexOf( '.' );
            if ( idx >= 0 ) {
                String ext = filename.substring( idx );
                if ( ext.equalsIgnoreCase( ".zip" ) ) {
                    return true;
                }
            }
            return false;
        }

        // +getDescription() : String
        public String getDescription() {
            return "Sourcepath elements (directories, *.zip)";
        }
    }
}
