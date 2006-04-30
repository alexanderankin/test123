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
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

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

	/** Don't clip the node string if it doesn't fit in the tree. */
	public final static int CLIP_NOCLIP		= 0;
	/** Clip the start of the node string if needed. */
	public final static int CLIP_START		= 1;
	/** Clip the end of the node string if needed. */
	public final static int CLIP_END		= 2;
	//}}}

	//{{{ Private members
	private boolean underlined;

	private JTree tree;
	private VPTNode node;
	private int row;
	//}}}

	//{{{ +VPTCellRenderer() : <init>
	public VPTCellRenderer() {
		setBorder(new EmptyBorder(1,0,1,0));
	} //}}}

	//{{{ +getTreeCellRendererComponent(JTree, Object, boolean, boolean, boolean, int, boolean) : Component
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

			this.tree = tree;
			this.row = row;
			this.node = node;
		} catch (ClassCastException cce) {
			this.node = null;
		}
		return this;
	} //}}}

	//{{{ +paintComponent(Graphics) : void
	public void paintComponent(Graphics g) {
		FontMetrics fm = null;
		String toShow = getText();

		//{{{ see if we need to clip the text
		if (node != null && node.getClipType() != CLIP_NOCLIP) {
			Rectangle bounds = tree.getRowBounds(row);
			fm = getFontMetrics(getFont());

			int width = fm.stringWidth(toShow);
			int textStart = (int) bounds.getX();
			if (getIcon() != null)
				textStart += getIcon().getIconWidth() + getIconTextGap();

			if(textStart < tree.getParent().getWidth()
					&& textStart + width > tree.getParent().getWidth()) {
				// figure out how much to clip
				int availableWidth = tree.getParent().getWidth() - textStart
										- fm.stringWidth("...");

				int shownChars = 0;
				for (int i = 1; i < toShow.length(); i++) {
					width = (node.getClipType() == CLIP_START)
						? fm.stringWidth(toShow.substring(toShow.length() - i, toShow.length()))
						: fm.stringWidth(toShow.substring(0, i));
					if (width < availableWidth)
						shownChars++;
					else
						break;
				}

				if (shownChars > 0) {
					// ask the node whether it wants to be clipped at the start or
					// at the end of the string
					if (node.getClipType() == CLIP_START) {
						toShow = "..." +
							toShow.substring(toShow.length() - shownChars, toShow.length());
					} else {
						toShow = toShow.substring(0, shownChars) + "...";
					}
					setText(toShow);
				}
			}
		} //}}}

		// underlines the string if needed
		if (underlined) {
			if (fm == null)
				fm = getFontMetrics(getFont());
			int x, y;
			y = getHeight() - 3;
			x = (getIcon() == null)
					? 0
					: getIcon().getIconWidth() + getIconTextGap();
			g.setColor(getForeground());
			g.drawLine(x, y, x + fm.stringWidth(getText()), y);
		}

		super.paintComponent(g);
	} //}}}

}

