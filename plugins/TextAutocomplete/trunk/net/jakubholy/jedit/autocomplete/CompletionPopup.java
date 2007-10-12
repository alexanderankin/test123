/*
 * CompleteWord.java - Complete word dialog - a part of jEdit
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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.KeyEventWorkaround;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
//}}}

/**
 * A pop-up window to display a list of available completions.
 *
 * When visible it intercepts all keys and either passes them on
 * or handles them if they've a special meaning for it (such
 * as dispose or insert the selected completion). What keys
 * have what meaning is detemined by a {@link PreferencesManager}.
 * It could be extended to react also to other events such as
 * focus lost.
 *
 * The window is absolutely unaware of the AutoComplete that's
 * using it and that contains most of the logic to show/dispose
 * the pop-up/update the list of completions.
 */
//
// TODO: (low) Add MouseListener to dispose the popup whenever it's clicked anywhere outside
//		the popup
// TODO: (medium) Dispose the popup whenever sb. else than the popup/the textArea gains the focus
//		- add 2 focus listeners for that;
@SuppressWarnings("serial")
public class CompletionPopup extends JWindow
{

/////////////////////////////////////////////////////////////////// GUI
    /**
     * Create a new (so far invisible) popup with its listeners.
     * @param buffer The buffer the AutoComplete is attached to; may be null but
     * must be set before the first display of this.
     */
    //{{{ CompleteWord constructor
	public CompletionPopup( View view, Buffer buffer )
	{
		super(view);

		setContentPane(new JPanel(new BorderLayout())
		{
			/* *
			 * Returns if this component can be traversed by pressing the
			 * Tab key. This returns false.
			 * /
			public boolean isManagingFocus()
			{
				return false;
			}/*/

			/**
			 * Makes the tab key work in Java 1.4.
			 */
			public boolean getFocusTraversalKeysEnabled()
			{
				return false;
			}
		});

		setView( view );

		// Set up the pop-up list
		words = new JList();
		words.addMouseListener(new MouseHandler());
		words.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		words.setCellRenderer(new Renderer());

		// stupid scrollbar policy is an attempt to work around
		// bugs people have been seeing with IBM's JDK -- 7 Sep 2000
		JScrollPane scroller = new JScrollPane(words,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		getContentPane().add(scroller, BorderLayout.CENTER);

		addKeyListener( keyHandler );
		words.addKeyListener( keyHandler );
	} //}}}

	//////////////////////////////////////////////////////////////////	display
	//	{{{ Display() method
	/** Display a popup with the given completions. */
	public void display( Point location, Completion[] completions )
	{
	    if ( ! setCompletions(completions) ) { return; }
		setLocation( location );
		GUIUtilities.requestFocus( this, words );
		setVisible(true);
		getView().setKeyEventInterceptor( keyHandler );
	} // }}}

	////////////////////////////////////////////////////////////////// setCompletions
	// {{{ setCompletions
	/** Set the list of completions shown in the pop-up window. Not null.
	 * Size of the pop-up is adjusted to fit the length of the completions.
	 * @param completions The completions to set; if empty => dispose.
	 * @return Returns true if the operation succeded (== valid completions). */
	public boolean setCompletions( Completion[] completions )
	{
		//Log.log(Log.DEBUG,this,"setComplet. called ");

	    if ( completions.length == 0 ) {
	    	dispose();
	    	return false;
	    } else {
	    	words.setListData( completions );
	    	words.setSelectedIndex(0);
		    words.setVisibleRowCount(Math.min(completions.length,8));
		    pack();
		    return true;
	    }
	} // setCompletions }}}

	////////////////////////////////////////////////////////////////// dispose
	//{{{ dispose() method
	/** Hide the popup & cease to grab the key input. */
	public void dispose()
	{
		// TODO: (low) After a user-invokde dispose do not show again for the same word.
		getView().setKeyEventInterceptor(null);
		super.dispose();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				getTextArea().requestFocus();
			}
		});
	} //}}}

	////////////////////////////////////////////////////////////////////
	/** Insert a postfix of the selected word of the list behind the prefix
	 * being completed and dispose the pop-up window. Prefix may be getword(). */
	//{{{ insertSelected() method
	protected void insertSelected( String prefix )
	{
		if ( words.getSelectedValue() == null ) {
			Toolkit.getDefaultToolkit().beep();
		} else {
			try {
				getTextArea().setSelectedText(
                    words.getSelectedValue().toString()
					.substring(prefix.length()) );
			} catch (Exception e){} // string out of bound - ignore, dispose
		}

		dispose();
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

	public View getView() {
		// DEBUG:
		if( jEdit.getActiveView() != view ) {
			String msgReset = "";
			if( jEdit.getActiveView().getBuffer() == getBuffer() )	{
				view = jEdit.getActiveView();
				msgReset = " The active view's buffer is the same as the one I was created for => " +
						"I'll use and remember the presently active view.";
			}
			Log.log(Log.WARNING, TextAutocompletePlugin.class, "CompletionPopup: the " +
					"currently active View is not the same as the remembered one!" + msgReset);
		}
		return view;
	}

	/**
	 * Set the view this pop-up is displayed for.
	 * This also sets the textArea used to modify
	 * the underlying buffer.
	 */
	public void setView(View view) {
		this.view = view;
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
	/** The textArea the user is using to edit the buffer for which we're displaying completions.
	 * Should equal to jEdit.getActiveView().getTextArea()?
	  */
	/** The buffer to which is attached the AutoComplete that created us. */
	private Buffer buffer;

	private String word;	/** The prefix to complete*/
	private final JList words;
	private final KeyHandler keyHandler = new KeyHandler();
	private final PreferencesManager prefManager = PreferencesManager.getPreferencesManager();
	//}}}

	////////////////////////////////////////////////////////////////////
	//			GUI EVENTS HANDLING CLASSES
    ////////////////////////////////////////////////////////////////////
	/** Needed for JList to add a string to it. */
	//{{{ Renderer class
	static class Renderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list,null,index,
				isSelected,cellHasFocus);

			Completion comp = (Completion)value;

			if(index < 9)
				setText((index + 1) + ": " + comp.getWord());
			else if(index == 9)
				setText("0: " + comp.getWord());
			else
				setText(comp.getWord());

			return this;
		}
	} //}}}

	/** Keys handling while the popup is visible.
	 * Displayable key are forwarded to the underlying textArea,
	 * control keys (Esc, F2 ...) are handeled w.r.t. PreferencesManager
	 * and unhandled keys dispose the popup window.
	 *
	 * ? This method is only invoked for special keys, not for those that
	 * can be typed such as a letter ?
	 */
	//{{{ KeyHandler class
	class KeyHandler extends KeyAdapter
	{
		//{{{ keyPressed() method
		public void keyPressed(KeyEvent evt)
		{
			/*
			 * EXAMPLE OF FOCUS HANDLING:
			Component fc = getFocusOwner(); // method of the popup window
			Component fcw = getView().getFocusOwner();
			String focused = fc==words?"words": ((fcw==textArea)?"word":
				(fc==CompletionPopup.this?"popup":("else:"+fc+", v:"+fcw)));
			Log.log( Log.DEBUG, this, "ENTRY: keyPressed, evt " +evt +
					"\n\tfocused: " + focused);
			*/

			if ( prefManager.isAcceptKey(evt) )
			{
				insertSelected( getWord() );
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
			else if ( evt.getKeyCode() == KeyEvent.VK_BACK_SPACE )
			{
				// Only forward the event to the textArea; the rest will be
				// handeled by the AutoComplete & its listener
				// TODO: (low) What if the user maps another key to delete a previous char?
				// We should react rather to the action identified by the name "backspace"
				// than to a particular key.
				getTextArea().backspace();
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
			    	{ selectCompletionByNumber(evt.getKeyChar()); }
			    	else
			    	{ evt.consume(); }
			    }
			    else if( evt.isActionKey()
						|| evt.isControlDown()
						|| evt.isAltDown()
						|| evt.isMetaDown() )
					{
						dispose();
						getView().processKeyEvent(evt);
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
			/*
			 * // Ignore arrows when the list has a focus
			if(getFocusOwner() == words)
				{ return; }
				*/

		    int selected = words.getSelectedIndex();
//		    Log.log( Log.DEBUG, this, "ENTRY: moveSelection, dir " + ((direction != DOWN)? "UP" : "D" +
//		    		", selected: " + selected ));

		    selected += (direction == DOWN)? +1 : -1;

		    // Wrap around when end/beginning reached
		    if ( selected == -1 ) {
				selected = words.getModel().getSize() - 1;
		    } else if ( selected == ( words.getModel().getSize() ) ) {
				selected = 0;
		    }

			words.setSelectedIndex( selected );
			words.ensureIndexIsVisible( selected );

			evt.consume();
		} // moveSelection }}}

		/////////////////////////////////////////////////////////////////////////////////
		//{{{ keyTyped() method
		/**
		 * Handle key typed while intercepting any user input when popup visible.
		 * Mostly we do nothing but forward the key to the textArea because
		 * that will fire a buffer changed event that will invoke
		 * AutoComplete.update that will handle the event and do
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
			evt = KeyEventWorkaround.processKeyEvent(evt);
			if(evt == null)
				return;
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
				{ return; }
			} // if a digit pressed

			//
			//  DISPLAYABLE CHARACTERS incl. '\n' etc.
			//
			// The character is inserted -> buffer change event fired ->
			// AutoComplete gets notified and updates the pop-up as
			// appropriate (change the completion list or dispose)
			// \t handled above by KeyEventWorkaround.processKeyEvent(evt);
			if(ch != '\b' && ch != '\t')	// not tab, backspace; '\b' - see keyPressed
			{
				getTextArea().userInput( ch );
			} // if not tab / \b
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
			if(index < words.getModel().getSize())
			{
				words.setSelectedIndex(index);
				insertSelected( getWord() ); // + dispose
				return true;
			}
			else {} // fall through; thee're only digits 0-9
			return false;
		}
	} // class KeyHandler }}}

	//{{{ MouseHandler class
	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			insertSelected( getWord() );
		}
	} //}}}

	/** Get the buffer to which is attached the AutoComplete that created us. */
	public Buffer getBuffer() {
		return buffer;
	}

	/** Set the buffer to which is attached the AutoComplete that created us. */
	public void setBuffer(Buffer buffer) {
		this.buffer = buffer;
	}

	/** Return the text area of the completed buffer's view. */
	private JEditTextArea getTextArea() {
		return getView().getTextArea();
	}


} // class CompletitionPopup