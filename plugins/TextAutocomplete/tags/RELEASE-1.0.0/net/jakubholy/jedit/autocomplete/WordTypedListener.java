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

import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.util.Log;

// WordTypedListener {{{
//
/**
 * Listens for word being inserted or removed into/from a buffer to know when a
 * whole word has been typed & notifies its observers.
 * 
 * What is a word is detemined by the method accept of the call-back object
 * checkIsWord. Replace it by another one to change what characters are treated
 * as a part of a word (e.g. accept '_' too as a part of a word). By default,
 * only letters are considered to belong to a word.
 * 
 * It is observable and fires the events AT_START, INSIDE, AT_END, RESET and
 * TRUNCATED when a word is inserted/ removed, see
 * {@link net.jakubholy.jedit.autocomplete.WordTypedEvent}. Notice that it
 * fires an event not only when a word has been finished but whenever the word
 * of the buffer changes.
 * 
 * @see net.jakubholy.jedit.autocomplete.WordTypedEvent
 * @see org.gjt.sp.jedit.buffer.BufferAdapter
 * @see org.gjt.sp.jedit.buffer.BufferListener
 * 
 * Note: according to the documentation of jEdit it's prefered to use
 * BufferAdapter since BufferListener might change in the future.
 * 
 */
public class WordTypedListener extends BufferAdapter
{
	/** Value of the lastCaret when it is not set. */
	private static final int CARET_UNSET = -1;

	/** How much logging shall be printed to jEdit's log. */
	public int logLevel = 
		PreferencesManager.getPreferencesManager().getLogLevel();

	/** Offset of the previously inserted word. */
	int lastCaret = CARET_UNSET;

	/** The word being currently typed. */
	StringBuffer word = new StringBuffer(15);
	
	/** Empty strign buffer. */
	final StringBuffer emptyWord = new StringBuffer(0);

	/** The text to be appended to the end of the word. */
	String insertion;

	// //////////////////////////////////////////////////////////////////////////////////////
	// BUFFER CHANGE STUFF
	// //////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////
	// {{{ contentInserted
	/*
	 * Called when a text is inserted into the buffer (e.g. a letter is typed).
	 * @param offset offset (the number of chracters) from the stoart of the
	 * buffer; it's offset of the place where the word was inserted @param
	 * length the number of characters that have been inserted
	 * 
	 * @see org.gjt.sp.jedit.buffer.BufferAdapter
	 */
	public void contentInserted(JEditBuffer buffer, int startLine, int offset,
			int numLines, int length)
	{
		if (logLevel == PreferencesManager.LOG_ALL)
		{
			Log.log(Log.DEBUG, TextAutocompletePlugin.class,
					"WordTypedListener.contentInserted: lastCaret: " + lastCaret
							+ ", offset: " + offset + ", length: " + length);
		}

		//
		// Set the caret after a reset
		//
		if (lastCaret == CARET_UNSET)
		{
			// If not at word beginning (pref. char. non-word), ignore the
			// insertion
			// == don't start recording a word from the middle
			boolean isWordStart = true;
			if (offset > 0)
			{
				char precedingChar = buffer.getText(offset - 1, 1).charAt(0);
				// Is the preceding character a non-word character or not?
				isWordStart = ! checkIsWord.accept(emptyWord, precedingChar);
			}
			if (! isWordStart)
			{
				if (logLevel == PreferencesManager.LOG_ALL)
				{
					Log.log(Log.DEBUG, TextAutocompletePlugin.class,
							"WordTypedListener: IGNORING an insert in the middle "
									+ "of a word after a reset. Offset: "
									+ offset);
				}
				return;
			}
			else
			{
				lastCaret = offset;
			} // set the current position
		} // if after reset

		// HANDLE INSERTION
		// If word inserted behind the last insertion and it's only 1 character
		//
		if ((lastCaret == offset) && (length == 1)) // TODO: support insertion
													// of longer texts
		{
			lastCaret = offset + length; // move to the end of the insertion
			insertion = buffer.getText(offset, length);

			if (checkIsWord.accept(word, insertion.charAt(0)))
			{
				//
				// INSERT
				//
				if (logLevel == PreferencesManager.LOG_ALL)
				{
					Log.log(Log.DEBUG, TextAutocompletePlugin.class,
							"WordTypedListener: Char appended: " + insertion);
				}

				int eventType = (word.length() == 0) ? WordTypedEvent.AT_START
						: WordTypedEvent.INSIDE;

				// Append the insertion before notifying observers
				word.append(insertion);
				notifier.notifyObservers(new WordTypedEvent(eventType, 
						(new StringBuffer(15)).append(word),
						insertion));
			}
			else
			{
				// word ended or between non-word characters (then
				// word.length()==0)
				if (logLevel >= PreferencesManager.LOG_WORD) Log.log(Log.DEBUG,
						TextAutocompletePlugin.class,
						"WordTypedListener: WORD ENDED: \"" + word + "\"");

				notifier.notifyObservers(new WordTypedEvent(
						WordTypedEvent.AT_END, (new StringBuffer(15)).append(word), insertion));
				reset();
			} // if-else is a word constituent

		}
		else
		// lastCaret != offset => reset: backspace, cared moved, undo/redo etc.
		{
			if (logLevel == PreferencesManager.LOG_ALL) Log.log(Log.DEBUG,
					TextAutocompletePlugin.class,
					"WordTypedListener:firing  JUMP RESET: lastCaret: "  
					+ lastCaret + ", offset: " + offset + ", length: " + length);

			notifier.notifyObservers(new WordTypedEvent(WordTypedEvent.RESET,
					(new StringBuffer(15)).append(word), null));
			reset();
		}
	} // contentInserted }}}

