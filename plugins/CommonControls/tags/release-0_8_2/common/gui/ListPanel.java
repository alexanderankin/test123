package common.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import common.gui.util.*;

/**
 * A simple list in a scroll pane, which can be reorderable or sorted.
 *
 * @author    mace
 * @version   $Revision$ modified $Date$ by $Author$
 */
public class ListPanel extends JPanel {
	private JLabel label;
	private JList list;
	private JScrollPane scrollpane;
	private boolean reorderable = true;
	private boolean sorted = false;

	public ListPanel(String title) {
		setLayout(new GridBagLayout());
		label = new JLabel(title);
		list = new JList(new DefaultListModel());
		list.setBorder(BorderFactory.createLoweredBevelBorder());
		scrollpane = new JScrollPane(list);
		ConstraintFactory cf = new ConstraintFactory();
		add(label, cf.buildConstraints(0, 0, 10, 1, cf.W, cf.H, 100, 0));
		add(scrollpane, cf.buildConstraints(0, 1, 10, 10, cf.N, cf.BOTH));
		if (reorderable) {
		}
	}

	public ListPanel(String title, Object[] items) {
		this(title);
		for (int i = 0; i < items.length; i++) {
			addElement(items[i]);
		}
	}

	public void clear() {
		((DefaultListModel) list.getModel()).clear();
	}

	public void addKeyListener(KeyListener kl) {
		super.addKeyListener(kl);
		list.addKeyListener(kl);
	}

	public void addElement(Object element) {
		((DefaultListModel) list.getModel()).addElement(element);
	}

	public boolean removeElement(Object element) {
		return ((DefaultListModel) list.getModel()).removeElement(element);
	}

	public void setLabel(String label) {
		this.label.setText(label);
	}

	public void setSorted(boolean b) {
		sorted = b;
		if (sorted) {
			reorderable = false;
		}
	}

	public void setReorderable(boolean b) {
		reorderable = b;
	}

	public void setSelectedIndex(int i) {
		list.setSelectedIndex(i);
	}

	public int getLastSelectedIndex() {
		int[] selected = list.getSelectedIndices();
		if (selected.length > 0) {
			return selected[selected.length - 1];
		} else {
			return -1;
		}
	}

	public Object[] getSelectedValues() {
		return list.getSelectedValues();
	}

	public Object[] toArray() {
		return ((DefaultListModel) list.getModel()).toArray();
	}
}

