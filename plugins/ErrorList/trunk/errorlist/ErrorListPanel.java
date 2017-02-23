/*
 * ErrorListPanel.java - Error list window
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999-2012 Slava Pestov, Alan Ezust, Shlomy Reinstein
 *
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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.EnhancedTreeCellRenderer;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ThreadUtilities;
import org.jedit.core.FileOpenerService;

import errorlist.ErrorSource.Error;

//}}}
/** The dockable ErrorList component */
public class ErrorListPanel extends JPanel implements DefaultFocusComponent
{
	public static final ImageIcon ERROR_ICON = new ImageIcon(
		ErrorList.class.getResource("error.png"));
	public static final ImageIcon WARNING_ICON = new ImageIcon(
		ErrorList.class.getResource("warning.png"));
	public static final Integer [] allTypes = new Integer[] {
		ErrorSource.ERROR, ErrorSource.WARNING };

	//{{{ data members
	private View view;
	private JLabel status;
	private DefaultMutableTreeNode errorRoot;
	private DefaultTreeModel errorModel;
	private JTree errorTree;
	private HashSet<Error> errors;
	private List<Integer> filteredTypes;
	private Map<Integer, JToggleButton> toggleButtons;
	private JButton multi;
	private PopupMenu popupMenu;
	private boolean isActive = false;
	private boolean multiStatus = jEdit.getBooleanProperty("error-list-multi.selected", false);
	private ErrorSource currentSource = null;
		// }}}

	//{{{ ErrorList constructor
	public ErrorListPanel(View view)
	{
		this.view = view;

		setLayout(new BorderLayout());

		errors = new ErrorHashSet();
		filteredTypes = new ArrayList<Integer>();
		initFilteredTypes();

		Box toolBar = new Box(BoxLayout.X_AXIS);
		status = new JLabel();
		toolBar.add(status);
		toolBar.add(Box.createHorizontalStrut(30));
		toggleButtons = new HashMap<Integer, JToggleButton>();

		JToggleButton toggleBtn = new JToggleButton(ERROR_ICON, true);
		toggleBtn.setSelected(! filteredTypes.contains(Integer.valueOf(
			ErrorSource.ERROR)));
		toggleBtn.setToolTipText(jEdit.getProperty("error-list-toggle-errors.label"));
		toggleBtn.addActionListener(new EditAction.Wrapper(
			jEdit.getActionContext(),
			"error-list-toggle-errors"));
		toolBar.add(toggleBtn);

		toggleButtons.put(Integer.valueOf(ErrorSource.ERROR), toggleBtn);

		toolBar.add(Box.createHorizontalStrut(3));

		toggleBtn = new JToggleButton(WARNING_ICON, true);
		toggleBtn.setSelected(! filteredTypes.contains(Integer.valueOf(
				ErrorSource.WARNING)));
		toggleBtn.setToolTipText(jEdit.getProperty("error-list-toggle-warnings.label"));
		toggleBtn.addActionListener(new EditAction.Wrapper(
			jEdit.getActionContext(),
			"error-list-toggle-warnings"));
		toolBar.add(toggleBtn);
		toggleButtons.put(Integer.valueOf(ErrorSource.WARNING), toggleBtn);
		toolBar.add(Box.createGlue());

		JButton btn = new RolloverButton(GUIUtilities.loadIcon("PreviousFile.png"));
		btn.setToolTipText(jEdit.getProperty("error-list-previous-error-file.label"));
		btn.addActionListener(new EditAction.Wrapper(
			jEdit.getActionContext(),
			"error-list-previous-error-file"));
		toolBar.add(btn);

		btn = new RolloverButton(GUIUtilities.loadIcon("NextFile.png"));
		btn.setToolTipText(jEdit.getProperty("error-list-next-error-file.label"));
		btn.addActionListener(new EditAction.Wrapper(
			jEdit.getActionContext(),
			"error-list-next-error-file"));
		toolBar.add(btn);

		btn = new RolloverButton(GUIUtilities.loadIcon("ArrowL.png"));
		btn.setToolTipText(jEdit.getProperty("error-list-previous-error.label"));
		btn.addActionListener(new EditAction.Wrapper(
			jEdit.getActionContext(),
			"error-list-previous-error"));
		toolBar.add(btn);

		btn = new RolloverButton(GUIUtilities.loadIcon("ArrowR.png"));
		btn.setToolTipText(jEdit.getProperty("error-list-next-error.label"));
		btn.addActionListener(new EditAction.Wrapper(
			jEdit.getActionContext(),
			"error-list-next-error"));
		toolBar.add(btn);

		btn = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		btn.setToolTipText(jEdit.getProperty(
			"error-list-expand-all.label"));
		btn.addActionListener(new EditAction.Wrapper(
			jEdit.getActionContext(),
			"error-list-expand-all"));
		toolBar.add(btn);

		btn = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		btn.setToolTipText(jEdit.getProperty("error-list-collapse-all.label"));
		btn.addActionListener(new EditAction.Wrapper(
			jEdit.getActionContext(),
			"error-list-collapse-all"));
		toolBar.add(btn);

		toolBar.add(Box.createHorizontalStrut(6));

		btn = new RolloverButton(GUIUtilities.loadIcon("Clear.png"));
		btn.setToolTipText(jEdit.getProperty(
			"error-list-clear.label"));
		btn.addActionListener(new EditAction.Wrapper(
			jEdit.getActionContext(),
			"error-list-clear"));
		btn.addActionListener( 
			new ActionListener() 
			{
				public void actionPerformed( ActionEvent ae ) 
				{
		         	errors.clear();  
		         	updateList();
				}
			}
		);
		toolBar.add(btn);

		multi = new RolloverButton();
		multi.addActionListener( 
			new ActionListener() 
			{
				public void actionPerformed( ActionEvent ae ) 
				{
					multiStatus = !multiStatus;
					updateMultiStatus();
				}
			}
		);
		multi.setSelected(multiStatus);
		toolBar.add(multi);
		
		toolBar.add(Box.createHorizontalStrut(6));

		add(BorderLayout.NORTH,toolBar);

		// Can't just use "" since the renderer expects string nodes
		// to have Error children
		errorRoot = new DefaultMutableTreeNode(new Root(),true);

		errorModel = new DefaultTreeModel(errorRoot,true);

		errorTree = new JTree(errorModel);
		if(!OperatingSystem.isMacOSLF())
			errorTree.putClientProperty("JTree.lineStyle", "Angled");
		errorTree.addMouseListener(new MouseHandler());
		errorTree.addKeyListener(new KeyHandler());
		errorTree.setCellRenderer(new ErrorCellRenderer());
		errorTree.setRootVisible(false);
		errorTree.setShowsRootHandles(true);

		TreeNode[] expandPath = new TreeNode[] { errorRoot, null };
		for(int i = 0; i < errorRoot.getChildCount(); i++)
		{
			expandPath[1] = errorRoot.getChildAt(i);
			errorTree.expandPath(new TreePath(expandPath));
		}

		JScrollPane scroller = new JScrollPane(errorTree);
		scroller.setPreferredSize(new Dimension(640,200));
		add(BorderLayout.CENTER,scroller);
		updateMultiStatus();
		updateStatus();

		popupMenu = new PopupMenu(new ActionHandler(this));

		load();
	} //}}}

