/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package projectviewer.vpt;

//{{{ Imports
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.FontMetrics;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
//}}}

/**
 *	Used by the jTree to underline the file name when it's opened.
 *
 *  @author		<A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public final class VPTCellRenderer extends DefaultTreeCellRenderer {

	//{{{ Constants
	private final static Font leafFont 	= UIManager.getFont("Tree.font");
	private final static Font folderFont = leafFont.deriveFont(Font.BOLD);
	//}}}

	//{{{ Private members
	private boolean underlined;
	//}}}

	//{{{ Constructors

	public VPTCellRenderer() {
		setBorder(new EmptyBorder(1,0,1,0));
	}

 	//}}}

	//{{{ getTreeCellRendererComponent(JTree, Object) method
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded,
			boolean leaf, int row,
			boolean focus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
		try {
			VPTNode node = (VPTNode) value;
			setIcon(node.getIcon(expanded));
			setBackground(node.getBackgroundColor(sel));
			setForeground(node.getForegroundColor(sel));
			setFont(leaf ? leafFont : folderFont);
			underlined = (node.canOpen() && node.isOpened());
			setText(node.getName());
		} catch (ClassCastException cce) {
			// just ignore it...
		}
		return this;
	} //}}}

	//{{{ paintComponent(Graphics) method
	public void paintComponent(Graphics g) {
		if(underlined) {
			FontMetrics fm = getFontMetrics(getFont());
			int x, y;
			if(getIcon() == null) {
				x = 0;
				y = fm.getAscent() + 2;
			} else {
				x = getIcon().getIconWidth() + getIconTextGap();
				y = Math.max(fm.getAscent() + 2,16);
			}
			g.setColor(getForeground());
			g.drawLine(x,y,x + fm.stringWidth(getText()),y);
		}
		super.paintComponent(g);
	} //}}}

}

