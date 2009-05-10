package superabbrevs.gui.controls.abbreviationlist;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import superabbrevs.model.Abbreviation;
import superabbrevs.model.Mode;

public class AbbreviationJList extends JList implements PropertyChangeListener {
	
	private Mode mode;
	
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
		this.mode = mode;
		updateModel();
		selectFirstValue();
	}

	private void updateModel() {
		Abbreviation selectedAbbrev = getSelectedValue();
		Set<Abbreviation> abbreviations = mode.getAbbreviations();
		setModel(new AbbrevsListModel(abbreviations));
		setSelectedValue(selectedAbbrev, true);
	}

	private void selectFirstValue() {
		if (!mode.getAbbreviations().isEmpty()) {
			setSelectedIndex(0);
		}
	}

	@Override
	public Abbreviation getSelectedValue() {
		return (Abbreviation) super.getSelectedValue();
	}
	
	public boolean hasSelectedValue() {
		return super.getSelectedValue() != null;
	}

	public void removeSelectedAbbreviation() {
		Abbreviation selectedValue = getSelectedValue();
		mode.getAbbreviations().remove(selectedValue);
		updateModel();
	}
	
	private void selectionChanged() {
		if (lastSelection != null) {
			lastSelection.removePropertyChangeListener(this);
		}
		Abbreviation selectedAbbrev = getSelectedValue();
		if (selectedAbbrev != null) {
			selectedAbbrev.addPropertyChangeListener(this);
		}
		lastSelection = selectedAbbrev;
	}
	

	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if ("abbreviationText".equals(propertyName) || "name".equals(propertyName)) {
			updateModel();
		}
	}

	public void addAbbrev(String name) {
		Abbreviation newAbbrev = new Abbreviation(name, "", "");
		mode.getAbbreviations().add(newAbbrev);
		updateModel();
		setSelectedValue(newAbbrev, true);
	}
	
	private Abbreviation lastSelection = null;
}
