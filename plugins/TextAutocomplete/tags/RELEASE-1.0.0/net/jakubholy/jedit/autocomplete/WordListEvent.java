package net.jakubholy.jedit.autocomplete;

import java.util.EventObject;

/**
 * Carry information about a change to the observed word list.
 * @see net.jakubholy.jedit.autocomplete.WordList 
 * @author Jakub Holy
 */
@SuppressWarnings("serial")
public class WordListEvent extends EventObject 
{

	public WordListEvent(Object source) 
	{ super(source); }

}
