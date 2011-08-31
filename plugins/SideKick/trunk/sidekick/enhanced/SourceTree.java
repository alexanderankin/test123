/*
* SourceTree.java
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2005 by Martin Raspe
* (hertzhaft@biblhertz.it)
*
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
package sidekick.enhanced;

//{{{ Imports
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import marker.MarkerSetsPlugin;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.KeyEventTranslator;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.EnhancedTreeCellRenderer;

import sidekick.Asset;
import sidekick.IAsset;
import sidekick.SideKickPlugin;
import sidekick.SideKickTree;

//}}}

/**
 * @author     Martin Raspe
 * @created    Oct 15, 2005
 * @modified   $Id$
 * @version    $Revision$
 
 * The Structure Browser dockable. Extends the SideKick structure browser,
 * adding a popup menu for marker setting and enhanced keyboard handling 
 * It replaces the SideKickTree dockable if desired.
 */
public class SourceTree extends SideKickTree {

    //{{{ private vars
    private static boolean _hasMarker;
    private static boolean _showMarkers = true;
    private static Color _markerColor = jEdit.getColorProperty( "view.gutter.markerColor" );

    private HashMap _actionShortcuts = new HashMap();
    //}}}

    public JPopupMenu popup;

    public SourceTree( View view, boolean docked ) {
        //{{{ SourceTree constructor
        super( view, docked );
        tree.setCellRenderer( new Renderer() );
        tree.addKeyListener( new KeyHandler() );
        MouseHandler mh = new MouseHandler();
        tree.addMouseListener( mh );
        if ( docked )
            tree.addMouseMotionListener( mh );
        update();
    } //}}}

    protected void createPopup() {
        //{{{ create the tree Popup menu from the "sidekick-tree.{mode}.menu" property
        // if not present, use the default menu (see above)
		Mode mode = view.getBuffer().getMode();
        String modename = (mode == null)
            ? ""
            : "." + mode.getName();
        String menu = "sidekick-tree" + modename + ".menu";
        if (jEdit.getProperty(menu) == null)
            menu = "sidekick-tree.menu";
        popup = GUIUtilities.loadPopupMenu(menu);
        } //}}}

    protected void update() {
        //{{{ create a new popup menu if mode changes
        super.update();
        createPopup();
        } //}}}

    private boolean hasMarker(int start, int end) {
        //{{{ getMarker method
    	EditPlugin markerSets = SideKickPlugin.getMarkerSetsPlugin();
    	if (markerSets != null) {
    		if (((MarkerSetsPlugin) markerSets).hasMarker(view.getBuffer(), start, end))
    			return true;
    	}
        return (view.getBuffer().getMarkerInRange(start, end) != null);
    } //}}}

    public static void toggleMarkersFlag() {
        //{{{ method toggleMarkersFlag
        _showMarkers = ! _showMarkers;
    } //}}}

    public static boolean isRegisteredDockable( String name ) {
        //{{{ method isRegisteredDockable
        // see if the dockable "name" is registered
        String[] dockables = DockableWindowFactory.getInstance()
                .getRegisteredDockableWindows();
        boolean _found = false;
        for ( int i = 0; i < dockables.length; i++ ) {
            if ( dockables[ i ].equals( name ) ) {
                _found = true;
                break;
            }
        }
        return _found;
    } //}}}

    public static boolean isMarkersFlagSet() {
        //{{{ method isMarkersFlagSet
        // should marked routines be shown in structure tree?
        return _showMarkers;
    } //}}}

    @EBHandler
    public void handleBufferUpdate( BufferUpdate upd ) {
        //{{{ handleBufferUpdate() method
        // react to marker changes immediately
        if ( upd.getWhat() == BufferUpdate.MARKERS_CHANGED )
            update();
    } //}}}

    public void handleKey( KeyEvent evt ) {
        //{{{ handleKey() method
        int _code = evt.getKeyCode();
        KeyEventTranslator.Key _key = KeyEventTranslator.translateKeyEvent( evt );
        // let the view handle marker actions
        // is there an easier way to assign a jEdit shortcut to a tree?
        if ( _actionShortcuts.containsKey( _key ) )
            view.getInputHandler().handleKey( _key);
        else if (( _code == KeyEvent.VK_ESCAPE || _code == KeyEvent.VK_CANCEL ) &&
                 !evt.isConsumed())
        {
            view.getTextArea().requestFocus();
        }
    } //}}}

    public void handleMouse( MouseEvent evt ) {
        //{{{ handleMouse() method
        if ( GUIUtilities.isPopupTrigger( evt ) ) {
            GUIUtilities.showPopupMenu( popup, this, evt.getX(), evt.getY() );
            view.getTextArea().requestFocus();
        }
    } //}}}

