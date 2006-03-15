package net.jakubholy.jedit.autocomplete;

/**
 * Thrown when an error condition occures while a jEdit action is triggered.
 * See actions.xml
 * @author aja
 *
 */
public class ActionException extends Exception {
	
	/** Description of the problem to display to the user. */
	String description;

	public ActionException(String description) {
		super(description);
		setDescription(description);
	}
	/** Description of the problem to display to the user. */
	public String getDescription() {
		return description;
	}
	
	/** Set the description of the problem to display to the user. */
	public void setDescription(String description) {
		this.description = description;
	}

}
