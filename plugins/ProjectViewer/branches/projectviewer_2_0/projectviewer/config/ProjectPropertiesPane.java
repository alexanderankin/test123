/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package projectviewer.config;

//{{{ Imports
// Import Java
import java.io.File;

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
import javax.swing.WindowConstants;

// Import jEdit
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.AbstractOptionPane;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;
//}}}

/**
 *  A dialog for configuring the properties of a project.
 *
 *  @author	 Marcelo Vanzin
 *  "	 "	 Matt Payne (made slight changes for urlRoot
 */
public class ProjectPropertiesPane extends AbstractOptionPane implements ActionListener {

	//{{{ Instance Variables
	
	private int result;
	private VPTProject project;
	
	private JTextField projName;
	private JTextField projRoot;
	private JTextField projURLRoot;
	
	private JButton	chooseRoot;
	
	private boolean ok;

	//}}}
	
	//{{{ Constructors
	
	/** Builds the dialog. */
	public ProjectPropertiesPane(VPTProject p) {
		super("projectviewer.project_props");
		this.project = p;
		this.ok = true;
	}
	
	//}}}
	
	//{{{ actionPerformed(ActionEvent) method
	/**
	 *  Shows a file chooser so the user can choose the root directory of
	 *  its project. In case the user chooses a directory, the corresponding
	 *  JTextField is updated to show the selection.
	 */
	public void actionPerformed(ActionEvent ae) {	
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Enter the root directory for the project:");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		String root = projRoot.getText().trim();
		if (root.length() > 0) {
		   chooser.setSelectedFile(new File(root));
		}
		
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			root = chooser.getSelectedFile().getAbsolutePath();
			projRoot.setText(root);
			projRoot.setToolTipText(projRoot.getText());
		}

	} //}}}
	
	//{{{ _save() method
	/** Updates the project with the info supplied by the user. */
	protected void _save() {
		String name = projName.getText().trim();
		
		if (name.length() == 0) {
			JOptionPane.showMessageDialog(
				this, 
				"Please specify a name for the project.",
				"Error: no name supplied",
				JOptionPane.ERROR_MESSAGE
			 );
			 ok = false;
		} else {
			ok = true;
		}
		
		//TODO: check if project name already exists
		
		String root = projRoot.getText().trim();
		if (root.length() == 0) {
			JOptionPane.showMessageDialog(
				this, 
				"Please specify a root for the project.",
				"Error: no root supplied",
				JOptionPane.ERROR_MESSAGE
			 );
			 ok = false;
		}
		
		//TODO: check if root exists
		
		String urlRoot = projURLRoot.getText().trim();
	
		if (ok) {
			project.setName(name);
			project.setRootPath(root);
			project.setURL(urlRoot);
		}
	} //}}}
	
	//{{{ _init() method
	/** Load the GUI components of the dialog. */
	protected void _init() {
		
		// Builds the dialog
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(3,3,3,3);
		setLayout(gridbag);
		
		// Project name
		
		JLabel label = new JLabel("Project name:");
		gc.weightx = 0;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gridbag.setConstraints(label,gc);
		add(label);
		
		projName = new JTextField();
		projName.setText(project.getName());
		gc.weightx = 1;
		gc.gridx = 1;
		gc.gridy = 0;
		gc.gridwidth = 2;
		gridbag.setConstraints(projName,gc);
		add(projName);
		
		// Project root
		label = new JLabel("Root directory:");
		gc.weightx = 0;
		gc.gridx = 0;
		gc.gridy = 1;
		gc.gridwidth = 1;
		gridbag.setConstraints(label,gc);
		add(label);

		projRoot = new JTextField();
		projRoot.setText(project.getRootPath());
		projRoot.setToolTipText(projRoot.getText());
		projRoot.setEnabled(false);
		projRoot.setPreferredSize(
			new Dimension(50, (int)projRoot.getPreferredSize().getHeight())
		);
		
		gc.weightx = 1;
		gc.gridx = 1;
		gc.gridy = 1;
		gc.gridwidth = 1;
		gridbag.setConstraints(projRoot,gc);
		add(projRoot);
	
	
		chooseRoot = new JButton("Choose");   
		chooseRoot.addActionListener(this);
		gc.weightx = 0;
		gc.gridx = 2;
		gc.gridy = 1;
		gc.gridwidth = 1;
		gridbag.setConstraints(chooseRoot,gc);
		add(chooseRoot);

		// URL Root for web projects.  Used to launch files in web browser against webserver
	
		label = new JLabel("Web URL Root(optional)");
		label.setToolTipText("sets the URL for a web project e.g. http://<projecturl>");
		gc.weightx = 0;
		gc.gridx = 0;
		gc.gridy = 2;
		gc.gridwidth = 1;
		gridbag.setConstraints(label, gc);
	
		add(label);
		projURLRoot = new JTextField();
		projURLRoot.setText(project.getURL());
		projURLRoot.setToolTipText(project.getURL());
	
		gc.weightx = 1;
		gc.gridx = 1;
		gc.gridy = 2;
		gc.gridwidth = 2;
		gridbag.setConstraints(projURLRoot, gc);
		add(projURLRoot);

		setPreferredSize(new Dimension(300,250));
	} //}}}
	
	boolean isOK() { return ok; }
}

