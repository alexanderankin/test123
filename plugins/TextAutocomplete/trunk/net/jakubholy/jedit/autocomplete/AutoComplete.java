/* 
 * AutoComplete.java
 * $$id$$
 * author Jakub (Kuba) Holy, 2005
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
 
 ///////////////////////////////////////////////////////////////////////////////
 // TO BE DONE:
 // - handle backspace (=> a new list of completions if appropriate, ...)
 // - detach calls attach
 // - +fce rememberWord( word ) ^ uzij v update, +get/setTheListener, 
 //   +get/setWordList
 // - attach: musim nastavit textArea? <=> uziva se jinde nez v displayPopup?
 // - Pref.Mng. - upozorni ze getIsWordFilter se uziva jen pri inic.
 // + parseBuffer ( all text as string/by line/ as a Segment ), volej v attach !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 //   Add Pref.Mngr.isParseBufferEnabled()
 // - +option: case-insensitive completion search (podpora toho uz existuje)
 // - enhancement: let the user define regions that are ignored / only considered
 //		( such as comments in a source code )
 // - log start, attach (+buffer), detach(-"-)
 // - enable the user to remove a word from the list directly via the current pop-up window
 // - add 'word blacklist' - words to never remember
package net.jakubholy.jedit.autocomplete;

//{{{ Imports

import java.awt.Point;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.SwingUtilities;

import net.jakubholy.jedit.autocomplete.WordTypedListener.Filter;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;


//}}}

/**
 * Try to complete a word typed in the associated buffer.
 * Automatically try to find any completions for the current
 * word being typed and if there're any, present them to the user 
 * in a pop-up list. A list of possible completions is 
 * constructed from words typed so far in the buffer that
 * satisfy some conditions.
 * 
 * The class observers an {@link WordTypedListener} 
 * to get notified of word events such as a word 
 * started/finished/invalidated (when the user 
 * jumps to another location etc.).
 * 
 * @see net.jakubholy.jedit.autocomplete.PreferencesManager
 * @see net.jakubholy.jedit.autocomplete.WordTypedListener
 * @see net.jakubholy.jedit.autocomplete.CompletionPopup
 * 
 * Based on jEdit's CompleteWord feature.
*/
public class AutoComplete //{{{
implements java.util.Observer
{
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Holds an instance of AutoComplete for each buffer that has got one.
	 */
	static Map bufferToAutoComplete = new IdentityHashMap();
	
	/** 
	 * Create a new AutoComplete that starts working for the given buffer if the buffer has none.
	 * If the buffer already has an AutoComplete, it's only returned. 
	 */
	public static AutoComplete CreateAutoCompleteAction( Buffer buffer )
	{
		AutoComplete autoComplete = (AutoComplete) bufferToAutoComplete.get(buffer);
		if(autoComplete == null)
		{
			autoComplete = new AutoComplete(buffer);
			bufferToAutoComplete.put(buffer, autoComplete);
		}
		return autoComplete;
	}
	
	/** 
	 * Attach an AutoComplete to the buffer. 
	 * It either re-attaches an existing detached one ore creates a new one. 
	 * If the buffer already has an AutoComplete attached to it, it's detached and re-attached. 
	 */
	public static void attachAction( Buffer buffer )
	{ CreateAutoCompleteAction(buffer).attach( buffer ); }
	
	/** 
	 * Detach the AutoCompletion from the given buffer if it has one.
	 * But we keep the AutoComplete for the buffer and it can be later re-attached. 
     * Word list is cleared.  
     */
	public static void detachAction( Buffer buffer )
	{
		AutoComplete autoComplete = (AutoComplete) bufferToAutoComplete.get(buffer);
		if(autoComplete == null || autoComplete.getBuffer() == null)
		{ Log.log(Log.DEBUG, null, "DETACH: doing nothing - no AutoComplete attached to the current buffer."); }
		else
		{ autoComplete.detach(); }
	}
	
	/** 
	 * Parse the current buffer and add all words in it to the list. 
	 * @throws ActionException When the action cannot be performed for some reason 
	 */
	public static void parseAction( Buffer buffer ) throws ActionException
	{
		AutoComplete autoComplete = (AutoComplete) bufferToAutoComplete.get(buffer);
		if(autoComplete == null || autoComplete.getBuffer() == null)
		{ throw new ActionException("No AutoComplete attached to the current buffer"); }
		else
		{ autoComplete.parseBuffer(); }
	}
	
	/**
	 * Show the WordList Editor user interface to edit words remembered for the current buffer. 
	 * @throws ActionException When the action cannot be performed for some reason 
	 */
	public static void showWordsAction( Buffer buffer ) throws ActionException
	{
		final AutoComplete autoComplete = (AutoComplete) bufferToAutoComplete.get(buffer);
		if(autoComplete == null || autoComplete.getBuffer() == null)
		{ throw new ActionException("No AutoComplete attached to the current buffer"); }
		else
		{
			java.awt.EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                new WordListEditorUI(autoComplete).setVisible(true);
	            }
	        });
		}
	}
	
	/**
	 * Attach AutoComplete to all opened buffers.
	 * @throws ActionException When the action cannot be performed for some reason 
	 */
	public static void attachToAllBuffersAction() throws ActionException
	{
		Buffer[] openedBuffers = jEdit.getBuffers();
		for (int i = 0; i < openedBuffers.length; i++) {
			attachAction( openedBuffers[i] );
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Detach and destroy (forget) all instance of AutoComplete.
	 */
	public static void destroyAllAutoCompletes()
	{
		for (Iterator acIter = bufferToAutoComplete.values().iterator(); acIter.hasNext();) 
		{
			AutoComplete autoComplete = (AutoComplete) acIter.next();
			autoComplete.detach();
			if(autoComplete.getBuffer() != null)
			{ Log.log(Log.DEBUG, null, "DESTROY ALL: removing autocomplete for the buffer "
					+ autoComplete.getBuffer().getPath() ); }
			acIter.remove();
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
    // {{{ constructor
	/** Create a new AutoComplete that start's working for the given buffer. */
    private AutoComplete( Buffer buffer )
    {
        Log.log(Log.DEBUG, this, "CREATED ");
        this.thePopup 	= new CompletionPopup( jEdit.getActiveView(),  buffer);
        //this.view 		= view;
        //this.buffer 	= buffer;
        //this.textArea 	= view.getTextArea();
        m_wordTypedListener = new WordTypedListener();
        m_wordTypedListener.addObserver( this );
        m_wordTypedListener.setCheckIsWord( prefManager.getIsWordFilter() );	// pass reference to Pref.Mngr's word filter, don't copy!
        //this.buffer.addBufferChangeListener( m_wordTypedListener );
        this.attach(buffer);
    } // constructor }}}

	///////////////////////////////////////////////////////////////////////////
    // {{{ detach
    /** Detach the AutoCompletion from the buffer it's bound to == stop working. 
     * Word list is emptied. */
    public void detach()
    {
        Log.log(Log.DEBUG, this, "Detaching from the buffer: " + buffer);
    	buffer.removeBufferListener( m_wordTypedListener );
    	if ( thePopup.isVisible() ) { thePopup.dispose(); }
        wordList.clear();
        buffer = null;
    } // detach }}}

	///////////////////////////////////////////////////////////////////////////
    // {{{ attach
    /** Attach the AutoCompletion to the buffer == start completing there.
     * The buffer is parsed for words to remeber as possible completions. */
    public void attach( Buffer buffer )
    {
        // If attached already detach first
        if ( this.buffer != null ) { detach(); }
        
        Log.log(Log.DEBUG, this, "Attaching to the buffer: " + buffer);
    	this.buffer = buffer;
    	buffer.addBufferListener( m_wordTypedListener );
    	this.thePopup.setBuffer( buffer );
        // Collect words in the buffer
        parseBuffer();
    } // attach }}}
    
	///////////////////////////////////////////////////////////////////////////
	// {{{ update
	/* Process word events w.r.t. their types.
	 * @param o = sender of the message (arg). The observer may observe more objects. */
	public void update(java.util.Observable o, Object arg ) 
	{
		 
		WordTypedEvent event = (WordTypedEvent)arg;
		int type = event.type;
		this.thePrefix = event.getWord().toString();
		
		switch(type) 
		{
			case WordTypedEvent.AT_START:
				// Nothing, 1-letter words are too short
				break;
			case WordTypedEvent.INSIDE:
			    // Offer a completition
			    if ( thePrefix.length() >= prefManager.minPrefixLength() ) { displayCompletionPopup(); }
				break;
			case WordTypedEvent.AT_END:
			    // Remember the word & hide the popup
			    if ( prefManager.isWordToRemember(thePrefix) )
			    {
					// TODO: (?) assert that prefix == the word before the caret(we have: "prefix|")
                    wordList.add( new Completion(thePrefix) );
			    }
			    if ( thePopup.isVisible() ) { thePopup.dispose(); }
				break;
			case WordTypedEvent.RESET:				
			    // Hide the popup
			    if ( thePopup.isVisible() ) { thePopup.dispose(); }
			case WordTypedEvent.TRUNCATED:				
			    // Reset the completition list and perhaps hide the popup
			    // TODO: implement WordTypedEvent.TRUNCATED as soon as supported by the listener
				break;
			default:
				Log.log( Log.ERROR, this, "AutoComplete.update: " +
						"unknown WordTypedEvent type: " +type + ", the event:" + event );
			// unknown type == an error           break;
		}//switch        
	}//update }}}
	
	//////////////////////////////////////////////////////////////////////	
    //	{{{ completeWord() method
	/** Display a pop-up window with possible completions for
	 * the word being typed. 
	 */
	protected void displayCompletionPopup()
	{		
		// TODO: (?) check jEdit.getActiveView().getBuffer() == myBuffer
        JEditTextArea textArea = jEdit.getActiveView().getTextArea();
        
        //int caretLine = textArea.getCaretLine();
		int caret = textArea.getCaretPosition();

		/* If the buffer isn't editable the user doesn't enter 
		 * a character and thus the pop-up will never show up.
		if(!buffer.isEditable())
		{
			textArea.getToolkit().beep();
			return;
		}
		*/

		// Get all possible completions
		Completion[] completions = getCompletions( thePrefix );

		//
		// Handle the number of completions
		//
		if ( completions.length >= 1 )
		{
			
			thePopup.setWord( thePrefix );
			
			if ( ! thePopup.isVisible() ) {
				//  Display the popup
				textArea.scrollToCaret(false);
				Point location = textArea.offsetToXY(
					caret - thePrefix.length());
				location.y += textArea.getPainter().getFontMetrics()
					.getHeight();

				SwingUtilities.convertPointToScreen(location,
					textArea.getPainter());
			    thePopup.display( location , getCompletions( thePrefix ) );
			} else {
			    // The pop-up is alredy visible => update it only
			    thePopup.setCompletions( getCompletions( thePrefix ) );
			} // if-else pop-up visible
			
		} 
		else
		{
			if ( thePopup.isVisible() ) { thePopup.dispose(); }
		}
	} // displayCompletionPopup }}}
	
	//////////////////////////////////////////////////////////////////////////////////
	/** Get all possible completion for the given prefix. */
	protected Completion[] getCompletions( String prefix )
	{
	    // TODO: (low) If the pop-up is already showing completions and only
        // the user has typed 1 more char we don't need to acces
        // the word list, instead we could work on the list we have already
        // TODO: (low) Add 'greatest common part' completion if the user desires it
        //  Ex.: prefix = pr, completions = prachovka, prachy => create the completion
        // 'prach' and add it to the front of the list. Use System.arraycopy.
        return wordList.getCompletions(prefix);
	    /*
        return new Completion[]{ new Completion("dummy10"), new Completion("dummy20"), 
	    		new Completion("dum"), new Completion("dudak") };
        */
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	//			FIELDS
    ////////////////////////////////////////////////////////////////////////////////////
	/** The prefix to complete/typed so far. */
	private String thePrefix = "";
	private CompletionPopup thePopup;
	//private View view;
	private Buffer buffer = null;
	//private JEditTextArea textArea;
    private WordTypedListener m_wordTypedListener;
    private PreferencesManager prefManager = PreferencesManager.getPreferencesManager();
    /** Place to store words typed so far & to use as completions. */
    private WordList wordList = new WordListTreeSet();
	
	//////////////////////////////////////////
    public String getThePrefix() {
        return thePrefix;
    }
    public void setThePrefix(String thePrefix) {
        this.thePrefix = thePrefix;
    }
    /** Set the list of words used to find completions. */
    public WordList getWordList() {
        return wordList;
    }
    /** Get the list of words used to find completions. */
    public void setWordList(WordList wordList) {
        this.wordList = wordList;
    }
    /** Remove the given word from the list of remembered words.
     * @return True if the word was in the list & is removed. */
    public boolean forgetWord(String word2remove) {
        return wordList.remove( new Completion(word2remove) );
    }
    /** Add the given word to the list of remembered words.
     * @return True if the word has not yet been in the list. */
    public boolean rememberWord(String word2remember) {
        return wordList.add( new Completion(word2remember) );
    }
    
    /**@return A concatenation of all rememberd words separated by ','. */
    public String getWords() {
        Completion[] ca = wordList.getAllWords();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ca.length; i++) {
            sb.append( ca[i] + ",");
        }
        return sb.toString();
    }
    
    /** Parse the current buffer and add all words in it to the list. */
    public void parseBuffer() {
        int length = buffer.getLength();
        Filter checkIsWord = m_wordTypedListener.getCheckIsWord();
        String insertion = null;
        StringBuffer word = new StringBuffer(15);

        // Simulate typing the buffer char by char
        for (int offset = 0; offset < length; offset++) {
            
            insertion = buffer.getText( offset, 1 );
            if ( checkIsWord.accept( word, insertion.charAt(0) ) ) {
                word.append(insertion);
            } else {
                if ( prefManager.isWordToRemember(word.toString()) )
                { wordList.add( new Completion(word.toString()) ); }
                word.setLength(0);
            } // if-else is a word character
            
        } // for all characters of the buffer
        
        // Remember the last word followed by EOF if there is any.
        if ( prefManager.isWordToRemember(word.toString()) )
        { wordList.add( new Completion(word.toString()) ); }
    }

	public Buffer getBuffer() {
		return buffer;
	}

	public void setBuffer(Buffer buffer) {
		this.buffer = buffer;
	}
} // AutoComplete }}} **********************************************************
