/*
 * XmlTree.java
 * Copyright (C) 2000, 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

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
	public XmlTree(View view, boolean docked)
	{
		super(new BorderLayout());

		this.view = view;

		// create toolbar with parse button
		JToolBar buttonBox = new JToolBar();
		buttonBox.setFloatable(false);

		parseBtn = new JButton(GUIUtilities.loadIcon("Refresh24.gif"));
		parseBtn.setToolTipText(jEdit.getProperty("xml-tree.parse"));
		parseBtn.setMargin(new Insets(0,0,0,0));
		parseBtn.setRequestFocusEnabled(false);
		parseBtn.addActionListener(new ActionHandler());
		buttonBox.add(parseBtn);
		buttonBox.add(Box.createGlue());

		add(BorderLayout.NORTH,buttonBox);

		// create a faux model that will do until a real one arrives
		DefaultTreeModel emptyModel = new DefaultTreeModel(
			new DefaultMutableTreeNode(null));
		tree = new JTree(emptyModel);
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setVisibleRowCount(10);
		tree.setCellRenderer(new Renderer());
		tree.addMouseListener(new MouseHandler());

		if(docked)
			tree.addMouseMotionListener(new MouseHandler());

		add(BorderLayout.CENTER,new JScrollPane(tree));

		documentHandler = new DocumentHandler();
		editPaneHandler = new EditPaneHandler();

		parser = new XmlParser(view);

		propertiesChanged(true);
	}

	public String getName()
	{
		return XmlPlugin.TREE_NAME;
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
			textArea.addFocusListener(editPaneHandler);
			textArea.addCaretListener(editPaneHandler);
		}

		parser.addNotify();

		parse(true);
	}

	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);

		EditPane[] editPanes = view.getEditPanes();
		for(int i = 0; i < editPanes.length; i++)
		{
			JEditTextArea textArea = editPanes[i].getTextArea();
			textArea.removeFocusListener(editPaneHandler);
			textArea.removeCaretListener(editPaneHandler);
			editPanes[i].putClientProperty(XmlPlugin
				.COMPLETION_INFO_PROPERTY,null);
		}

		parser.removeNotify();
	}

	public void parse(final boolean showParsingMessage)
	{
		this.showParsingMessage = showParsingMessage;

		parser.stopThread();

		// remove listener from old buffer
		if(buffer != null)
			buffer.removeDocumentListener(documentHandler);

		buffer = view.getBuffer();

		// add listener to new buffer
		buffer.addDocumentListener(documentHandler);

		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				parse = buffer.getBooleanProperty("xml.parse");

				EditPane editPane = view.getEditPane();
				editPane.putClientProperty(XmlPlugin.COMPLETION_INFO_PROPERTY,null);

				// check for non-XML file
				if(!parse)
				{
					DefaultMutableTreeNode root = new DefaultMutableTreeNode(buffer.getName());
					DefaultTreeModel model = new DefaultTreeModel(root);
		
					root.insert(new DefaultMutableTreeNode(
						jEdit.getProperty("xml-tree.not-xml-file")),0);
					model.reload(root);
					tree.setModel(model);

					return;
				}
				else if(showParsingMessage)
				{
					DefaultMutableTreeNode root = new DefaultMutableTreeNode(buffer.getName());
					DefaultTreeModel model = new DefaultTreeModel(root);

					root.insert(new DefaultMutableTreeNode(
						jEdit.getProperty("xml-tree.parsing")),0);
					model.reload(root);
					tree.setModel(model);
				}

				parser.parse(buffer);
			}
		});
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
				if(buffer.getBooleanProperty(
					"xml.buffer-change-parse")
					|| buffer.getBooleanProperty(
					"xml.keystroke-parse"))
				{
					parse(true);
				}
				else
					showNotParsedMessage();
			}
			else if(emsg.getWhat() == EditPaneUpdate.CREATED
				&& editPane.getView() == view)
			{
				JEditTextArea textArea = editPane.getTextArea();
				textArea.addFocusListener(editPaneHandler);
				textArea.addCaretListener(editPaneHandler);
			}
			else if(emsg.getWhat() == EditPaneUpdate.DESTROYED
				&& editPane.getView() == view)
			{
				JEditTextArea textArea = editPane.getTextArea();
				textArea.removeFocusListener(editPaneHandler);
				textArea.removeCaretListener(editPaneHandler);
			}
		}
		else if(msg instanceof BufferUpdate)
		{
			BufferUpdate bmsg = (BufferUpdate)msg;
			if(bmsg.getWhat() == BufferUpdate.DIRTY_CHANGED
				&& bmsg.getBuffer() == buffer
				&& !bmsg.getBuffer().isDirty())
			{
				if(buffer.getBooleanProperty(
					"xml.buffer-change-parse")
					|| buffer.getBooleanProperty(
					"xml.keystroke-parse"))
				{
					parse(true);
				}
				else
					showNotParsedMessage();
			}
			else if((bmsg.getWhat() == BufferUpdate.MODE_CHANGED
				|| bmsg.getWhat() == BufferUpdate.LOADED)
				&& bmsg.getBuffer() == buffer)
			{
				if(buffer.getBooleanProperty(
					"xml.buffer-change-parse")
					|| buffer.getBooleanProperty(
					"xml.keystroke-parse"))
				{
					parse(true);
				}
				else
					showNotParsedMessage();
			}
		}
		else if(msg instanceof PropertiesChanged)
			propertiesChanged(false);
	}

	// package-private members
	void parsingComplete(TreeModel model)
	{
		tree.setModel(model);
		expandTagAt(view.getTextArea().getCaretPosition());

		int errorCount = parser.getErrorSource().getErrorCount();

		if(showParsingMessage || errorCount != 0)
		{
			Object[] pp = { new Integer(errorCount) };
			view.getStatus().setMessageAndClear(jEdit.getProperty(
				"xml-tree.parsing-complete",pp));
		}
	}

	// private members
	private JButton parseBtn;
	private JTree tree;

	private boolean showAttributes;
	private boolean parse;
	private int delay;

	private View view;
	private Buffer buffer;
	private Timer timer;
	private DocumentHandler documentHandler;
	private EditPaneHandler editPaneHandler;

	private boolean showParsingMessage;

	private XmlParser parser;

	private void propertiesChanged(boolean init)
	{
		boolean newShowAttributes = jEdit.getBooleanProperty("xml.show-attributes");
		try
		{
			delay = Integer.parseInt(jEdit.getProperty("xml.auto-parse-delay"));
		}
		catch(NumberFormatException nf)
		{
			delay = 1500;
		}

		// due to possible change in show attributes setting
		if(!init && newShowAttributes != showAttributes)
		{
			showAttributes = newShowAttributes;
			parse(true);
		}
	}

	private void showNotParsedMessage()
	{
		parser.stopThread();

		// check for non-XML file
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(buffer.getName());
		DefaultTreeModel model = new DefaultTreeModel(root);

		root.insert(new DefaultMutableTreeNode(
			jEdit.getProperty("xml-tree.not-parsed")),0);

		model.reload(root);
		tree.setModel(model);
	}

	private void parseWithDelay()
	{
		if(timer != null)
			timer.stop();

		timer = new Timer(0,new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				parse(false);
			}
		});

		timer.setInitialDelay(delay);
		timer.setRepeats(false);
		timer.start();
	}

	private void expandTagAt(int dot)
	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree
			.getModel().getRoot();

		if(root.getChildCount() == 0)
			return;

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
			tree.scrollPathToVisible(treePath);
		}
	}

	private boolean expandTagAt(TreeNode node, int dot, Vector path)
	{
		int childCount = node.getChildCount();
		Object userObject = ((DefaultMutableTreeNode)node).getUserObject();
		XmlTag tag = (XmlTag)userObject;

		if(childCount == 0 && userObject instanceof XmlTag)
		{
			// check if the caret in inside this tag
			if(dot >= tag.start.getOffset()
				&& (tag.end == null
				|| dot <= tag.end.getOffset()))
				return true;
		}
		else
		{
			// check if any of our children contain the caret
			for(int i = childCount - 1; i >= 0; i--)
			{
				TreeNode _node = node.getChildAt(i);
				if(expandTagAt(_node,dot,path))
				{
					path.addElement(_node);
					return true;
				}
			}

			// check if the caret in inside this tag
			if(dot >= tag.start.getOffset()
				&& (tag.end == null
				|| dot <= tag.end.getOffset()))
				return true;
		}

		return false;
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			parse(true);
		}
	}

	class MouseHandler extends MouseAdapter implements MouseMotionListener
	{
		public void mousePressed(MouseEvent evt)
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

				JEditTextArea textArea = view.getTextArea();

				textArea.setCaretPosition(tag.start.getOffset());
				if(evt.getClickCount() == 2)
				{
					// counter default double-click action
					tree.expandPath(path);
					XmlActions.showEditTagDialog(view);
				}
			}
		}

		public void mouseExited(MouseEvent evt)
		{
			view.getStatus().setMessage(null);
		}

		public void mouseMoved(MouseEvent evt)
		{
			TreePath path = tree.getPathForLocation(
				evt.getX(),evt.getY());
			if(path == null)
				view.getStatus().setMessage(null);
			else
			{
				Object value = ((DefaultMutableTreeNode)path
					.getLastPathComponent()).getUserObject();

				if(value instanceof XmlTag)
				{
					view.getStatus().setMessage(((XmlTag)value)
						.attributeString);
				}
			}
		}

		public void mouseDragged(MouseEvent evt)
		{
		}
	}

	class EditPaneHandler implements FocusListener, CaretListener
	{
		public void focusGained(FocusEvent evt)
		{
			if(buffer == null)
				return;

			if(buffer.getBooleanProperty(
				"xml.buffer-change-parse")
				|| buffer.getBooleanProperty(
				"xml.keystroke-parse"))
			{
				if(view.getEditPane().getBuffer() != buffer)
					parse(true);
			}
			else
				showNotParsedMessage();
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

	class DocumentHandler implements DocumentListener
	{
		public void insertUpdate(DocumentEvent evt)
		{
			if(buffer.isLoaded() && parse
				&& buffer.getBooleanProperty("xml.keystroke-parse"))
				parseWithDelay();
		}

		public void removeUpdate(DocumentEvent evt)
		{
			if(buffer.isLoaded() && parse
				&& buffer.getBooleanProperty("xml.keystroke-parse"))
				parseWithDelay();
		}

		public void changedUpdate(DocumentEvent evt)
		{
		}
	}

	class Renderer extends DefaultTreeCellRenderer
	{
		public Component getTreeCellRendererComponent(JTree tree,
			Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree,value,sel,
				expanded,leaf,row,hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			Object nodeValue = node.getUserObject();
			if(nodeValue instanceof XmlTag)
			{
				XmlTag tag = (XmlTag)node.getUserObject();

				setText(showAttributes ? tag.attributeString : tag.name);
			}

			setIcon(null);

			return this;
		}
	}
}
