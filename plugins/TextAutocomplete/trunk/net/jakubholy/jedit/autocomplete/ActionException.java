package net.jakubholy.jedit.autocomplete;

/**
 * Thrown when an error condition occures while a jEdit action is triggered.
 * For example when the user tries to display the list of remembered words 
 * for a buffer that has no autocompletion attached to self.
 * See actions.xml
 * @author aja
 *
 */
public class ActionException extends Exception {
	
	/** No cause type given for this exception. */
	public static final int UNSPECIFIED 	= 0;
	/** 
	 * Attempt to invoke an action that requires an autocomplete 
	 * but no autocomplete is attached to the current buffer. 
	 */
	public static final int NOT_ATTACHED 	= 1;
	
	/** Description of the problem to display to the user. */
	String description = "<description unspecified>";
	
	/** The type of the exception - specifies the cause in more detail. */
	int causeType = UNSPECIFIED;

	public ActionException(String description) {
		super(description);
		setDescription(description);
	}

	public ActionException(int causeType) {
		super();
		setCauseType( causeType );
		if(causeType == NOT_ATTACHED)
		{ setDescription("No autocomplete attached to the current buffer."); }
	}
	
	/** Description of the problem to display to the user. */
	public String getDescription() {
		return description;
	}
	
	/** Set the description of the problem to display to the user. */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return The type of the exception - specifies the cause in more detail.
	 */
	public int getCauseType() {
		return causeType;
	}
	/**
	 * The type of the exception - specifies the cause in more detail.
	 * @param causeType See the constants of this class.
	 */
	public void setCauseType(int causeType) {
		this.causeType = causeType;
	}

}
