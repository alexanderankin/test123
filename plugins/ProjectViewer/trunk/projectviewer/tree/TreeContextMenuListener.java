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

// Import ProjectViewer
import projectviewer.Project;
import projectviewer.ProjectFile;
import projectviewer.ProjectViewer;
import projectviewer.ProjectDirectory;
import projectviewer.RemovalManager;

import projectviewer.config.ProjectPropertiesDlg;

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
    
    private JPopupMenu multipleSelMenu;
    private JMenuItem  removeMulti;
    private JMenuItem  deleteMulti;
    
    //--------------- Static Methods & Variables
    
    private static TreeContextMenuListener instance = null;
    
    public static synchronized TreeContextMenuListener getInstance(ProjectViewer viewer) {
        if (instance == null) {
            instance = new TreeContextMenuListener(viewer);
        }
        return instance;
    }
    
    //--------------- Constructors
    
    /**
     *  Constructs a listener that will ask the provided viewer instance for
     *  information about the nodes clicked.
     */
    private TreeContextMenuListener(ProjectViewer viewer) {
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
            ProjectPropertiesDlg.run(viewer,viewer.getCurrentProject());
            viewer.refresh();
        } else if (src == reimport) {
            
        } else if (src == renameDir) {
            
        } else if (src == renameFile) {
            
        } else if (src == removeProject ||
                   src == removeDir ||
                   src == removeFile ||
                   src == removeMulti) {
            // Removes nodes (from projects, or projects themselves)
            RemovalManager.handleSelectionRemoval(
                viewer.getCurrentTree(),false);
        } else if (src == deleteFile ||
                   src == deleteDir ||
                   src == deleteMulti) {
            // Deletes nodes from disk
            // (projects are only removed, nothing is deleted!)
            RemovalManager.handleSelectionRemoval(
                viewer.getCurrentTree(),true);
        }
        
    }
    
    //--------------- Private Methods

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
}
