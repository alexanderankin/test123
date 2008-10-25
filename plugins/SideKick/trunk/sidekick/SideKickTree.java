/*
 * SideKickTree.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Stack;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PositionChanging;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;

//}}}

/**
 * The Structure Browser dockable.  One instance is created for each View.
 */
public class SideKickTree extends JPanel
       implements EBComponent, DefaultFocusComponent
{

	//{{{ Instance variables
	private RolloverButton parseBtn;
	private RolloverButton propsBtn;

	private JComboBox parserCombo;
	protected JTree tree;
	//protected JEditorPane status;
	protected JTextArea status;
	private JPanel topPanel;
	private JSplitPane splitter;
	private boolean statusShowing = false;
	private Buffer lastParsedBuffer = null;

	protected JPopupMenu configMenu;
	protected JCheckBoxMenuItem onChange;
	protected JCheckBoxMenuItem followCaret;
	protected JCheckBoxMenuItem onSave;

	protected View view;
	private Timer caretTimer;

	protected SideKickParsedData data;

	private int autoExpandTree = 0;
	private JPanel toolBox;
	private JPanel parserPanel = null;

	private JLabel search;
	private JTextField searchField;
	private JButton clearSearchBtn;
	//}}}

	//{{{ SideKickTree constructor
	public SideKickTree(View view, boolean docked)
	{
		super(new BorderLayout());

		this.view = view;

		topPanel = new JPanel(new BorderLayout());

		// create toolbar with parse button
		JToolBar buttonBox = new JToolBar();
		buttonBox.setFloatable(false);
		JToolBar filterBox = new JToolBar();
		filterBox.setFloatable(false);

		parseBtn = new RolloverButton(GUIUtilities.loadIcon("Parse.png"));

		parseBtn.setToolTipText(jEdit.getProperty("sidekick-tree.parse"));
		parseBtn.setMargin(new Insets(0,0,0,0));
		parseBtn.setRequestFocusEnabled(false);
		parseBtn.setEnabled(true);
		ActionListener ah = new ActionHandler();
		parseBtn.addActionListener(ah);

		propsBtn= new RolloverButton(GUIUtilities.loadIcon("ButtonProperties.png"));
		propsBtn.setToolTipText(jEdit.getProperty("sidekick-tree.mode-options"));
		propsBtn.addActionListener(new SideKickProperties());

		configMenu = new JPopupMenu("Parse");
		followCaret = new JCheckBoxMenuItem("Follow Caret");

		configMenu.add(followCaret);
//                configMenu = new PopupMenu("Parse on...");
		JMenuItem item = new JMenuItem("Parse on...");
		item.setEnabled(false);
		configMenu.add(item);

		onChange = new JCheckBoxMenuItem("Buffer change");
		onChange.setState(SideKick.isParseOnChange());
		onSave = new JCheckBoxMenuItem("Buffer save");
		onSave.setState(SideKick.isParseOnSave());
		configMenu.add(onChange);
		configMenu.add(onSave);
		parseBtn.setComponentPopupMenu(configMenu);
		onChange.addActionListener(ah);
		onSave.addActionListener(ah);
		followCaret.addActionListener(ah);
		search = new JLabel(jEdit.getProperty("sidekick-tree.filter.label") + " ");
		searchField = new JTextField();
		searchField.setToolTipText(jEdit.getProperty("sidekick-tree.filter.tooltip"));
		clearSearchBtn = new RolloverButton(GUIUtilities.loadIcon( "16x16/actions/edit-clear.png" ));
		clearSearchBtn.addActionListener(
			new ActionListener() {
				public void actionPerformed( ActionEvent ae ) {
					searchField.setText("");
					updateFilter();
				}
			}
		);
		clearSearchBtn.setToolTipText(jEdit.getProperty("sidekick-tree.clear-filter.tooltip"));

		buttonBox.add(parseBtn);
		buttonBox.add(propsBtn);
		filterBox.add(search);
		filterBox.add(searchField);
		filterBox.add(clearSearchBtn);

		buttonBox.add(Box.createGlue());

		parserCombo = new JComboBox();
		reloadParserCombo();
		parserCombo.setToolTipText(jEdit.getProperty("sidekick-tree.parsercombo.tooltip"));

		buttonBox.add(parserCombo);
		parserCombo.addActionListener(ah);
		parserCombo.addActionListener(
			new ActionListener() {
				public void actionPerformed( ActionEvent ae ) {
					searchField.setText("");
					updateFilter();
				}
			}
		);

		toolBox = new JPanel(new BorderLayout());
		toolBox.add(BorderLayout.NORTH, buttonBox);
		toolBox.add(BorderLayout.SOUTH, filterBox);

		topPanel.add(BorderLayout.NORTH,toolBox);

		// create a faux model that will do until a real one arrives
		TreeModel emptyModel = new DefaultTreeModel(new DefaultMutableTreeNode(null));
		emptyModel = new FilteredTreeModel((DefaultTreeModel)emptyModel, true);
		tree = buildTree(emptyModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		KeyHandler kh = new KeyHandler();
		tree.addKeyListener(kh);
		if(docked)
			tree.addMouseMotionListener(new MouseHandler());
		searchField.addKeyListener(kh);

		// looks bad with the OS X L&F, apparently...
		if(!OperatingSystem.isMacOSLF())
			tree.putClientProperty("JTree.lineStyle", "Angled");

		tree.setVisibleRowCount(10);
		tree.setCellRenderer(new Renderer());

		topPanel.add(BorderLayout.CENTER,new JScrollPane(tree));

		status = new JTextArea();
		//status.setContentType("text/html");
		status.setEditable(false);
		status.setBackground(jEdit.getColorProperty("view.bgColor"));
		status.setForeground(jEdit.getColorProperty("view.fgColor"));
		status.setFont(view.getEditPane().getTextArea().getPainter().getFont());
		JScrollPane status_scroller = new JScrollPane(status);

		splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, topPanel, status_scroller);

		status_scroller.setMinimumSize(new Dimension(0, 30));
		splitter.setOneTouchExpandable(true);
		splitter.setResizeWeight(1.0f);
		int location =splitter.getSize().height - splitter.getInsets().bottom
			- splitter.getDividerSize() - status_scroller.getMinimumSize().height;
		location = jEdit.getIntegerProperty("sidekick.splitter.location", location);


		splitter.setDividerLocation(location);

		//add(splitter);

		propertiesChanged();

		CaretHandler caretListener = new CaretHandler();

		EditPane[] editPanes = view.getEditPanes();
		for(int i = 0; i < editPanes.length; i++)
		{
			JEditTextArea textArea = editPanes[i].getTextArea();
			textArea.putClientProperty(CaretHandler.class, caretListener);
			textArea.addCaretListener(caretListener);
		}

		update();
		searchField.setRequestFocusEnabled(true);
		searchField.requestFocusInWindow();
	} //}}}

	//{{{ focusOnDefaultComponent() method
	public void focusOnDefaultComponent()
	{
		searchField.requestFocusInWindow();
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
	public void handleMessage(EBMessage msg)
	{


		if(msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate)msg;
			EditPane editPane = epu.getEditPane();

			if(epu.getWhat() == EditPaneUpdate.CREATED)
				editPane.getTextArea().addCaretListener(new CaretHandler());
		}
		else if(msg instanceof PropertiesChanged)
			propertiesChanged();
		else if(msg instanceof SideKickUpdate)
		{
			if(((SideKickUpdate)msg).getView() == view) {
				update();
			}
		}
		//else if (msg instanceof PluginUpdate) {
			// This causes a ClassCircularityError sometimes, with HTMLSideKick.
			//	reloadParserCombo();
		//}
	} //}}}

	//{{{ setStatus() method
	public void setStatus(String msg) {
		status.setText(msg);
	}// }}}

	//{{{ addData method
	protected void addData(Object obj, Stack<String> keys)
	{
		if (obj instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)obj;
			String cur_key = "";
			FilteredTreeModel model = (FilteredTreeModel)tree.getModel();
			try {
				Asset a = (Asset)node.getUserObject();
				if (a != null)
					cur_key = a.getName();
			} catch (ClassCastException ex) {
				// FIXME: May fail with null
				if (node.toString() != null)
					cur_key = node.toString();
			}
			keys.push(cur_key);
			if (model.isLeaf(node)) {
				for (String key : keys) {
					model.addSearchKey(node, key);
				}
			}

			Enumeration<DefaultMutableTreeNode> e;
			for (e = node.children(); e.hasMoreElements(); ) {
				addData(e.nextElement(), keys);
			}

			keys.pop();
		} else {
			Log.log(Log.DEBUG, this, "addData called on a node that isn't a treenode!!!!!!!!!");
		}
	} //}}}

	//{{{ updateSearchData() method
	protected void updateSearchData()
	{
		DefaultMutableTreeNode root;
		FilteredTreeModel model = (FilteredTreeModel)tree.getModel();
		root = (DefaultMutableTreeNode)model.getRoot();
		addData(root, new Stack<String>());
	} //}}}

	//{{{ update() method
	protected void update()
	{
		onChange.setState(SideKick.isParseOnChange());
		onSave.setState(SideKick.isParseOnSave());
		Buffer parsedBuffer = view.getBuffer();
		SideKickParser parser = SideKickPlugin.getParserForBuffer(parsedBuffer);
		if (parser != null) {
			Object item = parserCombo.getSelectedItem();
			if (item != parser.getName()) {
				parserCombo.setSelectedItem(parser.getName());
			}
		}

		data = SideKickParsedData.getParsedData(view);
		if(parser == null || data == null)
		{
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(parsedBuffer.getName());
			root.insert(new DefaultMutableTreeNode(
				jEdit.getProperty("sidekick-tree.not-parsed")),0);
			tree.setModel(new FilteredTreeModel(new DefaultTreeModel(root), true));
			lastParsedBuffer = null;
		}
		else
		{
			tree.setModel(new FilteredTreeModel(data.tree, true));
			lastParsedBuffer = parsedBuffer;
			if(SideKick.isFollowCaret())
				expandTreeAt(view.getTextArea().getCaretPosition());
		}
		updateSearchData();

		if (autoExpandTree == -1)
			expandAll(true);
		else if (autoExpandTree == 0)
			expandAll(false);
		else if (autoExpandTree > 0) {
			tree.expandRow( 0 );
			for (int i = 1; i < autoExpandTree; i++) {
				for ( int j = tree.getRowCount() - 1; j > 0; j-- )
				    tree.expandRow( j );
			}
		}

		if (searchField.getText().length() != 0)
			updateFilter();

	} //}}}

	//{{{ expandAll() methods
	/**
	 * Expand or collapse all nodes in the tree.
	 * @param expand if true, expand all nodes, if false, collapse all nodes
	 */
	public void expandAll( boolean expand ) {
		TreeNode root = ( TreeNode ) tree.getModel().getRoot();
		expandAll( new TreePath( root ), expand );
	}

	// recursive method to traverse children
	private void expandAll( TreePath parent, boolean expand ) {
		TreeNode node = ( TreeNode ) parent.getLastPathComponent();
		if ( node.getChildCount() >= 0 ) {
		    for ( Enumeration e = node.children(); e.hasMoreElements(); ) {
			TreeNode n = ( TreeNode ) e.nextElement();
			TreePath path = parent.pathByAddingChild( n );
			expandAll( path, expand );
		    }
		}

		// expansion or collapse must be done from the bottom up
		if ( expand ) {
		    tree.expandPath( parent );
		}
		else {
		    tree.collapsePath( parent );
		}
	}//}}}

	//{{{ buildTree() method
	protected JTree buildTree(TreeModel model)
	{
		return new CustomTree(model);
	}//}}}

	//{{{ buildActionListener() method
	/**
	 * Creates an action listener for the parse button.
	 */
	protected ActionListener buildActionListener()
	{
		return new ActionHandler();
	}//}}}

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		followCaret.setSelected(SideKick.isFollowCaret());
		Mode m = view.getBuffer().getMode();
		String mode = m != null? m.getName(): null;
		autoExpandTree = AbstractModeOptionPane.getIntegerProperty(mode, SideKick.AUTO_EXPAND_DEPTH, 1);
		// autoExpandTree = ModeOptions.getAutoExpandTreeDepth();

		if (AbstractModeOptionPane.getBooleanProperty(mode, SideKick.SHOW_STATUS)) {
			if (!statusShowing) {
				remove(topPanel);
				splitter.setTopComponent(topPanel);
				add(splitter);
			}
			statusShowing = true;
		}
		else {
			remove(splitter);
			splitter.remove(topPanel);
			add(topPanel);
			statusShowing = false;
		}
	} //}}}

	//{{{ parserList() method
	/** @return a list of parsers, sorted, with special choices
		on top */
	public static StringList parserList() {
		String[] serviceNames = ServiceManager.getServiceNames(SideKickParser.SERVICE);
		Arrays.sort(serviceNames, new MiscUtilities.StringICaseCompare());
		StringList sl = new StringList();
		sl.add(SideKickPlugin.NONE);
		sl.add(SideKickPlugin.DEFAULT);
		sl.addAll(serviceNames);
		return sl;
	} // }}}

	//{{{ reloadParserCombo() method
	void reloadParserCombo()
	{
		parserCombo.setModel(new DefaultComboBoxModel(parserList().toArray()));
		SideKickParser currentParser = SideKickPlugin.getParserForBuffer(view.getBuffer());
		if (currentParser != null )
		{
			String name = currentParser.getName();
			name = name == null ? SideKickPlugin.DEFAULT : name;
			parserCombo.setSelectedItem(name);
		}
		else
		{
			String pp = view.getBuffer().getStringProperty(SideKickPlugin.PARSER_PROPERTY);
			if (pp == SideKickPlugin.NONE) parserCombo.setSelectedItem(SideKickPlugin.NONE);
			else parserCombo.setSelectedItem(SideKickPlugin.DEFAULT);
		}
	} // }}}

	//{{{ addParserPanel() method
	void addParserPanel(SideKickParser parser)
	{
		JPanel newParserPanel = parser.getPanel();
		if (newParserPanel != parserPanel)
		{
			if (parserPanel != null)
			{
				toolBox.remove(parserPanel);
				parserPanel = null;
			}
			if (newParserPanel != null)
			{
				toolBox.add(BorderLayout.WEST,newParserPanel);
				parserPanel = newParserPanel;
			}
		}
		focusOnDefaultComponent();
	} // }}}

	//{{{ removeParserPanel() method
	void removeParserPanel()
	{
		if (parserPanel != null)
		{
			toolBox.remove(parserPanel);
			parserPanel = null;
		}
	} // }}}

	//{{{ expandTreeWithDelay() method
	/**
	 * Expands the tree after a delay.
	 * The delay timer is restarted each time this method is called.
	 */
	protected void expandTreeWithDelay()
	{
		if(caretTimer != null) {
			caretTimer.stop();
		}
		else
		{
			caretTimer = new Timer(0,new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					// If the filter is *not* persistent, then clear
					// it when the tree is expanded for the current
					// caret position.
					if (!jEdit.getBooleanProperty("sidekick.persistentFilter")) {
						if (! searchField.getText().isEmpty()) {
							searchField.setText("");
							updateFilter(false);
						}
					}
					TextArea textArea = view.getTextArea();
					int caret = textArea.getCaretPosition();
					Selection s = textArea.getSelectionAtOffset(caret);
					expandTreeAt(s == null ? caret : s.getStart());
				}
			});
			caretTimer.setInitialDelay(500);
			caretTimer.setRepeats(false);
		}
		caretTimer.start();
	} //}}}

	//{{{ expandTreeAt() method
	protected void expandTreeAt(int dot)
	{
		if(data == null)
			return;

		TreePath treePath = data.getTreePathForPosition(dot);
		if(treePath != null)
		{
			tree.expandPath(treePath);
			tree.setSelectionPath(treePath);
			if (jEdit.getBooleanProperty("sidekick.scrollToVisible")) {
				Rectangle r = tree.getPathBounds(treePath);
				if (r != null) {
					r.width = 1;
					tree.scrollRectToVisible(r);
				} else
			tree.scrollPathToVisible(treePath);
			}
		}
	} //}}}

	//{{{ Inner classes

	//{{{ CustomTree class
	/**
	 * A JTree with added mouse handling.  Other plugins providing similar trees
	 * can extend CustomTree and override the mouse methods.
	 */
	protected class CustomTree extends JTree
	{
		protected CustomTree(TreeModel model)
		{
			super(model);
		}

		protected void processMouseEvent(MouseEvent evt)
		{
			switch(evt.getID())
			{
			//{{{ MOUSE_PRESSED...
			case MouseEvent.MOUSE_PRESSED:
				TreePath path = getPathForLocation(
					evt.getX(),evt.getY());
				if(path != null)
				{
					Object value = ((DefaultMutableTreeNode)path
						.getLastPathComponent()).getUserObject();

					if(value instanceof IAsset)
					{
						IAsset asset = (IAsset)value;

						JEditTextArea textArea = view.getTextArea();
						EditPane editPane = view.getEditPane();

						if(evt.getClickCount() == 2)
						{
							doubleClicked(view,asset,path);
						}
						else if(evt.isShiftDown())
						{
							shiftClick(view,asset,path);
						}
						else if(evt.isControlDown())
						{
							controlClick(view,asset,path);
						}
						else {
							EditBus.send(new PositionChanging(editPane));
							textArea.setCaretPosition(asset.getStart().getOffset());
						}

					}
					if (!jEdit.getBooleanProperty("sidekick.persistentFilter")) {
						if (! searchField.getText().isEmpty()) {
							searchField.setText("");
							updateFilter(false);
						}
					}
				}

				super.processMouseEvent(evt);
				break; //}}}
			//{{{ MOUSE_EXITED...
			case MouseEvent.MOUSE_EXITED:
				view.getStatus().setMessage(null);
				super.processMouseEvent(evt);
				break; //}}}
			default:
				super.processMouseEvent(evt);
				break;
			}
		}

		protected void doubleClicked(View view, IAsset asset, TreePath path)
		{
		}

		protected void shiftClick(View view, IAsset asset, TreePath path)
		{
			JEditTextArea textArea = view.getTextArea();
			textArea.setCaretPosition(asset.getEnd().getOffset());
			Selection.Range range = new Selection.Range(
				asset.getStart().getOffset(),
				asset.getEnd().getOffset() );
			textArea.addToSelection(range);
		}

		protected void controlClick(View view, IAsset asset, TreePath path)
		{
			JEditTextArea textArea = view.getTextArea();
			textArea.getDisplayManager().narrow(
				textArea.getLineOfOffset(asset.getStart().getOffset()),
				textArea.getLineOfOffset(asset.getEnd().getOffset()));
		}
	} //}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		/** A counter for counting how deep in recursion we are.
		 *  Since a call to reloadParserCombo can cause itemselected events
		 *  from the parserCombo,
		 */
		int level=0;
		public void actionPerformed(ActionEvent evt)
		{

			// Workaround to avoid infinite recursion as a result of parsercombos
			// updating
			synchronized (this) {
				if (evt.getSource() == parseBtn) level=0;
				level++;
				if (level>1) {
					level--;
					return;
				}
			}

			Buffer b = view.getBuffer();
			jEdit.setIntegerProperty("sidekick.splitter.location", splitter.getDividerLocation());
			if (evt.getSource() == onSave) {
				SideKick.setParseOnSave(onSave.isSelected());
				propertiesChanged();
			}
			if (evt.getSource() == followCaret) {
				boolean v = followCaret.isSelected();
				SideKick.setFollowCaret(followCaret.isSelected());
				if (v) {
					onChange.setSelected(true);
				}
				propertiesChanged();
			}

			else if (evt.getSource() == onChange) {
				boolean v = onChange.isSelected();
				SideKick.setParseOnChange(v);
				if (!v) followCaret.setSelected(false);
				propertiesChanged();
			}
			else if (evt.getSource() ==  parserCombo ) {
				Object selectedParser = parserCombo.getSelectedItem();
//                        	String preferredParser = b.getStringProperty(SideKickPlugin.PARSER_PROPERTY);
				if (selectedParser.toString().equals(SideKickPlugin.NONE)) {
					b.setProperty("usermode", Boolean.TRUE);
					SideKickPlugin.setParserForBuffer(b, selectedParser.toString());
				}
				else if (selectedParser.toString().equals(SideKickPlugin.DEFAULT)) {
					b = view.getBuffer();
					b.setProperty("usermode", Boolean.FALSE);
					Mode m = b.getMode();
					if (m == null) {
						Log.log(Log.ERROR, this, "SideKick: can't determine mode of current buffer:" + b);
					}
					else {
						SideKickParser newParser = SideKickPlugin.getParserForMode(m);
						if (newParser == null)
							SideKickPlugin.setParserForBuffer(b, SideKickPlugin.NONE);
						else
							SideKickPlugin.setParserForBuffer(b, newParser.getName());
					}
				}
				else {
					SideKickPlugin.setParserForBuffer(b, selectedParser.toString());
					b.setProperty("usermode", Boolean.TRUE);
				}
				propertiesChanged();

			}
			if (evt.getSource() == parseBtn || evt.getSource() == parserCombo) {
				level = 0;
				Object usermode =  b.getProperty("usermode");
				if (usermode == null || usermode == Boolean.FALSE) {
					SideKickParser sp = SideKickPlugin.getParserForBuffer(b);
					if (sp == null)  return;
					else reloadParserCombo();
				}
				SideKickPlugin.parse(view,true);
			}
			level--;
		}
	} //}}}

	//{{{ CaretHandler class
	class CaretHandler implements CaretListener
	{
		public void caretUpdate(CaretEvent evt)
		{
			if (!view.getBuffer().equals( lastParsedBuffer)) {
				return;
			}
			if(evt.getSource() == view.getTextArea() && SideKick.isFollowCaret())
			{
				expandTreeWithDelay();
			}
		}
	} //}}}

	public void updateFilter(boolean with_delay)
	{
		FilteredTreeModel ftm = (FilteredTreeModel)tree.getModel();

		if (searchField.getText().length() == 0) {
			ftm.clearFilter();
			ftm.reset();
			if (autoExpandTree == -1)
				expandAll(true);
			else if (autoExpandTree == 0)
				expandAll(false);
			else if (autoExpandTree > 0) {
				tree.expandRow( 0 );
				for (int i = 1; i < autoExpandTree; i++) {
					for ( int j = tree.getRowCount() - 1; j > 0; j-- )
						tree.expandRow( j );
				}
			}

			if (SideKick.isFollowCaret() && with_delay) {
				expandTreeWithDelay();
			}
		} else {
			ftm.filterByText(searchField.getText());
			expandAll(true);
		}
	}

	public void updateFilter()
	{
		updateFilter(true);
	}

	public void setSearchFilter(String text)
	{
		searchField.setText(text);
		updateFilter();
	}
	public String getSearchFilter()
	{
		return searchField.getText();
	}

	//{{{ KeyHandler class
	class KeyHandler extends KeyAdapter
	{

		protected void next()
		{
			DefaultMutableTreeNode node =  (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			FilteredTreeModel model = (FilteredTreeModel)tree.getModel();
			if (node == null) {
				node = (DefaultMutableTreeNode)model.getRoot();
			}
			if (model.isLeaf(node)) {
				node = node.getNextLeaf();
			} else {
				Enumeration<DefaultMutableTreeNode> e = node.depthFirstEnumeration();
				node = e.nextElement();
			}
			if (node != null) {
				while ((node != null) && (!model.isVisible(node))) {
					node = node.getNextLeaf();
				}
				if (node != null) {
					TreePath p = new TreePath(node.getPath());
					tree.setSelectionPath(p);
					//tree.scrollRowToVisible(tree.getRowForPath(p));
					Rectangle r = tree.getPathBounds(p);
					if (r != null) {
						r.width = 1;
						tree.scrollRectToVisible(r);
					}
				}
			}
		}

		protected void prev()
		{
			DefaultMutableTreeNode node =  (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			FilteredTreeModel model = (FilteredTreeModel)tree.getModel();
			if (node == null) {
				node = (DefaultMutableTreeNode)model.getRoot();
			}
			// If the node isn't a leaf, use depthFirstEnumeration to find the next
			// leaf (which moves us forward), then get the previous (to get the previous
			// node from where we started).
			if (!model.isLeaf(node)) {
				Enumeration<DefaultMutableTreeNode> e = node.depthFirstEnumeration();
				node = e.nextElement();
			}
			node = node.getPreviousLeaf();
			if (node != null) {
				while ((node != null) && (!model.isVisible(node))) {
					node = node.getPreviousLeaf();
				}
				if (node != null) {
					TreePath p = new TreePath(node.getPath());
					tree.setSelectionPath(p);
					Rectangle r = tree.getPathBounds(p);
					if (r != null) {
						r.width = 1;
						tree.scrollRectToVisible(r);
					}
				}
			}
		}

		public void keyPressed(KeyEvent evt)
		{
			if(caretTimer != null)
				caretTimer.stop();


			switch (evt.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
					evt.consume();
					if (searchField.getText().length() == 0) {
						view.getDockableWindowManager().hideDockableWindow(SideKickPlugin.NAME);
					} else {
						searchField.setText("");
						updateFilter();
					}
					break;
				case KeyEvent.VK_ENTER:
					evt.consume();

					TreePath path = tree.getSelectionPath();

					if(path != null)
					{
						Object value = ((DefaultMutableTreeNode)path
							.getLastPathComponent()).getUserObject();

						if(value instanceof IAsset)
						{
							IAsset asset = (IAsset)value;

							JEditTextArea textArea = view.getTextArea();

							if(evt.isShiftDown()) {
								textArea.setCaretPosition(asset.getEnd().getOffset());
								textArea.addToSelection(
									new Selection.Range(
										asset.getStart().getOffset(),
										asset.getEnd().getOffset()));
							} else {
								if (!jEdit.getBooleanProperty("sidekick.persistentFilter")) {
									if (! searchField.getText().isEmpty()) {
										searchField.setText("");
										updateFilter();
									}
								}
								textArea.setCaretPosition(asset.getStart().getOffset());
								textArea.requestFocus();
							}
						}
					}
					break;
				case KeyEvent.VK_BACK_SPACE:
					evt.consume();
					if (searchField.getText().length() <= 1) {
						searchField.setText("");
					} else {
						String s = searchField.getText();
						s = s.substring(0, s.length() - 1);
						searchField.setText(s);
					}
					updateFilter();
					break;
				case KeyEvent.VK_DOWN:
					evt.consume();
					next();
					break;
				case KeyEvent.VK_UP:
					evt.consume();
					prev();
					break;
				case KeyEvent.VK_PAGE_UP:
				{
					evt.consume();

					int offset = tree.getScrollableUnitIncrement(tree.getParent().getBounds(), javax.swing.SwingConstants.VERTICAL, 0);
					for (int i = 0; i < offset; ++i)
						prev();
				}
				break;
				case KeyEvent.VK_PAGE_DOWN:
				{
					evt.consume();

					int offset = tree.getScrollableUnitIncrement(tree.getParent().getBounds(), javax.swing.SwingConstants.VERTICAL, 0);
					for (int i = 0; i < offset; ++i)
						next();
				}
				break;
				default:
					break;
			}
		}

		public void keyTyped(KeyEvent evt)
		{
			Character c = evt.getKeyChar();
			// TODO: What is the correct combo here to filter
			// non-identifier characters?
			if (Character.isLetterOrDigit(c) ||
				(" _!@$%^&*()_+-=[]{};':\",.<>/?\\|".indexOf(c) != -1))
			{
				evt.consume();
				searchField.setText(searchField.getText() + c);
				updateFilter();
			}
		}
	} //}}}

	//{{{ MouseHandler class
	class MouseHandler extends MouseMotionAdapter
	{
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

				if(value instanceof IAsset)
				{
					String info = ((IAsset)value).getShortString();
					view.getStatus().setMessage(info);
				}
			}
		}
	} //}}}

	// {{{ SidekickProperties class
	/**
	 * This class creates an options dialog containing an optionpane
	 * for each SideKick service, as well as one for SideKick itself.
	 * This properties pane is mode-sensitive.
	 *
	 * sidekick options, and one for the specific plugin's option pane.
	 */
	class SideKickProperties implements ActionListener {

		public void actionPerformed(ActionEvent e)
		{
			try {
				new ModeOptionsDialog(view);
			}
			catch (Exception ex) {
				Log.log (Log.ERROR, this, "dialog create failed", ex);
			}

		}

	} // }}}

	//{{{ Renderer class
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
			if(nodeValue instanceof IAsset)
			{
				IAsset asset = (IAsset)node.getUserObject();

				setText(asset.getShortString());
				setIcon(asset.getIcon());
			}
			// is root?
			else if(node.getParent() == null)
			{
				setIcon(org.gjt.sp.jedit.browser.FileCellRenderer
					.fileIcon);
			}
			else
				setIcon(null);

			return this;
		}
	} //}}}


	//}}}
}