    @Override
    protected void propertiesChanged() {
    	super.propertiesChanged();
    	Color c = jEdit.getColorProperty("view.gutter.markerColor");
    	if (! _markerColor.equals(c)) {
    		_markerColor = c;
    		repaint();
    	}
        if (jEdit.getBooleanProperty("sidekick.showToolTips"))
            ToolTipManager.sharedInstance().registerComponent( tree );
        else
        	ToolTipManager.sharedInstance().unregisterComponent( tree );
    }

	protected class KeyHandler extends KeyAdapter {
        //{{{ KeyHandler class
        public void keyPressed( KeyEvent evt ) {
            handleKey( evt );
        }
    } //}}}

    protected class MouseHandler extends MouseAdapter implements MouseMotionListener {
        //{{{ MouseHandler class
        public void mousePressed( MouseEvent evt ) {
            handleMouse( evt );
        }
        public void mouseDragged( MouseEvent evt ) {
            // no-op, required for interface
        }
        public void mouseMoved( MouseEvent evt ) {
            TreePath path = tree.getPathForLocation(
                        evt.getX(), evt.getY() );
            if ( path == null )
                view.getStatus().setMessage( null );
            else {
                Object value = ( ( DefaultMutableTreeNode ) path
                        .getLastPathComponent() ).getUserObject();

                if ( value instanceof IAsset ) {
                	IAsset as = (IAsset)value;
                    view.getStatus().setMessage(as.getShortString());
                    setStatus(as.getLongString() == null ? as.getShortString() : as.getLongString());
                }
            }
        }
    } //}}}

    protected class Renderer extends EnhancedTreeCellRenderer {
        //{{{ Renderer class
        // based on sidekick/SideKickTree.java
    	@Override
        protected void configureTreeCellRendererComponent(
            //{{{ +getTreeCellRendererComponent() : Component
            JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus
        ) {
            DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) value;
            Object nodeValue = node.getUserObject();
            _hasMarker = false;
            if ( node.getParent() == null ) {
                setIcon( org.gjt.sp.jedit.browser.FileCellRenderer.fileIcon );
            }
            if ( nodeValue instanceof Asset ) {
                IAsset _asset = ( IAsset ) node.getUserObject();
                setIcon( _asset.getIcon() );
                setText( _asset.getShortString() );
                _hasMarker = hasMarker(
                            _asset.getStart().getOffset(),
                            _asset.getEnd().getOffset() );
                setToolTipText( getToolTipText(node, _asset) );
            }
            else
                setIcon( null );
        } //}}}

        private void wrap(StringBuffer sb, String indent, String s) {
        	StringTokenizer st = new StringTokenizer(s, " \t\n", false);
        	StringBuilder line = new StringBuilder();
        	while (st.hasMoreTokens())
        	{
        		String t = st.nextToken();
        		if (line.length() + t.length() >= 80)
        		{
        			sb.append(line + "<br>" + indent);
        			line.setLength(0);
        		}
    			line.append(t + " ");
        	}
    		sb.append(line);
        }
       
        private String getToolTipText(DefaultMutableTreeNode node, IAsset asset) {
            //{{{ -getToolTipText(DefaultMutableTreeNode, IAsset): String
        	StringBuffer sb = new StringBuffer("<html><body>");
        	sb.append(asset.getLongString());
        	sb.append("<br><br>");
        	StringBuffer indent = new StringBuffer();
        	for (TreeNode n: node.getPath()) {
        		DefaultMutableTreeNode tn = (DefaultMutableTreeNode) n;
        		Object o = tn.getUserObject();
        		boolean last = (n == node);
        		if (last)
        			sb.append("<b>");
        		String s;
        		if (o instanceof IAsset)
        			s = ((IAsset) o).getShortString();
        		else
        			s = o.toString();
        		wrap(sb, indent.toString(), s);
        		if (last)
        			sb.append("</b>");
        		if (! last) {
            		sb.append("<br>");
        			indent.append("&nbsp;&nbsp;");
       				sb.append(indent.toString());
        		}
        	}
        	sb.append("</body></html>");
        	return sb.toString();
        } //}}}
        
        public void paintComponent( Graphics g ) {
            //{{{ +paintComponent(Graphics) : void
            // inspired from ProjectViewer plugin
            // underlines the asset if it contains a marker
            if ( isMarkersFlagSet() && _hasMarker ) {
                FontMetrics fm = getFontMetrics( getFont() );
                int x, y;
                y = getHeight() - 3;
                x = ( getIcon() == null )
                    ? 0
                    : getIcon().getIconWidth() + getIconTextGap();
                g.setColor( _markerColor );
                g.fillRect( x, y, fm.stringWidth( getText() ), 3 );
            }
            //  setBackground(_markerColor); // does not work here
            super.paintComponent( g );
        } //}}}

		@Override
		protected TreeCellRenderer newInstance() {
			return new Renderer();
		}
    } //}}}

}

