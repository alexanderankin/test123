/*
 * XmlTree.java
 * Copyright (C) 2000 Slava Pestov
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

import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;

public class XmlTree extends JPanel implements DockableWindow, EBComponent
{
	public XmlTree(View view)
	{
		super(new BorderLayout());

		this.view = view;

		tree = new JTree();
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setVisibleRowCount(10);
		tree.setCellRenderer(new Renderer());
		tree.addMouseListener(new MouseHandler());

		add(BorderLayout.CENTER,new JScrollPane(tree));

		handler = new EditPaneHandler();
	}

	public String getName()
	{
		return XmlPlugin.NAME;
	}

	public Component getComponent()
	{
		return this;
	}

	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);

		EditPane[] editPanes = view.getEditPanes();
		for(int i = 0; i < editPanes.length; i++)
		{
			JEditTextArea textArea = editPanes[i].getTextArea();
			textArea.addFocusListener(handler);
			textArea.addCaretListener(handler);
		}

		update();
	}

	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);

		EditPane[] editPanes = view.getEditPanes();
		for(int i = 0; i < editPanes.length; i++)
		{
			JEditTextArea textArea = editPanes[i].getTextArea();
			textArea.removeFocusListener(handler);
			textArea.removeCaretListener(handler);
		}
	}

	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate emsg = (EditPaneUpdate)msg;
			EditPane editPane = emsg.getEditPane();
			if(emsg.getWhat() == EditPaneUpdate.BUFFER_CHANGED
				&& editPane == view.getEditPane())
			{
				update();
			}
			else if(emsg.getWhat() == EditPaneUpdate.CREATED
				&& editPane.getView() == view)
			{
				JEditTextArea textArea = editPane.getTextArea();
				textArea.addFocusListener(handler);
				textArea.addCaretListener(handler);
			}
			else if(emsg.getWhat() == EditPaneUpdate.DESTROYED
				&& editPane.getView() == view)
			{
				JEditTextArea textArea = editPane.getTextArea();
				textArea.removeFocusListener(handler);
				textArea.removeCaretListener(handler);
			}
		}
		else if(msg instanceof BufferUpdate)
		{
			BufferUpdate bmsg = (BufferUpdate)msg;
			if(bmsg.getWhat() == BufferUpdate.DIRTY_CHANGED
				&& bmsg.getBuffer() == buffer
				&& !bmsg.getBuffer().isDirty())
			{
				update();
			}
			else if((bmsg.getWhat() == BufferUpdate.MODE_CHANGED
				|| bmsg.getWhat() == BufferUpdate.LOADED)
				&& bmsg.getBuffer() == buffer)
			{
				update();
			}
		}
		else if(msg instanceof XmlTreeParsed)
		{
			XmlTreeParsed xmsg = (XmlTreeParsed)msg;
			if(xmsg.getBuffer() == buffer)
			{
				tree.setModel(xmsg.getTreeModel());
				expandTagAt(view.getTextArea().getCaretPosition());
			}
		}
	}

	// private members
	private View view;
	private Buffer buffer;
	private JTree tree;
	private EditPaneHandler handler;

	private void expandTagAt(int dot)
	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree
			.getModel().getRoot();

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			root.getChildAt(0);
		if(node.getUserObject() instanceof XmlTag)
		{
			Vector _path = new Vector();
			expandTagAt(node,dot,_path);
			_path.addElement(node);
			_path.addElement(root);

			Object[] path = new Object[_path.size()];
			for(int i = 0; i < path.length; i++)
				path[i] = _path.elementAt(path.length - i - 1);

			TreePath treePath = new TreePath(path);
			tree.expandPath(treePath);
			tree.setSelectionPath(treePath);
		}
	}

	private boolean expandTagAt(TreeNode node, int dot, Vector path)
	{
		int childCount = node.getChildCount();
		Object userObject = ((DefaultMutableTreeNode)node).getUserObject();
		if(childCount == 0 && userObject instanceof XmlTag)
		{
			// check if the caret in inside this tag
			XmlTag tag = (XmlTag)userObject;
			if(dot >= tag.start.getOffset() && dot <= tag.end.getOffset())
				return true;
		}
		else
		{
			// check if any of our children contain the caret
			for(int i = 0; i < childCount; i++)
			{
				TreeNode _node = node.getChildAt(i);
				if(expandTagAt(_node,dot,path))
				{
					path.addElement(_node);
					return true;
				}
			}

			// check if the caret in inside this tag
			XmlTag tag = (XmlTag)userObject;
			if(dot >= tag.start.getOffset() && dot <= tag.end.getOffset())
				return true;
		}

		return false;
	}

	private void update()
	{
		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				if(view.getBuffer() == buffer)
				{
					// don't reparse when switching between
					// split panes editing the same buffer
					return;
				}

				buffer = view.getBuffer();
				XmlPlugin.parse(buffer);
			}
		});
	}

	class Renderer extends DefaultTreeCellRenderer
	{
		public Component getTreeCellRendererComponent(JTree tree,
			Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree,value,sel,
				expanded,leaf,row,hasFocus);

			setIcon(null);

			return this;
		}
	}

	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			TreePath path = tree.getPathForLocation(
				evt.getX(),evt.getY());
			if(path == null)
				return;

			Object value = ((DefaultMutableTreeNode)path
				.getLastPathComponent()).getUserObject();

			if(value instanceof XmlTag)
			{
				XmlTag tag = (XmlTag)value;

				view.getTextArea().select(tag.start.getOffset(),
					tag.end.getOffset());
			}
		}
	}

	class EditPaneHandler implements FocusListener, CaretListener
	{
		public void focusGained(FocusEvent evt)
		{
			update();
		}

		public void focusLost(FocusEvent evt)
		{
		}

		public void caretUpdate(CaretEvent evt)
		{
			if(evt.getSource() == view.getTextArea())
				expandTagAt(evt.getDot());
		}
	}
}
