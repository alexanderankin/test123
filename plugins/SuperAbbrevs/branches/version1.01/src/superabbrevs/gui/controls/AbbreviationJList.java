package superabbrevs.gui.controls;

import java.util.Set;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import superabbrevs.model.Abbrev;
import superabbrevs.model.Mode;

public class AbbreviationJList extends JList {
	
	private Mode mode;
	
	{
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
		Set<Abbrev> abbreviations = mode.getAbbreviations();
		setModel(new AbbrevsListModel(abbreviations));
	}

	@Override
	public Abbrev getSelectedValue() {
		return (Abbrev) super.getSelectedValue();
	}
}
