package browser;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.util.Log;

import tags.TagsPlugin;
import browser.TagDB.Record;
import browser.TagDB.RecordSet;

@SuppressWarnings("serial")
public class ClassHierarchy extends JPanel implements DefaultFocusComponent {
	private static final String CLASS_BROWSER_CLASS_HIERARCHY = "class-browser-class-hierarchy";

	private View view;

	private JTree tree;

	private JList members;

	private JSplitPane splitPane;

	private JPanel topPanel;

	private JToolBar hierarchyToolbar;
	private JToggleButton superTypeHierButton;
	private JToggleButton subTypeHierButton;

	private JToolBar memberToolbar;
	private JToggleButton selfMembersButton;
	private JToggleButton derivedMembersButton;
	
	DefaultMutableTreeNode superTypeRoot;

	DefaultMutableTreeNode subTypeRoot;

	private TagDB db = null;

	private static ClassHierarchy instance;

	private Vector<String> derivedClasses = new Vector<String>();

	private Hashtable<String, Vector<Object>> membersHash = new Hashtable<String, Vector<Object>>();
	private Hashtable<String, Vector<Object>> derivedMembersHash =
		new Hashtable<String, Vector<Object>>();
	
	public ClassHierarchy(View view) {
		super(new BorderLayout());

		this.view = view;
		instance = this;
		buildTree();
		
		members = new JList();
		members.setCellRenderer(new MemberCellRenderer());
		members.addMouseListener(new MemberCellActionHandler(members));
		
		buildHierarchyToolbar();
		buildMemberToolbar();
		
		JPanel treePanel = new JPanel();
		treePanel.setLayout(new BorderLayout());
		treePanel.add(hierarchyToolbar, BorderLayout.NORTH);
		treePanel.add(new JScrollPane(tree), BorderLayout.CENTER);

		JPanel memberPanel = new JPanel();
		memberPanel.setLayout(new BorderLayout());
		memberPanel.add(memberToolbar, BorderLayout.NORTH);
		memberPanel.add(new JScrollPane(members), BorderLayout.CENTER);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treePanel,
				memberPanel);
		splitPane.setOneTouchExpandable(true);

