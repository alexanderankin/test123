/* $Id$
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
package projectviewer.tree;

import javax.swing.tree.*;
import java.awt.*;
import javax.swing.*;
import java.net.URL;

import projectviewer.*;

import org.gjt.sp.jedit.io.*;
import org.gjt.sp.util.Log;

/** Used by the jTree to change the background font for subscribed projects.
 *
 * @author     <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 */
public final class TreeRenderer extends DefaultTreeCellRenderer {

	private static Font normalFont;
	private static Font openedFont;

	private static Icon fileIcon;
	private static Icon dirClosedIcon;
	private static Icon dirOpenIcon;
	private static Icon projectIcon;

	private static Color treeSelectionForeground;
	private static Color treeNoSelectionForeground;
	private static Color treeSelectionBackground;
	private static Color treeNoSelectionBackground;

	private JLabel listCellRenderer = null;
	private JLabel treeCellRenderer = null;
	
	private boolean underlined;

	public TreeRenderer() {
		//Log.log(Log.DEBUG,this,"TreeRenderer()");
		/*
		 * Font f = UIManager.getFont("Tree.font");
		 * normalFont = new Font(f.getName(), Font.PLAIN, f.getSize());
		 * openedFont = new Font(f.getName(), Font.BOLD , f.getSize());
		 * treeSelectionForeground = UIManager.getColor("Tree.selectionForeground");
		 * treeNoSelectionForeground = UIManager.getColor("Tree.textForeground");
		 * treeSelectionBackground = UIManager.getColor("Tree.selectionBackground");
		 * treeNoSelectionBackground = UIManager.getColor("Tree.textBackground");
		 * fileIcon      = UIManager.getIcon("Tree.leafIcon");
		 * dirClosedIcon = UIManager.getIcon("Tree.closedIcon");
		 * dirOpenIcon   = UIManager.getIcon("Tree.openIcon");
		 */
		URL url = getClass().getResource("/projectviewer/icons/Project.gif");
		projectIcon = new ImageIcon(url);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded,
			boolean leaf, int row,
			boolean focus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);

		//Log.log(Log.DEBUG,this,"getTreeCellRendererComponent() : "+value.toString());

		//-- do this the first time
		if(treeCellRenderer == null) {
			treeCellRenderer =
				new JLabel() {
					public Dimension getPreferredSize() {
						// this prevents the "..." from showing up
						Dimension d = super.getPreferredSize();
						int width = d.width + (int)(d.width * .15);
						if(d != null)
							d = new Dimension(width, d.height);
						return d;
					}
				};
			treeCellRenderer.setOpaque(true);
		}

		//-- change colors to highlight selected items
		if(sel) {
			treeCellRenderer.setBackground(treeSelectionBackground);
			treeCellRenderer.setForeground(treeSelectionForeground);
		}
		else {
			treeCellRenderer.setBackground(treeNoSelectionBackground);
			treeCellRenderer.setForeground(treeNoSelectionForeground);
		}

		//-- set font && foreground color
		treeCellRenderer.setFont(normalFont);
		if(value instanceof ProjectFile) {
			//-/treeCellRenderer.setFont(getFontForFile((ProjectFile)value));
			underlined=((ProjectFile)value).isOpened();
			if(!sel) {
				treeCellRenderer.setForeground(VFS.getDefaultColorFor(value.toString()));
			}
		}
		else {
			underlined=false;
		//-/	treeCellRenderer.setFont(normalFont);
		}

		//-- set the icons for these entries
		if(value instanceof Project) {
			treeCellRenderer.setIcon(projectIcon);
		}
		else if(value instanceof ProjectDirectory) {
			if(expanded) {
				treeCellRenderer.setIcon(dirOpenIcon);
			}
			else {
				treeCellRenderer.setIcon(dirClosedIcon);
			}
		}
		else if(value instanceof ProjectFile) {
			treeCellRenderer.setIcon(fileIcon);
		}
		else {
			if(leaf) {
				treeCellRenderer.setIcon(fileIcon);
			}
			else if(expanded) {
				treeCellRenderer.setIcon(dirOpenIcon);
			}
			else {
				treeCellRenderer.setIcon(dirClosedIcon);
			}
		}

		treeCellRenderer.setText(value.toString());
		//Log.log(Log.DEBUG,this,"  getTreeCellRendererComponent() : tree.isEnabled()="+tree.isEnabled());
		//-- as the tree is never disabled, we can save this call
		//treeCellRenderer.setEnabled(tree.isEnabled());
		return this;
	}

	public void paintComponent(Graphics g) {
		//Log.log(Log.DEBUG,this,"paintComponent() : "+underlined);
		if(underlined) {
			Font font = getFont();

			FontMetrics fm = getFontMetrics(getFont());
			int x, y;
			if(getIcon() == null) {
				x = 0;
				y = fm.getAscent() + 2;
			}
			else {
				x = getIcon().getIconWidth() + getIconTextGap();
				y = Math.max(fm.getAscent() + 2,16);
			}
			g.setColor(getForeground());
			g.drawLine(x,y,x + fm.stringWidth(getText()),y);
		}

		super.paintComponent(g);
	}

	/** Returns the font to use for the specified file.
	 *
	 * @param  file  Description of Parameter
	 * @return       The fontForFile value
	 */
	protected Font getFontForFile(ProjectFile file) {
		return file.isOpened() ? openedFont : normalFont;
	}

	static {
		normalFont = UIManager.getFont("Tree.font");
		openedFont = normalFont.deriveFont(Font.BOLD);

		treeSelectionForeground = UIManager.getColor("Tree.selectionForeground");
		treeNoSelectionForeground = UIManager.getColor("Tree.textForeground");
		treeSelectionBackground = UIManager.getColor("Tree.selectionBackground");
		treeNoSelectionBackground = UIManager.getColor("Tree.textBackground");

		fileIcon = UIManager.getIcon("Tree.leafIcon");
		dirClosedIcon = UIManager.getIcon("Tree.closedIcon");
		dirOpenIcon = UIManager.getIcon("Tree.openIcon");
		//projectIcon   = new ImageIcon(getClass().getResource("/projectviewer/icons/Project.gif"));
	}

}

