/**
 * 
 */
package net.jakubholy.jedit.autocomplete;

/**
 * Thrown when we try to remember another word (completion) but the number of
 * remembered words would exceed the maximum allowed (set in preferences).
 * @author Jakub Holy
 */
public class MaxWordsExceededException extends Exception 
{
	/** The word that we wanted to remember but couldn't because of the limit. */
	String lastWord = null;

	/**
	 * 
	 */
	public MaxWordsExceededException() 
	{ super(); }

	/**
	 * @param lastWord The word that couldn't be added because 
	 * the limit would be exceeded.
	 */
	public MaxWordsExceededException(String lastWord) 
	{ setLastWord(lastWord); }

	public MaxWordsExceededException(int maxCountOfWords) 
	{ this( String.valueOf(maxCountOfWords) ); }

	/** Return the word that we wanted to remember but couldn't because of the limit. */
	public String getLastWord() {
		return lastWord;
	}

	/** Set the word that we wanted to remember but couldn't because of the limit. */
	public void setLastWord(String lastWord) {
		this.lastWord = lastWord;
	}

}
