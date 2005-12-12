package common.gui.util;

import javax.swing.*;
import java.util.*;

public class SortedListModel extends AbstractListModel {
	private ArrayList _elements = new ArrayList();
	private Comparator comparator;

	public void addElement(Object element) {
		_elements.add(element);
		sort();
	}

	private void sort() {
		if (comparator == null) {
			Collections.sort(_elements);
		} else {
			Collections.sort(_elements,comparator);
		}
	}

	public Object getElementAt(int i) {
		return _elements.get(i);
	}

	public int getSize() {
		return _elements.size();
	}
}
