package ise.plugin.svn.action;

import java.awt.event.*;
import javax.swing.*;
import projectviewer.action.Action;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.command.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

/**
 * ProjectViewer Action to be added to the PV context menu.  This class serves
 * as the menu for a pull-out menu containing the subversion commands.
 */
public class SVNAction extends projectviewer.action.Action {

    private JMenu menu = null;
    private VPTNode path = null;

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
            String classname = jEdit.getProperty( pbase + "code." + i );
            if ( classname == null ) {
                continue;
            }
            JMenuItem item = null;
            try {
                NodeActor action = ( NodeActor ) Class.forName( classname ).newInstance();
                if ( path != null ) {
                    action.prepareForNode( path, viewer.getView() );
                }
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
        path = node;
        View view = viewer.getView();
        for ( int i = 0; i < menu.getItemCount(); i++ ) {
            try {
                JMenuItem actor = ( JMenuItem ) menu.getItem( i );
                ActionListener[] listeners = actor.getActionListeners();
                for (ActionListener al : listeners) {
                    if (al instanceof NodeActor) {
                        ((NodeActor)al).prepareForNode(node, view);
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
}
