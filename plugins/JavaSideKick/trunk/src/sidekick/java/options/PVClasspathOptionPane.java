package sidekick.java.options;

// imports
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

import sidekick.java.PVHelper;
import sidekick.java.util.CopyUtils;


/**
 * Option pane for setting the classpath via ProjectViewer.  Borrowed a lot of
 * this code from the Java Core plugin by Robert Fletcher.
 */
public class PVClasspathOptionPane extends AbstractOptionPane {
    // instance fields
    private PathBuilder classpathBuilder;
    private PathBuilder sourcepathBuilder;
    private JCheckBox useJavaClassPath;
    private JCheckBox useDotClassPathFile;
    private JTextField buildPath;
    private View view;
    public static String PREFIX = "sidekick.java.pv.";


    public PVClasspathOptionPane() {
        super( "javasidekick.pv.options" );
    }

    /** Initialises the option pane. */
    protected void _init() {
        setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );

        view = jEdit.getActiveView();

        String name = PVHelper.getProjectName( view );

        // Include java.class.path in classpath
        useJavaClassPath = new JCheckBox(
                    jEdit.getProperty( PREFIX + "useJavaClasspath.label" ),
                    jEdit.getBooleanProperty( PREFIX + name + ".useJavaClasspath" )
                );
        addComponent( useJavaClassPath );

        // TODO: move hard-coded string to properties file
        String projectRoot = PVHelper.getProjectRoot( view );
        File dotClassPathFile = new File( projectRoot, ".classpath" );
        if ( dotClassPathFile.exists() ) {
            useDotClassPathFile = new JCheckBox( "Use .classpath file", jEdit.getBooleanProperty( PREFIX + name + ".useDotClasspathFile") );
            addComponent( useDotClassPathFile );
            useDotClassPathFile.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        handleDotClassPathFile();
                    }
                }
            );
        }

        // Classpath components
        classpathBuilder = new PathBuilder(
                    jEdit.getProperty( PREFIX + "optionalClasspath.label" )
                );
        classpathBuilder.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        classpathBuilder.setFileFilter( new ClasspathFilter() );
        classpathBuilder.setPath(
            jEdit.getProperty( PREFIX + name + ".optionalClasspath", "" )
        );
        handleDotClassPathFile();
        classpathBuilder.setStartDirectory( PVHelper.getProjectRoot( view ) );
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
        sourcepathBuilder.setStartDirectory( PVHelper.getProjectRoot( view ) );
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
        JPanel buildPathPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        buildPathPanel.add( buildPathLabel );
        buildPathPanel.add( buildPath );
        buildPathPanel.add( browse_btn );
        addComponent( buildPathPanel );
    }

    // #_save() : void
    /** Saves properties from the option pane. */
    protected void _save() {
        String name = PVHelper.getProjectName( view );
        jEdit.setBooleanProperty(
            PREFIX + name + ".useJavaClasspath",
            useJavaClassPath.isSelected()
        );
        jEdit.setBooleanProperty(
            PREFIX + name + ".useDotClasspathFile",
            useDotClassPathFile.isSelected()
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

    private void handleDotClassPathFile() {
        if (useDotClassPathFile == null) {
            return;   
        }
        String dotClassPathPaths = getDotClassPathClassPaths();
        classpathBuilder.removePath( dotClassPathPaths );
        if ( useDotClassPathFile.isSelected() ) {
            classpathBuilder.setPath( dotClassPathPaths );
        }
    }


    private String getDotClassPathClassPaths() {
        // the .classpath file is an xml file, but it's pretty simple, so I'm parsing
        // it by hand rather than invoking an xml parser. I'm looking for lines like
        // <classpathentry kind="lib" path="lib/ant-contrib-1.0b3.jar"/>, so the line
        // must contain "classpathentry" and "kind="lib"".  If it does, then grab
        // the "path" value.  This path should be relative to the project root
        // directory.
        try {
            // load the .classpath file into a string array of lines
            String projectRoot = PVHelper.getProjectRoot( view );
            File dotClassPathFile = new File( projectRoot, ".classpath" );
            BufferedReader reader = new BufferedReader( new FileReader( dotClassPathFile ) );
            StringWriter writer = new StringWriter();
            CopyUtils.copy( reader, writer );
            String[] lines = writer.toString().split( "\\n" );

            // parse the string into paths
            StringBuffer sb = new StringBuffer();
            String pathRegex = "path=[\"](.*?)[\"]";
            Pattern pattern = Pattern.compile( pathRegex );
            for ( String line : lines ) {
                if ( line.indexOf( "classpathentry" ) < 0 || line.indexOf( "kind=\"lib\"" ) < 0 ) {
                    continue;
                }
                Matcher matcher = pattern.matcher( line );
                if ( matcher.find() ) {
                    String path = matcher.group( 1 );
                    File file = new File( projectRoot, path );
                    sb.append( file.getAbsolutePath() ).append( File.pathSeparator );
                }
            }
            return sb.toString();
        }
        catch ( Exception e ) {
            return "";
        }
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