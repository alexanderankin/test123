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
package net.sourceforge.jedit.projectviewer;



//standard GUI stuff
import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.*;

//standard Java stuff
import java.util.*;


/**
Listen to all buttons and GUI events and respond to them.  

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
@version $Revision$
*/


public class ViewerListener implements ActionListener {

    private ProjectViewer       viewer          = null;
    private Launcher            launcher        = null;

    
    public ViewerListener(ProjectViewer instance, Launcher launcher) {

        this.viewer = instance;
        this.launcher = launcher;

    }

    /**
    Listen to specific GUI events.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    public void actionPerformed(ActionEvent evt) {

        this.viewer.setStatus(" ");

		Object source = evt.getSource();

		if (source == this.viewer.createProjectBtn) {

            this.createProject();
            
        } else if (source == this.viewer.configBtn) {
            
            this.showConfig();
            
        } else if (source == this.viewer.addFileBtn) {

            this.addFileToProject();

        } else if (source == this.viewer.removeFileBtn) {
            
            this.removeFileFromProject();

        } else if (source == this.viewer.removeAllFilesBtn) {

            this.removeAllFilesFromProject();

        } else if (source == this.viewer.deleteProjectBtn) {    

            this.deleteSelectedProject();

        } else if (source == this.viewer.importFilesBtn) {

            this.importFiles();

        } else if (source == this.viewer.openAllBtn) {

            this.openAllFilesInProject();

        } else if (source == this.viewer.expandBtn) {

            
            
            this.expand( 
                        (DefaultMutableTreeNode)((DefaultTreeModel)this.viewer.getCurrentTree().getModel()).getRoot(),
                        this.viewer.getCurrentTree()
                       );

        } else if (source == this.viewer.contractBtn) {

            this.contract( 
                         (DefaultMutableTreeNode)((DefaultTreeModel)this.viewer.getCurrentTree().getModel()).getRoot(), 
                         null 
                         );
            
        } else if (source == this.viewer.projectCombo) {

            
            if (this.viewer.lockProjectCombo)
                return;


            JComboBox combo = (JComboBox)evt.getSource();
                
                
            //don't do anything if the user selects the same project again.    
            String projectname = (String)combo.getSelectedItem();
                
            if (projectname.equals(ProjectViewer.ALL_PROJECTS) && this.viewer.getCurrentProject() == null) {
                Logger.log( "not reloading the tree",9 );
                return;
            }

            if (this.viewer.getCurrentProject() != null && projectname.equals(this.viewer.getCurrentProject().toString())) {
                Logger.log( "not reloading the tree",9 );
                return;
            }
            
                

            this.viewer.selectProject( projectname );

            
            
            
        } 
    }


    
    
    /**
    Create a new Project
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */    
    private void createProject() {



        String projectName = JOptionPane.showInputDialog(this.viewer, 
            "Please enter a project name.  You will also be prompted for a home directory." );

        if (projectName == null) 
            return;


        //if the project is the same name as another project... return false..
        
        if (ProjectResources.getProject( projectName ) != null) {
            JOptionPane.showMessageDialog(  this.viewer,
                                            "There is currently a project with this name.");
            return;
        }
        

        //ok... now prompt for a directory.

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Enter your home directory for \"" + projectName + "\"");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = chooser.showOpenDialog(this.viewer);
        
        if (returnVal == JFileChooser.CANCEL_OPTION)
            return;            

        String home = chooser.getSelectedFile().getAbsolutePath();


        //now ask them if they want to try importing files from there...
        int confirmed = JOptionPane.showConfirmDialog(  this.viewer, 
                                                        "Do you want to import files from " + home + "?", 
                                                        "Import files?", 
                                                        JOptionPane.YES_NO_OPTION );

        
                                                        
        Project project = new Project(projectName, new Directory(home));


        project = ProjectResources.addProject( project );
        project.setRoot( new Directory(home) );


        if (confirmed == JOptionPane.YES_OPTION) {


            this.viewer.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            File[] files = ImportFiles.getFiles(project, home);

            this.viewer.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            ProjectResources.addFiles( files );

        }






        this.viewer.loadProject(project);

        //add the current project to the JComboBox and then select it.
        this.viewer.projectCombo.addItem( project.toString() );

