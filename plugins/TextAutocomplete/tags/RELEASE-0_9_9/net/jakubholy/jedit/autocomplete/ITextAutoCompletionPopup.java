package net.jakubholy.jedit.autocomplete;

import org.gjt.sp.jedit.gui.CompleteWord;


/**
 * A pop-up window to display a list of available completions, react
 * to user input and insert a selected completion.
 * <p>
 * The purpose of this interface is to decouple the AutoComplete 
 * plugin class using the pop-up and the actual pop-up  
 * implementation based on the current jEdit API.  
 */
public interface ITextAutoCompletionPopup {

	//////////////////////////////////////////////////////////////////	display
	//	{{{ Display() method
	
	/** Display/update a popup with the given completions.
	 * @param completions The completions to set; if empty => dispose.
	 * @return Returns true if the operation succeeded (== valid completions).
	 */
	public abstract boolean showCompletions( Completion[] completions); // }}}

	////////////////////////////////////////////////////////////////// dispose
	//{{{ dispose() method
	/** Hide the popup & cease to grab the key input. */
	public abstract void dispose(); //}}}

	/** 
	 * Set the prefix being completed.
	 * We need it for positioning the pop-up and 
	 * for inserting only the missing part of a completion
	 * (e.g. only 'phant' for prefix 'ele' and completion 'elephant'). 
	 */
	public abstract void setWord(String word);

}