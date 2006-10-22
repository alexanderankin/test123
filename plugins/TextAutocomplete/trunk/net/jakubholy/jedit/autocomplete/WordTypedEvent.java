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
	/** The first letter of a new word has been typed. 
	 * Word = the letter, Message = the insertion == the Word. */
	public static final int AT_START   = 0;
	
	/** A letter has been appended to the word being typed. 
	 * Message == the character or string appended (String).
     * The Word has already the insertion appended. */
	public static final int INSIDE     = 1;
	
	/** The word has been finished i.e. a 1st non-word character has been typed.
	 * Word = the ended word, Message = the non-word character that has ended the word. */
	public static final int AT_END     = 2;
	
	/** The word has become invalid (the user pressed enter, moved the caret ...).
	 * Word = the invalidated word, Message == null. */
	public static final int RESET	   = 3;
	
	/** Some characters have been deleted from the end
	 * of the word. Message = length of the deleted part (Integer).*/
	public static final int TRUNCATED   = 4;
	
	public static final String typeNames[] = { "AT_START", "INSIDE", "AT_END", "RESET", "TRUNCATED" };
	
	/** The word typed so far if any. */
	final StringBuffer 	word;
	/** Type-dependent content of the event such as the word inserted. */
	final Object 			message;
	/** Type of the event. */    
	final int 				type;
	
	
	/**
	 * Constructs an event of the type 'type' including a 'message'.
	 * @param word The word typed so far or an empty buffer if none exists.
	 * @param message the object of the event (e.g. that what is received);
	 * 		may be null (depends on message type)
	 * @param type the type of the event
	 */
	public WordTypedEvent( int type, StringBuffer word, Object message ) {
		if(word == null)
		{ throw new NullPointerException("The 'word' passed to the constructor may not be null."); }
		this.type = type;
		this.word = word;
		this.message = message;
		
	}//  constr
	
	/** 
	 * Returns type of the event.
	 * @return type of the event. 
	 */
	public int getType() {return type; };
	
	/** Get name of the constant representing this type. */
	public static String getTypeName( int type ) { return typeNames[ type ];}
	
	/** 
	 * Returns 'content' of the event, which is type-dependent.
	 * @return 'content' of the event 
	 */
	public Object getMessage() { return message; };
	
	/**
	 * The word typed so far (including the insertion).
	 * @return Return the word typed so far (including the insertion)
	 * 			or an empty StringBuffer if none.
	 */
	public StringBuffer getWord() {
		return word;
	}
	
	public String toString(){
		return "WordTypedEvent ["+getTypeName(type)+"]: word "
		+ word + ", message: " + message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if(obj instanceof WordTypedEvent)
		{
			if(obj == this) return true;
			
			WordTypedEvent event = (WordTypedEvent) obj;
			if ((event.getMessage() == null && getMessage() != null)
					|| event.getMessage() != null && getMessage() == null)
				return false;
			if ((event.getWord() == null && getWord() != null)
					|| event.getWord() != null && getWord() == null)
				return false;
			return getType() == event.getType()
				&& (getMessage() == event.getMessage() 
						|| getMessage().equals(event.getMessage()))
				&& (getWord().toString().equals( event.getWord().toString() ));
		}
		else
		{ return false; }
		
	}
	
	
}