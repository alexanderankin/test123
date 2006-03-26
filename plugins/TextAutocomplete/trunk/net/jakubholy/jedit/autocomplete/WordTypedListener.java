/* 
 * KeyTypedListener.java
 * $id$
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
package net.jakubholy.jedit.autocomplete;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.BufferChangeAdapter;
import org.gjt.sp.util.Log;

//WordTypedListener {{{
//
// TODO: implement contentRemoved - we need RESET/TRUNCATED immediately after the 1st backspace/
// 		 removal; so far consecutive backspaces are treated as one and only detected on the next insert
//
/**
 * Listens for word being inserted or removed into/from a buffer
 * to know when a whole word has been typed & notifies its
 * observers.
 * 
 * What is a word is detemined by the method accept of
 * the call-back object checkIsWord. Replace it by another
 * one to change what characters are treated as a part of
 * a word (e.g. accept '_' too as a part of a word). By
 * default, only letters are considered to belong to a word.
 * 
 * It is observable and fires the events AT_START, INSIDE, 
 * AT_END, RESET and TRUNCATED when a word is inserted/ 
 * removed, see {@link net.jakubholy.jedit.autocomplete.WordTypedEvent}.
 * Notice that it fires an event not only when a word has
 * been finished but whenever the word of the buffer 
 * changes.
 *
 * @see WordTypedEvent
 * @see org.gjt.sp.jedit.Buffer#addBufferChangeListener
 * @see org.gjt.sp.jedit.buffer.BufferChangeAdapter
 * @see org.gjt.sp.jedit.buffer.BufferChangeListener
 * 
 * Note: according to the documentation of jEdit it's prefered
 * to use BufferChangeAdapter since BufferChangeListener
 * might change in the future.
 *
 */
