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
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.util.Log;

import sidekick.Asset;
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
	private static Color _markerColor = jEdit.getColorProperty("view.gutter.markerColor");

	private Asset _asset;
	private String[] _actions = {
		"add-marker",
		"remove-marker",
		"remove-all-markers",
		"undirty-buffer"
		};

	private HashMap _actionShortcuts = new HashMap();
	//}}}

	public JPopupMenu popup = new JPopupMenu();

	public SourceTree(View view, boolean docked) {
		//{{{ SourceTree constructor
		super(view, docked);
		tree.setCellRenderer(new Renderer());
		tree.addKeyListener(new KeyHandler());
		tree.addMouseListener(new MouseHandler());
		ActionHandler handler = new ActionHandler();
		for (int i = 0; i < _actions.length; ++i) {
			String action = _actions[i];
			KeyEventTranslator.Key key = KeyEventTranslator.parseKey(
				jEdit.getProperty(action + ".shortcut"));
			_actionShortcuts.put(key, action);
			addPopupEntry(action, handler);
			}
		update();
		} //}}}

	private void addPopupEntry(String action, ActionListener listener) {
		//{{{ addPopupEntry method
		String title = jEdit.getProperty("perlsidekick-" + action);
		JMenuItem item = new JMenuItem(title);
		item.setActionCommand(action);
		item.addActionListener(listener);
		popup.add(item);
		} //}}}

	private boolean hasMarker(int start, int end) {
		//{{{ getMarker method
		return (view.getBuffer().getMarkerInRange(start, end) != null);
		} //}}}

	public static void toggleMarkersFlag() {
		//{{{ method toggleMarkersFlag
		_showMarkers = ! _showMarkers;
	} //}}}

	public static boolean isRegisteredDockable(String name) {
		//{{{ method isRegisteredDockable
		// see if the dockable "name" is registered
		String[] dockables = DockableWindowFactory.getInstance()
			.getRegisteredDockableWindows();
		boolean _found = false;
		for(int i = 0; i < dockables.length; i++) {
		if (dockables[i].equals(name)) {
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

	public static void unloadSideKickTree(View view) {
		//{{{ method unloadSideKickTree
		// unload the SideKick structure browser - we are the substitute
		EditPlugin _sk = jEdit.getPlugin("sidekick.SideKickPlugin");
		if (_sk == null) {
			view.getStatus().setMessageAndClear("SideKick plugin not loaded");
			return;
			}
		DockableWindowFactory.getInstance().unloadDockableWindows(_sk.getPluginJAR());
		view.getStatus().setMessageAndClear("SideKick browser unloaded");
	} //}}}

	public void handleMessage(EBMessage msg) {
		//{{{ handleMessage() method
		// react to marker changes immediately
		super.handleMessage(msg);
		if (msg instanceof BufferUpdate) {
			BufferUpdate upd = (BufferUpdate) msg;
			if (upd.getWhat() == BufferUpdate.MARKERS_CHANGED)
				update();
			}
		} //}}}

	public void handleKey(KeyEvent evt) {
		//{{{ handleKey() method
		int _code = evt.getKeyCode();
		KeyEventTranslator.Key _key = KeyEventTranslator.translateKeyEvent(evt);
		// let the view handle marker actions
		// is there an easier way to assign a jEdit shortcut to a tree?
		if (_actionShortcuts.containsKey(_key))
			view.getInputHandler().handleKey(_key);
		else if (_code == KeyEvent.VK_ESCAPE || _code == KeyEvent.VK_CANCEL)
			view.getTextArea().requestFocus();
		} //}}}

	public void handleMouse(MouseEvent evt) {
		//{{{ handleMouse() method
		if (GUIUtilities.isPopupTrigger(evt)) {
			GUIUtilities.showPopupMenu(popup, this, evt.getX(), evt.getY());
			view.getTextArea().requestFocus();
			}
		} //}}}

	public void handleAction(String action) {
		//{{{ handleAction() method
		if (action.equals("remove-all-markers") || action.equals("remove-marker")) {
			// avoid unnecessary EB noise
			if (view.getBuffer().getMarkers().isEmpty()) return;
			}
		view.getInputHandler().invokeAction(action);
		} //}}}

	protected class KeyHandler extends KeyAdapter {
		//{{{ KeyHandler class
		public void keyPressed(KeyEvent evt) {
			handleKey(evt);
			}
		} //}}}
	
	protected class MouseHandler extends MouseAdapter {
		//{{{ MouseHandler class
		public void mousePressed(MouseEvent evt) {
			handleMouse(evt);
			}
		} //}}}

	protected class ActionHandler implements ActionListener {
		//{{{ ActionHandler class
		public void actionPerformed(ActionEvent evt) {
			handleAction(evt.getActionCommand());
			}
		} //}}}

	protected class Renderer extends DefaultTreeCellRenderer {
		//{{{ Renderer class
		// based on sidekick/SideKickTree.java
		public Component getTreeCellRendererComponent(
			//{{{ +getTreeCellRendererComponent() : Component
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

		public void paintComponent(Graphics g) {
			//{{{ +paintComponent(Graphics) : void
			// inspired from ProjectViewer plugin
			// underlines the asset if it contains a marker
			if (isMarkersFlagSet() && _hasMarker) {
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

