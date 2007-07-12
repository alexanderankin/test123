/*
Copyright (C) 2007  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package browser;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.util.Log;

import tags.TagsPlugin;

@SuppressWarnings("serial")
public class CallTree extends JPanel implements DefaultFocusComponent, CallTreeActions {
	static private HashMap<View, CallTree> viewMap =
		new HashMap<View, CallTree>();
	private View view;
	private JTree tree;
	private JPanel topPanel;
	FunctionNode root = null;
	Pattern spaces = Pattern.compile("\\s+");
	Hashtable<String, Vector<FunctionTag>> fileTags = new Hashtable<String, Vector<FunctionTag>>(); 

	static public CallTree instanceFor(View view, String position) {
		CallTree instance = viewMap.get(view);
		if (instance == null) {
			System.err.println("Creating a new inst");
			instance = new CallTree(view);
			viewMap.put(view, instance);
		}
		return instance;
	}
	
	private CallTree(View view) {
		super(new BorderLayout());

		this.view = view;
		buildTree();
		
		JScrollPane treePane = new JScrollPane(tree);
		
		topPanel = new JPanel(new BorderLayout());
		topPanel.add(BorderLayout.CENTER, treePane);

		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.CENTER);
	}

	private void showCallTree(View view, String function) {
		long start = System.currentTimeMillis();
		FunctionTag tag = new FunctionTag(function, null, 0);
		root = new FunctionNode(tag);
		updateTree();
		long end = System.currentTimeMillis();
		Log.log(Log.DEBUG, CallTree.class, "Callers of '" + function
				+ "' took " + (end - start) * .001 + " seconds.");
	}

	private TreeSet<FunctionTag> getCallers(FunctionTag func)
	{
		TreeSet<FunctionTag> callers = new TreeSet<FunctionTag>();
		String function = func.getName();
		GlobalLauncher launcher = new GlobalLauncher();
		Vector<GlobalRecord> records =
			launcher.run("-r -x " + function, getBufferDirectory());
		for (int i = 0; i < records.size(); i++) {
			GlobalRecord r = records.get(i);
			String file = r.getFile();
			int line = r.getLine();
			FunctionTag caller = findFunctionContaining(file, line);
			if (caller != null)
				callers.add(caller);
		}
		return callers;
	}

	private String getBufferDirectory() {
		File file = new File(view.getBuffer().getPath());
		return file.getParent();
	}

	private FunctionTag findFunctionContaining(String file, int line) {
		// First, find all functions defined in 'file'
		if (! fileTags.containsKey(file))
			fileTags.put(file, getFileTags(file));
        FunctionTag func = null;
        Vector<FunctionTag> functions = fileTags.get(file);
        for (int i = 0; i < functions.size(); i++) {
        	FunctionTag tag = functions.get(i);
        	int lineNo = tag.getLine();
        	if (lineNo < line)
	        	func = tag;
        	else
        		break;
        }
		return func;
	}

	private Vector<FunctionTag> getFileTags(String file) {
		Vector<FunctionTag> tags = new Vector<FunctionTag>();
		GlobalLauncher launcher = new GlobalLauncher();
		Vector<GlobalRecord> records = 
			launcher.run("-f " + file, getBufferDirectory());
		for (int i = 0; i < records.size(); i++) {
			GlobalRecord r = records.get(i);
			String caller = r.getName();
			int callerLine = r.getLine();
			FunctionTag func = new FunctionTag(caller, getBufferDirectory() + "/" + file, callerLine);
			tags.add(func);
		}
		return tags;
	}

	public void show(View view) {
		String selected = view.getTextArea().getSelectedText();
		if (selected == null)
			selected = TagsPlugin.getTagNameAtCursor(view.getTextArea());
		if (selected == null) {
			Log.log(Log.ERROR, CallTree.class,
					"No function selected");
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		showCallTree(view, selected);
	}

	private void updateTree() {
		tree.setModel(new DefaultTreeModel(root));
		tree.setSelectionRow(0);
	}

	private void buildTree() {
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		tree = new JTree();
		//tree.setModel(emptyHierarchy);
		tree.setCellRenderer(renderer);
		tree.addMouseListener(new HierarchyCellActionHandler(tree));
		renderer.setLeafIcon(null);
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);

		tree.setScrollsOnExpand(true);
		tree.setShowsRootHandles(true);

		tree.putClientProperty("JTree.lineStyle", "Angled");

		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		ToolTipManager.sharedInstance().registerComponent(tree);
	}

	public void focusOnDefaultComponent() {
		tree.requestFocus();
	}

	class FunctionNode extends DefaultMutableTreeNode
	{
		boolean createdChildren = false;
		FunctionNode(FunctionTag obj)
		{
			super(obj);
		}
		private void populateChildren()
		{
			if (createdChildren)
				return;
			createdChildren = true;
			FunctionTag obj = (FunctionTag) getUserObject();
			Iterator<FunctionTag> callers = getCallers(obj).iterator();
			while (callers.hasNext())
				add(new FunctionNode(callers.next()));
		}
		@Override
		public int getChildCount() {
			populateChildren();
			return super.getChildCount();
		}
		
	}
	/***************************************************************************
	 * MouseHandler, Context- and Options menu
	 **************************************************************************/
	class MouseHandler extends MouseAdapter {
		public void mousePressed(MouseEvent evt) {
			if (evt.isConsumed())
				return;

			TreePath path1 = tree.getPathForLocation(evt.getX(), evt.getY());
			if (path1 == null)
				return;
			// goToSelectedNode();
		}

	}

	/***************************************************************************
	 * HierarchyCellActionHandler
	 **************************************************************************/
	class HierarchyCellActionHandler extends MouseAdapter {
		protected JTree tree;

		public HierarchyCellActionHandler(JTree t) {
			tree = t;
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				TreePath path = tree.getSelectionPath();
				Object obj = ((DefaultMutableTreeNode) path
						.getLastPathComponent()).getUserObject();
				//System.err.println("Double-click on " + obj.toString());
				if (obj instanceof FunctionTag) {
					//System.err.println("... jumping");
					((FunctionTag) obj).jump();
				}
			}
		}
	}
}
