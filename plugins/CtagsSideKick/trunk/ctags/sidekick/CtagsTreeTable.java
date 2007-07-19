package ctags.sidekick;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;

import treetable.AbstractTreeTableModel;
import treetable.JTreeTable;
import treetable.TreeTableModel;

@SuppressWarnings("serial")
public class CtagsTreeTable extends JPanel implements EBComponent {
	private static final String GROUP = "group";

	private static class CtagsSideKickTreeNode
	{
		private Vector<CtagsSideKickTreeNode> children =
			new Vector<CtagsSideKickTreeNode>();
		private Object object = null;
		
		public CtagsSideKickTreeNode(Object obj) {
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
			Enumeration<ctags.sidekick.CtagsTreeTable.CtagsSideKickTreeNode> children =
				this.children.elements();
			while (children.hasMoreElements())
			{
				CtagsSideKickTreeNode child =
					(CtagsSideKickTreeNode) children.nextElement();
				Object obj = child.getUserObject();
				if (obj instanceof String)
				{
					int num = child.children.size();
					child.setUserObject((String)obj + " (" + num + ")");
				}
			}
		}
		CtagsSideKickTreeNode addChild(Object obj) {
			CtagsSideKickTreeNode node = new CtagsSideKickTreeNode(obj);
			children.add(node);
			return node;
		}
		boolean hasChildren() {
			return (children.size() > 0);
		}
		Object findChild(Object obj) {
			Enumeration<ctags.sidekick.CtagsTreeTable.CtagsSideKickTreeNode> children =
				this.children.elements();
			while (children.hasMoreElements()) {
				CtagsSideKickTreeNode child =
					(CtagsSideKickTreeNode) children.nextElement();
				if (child.getUserObject().equals(obj) ||
						((String)child.getColumn(GROUP)).equals(obj))
					return child;
			}
			return null;			
		}
		void addToTree(DefaultMutableTreeNode root) {
			addChildrenToTree(root);
		}
		void addChildrenToTree(DefaultMutableTreeNode node) {
			Enumeration<ctags.sidekick.CtagsTreeTable.CtagsSideKickTreeNode> children =
				this.children.elements();
			while (children.hasMoreElements()) {
				CtagsSideKickTreeNode child =
					(CtagsSideKickTreeNode) children.nextElement();
				DefaultMutableTreeNode newNode = 
					new DefaultMutableTreeNode(child.getUserObject());
				node.add(newNode);
				child.addChildrenToTree(newNode);
			}
		}
		void sort(Comparator<CtagsSideKickTreeNode> sorter) {
			Collections.sort(children, sorter);
			Enumeration<ctags.sidekick.CtagsTreeTable.CtagsSideKickTreeNode> children =
				this.children.elements();
			while (children.hasMoreElements()) {
				CtagsSideKickTreeNode child =
					(CtagsSideKickTreeNode) children.nextElement();
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
					val = "";
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
		static CtagsSideKickTreeNode tree;
		
		@SuppressWarnings("unchecked")
		public CtagsTreeTableModel(Vector<Tag> tags, ITreeMapper mapper) {
			super(new CtagsSideKickTreeNode("/"));
			tree = (CtagsSideKickTreeNode) getRoot();
			this.mapper = mapper;
			columns.add(GROUP);
			for (int i = 0; i < tags.size(); i++) {
				Tag tag = tags.get(i);
				Enumeration<String> info = tag.getInfo().keys();
				while (info.hasMoreElements()) {
					String key = info.nextElement();
					if (! columns.contains(key))
						columns.add(key);
				}
				add(tag);
			}
		}
		
		void add(Tag tag) {
			if (mapper == null) {
				tree.addChild(tag);
				return;
			}
			Vector<Object> path = mapper.getPath(tag);
			CtagsSideKickTreeNode node = tree; 
			for (int i = 0; i < path.size(); i++) {
				Object obj = path.get(i);
				CtagsSideKickTreeNode child =
					(CtagsSideKickTreeNode) node.findChild(obj);
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
		
		public int getColumnCount() {
			return columns.size();
		}

		public String getColumnName(int column) {
			return columns.get(column);
		}

		public Object getValueAt(Object node, int column) {
			return ((CtagsSideKickTreeNode)node).getColumn(columns.get(column));
		}

		public Object getChild(Object node, int index) {
			return ((CtagsSideKickTreeNode)node).getChild(index);
		}

		public int getChildCount(Object node) {
			return ((CtagsSideKickTreeNode)node).getChildCount();
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
		createTree(new Vector<Tag>());
		EditBus.addToBus(this);
	}

	private void createTree(Vector<Tag> tags) {
		if (sp != null)
			remove(tree);
		model = new CtagsTreeTableModel(tags, mapper );
		tree = new JTreeTable(model);
		sp = new JScrollPane(tree);
		add(sp, BorderLayout.CENTER);
	}

	private static final String SPACES = "\\s+";
	private Vector<Tag> parse() {
		Vector<Tag> data = new Vector<Tag>();
		Buffer buffer = view.getBuffer();
		if (! buffer.isLoaded())
			return data;
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
	public void handleMessage(EBMessage message) {
		if ((message instanceof BufferUpdate &&
			((BufferUpdate)message).getWhat() == BufferUpdate.LOADED) ||
			(message instanceof EditPaneUpdate &&
			((EditPaneUpdate)message).getWhat() == EditPaneUpdate.BUFFER_CHANGED))
		{
			createTree(parse());
		}
	}
}