	//{{{ updateMultiStatus() method
	private void updateMultiStatus()
	{
		if(multiStatus)
		{
			multi.setIcon(GUIUtilities.loadIcon(jEdit.getProperty("hypersearch-results.multi.multiple.icon")));
			multi.setToolTipText(jEdit.getProperty("error-list-multi-on.label"));
		}
		else
		{
			multi.setIcon(GUIUtilities.loadIcon(jEdit.getProperty("hypersearch-results.multi.single.icon")));
			multi.setToolTipText(jEdit.getProperty("error-list-multi-off.label"));
			removeAllButLast();
		}
		jEdit.setBooleanProperty("error-list-multi.selected", multiStatus);
	} //}}}
	
	//{{{ load() method
	public void load()
	{
		EditBus.addToBus(this);
	} //}}}

	//{{{ unload() method
	public void unload()
	{
		EditBus.removeFromBus(this);
	} //}}}

	//{{{ focusOnDefaultComponent() method
	/**
	 * This method is called by jEdit when ErrorList is shown by invoking
	 * <code>DockableWindowManager.showDockableWindow(String)</code>, in
	 * order to request the focus on some component of the Dockable.
	 * Depending on the options, will focus on the ErrorList tree or the
	 * View's TextArea.  If you really want to request
	 * the focus on ErrorList, use the {@link #focus()} method.
	 */
	public void focusOnDefaultComponent()
	{
		// Whenever Errorlist "gets" focus, it sends the focus back to the textarea.
		if (jEdit.getBooleanProperty("error-list.autoRefocusTextArea"))
			view.getTextArea().requestFocusInWindow();
		else
			errorTree.requestFocusInWindow();
	} //}}}

