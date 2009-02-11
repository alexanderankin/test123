package superabbrevs.gui.controls.abbreviationlist;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import superabbrevs.model.Abbrev;
import superabbrevs.model.Mode;

public class AbbreviationJList extends JList implements PropertyChangeListener {
	
	public AbbreviationJList() {
		initComponents();
	}
	
	private void initComponents() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selectionChanged();
			}
		});
	}
	
	public void setMode(Mode mode) {
		Set<Abbrev> abbreviations = mode.getAbbreviations();
		model = new AbbrevsListModel(abbreviations);
		updateViewFromModel();
		if (!abbreviations.isEmpty()) {
			setSelectedIndex(0);
		}
	}

	@Override
	public Abbrev getSelectedValue() {
		return (Abbrev) super.getSelectedValue();
	}

	public void removeSelectedAbbreviation() {
		// TODO 
		
	}
	
	private void selectionChanged() {
		if (lastSelection != null) {
			lastSelection.removePropertyChangeListener("abbreviation", this);	
		}
		Abbrev selectedAbbrev = getSelectedValue();
		if (selectedAbbrev != null) {
			selectedAbbrev.addPropertyChangeListener("abbreviation", this);
		}
		lastSelection = selectedAbbrev;
	}
	

	public void propertyChange(PropertyChangeEvent evt) {
		if ("abbreviation".equals(evt.getPropertyName())) {
			updateViewFromModel();
			//setSelectedValue(lastSelection, false);
		}
	}
	
	private void updateViewFromModel() {
		Abbrev selectedAbbrev = getSelectedValue();
		setModel(model);
		setSelectedValue(selectedAbbrev,false);
		this.repaint();
	}

	private Abbrev lastSelection = null;
	private AbbrevsListModel model;
}
