package superabbrevs.gui.controls.abbreviationlist;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
		Abbrev selectedAbbrev = getSelectedValue();
		Set<Abbrev> abbreviations = mode.getAbbreviations();
		setModel(new AbbrevsListModel(abbreviations));
		setSelectedValue(selectedAbbrev, true);
	}

	private void selectFirstValue() {
		if (!mode.getAbbreviations().isEmpty()) {
			setSelectedIndex(0);
		}
	}

	@Override
	public Abbrev getSelectedValue() {
		return (Abbrev) super.getSelectedValue();
	}
	
	public boolean hasSelectedValue() {
		return super.getSelectedValue() != null;
	}

	public void removeSelectedAbbreviation() {
		Abbrev selectedValue = getSelectedValue();
		mode.getAbbreviations().remove(selectedValue);
		updateModel();
	}
	
	private void selectionChanged() {
		if (lastSelection != null) {
			lastSelection.removePropertyChangeListener(this);
		}
		Abbrev selectedAbbrev = getSelectedValue();
		if (selectedAbbrev != null) {
			selectedAbbrev.addPropertyChangeListener(this);
		}
		lastSelection = selectedAbbrev;
	}
	

	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if ("abbreviation".equals(propertyName) || "name".equals(propertyName)) {
			updateModel();
		}
	}

	public void addAbbrev(String name) {
		Abbrev newAbbrev = new Abbrev(name, "", "");
		mode.getAbbreviations().add(newAbbrev);
		updateModel();
		setSelectedValue(newAbbrev, true);
	}
	
	private Abbrev lastSelection = null;
}
