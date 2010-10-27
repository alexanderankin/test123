package sidekick;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class SideKickToolBar extends JToolBar
{
	private View view;
	private JComboBox combo;
	public SideKickToolBar(View view)
	{
		this.view = view;
		setFloatable(false);
		combo = new JComboBox();
		combo.setRenderer(new ComboCellRenderer());
		combo.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;
				Object o = e.getItem();
				if (! (o instanceof NodeWrapper))
					return;
				Asset asset = ((NodeWrapper) o).asset;
				if (asset == null)
					return;
				SideKickToolBar.this.view.getTextArea().setCaretPosition(
					asset.start.getOffset());
			}
		});
		add(combo);
		update();
		EditBus.addToBus(this);
	}
	@EBHandler
	public void handleSideKickUpdate(SideKickUpdate msg)
	{
		if(msg.getView() == view)
			update();
	}
	private void update()
	{
		SideKickParsedData data = SideKickParsedData.getParsedData(view);
		combo.removeAllItems();
		if (data == null)
			combo.addItem(jEdit.getProperty("sidekick-tree.not-parsed"));
		else
			addTree(data.root, new ArrayList<String>());
	}
	private void addTree(TreeNode node, ArrayList<String> path)
	{
		// Always insert children only
		for (int i = 0; i < node.getChildCount(); i++)
		{
			TreeNode child = node.getChildAt(i);
			NodeWrapper nw = new NodeWrapper(path, child);
			combo.addItem(new NodeWrapper(path, child));
			path.add(nw.nodeStr);
			addTree(child, path);
			path.remove(path.size() - 1);
		}
	}

	public void dispose()
	{
		EditBus.removeFromBus(this);
	}

	private static class NodeWrapper
	{
		public String pathStr;
		public String nodeStr;
		public String fullStr;
		public Icon icon;
		public Asset asset;
		public NodeWrapper(ArrayList<String> path, TreeNode node)
		{
			pathStr = getString(path);
			if (node instanceof DefaultMutableTreeNode)
			{
				DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) node;
				Object o = dmtn.getUserObject();
				if (o == null)
					nodeStr = "";
				else
				{
					if (o instanceof Asset)
					{
						asset = (Asset) o;
						icon = asset.getIcon();
						nodeStr = asset.getShortString();
					}
					else
						nodeStr = o.toString();
				}
			}
			fullStr = pathStr + " " + nodeStr;
		}
		private String getString(ArrayList<String> path)
		{
			StringBuilder sb = new StringBuilder();
			for (String s: path)
			{
				sb.append("[" + s + "]");
			}
			return sb.toString();
		}
	}

	private static class ComboCellRenderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
			JLabel l = (JLabel) super.getListCellRendererComponent(list, value,
				index, isSelected, cellHasFocus);
			if (value instanceof NodeWrapper)
			{
				NodeWrapper nw = (NodeWrapper) value;
				l.setText(nw.fullStr);
				Icon icon = nw.icon;
				if (icon != null)
					l.setIcon(icon);
			}
			return l;
		}
	}

}
