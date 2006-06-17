package sidekick.java.options;

// imports
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

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
	
	private String prefix = "sidekick.java.pv.";


	public PVClasspathOptionPane() {
		super( "sidekick.java" );
	}

	/** Initialises the option pane. */
	protected void _init() {
		String name = getProjectName();
		
		// Include java.class.path in classpath
		useJavaClassPath = new JCheckBox(
		            jEdit.getProperty( prefix + "useJavaClasspath.label" ),
		            jEdit.getBooleanProperty( prefix + name + ".useJavaClasspath" )
		        );
		addComponent( useJavaClassPath );

		// Classpath components
		classpathBuilder = new PathBuilder(
		            jEdit.getProperty( prefix + "optionalClasspath.label" )
		        );
		classpathBuilder.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		classpathBuilder.setFileFilter( new ClasspathFilter() );
		classpathBuilder.setPath(
		    jEdit.getProperty( prefix + name + ".optionalClasspath", "" )
		);
		classpathBuilder.setEnabled(true);
		addComponent( classpathBuilder );
	}

	// #_save() : void
	/** Saves properties from the option pane. */
	protected void _save() {
		String name = getProjectName();
		jEdit.setBooleanProperty(
		    prefix + name + ".useJavaClasspath",
		    useJavaClassPath.isSelected()
		);
		jEdit.setProperty(
		    prefix + name + ".optionalClasspath",
		    classpathBuilder.getPath()
		);
	}

	private String getProjectName() {
		String project_name = "";
		if (ProjectOptions.getProject().getName() != null) {
			project_name = ProjectOptions.getProject().getName();	
		}
		return project_name;		
	}
	
	public void addComponent( PathBuilder comp ) {
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
}
