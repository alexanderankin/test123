/*
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
 
package projectviewer.config;

// Import Java

// Import AWT/Swing
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

// Import jEdit
import projectviewer.Project;
import projectviewer.ProjectViewer;
import projectviewer.ProjectDirectory;

/**
 *  A dialog for configuring the properties of a project.
 *
 *  @author     Marcelo Vanzin
 */
public class ProjectPropertiesDlg extends JDialog implements ActionListener {

    //--------------- Static Methods & Variables
    
    private final static int ERROR  = -1;
    private final static int CANCEL = 0;
    private final static int OK     = 1;
    
    private static ProjectPropertiesDlg instance;
    
    /**
     *  Shows the dialog to edit the properties of the provided
     *  project. If the project is <i>null</i>, creates a new one,
     *  unless the user hits "Cancel" (then <i>null</i> is returned).
     *
     *  @param  proj    The project to be edited, or null to create a new one.
     *  @param  x       Where to show the dialog (x-axis).
     *  @param  y       Where to show the dialog (y-axis);
     */
    public static Project run(ProjectViewer owner, Project proj) {
        if (instance == null) {
            instance = new ProjectPropertiesDlg(owner);
        }
        
        instance.setProject(proj);
        instance.show();
        return instance.getProject();
    }
    
    //--------------- Instance Variables
    
    private int result;
    private Project project;
    
    private JTextField projName;
    private JTextField projRoot;
    private JTextField projURLRoot;
    
    private JButton    chooseRoot;
    private JButton    updateProject;
    private JButton    cancel;

    //--------------- Constructors
    
    /** Builds the dialog. */
    private ProjectPropertiesDlg(ProjectViewer owner) {
        loadGUI();
        setModal(true);
        setLocationRelativeTo(owner);
    }
    
    //--------------- Public Methods
    
    public void actionPerformed(ActionEvent e) {
        JButton jb = (JButton) e.getSource();
        
        if (jb == chooseRoot) {
            chooseRoot();
        } else if (jb == updateProject) {
            result = updateProject();
            if (result == OK) {
                hide();
            }
        } else if (jb == cancel) {
            result = CANCEL;
            hide();
        }
        
    }
    
    //--------------- Private Methods
    
    private Project getProject() {
        return project;
    }
    
    private void setProject(Project p) {
        this.project = p;
        if (p != null) {
            projName.setText(p.getName());
            projRoot.setText(p.getRoot().getPath());
            projRoot.setToolTipText(projRoot.getText());
            setTitle("Edit project: " + p.getName());
        } else {
            projName.setText("");
            projRoot.setText("");
            projRoot.setToolTipText(projRoot.getText());
            setTitle("Create new project");
        }
    }
    
    /**
     *  Shows a file chooser so the user can choose the root directory of
     *  its project. In case the user chooses a directory, the corresponding
     *  JTextField is updated to show the selection.
     */
    private void chooseRoot() {    
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Enter the root directory for the project:");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            projRoot.setText(chooser.getSelectedFile().getAbsolutePath());
            projRoot.setToolTipText(projRoot.getText());
        }

    }
    
    /** Updates the project with the info supplied by the user. */
    private int updateProject() {
        String name = projName.getText().trim();
        
        if (name.length() == 0) {
            JOptionPane.showMessageDialog(
                this, 
                "Please specify a name for the project.",
                "Error: no name supplied",
                JOptionPane.ERROR_MESSAGE
             );
             return ERROR;
        } 
        
        String root = projRoot.getText().trim();
        if (root.length() == 0) {
            JOptionPane.showMessageDialog(
                this, 
                "Please specify a root for the project.",
                "Error: no root supplied",
                JOptionPane.ERROR_MESSAGE
             );
             return ERROR;
        } 
        
        if (project == null) { 
            project = new Project();
            project.setName(name);
            project.setRoot(new ProjectDirectory(root));
        } else {
            project.setName(name);
            if (!root.equals(project.getRoot().getPath())) {
                project.setRoot(new ProjectDirectory(root));
                project.setLoaded(false);
                project.load();
            }
        }
        
        return OK;
    }
    
    /** Load the GUI components of the dialog. */
    private void loadGUI() {
        
        // Ignores the close window button
        addWindowListener( 
            new WindowAdapter() {
                public void windowClosing() { }
            } 
        );
        
        // Builds the dialog
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(3,3,3,3);
        getContentPane().setLayout(gridbag);
        
        // Project name
        
        JLabel label = new JLabel("Project name:");
        gc.weightx = 0;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gridbag.setConstraints(label,gc);
        getContentPane().add(label);
        
        projName = new JTextField();
        gc.weightx = 1;
        gc.gridx = 1;
        gc.gridy = 0;
        gc.gridwidth = 2;
        gridbag.setConstraints(projName,gc);
        getContentPane().add(projName);
        
        // Project root
        label = new JLabel("Root directory:");
        gc.weightx = 0;
        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = 1;
        gridbag.setConstraints(label,gc);
        getContentPane().add(label);

        projRoot = new JTextField();
        projRoot.setEnabled(false);
        projRoot.setPreferredSize(
            new Dimension(50, (int)projRoot.getPreferredSize().getHeight())
        );
        
        gc.weightx = 1;
        gc.gridx = 1;
        gc.gridy = 1;
        gc.gridwidth = 1;
        gridbag.setConstraints(projRoot,gc);
        getContentPane().add(projRoot);

	
	
        chooseRoot = new JButton("Choose");   
        chooseRoot.addActionListener(this);
        gc.weightx = 0;
        gc.gridx = 2;
        gc.gridy = 1;
        gc.gridwidth = 1;
        gridbag.setConstraints(chooseRoot,gc);
        getContentPane().add(chooseRoot);

        // URL Root for web projects.  Used to launch files in web browser against webserver
	
	label = new JLabel("Web URL Root(optional)");
	gc.weightx = 0;
        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 1;
	gridbag.setConstraints(label, gc);
	
	getContentPane().add(label);
	projURLRoot = new JTextField();
	
	gc.weightx = 1;
        gc.gridx = 1;
        gc.gridy = 2;
        gc.gridwidth = 2;
	gridbag.setConstraints(projURLRoot, gc);
	getContentPane().add(projURLRoot);

	// Lower buttons
        
        JPanel panel = new JPanel();
        gc.weightx = 1;
        gc.gridx = 0;
        gc.gridy = 3;
        gc.gridwidth = 3;
        gridbag.setConstraints(panel,gc);
        getContentPane().add(panel);
        
        
        updateProject = new JButton("OK");
        updateProject.addActionListener(this);
        panel.add(updateProject);
        
        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        panel.add(cancel);
        
        updateProject.setPreferredSize(cancel.getPreferredSize());
   
        // Finishing
        setResizable(false);
        setSize(new Dimension(350,180));
    }
    
} 
