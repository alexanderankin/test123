/* 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

// Import Java
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

// Import AWT/Swing
import javax.swing.JTree;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

// Import jEdit
import org.gjt.sp.util.Log;

/**
 *  Miscleaneous methods to remove things from projects.
 *
 *  @author     Marcelo Vanzin
 */
public final class RemovalManager {
    
    private final static int FILE  = 1;
    private final static int DIR   = 2;
    private final static int PROJ  = 3;
    private final static int MULTI = 4;
    
    /**
     *  <p>Method to handle the removal of a selection made in the tree. This is
     *  used from inside the listeners to handle the selections the same way.</p>
     *
     *  <p>The method analyses which objects are selected, firing the respective
     *  remove action for the object. Projects selected are removed. Files and
     *  directories selected are removed and, optionally, deleted from disk.
     *  There's no option to remove a project and all its files/dirs (yet?).</p>
     *  
     *  <p>Confirmation dialogs are shown for the following actions: removal of
     *  directories from the project, removal of projects, physical removal
     *  of any kind (files or dirs), and multiple selections. No confirmation is
     *  asked for single file removals.</p>
     *
     *  @param  tree    The tree where to look for the selection.
     *  @param  delete  Whether files and directories should be removed from disk.
     */
    public static void handleSelectionRemoval(JTree tree, boolean delete) {
        switch (tree.getSelectionCount()) {
            case 0:
                // No Selecion!
                return;
                
            case 1: {
                // Single selection!
                remove(tree.getLastSelectedPathComponent(),delete,true);
                break;
            }
                
            default: {
                if (confirmAction(delete,MULTI)) {
                    // Any other thing == multiple selection
                    List sel = getSelectedArtifacts(tree.getSelectionPaths());
                    for (Iterator i = sel.iterator(); i.hasNext(); ) {
                        remove(i.next(),delete,false);
                    }
                }
            }
        }
    }
    
    /**
     *  Receives a collection of TreePath objects and returns the underlying
     *  objects selected, removing a child when its parent has also been
     *  selected.
     */
    private static List getSelectedArtifacts(TreePath[] paths) {
        TreePath last = null;
        ArrayList objs = new ArrayList();
        
        for (int i = 0; i < paths.length; i++) {
            if (last != null && !paths[i].isDescendant(last)) {
                last = null;
            } 
            
            if (last == null) {
                last = paths[i];
                objs.add(paths[i].getLastPathComponent());
            }
        }
        
        return objs;
    }

    /** 
     *  Asks the user if he reeeeeally wants to delete the selection he's
     *  made.
     *
     *  @param  delete  If the selection will be deleted or only removed
     *                  from the project;
     *  @param  type    Type of object being removed. 1=File 2=Directory 3=Project
     *                  4=Multiple objects
     */
    private static boolean confirmAction(boolean delete, int type) {
        String message = null;
        
        // Sanity check
        if (type > MULTI || type < FILE) return false;
        
        if (delete) {
            switch (type) {
                case FILE: // File
                    message = "Do you really want to delete the selected file from disk?";
                    break;
                    
                case DIR: // Directory
                    message = "Do you really want to recursively delete the selected directory from disk?";
                    break;
                    
                case MULTI: // Multiple
                    message = "Do you really want to delete the selected nodes from disk?";
                    break;
                    
                default: // No other deletion is supported (yet?)
                    return false;
            }
        } else {
            switch(type) {
                case FILE: // File
                    message = "Do you really want to delete the selected file from disk?";
                    break;
                    
                case DIR: // Directory
                    message = "Do you really want to remove the selected directory from the project?";
                    break;
                    
                case PROJ: // Project
                    message = "Do you really want to remove the selected project?";
                    break;
                    
                case MULTI: // Multiple
                    message = "Do you really want to remove the selected nodes?";
                    break;
                    
                default: // Won't happen, but...
            }
        }
        
        if (message == null) return true;
        
		int choice = 
            JOptionPane.showConfirmDialog(
                ProjectViewer.getCurrentInstance(),
				message,
                "Confirmation of action:",
				JOptionPane.YES_NO_OPTION
            );
		return (choice == JOptionPane.YES_OPTION);
    }
    
    /**
     *  Removes an object from a project (or the project, if the object is one).
     *
     *  @param  o       The object.
     *  @param  delete  If deletion from disk should occur.
     *  @param  ask     Is we should ask for confirmation.
     */
    private static void remove(Object o, boolean delete, boolean ask) {
        if (o instanceof ProjectFile) {
            if (!delete || (!ask || confirmAction(delete,FILE))) {
                Log.log(Log.NOTICE, instance, "Removing: " + o);
                Project p = ProjectViewer.getCurrentInstance().getCurrentProject();
                p.removeFile((ProjectFile)o,delete);
            }
        } else if (o instanceof ProjectDirectory) {
            if (!ask || confirmAction(delete,DIR)) {
                Log.log(Log.NOTICE, instance, "Removing: " + o);
                Project p = ProjectViewer.getCurrentInstance().getCurrentProject();
                p.removeDirectory((ProjectDirectory)o,delete);
            }
        } else if (o instanceof Project) {
            if (!ask || confirmAction(false,PROJ)) {
                Log.log(Log.NOTICE, instance, "Removing: " + o);
                ProjectManager.getInstance().removeProject((Project)o);
                ProjectViewer.getCurrentInstance().setCurrentProject(null);
                ProjectViewer.getCurrentInstance().refresh();
            }
        }
    }
    
    //----- Just for logging purposes
    
    private static final RemovalManager instance = new RemovalManager();
    
    private RemovalManager() { }
    
    public String toString() {
        return "ProjectViewer::RemovalManager";  
    }
}
