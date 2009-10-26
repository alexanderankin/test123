package gatchan.phpparser.project.itemfinder;

import javax.swing.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * @author Matthieu Casanova
 */
public final class SimpleListModel extends AbstractListModel {
	private TreeSet list = new TreeSet(new SimpleComparator());

	private int mode;

	private Object[] items;

	public SimpleListModel() {
	}

	public int getSize() {
		if (items == null) return 0;
		return Math.min(items.length, 25);
	}

	public Object getElementAt(int index) {
		return items[index];
	}

	public void setList(List list, String searchString) {
		int oldSize = this.list.size();
		int size = list.size();
		if (oldSize != 0) {
			fireIntervalRemoved(this, 0, Math.min(oldSize - 1, 25));
		}
		this.list.clear();
		for (int i = 0; i < list.size(); i++) {
			PHPItem phpItem = (PHPItem) list.get(i);
			if (accept(phpItem, searchString)) {
				this.list.add(phpItem);
			}
		}
		items = this.list.toArray();
		if (size != 0) {
			fireIntervalRemoved(this, 0, Math.min(size - 1, 25));
		}
	}

	public void filter(String searchString) {
		if (getSize() != 0) {
			boolean modified = false;
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				PHPItem phpItem = (PHPItem) iterator.next();
				if (!accept(phpItem, searchString)) {
					modified = true;
					iterator.remove();
				}
			}
			if (modified) {
				int oldSize = items.length;
				items = list.toArray();
				fireIntervalRemoved(this, items.length, oldSize);
			}
			fireContentsChanged(this, 0, items.length);
		}
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	private boolean accept(PHPItem phpItem, String searchText) {
		return (mode & phpItem.getItemType()) == phpItem.getItemType() && phpItem.getNameLowerCase().indexOf(searchText) != -1;
	}

	public void clear() {
		list.clear();
		items = null;
	}

	/**
	 * Comparator that will compare PHPItems.
	 *
	 * @author Matthieu Casanova
	 */
	private static class SimpleComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			PHPItem item1 = (PHPItem) o1;
			PHPItem item2 = (PHPItem) o2;
			String name1 = item1.getName();
			String name2 = item2.getName();
			int l1 = name1.length();
			int l2 = name2.length();
			if (l1 != l2)
				return l1 - l2;
			int comp = name1.compareTo(name2);
			if (comp == 0) {
				return item1.getSourceStart() - item2.getSourceStart();
			}
			return comp;
		}
	}
}
