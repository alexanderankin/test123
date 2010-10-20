/*
* SideKickParser.java
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright 2003 Slava Pestov
*           2005 Robert McKinnon
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

package sidekick;

//{{{ Imports
import java.util.*;

import javax.swing.JPanel;

import org.gjt.sp.jedit.*;

import errorlist.DefaultErrorSource;
import org.gjt.sp.util.Log;

//}}}

/**
 * An abstract base class for plugin-provided parser implementations.<p>
 *
 * Plugins can provide SideKick parsers by defining entries in their
 * <code>services.xml</code> files like so:
 *
 * <pre>&lt;SERVICE CLASS="sidekick.SideKickParser" NAME="<i>name</i>"&gt;
 *    new <i>MyParser<i>();
 *&lt;/SERVICE&gt;</pre>
 *
 * See <code>org.gjt.sp.jedit.ServiceManager</code> for details.<p>
 *
 * Note that each <code>SideKickParser</code> subclass has a name which is
 * used to key a property <code>sidekick.parser.<i>name</i>.label</code>.<p>
 *
 * To associate a parser with some edit modes, define properties like this:
 * <pre>mode.scheme.sidekick.parser=lisp
 *mode.lisp.sidekick.parser=lisp</pre>
 *
 * @version $Id$
 * @author Slava Pestov
 */
public abstract class SideKickParser {
	public static final String SERVICE = "sidekick.SideKickParser";

	//{{{ SideKickParser constructor
	/**
	 * The parser constructor.
	 *
	 */
	public SideKickParser( String serviceName ) {
		this.name = serviceName;
	} //}}}

	//{{{ getName() method
	/**
	 * Returns the parser's name.
	 */
	public final String getName() {
		return name;
	} //}}}

	//{{{ stop() method
	/**
	 * Stops the parse request currently in progress. It is up to the
	 * parser to implement this.
	 * @since SideKick 0.3
	 */
	public void stop() {} //}}}

	//{{{ activate() method
	/**
	 * This method is called when a buffer using this parser is selected
	 * in the specified view.
	 * @param view The view
	 * @since SideKick 0.2
	 * @deprecated Use the form taking an <code>EditPane</code> instead.
	 */
	public void activate( View view ) {} //}}}

	//{{{ deactivate() method
	/**
	 * This method is called when a buffer using this parser is no longer
	 * selected in the specified view.
	 * @param view The view
	 * @since SideKick 0.2
	 * @deprecated Use the form taking an <code>EditPane</code> instead.
	 */
	public void deactivate( View view ) {} //}}}

	//{{{ activate() method
	/**
	 * This method is called when a buffer using this parser is selected
	 * in the specified view.
	 * @param editPane The edit pane
	 * @since SideKick 0.3.1
	 */
	public void activate( EditPane editPane ) {
		activate( editPane.getView() );
		Log.log( Log.DEBUG, this, getName() + ": activated for " + editPane.getBuffer() );
	} //}}}

	//{{{ deactivate() method
	/**
	 * This method is called when a buffer using this parser is no longer
	 * selected in the specified view.
	 * @param editPane The edit pane
	 * @since SideKick 0.3.1
	 */
	public void deactivate( EditPane editPane ) {
		deactivate( editPane.getView() );
		Log.log( Log.DEBUG, this, getName() + ": deactivated" );
	} //}}}

	//{{{ parse() method
	/**
	 * Parses the given text and returns a tree model.
	 *
	 * @param buffer The buffer to parse.
	 * @param errorSource An error source to add errors to.
	 *
	 * @return A new instance of the <code>SideKickParsedData</code> class.
	 */
	public abstract SideKickParsedData parse( Buffer buffer,
			DefaultErrorSource errorSource );
	//}}}

	//{{{ supportsCompletion() method
	/**
	 * Returns if the parser supports code completion.
	 *
	 * Returns false by default.
	 */
	public boolean supportsCompletion() {
		return true;
	} //}}}