	//{{{ focus() method
	/**
	 * Force focus on ErrorList.
	 * This method is used by the 'errorlist-focus' action.
	 */
	public void focus()
	{
		errorTree.requestFocusInWindow();
	} //}}}


	//{{{ expandAll() methods
	/**
	 * Recursively expand all the nodes on the ErrorList.
	 */
	public void expandAll()
	{
		expandAll(new TreePath(new TreeNode[]{errorRoot}));
	}

	public void expandAll(TreePath parent)
	{
		errorTree.expandPath(parent);
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		Enumeration<TreeNode> e = node.children();
		while ( e.hasMoreElements())
		{
			TreeNode n = e.nextElement();
			TreePath path = parent.pathByAddingChild(n);
			expandAll(path);
		}
	} //}}}

	//{{{ collapseAll() method
	/**
	 * Collapse All the nodes on the ErrorList.
	 */
	public void collapseAll()
	{
		TreeNode[] collapsePath = new TreeNode[] { errorRoot, null };
		for(int i = 0; i < errorRoot.getChildCount(); i++)
		{
			collapsePath[1] = errorRoot.getChildAt(i);
			errorTree.collapsePath(new TreePath(collapsePath));
		}
	} //}}}
	
	public void removeSelection()
	{
		TreePath[] selected = errorTree.getSelectionPaths();
		if (selected == null)
			return;
		for (TreePath path : selected)
		{
			DefaultMutableTreeNode selectedNode = ((DefaultMutableTreeNode)path.getLastPathComponent());
			Object userObject = selectedNode.getUserObject();
			if (userObject instanceof String)
			{
				// file node is selected, children are error nodes, remove them
				// from the error set
				Enumeration children = selectedNode.children();
				while(children.hasMoreElements())
				{
					DefaultMutableTreeNode errorNode = (DefaultMutableTreeNode)children.nextElement();
					Error error = (Error)errorNode.getUserObject();
					errors.remove(error);
				}
			}
			else if (userObject instanceof Error)
			{
				// error node is selected
				errors.remove((Error)userObject);	
			}
		}
		updateList();
	}
	
	private void removeAllButLast()
	{
		if (errorRoot.getChildCount() == 0)
		{
			return;	
		}
		
		// if there is a current error source set, it should be the one that sent
		// the last errors, so remove all errors not from this source
		if (currentSource != null)
		{
			HashSet<Error> toRemove = new HashSet<Error>();
			for (Error error : errors)
			{
				if (!currentSource.equals(error.getErrorSource()))
				{
					toRemove.add(error);
				}
			}
			for (Error error: toRemove)
			{
				errors.remove(error);	
			}
			updateList();
			return;
		}
		
		// otherwise, just keep the last node in the tree
		DefaultMutableTreeNode lastFileNode = (DefaultMutableTreeNode)errorRoot.getLastChild();
		if (lastFileNode == null)
		{
			return;
		}
		Enumeration fileNodes = errorRoot.children();
		while(fileNodes.hasMoreElements())
		{
			DefaultMutableTreeNode fileNode = (DefaultMutableTreeNode)fileNodes.nextElement();
			if (fileNode.equals(lastFileNode))
			{
				break;	
			}
			Object userObject = fileNode.getUserObject();
			if (userObject instanceof String)
			{
				// file node
				Enumeration errorNodes = fileNode.children();
				while(errorNodes.hasMoreElements())
				{
					DefaultMutableTreeNode errorNode = (DefaultMutableTreeNode)errorNodes.nextElement();
					Error error = (Error)errorNode.getUserObject();
					errors.remove(error);
				}
			}
			else if (userObject instanceof Error)
			{
				// error node
				errors.remove((Error)userObject);	
			}
			
		}
		updateList();
	}


	//{{{ initFilteredTypes() method
	private void initFilteredTypes() {
		for (Integer type: allTypes)
		{
			if (jEdit.getBooleanProperty("error-list-filtered-types." + type, false))
				filteredTypes.add(type);
		}
	} //}}}

	//{{{ handleViewUpdate() method
	@EBHandler
	public void handleViewUpdate(ViewUpdate vu) {
		if (vu.getWhat() == ViewUpdate.CLOSED && vu.getView() == view)
			unload();
	}
	//}}}