public class WordTypedListener
extends BufferAdapter //ChangeAdapter
{
	static int LOG_NONE = 0;
	/** Log only AT_END events */
	static int LOG_WORD = 1;
	/** Log all events + entry to contentInserted. */
	static int LOG_ALL  = 2;
	
	/** How much logging shall be printed to jEdit's log. */
	public int logLevel = LOG_NONE;
	
	/** Offset of the previously inserted word. */
	int 			lastCaret 	= -1;
	
	/** The word being currently typed. */
	StringBuffer 	word 		= new StringBuffer( 15 );
	/** The text to be appended to the end of the word. */
	String 			insertion;
	
	////////////////////////////////////////////////////////////////////////////////////////
	//						BUFFER CHANGE STUFF
	////////////////////////////////////////////////////////////////////////////////////////    
	////////////////////////////////////////////////////////////////////////////////////////
//	 {{{ contentInserted
    /*
     * @param offset offset (the number of chracters) from the stoart of the buffer; it's
     * offset of the place where the word was inserted
     * @param length the number of characters that have been inserted
     */
	public void contentInserted(Buffer buffer, int startLine, int offset, int numLines, int length)
    {
        if (logLevel == LOG_ALL) Log.log( Log.DEBUG, this, "Buff. insertion, lastC: " +lastCaret + " off: " + offset + " len: " + length );                
       
        //
        // Set the caret after a reset
        //
        if ( lastCaret == -1 )
        {            
            // If not at word beginning (pref. char. non-word) => ignore
            // - don't start recording a word from a middle
            if ( offset > 0 && checkIsWord.accept( word, buffer.getText( offset-1, 1 ).charAt(0) ) )
            {
            	if (logLevel == LOG_ALL) Log.log( Log.DEBUG, this, "IGNORING an insert in the middle " +
            			"of a word after a reset. Offset: "+offset );
            	return; 
            }
            
            lastCaret = offset; // set the current position
        } // if after reset

        // HANDLE INSERTION
        // If word inserted behind the last insertion and it's only 1 character
        //
        if ( (lastCaret == offset) && (length == 1) ) // TODO: support insertion of longer texts
        {                    
            lastCaret = offset + length;    // move to the end of the insertion
            insertion = buffer.getText( offset, 1 );
            
            if ( checkIsWord.accept( word, insertion.charAt(0) ) )
            {
            	//
            	// INSERT
            	//
            	if (logLevel == LOG_ALL) Log.log( Log.DEBUG, this, "char appended: " + insertion );

                // Append the insertion before  notifying observers
                word.append( insertion );
                
            	int eventType = ( word.length() == 0)? WordTypedEvent.AT_START : WordTypedEvent.INSIDE; // FIXME:never true - we've just appended the insertion to the word???
                
            	notifier.notifyObservers( new WordTypedEvent( eventType, word, insertion ) );
            }
            else
            {
                // word ended or between non-word characters (then word.length()==0)
            	if (logLevel >= LOG_WORD) Log.log( Log.DEBUG, this, "WORD ENDED: \"" + word + "\"");
            	
            	notifier.notifyObservers( new WordTypedEvent( WordTypedEvent.AT_END, word, insertion ) );
                reset();               
            } // if-else is a word constituent
                      
        }
        else // reset: backspace, cared moved, undo/redo etc.
        {
        	if (logLevel == LOG_ALL) Log.log( Log.DEBUG, this, "JUMP RESET" +lastCaret + " off: " + offset + " len: " + length );
        	
        	notifier.notifyObservers( new WordTypedEvent( WordTypedEvent.RESET, word, null ) );
        	reset();           
        }
    } // contentInserted }}}
    
	////////////////////////////////////////////////////////////////////////////////////////
	//	 {{{ reset
	/** The word currently typed is not a word => discard it and forget about it. */
    void reset(){    	
        lastCaret = -1;
        word.setLength(0);       
    } // reset }}}
    
	////////////////////////////////////////////////////////////////////////////////////////
	//					OBSERVABLE STUFF
	////////////////////////////////////////////////////////////////////////////////////////
	/** The object that to make us observable. */
	java.util.Observable notifier = new java.util.Observable(){
		public void notifyObservers( Object keyTypedEvent )
	    {
			super.setChanged();
	        super.notifyObservers( keyTypedEvent );
	    }// letObserversKnow
		
	};
	
	public void addObserver(java.util.Observer o){ notifier.addObserver(o); }
    public void deleteObserver(java.util.Observer o){ notifier.deleteObserver(o); }
    
////////////////////////////////////////////////////////////////////////////////////////
    ///				RULES FOR WHAT IS A WORD x WORD SEPARATOR 
////////////////////////////////////////////////////////////////////////////////////////
    // IMPORTANT: this filter doesn't apply for it's replaced by a filter
    // defined in the PreferencesManager - done in AutoComplete's constructor 
    /** Checker that determines what is a word separator and what is not. */
	Filter			checkIsWord = new Filter(){
		public boolean accept(StringBuffer word, char insertion){
			return (Character.isLetter( insertion ));
		}
	};
 
	///////////////////////////////////////////////////////////////////////////////
    // {{{ Filter
    /** Decides what belongs to a word and what doesn't,
	 * i.e. it distinguishes word separators and word elements.
	 * Modify it to change what is considered to be a part of a word.*/
    public interface Filter
    {
    	/** 
    	 * Decide whether the character can be appended to the word
    	 * or whether it ends it.
    	 * @param word The word typed so far, may be empty but not null.
    	 * @param insertion The character typed at the end of the word
    	 * @return Return true if the insertion does not end the word i.e.
    	 * if it is not the first non-word character.
    	 * */
    	public boolean accept(StringBuffer word, char insertion);
    } // Filter }}}
    

	//////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return Returns the checkIsWord Filter
	 */
	public Filter getCheckIsWord() {
		return checkIsWord;
	}
	////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param checkIsWord The checkIsWord Filer to set.
	 * @see WordTypedListener.Filter
	 */
	public void setCheckIsWord(Filter checkIsWord) {
		this.checkIsWord = checkIsWord;
	}
} // //WordTypedListener }}} ----------------------------------------------------------------
