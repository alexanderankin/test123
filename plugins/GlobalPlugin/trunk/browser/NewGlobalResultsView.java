package browser;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import options.GlobalOptionPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.util.Log;

@SuppressWarnings("serial")
public class NewGlobalResultsView extends JPanel implements
	DefaultFocusComponent, GlobalDockableInterface
{
	private View view;
	private String param;
	private JTextField symbolTF;
	private JLabel statusLbl;
	private DefaultMutableTreeNode root;
	private JTree tree;
	private DefaultTreeModel model;
	
	public NewGlobalResultsView(final View view, String param)
	{
		super(new BorderLayout());
		this.view = view;
		this.param = param;
		JPanel symbolPanel = new JPanel(new BorderLayout());
		symbolPanel.add(new JLabel("Search for:"), BorderLayout.WEST);
		symbolTF = new JTextField(40);
		symbolTF.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					show(view, symbolTF.getText());
			}
		});
		symbolPanel.add(symbolTF, BorderLayout.CENTER);
		JPanel toolbar = new JPanel(new BorderLayout(5, 0));
		statusLbl = new JLabel("");
		toolbar.add(statusLbl, BorderLayout.EAST);
		symbolPanel.add(toolbar, BorderLayout.EAST);
		add(symbolPanel, BorderLayout.NORTH);
		root = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(root);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		renderer.setLeafIcon(null);
		tree = new JTree(model);
		tree.setCellRenderer(renderer);
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					tp.getLastPathComponent();
				switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					e.consume();
					handleTreeNodeSelection((DefaultMutableTreeNode) node);
					break;
				case MouseEvent.BUTTON3:
					e.consume();
					model.removeNodeFromParent(node);
					break;
				}
			}
		});
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}
	
	private void handleTreeNodeSelection(DefaultMutableTreeNode node) {
		Object userObj = node.getUserObject();
		if (userObj instanceof GlobalReference) {
			GlobalReference ref = (GlobalReference) node.getUserObject();
			ref.jump(view);
		}
	}
	
	protected String getParam()	{
		return param;
	}
	
	private String getBufferDirectory() {
		File file = new File(view.getBuffer().getPath());
		return file.getParent();
	}

	private String makeBold(String s) {
		return "<html><body><b>" + s + "</body></html>";
	}

	private class FileNode extends DefaultMutableTreeNode {
		String file;
		public FileNode(String file) {
			this.file = file;
		}
		public String toString() {
			return makeBold(file + " (" + getChildCount() + " occurrences)");
		}
	}
	
	private class SearchNode extends DefaultMutableTreeNode {
		String search;
		public SearchNode(String search) {
			this.search = search;
		}
		public String toString() {
			int occurs = getLeafCount();
			int files = getChildCount();
			return makeBold(search + " (" + occurs + " occurrences in " + files +
				" files)");
		}
	}
	
	private class RecordQuery implements Runnable {
		String identifier;
		String workingDirectory;
		public RecordQuery(String identifier, String workingDirectory)
		{
			this.identifier = identifier;
			this.workingDirectory = workingDirectory;
		}
		public void run() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					statusLbl.setText("Working...");
				}
			});
			long start = System.currentTimeMillis();
			final Vector<GlobalRecord> refs =
				GlobalLauncher.instance().runRecordQuery(getParam() +
					" " + identifier, workingDirectory);
			DefaultMutableTreeNode rootNode = new SearchNode(identifier);
			root.add(rootNode);
			String lastFile = null;
			DefaultMutableTreeNode fileNode = null;
			for (int i = 0; i < refs.size(); i++)
			{
				GlobalRecord rec = refs.get(i);
				String file = rec.getFile();
				if (! file.equals(lastFile)) {
					fileNode = new FileNode(file);
					rootNode.add(fileNode);
					lastFile = file;
				}
				DefaultMutableTreeNode refNode = new DefaultMutableTreeNode(
					new GlobalReference(rec));
				fileNode.add(refNode);
			}
			if (refs.size() == 1 && GlobalOptionPane.isJumpImmediately())
				new GlobalReference(refs.get(0)).jump(view);
			long end = System.currentTimeMillis();
			Log.log(Log.DEBUG, this.getClass(), "GlobalResultsView(" + getParam() +
				", " + identifier + "' took " + (end - start) * .001 + " seconds.");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					statusLbl.setText(refs.size() + " results");
					model.nodeStructureChanged(root);
					for (int i = 0; i < tree.getRowCount(); i++)
						tree.expandRow(i);
				}
			});
		}
	}
	public void show(View view, String identifier) {
		root.removeAllChildren();
		symbolTF.setText(identifier);
		RecordQuery query = new RecordQuery(identifier,	getBufferDirectory());
		GlobalPlugin.runInBackground(query);
	}
	
	public void focusOnDefaultComponent() {
		tree.requestFocus();
	}
}
