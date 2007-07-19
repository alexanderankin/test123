package ctags.sidekick;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import treetable.AbstractTreeTableModel;
import treetable.JTreeTable;
import treetable.TreeTableModel;

@SuppressWarnings("serial")
public class CtagsTreeTable extends JPanel {
	private static final String GROUP = "";

	private static class CtagsTreeTableNode
	{
		private Vector<CtagsTreeTableNode> children =
			new Vector<CtagsTreeTableNode>();
		private Object object = null;
		
		public CtagsTreeTableNode(Object obj) {
			object = obj;
		}
		void setUserObject(Object obj) {
			object = obj;
		}
		Object getUserObject() {
			return object;
		}
		Object getColumn(String colName) {
			boolean groupCol = colName.equals(GROUP);
			if (object instanceof Tag) {
				String name = groupCol ? "k_tag" : colName;
				return ((Tag)object).getInfo().get(name);
			}
			if (colName.equals(GROUP))
				return object;
			return null;
		}
		public void addChildCounts() {
			Enumeration<ctags.sidekick.CtagsTreeTable.CtagsTreeTableNode> children =
				this.children.elements();
			while (children.hasMoreElements())
			{
				CtagsTreeTableNode child = children.nextElement();
				Object obj = child.getUserObject();
				if (obj instanceof String)
				{
					int num = child.children.size();
					child.setUserObject((String)obj + " (" + num + ")");
				}
			}
		}
		CtagsTreeTableNode addChild(Object obj) {
			CtagsTreeTableNode node = new CtagsTreeTableNode(obj);
			children.add(node);
			return node;
		}
		boolean hasChildren() {
			return (children.size() > 0);
		}
		Object findChild(Object obj) {
			Enumeration<ctags.sidekick.CtagsTreeTable.CtagsTreeTableNode> children =
				this.children.elements();
			while (children.hasMoreElements()) {
				CtagsTreeTableNode child = children.nextElement();
				if (child.getUserObject().equals(obj) ||
						((String)child.getColumn(GROUP)).equals(obj))
					return child;
			}
			return null;			
		}
		void sort(Comparator<CtagsTreeTableNode> sorter) {
			Collections.sort(children, sorter);
			Enumeration<ctags.sidekick.CtagsTreeTable.CtagsTreeTableNode> children =
				this.children.elements();
			while (children.hasMoreElements()) {
				CtagsTreeTableNode child = children.nextElement();
				child.sort(sorter);
			}			
		}
		public Object getChild(int index) {
			return children.get(index);
		}
		public int getChildCount() {
			return children.size();
		}
		public String toString() {
			return object.toString();
		}
	}

	public static class CtagsTreeTableMapper implements ITreeMapper {

		Vector<String> keys;
		
		public CtagsTreeTableMapper(Vector<String> keys) {
			this.keys = keys;
		}
		
		public Vector<Object> getPath(Tag tag) {
			Vector<Object> path = new Vector<Object>();
			for (int i = 0; i < keys.size(); i++) {
				String val = (String) tag.getInfo().get(keys.get(i));
				if (val == null)
					val = "<none>";
				path.add(val);
			}
			path.add(tag);
			return path;
		}

		public void setLang(String lang) {
		}
		
	}
	public static class CtagsTreeTableModel extends AbstractTreeTableModel {

		Vector<String> columns = new Vector<String>();
		ITreeMapper mapper = null;
		
		@SuppressWarnings("unchecked")
		public CtagsTreeTableModel(CtagsTreeTableNode root, Vector<String> columns) {
			super(root);
			this.columns = columns;
		}
		
		public int getColumnCount() {
			return columns.size();
		}

		public String getColumnName(int column) {
			return columns.get(column);
		}

		public Object getValueAt(Object node, int column) {
			Object value = ((CtagsTreeTableNode)node).getColumn(columns.get(column));
			if (value == null)
				return "";
			return value;
		}

		public Object getChild(Object node, int index) {
			Object child = ((CtagsTreeTableNode)node).getChild(index);
			return child;
		}

		public int getChildCount(Object node) {
			int count = ((CtagsTreeTableNode)node).getChildCount();
			return count;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class getColumnClass(int column) {
			if (columns.get(column).equals(GROUP))
				return TreeTableModel.class;
			return super.getColumnClass(column);
		}

	}
	
