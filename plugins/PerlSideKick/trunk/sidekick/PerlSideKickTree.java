/*
 * PerlSideKickTree.java
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
package sidekick;

//{{{ Imports
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.JTree;
import java.awt.event.*;
import java.awt.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.gui.KeyEventTranslator;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.util.Log;

import sidekick.perl.PerlSideKickPlugin;

//}}}

/**
 * The Structure Browser dockable. Extends the SideKick structure browser,
 * adding a popup menu for marker setting and enhanced keyboard handling 
 * It replaces the SideKickTree dockable if desired.
 */
public class PerlSideKickTree extends SideKickTree {

	//{{{ private vars
	private Asset _asset;
	private boolean _hasMarker;
	private JPopupMenu _popup = new JPopupMenu();
	private static Color _markerColor = jEdit.getColorProperty("view.gutter.markerColor");
	private String[] _actions = {
		"add-marker",
		"remove-marker",
		"remove-all-markers",
		};
	private HashMap _actionShortcuts = new HashMap();
		
	//}}}

	//{{{ PerlSideKickTree constructor
	public PerlSideKickTree(View view, boolean docked) {
		super(view, docked);
		tree.setCellRenderer(new Renderer());
		tree.addKeyListener(new KeyHandler());
		tree.addMouseListener(new MouseHandler());
		ActionHandler _ah = new ActionHandler();
		for (int i = 0; i < _actions.length; ++i) {
			String _ac = _actions[i];
			KeyEventTranslator.Key _key = KeyEventTranslator.parseKey(jEdit.getProperty(_ac + ".shortcut"));
			_actionShortcuts.put(_key, _ac);
			addPopupEntry(_ac, _ah);
			}
		update();
		} //}}}

	//{{{ addPopupEntry method
	private void addPopupEntry(String action, ActionListener al) {
		String _title = jEdit.getProperty(action + ".label");
		JMenuItem _entry = new JMenuItem();
		_entry.setActionCommand(action);
		_entry.addActionListener(al);
		_popup.add(_entry);
		} //}}}

	//{{{ getMarker method
	private boolean hasMarker(int start, int end) {
		return (view.getBuffer().getMarkerInRange(start, end) != null);
		} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg) {
		// react to marker changes immediately
		super.handleMessage(msg);
		if (msg instanceof BufferUpdate) {
			BufferUpdate upd = (BufferUpdate) msg;
			if (upd.getWhat() == BufferUpdate.MARKERS_CHANGED)
				update();
			}
		} //}}}

	//{{{ handleKey() method
	public void handleKey(KeyEvent evt) {
		int _code = evt.getKeyCode();
		KeyEventTranslator.Key _key = KeyEventTranslator.translateKeyEvent(evt);
		// let the view handle marker actions
		// is there an easier way to assign a jEdit shortcut to a tree?
		if (_actionShortcuts.containsKey(_key))
			view.getInputHandler().handleKey(_key);
		else if (_code == KeyEvent.VK_ESCAPE || _code == KeyEvent.VK_CANCEL)
			view.getTextArea().requestFocus();
		} //}}}

	//{{{ handleMouse() method
	public void handleMouse(MouseEvent evt) {
		if (GUIUtilities.isPopupTrigger(evt)) {
			GUIUtilities.showPopupMenu(_popup, this, evt.getX(), evt.getY());
			view.getTextArea().requestFocus();
			}
		} //}}}

	//{{{ handleAction() method
	public void handleAction(String action) {
		if (action.equals("remove-all-markers") || action.equals("remove-marker")) {
			// avoid unnecessary EB noise
			if (view.getBuffer().getMarkers().isEmpty()) return;
			}
		view.getInputHandler().invokeAction(action);
		} //}}}

	//{{{ KeyHandler class
	class KeyHandler extends KeyAdapter {
		public void keyPressed(KeyEvent evt) {
			handleKey(evt);
			}
		} //}}}
	
	//{{{ MouseHandler class
	class MouseHandler extends MouseAdapter {
		public void mousePressed(MouseEvent evt) {
			handleMouse(evt);
			}
		} //}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			handleAction(evt.getActionCommand());
			}
		} //}}}

	//{{{ Renderer class
	class Renderer extends DefaultTreeCellRenderer {
		//{{{ +getTreeCellRendererComponent() : Component
		// based on sidekick/SideKickTree.java
		public Component getTreeCellRendererComponent(
			JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, 
			int row, boolean hasFocus
			) {
			JLabel comp = (JLabel) super.getTreeCellRendererComponent(
				tree, value, sel, expanded, leaf, row, hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object nodeValue = node.getUserObject();
			_hasMarker = false;
			if (node.getParent() == null) {
				setIcon(org.gjt.sp.jedit.browser.FileCellRenderer.fileIcon);
				}
			else if (nodeValue instanceof Asset) {
				_asset = (Asset) node.getUserObject();
				setIcon(_asset.getIcon());
				setText(_asset.getShortString());
				_hasMarker = hasMarker(
					_asset.start.getOffset(),
					_asset.end.getOffset() );
				}
			else setIcon(null);
			return this;
		} //}}}

		//{{{ +paintComponent(Graphics) : void
		public void paintComponent(Graphics g) {
		// inspired from ProjectViewer plugin
		// underlines the asset if it contains a marker
			if (PerlSideKickPlugin.isMarkersFlagSet() && _hasMarker) {
				FontMetrics fm = getFontMetrics(getFont());
				int x, y;
				y = getHeight() - 3;
				x = (getIcon() == null)
					? 0
					: getIcon().getIconWidth() + getIconTextGap();
				g.setColor(_markerColor);
				g.fillRect(x, y, fm.stringWidth(getText()), 3);
			}
//		setBackground(_markerColor); // does not work here
		super.paintComponent(g);
		} //}}}
	} //}}}

}

