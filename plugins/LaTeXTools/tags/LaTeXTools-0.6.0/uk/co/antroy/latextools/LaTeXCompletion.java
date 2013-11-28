/*:folding=indent:
* LaTeXCompletion.java - Code completion.
* Copyright (C) 2003 Anthony Roy
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
package uk.co.antroy.latextools;

import java.util.Iterator;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import sidekick.SideKickCompletion;

/**
 * Represents a list of completion for a given prefix.
 * It's passed to the {@link sidekick.SideKickCompletionPopup}  
 * to present to the user to choose from.
 * @author Jakub Holy
 */


public class LaTeXCompletion
    extends SideKickCompletion {
	
	/** A list of LaTeX commands - possible completions. */
	private static java.util.TreeSet latexCommands = null; 

    //~ Constructors ..........................................................

    /**
     * @inheritDoc
     * 
     * Construct a list of possible completions for the given
     * prefix. More exactly, we complete (La)TeX commands.
     * 
     * Notice, that by default SideKick replaces the prefix by 
     * the completion.
     * 
	 * @param view The view in which we do complete
	 * @param prefix The word (prefix) to complete - we expect 
	 * that the leading '\' of latex commands has been removed. 
	 */
	public LaTeXCompletion(View view, String prefix) {
		super(view, prefix);
		Log.log(Log.DEBUG, LaTeXPlugin.class, "LaTeXCompletion: completing in "
				+ view.getBuffer().getName() + " for the prefix '\\" + prefix + "'"); // FIXME: comment out
		// CONTEXT - SENSITIVE COMLETION:
		// See SideKickParsedData.getAssetAtOffset(int) and LaTeXAsset and LaTeXParser.data
		// Possible problems: If the user selects other filter (navigation list/data) than All
		// than we will only detect the parent element included in the navigation list.
		// So if it doesn't match e.g. enumerations, we won't find we're inside one.
		// But perhaps we could always search for all elements and then only pass the
		// required subset to the structure browser?
		// What about using TeXlipse.sf.net's LaTeX parser? ((la)tex plugin for eclipse)
		
		// Construct the list of available completions
		// TODO: What of too many completions? typing '\' -> 100s of completions=commands
		// TODO: Don't offer latex commands in TeX mode - only tex ones
		// TODO: Templates - as in Eclipse (items hold the name, || strukt. templates hold the text itself)
		prepareMatchingCompletions( prefix );
	}
	
	// TODO: override insert to insert cmd{|} for completions ending with {; | == cursor

	/** A list of LaTeX commands - possible completions. */
	private static synchronized java.util.TreeSet getLatexCommands() {
		if(latexCommands == null)
		{
			latexCommands = new java.util.TreeSet();
			// Add the comands
			// TODO: load from a file
			latexCommands.add("begin{"); // TODO: add more latex commands
			latexCommands.add("chapter{");
			latexCommands.add("chapter*{");
			latexCommands.add("end{");
			latexCommands.add("part{");
			latexCommands.add("part*{");
			latexCommands.add("section{");
			latexCommands.add("section*{");
			latexCommands.add("subsubsection{");
			latexCommands.add("subsection{");
		}
		return latexCommands;
	}
	
	/**
	 * Find and set completions possible for the given prefix.
	 * @param prefix
	 */
	private void prepareMatchingCompletions(String prefix) {

		// Find the first matching completion
		// 
		// tailSet(prefix) returns all words >= prefix 
		Iterator iter = getLatexCommands().tailSet(prefix).iterator();
		String completion = null;
		// Find all words starting with the given prefix
		while (iter.hasNext()) {
			completion = (String) iter.next();

			if (completion.startsWith(prefix)) {
				items.add(completion);
			} else {
				return; // finish - no more words with the prefix
			}
		} // while more completions with the same prefix
	}

    //~ Methods ...............................................................

}
