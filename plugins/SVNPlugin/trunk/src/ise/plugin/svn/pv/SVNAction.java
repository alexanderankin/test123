/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.pv;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.TreePath;

import ise.plugin.svn.PVHelper;
import ise.plugin.svn.command.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.config.VersionControlService;

/**
 * ProjectViewer Action to be added to the PV context menu.  This class serves
 * as the menu for a pull-out menu containing the subversion commands.  A JMenu
 * is created per View.
 * DONE: hide the menu for projects that aren't using Subversion.
 * TODO: make the menu smarter, remove the "Add" menu item for files already
 * under subversion control, show only the "Add" menu item for files not under
 * subversion control.
 */
public class SVNAction extends projectviewer.action.Action {

    // for property lookup
    public final static String PREFIX = "ise.plugin.svn.pv.";
    
    // QUESTION: move "Subversion" to properties file?
    public final static String SUBVERSION = "Subversion";

    // need to have a menu per View, this map stores the relationship
    private static HashMap<View, JMenu> menus = new HashMap<View, JMenu>();

    // this won't be displayed in the PV context menu
    public String getText() {
        return SUBVERSION;
    }
    
    public static void remove(View view) {
        if (view != null) {
            menus.remove(view);
        }
    }

    // returns the menu 'Subversion' with a pull-out submenu containing the
    // subversion commands.
    public JComponent getMenuItem() {

        // possibly reuse the JMenu for the View
        View view = viewer.getView();
        JMenu menu = menus.get( view );
        if ( menu != null ) {
            return menu;
        }

        // set up the menu to be added to Project Viewer's context menu. This
        // will be displayed in the PV context menu.
        menu = new JMenu( SUBVERSION );

        // Each subversion command to be added to the context
        // menu has 2 properties, a label and a command.  The properties have a numeric
        // suffix, the commands are added to the menu in the order of the suffix.  The
        // label property is the displayed name of the command, e.g. "Commit", and the
        // code property is the fully qualified classname of a class in the
        // ise.plugin.svn.action package.
        String pbase = "ise.plugin.svn.action.";
        for ( int i = 1; i < 40; i++ ) {
            String label = jEdit.getProperty( pbase + "label." + i );
            if ( label == null ) {
                continue;
            }
            if ( label.equals( "-" ) ) {
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
                //e.printStackTrace();
                continue;
            }
        }
        menus.put( view, menu );
        return menu;
    }

    // called by ProjectViewer to let us know the currently selected node in
    // the PV tree.  This method updates the various action listeners that
    // execute the subversion commands so they know the current node and can
    // act accordingly.
    public void prepareForNode( VPTNode node ) {
        View view = viewer.getView();
        VPTProject project = viewer.getActiveProject(view);
        boolean visible = false;
        if (project != null) {
            String vcService = project.getProperty(VersionControlService.VC_SERVICE_KEY);
            visible = SUBVERSION.equals(vcService);
        }
        
        JMenu menu = menus.get(view);
        if (menu == null) {
            menu = (JMenu)getMenuItem();   
        }
        menu.setVisible(visible);
        
        //String project_name = PVHelper.getProjectName( view );
        String project_root = PVHelper.getProjectRoot( view );

        // don't handle username/password here anymore, see i.p.s.a.SVNAction
        String username = null;
        String password = null;
        for ( int i = 0; i < menu.getItemCount(); i++ ) {
            try {
                JMenuItem actor = ( JMenuItem ) menu.getItem( i );
                if ( actor == null ) {
                    continue;
                }
                ActionListener[] listeners = actor.getActionListeners();
                for ( ActionListener al : listeners ) {
                    if ( al instanceof NodeActor ) {
                        ( ( NodeActor ) al ).prepareForNode( getSelectedNodes(), view, project_root, username, password );
                    }
                }
            }
            catch ( ClassCastException e ) {    // NOPMD
                // ignored, move on
            }
        }
    }

    public void actionPerformed( ActionEvent ae ) {
        // does nothing, this is the top of a pull out menu so there is no specific
        // action other than to display the pull out.
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