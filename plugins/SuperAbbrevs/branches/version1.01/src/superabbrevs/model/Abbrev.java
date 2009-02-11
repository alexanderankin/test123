package superabbrevs.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class Abbrev implements Serializable, Comparable<Abbrev> {

	public enum ReplacementTypes {
		AT_CARET("At caret"), BUFFER("Buffer"), LINE("Line"), WORD("Word"), CHAR(
				"Character");

		private String label;

		ReplacementTypes(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	}

	public enum ReplementSelectionTypes {
		NOTHING("Nothing"), SELECTION("Selection"), SELECTED_LINES(
				"Selected lines");

		private String label;

		ReplementSelectionTypes(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	}

	/**
	 * A short name of the abbreviation.
	 */
	private String name;

	/**
	 * The abbreviation that will get expanded.
	 */
	private String abbreviation;

	/**
	 * The expansion of the abbreviation.
	 */
	private String expansion;

	/**
	 * True if the abbreviation should be indented when changed.
	 */
	public boolean autoIndent;

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		String oldValue = this.abbreviation;
		this.abbreviation = abbreviation;

		propertySupport.firePropertyChange("abbreviation", oldValue,
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

	public WhenInvokedAsCommand getWhenInvokedAsCommand() {
		return whenInvokedAsCommand;
	}

	public void setWhenInvokedAsCommand(
			WhenInvokedAsCommand whenInvokedAsCommand) {
		this.whenInvokedAsCommand = whenInvokedAsCommand;
	}

	public static class WhenInvokedAsCommand {
		/**
		 * The type of input provided for the abbreviation generation.
		 */
		public ReplacementTypes replacementType = ReplacementTypes.AT_CARET;

		/**
		 * The type of input provided for the abbreviation generation when text
		 * is selected in the buffer.
		 */
		public ReplementSelectionTypes replacementSelectionType = ReplementSelectionTypes.NOTHING;
	}

	public WhenInvokedAsCommand whenInvokedAsCommand = new WhenInvokedAsCommand();

	/**
	 * Creates a new instance of Abbrev
	 */
	public Abbrev(String name, String abbreviation, String expansion) {
		setName(name);
		setAbbreviation(abbreviation);
		setExpansion(expansion);
	}

	@Override
	public String toString() {
		return name + " (" + abbreviation + ")";
	}

	public int compareTo(Abbrev abbrev) {
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
		Abbrev other = (Abbrev) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}
}
