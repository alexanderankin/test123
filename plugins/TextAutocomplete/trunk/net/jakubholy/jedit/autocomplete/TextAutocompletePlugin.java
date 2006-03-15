package net.jakubholy.jedit.autocomplete;

//import java.util.Vector;

import org.gjt.sp.jedit.EditPlugin;
//import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.OptionsDialog;

// TODO: modify plugin source code structure: needed parent directory plugins/ ?
// TODO GUI to display&modif. WordList of the current buffer
// TODO (low): Options - also options local to a buffer? for an edit mode? possible to save?
// TODO: options: + reset to default
// TODO: options: + button to check beanshell code 
// TODO (high): Write the documentation
public class TextAutocompletePlugin extends EditPlugin {
	
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
