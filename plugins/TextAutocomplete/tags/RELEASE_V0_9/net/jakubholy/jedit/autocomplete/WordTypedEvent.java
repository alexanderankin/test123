package net.jakubholy.jedit.autocomplete;

/**
 * Sent to observers of this WordTypedListener.
 * Carries info about the word being typed, the current
 * insertion (or perhaps removal) and type of the event.
 * Carries info about a key typed event:
 * whether it's a word start/end/body and the prefix typed so far.
 * 
 * @see WordTypedListener
 */ 
class WordTypedEvent 
{		
	/** The first letter of a new word has been typed. Message == null. */
	public static final int AT_START   = 0;
	/** A letter has been appended to the word being typed. 
	 * Message == the character or string appended (String).
     * The word has already the character appended. */
	public static final int INSIDE     = 1;
	/** The word has been finished i.e. a 1st non-word character has been typed.
	 * Message == null. */
	public static final int AT_END     = 2;
	/** The word has become invalid (the user pressed enter, moved the caret ...).
	 * Message == null. */
	public static final int RESET	   = 3;		
	/** Some characters have been deleted from the end
	 * of the word. Message = length of the deleted parts (int).*/
	public static final int TRUNCATED   = 4;
	
	public static final String typeNames[] = { "AT_START", "INSIDE", "AT_END", "RESET" };
	
	/** The word typed so far if any. */
	final StringBuffer 	word;
	/** Type-dependent content of the event such as the word inserted. */
	final Object 			message;
	/** Type of the event. */    
	final int 				type;
	
	
	/**
	 * Constructs an event of the type 'type' including a 'message'.
	 * @param word The word typed so far or an empty buffer if none exists.
	 * @param message the object of the event (e.g. that what is received)
	 * @param type the type of the event
	 */
	public WordTypedEvent( int type, StringBuffer word, Object message ) {
		this.type = type;
		this.word = word;
		this.message = message;
		
	}//  constr
	
	/** Returns type of the event.
	 * @return type of the event. */
	public int getType() {return type; };
	
	/** Get name of the constant representing this type. */
	public static String getTypeName( int type ) { return typeNames[ type ];}
	
	/** Returns 'content' of the event, which is type-dependent.
	 *@return 'content' of the event */
	public Object getMessage() { return message; };
	
	/**
	 * @return Return the word typed so far or an empty buffer if none.
	 */
	public StringBuffer getWord() {
		return word;
	}
	
	public String toString(){
		return "WordTypedEvent ["+getTypeName(type)+"]: word "
		+ word + ", message: " + message;
	}
}