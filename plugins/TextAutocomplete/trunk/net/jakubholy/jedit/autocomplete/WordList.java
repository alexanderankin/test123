/*
 * Created on 9.2.2005
 */
package net.jakubholy.jedit.autocomplete;

/**
 * A storage of words that can be used as completions.
 * 
 * {@link AutoComplete} stores there all words typed
 * so far that satisfy certain conditions given by 
 * the {@link PreferencesManager}.
 */
public interface WordList {

	/** 
	 * Return an array of all words in the list starting
	 * with the given prefix.
	 * @param The prefix whose completions we search; at least 1 letter.
	 * @return An array of possible completions of an  empty array.
	 * 	Entries in the array are sorted.
	 *  */
	public Completion[] getCompletions( String prefix );
	
	/** Returns all words in the list in a sorted array. */
	public Completion[] getAllWords();
	
	/** Insert the completion into the list.
	 * @return False if the completion has already been in the list. */
	public boolean add( Completion completion );
    
    /** Return true if is in the list.*/
    public boolean containes( Completion completion );
    
    public void addAll( Completion[] completions );
    
    /** Removes the specified element from this wordList if it is present. */
    public boolean remove( Completion completion ); 
    
    /** Removes all of the elements from this wordList. */
    public void clear();
    /** Returns the number of elements in this wordlist. */
    int size();

}; // WordList
