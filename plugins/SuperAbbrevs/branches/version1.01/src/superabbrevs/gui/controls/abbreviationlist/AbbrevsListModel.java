package superabbrevs.gui.controls.abbreviationlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import org.gjt.sp.util.Log;

import superabbrevs.model.Abbrev;

public class AbbrevsListModel extends AbstractListModel {
	private final List<Abbrev> items;
	
	public AbbrevsListModel(Collection<? extends Abbrev> abbrevs) {
		items = new ArrayList<Abbrev>(abbrevs);
		if (!items.isEmpty()) {
			Collections.sort(items);
		}
	}
	
	public Abbrev getElementAt(int index) {
		return items.get(index);
	}

	public int getSize() {
		return items.size();
	}
}
