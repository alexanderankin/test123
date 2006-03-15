package net.jakubholy.jedit.autocomplete;

//import java.util.Vector;

import org.gjt.sp.jedit.EditPlugin;

// TODO (low): Options - also options local to a buffer? for an edit mode? possible to save?
// TODO: options: + button to check beanshell code 
// TODO (high): Write the documentation
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