        this.viewer.projectCombo.setSelectedIndex(this.viewer.projectCombo.getItemCount() - 1 );

    }
    
    /**
    Show the config dialog to the user.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */    
    public void showConfig() {

        Logger.log("showConfig() ", 9);
        JDialog dialog = new JDialog();
        dialog.setTitle("Config");
        dialog.getContentPane().add(new ProjectViewerPane() );
        dialog.setSize( 350, 600 );
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.toFront();
        dialog.show();

        
    }
    
    
    /**
    Prompt the user to a file, get the current project, and then add the file
    to the project.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */    
    private void addFileToProject() {

        JFileChooser chooser = new JFileChooser( this.viewer.getCurrentDirectory().get() );
        //chooser.setMultiSelectionEnabled(true);
        int returnVal = chooser.showOpenDialog(this.viewer);

        
        if (returnVal != JFileChooser.APPROVE_OPTION) 
            return;


        //it appears that multiple selection mode doesn't work right.
        //come back to this in the future.
        

        java.io.File[] files = new java.io.File[1];
        files[0] = chooser.getSelectedFile();    

            
        Project project = this.viewer.getCurrentProject(); 

        //this is basically done so that in the future... when JFileChooser works
        //the above "files" array will be more than 0
        for (int i = 0; i < files.length; ++i ) {
            DefaultMutableTreeNode node = this.viewer.currentlySelectedProject;

            String fileName = files[i].getAbsolutePath();

            File file = new File(project, fileName);
    
            if (ProjectResources.getFile(file) != null) {
                JOptionPane.showMessageDialog(  this.viewer, 
                                                "Sorry, this file is already in your project.",
                                                "Files exists",
                                                JOptionPane.ERROR_MESSAGE );
                                                      
                
                return;
            }

            ProjectResources.addFile( file  );

            this.addFileToNode( node, file );
            
        }
            
        
        this.viewer.loadProject( project );

    }

    /**
    Remove the file from a project
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */    
    private void removeFileFromProject() {
        

        File file = this.viewer.getCurrentFile();

        ProjectResources.removeFile(file);
        
        //ok.... now remove the node...
        
        this.viewer.loadProject( file.getProject() );

    }


    /**
    Prompt the user if they want to remove all projects from the user.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */    
    private void removeAllFilesFromProject() {

        int answer = JOptionPane.showConfirmDialog(  this.viewer, 
                        "Are you sure you want to remove all files from the current project?",
                        "Remove all files?", 
                        JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) 
            return;
    
        ProjectResources.removeFiles(this.viewer.getCurrentProject());
        
        this.viewer.loadProject(this.viewer.getCurrentProject());
            
    }



    /**
    Delete all this project and selete all all projects.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */    
    private void deleteSelectedProject() {

        Project project = this.viewer.getCurrentProject();


        int confirmed = JOptionPane.showConfirmDialog(  this.viewer, 
                                                        "Are you sure you want to delete the project: " + project.get() + " ?", 
                                                        "Delete project?", 
                                                        JOptionPane.YES_NO_OPTION );
                                                        
        if (confirmed != JOptionPane.YES_OPTION)
            return;

                                      
        this.launcher.closeProject(project);

        ProjectResources.removeProject(project);
        
        this.viewer.loadAllProjects();

        this.viewer.loadProjectCombo();

    }

    /**
    Contract every non-leaf node within the jTree
    
    set knowNodes to null when you want to start this out..
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */    
    public void contract(DefaultMutableTreeNode node, Vector knownNodes) {

        //there is a bug/feature that only returns the root node...
        //stack the nodes in a vector and then contract all of them...

        if (knownNodes == null) {
            knownNodes = new Vector();
        }
        
    
        //don't do anything for the root node
        if (node.isRoot() == false ) {
            knownNodes.addElement(node);
        }

        //if this node holds any other nodes... expand them too.
        Enumeration children = node.children();
        
        while ( children.hasMoreElements() ) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();
            
            if (this.viewer.getCurrentTree().isExpanded( new TreePath( child.getPath() ) ) ) {
                this.contract(child, knownNodes);
            }

        }

        DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)this.viewer.getCurrentTree().getModel()).getRoot();
        
        //now when we get back to the root node... then contract everything we know of
        if (node.equals(root)  ) {

            for (int i = knownNodes.size() - 1; i >= 0; --i) {
                 
                 DefaultMutableTreeNode knownNode = (DefaultMutableTreeNode)knownNodes.elementAt(i);
                 this.viewer.getCurrentTree().collapsePath( new TreePath( knownNode.getPath() ) );
            }

        }
        
    }
    
    /**
    Expand every non-leaf node within the jTree
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */    
    public void expand(DefaultMutableTreeNode node, JTree tree) {

        //expand the given node
        tree.expandPath( new TreePath( node.getPath() ) );
        
        //if this node holds any other nodes... expand them too.
        Enumeration children = node.children();
        
        while ( children.hasMoreElements() ) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();
            
            if (child.isLeaf() == false) {
                this.expand(child, tree);
            }

        }
        
        
    }
    
    /**
    Import files using a dialog box...
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */    
    private void importFiles() {

        Project project = this.viewer.getCurrentProject(); 
        File[] importedFiles = ImportFiles.getFiles( project, this.viewer );
        
        //ok.. now go through all the imported projects... add them to the current project..
        //and load the project
        
        if (importedFiles.length == 0) {
            JOptionPane.showMessageDialog(  this.viewer, 
                                            "No files were found.", 
                                            "Note", 
                                            JOptionPane.ERROR_MESSAGE );
            return;
        }


        ProjectResources.addFiles(importedFiles);

        
        this.viewer.loadProject(project);

        JOptionPane.showMessageDialog(  this.viewer, 
                                        "Imported " + importedFiles.length + "file(s) into your project", 
                                        "Note", 
                                        JOptionPane.INFORMATION_MESSAGE );


    }


    /**
    Progmatically open all files under the current project...
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    private void openAllFilesInProject() {

        this.viewer.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        File[] files = ProjectResources.getFiles( this.viewer.getCurrentProject() ); 
        
        for( int i = 0; i < files.length; ++i ) {
            this.launcher.launchFile( files[i] );
        }

        this.viewer.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
    }


    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */    
    private void addFileToNode(DefaultMutableTreeNode node, File file) {

        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode( file );
        fileNode.setUserObject(file);
        node.add(fileNode);

    }
    


    
}