	//{{{ nextErrorFile() method
	public void nextErrorFile()
	{
		if(errorRoot.getChildCount() == 0)
		{
			UIManager.getLookAndFeel().provideErrorFeedback(null);
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
					UIManager.getLookAndFeel().provideErrorFeedback(null);
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
		_openFile((String)next.getUserObject());
	} //}}}

	//{{{ prevErrorFile() method
	public void prevErrorFile()
	{
		if(errorRoot.getChildCount() == 0)
		{
			UIManager.getLookAndFeel().provideErrorFeedback(null);
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
					UIManager.getLookAndFeel().provideErrorFeedback(null);
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

		_openFile((String)prev.getUserObject());
	} //}}}

	//{{{ nextError() method
	public void nextError()
	{
		if(errorRoot.getChildCount() == 0)
		{
			UIManager.getLookAndFeel().provideErrorFeedback(null);
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
						UIManager.getLookAndFeel().provideErrorFeedback(null);
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
			UIManager.getLookAndFeel().provideErrorFeedback(null);
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
					UIManager.getLookAndFeel().provideErrorFeedback(null);
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
						UIManager.getLookAndFeel().provideErrorFeedback(null);
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

	//{{{ toggleErrors() method
	public void toggleErrors()
	{
		toggleType(ErrorSource.ERROR);
	} //}}}

	//{{{ toggleWarnings() method
	public void toggleWarnings()
	{
		toggleType(ErrorSource.WARNING);
	} //}}}
	
	public void setIsActive(boolean b) 
	{
		isActive = b;	
	}
	
	public boolean getIsActive() 
	{
		return isActive;	
	}

	//{{{ handleErrorSourceMessage() method
	@EBHandler
	public void handleErrorSourceMessage(ErrorSourceUpdate message)
	{
		if (!getIsActive())
			return;
		Object what = message.getWhat();
		ErrorSource es = message.getErrorSource();
		View v = es.getView();
		// Ignore messages that are not meant for me
		if ((v != null) && (v != this.view)) return;
		if(what == ErrorSourceUpdate.ERROR_SOURCE_ADDED)
		{
			addErrorSource(message.getErrorSource(),
						   message.getErrors());
			updateList();
		}
		if(what == ErrorSourceUpdate.ERROR_ADDED)
		{
			ErrorSource errorSource = message.getErrorSource();
			if (errorSource != null && !errorSource.equals(currentSource) && !multiStatus)
			{
				errors.clear();
			}
			currentSource = errorSource;
			addError(message.getError(), false);
			updateList();
		}
		else if(what == ErrorSourceUpdate.ERROR_REMOVED)
		{
			removeError(message.getError());
			updateList();
		}
		else if(what == ErrorSourceUpdate.ERRORS_CLEARED
			|| what == ErrorSourceUpdate.ERROR_SOURCE_REMOVED)
		{
			if (!multiStatus)
			{
				removeErrorSource(message.getErrorSource());
			}
			updateList();
		}
	} //}}}

	//{{{ addErrorSource() method
	// TODO: source isn't used, remove it? 
	private void addErrorSource(ErrorSource source,
								ErrorSource.Error[] errors)
	{
		if(errors == null || errors.length == 0)
			return;

		if (!multiStatus)
		{
			this.errors.clear();	
		}
		currentSource = source;
		for(int j = 0; j < errors.length; j++)
		{
			addError(errors[j], true);
		}

		errorModel.reload(errorRoot);

		TreeNode[] expandPath = new TreeNode[] { errorRoot, null };
		for(int i = 0; i < errorRoot.getChildCount(); i++)
		{
			expandPath[1] = errorRoot.getChildAt(i);
			errorTree.expandPath(new TreePath(expandPath));
		}
	} //}}}

	//{{{ Private members

	//{{{ updateList() method
	private void updateList()
	{
		errorRoot.removeAllChildren();
		errorModel.reload(errorRoot);
		for (Error error : errors) {
			if (! isFiltered(error))
				addErrorToTree(error, false);
		}
		updateStatus();
	}
	//}}}

	//{{{ toggleType() method
	private void toggleType(int errType)
	{
		Integer type = Integer.valueOf(errType);
		boolean filtered = filteredTypes.contains(type);
		JToggleButton toggleBtn = (JToggleButton) toggleButtons.get(type);
		toggleBtn.setSelected(filtered);
		if (filtered)
			filteredTypes.remove(type);
		else
			filteredTypes.add(type);
		jEdit.setBooleanProperty("error-list-filtered-types." + type,
			filteredTypes.contains(type));
		updateList();
	}
	//}}}

	//{{{ updateStatus() method
	private void updateStatus()
	{
		int warningCount = 0;
		int errorCount = 0;
		for (ErrorSource.Error error : errors)
		{
			if(error.getErrorType() == ErrorSource.ERROR)
				errorCount++;
			else
				warningCount++;
		}

		int shownWarningCount = 0;
		int shownErrorCount = 0;
		for(int i = 0; i < errorRoot.getChildCount(); i++)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				errorRoot.getChildAt(i);
			for(int j = 0; j < node.getChildCount(); j++)
			{
				DefaultMutableTreeNode errorNode = (DefaultMutableTreeNode)
					node.getChildAt(j);
				ErrorSource.Error error = (ErrorSource.Error) errorNode.getUserObject();
				if (error.getErrorType() == ErrorSource.ERROR)
					shownErrorCount++;
				else
					shownWarningCount++;
			}
		}

		StringBuffer errorStr = new StringBuffer(String.valueOf(shownErrorCount));
		if (shownErrorCount != errorCount)
			errorStr.append("(" + errorCount + ")");
		StringBuffer warningStr = new StringBuffer(String.valueOf(shownWarningCount));
		if (shownWarningCount != warningCount)
			warningStr.append("(" + warningCount + ")");
		StringBuffer[] args = { errorStr, warningStr };
		status.setText(jEdit.getProperty(
			getStatusProperty(errorCount, warningCount),args));
	} //}}}

	//{{{ removeErrorSource() method
	private void removeErrorSource(ErrorSource source)
	{
		Iterator<Error> it = errors.iterator();
		while (it.hasNext())
		{
			ErrorSource.Error error = (Error) it.next();
			if (error.getErrorSource() == source)
				it.remove();
		}

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
		if(errorRoot.getChildCount()==0
			&& jEdit.getBooleanProperty("error-list.autoCloseOnNoErrors"))
		{
			DockableWindowManager dwm=view.getDockableWindowManager();
			if(dwm.isDockableWindowDocked("error-list")
				&& dwm.isDockableWindowVisible("error-list"))
			{
				dwm.toggleDockableWindow("error-list");
			}
		}
	} //}}}

	//{{{ isFiltered() method
	private boolean isFiltered(ErrorSource.Error error)
	{
		// Check if the type of error should be hidden
		if (filteredTypes.contains(Integer.valueOf(error.getErrorType())))
			return true;
		// Check if the filename pattern should be excluded
		Pattern filter = ErrorListPlugin.getFilenameFilter();
		if (filter != null) {
			String path = error.getFilePath();
			boolean match = filter.matcher(path).matches();
			if (match != ErrorListPlugin.isInclusionFilter())
				return true;
		}
		return false;
	}
	//}}}

	//{{{ addError() method
	private void addError(ErrorSource.Error error, boolean init)
	{
		// Due to thread issues a given error may come here twice,
		// so we need to check whether this is a new error
		int c = errors.size();
		errors.add(error);
		if (errors.size() > c)
		{
			if (! isFiltered(error))
				addErrorToTree(error, init);
		}
	}
	//}}}

	//{{{ addErrorToTree() method
	private void addErrorToTree(ErrorSource.Error error, boolean init)
	{
		Log.log(Log.DEBUG,ErrorList.class,"Adding Error Line#" + (error.getLineNumber()+1)
				+ " Start#" + (error.getStartOffset()+1)  + " Error Message:" + error.getErrorMessage());
		String[] extras = error.getExtraMessages();
		final DefaultMutableTreeNode errorNode = new DefaultMutableTreeNode(error, extras.length > 0);
		for(int j = 0; j < extras.length; j++)
		{
			errorNode.add(new DefaultMutableTreeNode(new Extra(extras[j]), false));
		}

		String path = error.getFilePath();

		for(int i = 0; i < errorRoot.getChildCount(); i++)
		{
			final DefaultMutableTreeNode fileNode = (DefaultMutableTreeNode)errorRoot.getChildAt(i);

			String nodePath = (String)fileNode.getUserObject();
			if(nodePath.equals(path))
			{
				if (!contains(fileNode, errorNode))
				{
					fileNode.add(errorNode);
				}

				if(!init)
				{
					errorModel.reload(fileNode);
					errorTree.expandPath(new TreePath(new TreeNode[] { errorRoot, fileNode, errorNode }));
				}
				return;
			}
		}

		// no node for this file exists yet, so add a new one
		DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(path, true);
		fileNode.add(errorNode);
		errorRoot.add(fileNode);
		errorModel.reload(errorRoot);

		if(!init)
		{
			TreeNode[] expandPath = new TreeNode[] { errorRoot, null };
			for(int i = 0; i < errorRoot.getChildCount(); i++)
			{
				expandPath[1] = errorRoot.getChildAt(i);
				errorTree.expandPath(new TreePath(expandPath));
			}
		}
	} //}}}
	
	private boolean contains(DefaultMutableTreeNode fileNode, DefaultMutableTreeNode errorNode)
	{
		Enumeration children = fileNode.children();
		while(children.hasMoreElements())
		{
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();
			String childErrorMessage = ((ErrorSource.Error)child.getUserObject()).getErrorMessage();
			String errorNodeMessage = ((ErrorSource.Error)errorNode.getUserObject()).getErrorMessage();
			if (childErrorMessage != null && childErrorMessage.equalsIgnoreCase(errorNodeMessage))
			{
				return true;	
			}
		}
		return false;
	}

	//{{{ removeError() method
	private void removeError(ErrorSource.Error error)
	{
		errors.remove(error);
		removeErrorFromTree(error);
	}
	//}}}

	//{{{ removeErrorFromTree() method
	private void removeErrorFromTree(ErrorSource.Error error)
	{
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

		errorModel.reload(errorRoot);

		if(errorRoot.getChildCount()==0
			&& jEdit.getBooleanProperty("error-list.autoCloseOnNoErrors"))
		{
			DockableWindowManager dwm=view.getDockableWindowManager();
			if(dwm.isDockableWindowDocked("error-list")
				&& dwm.isDockableWindowVisible("error-list"))
			{
				dwm.toggleDockableWindow("error-list");
			}
		}
	} //}}}

	//{{{ _openFile() method
	private void _openFile(String vfsPath) {
		try {
			if (MiscUtilities.isURL(vfsPath) && vfsPath.startsWith("http")) {
				jEdit.openFile(view,vfsPath);
				return;
			}
			VFS vfs = VFSManager.getVFSForPath(vfsPath);
			VFSFile file = vfs._getFile(null, vfsPath, null);
			if (file == null || file.getLength() == 0) {
				FileOpenerService.open(MiscUtilities.getFileName(vfsPath), view);
			}
			else {
				jEdit.openFile(view,vfsPath);
			}
		}
		catch (IOException ioe) {
			Log.log(Log.ERROR, this, ioe);
		}
	}//}}}

	//{{{ openError() method
	private void openError(final ErrorSource.Error error)
	{
		_openFile(error.getFilePath());
		final Buffer buffer = error.getBuffer() != null ?
				error.getBuffer() : view.getEditPane().getBuffer();

		if (buffer.isNewFile() || !buffer.getName().equals(error.getFileName())) return;

		ThreadUtilities.runInDispatchThread(new Runnable()
		{
			public void run()
			{
				view.goToBuffer(buffer);

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

	//{{{ openNode() method
	private void openNode(DefaultMutableTreeNode node)
	{
		Object object = node.getUserObject();
		if(object instanceof Root)
		{	// NOPMD
			// do nothing
		}
		else if(object instanceof String)
		{
			_openFile((String)object);
		}
		else if(object instanceof Extra)
		{
			openNode((DefaultMutableTreeNode)node.getParent());
		}
		else if(object instanceof ErrorSource.Error)
		{
			openError((ErrorSource.Error)object);
		}
	} //}}}

	//{{{ openSelectedNode() method
	private void openSelectedNode()
	{
		TreePath selected = errorTree.getSelectionPath();
		if(selected != null)
		{
			openNode((DefaultMutableTreeNode)
				selected.getLastPathComponent());
		}
	} //}}}

	//{{{ setClipboardContents() method
	private void setClipboardContents(String errorMessage)
	{
		StringSelection stringSelection = new StringSelection(errorMessage);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	} //}}}

	//{{{ copySelectedNodeToClipboard() method
	public void copySelectedNodeToClipboard()
	{
		TreePath[] allSelected = errorTree.getSelectionPaths();
		StringBuilder allErrors = new StringBuilder();
		Set<String> selectedFiles = new HashSet<String>();

		if(allSelected != null)
		{
			for (TreePath selected : allSelected)
			{
				copyNode(selected, allErrors, "", selectedFiles);
			}

			setClipboardContents(allErrors.toString());
		}
	} //}}}


	//{{{ copyAllNodesToClipboard() method
	public void copyAllNodesToClipboard()
	{
		StringBuilder allErrors = new StringBuilder();
		Set<String> selectedFiles = new HashSet<String>();
		copyNode(new TreePath(new TreeNode[]{errorRoot}), allErrors, "", selectedFiles);
		setClipboardContents(allErrors.toString());
	} //}}}

	private void copyNode(TreePath parent, StringBuilder allErrors, String lastPath, Set<String> selectedFiles)
	{
		DefaultMutableTreeNode selectedNode =
				   (DefaultMutableTreeNode)parent.getLastPathComponent();

		if (selectedNode.getUserObject() instanceof ErrorSource.Error)
		{
			ErrorSource.Error error = (ErrorSource.Error) selectedNode.getUserObject();

			if (!lastPath.equals(error.getFilePath()) && !selectedFiles.contains(error.getFilePath()))
			{
				if (!"".equals(lastPath))
				{
					allErrors.append('\n');
				}
				allErrors.append(error.getFilePath());
				allErrors.append('\n');
				lastPath = error.getFilePath();
				selectedFiles.add(error.getFilePath());
			}

			allErrors.append(formatErrorDisplay(error));
			allErrors.append('\n');
		}
		else if (selectedNode.getUserObject() instanceof Extra)
		{
			Extra extra = (Extra) selectedNode.getUserObject();

			allErrors.append(extra.toString().replaceAll("\n", ""));
			allErrors.append('\n');

		}
		else if (selectedNode.getUserObject() instanceof String)
		{
			// This is a file name
			// Skip if this has already been copied to clipboard by selecting the file name
			String fileName = (String)selectedNode.getUserObject();
			if (!selectedFiles.contains(fileName))
			{
				allErrors.append(fileName);
				allErrors.append('\n');

				// Keep track of Selected files, so that we don't accidentally get a a double selection if a node
				// is selected as well
				selectedFiles.add(fileName);
			}
		}

		TreeNode node = (TreeNode) parent.getLastPathComponent();
		Enumeration<TreeNode> e = node.children();
		while ( e.hasMoreElements())
		{
			TreeNode n = e.nextElement();
			TreePath path = parent.pathByAddingChild(n);
			copyNode(path, allErrors, lastPath, selectedFiles);
		}
	} //}}}



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

	static protected String getStatusProperty(int errorCount, int warningCount)
	{
		StringBuilder statusProp = new StringBuilder("error-list.status.");
		statusProp.append(errorCount == 1 ? "one" : "many");
		statusProp.append("-error-");
		statusProp.append(warningCount == 1 ? "one" : "many");
		statusProp.append("-warning");
		return statusProp.toString();
	}

	//{{{ formatErrorDisplay() method
	static protected String formatErrorDisplay(ErrorSource.Error error) {
		Log.log(Log.DEBUG,ErrorList.class,"Formatted Error Line#" + (error.getLineNumber()+1)
				+ " Error Message: " + error.getErrorMessage());


		StringBuilder errorFormat = new StringBuilder();

		errorFormat.append(error.getLineNumber() + 1);
		errorFormat.append( ':');
		errorFormat.append(error.getErrorMessage() == null ? "" :
						   error.getErrorMessage().replace('\t',' '));
		return errorFormat.toString();

	} //}}}

	//{{{ ErrorCellRenderer class
	static class ErrorCellRenderer extends EnhancedTreeCellRenderer
	{
		//{{{ ErrorCellRenderer constructor
		ErrorCellRenderer()
		{
			//setOpaque(true);
		} //}}}

		//{{{ newInstance() method
		@Override
		protected TreeCellRenderer newInstance()
		{
			return new ErrorCellRenderer();
		} //}}}

		//{{{ getTreeCellRendererComponent() method
		@Override
		protected void configureTreeCellRendererComponent(JTree tree,
			Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean focus)
		{
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

				setText(nodeValue + " (" +
					jEdit.getProperty(
						getStatusProperty(errorCount, warningCount),
						new Object[] {
							new Integer(errorCount),
							new Integer(warningCount) }) +
					")");

				setIcon(null);
			}
			else if(nodeValue instanceof ErrorSource.Error)
			{
				setFont(plainFont);
				ErrorSource.Error error = (ErrorSource.Error)nodeValue;
				setText(formatErrorDisplay(error));
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
		} //}}}

		//{{{ Private members
		private static Font plainFont, boldFont;

		static
		{
			plainFont = UIManager.getFont("Tree.font");
			if(plainFont == null)
				plainFont = jEdit.getFontProperty("metal.secondary.font");
			boldFont = plainFont.deriveFont(Font.BOLD);
		} //}}}
	} //}}}

	//{{{ MouseHandler class
	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			int shiftMask = MouseEvent.SHIFT_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK;
			int controlMask = MouseEvent.CTRL_DOWN_MASK;
			int modifiers = evt.getModifiersEx();
			if (modifiers > 0 && (modifiers & (shiftMask | controlMask)) == modifiers) {
				// doing selection
				return;
			}
			else if (SwingUtilities.isRightMouseButton(evt)) 
			{
				popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				popupMenu.enableSelectOne(!errorTree.isSelectionEmpty());
			} else {
				TreePath path = errorTree.getPathForLocation(evt.getX(),evt.getY());
				if(path == null)
					return;
				errorTree.setSelectionPath(path);
				openNode((DefaultMutableTreeNode)path.getLastPathComponent());
			}
		}
	} //}}}

	//{{{ KeyHandler class
	class KeyHandler extends KeyAdapter
	{
		public void keyPressed(KeyEvent evt)
		{
			switch(evt.getKeyCode())
			{
			case KeyEvent.VK_SPACE:
				openSelectedNode();

				// Dirty method to keep the focus.
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						errorTree.requestFocusInWindow();
					}
				});

				evt.consume();
				break;
			case KeyEvent.VK_ENTER:
				openSelectedNode();
				evt.consume();
				break;
			case KeyEvent.VK_DELETE:
				// removeSelectedNode() should be here.
				// Now just consume the event so prevent
				// VK_DELETE passed to the text area.
				UIManager.getLookAndFeel().provideErrorFeedback(null);
				evt.consume();
				break;
			default:
				break;
			}
		}
	} //}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public ErrorListPanel errorListPanel;

		// TODO: argument is not used, remove?
		public ActionHandler (ErrorListPanel errorListPanel)
		{
			this.errorListPanel = errorListPanel;
		}

		public void actionPerformed(ActionEvent evt)
		{
			JMenuItem item = (JMenuItem)(evt.getSource());

			if ( jEdit.getProperty("hypersearch-results.copy-to-clipboard").equals(item.getText())) {

				copySelectedNodeToClipboard();

			} else if (jEdit.getProperty("error-list.copy-all-to-clipboard").equals(item.getText())) {

				copyAllNodesToClipboard();

			} else if (jEdit.getProperty("error-list.expand-all").equals(item.getText())) {

				expandAll();

			} else if (jEdit.getProperty("error-list.collapse-all").equals(item.getText())) {

				collapseAll();
				
			} else if (jEdit.getProperty("error-list.remove").equals(item.getText())) {
				
				removeSelection();

			} else {

				JOptionPane.showMessageDialog(null, "Invalid Menu option.");

			}
		}
	} //}}}


	//{{{ PopupMenu class
	class PopupMenu extends JPopupMenu
	{
		JMenuItem selectOne;
		JMenuItem selectAll;
		JMenuItem expandAll;
		JMenuItem collapseAll;
		JMenuItem remove;

		public PopupMenu(ActionListener listener)
		{
			selectOne = new JMenuItem(jEdit.getProperty("hypersearch-results.copy-to-clipboard"));
			selectOne.addActionListener(listener);

			selectAll = new JMenuItem(jEdit.getProperty("error-list.copy-all-to-clipboard"));
			selectAll.addActionListener(listener);

			expandAll = new JMenuItem(jEdit.getProperty("error-list.expand-all"));
			expandAll.addActionListener(listener);

			collapseAll = new JMenuItem(jEdit.getProperty("error-list.collapse-all"));
			collapseAll.addActionListener(listener);
			
			remove = new JMenuItem(jEdit.getProperty("error-list.remove"));
			remove.addActionListener(listener);

			add(selectOne);
			add(selectAll);
			addSeparator();
			add(expandAll);
			add(collapseAll);
			add(remove);
		}

		public void enableSelectOne(boolean enabled)
		{
			selectOne.setEnabled(enabled);
		}


	} //}}}


}