	// //////////////////////////////////////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * Called when text is removed from the buffer.
	 * If the whole word is removed we issue RESET.
	 * If only a part of the word being typed is removed, we issue TRUNCATED.
	 * 
	 * @param buffer The buffer in question
	 * @param startLine The first line
	 * @param offset The start offset, from the beginning of the buffer
	 * 		We remove the text between offest and (offset+length)
	 * @param numLines The number of lines removed
	 * @param length The number of characters removed
	 * @see org.gjt.sp.jedit.buffer.BufferAdapter#contentRemoved(org.gjt.sp.jedit.buffer.JEditBuffer, int, int, int, int)
	 */
	public void contentRemoved(JEditBuffer buffer, int startLine, int offset, int numLines, int length)
	{
		if (logLevel == PreferencesManager.LOG_ALL)
		{
			Log.log(Log.DEBUG, TextAutocompletePlugin.class,
					"WordTypedListener.contentRemoved(offset:"+offset
						+ ",length:"+length + "), lastCaret:"+ lastCaret);
		}
		// Check that the text has been removed from the end of the word being typed
		// and that it's <= word.length
		
		// Check that we remove from the end of the word being typed
		if (lastCaret != (offset+length))
		{
			if (logLevel == PreferencesManager.LOG_ALL)
			{
				Log.log(Log.DEBUG, TextAutocompletePlugin.class,
						"WordTypedListener.contentRemoved: IGNORING - not "
						+ "removing from the end of the last word. LastCaret:"
								+ lastCaret + ", Offset+length: " + (offset + length));
			}
			return;
		}
		else // Removing from the end of the word being typed...
		{
			// All the word (or even more) removed?
			if(length >= word.length())
			{
				if (logLevel == PreferencesManager.LOG_ALL)
				{
					Log.log(Log.DEBUG, TextAutocompletePlugin.class,
							"WordTypedListener.contentRemoved: more than the "
							+ "word removed: word.length=" + word.length()
							+ ", removed:" + length);
				}
				
				notifier.notifyObservers(new WordTypedEvent(WordTypedEvent.RESET,
						(new StringBuffer(15)).append(word), null));
				reset();
			}
			else
			{
				// The last word has been shortened
				word.delete(word.length() - length, word.length());
				lastCaret -= length;
				notifier.notifyObservers(new WordTypedEvent(WordTypedEvent.TRUNCATED,
						(new StringBuffer(15)).append(word), new Integer(length)));
			} // if-else removing more than the length of the last word
		} // if-else not shortening the last word
		
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// {{{ reset
	/** The word currently typed is not a word => discard it and forget about it. */
	void reset()
	{
		lastCaret = CARET_UNSET;
		word.setLength(0);
	} // reset }}}

	// //////////////////////////////////////////////////////////////////////////////////////
	// OBSERVABLE STUFF
	// //////////////////////////////////////////////////////////////////////////////////////
	/** The object that to make us observable. */
	java.util.Observable notifier = new java.util.Observable()
	{
		public void notifyObservers(Object keyTypedEvent)
		{
			super.setChanged();
			super.notifyObservers(keyTypedEvent);
		}// letObserversKnow

	};

	public void addObserver(java.util.Observer o)
	{
		notifier.addObserver(o);
	}

	public void deleteObserver(java.util.Observer o)
	{
		notifier.deleteObserver(o);
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	// / RULES FOR WHAT IS A WORD x WORD SEPARATOR
	// //////////////////////////////////////////////////////////////////////////////////////
	// IMPORTANT: this filter doesn't apply for it's replaced by a filter
	// defined in the PreferencesManager - done in AutoComplete's constructor
	/** Checker that determines what is a word separator and what is not. */
	Filter checkIsWord = new Filter()
	{
		public boolean accept(StringBuffer word, char insertion)
		{
			return (Character.isLetter(insertion));
		}
	};

	// /////////////////////////////////////////////////////////////////////////////
	// {{{ Filter
	/**
	 * Decides what belongs to a word and what doesn't, i.e. it distinguishes
	 * word separators and word elements. Modify it to change what is considered
	 * to be a part of a word.
	 */
	public interface Filter
	{
		/**
		 * Decide whether the character can be appended to the word or whether
		 * it ends it.
		 * 
		 * @param word
		 *            The word typed so far, may be empty but not null.
		 * @param insertion
		 *            The character typed at the end of the word
		 * @return Return true if the insertion does not end the word i.e. if it
		 *         is not the first non-word character.
		 */
		public boolean accept(StringBuffer word, char insertion);
	} // Filter }}}

	// ////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return Returns the checkIsWord Filter
	 */
	public Filter getCheckIsWord()
	{
		return checkIsWord;
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param checkIsWord
	 *            The checkIsWord Filer to set.
	 * @see WordTypedListener.Filter
	 */
	public void setCheckIsWord(Filter checkIsWord)
	{
		this.checkIsWord = checkIsWord;
	}
} // //WordTypedListener }}}
	// ----------------------------------------------------------------
