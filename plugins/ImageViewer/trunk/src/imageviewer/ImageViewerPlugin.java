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

import imageviewer.actions.*;

public class ImageViewerPlugin extends EBPlugin {
    public static final String NAME = "imageviewer";

    // view to image viewer map
    private static HashMap<View, ImageViewer> viewMap = null;

    // VFSDirectoryEntryTable to MouseAdapter map
    private static HashMap<VFSDirectoryEntryTable, MMouseAdapter> vfsAdapterMap = null;

    // PV tree to MouseAdapter map
    private static HashMap<JTree, MMouseAdapter> pvAdapterMap = null;
    
    private static void initMaps() {
        if (viewMap == null) {
            viewMap = new HashMap<View, ImageViewer>();   
        }
        if (vfsAdapterMap == null) {
            vfsAdapterMap = new HashMap<VFSDirectoryEntryTable, MMouseAdapter>();   
        }
        if (pvAdapterMap == null) {
            pvAdapterMap = new HashMap<JTree, MMouseAdapter>();   
        }
    }

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
            return;
        }
        Component c = view.getDockableWindowManager().getDockable( NAME );
        if ( c == null || !c.isVisible() ) {
            return;
        }

        if ( browser.getSelectedFiles() != null && browser.getSelectedFiles().length > 0 ) {
            String filename = browser.getSelectedFiles() [0].getPath();
            if ( ImageViewer.isValidFilename( filename ) ) {
                ImageViewer imageViewer = getImageViewer( view );
                imageViewer.showImage( filename );
                view.getDockableWindowManager().showDockableWindow( NAME );
            }
        }
    }

    public void handleMessage( EBMessage msg ) {
        if ( msg instanceof ViewUpdate ) {
            ViewUpdate message = ( ViewUpdate ) msg;
            if ( ViewUpdate.CREATED.equals( message.getWhat() ) ) {
                addVFSMouseAdapter( message.getView() );
                addPVMouseAdapter( message.getView() );
            } else if ( ViewUpdate.CLOSED.equals( message.getWhat() ) ) {
                viewMap.remove( message.getView() );

                // remove and add mouse adapters.  This prevents any memory leaks
                // from having references in the adapter maps for non-existent
                // trees or file system browsers.
                removeMouseAdapters();
                addMouseAdapters();
            }
        } else if ( msg instanceof PropertiesChanged ) {
            boolean allowVFSMouseOver = jEdit.getBooleanProperty( "imageviewer.allowVFSMouseOver", true );
            boolean allowPVMouseOver = jEdit.getBooleanProperty( "imageviewer.allowPVMouseOver", true );

            // maybe turn off mouse adapters
            if ( ! allowVFSMouseOver ) {
                removeVFSMouseAdapters();
            }
            if ( ! allowPVMouseOver ) {
                removePVMouseAdapters();
            }

            // maybe start mouse adapters
            addMouseAdapters();
        } else if ( msg instanceof DockableWindowUpdate ) {
            DockableWindowUpdate message = ( DockableWindowUpdate ) msg;
            DockableWindowManager dwm = ( DockableWindowManager ) message.getSource();
            if ( "vfs.browser".equals( message.getDockable() ) ) {
                addVFSMouseAdapter( dwm.getView() );
            } else if ( "projectviewer".equals( message.getDockable() ) ) {
                addPVMouseAdapter( dwm.getView() );
            }
        }
    }

    private void removeMouseAdapters() {
        removeVFSMouseAdapters();
        removePVMouseAdapters();
    }

    private void removeVFSMouseAdapters() {
        if (vfsAdapterMap == null) {
            return;   
        }
        for ( VFSDirectoryEntryTable table : vfsAdapterMap.keySet() ) {
            MMouseAdapter adapter = vfsAdapterMap.get( table );
            table.removeMouseMotionListener( adapter );
            table.removeMouseListener( adapter );
        }
        vfsAdapterMap.clear();
    }

    private void removePVMouseAdapters() {
        if (pvAdapterMap == null) {
            return;   
        }
        for ( JTree tree : pvAdapterMap.keySet() ) {
            MMouseAdapter adapter = pvAdapterMap.get( tree );
            tree.removeMouseMotionListener( adapter );
            tree.removeMouseListener( adapter );
        }
        pvAdapterMap.clear();
    }

    /**
     * Create mouse motion listeners for File System Browser and Project Viewer.
     */
    private void addMouseAdapters() {
        removeMouseAdapters();
        View[] views = jEdit.getViews();
        for ( View view : views ) {
            // add a mouse adapter to the VFSBrowser in each view
            addVFSMouseAdapter( view );

            // add a mouse adapter to the ProjectViewer in each view
            addPVMouseAdapter( view );
        }
    }

    private void addPVMouseAdapter( View view ) {
        if ( view == null ) {
            return;
        }

        boolean allowPVMouseOver = jEdit.getBooleanProperty( "imageviewer.allowPVMouseOver", true );
        if ( ! allowPVMouseOver ) {
            return;
        }

        EditPlugin pvPlugin = jEdit.getPlugin( "projectviewer.ProjectPlugin" );
        if ( pvPlugin == null ) {
            return;
        }

        ProjectViewer pv = ProjectViewer.getViewer( view );
        if ( pv == null ) {
            return;
        }

        try {
            // TODO: PV 2.9.1 is out and the API has changed.  Check if the PrivilegedAccessor
            // can be removed. -- Not quite.  The tree panel is now accessible, but the list of
            // trees contained by the tree panel is not, so still need to cheat to be able to
            // add the mouse motion listeners.
            Object treePanel = PrivilegedAccessor.invokeMethod( pv, "getTreePanel", null );
            java.util.List treeList = ( java.util.List ) PrivilegedAccessor.getValue( treePanel, "trees" );
            for ( int i = 0; i < treeList.size(); i++ ) {
                JTree tree = ( JTree ) treeList.get( i );
                if ( tree != null && pvAdapterMap.get( tree ) == null ) {
                    MMouseAdapter adapter = createPVMouseAdapter( view, tree );
                    tree.addMouseListener( adapter );
                    tree.addMouseMotionListener( adapter );
                    pvAdapterMap.put( tree, adapter );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private MMouseAdapter createPVMouseAdapter( final View view, final JTree tree ) {
        MMouseAdapter adapter = new MMouseAdapter() {
            public void mouseMoved( MouseEvent me ) {
                if ( jEdit.getBooleanProperty( "imageviewer.mouseover" ) ) {
                    showImage( me );
                }
            }

            public void mouseClicked( MouseEvent me ) {
                if ( !jEdit.getBooleanProperty( "imageviewer.mouseover" ) ) {
                    showImage( me );
                }
            }

            private void showImage( MouseEvent me ) {
                // Do nothing if dockable is not visible.
                if ( view == null ) {
                    return;
                }
                Component c = view.getDockableWindowManager().getDockable( NAME );
                if ( c == null || !c.isVisible() ) {
                    return;
                }
                TreePath treepath = tree.getClosestPathForLocation( me.getX(), me.getY() );
                Object lastComponent = treepath.getLastPathComponent();
                if ( lastComponent instanceof VPTNode ) {
                    VPTNode node = ( VPTNode ) lastComponent;
                    String path = node.getNodePath();
                    if ( ImageViewer.isValidFilename( path ) ) {
                        view.getDockableWindowManager().showDockableWindow( NAME );
                        ImageViewer imageViewer = getImageViewer( view );
                        imageViewer.showImage( path );
                    }
                }

            }

        };
        return adapter;
    }

    private void addVFSMouseAdapter( View view ) {
        boolean allowVFSMouseOver = jEdit.getBooleanProperty( "imageviewer.allowVFSMouseOver", true );
        if ( ! allowVFSMouseOver ) {
            return;
        }
        if ( view == null ) {
            return;
        }
        VFSBrowser browser = ( VFSBrowser ) view.getDockableWindowManager().getDockable( "vfs.browser" );
        if ( browser == null ) {
            return;
        }

        Component[] children = browser.getComponents();
        for ( Component child : children ) {
            if ( child.getClass().getName().endsWith( "BrowserView" ) ) {
                try {
                    VFSDirectoryEntryTable table = ( VFSDirectoryEntryTable ) PrivilegedAccessor.invokeMethod( child, "getTable", null );
                    if ( table != null && vfsAdapterMap.get( table ) == null ) {
                        MMouseAdapter adapter = createVFSMouseAdapter( view, table );
                        table.addMouseListener( adapter );
                        table.addMouseMotionListener( adapter );
                        vfsAdapterMap.put( table, adapter );
                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    private MMouseAdapter createVFSMouseAdapter( final View view, final VFSDirectoryEntryTable table ) {
        MMouseAdapter adapter = new MMouseAdapter() {
            public void mouseMoved( MouseEvent me ) {
                if ( jEdit.getBooleanProperty( "imageviewer.mouseover" ) ) {
                    showImage( me );
                }
            }

            public void mouseClicked( MouseEvent me ) {
                if ( !jEdit.getBooleanProperty( "imageviewer.mouseover" ) ) {
                    showImage( me );
                }
            }

            private void showImage( MouseEvent me ) {
                if ( requireIVVisible( view ) ) {
                    Point p = me.getPoint();
                    int row = table.rowAtPoint( p );
                    int column = table.columnAtPoint( p );
                    if ( row == -1 ) {
                        return;
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
                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            private boolean requireIVVisible( View view ) {
                Component iv = ( Component ) view.getDockableWindowManager().getDockable( "imageviewer" );
                if ( iv == null ) {
                    return true;
                }
                return !jEdit.getBooleanProperty( "imageviewer.ifVfsVisible", true ) || ( iv.isVisible() && jEdit.getBooleanProperty( "imageviewer.ifVfsVisible", true ) );
            }

        };
        return adapter;
    }
    
    public void start() {
        initMaps();   
    }

    public void stop() {
        removeMouseAdapters();
        vfsAdapterMap = null;
        pvAdapterMap = null;
        viewMap = null;
    }

    public static void clear( final View view ) {
        SwingUtilities.invokeLater ( new Runnable() {
            public void run() {
                ImageViewer imageViewer = viewMap.get( view );
                if ( imageViewer != null ) {
                    ClearAction clearAction = new ClearAction(imageViewer);
                    clearAction.actionPerformed(null);
                }
            }
        }
        );
    }

    public static void copy( final View view ) {
        SwingUtilities.invokeLater ( new Runnable() {
            public void run() {
                ImageViewer imageViewer = viewMap.get( view );
                if ( imageViewer != null ) {
                    imageViewer.copy();
                }
            }
        }
        );
    }

    public static void reload( final View view ) {
        SwingUtilities.invokeLater ( new Runnable() {
            public void run() {
                ImageViewer imageViewer = viewMap.get( view );
                if ( imageViewer != null ) {
                    imageViewer.reload();
                }
            }
        }
        );
    }

    public static void zoomIn( final View view ) {
        SwingUtilities.invokeLater ( new Runnable() {
            public void run() {
                ImageViewer imageViewer = viewMap.get( view );
                if ( imageViewer != null ) {
                    imageViewer.zoomIn();
                }
            }
        }
        );
    }

    public static void zoomOut( final View view ) {
        SwingUtilities.invokeLater ( new Runnable() {
            public void run() {
                ImageViewer imageViewer = viewMap.get( view );
                if ( imageViewer != null ) {
                    imageViewer.zoomOut();
                }
            }
        }
        );
    }

    public static void rotateCCW( final View view ) {
        SwingUtilities.invokeLater ( new Runnable() {
            public void run() {
                ImageViewer imageViewer = viewMap.get( view );
                if ( imageViewer != null ) {
                    imageViewer.rotateCCW();
                }
            }
        }
        );
    }

    public static void rotateCW( final View view ) {
        SwingUtilities.invokeLater ( new Runnable() {
            public void run() {
                ImageViewer imageViewer = viewMap.get( view );
                if ( imageViewer != null ) {
                    imageViewer.rotateCW();
                }
            }
        }
        );
    }

}