		topPanel = new JPanel(new BorderLayout());
		topPanel.add(BorderLayout.CENTER, splitPane);

		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.CENTER);

		db = new TagDB();
	}

	private void updateDB(View view) {
		boolean updated = false;
		Vector tagIndexFiles = TagsPlugin.getTagFileManager().getTagIndexFiles(
				view, ".");
		for (int i = 0; i < tagIndexFiles.size(); i++) {
			String tagFile = (String) tagIndexFiles.get(i);
			if (tagFile.equals(".."))
				continue;
			if (!db.hasTagFile(tagFile)) {
				updated = true;
				db.addTagFile(tagFile);
			}
		}
		if (updated)
			derivedClasses = db.findMatches(db.getInheritsRegExp("\\S+"));
	}

	private Record findClass(String clazz) {
		RecordSet rs = db.getTag(clazz, derivedClasses);
		if (rs.isEmpty())
			rs = db.getTag(clazz);
		while (rs.next()) {
			Record r = rs.getRecord();
			if (db.isScopeTag(r))
				return r;
		}
		return null;
	}

	private void addSuperClasses(DefaultMutableTreeNode node,
			HashSet<String> classes) {
		Object obj = node.getUserObject();
		String name;
		if (obj instanceof Record) {
			Record tag = (Record) obj;
			name = tag.getName();
			String inheritsStr = tag.get(TagDB.INHERITS_COL);
			if (inheritsStr == null)
				return;
			String[] superClasses = inheritsStr.split(",");
			for (int i = 0; i < superClasses.length; i++) {
				String superClass = superClasses[i];
				classes.add(superClass);
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(
						superClass);
				node.add(child);
				addSuperClasses(child, classes);
			}
		} else {
			name = (String) obj;
			Record clazzTag = findClass(name);
			if (clazzTag == null)
				return;
			node.setUserObject(clazzTag);
			addSuperClasses(node, classes);
		}
	}

	private void addSubClasses(DefaultMutableTreeNode node,
			HashSet<String> classes) {
		Object obj = node.getUserObject();
		String name;
		if (obj instanceof Record)
			name = ((Record) obj).getName();
		else
			name = (String) obj;
		RecordSet rs = db.getInherits(name, derivedClasses);
		while (rs.next()) {
			Record subclass = rs.getRecord();
			classes.add(subclass.getName());
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(subclass);
			node.add(child);
			addSubClasses(child, classes);
		}
	}

	private void setClass(View view, String clazz) {
		updateDB(view);
		Record clazzTag = findClass(clazz);
		Object obj = (clazzTag != null) ? clazzTag : clazz;
		HashSet<String> classesInHierarchy = new HashSet<String>();
		classesInHierarchy.add(clazz);
		superTypeRoot = new DefaultMutableTreeNode(obj);
		addSuperClasses(superTypeRoot, classesInHierarchy);
		subTypeRoot = new DefaultMutableTreeNode(obj);
		addSubClasses(subTypeRoot, classesInHierarchy);
		if (classesInHierarchy.size() == 1 && clazzTag == null)
		{
			Log.log(Log.ERROR, ClassHierarchy.class, "No hierarchy for class '" + clazz + "'");
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		updateTree();
		getMembers(classesInHierarchy);
		setMembers();
	}

	private String tagName(Object obj)
	{
		if (obj instanceof Record)
			return ((Record)obj).getName();
		return obj.toString();
	}
	private void buildDerivedMembers(String clazz)
	{
		HashSet<String> seen = new HashSet<String>();
		Vector<Object> derivedMembers = new Vector<Object>();
		LinkedList<String> classes = new LinkedList<String>();
		classes.add(clazz);
		while (! classes.isEmpty())
		{
			String curClass = classes.removeFirst();
			Vector<Object> classMembers = membersHash.get(curClass);
			if (classMembers == null)
				continue;
			for (int i = 0; i < classMembers.size(); i++)
			{
				Object member = classMembers.get(i);
				String memberName = tagName(member);
				if (! seen.contains(memberName))
				{
					derivedMembers.add(member);
					seen.add(memberName);
				}
			}
			Record classTag = findClass(curClass);
			String inheritsStr = classTag.get(TagDB.INHERITS_COL);
			if (inheritsStr != null)
			{
				String [] children = inheritsStr.split(",");
				for (int i = 0; i < children.length; i++)
					classes.add(children[i]);
			}
		}
		derivedMembersHash.put(clazz, derivedMembers);
	}
	private String getSelectedClassName()
	{
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if (node == null)
			return null;
		Object obj = node.getUserObject();
		String clazz = tagName(obj);
		return clazz;
	}
	private void getMembers(HashSet<String> classes) {
		membersHash.clear();
		derivedMembersHash.clear();
		StringBuffer classStr = new StringBuffer("(");
		Iterator<String> it = classes.iterator();
		while (it.hasNext())
			classStr.append(it.next() + "|");
		classStr.replace(classStr.length() - 1, classStr.length(), ")");
		RecordSet rs = db.getMembers(classStr.toString());
		while (rs.next()) {
			Record member = rs.getRecord();
			String memberOf = member.get(TagDB.SCOPE_COL);
			if (memberOf == null)
				continue; // Shouldn't happen, but just in case ...
			Vector<Object> classMembers;
			if (membersHash.containsKey(memberOf))
				classMembers = membersHash.get(memberOf);
			else {
				classMembers = new Vector<Object>();
				membersHash.put(memberOf, classMembers);
			}
			classMembers.add(member);
		}
	}
	private void setMembers() {
		String clazz = getSelectedClassName();
		if (clazz == null)
			return;
		if (derivedMembersButton.isSelected())
		{
			if (clazz == null)
				return;
			if (! derivedMembersHash.containsKey(clazz))
				buildDerivedMembers(clazz);
			members.setListData(derivedMembersHash.get(clazz));
		}
		else
		{
			if (membersHash.containsKey(clazz))
				members.setListData(membersHash.get(clazz));
			else
				members.setListData(new Vector<String>());
		}
	}

	public static void set(View view) {
		view.getDockableWindowManager().showDockableWindow(
				CLASS_BROWSER_CLASS_HIERARCHY);
		String clazz = JOptionPane.showInputDialog(null, "Enter class name:");
		instance.setClass(view, clazz);
	}

	public static void setSelected(View view)
	{
		view.getDockableWindowManager().showDockableWindow(
				CLASS_BROWSER_CLASS_HIERARCHY);
		String selected = view.getTextArea().getSelectedText();
		if (selected == null)
			selected = TagsPlugin.getTagNameAtCursor(view.getTextArea());
		if (selected == null)
		{
			Log.log(Log.ERROR, ClassHierarchy.class, "No 'class' selected for hierarchy");
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		instance.setClass(view, selected);
	}
	
	private void buildHierarchyToolbar() {
		hierarchyToolbar = new JToolBar();
		hierarchyToolbar.setFloatable(false);
		superTypeHierButton = new JToggleButton("Base", true);
		ActionListener l = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateTree();
			}
		};
		superTypeHierButton.addActionListener(l);
		subTypeHierButton = new JToggleButton("Derived", false);
		subTypeHierButton.addActionListener(l);
		ButtonGroup buttons = new ButtonGroup();
		buttons.add(superTypeHierButton);
		buttons.add(subTypeHierButton);
		hierarchyToolbar.add(superTypeHierButton);
		hierarchyToolbar.add(subTypeHierButton);
	}
	private void buildMemberToolbar() {
		memberToolbar = new JToolBar();
		memberToolbar.setFloatable(false);
		selfMembersButton = new JToggleButton("Self", true);
		derivedMembersButton = new JToggleButton("Inherited", false);
		ActionListener l = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMembers();
			}
		};
		selfMembersButton.addActionListener(l);
		derivedMembersButton.addActionListener(l);
		ButtonGroup buttons = new ButtonGroup();
		buttons.add(selfMembersButton);
		buttons.add(derivedMembersButton);
		memberToolbar.add(selfMembersButton);
		memberToolbar.add(derivedMembersButton);
	}

	private void updateTree() {
		DefaultMutableTreeNode root = superTypeHierButton.isSelected() ? superTypeRoot
				: subTypeRoot;
		tree.setModel(new DefaultTreeModel(root));
		tree.setSelectionRow(0);
	}

	private void buildTree() {
		DefaultTreeCellRenderer renderer = new HierarchyCellRenderer();
		tree = new JTree();
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
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				setMembers();
			}
		});
		// tree.addMouseListener(new MouseHandler());

		ToolTipManager.sharedInstance().registerComponent(tree);
	}

	public void focusOnDefaultComponent() {
		tree.requestFocus();
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
	 * MemberCellRenderer
	 **************************************************************************/
	@SuppressWarnings("serial")
	class MemberCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
			if (value instanceof Record) {
				ImageIcon icon = db.getIcon((Record) value);
				if (icon != null)
					label.setIcon(icon);
			}
			return label;
		}
	}

	/***************************************************************************
	 * ScopeCellRenderer
	 **************************************************************************/
	@SuppressWarnings("serial")
	class HierarchyCellRenderer extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			JLabel label = (JLabel) super.getTreeCellRendererComponent(tree,
				value, isSelected, expanded, leaf, row, hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject(); 
			if (obj instanceof Record) {
				ImageIcon icon = db.getIcon((Record) obj);
				if (icon != null)
					label.setIcon(icon);
			}
			return label;
		}
	}
	
	/***************************************************************************
	 * MemberCellActionHandler
	 **************************************************************************/
	class MemberCellActionHandler extends MouseAdapter {
		protected JList list;

		public MemberCellActionHandler(JList l) {
			list = l;
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				Object obj = list.getSelectedValue();
				if (obj instanceof Record) {
					((Record) obj).jump(view);
				}
			}
		}
	}
	
	/****************************************************************************
	 * HierarchyCellActionHandler
	 ***************************************************************************/
	class HierarchyCellActionHandler extends MouseAdapter {
		protected JTree tree;
		
		public HierarchyCellActionHandler(JTree t)
		{
			tree = t;
		}
		
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				TreePath path = tree.getSelectionPath();
                Object obj = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
				if (obj instanceof Record) {
					((Record) obj).jump(view);
				}
			}
		}
	}
}
