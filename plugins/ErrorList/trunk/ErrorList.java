/*
 * ErrorList.java - Error list window
 * Copyright (C) 1999, 2000, 2001 Slava Pestov
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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.DockableWindow;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.Selection;

public class ErrorList extends JPanel implements EBComponent, DockableWindow
{
	public static final ImageIcon ERROR_ICON = new ImageIcon(
		ErrorList.class.getResource("error.gif"));
	public static final ImageIcon WARNING_ICON = new ImageIcon(
		ErrorList.class.getResource("warning.gif"));

	public ErrorList(View view)
	{
		this.view = view;

		setLayout(new BorderLayout());
		add(BorderLayout.NORTH,status = new JLabel());

		errorRoot = new DefaultMutableTreeNode(null,true);
		errorModel = new DefaultTreeModel(errorRoot,true);

		Object[] sources = EditBus.getNamedList(ErrorSource.ERROR_SOURCES_LIST);
		if(sources != null)
		{
			for(int i = 0; i < sources.length; i++)
			{
				ErrorSource source = (ErrorSource)sources[i];
				ErrorSource.Error[] errors = source.getAllErrors();
				if(errors == null)
					continue;
				for(int j = 0; j < errors.length; j++)
				{
					addError(errors[j]);
				}
			}
		}

		errorTree = new JTree(errorModel);
		errorTree.putClientProperty("JTree.lineStyle", "Angled");
		errorTree.addMouseListener(new MouseHandler());
		errorTree.setCellRenderer(new ErrorCellRenderer());

		JScrollPane scroller = new JScrollPane(errorTree);
		scroller.setPreferredSize(new Dimension(640,200));
		add(BorderLayout.CENTER,scroller);

		updateStatus();
	}

	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
	}

	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
	}

	public void handleMessage(EBMessage message)
	{
		if(message instanceof ErrorSourceUpdate)
			handleErrorSourceMessage((ErrorSourceUpdate)message);
	}

	public void nextError()
	{
		/* if(errorModel.getSize() > 0)
		{
			int index = errorList.getSelectedIndex() + 1;
			if(index < errorModel.getSize())
			{
				errorList.setSelectedIndex(index);
				openErrorAt(index);
			}
			else
				getToolkit().beep();
		}

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				view.getEditPane().focusOnTextArea();
			}
		}); */
	}

	public void previousError()
	{
		/* if(errorModel.getSize() > 0)
		{
			int index = errorList.getSelectedIndex() - 1;
			if(index >= 0)
			{
				errorList.setSelectedIndex(index);
				openErrorAt(index);
			}
			else
				getToolkit().beep();
		}

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				view.getEditPane().focusOnTextArea();
			}
		}); */
	}

	// DockableWindow implementation
	public String getName()
	{
		return ErrorListPlugin.NAME;
	}

	public Component getComponent()
	{
		return this;
	}

	// private members
	private View view;
	private JLabel status;
	private DefaultMutableTreeNode errorRoot;
	private DefaultTreeModel errorModel;
	private JTree errorTree;

	private void updateStatus()
	{
		int warningCount = 0;
		int errorCount = 0;
		for(int i = 0; i < errorRoot.getChildCount(); i++)
		{
			DefaultMutableTreeNode fileNode = (DefaultMutableTreeNode)
				errorRoot.getChildAt(i);
			for(int j = 0; j < fileNode.getChildCount(); j++)
			{
				DefaultMutableTreeNode errorNode = (DefaultMutableTreeNode)
					fileNode.getChildAt(j);
				ErrorSource.Error error = (ErrorSource.Error)
					errorNode.getUserObject();

				if(error.getErrorType() == ErrorSource.ERROR)
					errorCount++;
				else
					warningCount++;
			}
		}

		Integer[] args = { new Integer(errorCount),
			new Integer(warningCount) };
		status.setText(jEdit.getProperty("error-list.status",args));
	}

	private void handleErrorSourceMessage(ErrorSourceUpdate message)
	{
		Object what = message.getWhat();

		if(what == ErrorSourceUpdate.ERROR_ADDED)
		{
			addError(message.getError());
			errorModel.reload(errorRoot);
			updateStatus();
		}
		else if(what == ErrorSourceUpdate.ERROR_REMOVED)
		{
			removeError(message.getError());
			errorModel.reload(errorRoot);
			updateStatus();
		}
		else if(what == ErrorSourceUpdate.ERRORS_CLEARED)
		{
			ErrorSource source = message.getErrorSource();

			for(int i = 0; i < errorRoot.getChildCount(); i++)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					errorRoot.getChildAt(i);

				for(int j = 0; j < node.getChildCount(); j++)
				{
					DefaultMutableTreeNode errorNode
						= (DefaultMutableTreeNode)
						node.getChildAt(j);

					if(((ErrorSource.Error)errorNode.getUserObject())
						.getErrorSource() == source)
					{
						node.remove(errorNode);
						if(node.getChildCount() == 0)
						{
							errorRoot.remove(node);
							i--;
						}

						j--;
					}
				}
			}

			errorModel.reload(errorRoot);

			updateStatus();
		}
	}

	private void addError(ErrorSource.Error error)
	{
		String path = error.getFilePath();

		for(int i = 0; i < errorRoot.getChildCount(); i++)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				errorRoot.getChildAt(i);

			String nodePath = (String)node.getUserObject();
			if(nodePath.equals(path))
			{
				node.add(new DefaultMutableTreeNode(error,false));
				return;
			}
		}

		// no node for this file exists yet, so add a new one
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(path,true);
		errorRoot.add(node);

		node.add(new DefaultMutableTreeNode(error,false));
	}

	private void removeError(ErrorSource.Error error)
	{
		String path = error.getFilePath();

		for(int i = 0; i < errorRoot.getChildCount(); i++)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				errorRoot.getChildAt(i);

			for(int j = 0; j < node.getChildCount(); j++)
			{
				DefaultMutableTreeNode errorNode
					= (DefaultMutableTreeNode)
					node.getChildAt(j);

				if(errorNode.getUserObject() == error)
				{
					node.remove(errorNode);
					if(node.getChildCount() == 0)
						errorRoot.remove(node);

					break;
				}
			}
		}
	}

	private void openError(final ErrorSource.Error error)
	{
		final Buffer buffer;
		if(error.getBuffer() != null)
			buffer = error.getBuffer();
		else
		{
			buffer = jEdit.openFile(view,null,
				error.getFilePath(),false,false);
			if(buffer == null)
				return;
		}

		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				view.setBuffer(buffer);

				int start = error.getStartOffset();
				int end = error.getEndOffset();

				int lineNo = error.getLineNumber();
				Element line = buffer.getDefaultRootElement()
					.getElement(lineNo);
				if(line != null)
				{
					start += line.getStartOffset();
					if(end == 0)
						end = line.getEndOffset() - 1;
					else
						end += line.getStartOffset();
				}

				view.getTextArea().setSelection(
					new Selection.Range(start,end));
				view.getTextArea().moveCaretPosition(end);
			}
		});
	}

	static class ErrorCellRenderer extends JLabel implements TreeCellRenderer
	{
		public Component getTreeCellRendererComponent(JTree tree,
			Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean focus)
		{
			if(sel)
			{
				setBackground(UIManager.getColor("Tree.selectionBackground"));
				setForeground(UIManager.getColor("Tree.selectionForeground"));
			}
			else
			{
				setBackground(UIManager.getColor("Tree.textBackground"));
				setForeground(UIManager.getColor("Tree.textForeground"));
			}

			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			Object nodeValue = node.getUserObject();

			if(nodeValue == null)
			{
				setIcon(null);
				setText(null);
			}
			else if(nodeValue instanceof String)
			{
				setFont(UIManager.getFont("Label.font"));

				int errorCount = 0;
				int warningCount = 0;
				for(int i = 0; i < node.getChildCount(); i++)
				{
					DefaultMutableTreeNode errorNode = (DefaultMutableTreeNode)
						node.getChildAt(i);
					ErrorSource.Error error = (ErrorSource.Error)
						errorNode.getUserObject();

					if(error.getErrorType() == ErrorSource.ERROR)
						errorCount++;
					else
						warningCount++;
				}

				setText(jEdit.getProperty("error-list.file",
					new Object[] { nodeValue,
						new Integer(errorCount),
						new Integer(warningCount) }));

				setIcon(null);
			}
			else if(nodeValue instanceof ErrorSource.Error)
			{
				setFont(UIManager.getFont("Tree.font"));
				ErrorSource.Error error = (ErrorSource.Error)nodeValue;
				setText(error.getLineNumber() + ": "
					+ error.getErrorMessage());
				setIcon(error.getErrorType() == ErrorSource.WARNING
					? WARNING_ICON : ERROR_ICON);
			}

			return this;
		}
	}

	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			TreePath path = errorTree.getPathForLocation(evt.getX(),evt.getY());
			if(path == null)
				return;

			if(!errorTree.isPathSelected(path))
				errorTree.setSelectionPath(path);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				path.getLastPathComponent();
			if(node.getUserObject() instanceof String)
			{
				jEdit.openFile(view,(String)node.getUserObject());
			}
			else
			{
				openError((ErrorSource.Error)node.getUserObject());
			}
		}
	}
}
