/*
 * ErrorList.java - Error list window
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2000, 2001, 2002 Slava Pestov
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

package errorlist;

//{{{ Imports
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.Selection;
//}}}

public class ErrorList extends JPanel implements EBComponent
{
	public static final ImageIcon ERROR_ICON = new ImageIcon(
		ErrorList.class.getResource("error.gif"));
	public static final ImageIcon WARNING_ICON = new ImageIcon(
		ErrorList.class.getResource("warning.gif"));

	//{{{ ErrorList constructor
	public ErrorList(View view)
	{
		this.view = view;

		setLayout(new BorderLayout());
		add(BorderLayout.NORTH,status = new JLabel());

		// Can't just use "" since the renderer expects string nodes
		// to have Error children
		errorRoot = new DefaultMutableTreeNode(new Root(),true);

		errorModel = new DefaultTreeModel(errorRoot,true);

		errorTree = new JTree(errorModel);
		if(!OperatingSystem.isMacOSLF())
			errorTree.putClientProperty("JTree.lineStyle", "Angled");
		errorTree.addMouseListener(new MouseHandler());
		errorTree.setCellRenderer(new ErrorCellRenderer());
		errorTree.setRootVisible(false);
		errorTree.setShowsRootHandles(true);

		ErrorSource[] sources = ErrorSource.getErrorSources();

		for(int i = 0; i < sources.length; i++)
		{
			ErrorSource source = sources[i];
			ErrorSource.Error[] errors = source.getAllErrors();
			if(errors == null)
				continue;
			for(int j = 0; j < errors.length; j++)
			{
				addError(errors[j]);
			}
		}

		JScrollPane scroller = new JScrollPane(errorTree);
		scroller.setPreferredSize(new Dimension(640,200));
		add(BorderLayout.CENTER,scroller);

		updateStatus();
	} //}}}

	//{{{ addNotify() method
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
	} //}}}

	//{{{ removeNotify() method
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage message)
	{
		if(message instanceof ErrorSourceUpdate)
			handleErrorSourceMessage((ErrorSourceUpdate)message);
	} //}}}

	//{{{ nextErrorFile() method
	public void nextErrorFile()
	{
		if(errorRoot.getChildCount() == 0)
		{
			getToolkit().beep();
			return;
		}

		TreePath selected = errorTree.getSelectionPath();

		DefaultMutableTreeNode next;
		if(selected == null)
			next = (DefaultMutableTreeNode)errorRoot.getChildAt(0);
		else
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				selected.getLastPathComponent();

			if(node.getUserObject() instanceof Extra)
				node = (DefaultMutableTreeNode)node.getParent();

			if(node.getUserObject() instanceof ErrorSource.Error)
				node = (DefaultMutableTreeNode)node.getParent();

			if(node.getUserObject() instanceof String)
			{
				int index = errorRoot.getIndex(node);
				if(index == errorRoot.getChildCount() - 1)
				{
					getToolkit().beep();
					return;
				}
				else
				{
					next = (DefaultMutableTreeNode)
						errorRoot.getChildAt(index + 1);
				}
			}
			else
			{
				// wtf?
				return;
			}
		}

		TreePath path = new TreePath(new TreeNode[] { errorRoot, next });
		errorTree.setSelectionPath(path);
		errorTree.scrollPathToVisible(path);

		jEdit.openFile(view,(String)next.getUserObject());
	} //}}}

	//{{{ prevErrorFile() method
	public void prevErrorFile()
	{
		if(errorRoot.getChildCount() == 0)
		{
			getToolkit().beep();
			return;
		}

		TreePath selected = errorTree.getSelectionPath();

		DefaultMutableTreeNode prev;
		if(selected == null)
		{
			prev = (DefaultMutableTreeNode)errorRoot.getChildAt(
				errorRoot.getChildCount() - 1);
		}
		else
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				selected.getLastPathComponent();

			if(node.getUserObject() instanceof Extra)
				node = (DefaultMutableTreeNode)node.getParent();

			if(node.getUserObject() instanceof ErrorSource.Error)
				node = (DefaultMutableTreeNode)node.getParent();

			if(node.getUserObject() instanceof String)
			{
				int index = errorRoot.getIndex(node);
				if(index == 0)
				{
					getToolkit().beep();
					return;
				}
				else
				{
					prev = (DefaultMutableTreeNode)
						errorRoot.getChildAt(index - 1);
				}
			}
			else
			{
				// wtf?
				return;
			}
		}

		TreePath path = new TreePath(new TreeNode[] { errorRoot, prev });
		errorTree.setSelectionPath(path);
		errorTree.scrollPathToVisible(path);

		jEdit.openFile(view,(String)prev.getUserObject());
	} //}}}

	//{{{ nextError() method
	public void nextError()
	{
		if(errorRoot.getChildCount() == 0)
		{
			getToolkit().beep();
			return;
		}

		DefaultMutableTreeNode parent, next;

		TreePath selected = errorTree.getSelectionPath();
		if(selected == null)
		{
			parent = (DefaultMutableTreeNode)errorRoot.getChildAt(0);
			next = (DefaultMutableTreeNode)parent.getChildAt(0);
		}
		else
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				selected.getLastPathComponent();

			if(node.getUserObject() instanceof Extra)
				node = (DefaultMutableTreeNode)node.getParent();

			if(node.getUserObject() instanceof String)
			{
				parent = node;
				next = (DefaultMutableTreeNode)node.getChildAt(0);
			}
			else if(node.getUserObject() instanceof ErrorSource.Error)
			{
				parent = (DefaultMutableTreeNode)node.getParent();

				int index = parent.getIndex(node);
				if(index == parent.getChildCount() - 1)
				{
					index = errorRoot.getIndex(parent);
					if(index == errorRoot.getChildCount() - 1)
					{
						getToolkit().beep();
						return;
					}
					else
					{
						parent = (DefaultMutableTreeNode)
							errorRoot.getChildAt(index + 1);
						next = (DefaultMutableTreeNode)parent.getChildAt(0);
					}
				}
				else
				{
					next = (DefaultMutableTreeNode)
						parent.getChildAt(index + 1);
				}
			}
			else
			{
				// wtf?
				return;
			}
		}

		TreePath path = new TreePath(new TreeNode[]
			{ errorRoot, parent, next });
		selectPath(path);

		openError((ErrorSource.Error)next.getUserObject());
	} //}}}

	//{{{ previousError() method
	public void previousError()
	{
		if(errorRoot.getChildCount() == 0)
		{
			getToolkit().beep();
			return;
		}

		DefaultMutableTreeNode parent, prev;

		TreePath selected = errorTree.getSelectionPath();
		if(selected == null)
		{
			parent = (DefaultMutableTreeNode)errorRoot.getChildAt(
				errorRoot.getChildCount() - 1);
			prev = (DefaultMutableTreeNode)parent.getChildAt(
				parent.getChildCount() - 1);
		}
		else
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				selected.getLastPathComponent();

			if(node.getUserObject() instanceof Extra)
				node = (DefaultMutableTreeNode)node.getParent();

			if(node.getUserObject() instanceof String)
			{
				int index = errorRoot.getIndex(node);
				if(index == 0)
				{
					getToolkit().beep();
					return;
				}
				else
				{
					parent = (DefaultMutableTreeNode)
						errorRoot.getChildAt(index - 1);
					prev = (DefaultMutableTreeNode)
						parent.getChildAt(parent.getChildCount() - 1);
				}
			}
			else if(node.getUserObject() instanceof ErrorSource.Error)
			{
				parent = (DefaultMutableTreeNode)node.getParent();

				int index = parent.getIndex(node);
				if(index == 0)
				{
					index = errorRoot.getIndex(parent);
					if(index == 0)
					{
						getToolkit().beep();
						return;
					}
					else
					{
						parent = (DefaultMutableTreeNode)
							errorRoot.getChildAt(index - 1);
						prev = (DefaultMutableTreeNode)parent.getChildAt(
							parent.getChildCount() - 1);
					}
				}
				else
				{
					prev = (DefaultMutableTreeNode)
						parent.getChildAt(index - 1);
				}
			}
			else
			{
				// wtf?
				return;
			}
		}

		TreePath path = new TreePath(new TreeNode[]
			{ errorRoot, parent, prev });
		selectPath(path);

		openError((ErrorSource.Error)prev.getUserObject());
	} //}}}

	//{{{ Private members
	private View view;
	private JLabel status;
	private DefaultMutableTreeNode errorRoot;
	private DefaultTreeModel errorModel;
	private JTree errorTree;

	//{{{ updateStatus() method
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
	} //}}}

	//{{{ handleErrorSourceMessage() method
	private void handleErrorSourceMessage(ErrorSourceUpdate message)
	{
		Object what = message.getWhat();

		if(what == ErrorSourceUpdate.ERROR_ADDED)
		{
			addError(message.getError());
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

			// this is a silly hack, because changing branches
			// collapses all existing ones.

			TreeNode[] expandPath = new TreeNode[] { errorRoot, null };
			for(int i = 0; i < errorRoot.getChildCount(); i++)
			{
				expandPath[1] = errorRoot.getChildAt(i);
				errorTree.expandPath(new TreePath(expandPath));
			}

			updateStatus();
		}
	} //}}}

	//{{{ addError() method
	private synchronized void addError(ErrorSource.Error error)
	{
		String[] extras = error.getExtraMessages();
		final DefaultMutableTreeNode newNode
			= new DefaultMutableTreeNode(error,extras.length > 0);
		for(int j = 0; j < extras.length; j++)
			newNode.add(new DefaultMutableTreeNode(
				new Extra(extras[j]),false));

		String path = error.getFilePath();

		for(int i = 0; i < errorRoot.getChildCount(); i++)
		{
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				errorRoot.getChildAt(i);

			String nodePath = (String)node.getUserObject();
			if(nodePath.equals(path))
			{
				node.add(newNode);

				errorModel.reload(node);

				errorTree.expandPath(new TreePath(
					new TreeNode[] { errorRoot, node }));

				return;
			}
		}

		// no node for this file exists yet, so add a new one
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(path,true);
		node.add(newNode);
		errorRoot.add(node);
		errorModel.reload(errorRoot);

		TreeNode[] expandPath = new TreeNode[] { errorRoot, null };
		for(int i = 0; i < errorRoot.getChildCount(); i++)
		{
			expandPath[1] = errorRoot.getChildAt(i);
			errorTree.expandPath(new TreePath(expandPath));
		}
	} //}}}

	//{{{ removeError() method
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
	} //}}}

	//{{{ openError() method
	private void openError(final ErrorSource.Error error)
	{
		final Buffer buffer;
		if(error.getBuffer() != null)
			buffer = error.getBuffer();
		else
		{
			buffer = jEdit.openFile(view,error.getFilePath());
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
				if(lineNo >= 0 && lineNo < buffer.getLineCount())
				{
					start += buffer.getLineStartOffset(lineNo);
					if(end == 0)
						end = buffer.getLineEndOffset(lineNo) - 1;
					else
						end += buffer.getLineStartOffset(lineNo);
				}

				view.getTextArea().setSelection(
					new Selection.Range(start,end));

				view.getTextArea().moveCaretPosition(end);
			}
		});
	} //}}}

	//{{{ selectPath() method
	private void selectPath(TreePath path)
	{
		errorTree.setSelectionPath(path);
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) path.getLastPathComponent();
		if(node.getChildCount() > 0)
		{
			errorTree.expandPath(path);
			errorTree.scrollPathToVisible(path.pathByAddingChild(
				node.getChildAt(node.getChildCount() - 1)));
		}
		else
			errorTree.scrollPathToVisible(path);
	}
	//}}}

	//}}}

	//{{{ Root class
	static class Root {}
	//}}}

	//{{{ Extra class
	/* silly hack so that we can tell the difference between a file node
	 * and an extra message node */
	static class Extra
	{
		Extra(String message)
		{
			this.message = message;
		}

		public String toString()
		{
			return message;
		}

		// private members
		String message;
	} //}}}

	//{{{ ErrorCellRenderer class
	static class ErrorCellRenderer extends JLabel implements TreeCellRenderer
	{
		//{{{ ErrorCellRenderer constructor
		ErrorCellRenderer()
		{
			setOpaque(true);
		} //}}}

		//{{{ getTreeCellRendererComponent() method
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
				setFont(boldFont);

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
				setFont(plainFont);
				ErrorSource.Error error = (ErrorSource.Error)nodeValue;
				setText((error.getLineNumber() + 1)
					+ ": "
					+ (error.getErrorMessage() == null
					? ""
					: error.getErrorMessage()
					.replace('\t',' ')));
				setIcon(error.getErrorType() == ErrorSource.WARNING
					? WARNING_ICON : ERROR_ICON);
			}
			else if(nodeValue instanceof Extra)
			{
				setFont(plainFont);
				setText(nodeValue.toString());
				setIcon(null);
			}
			else if(nodeValue instanceof Root)
			{
				setText(null);
				setIcon(null);
			}

			return this;
		} //}}}

		//{{{ Private members
		private static Font plainFont, boldFont;

		static
		{
			plainFont = UIManager.getFont("Tree.font");
			boldFont = new Font(plainFont.getName(),Font.BOLD,plainFont.getSize());
		} //}}}
	} //}}}

	//{{{ MouseHandler class
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
			if(node.getUserObject() instanceof Root)
			{
				// do nothing
			}
			else if(node.getUserObject() instanceof String)
			{
				jEdit.openFile(view,(String)node.getUserObject());
			}
			else
			{
				if(node.getUserObject() instanceof Extra)
					node = (DefaultMutableTreeNode)node.getParent();

				openError((ErrorSource.Error)node.getUserObject());
			}
		}
	} //}}}
}
