/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2001 Slava Pestov
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

 /**
  * @see Character#isWhitespace
  * @see org.gjt.sp.jedit.Buffer#getStringProperty for "noWordSep"
 */

package net.jakubholy.jedit.autocomplete; // was: org.gjt.sp.jedit.gui;

//{{{ Imports
import java.awt.Component;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

/**
 * A pop-up window to display a list of available completions.
 *
 * When visible it intercepts all keys and either passes them on
 * or handles them if they've a special meaning for it (such
 * as dispose or insert the selected completion). What keys
 * have what meaning is determined by a {@link PreferencesManager}.
 * It could be extended to react also to other events such as
 * focus lost.
 *
 * The window is absolutely unaware of the AutoComplete that's
 * using it and that contains most of the logic to show/dispose
 * the pop-up/update the list of completions.
 */
@SuppressWarnings("serial")
public class CompletionPopup extends org.gjt.sp.jedit.gui.CompletionPopup
{

	/** A default location of the pop-up. */
	private static final Point defaultLocation = new Point();
	
/////////////////////////////////////////////////////////////////// GUI
    /**
     * Create a new (so far invisible) popup with its listeners.
     * @param buffer The buffer the AutoComplete is attached to; may be null but
     * must be set before the first display of this.
     */
    //{{{ Constructor
	public CompletionPopup( View view )
	{
		super( view, defaultLocation );
		this.view = view;
	} //}}}

	//////////////////////////////////////////////////////////////////	display
	//	{{{ Display() method
	/** Display a popup with the given completions. */
	public void display( Point location, Completion[] completions )
	{
		setLocation( location );
	    setCompletions( completions );
	} // }}}

	////////////////////////////////////////////////////////////////// setCompletions
	// {{{ setCompletions
	/** Set the list of completions shown in the pop-up window. Not null.
	 * Size of the pop-up is adjusted to fit the length of the completions.
	 * @param completions The completions to set; if empty => dispose.
	 * @return Returns true if the operation succeeded (== valid completions). */
	public boolean setCompletions( Completion[] completions )
	{
		//Log.log(Log.DEBUG,this,"setComplet. called ");

	    if ( completions.length == 0 ) {
	    	dispose();
	    	return false;
	    } else {
			reset( new Words(completions), true );
		    return true;
	    }
	} // setCompletions }}}

	////////////////////////////////////////////////////////////////// dispose
	//{{{ dispose() method
	/** Hide the popup & cease to grab the key input. */
	public void dispose()
	{
		// TODO: (low) After a user-invoked dispose do not show again for the same word.
		super.dispose();
	} //}}}

	////////////////////////////////////////////////////////////////////
	//			GETTERS & SETTERS
    ////////////////////////////////////////////////////////////////////

	/** Set the prefix being completed. */
    public void setWord(String word) {
        this.word = word;
    }

    /** Get the prefix being completed. */
    public String getWord() {
        return word;
    }

	////////////////////////////////////////////////////////////////////
	//			FIELDS
    ////////////////////////////////////////////////////////////////////
	//{{{ Instance variables
	/**
	 * The View the user is using to edit the buffer for which we're displaying completions.
	 * Should equal to jEdit.getActiveView()?
	 */
	private View view;				// May change when editing the same buffer in another view

	private String word;	/** The prefix to complete*/

	private final PreferencesManager prefManager = PreferencesManager.getPreferencesManager();
	//}}}

	////////////////////////////////////////////////////////////////////
	//			GUI EVENTS HANDLING METHODS
    ////////////////////////////////////////////////////////////////////

	//{{{ keyPressed() method
	/** Keys handling while the popup is visible.
	 * Displayable key are forwarded to the underlying textArea,
	 * control keys (Esc, F2 ...) are handeled w.r.t. PreferencesManager
	 * and unhandled keys dispose the popup window.
	 *
	 * ? This method is only invoked for special keys, not for those that
	 * can be typed such as a letter ?
	 */
	public void keyPressed(KeyEvent evt)
	{
		if ( prefManager.isAcceptKey(evt) )
		{
			doSelectedCompletion();
			evt.consume();
		}
		else if ( prefManager.isDisposeKey(evt) )
		{
			dispose();
			evt.consume();
		}
		else if ( prefManager.isSelectionUpKey(evt) )
		{
			moveSelection( evt, UP );
		}
		else if ( prefManager.isSelectionDownKey(evt) )
		{
			moveSelection( evt, DOWN );
		}
		else
		{
//				Log.log(Log.DEBUG, TextAutocompletePlugin.class, "CompletionPopup.keyPressed: evt=" + evt + ",modif.:" + evt.getModifiers()
//						+ ", prefMng.sel.modif:" + prefManager.getSelectionByNumberModifier());
			// Handle modifier keys: consume or forward to the view
			if ( prefManager.isSelectionByNumberEnabled() &&
					evt.getModifiers() == prefManager.getSelectionByNumberModifier())
			{
				// WORKAROUND FOR CTRL+NUMBER
				// Ctrl+1 initiates 2*keyPressed (1st for Ctrl, 2nd for Ctrl+1) and no keyTyped
				if ( prefManager.getSelectionByNumberModifier() == InputEvent.CTRL_MASK
						&& Character.isDigit(evt.getKeyChar()) )
				{
					selectCompletionByNumber(evt.getKeyChar());
				}
				evt.consume();
			}
		} // if-else-... type of the key

	} // keyTyped }}}

