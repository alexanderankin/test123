package ise.plugin.svn.action;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.TreePath;
import projectviewer.action.Action;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.ProjectViewer;
import ise.plugin.svn.command.*;
import ise.plugin.svn.library.PasswordHandler;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

/**
 * ProjectViewer Action to be added to the PV context menu.  This class serves
 * as the menu for a pull-out menu containing the subversion commands.
 */
public class SVNAction extends projectviewer.action.Action {

    public static String PREFIX = "ise.plugin.svn.pv.";

    private JMenu menu = null;

    public SVNAction() {
        // set up the menu to be added to Project Viewer's context menu
        menu = new JMenu( "Subversion" );

        // Each subversion command to be added to the context
        // menu has 2 properties, a label and a command.  The properties have a numeric
        // suffix, the commands are added to the menu in the order of the suffix.  The
        // label property is the displayed name of the command, e.g. "Commit", and the
        // code property is the fully qualified classname of a class in the
        // ise.plugin.svn.action package.
        String pbase = "ise.plugin.svn.action.";
        for ( int i = 1; i < 100; i++ ) {
            String label = jEdit.getProperty( pbase + "label." + i );
            if ( label == null ) {
                break;
            }
            if ( label.equals("-")) {
                menu.addSeparator();
                continue;
            }
            String classname = jEdit.getProperty( pbase + "code." + i );
            if ( classname == null ) {
                continue;
            }
            JMenuItem item = null;
            try {
                NodeActor action = ( NodeActor ) Class.forName( classname ).newInstance();
                item = new JMenuItem( label );
                item.addActionListener( ( ActionListener ) action );
                menu.add( item );
            }
            catch ( Exception e ) {
                // class not found or instantiation exception, don't worry
                // about it, assume it's a typo
                continue;
            }
        }
    }

    // this will be displayed in the PV context menu
    public String getText() {
        return "Subversion";
    }

    // returns the menu 'Subversion' with a pull-out submenu containing the
    // subversion commands.
    public JComponent getMenuItem() {
        return menu;
    }

    // called by ProjectViewer to let us know the currently selected node in
    // the PV tree.  This method updates the various action listeners that
    // execute the subversion commands so they know the current node and can
    // act accordingly.
    public void prepareForNode( VPTNode node ) {
        View view = viewer.getView();
        String project_name = getProjectName( view );
        String project_root = getProjectRoot( view );
        String username = jEdit.getProperty( PREFIX + project_name + ".username" );
        String password = jEdit.getProperty( PREFIX + project_name + ".password" );
        if ( password != null && password.length() > 0 ) {
            try {
                PasswordHandler ph = new PasswordHandler();
                password = ph.decrypt( password );
            }
            catch ( Exception e ) {
                password = "";
            }
        }
        for ( int i = 0; i < menu.getItemCount(); i++ ) {
            try {
                JMenuItem actor = ( JMenuItem ) menu.getItem( i );
                if (actor == null) {
                    continue;
                }
                ActionListener[] listeners = actor.getActionListeners();
                for ( ActionListener al : listeners ) {
                    if ( al instanceof NodeActor ) {
                        ( ( NodeActor ) al ).prepareForNode( getSelectedNodes(), view, project_root, username, password );
                    }
                }
            }
            catch ( ClassCastException e ) {
                // ignored, move on
            }
        }
    }

    public void actionPerformed( ActionEvent ae ) {
        // does nothing, this is the top of a pull out menu so has no specific
        // action other than to display the pull out.
    }

    private String getProjectName( View view ) {
        VPTProject project = ProjectViewer.getActiveProject( view );
        return project == null ? "" : project.getName();
    }

    private String getProjectRoot( View view ) {
        VPTProject project = ProjectViewer.getActiveProject( view );
        return project == null ? "" : project.getRootPath();
    }

    private List<VPTNode> getSelectedNodes() {
        List<VPTNode> list = new ArrayList<VPTNode>();
        JTree tree = viewer.getCurrentTree();

        switch ( tree.getSelectionCount() ) {
            case 0:
                // no Selection, shouldn't happen, but just in case...
                break;

            case 1: {
                    // single selection
                    list.add( ( VPTNode ) tree.getLastSelectedPathComponent() );
                    break;
                }

            default: {
                    list = getSelectedArtifacts( tree.getSelectionPaths() );
                    break;
                }
        }
        return list;
    }

    /**
     *  Receives a collection of TreePath objects and returns the underlying
     *  objects selected, removing a child when its parent has also been
     *  selected.
     */
    private List<VPTNode> getSelectedArtifacts( TreePath[] paths ) {
        TreePath last = null;
        List<VPTNode> objs = new ArrayList<VPTNode>();

        for ( int i = 0; i < paths.length; i++ ) {
            if ( last != null && !last.isDescendant( paths[ i ] ) ) {
                last = null;
            }

            if ( last == null ) {
                last = paths[ i ];
                objs.add( ( VPTNode ) paths[ i ].getLastPathComponent() );
            }
        }

        return objs;
    }

}
