package sidekick;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

@SuppressWarnings("serial")
public class SideKickToolBar extends JToolBar implements ActionListener
{
	private View view;
	private JButton select;
	private JComboBox combo;
	private boolean followCaret;
	private CaretListener caretListener;
	private Timer caretTimer;
	private SideKickParsedData data;
	private boolean automaticUpdate = false;
	private int delayMs;
	private boolean splitCombo;
	private JPanel splitComboPanel;
	private ArrayList<JComboBox> combos;
	private ComboCellRenderer renderer;

	public SideKickToolBar(View view)
	{
		this.view = view;
		setFloatable(false);
		select = new JButton(jEdit.getProperty("sidekick-toolbar.select"));
		select.addActionListener(this);
		add(select);
		renderer = new ComboCellRenderer();
		splitCombo = jEdit.getBooleanProperty(SideKickOptionPane.SPLIT_COMBO);
		if (splitCombo)
			createSplitComboPanel();
		else
			createSingleCombo();
		followCaret = SideKick.isFollowCaret();
		update();
		delayMs = jEdit.getIntegerProperty("sidekick.toolBarUpdateDelay", 200);
		if (followCaret)
			addCaretListener();
		EditBus.addToBus(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == select)
		{
			Object o = combo.getSelectedItem();
			if (! (o instanceof NodeWrapper))
				return;
			NodeWrapper nw = (NodeWrapper) o;
			nw.select(view);
		}
	}

	@EBHandler
	public void handleSideKickUpdate(SideKickUpdate msg)
	{
		if(msg.getView() == view)
			update();
	}
	@EBHandler
	public void handlePropertiesChanged(PropertiesChanged msg)
	{
		boolean newSplitCombo = jEdit.getBooleanProperty(SideKickOptionPane.SPLIT_COMBO);
		if (newSplitCombo != splitCombo)
		{
			splitCombo = newSplitCombo;
			if (splitCombo)
			{
				removeSingleCombo();
				createSplitComboPanel();
			}
			else
			{
				removeSplitComboPanel();
				createSingleCombo();
			}
			update();
		}
		delayMs = jEdit.getIntegerProperty("sidekick.toolBarUpdateDelay", 200);
		boolean newFollowCaret = SideKick.isFollowCaret();
		if (newFollowCaret != followCaret)
		{
			followCaret = newFollowCaret;
			if (followCaret)
				addCaretListener();
			else
				removeCaretListener();
		}
	}
	@EBHandler
	public void handleEditPaneUpdate(EditPaneUpdate epu)
	{
		if (! followCaret)
			return;
		EditPane editPane = epu.getEditPane();
		if (epu.getWhat() == EditPaneUpdate.CREATED)
			editPane.getTextArea().addCaretListener(caretListener);
		else if (epu.getWhat() == EditPaneUpdate.DESTROYED)
			editPane.getTextArea().removeCaretListener(caretListener);
	}
	private void addCaretListener()
	{
		caretListener = new CaretHandler();
		for (EditPane ep: view.getEditPanes())
			ep.getTextArea().addCaretListener(caretListener);
	}
	private void removeCaretListener()
	{
		for (EditPane ep: view.getEditPanes())
			ep.getTextArea().removeCaretListener(caretListener);
		caretListener = null;
	}
	private void update()
	{
		automaticUpdate = true;
		data = SideKickParsedData.getParsedData(view);
		if (splitCombo)
			updateSplitCombo();
		else
			updateSingleCombo();
		if (followCaret)
			updateSelectionByCaretPosition();
		automaticUpdate = false;
	}

	private void updateSelectionByCaretPosition()
	{
		JEditTextArea textArea = view.getTextArea();
		int caret = textArea.getCaretPosition();
		Selection s = textArea.getSelectionAtOffset(caret);
		selectItemAtPosition(s == null ? caret : s.getStart());
	}

	public void dispose()
	{
		EditBus.removeFromBus(this);
	}

	private void selectItemAtPosition(int position)
	{
		if (splitCombo)
			selectItemAtPositionSplitCombo(position);
		else
			selectItemAtPositionSingleCombo(position);
	}

	/*
	 * Single combo mode
	 */

