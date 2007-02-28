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
package projectviewer.gui;

//{{{ Imports
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import javax.swing.border.EtchedBorder;

import org.gjt.sp.jedit.View;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTRoot;
//}}}

/**
 *	A "fake" combo box that shows a GroupMenu instead of the regular
 *	JComboBox popup menu when the menu is requested.
 *
 *	<p>I'm not too happy with this since it will look like the Metal
 *	combo box everywhere. But there's no easy way to override the
 *	JComboBox popup menu, which is the optimal solution to this.</p>
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 2.1.0
 */
public class ProjectComboBox extends JButton
	implements ActionListener, KeyListener, MouseListener {

	private GroupMenu menu;
	private JPopupMenu popup;
	private View view;
	private VPTNode active;
	private boolean showOnNextEvent;

	//{{{ +ProjectComboBox(View) : <init>
	public ProjectComboBox(View view) {
		this.view = view;
		this.showOnNextEvent = true;
		setLayout(new BorderLayout());

		setHorizontalAlignment(JButton.LEFT);
		addMouseListener(this);
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		Dimension dim = getPreferredSize();
		dim.height = 24;
		setPreferredSize(dim);

		dim.width = Integer.MAX_VALUE;
		setMaximumSize(dim);

		menu = new GroupMenu(null, true, true, this);
		active = VPTRoot.getInstance();
		setText(" " + active.getName());

		popup = new JPopupMenu();
		updateMenu();
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	public void actionPerformed(ActionEvent ae) {
		showOnNextEvent = true;
		ProjectViewer.setActiveNode(view, (VPTNode) ae.getSource());
	} //}}}

	//{{{ +updateMenu() : void
	public void updateMenu() {
		menu.populate(popup, VPTRoot.getInstance(), view);
	} //}}}

	//{{{ +setSelectedNode(VPTNode) : void
	public void setSelectedNode(VPTNode node) {
		active = node;
		setText(" " + active.getName());
	} //}}}

	//{{{ +isFocusable() : boolean
	public boolean isFocusable() {
		return false;
	} //}}}

	//{{{ Mouse Listener interface

	//{{{ +mouseClicked(MouseEvent) : void
	public void mouseClicked(MouseEvent e) { } //}}}

	//{{{ +mouseEntered(MouseEvent) : void
	public void mouseEntered(MouseEvent e) { } //}}}

    //{{{ +mouseExited(MouseEvent) : void
    public void mouseExited(MouseEvent e) { } //}}}

    //{{{ +mousePressed(MouseEvent) : void
    public void mousePressed(MouseEvent e) {
		if (!isEnabled()) {
			if (popup.isVisible())
				hidePopup();
			return;
		}
		if (showOnNextEvent) {
			showPopup();
		} else {
			showOnNextEvent = true;
		}
	} //}}}

    //{{{ +mouseReleased(MouseEvent) : void
    public void mouseReleased(MouseEvent e) { } //}}}

	//}}}

	//{{{ Key Listener interface

	public void	keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			showOnNextEvent = true;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	//}}}

	//{{{ -showPopup() : void
	private void showPopup() {
		Point p = getLocation();
		popup.show(this, (int) p.getX(), (int) p.getY() + getHeight() - 4);
		showOnNextEvent = false;

		Container root = SwingUtilities.getAncestorOfClass(JRootPane.class, popup);
		root.removeKeyListener(this);
		root.addKeyListener(this);
	} //}}}

	//{{{ -hidePopup() : void
	private void hidePopup() {
		popup.setVisible(false);
		showOnNextEvent = true;
	} //}}}

	//{{{ +paintComponent(Graphics) : void
	/**
	 *	Draws the button with an arrow on the right hand side. This will
	 *	kinda look like a Metal L&F combo box button, but, whatever...
	 */
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());

		// set the clip so long project names don't overlap the arrow
		g.setClip(0, 0, getWidth() - 20, getHeight());
		super.paintComponent(g);
		g.setClip(null);
		g.setColor(getBackground());
		g.fillRect(getWidth() - 20, 0, 20, getHeight());
		g.setColor(getForeground());

		int[] x = new int[3];
		int[] y = new int[3];

		x[0] = getWidth() - 16;
		x[1] = x[0] + 10;
		x[2] = x[0] + 5;

		y[0] = (getHeight() - 6) / 2;
		y[1] = y[0];
		y[2] = y[0] + 6;

		g.fillPolygon(x, y, 3);
	} //}}}

}

