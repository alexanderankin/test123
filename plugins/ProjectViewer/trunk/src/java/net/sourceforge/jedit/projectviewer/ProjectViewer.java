/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sourceforge.jedit.projectviewer;


//the standard swing stuff
import javax.swing.*;
import javax.swing.tree.*;

//awt stuff for swing support
import java.awt.*;


import java.awt.event.*;  // required for KeyListener and ActionListener
import javax.swing.event.*;


//required for jEdit use
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

import java.net.*;
import java.util.Vector;

import net.sourceforge.jedit.pluginholder.HoldablePlugin;
import net.sourceforge.jedit.pluginholder.Config;

/**

A Project Viewer plugin for jEdit.

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Revision$
*/

public class ProjectViewer extends HoldablePlugin implements ResourceListener, WindowListener {


    //project constants...
    public  final static String NAME             = "ProjectViewer";
    public  final static String PRODUCT          = "Project Viewer";
    public  final static String VERSION          = "1.0.1";
            final static String ALL_PROJECTS     = "All Projects";
    private final static String NO_FILES         = "No files...";
    private final static String NO_PROJECTS      = "No projects found...";

            final static String FOLDERS          = "Folders";
            final static String FILES            = "Files";
            final static String WORKING_FILES    = "Working Files";
            final static String OPTIONS          = "Options";



            final static int DISPLAY_FOLDERS        = 0;
            final static int DISPLAY_FILES          = 1;
            final static int DISPLAY_WORKING_FILES  = 2;




    JTabbedPane tabs                            = new JTabbedPane();

    DefaultMutableTreeNode root                 = new DefaultMutableTreeNode("default");

    DefaultTreeModel contentsModel              = new DefaultTreeModel( root );


    JTree folders                               = new JTree( contentsModel );
    JTree files                                 = new JTree( contentsModel );
    JTree workingfiles                          = new JTree( contentsModel );
    
    JComboBox projectCombo                      = new JComboBox();

    JLabel  status = new JLabel(" ");
    JButton deleteProjectBtn;
    JButton removeFileBtn;
    JButton removeAllFilesBtn;
    JButton createProjectBtn;
    JButton addFileBtn;
    JButton importFilesBtn;
    JButton openAllBtn;
    JButton expandBtn;
    JButton contractBtn;
    JButton configBtn;

    
    /**
    The view that Launcher uses to open files...
    */    
    private View view = null;


    private ViewerListener               vsl    = null;
    
    
    private ProjectTreeSelectionListener tsl    = null;
    
    private Launcher launcher                   = null;
    
    //internal non-gui ProjectResources
    //warning:  this might cause a problem if multiple versions of this GUI are running
    DefaultMutableTreeNode currentlySelectedProject    = null;
    DefaultMutableTreeNode currentlySelectedFile       = null;    
    
    
    //FIX ME:  consider making the currentDirectory a "Directory" object
    private Directory      currentDirectory            = null;
    private Project        currentProject              = null;
            File           currentFile                 = null;
    
    private DefaultMutableTreeNode currentNode         = null;

    /**
    This is a kludge to get around a problem with JComboBox and 
    removeActionListener not working

    @version $Id$
    */
    boolean lockProjectCombo = false;

    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    DefaultMutableTreeNode getCurrentNode() {
        return this.currentNode;
    }