	JScrollPane sp = null;
	JTreeTable tree;
	CtagsTreeTableModel model;
	View view;
	private ITreeMapper mapper;
	
	public CtagsTreeTable(View view) {
		super(new BorderLayout());
		this.view = view;
		Vector<String> keys = new Vector<String>();
		keys.add("class");
		mapper = new CtagsTreeTableMapper(keys);
		JPanel buttons = new JPanel(new GridLayout(1, 0));
		add(buttons, BorderLayout.NORTH);
		JButton parseBtn = new JButton("Parse");
		buttons.add(parseBtn);
		parseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createTree(parse(), mapper);
			}
		});
		JButton groupBtn = new JButton("Group...");
		buttons.add(groupBtn);
		groupBtn.addActionListener(new GroupingListener());
		JButton filterBtn = new JButton("Filter...");
		buttons.add(filterBtn);
		filterBtn.addActionListener(new FilterListener());
	}

	private void createTree(Vector<Tag> tags, ITreeMapper mapper) {
		if (sp != null)
			remove(sp);
		buildTree(tags, mapper);
		model = new CtagsTreeTableModel(root, columns);
		tree = new JTreeTable(model);
		sp = new JScrollPane(tree);
		add(sp, BorderLayout.CENTER);
		revalidate();
	}

	private static final String SPACES = "\\s+";
	private Vector<Tag> parse() {
		Vector<Tag> data = new Vector<Tag>();
		Buffer buffer = view.getBuffer();
		String ctagsExe = jEdit.getProperty("options.CtagsSideKick.ctags_path");
		String path = buffer.getPath();
		String mode = buffer.getMode().getName();
		String options = ModeOptionsPane.getProperty(mode, Plugin.CTAGS_MODE_OPTIONS);
		if (options == null)
			options = "";
		Vector<String> cmdLine = new Vector<String>();
		cmdLine.add(ctagsExe);
		cmdLine.add("--fields=KsSz");
		cmdLine.add("--excmd=pattern");
		cmdLine.add("--sort=no");
		cmdLine.add("--fields=+n");
		cmdLine.add("--extra=-q");
		cmdLine.add("-f");
		cmdLine.add("-");
		if (path.endsWith("build.xml"))
			cmdLine.add("--language-force=ant");
		String [] customOptions = options.split(SPACES);
		for (int i = 0; i < customOptions.length; i++)
			cmdLine.add(customOptions[i]);
		cmdLine.add(path);
		String [] args = new String[cmdLine.size()]; 
		cmdLine.toArray(args);
		Process p;
		BufferedReader in = null;
		try {
			p = Runtime.getRuntime().exec(args);
			in = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String line;
			Tag prevTag = null;
			while ((line=in.readLine()) != null)
			{
				Hashtable<String, String> info =
					new Hashtable<String, String>();
				if (line.endsWith("\n") || line.endsWith("\r"))
					line = line.substring(0, line.length() - 1);
				String fields[] = line.split("\t");
				if (fields.length < 3)
					continue;
				info.put("k_tag", fields[0]);
				info.put("k_pat", fields[2]);
				// extensions
				for (int i = 3; i < fields.length; i++)
				{
					String pair[] = fields[i].split(":", 2);
					if (pair.length != 2)
						continue;
					info.put(pair[0], pair[1]);
				}
				Tag curTag = new Tag(buffer, info);
				if (prevTag != null)
				{	// Set end position of previous tag and add it to the tree
					// (If both tags are on the same line, make the previous tag
					// end at the name of the current one.)
					LinePosition prevEnd;
					int curLine = curTag.getLine();
					if (curLine == prevTag.getLine())
					{
						String def = buffer.getLineText(curLine);
						Pattern pat = Pattern.compile("\\b" + curTag.getName() + "\\b");
						Matcher mat = pat.matcher(def);
						int pos = mat.find() ? mat.start() : -1;
						if (pos == -1) // nothing to do, share assets...
							prevEnd = new LinePosition(buffer, curLine, false); 
						else
						{
							prevEnd = new LinePosition(buffer, curLine, pos);
							curTag.setStart(
									new LinePosition(buffer, curLine, pos));
						}
					}
					else
						prevEnd = new LinePosition(buffer, curLine - 1, false);
					prevTag.setEnd(prevEnd);
					data.add(prevTag);
				}
				prevTag = curTag;
			}
			if (prevTag != null)
			{
				prevTag.setEnd(new LinePosition(buffer));
				data.add(prevTag);
			}
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
	}

	CtagsTreeTableNode root;
	Vector<String> columns;
	@SuppressWarnings("unchecked")
	public void buildTree(Vector<Tag> tags, ITreeMapper mapper) {
		root = new CtagsTreeTableNode("/");
		columns = new Vector<String>();
		columns.add(GROUP);
		for (int i = 0; i < tags.size(); i++) {
			Tag tag = tags.get(i);
			Enumeration<String> info = tag.getInfo().keys();
			while (info.hasMoreElements()) {
				String key = info.nextElement();
				if (! columns.contains(key))
					columns.add(key);
			}
			add(tag, mapper);
		}
	}
	
	void add(Tag tag, ITreeMapper mapper) {
		if (filter != null) {
			Enumeration<String> keys = filter.keys();
			while (keys.hasMoreElements()) {
				String col = keys.nextElement();
				String value = (String) tag.getInfo().get(col);
				if (filter.get(col).contains(value))
					return;
			}
		}
		if (mapper == null) {
			root.addChild(tag);
			return;
		}
		Vector<Object> path = mapper.getPath(tag);
		CtagsTreeTableNode node = root; 
		for (int i = 0; i < path.size(); i++) {
			Object obj = path.get(i);
			CtagsTreeTableNode child =
				(CtagsTreeTableNode) node.findChild(obj);
			if (child == null) {
				child = node.addChild(obj);
			} else {
				// Let real tags take over String placeholders
				if ((child.getUserObject() instanceof String) &&
					(!(obj instanceof String)))
				{
					child.setUserObject(obj);
				}
			}
			node = child;
		}
	}
	
	private class GroupingListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Vector<String> keys = new Vector<String>();
			Vector<String> cols = new Vector<String>();
			for (int j = 0; j < columns.size(); j++)
				cols.add(columns.get(j));
			cols.remove(GROUP);
			int i = 1;
			while (true) {
				String column = (String) JOptionPane.showInputDialog(
					null,
					"Select column for grouping at level " + i + ":\n" +
					"(Click Cancel to end the column selection)",
					"Grouping",
					JOptionPane.QUESTION_MESSAGE,
					null,
					cols.toArray(), cols.get(0));
				i++;
				if (column == null)
					break;
				keys.add(column);
				cols.remove(column);
			}
			mapper = new CtagsTreeTableMapper(keys);
			createTree(parse(), mapper);
		}
	}
	
	private Hashtable<String, HashSet<String>> filter = null;
	private void setFilter(String filterString) {
		if (filterString == null) {
			if (filter != null)
				filter.clear();
			return;
		}
		if (filter == null)
			filter = new Hashtable<String, HashSet<String>>();
		String [] filterStrings = filterString.split(",");
		for (int i = 0; i < filterStrings.length; i++) {
			String [] pair = filterStrings[i].split("=");
			if (pair.length == 2) {
				if (! filter.containsKey(pair[0]))
					filter.put(pair[0], new HashSet<String>());
				filter.get(pair[0]).add(pair[1]);
			}
		}		
	}
	private String getFilterString() {
		if (filter == null)
			return "";
		StringBuffer buf = new StringBuffer("");
		Enumeration<String> keys = filter.keys();
		while (keys.hasMoreElements()) {
			String col = keys.nextElement();
			Iterator<String> values = filter.get(col).iterator();
			while (values.hasNext()) {
				buf.append(col + "=" + values.next());
				if (values.hasNext())
					buf.append(",");
			}
		}
		return buf.toString();
	}
	private class FilterListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String s = (String) JOptionPane.showInputDialog(
					"Enter (column,value) pairs to be filtered.\n" +
					"The pairs should be entered in the form column1=value1,column2=value2,...",
					getFilterString());
			setFilter(s);
		}
	}
}
