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
		buttonBox.putClientProperty("JToolBar.isRollover",Boolean.TRUE);

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

		propertiesChanged();
	}

	public void parse()
	{
		parse(true,true);
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
			textArea.addFocusListener(editPaneHandler);
			textArea.addCaretListener(editPaneHandler);
		}

		errorSource = new DefaultErrorSource("XML");
		EditBus.addToNamedList(ErrorSource.ERROR_SOURCES_LIST,errorSource);
		EditBus.addToBus(errorSource);

		parse(true,true);
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
		}

		if(thread != null)
			stopThread();

		errorSource.clear();
		EditBus.removeFromNamedList(ErrorSource.ERROR_SOURCES_LIST,errorSource);
		EditBus.removeFromBus(errorSource);
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
					parse(false,true);
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
					parse(false,true);
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
					parse(false,true);
				}
				else
					showNotParsedMessage();
			}
		}
		else if(msg instanceof PropertiesChanged)
			propertiesChanged();
	}

	// package-private members
	void parsingComplete(TreeModel model)
	{
		thread = null;

		tree.setModel(model);
		expandTagAt(view.getTextArea().getCaretPosition());
	}

	void addError(int type, String path, int line, String message)
	{
		// FIXME?
		if(path.startsWith("file://"))
			path = path.substring(7);

		errorSource.addError(type,path,line,0,0,message);
	}

	void stopThread()
	{
		thread.stop();

		/* thread.interrupt();
		try
		{
			thread.join();
		}
		catch(InterruptedException ie)
		{
		} */
	}

	// private members
	private JButton parseBtn;
	private JTree tree;

	private boolean showAttributes;
	private boolean parse;
	private int delay;

	private View view;
	private Buffer buffer;
	private XmlParseThread thread;
	private Timer timer;
	private DefaultErrorSource errorSource;
	private DocumentHandler documentHandler;
	private EditPaneHandler editPaneHandler;

	private void propertiesChanged()
	{
		boolean newShowAttributes = jEdit.getBooleanProperty("xml.show-attributes");
		try
		{
			delay = Integer.parseInt(jEdit.getProperty("xml.delay"));
		}
		catch(NumberFormatException nf)
		{
			delay = 1500;
		}

		// due to possible change in show attributes setting
		if(newShowAttributes != showAttributes)
		{
			showAttributes = newShowAttributes;
			parse(true,true);
		}
	}

	private void parse(final boolean force, final boolean showParsingMessage)
	{
		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				if(!force && view.getBuffer() == buffer)
				{
					// don't reparse when switching between
					// split panes editing the same buffer
					return;
				}

				// remove listener from old buffer
				if(buffer != null)
					buffer.removeDocumentListener(documentHandler);

				buffer = view.getBuffer();

				// add listener to new buffer
				buffer.addDocumentListener(documentHandler);

				parse = buffer.getBooleanProperty("xml.parse");

				_parse(showParsingMessage);
			}
		});
	}

	// only ever called from parse()
	private void _parse(boolean showParsingMessage)
	{
		if(thread != null)
			stopThread();

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

		errorSource.clear();

		thread = new XmlParseThread(this,buffer);
		thread.start();
	}

	private void showNotParsedMessage()
	{
		if(thread != null)
			stopThread();

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
				parse(true,false);
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
			// invalid tags and parse errors have this
			if(tag.end == null)
				return false;

			// check if the caret in inside this tag
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

			// invalid tags and parse errors have this
			if(tag.end == null)
				return false;

			// check if the caret in inside this tag
			if(dot >= tag.start.getOffset() && dot <= tag.end.getOffset())
				return true;
		}

		return false;
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			parse();
		}
	}

	class MouseHandler extends MouseAdapter implements MouseMotionListener
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

				JEditTextArea textArea = view.getTextArea();

				textArea.setCaretPosition(tag.start.getOffset());

				// Invalid tags and parse errors have no end pos
				if(tag.end != null)
				{
					textArea.setSelection(
						new Selection.Range(tag.start
						.getOffset(),tag.end.getOffset()));
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
				parse(false,true);
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
