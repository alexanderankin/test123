/* 
 * TextAutocompletePlugin.java
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

//import java.util.Vector;

import org.gjt.sp.jedit.EditPlugin;

// TODO (low): Options - also options local to a buffer? for an edit mode? possible to save?
// TODO: options: + button to check beanshell code 
// TODO (high): Write the documentation
/**
 * Automatic text completion for buffers.
 * Try to complete a word typed in an associated buffer.
 * Automatically try to find any completions for the current
 * word being typed and if there're any, present them to the user 
 * in a pop-up list. A list of possible completions is 
 * constructed from words typed so far in the buffer that
 * satisfy some conditions.
 * 
 * @author Jakub Holý
 */
public class TextAutocompletePlugin extends EditPlugin {
	
	/** The prefix of (nearly) all properties used by this plugin. */
	public static final String PROPS_PREFIX = "plugin.net.jakubholy.jedit.autocomplete.TextAutocompletePlugin.";

	// {{{ stop() method
	/**
	 * Called upon plugin unload - remove all instances of all classes that may
	 * be still bound to some buffers.
	 */
	public void stop() {
		AutoComplete.destroyAllAutoCompletes();
	} //}}}

}
