/*
Copyright (c) 2009, Dale Anson
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

package imageviewer;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.*;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.msg.DockableWindowUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;

public class ImageViewerPlugin extends EBPlugin {
    public static final String NAME = "imageviewer";

    // view to image viewer map
    private static HashMap<View, ImageViewer> viewMap = new HashMap<View, ImageViewer>();

    // VFSDirectoryEntryTable to MouseAdapter map
    // TODO: possible memory leak here -- if there are multiple Views and one is closed, then
    // this table needs to be cleaned up
    private static HashMap<VFSDirectoryEntryTable, MouseAdapter> vfsAdapterMap = new HashMap<VFSDirectoryEntryTable, MouseAdapter>();

    // PV tree to MouseAdapter map
    // TODO: possible memory leak here -- if there are multiple Views and one is closed, then
    // this table needs to be cleaned up
    private static HashMap<JTree, MouseAdapter> pvAdapterMap = new HashMap<JTree, MouseAdapter>();


    /**
     * @return the ImageViewer for the given View.    
     */
    public static ImageViewer getImageViewer( View view ) {
        if ( view == null ) {
            return null;
        }
        ImageViewer viewer = viewMap.get( view );
        if ( viewer == null ) {
            viewer = new ImageViewer();
            viewMap.put( view, viewer );
        }
        return viewer;
    }

    /**
     * Called from beanshell code in browser.actions.xml, shows
     * an image from the first selected file in the given VFSBrowser.    
     */
    public static void showImage( View view, VFSBrowser browser ) {
        if ( view == null ) {
            return ;
        }
        String filename = browser.getSelectedFiles() [ 0 ].getPath();
        if ( ImageViewer.isValidFilename( filename ) ) {
            ImageViewer imageViewer = getImageViewer( view );
            imageViewer.showImage( filename );
            view.getDockableWindowManager().showDockableWindow( NAME );
        }
    }

    public void handleMessage( EBMessage msg ) {
        if ( msg instanceof ViewUpdate ) {
            ViewUpdate message = ( ViewUpdate ) msg;
            if ( ViewUpdate.CREATED.equals( message.getWhat() ) ) {
                addVFSMouseAdapter( message.getView() );
                addPVMouseAdapter( message.getView() );
            }
            else if ( ViewUpdate.CLOSED.equals( message.getWhat() ) ) {
                viewMap.remove( message.getView() );
            }
        }
        else if ( msg instanceof PropertiesChanged ) {
            boolean allowVFSMouseOver = jEdit.getBooleanProperty( "imageviewer.allowVFSMouseOver", true );
            boolean allowPVMouseOver = jEdit.getBooleanProperty( "imageviewer.allowPVMouseOver", true );

            // maybe turn off mouse adapters
            if ( !allowVFSMouseOver ) {
                for ( VFSDirectoryEntryTable table : vfsAdapterMap.keySet() ) {
                    table.removeMouseMotionListener( vfsAdapterMap.get( table ) );
                }
                vfsAdapterMap.clear();
            }
            if ( !allowPVMouseOver ) {
                for ( JTree tree : pvAdapterMap.keySet() ) {
                    tree.removeMouseMotionListener( pvAdapterMap.get( tree ) );
                }
                pvAdapterMap.clear();
            }

            // maybe start mouse adapters
            addMouseAdapters();
        }
        else if ( msg instanceof DockableWindowUpdate ) {
            DockableWindowUpdate message = ( DockableWindowUpdate ) msg;
            DockableWindowManager dwm = ( DockableWindowManager ) message.getSource();
            if ( "vfs.browser".equals( message.getDockable() ) ) {
                addVFSMouseAdapter( dwm.getView() );
            }
            else if ( "projectviewer".equals( message.getDockable() ) ) {
                addPVMouseAdapter( dwm.getView() );
            }
        }
    }

    /**
     * Create mouse motion listeners for File System Browser and Project Viewer.    
     */
    private void addMouseAdapters() {
        View[] views = jEdit.getViews();
        for ( View view : views ) {
            // add a mouse adapter to the VFSBrowser in each view
            addVFSMouseAdapter( view );

            // add a mouse adapter to the ProjectViewer in each view
            addPVMouseAdapter( view );
        }
    }

    /*
     * I'm doing some gyrations here to be able to work with either PV 2.1.3.7,
     * which is the current release version or with the newer, soon to be PV 3.0.
     * TODO: clean this up when PV 3.0 is out.
     */
    private void addPVMouseAdapter( View view ) {
        boolean allowPVMouseOver = jEdit.getBooleanProperty( "imageviewer.allowPVMouseOver", true );
        if ( !allowPVMouseOver ) {
            return ;
        }
        if ( view == null ) {
            return ;
        }
        EditPlugin pvPlugin = jEdit.getPlugin( "projectviewer.ProjectPlugin", true );
        if ( pvPlugin == null ) {
            return ;
        }
        String version = jEdit.getProperty( "plugin.projectviewer.ProjectPlugin.version" );
        if ( version == null ) {
            return ;
        }

        if ( "2.1.3.7".equals( version ) ) {        // NOPMD
            // have old/last release of PV
            ProjectViewer pv = ProjectViewer.getViewer( view );
            if ( pv == null ) {
                return ;
            }
            try {
                String[] tree_field_names = new String[] {"folderTree", "fileTree", "workingFileTree", "compactTree", "filteredTree"};
                for ( String name : tree_field_names ) {
                    JTree tree = ( JTree ) PrivilegedAccessor.getValue( pv, name );
                    if ( tree != null && pvAdapterMap.get( tree ) == null ) {
                        MouseAdapter adapter = createPVMouseAdapter( view, tree );
                        tree.addMouseMotionListener( adapter );
                        pvAdapterMap.put( tree, adapter );
                    }
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        else {
            String[] parts = version.split( "[.]" );
            int major = Integer.parseInt( parts[ 0 ] );
            int minor = Integer.parseInt( parts[ 1 ] );
            if ( major >= 3 || ( major >= 2 && minor >= 9 ) ) {
                // have current svn version of PV
                ProjectViewer pv = ProjectViewer.getViewer( view );
                if ( pv == null ) {
                    return ;
                }
                try {
                    Object treePanel = PrivilegedAccessor.invokeMethod( pv, "getTreePanel", null );
                    java.util.List treeList = ( java.util.List ) PrivilegedAccessor.getValue( treePanel, "trees" );
                    for ( int i = 0; i < treeList.size(); i++ ) {
                        JTree tree = ( JTree ) treeList.get( i );
                        if ( tree != null && pvAdapterMap.get( tree ) == null ) {
                            MouseAdapter adapter = createPVMouseAdapter( view, tree );
                            tree.addMouseMotionListener( adapter );
                            pvAdapterMap.put( tree, adapter );
                        }
                    }
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    private MouseAdapter createPVMouseAdapter( final View view, final JTree tree ) {
        MouseAdapter adapter = new MouseAdapter() {
                    public void mouseMoved( MouseEvent me ) {
                        TreePath treepath = tree.getClosestPathForLocation( me.getX(), me.getY() );
                        VPTNode node = ( VPTNode ) treepath.getLastPathComponent();
                        String path = node.getNodePath();
                        if ( ImageViewer.isValidFilename( path ) ) {
                            view.getDockableWindowManager().showDockableWindow( NAME );
                            ImageViewer imageViewer = getImageViewer( view );
                            imageViewer.showImage( path );
                        }
                    }
                };
        return adapter;
    }

    private void addVFSMouseAdapter( View view ) {
        boolean allowVFSMouseOver = jEdit.getBooleanProperty( "imageviewer.allowVFSMouseOver", true );
        if ( !allowVFSMouseOver ) {
            return ;
        }
        if ( view == null ) {
            return ;
        }
        VFSBrowser browser = ( VFSBrowser ) view.getDockableWindowManager().getDockable( "vfs.browser" );
        if ( browser == null ) {
            return ;
        }

        Component[] children = browser.getComponents();
        for ( Component child : children ) {
            if ( child.getClass().getName().endsWith( "BrowserView" ) ) {
                try {
                    VFSDirectoryEntryTable table = ( VFSDirectoryEntryTable ) PrivilegedAccessor.invokeMethod( child, "getTable", null );
                    if ( table != null && vfsAdapterMap.get( table ) == null ) {
                        MouseAdapter adapter = createVFSMouseAdapter( view, table );
                        table.addMouseMotionListener( adapter );
                        vfsAdapterMap.put( table, adapter );
                    }
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    private MouseAdapter createVFSMouseAdapter( final View view, final VFSDirectoryEntryTable table ) {
        MouseAdapter adapter = new MouseAdapter() {
                    public void mouseMoved( MouseEvent me ) {
                        Point p = me.getPoint();
                        int row = table.rowAtPoint( p );
                        int column = table.columnAtPoint( p );
                        if ( row == -1 ) {
                            return ;
                        }
                        if ( column == 0 ) {
                            try {
                                VFSFile file = ( VFSFile ) PrivilegedAccessor.getValue( table.getModel().getValueAt( row, 0 ), "dirEntry" );
                                String path = file.getPath();
                                if ( ImageViewer.isValidFilename( path ) ) {
                                    view.getDockableWindowManager().showDockableWindow( NAME );
                                    ImageViewer imageViewer = getImageViewer( view );
                                    imageViewer.showImage( path );
                                }
                            }
                            catch ( Exception e ) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
        return adapter;
    }

    public void stop() {
        for ( VFSDirectoryEntryTable table : vfsAdapterMap.keySet() ) {
            MouseAdapter adapter = vfsAdapterMap.get( table );
            table.removeMouseMotionListener( adapter );
        }
        for ( JTree tree : pvAdapterMap.keySet() ) {
            MouseAdapter adapter = pvAdapterMap.get( tree );
            tree.removeMouseMotionListener( adapter );
        }
        vfsAdapterMap = null;
        pvAdapterMap = null;
        viewMap = null;
    }
}