	//{{{ canHandleBackspace() method
	/**
	    * <p>
	 * Returns true if the parser can handle
	    * the backspace key being typed when
	    * the completion popup is open,
	    * else false if not.
	    * </p><p>
	    * If false, the completion popup is
	    * closed when backspace is typed.
	 * </p><p>
	    * If true, the
	    * {@link SideKickCompletion#handleKeystroke(int, char)}
	    * method must be overidden to handle receiving
	    * the backspace character, '\b', as a value
	    * for the keyChar parameter.
	    * </p><p>
	 * Returns false by default.
	    * </p>
	    * @since SideKick 0.3.4
	 */
	public boolean canHandleBackspace() {
		return false;
	} //}}}

	//{{{ canCompleteAnywhere() method
	/**
	 * Returns if completion popups should be shown after any period of
	 * inactivity. Otherwise, they are only shown if explicitly requested
	 * by the user.
	 *
	 * Returns true by default.
	 */
	public boolean canCompleteAnywhere() {
		return true;
	} //}}}

	//{{{ getInstantCompletionTriggers() method
	/**
	 * Returns a list of characters which trigger completion immediately.
	 *
	 * Returns null by default.
	 *
	 */
	public String getInstantCompletionTriggers() {
		return null;
	} //}}}

	//{{{ getParseTriggers() method
	/**
	 * Returns a list of characters which trigger a buffer re-parse.
	 *
	 * Returns null by default.
	 * @since SideKick 0.3
	 *
	 */
	public String getParseTriggers() {
		return null;
	} //}}}

	//{{{ complete() method
	/**
	 * Returns completions suitable for insertion at the specified position.
	 *
	 * Returns null by default.
	 *
	 * @param editPane The edit pane involved.
	 * @param caret The caret position.
	 */
	public SideKickCompletion complete( EditPane editPane, int caret ) {
		try {
			String[] keywords = editPane.getBuffer().getKeywordMapAtOffset(caret).getKeywords();
			if (keywords.length > 0) {
				String word = getWordAtCaret( editPane, caret );
				if (word != null && word.length() > 0) {
					List possibles = new ArrayList();
					for (int i = 0; i < keywords.length; i++) {
						String kw = keywords[i];
						if (kw.startsWith(word) && !kw.equals(word)) {
							possibles.add(keywords[i]);
						}
					}
					Collections.sort(possibles);
					return new ConcreteSideKickCompletion(editPane.getView(), word, possibles);
				}
			}
		}
		catch ( Exception e ) {
		}
		return null;
	} //}}}

	//{{{ getCompletionPopup() method
	public SideKickCompletionPopup getCompletionPopup(View view, 
		int caretPosition, SideKickCompletion complete, boolean active)
	{
		return new SideKickCompletionPopup(view, this, caretPosition,
			complete, active);
	} //}}}

	//{{{ getPanel() method
	/**
	 * Returns a parser-specific panel that will be shown in the SideKick dockable
	 * window just below the SideKick toolbar. This panel is meant to be a toolbar,
	 * but can be another UI element if needed.
	 *
	 * Returns null by default.
	 * @since SideKick 0.7.4
	 */
	public JPanel getPanel() {
		return null;
	} //}}}

	private String getWordAtCaret( EditPane editPane, int caret ) {
		if ( caret <= 0 ) {
			return "";
		}
		Buffer buffer = editPane.getBuffer();
		String text = buffer.getText(0, caret);
		Mode mode = buffer.getMode();
		String word_break_chars = ( String ) mode.getProperty( "wordBreakChars" );
		if ( word_break_chars == null ) {
			word_break_chars = "";
		}
		word_break_chars += " \n\r\t";
		int offset = 0;
		for (int i = 0; i < word_break_chars.length(); i++) {
			int maybe = text.lastIndexOf(word_break_chars.charAt(i)) + 1;
			if (maybe > offset) {
				offset = maybe;
			}
		}
		return text.substring(offset);
	}

	class ConcreteSideKickCompletion extends SideKickCompletion {
		public ConcreteSideKickCompletion(View view, String word, List possibles) {
			super(view, word, possibles);
		}
	}


	protected String name;
}
