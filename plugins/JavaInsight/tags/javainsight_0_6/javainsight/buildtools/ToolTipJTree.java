/*
 * jEdit edit mode settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
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

package javainsight.buildtools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


/**
 * A <code>JTree</code> that displays tooltips for partially visible
 * entries.
 *
 * Parts stolen from Slava Pestov's VFSBrowser.
 *
 * @author Dirk Moebius
 * @author Slava Pestov
 * @version $Id$
 * @see org.gjt.sp.jedit.gui.BrowserView.BrowserJTree
 */
public class ToolTipJTree extends JTree {

    public ToolTipJTree() { super(); init(); }
    public ToolTipJTree(Object[] objects) { super(objects); init(); }
    public ToolTipJTree(Vector vector) { super(vector); init(); }
    public ToolTipJTree(Hashtable hashtable) { super(hashtable); init(); }
    public ToolTipJTree(TreeNode treenode) { super(treenode); init(); }
    public ToolTipJTree(TreeNode treenode, boolean asksAllowsChildren) { super(treenode, asksAllowsChildren); init(); }
    public ToolTipJTree(TreeModel model) { super(model); init(); }


    private void init() {
        ToolTipManager.sharedInstance().registerComponent(this);
    }


    public final String getToolTipText(MouseEvent evt) {
        TreePath path = getPathForLocation(evt.getX(), evt.getY());
        if (path != null) {
            Rectangle cellRect = getPathBounds(path);
            if (cellRect != null && !cellRectIsVisible(cellRect))
                return path.getLastPathComponent().toString();
        }
        return null;
    }


    public final Point getToolTipLocation(MouseEvent evt) {
        TreePath path = getPathForLocation(evt.getX(), evt.getY());
        if (path != null) {
            Rectangle cellRect = getPathBounds(path);
            if (cellRect != null && !cellRectIsVisible(cellRect))
                return new Point(cellRect.x + 20, cellRect.y + 2);
        }
        return null;
    }


    protected void processMouseEvent(MouseEvent evt) {
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        switch (evt.getID()) {
            case MouseEvent.MOUSE_ENTERED:
                toolTipInitialDelay = ttm.getInitialDelay();
                toolTipReshowDelay = ttm.getReshowDelay();
                ttm.setInitialDelay(200);
                ttm.setReshowDelay(0);
                super.processMouseEvent(evt);
                break;
            case MouseEvent.MOUSE_EXITED:
                ttm.setInitialDelay(toolTipInitialDelay);
                ttm.setReshowDelay(toolTipReshowDelay);
                super.processMouseEvent(evt);
                break;
            default:
                super.processMouseEvent(evt);
                break;
        }
    }


    private boolean cellRectIsVisible(Rectangle cellRect) {
        Rectangle vr = getVisibleRect();
        return vr.contains(cellRect.x,cellRect.y) &&
            vr.contains(cellRect.x + cellRect.width, cellRect.y + cellRect.height);
    }


    private int toolTipInitialDelay = -1;
    private int toolTipReshowDelay = -1;

}

