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
package projectviewer.tree;

// Import Swing/AWT
import java.io.File;
import java.util.Hashtable;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;

import javax.swing.JTree;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javax.swing.tree.TreePath;

// Import jEdit
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;

// Import ProjectViewer
import projectviewer.Project;
import projectviewer.ProjectFile;
import projectviewer.ProjectViewer;
import projectviewer.RemovalManager;
import projectviewer.ProjectDirectory;
import projectviewer.ProjectFileImporter;

import projectviewer.config.ProjectPropertiesDlg;
import projectviewer.config.ProjectViewerConfig;
/**
 *  <p>Listener for mouse events in nodes of the tree. This listener is
 *  responsible for building the context-menus for right button clicks
 *  in the nodes of the tree.</p>
 *
 *  @author     Marcelo Vanzin
 */
public class TreeContextMenuListener extends MouseAdapter implements ActionListener {

    //--------------- Instance Variables
    
    private final ProjectViewer viewer;
    
    private JPopupMenu projectMenu;
    private JMenuItem  properties;
    private JMenuItem  reimport;
    private JMenuItem  removeProject;
    
    private JPopupMenu dirMenu;
    private JMenuItem  removeDir;
    private JMenuItem  deleteDir;
    private JMenuItem  renameDir;
    
    private JPopupMenu fileMenu;
    private JMenuItem  removeFile;
    private JMenuItem  deleteFile;
    private JMenuItem  renameFile;
    private JMenuItem  miLaunchBrowser;
    
    private JPopupMenu multipleSelMenu;
    private JMenuItem  removeMulti;
    private JMenuItem  deleteMulti;
    
    //--------------- Constructors
    
    /**
     *  Constructs a listener that will ask the provided viewer instance for
     *  information about the nodes clicked.
     */
    public TreeContextMenuListener(ProjectViewer viewer) {
        this.viewer = viewer;
        loadGUI();
    }
    
    //--------------- Public Methods
    
    /** Context-menus are shown on the "pressed" event. */
    public void mousePressed(MouseEvent me) {
        JTree tree = (JTree) me.getSource();

        if (SwingUtilities.isRightMouseButton(me)) {
            TreePath tp = tree.getClosestPathForLocation(me.getX(),me.getY());
            if (tp != null && !tree.isPathSelected(tp)) {
                if ((me.getModifiers() & MouseEvent.CTRL_MASK) == MouseEvent.CTRL_MASK) {
                    tree.addSelectionPath(tp);
                } else {
                    tree.setSelectionPath(tp);
                }
            }
        }

        if (me.isPopupTrigger()) {
            handleMouseEvent(me);
        }
    }
    
    /** Context-menus are shown on the "pressed" event. */
    public void mouseReleased(MouseEvent me) {
        if (me.isPopupTrigger()) {
            handleMouseEvent(me);
        }
    }

    /** Listener for actions on the JMenuItems of the popup menus. */
    public void actionPerformed(ActionEvent ae) {
        JMenuItem src = (JMenuItem) ae.getSource();
        
        if (src == properties) {
            ProjectPropertiesDlg.run(viewer,viewer.getCurrentProject(),true);
        } else if (src == reimport) {
            Project p = viewer.getCurrentProject();
            p.removeAllFiles();
            new ProjectFileImporter(viewer).doImport(p.getRoot().toFile());
        } else if (src == renameDir) {
            renameDirectory();
        } else if (src == renameFile) {
            renameFile();
	    } else if (src == miLaunchBrowser) {
           launchBrowser();
        } else if (src == removeProject ||
                   src == removeDir ||
                   src == removeFile ||
                   src == removeMulti) {
            // Removes nodes (from projects, or projects themselves)
            new RemovalManager(viewer).handleSelectionRemoval(false);
        } else if (src == deleteFile ||
                   src == deleteDir ||
                   src == deleteMulti) {
            // Deletes nodes from disk
            // (projects are only removed, nothing is deleted!)
            new RemovalManager(viewer).handleSelectionRemoval(true);
        }
        
    }
    
    //--------------- Private Methods

    private void launchBrowser() {
        /* need to get browser setting */
        String sURLRoot = viewer.getCurrentProject().getURLRoot();
        String sURL;
        String browserExecPath = ProjectViewerConfig.getInstance().getBrowserPath();
        if (sURLRoot == "" )
        {
            JOptionPane.showMessageDialog(viewer, "Web URL Not set for project");
            return;	
        }
    
        if (viewer.isFileSelected())
        {
            ProjectFile fileToView = viewer.getSelectedFile();
            
            /* Produce the url of the file based upon the projects urlRoot */
            sURL = sURLRoot + fileToView.getPath().toString().substring(viewer.getCurrentProject().getRoot().getPath().length());
            
            //JOptionPane.showMessageDialog(viewer, sURL);
            
            Runtime rt = Runtime.getRuntime();
            String[] callAndArgs = { browserExecPath, sURL };
            try {
               Process child = rt.exec(callAndArgs);
               child.wait(4);
               System.out.println("Process exit code is: " + child.exitValue());
               }
            catch(java.io.IOException e) {
            System.err.println(
            "IOException starting process!");
            }
            catch(InterruptedException e) {
               System.err.println(
               "Interrupted waiting for process!");
            }
        } else { JOptionPane.showMessageDialog(viewer, "No File selected");}	

    }
    
