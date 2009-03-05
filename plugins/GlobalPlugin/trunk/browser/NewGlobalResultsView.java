package browser;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
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
		symbolPanel.add(new JLabel("Symbol:"), BorderLayout.WEST);
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
		tree = new JTree(model);
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath tp = e.getPath();
				Object obj = tp.getPathComponent(tp.getPathCount() - 1);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
				Object userObj = node.getUserObject();
				if (userObj instanceof GlobalReference) {
					GlobalReference ref = (GlobalReference) node.getUserObject();
					ref.jump(view);
				}
			}
		});
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}
	
	protected String getParam()	{
		return param;
	}
	
	private String getBufferDirectory() {
		File file = new File(view.getBuffer().getPath());
		return file.getParent();
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
			DefaultMutableTreeNode rootNode =
				new DefaultMutableTreeNode(identifier);
			root.add(rootNode);
			String lastFile = null;
			DefaultMutableTreeNode fileNode = null;
			for (int i = 0; i < refs.size(); i++)
			{
				GlobalRecord rec = refs.get(i);
				String file = rec.getFile();
				if (! file.equals(lastFile)) {
					fileNode = new DefaultMutableTreeNode(file);
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
