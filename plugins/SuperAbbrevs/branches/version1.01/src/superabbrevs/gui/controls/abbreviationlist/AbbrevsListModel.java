package superabbrevs.gui.controls.abbreviationlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import superabbrevs.model.Abbreviation;

public class AbbrevsListModel extends AbstractListModel {
	private final List<Abbreviation> items;
	
	public AbbrevsListModel(Collection<? extends Abbreviation> abbrevs) {
		items = new ArrayList<Abbreviation>(abbrevs);
		sort();
	}
	
	public boolean remove(Abbreviation abbrev) {
		
		int index = items.indexOf(abbrev);
		boolean exists = index != -1;
		
		if (exists) {
			items.remove(index);
			fireIntervalRemoved(this, index, index);
		}
		return exists;
	}
	
	public Abbreviation getElementAt(int index) {
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
