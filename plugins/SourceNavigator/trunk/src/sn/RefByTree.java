package sn;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.View;

import sn.DbAccess.RecordHandler;

import com.sleepycat.db.DatabaseEntry;

@SuppressWarnings("serial")
public class RefByTree extends JPanel {

	private View view;
	private JTextField text;
	private JTree tree;
	private DefaultTreeModel model;
	private SourceElementTreeNode root;
	private SourceElementTreeNode lastClicked;
	
	private class SourceElementTreeNode extends DefaultMutableTreeNode {
		private String desc;
		private Vector<SourceElement> elements;
		int index = 0;
		private boolean childrenCreated = false;
		public SourceElementTreeNode(SourceElement element) {
			elements = new Vector<SourceElement>();
			if (element == null) {	// root node
				childrenCreated = true;
				return;
			}
			add(element);
			desc = element.getRepresentativeName();
			index = 0;
		}
		public void addChild(SourceElement element) {
			String desc = element.getRepresentativeName();
			if (getChildCount() > 0) {
				TreeNode child = getFirstChild();
				while (child != null) {
					SourceElementTreeNode childNode = (SourceElementTreeNode) child;
					String childDesc = childNode.toString();
					if (childDesc.equals(desc)) {
						childNode.add(element);
						return;
					}
					child = childNode.getNextSibling();
				}
			}
			SourceElementTreeNode node = new SourceElementTreeNode(element);
			add(node);
		}
		public String toString() {
			return desc;
		}
		public void add(SourceElement element) {
			elements.add(element);
		}
		public SourceElement getNext() {
			if (index >= elements.size())
				index = 0;
			return elements.get(index++);
		}
		public void reset() {
			index = 0;
		}
		@Override
		public int getChildCount() {
			if (! childrenCreated) {
				childrenCreated = true;
				find(desc, this);
			}
			return super.getChildCount();
		}
		@Override
		public boolean isLeaf() {
			if (childrenCreated)
				return (getChildCount() == 0);
			return false;
		}
	}
	public RefByTree(View view) {
		super(new BorderLayout());
		this.view = view;
		root = new SourceElementTreeNode(null);
		model = new DefaultTreeModel(root);
		tree = new JTree(model);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		renderer.setLeafIcon(null);
		tree.setCellRenderer(renderer);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				SourceElementTreeNode n = (SourceElementTreeNode)
					e.getPath().getLastPathComponent();
				if (lastClicked != null)
					lastClicked.reset();
				lastClicked = n;
				SourceElement refBy = n.getNext();
				if (refBy != null)
					refBy.jumpTo(RefByTree.this.view);
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
				if (tp == null)
					return;
				SourceElementTreeNode n = (SourceElementTreeNode)
					tp.getLastPathComponent();
				if (n == null)
					return;
				if (lastClicked != n)
					return;
				SourceElement refBy = n.getNext();
				if (refBy != null)
					refBy.jumpTo(RefByTree.this.view);
			}
		});
		add(new JScrollPane(tree), BorderLayout.CENTER);
		JPanel p = new JPanel(new BorderLayout());
		JLabel l = new JLabel("Find:");
		p.add(l, BorderLayout.WEST);
		text = new JTextField(40);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					find(text.getText());
				else
					super.keyReleased(e);
			}
		});
		p.add(text, BorderLayout.CENTER);
		add(p, BorderLayout.NORTH);
	}
	
	private DatabaseEntry identifierToKey(String identifier) {
        int index = identifier.lastIndexOf("::");
        byte[] bytes;
        if (index >= 0) {
        	String namespace = identifier.substring(0, index);
        	String name = identifier.substring(index + 2);
        	bytes = new byte[index + 1 + name.length() + 1];
        	for (int i = 0; i < namespace.length(); i++)
        		bytes[i] = (byte) namespace.charAt(i);
        	bytes[index] = 1;
        	for (int i = 0; i < name.length(); i++)
        		bytes[index + 1 + i] = (byte) name.charAt(i);
        } else {
        	bytes = new byte[3 + identifier.length()];
        	bytes[0] = '#';
        	bytes[1] = 1;
        	for (int i = 0; i < identifier.length(); i++)
        		bytes[2 + i] = (byte) identifier.charAt(i);
        }
    	bytes[bytes.length - 1] = 1;
        return new DatabaseEntry(bytes);
	}
	
	private class RefByRecordHandler implements RecordHandler {
		private String dir;
		private String identifier;
		private SourceElementTreeNode parent;
		
		public RefByRecordHandler(String dir, String identifier, SourceElementTreeNode parent) {
			this.dir = dir;
			this.identifier = identifier;
			this.parent = parent;
		}
		@Override
		public boolean handle(DatabaseEntry key, DatabaseEntry data) {
			String [] strings = keyToStrings(key);
			if (! getIdentifier(strings).equals(identifier))
				return false;
			SourceElement element = recordToSourceElement(strings, dir);
			parent.addChild(element);
			return true;
		}
		private String [] keyToStrings(DatabaseEntry key) {
			byte [] bytes = key.getData();
			String [] strings = new String[9];
			int start = 0;
			int index = 0;
			for (int i = 0; i < bytes.length && index < 9; i++) {
				if (bytes[i] <= 1) {
					strings[index++] = new String(bytes, start, i - start);
					start = i + 1;
				}
			}
			if (index < 9)
				strings[index] = new String(bytes, start, bytes.length - start - 1);
			return strings;
		}
		private String getIdentifier(String [] strings) {
			if (strings[0].equals("#"))
				return strings[1];
			return strings[0] + "::" + strings[1];
		}
		private SourceElement recordToSourceElement(String [] strings, String dir) {
			return new SourceElement(strings[3], strings[4], strings[5], strings[8],
				Integer.valueOf(strings[7]), dir);
		}
	}
	private void find(String identifier) {
		find(identifier, root);
	}
	private void find(final String identifier, final SourceElementTreeNode parent) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				parent.removeAllChildren();
				DbAccess db = new DbAccess("by");
				DatabaseEntry key = identifierToKey(identifier);
				DatabaseEntry data = new DatabaseEntry();
				db.lookup(key, data, new RefByRecordHandler(db.getDir(), identifier, parent));
				model.nodeStructureChanged(parent);
			}
		});
	}
}
