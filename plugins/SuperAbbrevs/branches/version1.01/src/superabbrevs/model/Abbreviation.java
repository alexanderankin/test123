package superabbrevs.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class Abbreviation implements Serializable, Comparable<Abbreviation> {
	/**
	 * A short name of the abbreviation.
	 */
	private String name;

	/**
	 * The text that will be expanded into expansion string.
	 */
	private String abbreviationText;

	/**
	 * The expansion of the abbreviation.
	 */
	private String expansion;

	/**
	 * True if the abbreviation should be indented when changed.
	 */
	private boolean autoIndent;
	
	/**
	 * The area that should be replaced when the abbreviation is invoked as a 
	 * command an no text has been selected.
	 */
	private ReplacementTypes replacementArea = ReplacementTypes.AT_CARET;
	
	/**
	 * The area that should be replaced when the abbreviation is invoked as a 
	 * command on a selection.
	 */
	private SelectionReplacementTypes selectionReplacementArea = SelectionReplacementTypes.NOTHING;

	/**
	 * Creates a new instance of Abbreviation
	 */
	public Abbreviation(String name, String abbreviationText, String expansion) {
		setName(name);
		setAbbreviationText(abbreviationText);
		setExpansion(expansion);
	}
	
	public String getAbbreviationText() {
		return abbreviationText;
	}

	public void setAbbreviationText(String abbreviation) {
		String oldValue = this.abbreviationText;
		this.abbreviationText = abbreviation;

		propertySupport.firePropertyChange("abbreviationText", oldValue,
				abbreviation);
	}

	public boolean isAutoIndent() {
		return autoIndent;
	}

	public void setAutoIndent(boolean autoIndent) {
		boolean oldValue = this.autoIndent;
		this.autoIndent = autoIndent;
		propertySupport.firePropertyChange("autoIndent", oldValue, autoIndent);
	}

	public String getExpansion() {
		return expansion;
	}

	public void setExpansion(String expansion) {
		String oldValue = this.expansion;
		this.expansion = expansion;
		propertySupport.firePropertyChange("expansion", oldValue, expansion);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		propertySupport.firePropertyChange("name", oldValue, name);
	}
	
	public void setSelectionReplacementArea(SelectionReplacementTypes selectionReplacementArea) {
		SelectionReplacementTypes oldValue = this.selectionReplacementArea;
		this.selectionReplacementArea = selectionReplacementArea;
		propertySupport.firePropertyChange("selectionReplacementArea", oldValue, selectionReplacementArea);
	}

	public SelectionReplacementTypes getSelectionReplacementArea() {
		return selectionReplacementArea;
	}

	public void setReplacementArea(ReplacementTypes replacementArea) {
		ReplacementTypes oldValue = this.replacementArea;
		this.replacementArea = replacementArea;
		propertySupport.firePropertyChange("replacementArea", oldValue, replacementArea);
	}

	public ReplacementTypes getReplacementArea() {
		return replacementArea;
	}

	@Override
	public String toString() {
		return name + " (" + abbreviationText + ")";
	}

	public int compareTo(Abbreviation abbrev) {
		return getName().compareToIgnoreCase(abbrev.getName());
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(propertyName, listener);
	}

	private transient PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
	
	private Object readResolve() {
		propertySupport = new PropertyChangeSupport(this);
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.toLowerCase().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Abbreviation other = (Abbreviation) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}
}