	/////////////////////////////////////////////////////////////////////////////////
	/** A constant used by moveSelection - down arrow pressed. */
	final protected int DOWN 	= 0;	// down arrow; towards higher indices
	/** A constant used by moveSelection - up arrow pressed. */
	final protected int UP 		= 1;	// towars lower indices

	// moveSelection {{{
	/** Move selection in the popup completition list up or down. */
	protected void
	moveSelection( KeyEvent evt, int direction )
	{
		int selected = getSelectedIndex();
//		    Log.log( Log.DEBUG, this, "ENTRY: moveSelection, dir " + ((direction != DOWN)? "UP" : "D" +
//		    		", selected: " + selected ));

		selected += (direction == DOWN)? +1 : -1;

		// Wrap around when end/beginning reached
		if ( selected == -1 ) {
			selected = getCandidates().getSize() - 1;
		} else if ( selected == ( getCandidates().getSize() ) ) {
			selected = 0;
		}

		setSelectedIndex( selected );

		evt.consume();
	} // moveSelection }}}

	/////////////////////////////////////////////////////////////////////////////////
	//{{{ keyTyped() method
	/**
	 * Handle key typed while intercepting any user input when popup visible.
	 * Mostly we do nothing because that will fire a buffer changed event
	 * that will invoke AutoComplete.update that will handle the event and do
	 * something with the pop-up window.
	 * Only if it's a digit we insert the completion with the given number
	 * if there's such on the list.
	 *
	 * @see AutoComplete#update(java.util.Observable, Object)
	 * @see WordTypedListener
	 * */
	public void keyTyped(KeyEvent evt)
	{
		char ch = evt.getKeyChar();
//			Log.log(Log.DEBUG, TextAutocompletePlugin.class, "CompletionPopup.keyTyped: evt=" + evt + ",modif.:" + evt.getModifiers());

		//
		// DIGITS: insert the completion number $digit
		//
		// This functionality may be disabled or it may require a particular
		// modifier key to be pressed together with the number key.
		if(prefManager.isSelectionByNumberEnabled()
				&& Character.isDigit(ch)
				&& prefManager.getSelectionByNumberModifier() == evt.getModifiers() )
		{
			if (selectCompletionByNumber(ch))
			{ evt.consume(); }
		} // if a digit pressed
	} // keyTyped }}}

	/**
	 * Insert the numChar-th completion into the buffer if there is such a completion.
	 * @param numChar A character representing a number [0-9].
	 * @return True if a completion has been selected and inserted.
	 */
	private boolean selectCompletionByNumber(char numChar)
	{
		int index = numChar - '0';
		if(index == 0) // numbering starts from 1; do wrap around
			index = 9;
		else
			index--;
		if(index < getCandidates().getSize())
		{
			setSelectedIndex(index);
			doSelectedCompletion(); // + dispose
			return true;
		}
		else {} // fall through; thee're only digits 0-9
		return false;
	}

	/** Return the text area of the completed buffer's view. */
	private JEditTextArea getTextArea() {
		return view.getTextArea();
	}

	//{{{ Words class
	private class Words implements Candidates
	{
		private final DefaultListCellRenderer renderer;
		private final Completion[] completions;

		public Words(Completion[] completions)
		{
			this.renderer = new DefaultListCellRenderer();
			this.completions = completions;
		}

		public int getSize()
		{
			return completions.length;
		}

		public boolean isValid()
		{
			return true;
		}

		public void complete(int index)
		{
			String insertion = completions[index].toString().substring(word.length());
			getTextArea().setSelectedText(insertion);
		}
	
		public Component getCellRenderer(JList list, int index,
			boolean isSelected, boolean cellHasFocus)
		{
			renderer.getListCellRendererComponent(list,null,index,
				isSelected,cellHasFocus);

			Completion comp = completions[index];

			String text = comp.getWord();

			if(index < 9)
				text = (index + 1) + ": " + text;
			else if(index == 9)
				text = "0: " + text;

			renderer.setText(text);
			return renderer;
		}

		public String getDescription(int index)
		{
			return null;
		}
	} //}}}

} // class CompletionPopup