    /** Handles the mouse event internally. */
    private void handleMouseEvent(MouseEvent me) {
        JTree tree = viewer.getCurrentTree();
        
        if (tree.getSelectionCount() == 0) {
            return;
        } else if (tree.getSelectionCount() > 1) {
            // Multiple selections.
            multipleSelMenu.show(me.getComponent(), me.getX(), me.getY());
        } else {
            // Single selection, detects the node type and shows
            // the corresponding popup menu.
            Object node = viewer.getSelectedNode();
            
            if (node instanceof Project) {
                projectMenu.show(me.getComponent(), me.getX(), me.getY());
            } else if (node instanceof ProjectDirectory) {
                dirMenu.show(me.getComponent(), me.getX(), me.getY());
            } else if (node instanceof ProjectFile) {
                fileMenu.show(me.getComponent(), me.getX(), me.getY());
            }
        }
    }
    
    /** Constructs the menus' GUI. */
    private void loadGUI() {
        JMenuItem tmp;
        Font font = new Font("Arial", Font.ITALIC, 10);
        
        // Project menu
        projectMenu = new JPopupMenu();
        tmp = new JMenuItem("Selected project");
        tmp.setEnabled(false);
        tmp.setFont(font);
        projectMenu.add(tmp);
        projectMenu.addSeparator();
        
        properties = new JMenuItem("Properties");
        properties.addActionListener(this);
        projectMenu.add(properties);
        
        reimport = new JMenuItem("Re-Import Files");
        reimport.addActionListener(this);
        projectMenu.add(reimport);
        
        removeProject = new JMenuItem("Remove project");
        removeProject.addActionListener(this);
        projectMenu.add(removeProject);
        
        // Directory menu
        dirMenu = new JPopupMenu();
        tmp = new JMenuItem("Selected directory");
        tmp.setEnabled(false);
        tmp.setFont(font);
        dirMenu.add(tmp);
        dirMenu.addSeparator();
        
        removeDir = new JMenuItem("Remove from project");
        removeDir.addActionListener(this);
        dirMenu.add(removeDir);
        
        deleteDir = new JMenuItem("Delete from disk");
        deleteDir.addActionListener(this);
        dirMenu.add(deleteDir);
        
        renameDir = new JMenuItem("Rename");
        renameDir.addActionListener(this);
        dirMenu.add(renameDir);
        
        // File menu
        fileMenu = new JPopupMenu();
        tmp = new JMenuItem("Selected file");
        tmp.setEnabled(false);
        tmp.setFont(font);
        fileMenu.add(tmp);
        fileMenu.addSeparator();
        
        removeFile = new JMenuItem("Remove from project");
        removeFile.addActionListener(this);
        fileMenu.add(removeFile);
        
        deleteFile = new JMenuItem("Delete from disk");
        deleteFile.addActionListener(this);
        fileMenu.add(deleteFile);
        
        renameFile = new JMenuItem("Rename");
        renameFile.addActionListener(this);
        fileMenu.add(renameFile);
	
	    // sutter2k: need to tap in here for preview in browser
        miLaunchBrowser= new JMenuItem("Preview in Browser");
        miLaunchBrowser.addActionListener(this);
        fileMenu.add(miLaunchBrowser);
	
        // Menu to show when multiple nodes are selected
        multipleSelMenu = new JPopupMenu();
        tmp = new JMenuItem("Multiple selection");
        tmp.setEnabled(false);
        tmp.setFont(font);
        multipleSelMenu.add(tmp);
        multipleSelMenu.addSeparator();
        
        removeMulti = new JMenuItem("Remove from project");
        removeMulti.addActionListener(this);
        multipleSelMenu.add(removeMulti);
        
        deleteMulti = new JMenuItem("Delete from disk");
        deleteMulti.addActionListener(this);
        multipleSelMenu.add(deleteMulti);
        
    }
    
    /** 
     *  Renames the currently selected file in the viewer, both in the project
     *  view and on disk. To update the view, the file is removed from the
     *  project, and a new ProjectFile is added.
     */
    private void renameFile() {
        ProjectFile file = (ProjectFile) viewer.getSelectedNode();
        String newName = (String)
            JOptionPane.showInputDialog(
                viewer,
                "Enter the new name of the file:",
                "Rename file",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                file.toFile().getName()
            );
            
        if (newName != null) {
            Project p = viewer.getCurrentProject();
            
            File oldFile = file.toFile();

            Buffer b = jEdit.getBuffer(oldFile.getAbsolutePath());
            if (b != null) {
                jEdit.closeBuffer(viewer.getView(),b);
            }
            
            File newFile = new File(oldFile.getParent() + File.separator + newName);
            if (!oldFile.renameTo(newFile)) {
                JOptionPane.showMessageDialog(
                    viewer,
                    "Could not rename selected file!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
            
            if (b != null) {
                jEdit.openFile(viewer.getView(),newFile.getAbsolutePath());
            }
            
            p.removeFile(file);
            p.importFile(new ProjectFile(newFile.getAbsolutePath()));
        }
    }
    
    /**
     *  Renames a directory in a project. All files below the modified directory
     *  are also modified to point to the new directory name.
     */
    private void renameDirectory() {
        ProjectDirectory dir = (ProjectDirectory) viewer.getSelectedNode();
        String oldName = dir.toFile().getName();
        String newName = (String)
            JOptionPane.showInputDialog(
                viewer,
                "Enter the new name of the directory:",
                "Rename directory",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                oldName
            );
            
        if (newName != null && !oldName.equals(newName)) {
            if (!dir.changeName(newName)) {
                JOptionPane.showMessageDialog(
                    viewer,
                    "Could not rename selected file!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } else {
                viewer.refresh();
            }
        }
    
    }
}