    void setCurrentNode(DefaultMutableTreeNode currentNode) {
        this.currentNode = currentNode;
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public File getCurrentFile() {
        if (this.currentFile == null)
            Logger.log("WARNING:  getCurrentFile() is null", 200);

        return this.currentFile;
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    void setCurrentFile(File file) {
        this.currentFile = file;
    }

    
    /**
    Returns the current jTree being looked at.. or null if no jtree is being 
    looked at...

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public JTree getCurrentTree() {

        String title = this.tabs.getTitleAt(this.tabs.getSelectedIndex());


        if (title.equals(FOLDERS)) {
            return this.folders;
        } else if (title.equals(FILES)) {
            return this.files;
        } else if (title.equals(WORKING_FILES)){
            return this.workingfiles;
        }
        
        return null;
    }
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */



    /**
    Since projects can have root directories and subdirectories.. This 
    will return the currently selected one.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public Directory getCurrentDirectory() {
        if (this.currentDirectory == null)
            Logger.log("WARNING:  getCurrentDirectory() is null", 200);

        return this.currentDirectory;
    }


    /**
    Change the currently selected directory.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    void setCurrentDirectory(Directory directory) {
        this.currentDirectory = directory;
    }
    
    /**
    Selecte a project for viewing... this is done via the pulldown JComboBox
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    void selectProject(String project) {

        if ( project == null ||  project.equals(this.ALL_PROJECTS) ) {

            this.setCurrentProject(null);
            this.loadAllProjects();

        } else {               

            Project p = ProjectResources.getProject( project );
            this.setCurrentProject( p );
            this.loadProject( p );
        }
        
    }

    

    /**
    @return Project The currently selected project within the JTree or null
            if no project is selected
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public Project getCurrentProject() {
        return this.currentProject;
    }




    /**
    Set the project that the user is working with.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    void setCurrentProject(Project project) {
        this.currentProject = project;
    }


    /**
    A standard way to initialize the buttons...

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    private JButton initButton(String icon, String tooltip) {

        URL url = this.getClass().getResource( icon );
        JButton init = new JButton( new ImageIcon(url) );

        Insets zeroMargin = new Insets(0, 0, 0, 0);
        init.setMargin(zeroMargin);

        init.setToolTipText( tooltip );

        init.addActionListener(this.vsl);

        return init;
    }

    private void initTrees() {

        initTree(this.folders);
        initTree(this.files);
        initTree(this.workingfiles);

    }

    
    
    private void initTree(JTree tree) {
        
        tree.getSelectionModel().setSelectionMode
            (TreeSelectionModel.SINGLE_TREE_SELECTION);


        tree.setCellRenderer( new TreeRenderer() );
        //tree.setScrollsOnExpand( true );

        this.tsl = new ProjectTreeSelectionListener(this, this.launcher);  



        ToolTipManager.sharedInstance().registerComponent(tree);

    }
    
    
    /**
    loads the GUI of Project Viewer


    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    void loadGUI() {

        if (this.view != null) {
            this.view.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        }
        
       
        this.launcher = new Launcher( this.view, this);
        

        ProjectResources.setUseMemory(true);
        ProjectResources.parse();
        
        this.initTrees();        

        //tell the projectCombo what to do when someone changes an item...

        JPanel bar = new JPanel(new BorderLayout());
           
        //initialize the toolbar...
        JPanel actPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));


        this.vsl = new ViewerListener(this, this.launcher);

        deleteProjectBtn = this.initButton("/icons/DeleteProject.gif", "Delete project");
        removeFileBtn = this.initButton("/icons/RemoveFile.gif", "Remove file");
        removeAllFilesBtn = this.initButton("/icons/RemoveAllFiles.gif", "Remove all files");
        createProjectBtn = this.initButton("/icons/CreateProject.gif", "Create project");
        addFileBtn = this.initButton("/icons/AddFile.gif", "Add file to project");
        importFilesBtn = this.initButton("/icons/Import.gif", "Import files into this project");
        openAllBtn = this.initButton("/icons/OpenAll.gif", "Open all files in this project");
        expandBtn = this.initButton("/icons/Expand.gif", "Expand the file list");
        contractBtn = this.initButton("/icons/Contract.gif", "Contract the file list");
        configBtn = this.initButton("/icons/Config.gif", "Configure Project Viewer");        

        //init 
        
        actPanel.add(createProjectBtn);
        actPanel.add(deleteProjectBtn);
        actPanel.add(expandBtn);
        actPanel.add(contractBtn);
        actPanel.add(openAllBtn);
        actPanel.add(addFileBtn);
        actPanel.add(importFilesBtn);
        actPanel.add(removeFileBtn);
        actPanel.add(removeAllFilesBtn);

        //actPanel.add(configBtn);        
        
        //set the default state of the toggleable buttons...
        removeFileBtn.setEnabled(false);
        removeAllFilesBtn.setEnabled(false);
        addFileBtn.setEnabled(false);
        deleteProjectBtn.setEnabled(false);
        importFilesBtn.setEnabled(false);        
        openAllBtn.setEnabled(false);
        expandBtn.setEnabled(true);
        contractBtn.setEnabled(true);
        
        bar.add(actPanel, BorderLayout.NORTH);
        bar.add(projectCombo, BorderLayout.SOUTH);


        
        


        //ok... now create a JPanel for placing the bar and the tree into and then stick that into
        //tabs...
        JPanel allComponents = new JPanel(new BorderLayout());



        allComponents.add(bar, BorderLayout.NORTH);



        tabs.addChangeListener( new TabViewListener( this ) );


        tabs.addTab(FOLDERS, new JScrollPane( folders ));
        tabs.addTab(FILES, new JScrollPane( files ));
        tabs.addTab(WORKING_FILES, new JScrollPane( workingfiles ));

        allComponents.add(tabs, BorderLayout.CENTER);



        //ok.. add the bar to the tab...
        //contentPanel.getContentPane().add(bar, BorderLayout.NORTH);
        this.add(allComponents, BorderLayout.CENTER);
        this.add(status, BorderLayout.SOUTH);
        
        
        //initialize the GUI.
        refresh();

        projectCombo.addActionListener ( vsl );

        //register the jtree for tooltips...


        //contentPanel.setSize(new Dimension(300, 600) );

        ProjectResources.setUseMemory(false);

        this.setVisible(true);

        if (this.view != null) {
            this.view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

    }
    

    void setStatus(String status) {
        this.status.setText(status);
    }

    /**
    Reload ProjectResources and then reparse the object tree.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void refresh() {



        //ProjectResources.parse();

        this.loadProjectCombo();

        if (this.getCurrentProject() == null) {
            this.loadAllProjects();
        } else {
            this.loadProject( this.getCurrentProject() );
        }

    }

    
    
    /**
    Removes all but the root for you...

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    private void reInitTrees() {

        reInitTree(this.folders);
        reInitTree(this.files);
        reInitTree(this.workingfiles);
        
    }

    private void reInitTree(JTree tree) {

        tree.removeTreeSelectionListener( tsl );
        tree.removeMouseListener(tsl);
        tree.setModel(null);


        //clear the JTree...
        //tree.removeAllChildren();


        DefaultTreeModel model = new DefaultTreeModel( new DefaultMutableTreeNode() );

        model.reload();
        tree.setModel( model );


        if (tsl != null) {

            tree.removeTreeSelectionListener( this.tsl );
            tree.removeMouseListener(this.tsl);

        } else {
            this.tsl =  new ProjectTreeSelectionListener(this, this.launcher);            
        }


        tree.addTreeSelectionListener( this.tsl );
        tree.addMouseListener(this.tsl);


    }
    


    /**
    Build a jTree for all the projects...

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    void buildAllTrees(JTree tree, int mode) {
        

        DefaultMutableTreeNode root = new DefaultMutableTreeNode( ALL_PROJECTS );
        root.setUserObject( ALL_PROJECTS );


        Project[] projects = ProjectResources.getProjects();

        if (projects.length == 0)
            root.setUserObject( NO_PROJECTS );
            
            
        //add all the projects...
        for (int i = 0; i < projects.length; ++i) {
            root.add( this.getProjectNode2( projects[i], mode ) );

        }

        ((DefaultTreeModel)tree.getModel()).reload();
        ((DefaultTreeModel)tree.getModel()).setRoot(root);

        
    }
    
 
    /**
    Load all projects into the editor

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void loadAllProjects() {

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));


        //remove the current project from the list

        /*
        if (this.getCurrentProject() != null) {
            this.launcher.closeProject( this.getCurrentProject() );
        }
        */
        
        this.reInitTrees();

        this.buildAllTrees(folders, DISPLAY_FOLDERS);
        this.buildAllTrees(files, DISPLAY_FILES);
        this.buildAllTrees(workingfiles, DISPLAY_WORKING_FILES);


        this.reloadAllTrees();
        
        removeFileBtn.setEnabled(false);
        removeAllFilesBtn.setEnabled(false);
        addFileBtn.setEnabled(false);
        deleteProjectBtn.setEnabled(false);
        importFilesBtn.setEnabled(false);
        openAllBtn.setEnabled(false);
        
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
    }

    /**
    Creates an initialzed project combo and adds all known projects
    

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    static synchronized JComboBox getProjectCombo(JComboBox combo) {

        //under JDK 1.2 Swing appears to have a bug where it can't removeAllItems when
        //none are in there... so just add one even if it is going to be removed
        combo.addItem(ALL_PROJECTS);
        
        combo.removeAllItems();

        //add just the basic one...
        combo.addItem(ALL_PROJECTS);

        Project[] comboEnterProjects = ProjectResources.getProjects();

        
        for (int i = 0; i < comboEnterProjects.length; ++i) {
               combo.addItem( comboEnterProjects[i].get());
        }

        return combo;

    }
    
    /**
    

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    void loadProjectCombo() {

        this.lockProjectCombo = true;


        ProjectViewer.getProjectCombo(this.projectCombo);

        if (ProjectResources.getProjects().length == 0) {
            this.root.setUserObject( NO_PROJECTS );
        }

        
        this.lockProjectCombo = false;
        
    }

    /**
    Returns a JTree node for a given project.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    private DefaultMutableTreeNode getProjectNode2(Project project, int mode) {


        DefaultMutableTreeNode node;

        if (mode == DISPLAY_FOLDERS ) {

            project.getRoot().setProject(project);
            node = getNode( project.getRoot() );


        } else {

            node = getFlatNode(project, mode);

        }

        if (node != null) {
            node.setUserObject(project);
        } else {
            node = new DefaultMutableTreeNode ( project );
        }

        return node;
    }



    /**
    When in non DISPLAY_FOLDERS mode... use this method.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    private DefaultMutableTreeNode getFlatNode(Project project, int mode) {


        DefaultMutableTreeNode node = new DefaultMutableTreeNode(project);


        File[] files = ProjectResources.getFiles( project );

        if (files.length == 0) {
            node.add(new DefaultMutableTreeNode( NO_FILES ) );
            return node;
        }

        
        for (int i = 0; i < files.length; ++i ) {
            
            if (mode == DISPLAY_WORKING_FILES ) {

                if ( files[i].isSubscribed() ) {

                    DefaultMutableTreeNode file = new DefaultMutableTreeNode( files[i] );
                    file.setUserObject( files[i] );
                    node.add(file);

                }

            } else {

                DefaultMutableTreeNode file = new DefaultMutableTreeNode( files[i] );
                file.setUserObject( files[i] );
                node.add(file);

            }


        }

        return node;

    }
    



    /**
    Returns all nodes (files and sub - directories) for a given directory or
    null if it has no files or directories.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    private DefaultMutableTreeNode getNode(Directory directory) {

        //Logger.log( "working with dir" + directory, 200 );

        DefaultMutableTreeNode current = new DefaultMutableTreeNode(directory);
        current.setUserObject(directory);

        File[] files = ProjectResources.getFiles(directory.getProject(), directory);

        //if (files.length == 0) {
        //    current.add( new DefaultMutableTreeNode(NO_FILES) );
        //}

        //get the directories... 
        Directory[] children = directory.getChildren( directory.getProject().getDirectories() );


        //Logger.log("num children: "
        //if this directory doesn't have any nodes return null
        if (children.length == 0 && files.length == 0) {
            return null;
        }

        for (int i = 0; i < children.length; ++i ) {

            DefaultMutableTreeNode node = getNode( children[i] );
            if (node != null)
                current.add(node);

        }

        for (int i = 0; i < files.length; ++i ) {
            
            DefaultMutableTreeNode file = new DefaultMutableTreeNode( files[i] );
            file.setUserObject( files[i] );
            current.add(file);
            
        }

        return current;
        
    }
    
    /**
    Get all subnodes of this project...

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    private DefaultMutableTreeNode[] getProjectNodes(Project project) {

        
        Vector rootVector = new Vector();
        
        //ok.. add the directories
        Directory[] directories = project.getDirectories();

        if (directories.length == 0) 
            rootVector.addElement( new DefaultMutableTreeNode( NO_FILES ) );

            
        for (int i = 0; i < directories.length; ++i) {

            //if the current directory is an offshoot of the project directory... 
            //don't include the fullpath.
            Directory current = directories[i];

            //replace the begining of the path with "."
            
            String dirName = current.get();

            int homeIndex = current.get().indexOf(project.getRoot().toString());
            if (homeIndex > -1) {
                String newDir = current.get().substring( project.getRoot().toString().length(), current.get().length() );
                newDir = "." + newDir;
            } 

            
            DefaultMutableTreeNode dir = new DefaultMutableTreeNode( current );

        
            File[] files = ProjectResources.getFiles(project, current);
    
            if (files.length == 0) 
                root.add( new DefaultMutableTreeNode( NO_FILES ) );

            for (int j = 0; j < files.length; ++j) {

                DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode( files[j].getFileName() );
                fileNode.setUserObject(files[j]);
                dir.add( fileNode );
                
            }

            rootVector.addElement( dir );

            
        }
        
        DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[rootVector.size()];
        rootVector.copyInto(nodes);
        return nodes;
        
    }



    /**
    Given a project and a mode... load up the correct JTree...

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    void buildTree(Project project, int mode) {

        
        if (mode == DISPLAY_FOLDERS) {
            
            ((DefaultTreeModel)this.folders.getModel()).setRoot( this.getProjectNode2(project, DISPLAY_FOLDERS) );

        } else if (mode == DISPLAY_FILES) {

            DefaultMutableTreeNode root = this.getProjectNode2(project, DISPLAY_FILES);
            ((DefaultTreeModel)this.files.getModel()).setRoot( root );
            this.vsl.expand(root, this.files);

            
        } else if (mode == DISPLAY_WORKING_FILES) {

            DefaultMutableTreeNode root = this.getProjectNode2(project, DISPLAY_WORKING_FILES);
            ((DefaultTreeModel)this.workingfiles.getModel()).setRoot( root );
            this.vsl.expand(root, this.workingfiles);

            
        }



        
    }

    
    /**
    Load a project into jEdit

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void loadProject(Project project) {

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));        
        
        this.setCurrentProject( project );
        this.setCurrentDirectory( project.getRoot() );
        
        this.reInitTrees();
        

        this.buildTree(project, DISPLAY_FOLDERS);
        this.buildTree(project, DISPLAY_FILES);
        this.buildTree(project, DISPLAY_WORKING_FILES);        
        


        //launcher.launchProject( project );
        

        //reload all trees
        this.reloadAllTrees();
        
        removeFileBtn.setEnabled(false);
        removeAllFilesBtn.setEnabled(true);
        addFileBtn.setEnabled(true);
        deleteProjectBtn.setEnabled(true);
        importFilesBtn.setEnabled(true);
        openAllBtn.setEnabled(true);
        
        //set the current project node...
        this.currentlySelectedProject = this.root;

        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    }

    
    /**
    Used to refresh all jtrees.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    private void reloadAllTrees() {
        

        ((DefaultTreeModel)this.folders.getModel()).reload();
        this.folders.repaint();

        ((DefaultTreeModel)this.files.getModel()).reload();
        this.files.repaint();

        ((DefaultTreeModel)this.workingfiles.getModel()).reload();
        this.workingfiles.repaint();

        
    }

    /**
    Return the view that this project viewer is working with
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public View getView() {
        return this.view;
    }

    public void setView( View view ) {
        this.view = view;
    }
    

    /**
    Init this holdable plugin

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public void init(Config config){

        this.setView( config.getView() );
        
        this.loadGUI();
        ProjectResources.addResourceListener( this );
    }

    
    

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public JPanel getContent() {
        this.setEnabled(true);
        this.setVisible(true);
        //this.setSize(new Dimension(300, 600) );
        //this.show();
        return this;
    }


    
    /**
    Handle any buffer updates or closes and notify the Project Viewer instance
    that is running.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    
    */
    public void handleMessage( EBMessage message) {

        if (message instanceof BufferUpdate) {

            BufferUpdate update = (BufferUpdate)message;


            if (update.getWhat().equals(BufferUpdate.CREATED)) {
                //Logger.log("buffer created", 9);
            }

            if (update.getWhat().equals(BufferUpdate.CLOSED)) {
                
                String file = update.getBuffer().getPath();

                //Logger.log("buffer closed:  " + file, 9);                
                
                if ( ProjectResources.isSubscribedFile( file ) ) {
                    ProjectResources.removeSubscribedFile( file );
                    //Logger.log( "removing file:  " + file, 9 );
                }


            }

        }
        
    }
    
    
    /**
    Get the temporary directory.  It will be returned in the format of <dir>.  
    Sometimes under different platforms it may have a trailing / and this should 
    be trimmed.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public String getTempDir() {
        //java.io.tmpdir
        //file.separator

        String temp = System.getProperty("java.io.tmpdir");
        //if () {

        //}
        return temp;

    }

    //ResourceListener interface

    public void fileSubscribed(File file) {
    }

    public void fileUnSubscribed(File file) {
        this.getCurrentTree().repaint();
    }

    //WindowListener interface
    public void windowActivated(WindowEvent e) {}
        
    public void windowClosed(WindowEvent e) {}
    
    public void windowClosing(WindowEvent e) {
        ProjectResources.removeResourceListener( this );
    }

    public void windowDeactivated(WindowEvent e) {} 

    public void windowDeiconified(WindowEvent e) {} 

    public void windowIconified(WindowEvent e) {}

    public void windowOpened(WindowEvent e) {}
    

}

