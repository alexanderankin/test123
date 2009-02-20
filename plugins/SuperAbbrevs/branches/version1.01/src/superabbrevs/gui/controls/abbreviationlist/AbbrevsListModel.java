package superabbrevs.gui.controls.abbreviationlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import superabbrevs.model.Abbrev;

public class AbbrevsListModel extends AbstractListModel {
	private final List<Abbrev> items;
	
	public AbbrevsListModel(Collection<? extends Abbrev> abbrevs) {
		items = new ArrayList<Abbrev>(abbrevs);
		sort();
	}
	
	public boolean remove(Abbrev abbrev) {
		
		int index = items.indexOf(abbrev);
		boolean exists = index != -1;
		
		if (exists) {
			items.remove(index);
			fireIntervalRemoved(this, index, index);
		}
		return exists;
	}
	
	public Abbrev getElementAt(int index) {
		return items.get(index);
	}

	public int getSize() {
		return items.size();
	}

	public void sort() {
		if (!items.isEmpty()) {
			Collections.sort(items);
		}
	}
}