	private void createSingleCombo()
	{
		combo = new JComboBox();
		combo.setRenderer(renderer);
		combo.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (automaticUpdate)
					return;
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;
				Object o = e.getItem();
				if (! (o instanceof NodeWrapper))
					return;
				((NodeWrapper)o).jump(SideKickToolBar.this.view);
			}
		});
		add(combo);
	}

	private void removeSingleCombo()
	{
		remove(combo);
		combo = null;
	}

	private void updateSingleCombo()
	{
		combo.removeAllItems();
		if (data == null)
			combo.addItem(jEdit.getProperty("sidekick-tree.not-parsed"));
		else
			addTree(data.root, null);
	}

	private void addTree(TreeNode node, NodeWrapper parent)
	{
		// Always insert children only
		for (int i = 0; i < node.getChildCount(); i++)
		{
			TreeNode child = node.getChildAt(i);
			NodeWrapper nw = new NodeWrapper(parent, child);
			if (nw.isAsset())
				combo.addItem(nw);
			addTree(child, nw);
		}
	}

	private void selectItemAtPositionSingleCombo(int position)
	{
		NodeWrapper selected = null;
		for (int i = 0; i < combo.getItemCount(); i++)
		{
			NodeWrapper nw = (NodeWrapper) combo.getItemAt(i);
			if (nw == null)
				continue;
			if (nw.contains(position))
			{
				if (nw.isBetterThan(selected))
					selected = nw;
			}
		}
		if (selected != null)
		{
			automaticUpdate = true;
			combo.setSelectedItem(selected);
			automaticUpdate = false;
		}
	}

	/*
	 * Split combo mode
	 */

	private void createSplitComboPanel()
	{
		combos = new ArrayList<JComboBox>();
		splitComboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		add(splitComboPanel);
		getSplitCombo(0);
	}

	private void removeSplitComboPanel()
	{
		for (JComboBox c: combos)
			splitComboPanel.remove(c);
		combos = null;
		remove(splitComboPanel);
		splitComboPanel = null;
	}

	private void updateSplitCombo()
	{
		JComboBox c = combos.get(0);
		c.removeAllItems();
		if (data == null)
			c.addItem(jEdit.getProperty("sidekick-tree.not-parsed"));
		else
			addFirstTreeLevel(data.root);
	}
	
	private void addFirstTreeLevel(TreeNode node)
	{
		JComboBox c = combos.get(0);
		addChildrenToSplitCombo(node, c, 0);
	}

	private void addChildrenToSplitCombo(TreeNode node, JComboBox c, int index)
	{
		for (int i = 0; i < node.getChildCount(); i++)
		{
			TreeNode child = node.getChildAt(i);
			NodeWrapper nw = new NodeWrapper(child);
			c.addItem(nw);
		}
		Object o = c.getSelectedItem();
		if (o != null)
			updateNextTreeLevel((NodeWrapper)o, index + 1);
	}

	private JComboBox getSplitCombo(final int index)
	{
		if (index < combos.size())
			return combos.get(index);
		JComboBox c = new JComboBox();
		c.setRenderer(renderer);
		combos.add(c);
		splitComboPanel.add(c);
		c.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (automaticUpdate)
					return;
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;
				Object o = e.getItem();
				if (! (o instanceof NodeWrapper))
					return;
				NodeWrapper nw = (NodeWrapper) o;
				updateNextTreeLevel(nw, index + 1);
			}
		});
		return c;
	}

	private void updateNextTreeLevel(NodeWrapper nw, int index)
	{
		if (nw.node == null)
			return;
		int children = nw.node.getChildCount();
		if (children == 0)
		{
			// This is a leaf node - remove trailing combos and jump to asset if exists
			while (combos.size() > index)
			{
				JComboBox removed = combos.remove(index);
				splitComboPanel.remove(removed);
			}
			if (! automaticUpdate)
				nw.jump(view);
		}
		else
		{
			JComboBox c;
			c = getSplitCombo(index);
			c.removeAllItems();
			addChildrenToSplitCombo(nw.node, c, index);
		}
	}

	private void selectItemAtPositionSplitCombo(int position)
	{
		TreePath tp = data.getTreePathForPosition(position);
		// Ignore first path element, which is the file itself
		for (int i = 1; i < tp.getPathCount(); i++)
		{
			Object o = tp.getPathComponent(i);
			JComboBox c = getSplitCombo(i - 1);
			boolean found = false;
			for (int j = 0; j < c.getItemCount(); j++)
			{
				NodeWrapper nw = (NodeWrapper) c.getItemAt(j);
				if (nw.node == o)
				{
					automaticUpdate = true;
					c.setSelectedIndex(j);
					updateNextTreeLevel(nw, i);
					automaticUpdate = false;
					found = true;
					break;
				}
			}
			if (! found)
				break;
		}
	}

	private class CaretHandler implements CaretListener
	{
		public void caretUpdate(CaretEvent e)
		{
			if (e.getSource() != view.getTextArea())
				return;
			if (caretTimer != null)
			{
				caretTimer.stop();
			}
			else
			{
				caretTimer = new Timer(0,new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						updateSelectionByCaretPosition();
					}
				});
				caretTimer.setRepeats(false);
			}
			caretTimer.setInitialDelay(delayMs);
			caretTimer.start();
		}
	}

	private static class NodeWrapper
	{
		public NodeWrapper parent;
		public String str;
		public Icon icon;
		public Asset asset;
		public TreeNode node;

		// A constructor for split combo mode - each node shows itself only
		public NodeWrapper(TreeNode node)
		{
			this(null, node);
		}
		// A constructor for single combo mode - each node shows the path
		public NodeWrapper(NodeWrapper parent, TreeNode node)
		{
			this.parent = parent;
			this.node = node;
			if (node instanceof DefaultMutableTreeNode)
			{
				DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) node;
				Object o = dmtn.getUserObject();
				if (o == null)
					str = "";
				else
				{
					if (o instanceof Asset)
					{
						asset = (Asset) o;
						icon = asset.getIcon();
						str = asset.getShortString();
					}
					else
						str = o.toString();
				}
			}
		}
		public void jump(View view)
		{
			if (asset == null)
				return;
			view.getTextArea().setCaretPosition(asset.start.getOffset());
		}
		public void select(View view)
		{
			if (asset == null)
				return;
			view.getTextArea().setSelection(new Selection.Range(asset.start.getOffset(),
				asset.end.getOffset()));
		}
		public boolean contains(int position)
		{
			return (asset != null && asset.getStart().getOffset() <= position &&
				asset.getEnd().getOffset() > position);
		}
		public boolean isBetterThan(NodeWrapper other)
		{
			return (other == null ||
				asset.getStart().getOffset() > other.asset.getStart().getOffset() ||
				asset.getEnd().getOffset() < other.asset.getEnd().getOffset());
		}
		public boolean isAsset()
		{
			return (asset != null);
		}
		public void addLabel(JPanel p)
		{
			if (parent != null)
				parent.addLabel(p);
			JLabel l = new JLabel();
			l.setText(str);
			if (icon != null)
				l.setIcon(icon);
			p.add(l);
		}
	}

	private static class ComboCellRenderer implements ListCellRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 1));
			if (value instanceof NodeWrapper)
			{
				NodeWrapper nw = (NodeWrapper) value;
				nw.addLabel(p);
			}
			return p;
		}
	}
}
