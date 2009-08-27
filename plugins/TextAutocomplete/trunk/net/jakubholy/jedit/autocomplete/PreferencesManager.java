/*
 * PreferencesManager.java
 * Created on 6.2.2005 by aja
 * $id
 */
package net.jakubholy.jedit.autocomplete;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import net.jakubholy.jedit.autocomplete.WordTypedListener.Filter;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.bsh.BshMethod;
import org.gjt.sp.jedit.bsh.NameSpace;
import org.gjt.sp.jedit.bsh.UtilEvalError;
import org.gjt.sp.jedit.gui.BeanShellErrorDialog;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;

/**
 * Makes user settings available for other classes so that they don't
 * need to deal with properties directly, calling PreferencesManager's
 * methods instead.
 * <p>
 * An instance of this class keeps values of the various settings and makes them
 * availabe through getter methods. The settings are also stored as jEdit
 * properties (see {@link jEdit#setProperty}). To change a setting, modify the
 * appropriate jEdit property and call aPreferencesManager.{@link #optionsChanged()}.
 * </p><p>
 * Most settings can be changed via the menu Plugins &gt; Plugins Options... &gt;
 * TextAutocomplete. See {@link TextAutocompletePane}.
 * </p>
 *
 * @author Jakub Holy
 */
public class PreferencesManager {

	/** Do not log anything but errors and warnings. */
	static final int LOG_NONE = 0;

	/** Log only AT_END events */
	static final int LOG_WORD = 1;

	/** Log all events. */
	static final int LOG_ALL = 2;

	/** Single instance */
    private static PreferencesManager thePreferencesManager = null;

    /** Precompiled BeanShell codes for some of the methods. */
    BshMethod[] cachedCodes = new BshMethod[]{null, null};
    /** Index into the array cachedCodes. */
    int IS_WORD					= 0;
    /** Index into the array cachedCodes. */
    int IS_WORD_TO_REMEMBER		= 1;
    //** Index into the array cachedCodes. */
    //int IS_WORD_ELEMENT			= 2;

    /** NameSpace used to pass parameters to the BeanShell snippets. */
    NameSpace bsNameSpace		= new NameSpace(BeanShell.getNameSpace(), "PreferencesManager");

    /** List of keys used to accept the selected completion. */
    List<Integer> acceptKeys 			= null;
    List<Integer> disposeKeys 			= null;
    List<Integer> selectionUpKeys 		= null;
    List<Integer> selectionDownKeys 	= null;

	/**
	 * Holds the filename filter pattern.
	 */
	Pattern filenameFilter = null;

	/** Path to the directory holding default word lists for new buffers. */
	private String defaultWordListsDir = null;

	/** Characters that do not end word such as '@' or '_' in PHP. */
	private String noWordSeparators = "";

    /////////////////////////////////////////////////////////////////////////////////////
    // getPreferencesManager {{{
    /** Create a new Pref.Manager or return the existing one if exists. */
    synchronized public static PreferencesManager
    getPreferencesManager()
    {
        if ( thePreferencesManager == null )
        { thePreferencesManager = new PreferencesManager(); }
        return thePreferencesManager;
    } // }}} getPreferencesManager

    // getPreferencesManager {{{
    /* * Destroy (forget) the existing Pref.Manager. if there is one. * /
    synchronized public static void
    unsetPreferencesManager()
    { thePreferencesManager = null; } // }}} getPreferencesManager
    */

    /** Create the PrefMngr and read saved options. Private; other use getPreferencesManager(). */
    private PreferencesManager()
    { optionsChanged();	}

	/////////////////////////////////////////////////////////////////////////////////////
    // getPreferencesManager {{{
    /** Called to notify the PreferencesManager that preferences have changed. */
    synchronized public void
    optionsChanged()
    {
    	// Reset cached blocks of code
        for (int i = 0; i < this.cachedCodes.length; i++)
        { cachedCodes[i] = null; }

        // Precompile and cache the codes; precompileCode notifies the user of any error
        String code = null;
        code = jEdit.getProperty(TextAutocompletePlugin.PROPS_PREFIX + "isWord-code");
        precompileCode(code, "isWord", IS_WORD);
        code = jEdit.getProperty(TextAutocompletePlugin.PROPS_PREFIX + "isWordToRemember-code");
        precompileCode( code, "isWordToRemember", IS_WORD_TO_REMEMBER);

        // (Re)set key codes
        acceptKeys 			= propertyToKeyCodes(TextAutocompletePlugin.PROPS_PREFIX + "acceptKey");
        disposeKeys 		= propertyToKeyCodes(TextAutocompletePlugin.PROPS_PREFIX + "disposeKey");
        selectionUpKeys 	= propertyToKeyCodes(TextAutocompletePlugin.PROPS_PREFIX + "selectionUpKey");
        selectionDownKeys	= propertyToKeyCodes(TextAutocompletePlugin.PROPS_PREFIX + "selectionDownKey");

        // Update the filename filter pattern
        String filter = getFilenameFilter();
        if (filter.length() > 0)
        	filenameFilter = Pattern.compile(StandardUtilities.globToRE(filter));
        else
        	filenameFilter = null;

    } // }}} getPreferencesManager


    /////////////////////////////////////////////////////////////////////////////////////
    //							isWordFilter
    /////////////////////////////////////////////////////////////////////////////////////
    /** Used to check whether an insertion appended to a word is still a word. */
    protected final Filter isWordFilter = new Filter() {
		/** Return true if the insertion appended to the word is still a word (to remember/to complete...). */
    	public boolean
    	accept(StringBuffer word, char insertion)
    	{
    		if(word == null)
    		{ word = new StringBuffer();}

    		final int cachedCodeIndex = IS_WORD;
        	if(cachedCodes[cachedCodeIndex] != null)
	    	{
	    		try
	    		{
	    			bsNameSpace.setTypedVariable("prefix", String.class, word.toString(), null);
	    			bsNameSpace.setTypedVariable("insertion", Character.class, new Character(insertion), null);
	    			bsNameSpace.setTypedVariable("noWordSeparators", String.class, noWordSeparators, null);
	    		}
				catch (UtilEvalError e) { throw new RuntimeException(e); }

	    		boolean isWord = executeCachedCode("isWord", cachedCodeIndex, bsNameSpace);
	    		bsNameSpace.unsetVariable("prefix");	// clean up for better robustness
	    		bsNameSpace.unsetVariable("insertion");
	    		return isWord;
	    	}
	    	else
	    	{ return defaultIsWordCheck(insertion); }
		}
	};

	/**
	 * The default test to determine whether a character is a word separator
	 * or not used when no code snippet for that defined by the user in preferences.
	 * It takes into account the current value of {@link #noWordSeparators} property.
	 * @param insertion (required) a new character typed
	 * @return true if the character is not a word separator
	 *
	 * @see #isWordFilter
	 */
	private boolean
	defaultIsWordCheck(final char insertion)
	{
		return Character.isLetterOrDigit( insertion )
			|| this.noWordSeparators.indexOf( insertion ) >= 0;
	}

    /////////////////////////////////////////////////////////////////////////////////////
    //							METHODS
    /////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////
    //	{{{ isWordElement
    /* *
     * Return true if the character is a letter or digit or is not word separator (is e.g. '_')
     * Modify this function to change what is considered to be a part of a word.
     * /
    public boolean
    isWordElement( char ch )
    {
    	String code = jEdit.getProperty(TextAutocompletePlugin.PROPS_PREFIX + "isWordElement-code");
    	if(code != null)
    	{
    		try
    		{ bsNameSpace.setTypedVariable("character", Character.class, new Character(ch), null); }
			catch (UtilEvalError e) { throw new RuntimeException(e); }
    		return evaluateCode(code, "isWordElement", IS_WORD_ELEMENT, bsNameSpace);
    	}
    	else
    	{
    		String lNoWordSep = "_";
    		return Character.isLetterOrDigit(ch) || ( lNoWordSep.indexOf(ch) != -1 );
    	}
    } // isWordElement }}}
    */

    /**
     * Checker that determines what is a word separator and what is not.
     * The filter's method boolean accept(StringBuffer word, char insertion)
     * is used for that.
     */
	public Filter getIsWordFilter() {
    	return this.isWordFilter;
    }

    //  {{{ minPrefixLength
    /**
     * Return the minimal length the word being typed must have for the list of completions
     * to be displayed. At least minPrefixLength characters must be typed before the pop-up
     * window appears.
     * It can be set to any value >= 1.
     */
    public int
    minPrefixLength()
    { return jEdit.getIntegerProperty(TextAutocompletePlugin.PROPS_PREFIX + "minPrefixLength", 2); }
    // minPrefixLength }}}
    /////////////////////////////////////////////////////////////////////////////////////
    //						DETERMINE KEY TYPE METHODS
    //				what keys have special meaning for the popup win.
    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ isAcceptKey
    /** Return true for keys used to accept a selected completition. */
    public boolean
    isAcceptKey( KeyEvent evt )
    {
    	if(acceptKeys != null)
    	{ return acceptKeys.contains(new Integer( evt.getKeyCode() )); }
    	else
        { return (evt.getKeyCode() == KeyEvent.VK_TAB) || (evt.getKeyCode() == KeyEvent.VK_ENTER); }
    } // isAcceptKey }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ isDisposeKey
    /** Return true for keys used to dispose (hide) the popup window. */
    public boolean
    isDisposeKey( KeyEvent evt )
    {
    	if(disposeKeys != null)
    	{ return disposeKeys.contains(new Integer( evt.getKeyCode() )); }
    	else
        { return evt.getKeyCode() == KeyEvent.VK_ESCAPE; }
    } // isDisposeKey }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ isSelectionUpKey
    /** Return true for keys used to move up in the popup list of completitions. */
    public boolean
    isSelectionUpKey( KeyEvent evt )
    {
    	if(selectionUpKeys != null)
    	{ return selectionUpKeys.contains(new Integer( evt.getKeyCode() )); }
    	else
        { return evt.getKeyCode() == KeyEvent.VK_UP; }

    } // isSelectionUpKey }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ isSelectionDownKey
    /** Return true for keys used to move down in the popup list of completitions. */
    public boolean
    isSelectionDownKey( KeyEvent evt )
    {
		if(selectionDownKeys != null)
		{ return selectionDownKeys.contains(new Integer( evt.getKeyCode() )); }
		else
		{ return evt.getKeyCode() == KeyEvent.VK_DOWN; }
    } // isSelectionDownKey }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //						COMPLETION WORDS LIST
    //
    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ isWordToRemember
    /** Return true if the word shall be rememberd i.e. added to the completion word list. */
    public boolean
    isWordToRemember( String word )
    {
        boolean doRemember = true;
        final int cachedCodeIndex = IS_WORD_TO_REMEMBER;
    	if(cachedCodes[cachedCodeIndex] != null)
    	{
    		try
    		{ bsNameSpace.setTypedVariable("word", String.class, word.toString(), null); }
			catch (UtilEvalError e) { throw new RuntimeException(e); }

			doRemember = executeCachedCode("isWordToRemember", cachedCodeIndex, bsNameSpace);
			bsNameSpace.unsetVariable("word");
    	}

    	// Check length if the word has been accepted so far
        if(doRemember)
    	{ return word.length() >= jEdit.getIntegerProperty(TextAutocompletePlugin.PROPS_PREFIX + "minWordToRememberLength", 5); }
        else
        { return false; }
    } // isWordToRemember }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ getMaxCountOfWords
    /**
     * The maximal number of words that we do remeber for a given buffer.
     * Additional words that would otherwise be remembered are ignored.
     */
    public int
    getMaxCountOfWords()
    { return jEdit.getIntegerProperty(TextAutocompletePlugin.PROPS_PREFIX + "maxCountOfWords", 1000); }
    // getMaxCountOfWords }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ isStartForBuffers
    /**
     * True if the autocompletion should be started automatically for
     * new buffers.
     */
    public boolean
    isStartForBuffers()
    { return jEdit.getBooleanProperty(TextAutocompletePlugin.PROPS_PREFIX + "isStartForBuffers", false); }
    // isStartForBuffers }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ isLoadModeKeywords
    /**
     * When loading keywords associated with the buffer's edit mode, shall
     * we load only those from the default mode? E.g. for PHP there is
     * over 10 edit modes (or perhaps more exactly sub-modes).
     * This may save memory while still providing the most important keywords for completion.
     * @see #isLoadModeKeywords()
     */
    public boolean
    isLoadMainModeOnly()
    { return jEdit.getBooleanProperty(TextAutocompletePlugin.PROPS_PREFIX + "isLoadMainModeOnly", false); }
    // isLoadModeKeywords }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ isLoadModeKeywords
    /**
     * True if keywords from the buffer's edit mode shall be loaded upon start.
     */
    public boolean
    isLoadModeKeywords()
    { return jEdit.getBooleanProperty(TextAutocompletePlugin.PROPS_PREFIX + "isLoadModeKeywords", false); }
    // isLoadModeKeywords }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ getFilenameFilterPattern
    /**
     * Returns the filename filter pattern.
     * completion. Use 'isIncludeFilter' or 'isExcludeFilter' to find whether
     * filenames matching the filter should be included or excluded.
     */
    public Pattern
    getFilenameFilterPattern()
    {
    	return filenameFilter;
    }
    // getFilenameFilterPattern }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ getFilenameFilter
    /**
     * Returns the filename filter registered for inclusion or exclusion from auto
     * completion. Use 'isIncludeFilter' or 'isExcludeFilter' to find whether
     * filenames matching the filter should be included or excluded.
     */
    public String
    getFilenameFilter()
    {
    	return jEdit.getProperty(TextAutocompletePlugin.PROPS_PREFIX + "filenameFilter", "");
    }
    // getFilenameFilter }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ isInclusionFilter
    /**
     * Returns whether filenames matching the filename filter should be included.
     */
    public boolean
    isInclusionFilter()
    {
    	return jEdit.getBooleanProperty(TextAutocompletePlugin.PROPS_PREFIX + "isInclusionFilter", false);
    }
    // isInclusionFilter }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ isExclusionFilter
    /**
     * Returns whether filenames matching the filename filter should be excluded.
     */
    public boolean
    isExclusionFilter()
    {
    	return !isInclusionFilter();
    }
    // isExclusionFilter }}}

    /////////////////////////////////////////////////////////////////////////////////////
    //	{{{ getLogLevel
    /**
     * True if the autocompletion should be started automatically for
     * new buffers.
     * TODO: Make it possible to start only for buffers matching some condition
     * (edit mode, file name extension, ...)
     */
    public int
    getLogLevel()
    { return jEdit.getIntegerProperty(TextAutocompletePlugin.PROPS_PREFIX + "logLevel", LOG_NONE); }
    // getLogLevel }}}


    /////////////////////////////////////////////////////////////////////////////////////


    /**
     * Read a list of keys from the given property and return a list of their key codes.
     * @param propertyName Name of the plugin's property that contains a list of names of
     * KeyEvent's constants separated by a space or a comma. Ex.: "VK_ESCAPE VK_ENTER".
     * @return List of Integers holding key codes of the keys or null if the property isn't
     * set or contains no valid keys.
     * @see KeyEvent
     */
	private java.util.ArrayList<Integer>
	propertyToKeyCodes(String propertyName)
	{
		java.util.ArrayList<Integer> keyCodes = null;
    	String acceptkeysProp = jEdit.getProperty(propertyName);
    	if(acceptkeysProp != null)
    	{
    		StringTokenizer token = new StringTokenizer( acceptkeysProp, ", ");
    		keyCodes = new java.util.ArrayList<Integer>( token.countTokens() );
    		Field key = null;
    		while(token.hasMoreTokens())
    		{
    			// Get the Field corresponding to the given constant name
    			String tokenValue = token.nextToken();
    			try
    			{ key = KeyEvent.class.getDeclaredField(tokenValue); } // does a constant of the given name exist?
				catch (SecurityException e)
				{ throw new RuntimeException(e); }
				catch (NoSuchFieldException e)
				{
					GUIUtilities.error(null, TextAutocompletePlugin.PROPS_PREFIX + "errorMessage",
							new Object[]{ "Invalid key '"+tokenValue+"' in the property " + propertyName });
					continue;	// go to the next token (key)
				}

				// Store the Field's value for later use
				try
				{ keyCodes.add(new Integer( key.getInt(KeyEvent.class) )); }
				catch (IllegalArgumentException e)
				{} // this cannot occur
				catch (IllegalAccessException e)
				{} // this cannot occur

    		} // while more keys

    		// If no (valid) keys => use the default
    		if(keyCodes.size() == 0)
    		{ keyCodes = null; }
    	}
		return keyCodes;
	}

	/**
	 * Do we allow to select an entry in the completion popup by typing its
	 * number?
	 * <p>
	 * Returnes true by default. The corrseponding property is
	 * {@link TextAutocompletePlugin#PROPS_PREFIX} + "isSelectionByNumberEnabled".
	 * </P>
	 *
	 * @return True if a number key selects an entry in the completion popup win.,
	 * false if the number should be just inserted into the buffer.
	 */
	public boolean isSelectionByNumberEnabled()
	{ return jEdit.getBooleanProperty(TextAutocompletePlugin.PROPS_PREFIX + "isSelectionByNumberEnabled", true); }

	/**
	 * Returns the mask for the special key that must be pressed together with a number N
	 * to select the Nth completion in the popup. Returns 0 if no modifier key
	 * is required and the number on itself selects the entry.
	 * <p>The corresponding property is {@link TextAutocompletePlugin#PROPS_PREFIX} + 'selectionByNumberModifierMask'.</p>
	 * <p>
	 * Valid values: InputEvent.CTRL_MASK, InputEvent.ALT_MASK, InputEvent.ALT_GRAPH_MASK.
	 * </p>
	 *
	 * @return 0 = no modifier [default] or a mask defined in {@link InputEvent} such as
	 * {@link InputEvent#CTRL_DOWN_MASK}.
	 */
	public int getSelectionByNumberModifier() {
		return jEdit.getIntegerProperty(
				TextAutocompletePlugin.PROPS_PREFIX + "selectionByNumberModifierMask",
				0);
	}
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Set the String jEdit property named {@link TextAutocompletePlugin#PROPS_PREFIX} + 'name'
	 * to the value 'value'.
	 */
	public void setProperty(String name, String value)
	{
		jEdit.setProperty(TextAutocompletePlugin.PROPS_PREFIX + name, value);
		optionsChanged();
	}

	/**
	 * Set the boolean jEdit property named {@link TextAutocompletePlugin#PROPS_PREFIX} + 'name'
	 * to the value 'value'.
	 */
	public void setBooleanProperty(String name, boolean value)
	{
		jEdit.setBooleanProperty(TextAutocompletePlugin.PROPS_PREFIX + name, value);
		optionsChanged();
	}

	/**
	 * Set the int jEdit property named {@link TextAutocompletePlugin#PROPS_PREFIX} + 'name'
	 * to the value 'value'.
	 */
	public void setIntegerProperty(String name, int value)
	{
		jEdit.setIntegerProperty(TextAutocompletePlugin.PROPS_PREFIX + name, value);
		optionsChanged();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Evaluate the BeanShell code and return its return value. Show an error dialog if
	 * its return value doesn't evaluate to a boolean and clear the cached code to prevent
	 * repetitions of this error.
	 * Warning: cachedCodes[cachedCodeIndex] must not be null.
	 * @param codeName A name identifying the code such as "isWordElement" - used in an error message.
	 * @param cachedCodeIndex One of IS_WORD, IS_WORD_TO_REMEMBER, ...
	 * @param nameSpace NameSpace containing arguments for the code to be invoked.
	 * @return Return value of the code
	 */
	private boolean
	executeCachedCode(String codeName, int cachedCodeIndex, NameSpace nameSpace)
	{

		try
		{
			Object retVal = BeanShell.runCachedBlock(cachedCodes[cachedCodeIndex], null, nameSpace);

			if (retVal instanceof Boolean)
			{ return ((Boolean) retVal).booleanValue(); }
			else
			{
				cachedCodes[cachedCodeIndex] = null;	// reset the cached code to prevent repetitions of the error
				String msg = "PreferencesManager: The beanshell code for "+codeName+" doesn't "
				+ "return a boolean but " + ((retVal == null)? null : retVal.getClass());
				Log.log(Log.ERROR, TextAutocompletePlugin.class, msg);
				throw new IllegalArgumentException(msg);
			}

		}
		catch(Throwable e)
		{
			cachedCodes[cachedCodeIndex] = null;	// reset the cached code to prevent repetitions of the error
			Log.log(Log.ERROR, TextAutocompletePlugin.class, e);
			new BeanShellErrorDialog(null,e);
			return false;
		}
	}

	/**
	 * Try to precompile and cache the BeanShell code. The result is stored into {@link #cachedCodes}.
	 * If the compilation fails, a graphical notification is presented to the user.
	 * @param code The code to compile - if null or empty, nothing is done
	 * @param codeName Calling method and code name such as "isWordElement". Must be valid java identifier.
	 * @param cachedCodeIndex One of IS_WORD, IS_WORD_ELEMENT, ...
	 * @return True if the code has been compiled
	 */
	private boolean
	precompileCode(String code, String codeName, int cachedCodeIndex)
	{
		//	Make sure that tha code ends with ';' == that it is a valid java statement
		if(code == null || code.trim().length() == 0)
		{ return false; }

		code = sanitizeCode(code);

		try
		{
			if (cachedCodes[cachedCodeIndex] == null)
			{
				cachedCodes[cachedCodeIndex] = BeanShell.cacheBlock(
						"textautocomplete_" + codeName, code, true);
			}
		}
		catch (Exception e)
		{
			String msg = "PreferencesManager: Failed to precompile BeanShell code for '" + codeName + "';";
			Log.log(Log.ERROR, TextAutocompletePlugin.class, msg + " cause: " + e);
			GUIUtilities.error(null, TextAutocompletePlugin.PROPS_PREFIX + "errorMessage",
					new Object[]{ msg + "\nCause:" + e });
			return false;
		}

		return true;
	}

	/** Make sure that the code ends with ';' i.e. that it is a valid java statement */
	public static String
	sanitizeCode(final String code)
	{
		String sanitizedCode = code.trim();
		if( !sanitizedCode.endsWith(";") && !sanitizedCode.endsWith("}") )
		{ sanitizedCode += ";"; }
		return sanitizedCode;
	}

    /////////////////////////////////////////////////////////////////////////////////////
    /**
     * Return a list of default words for a newly opened buffer
     * of the given name if there is any. The format of the file
     * pointed to by the URL is the same as used by
     * {@link AutoComplete#importWordList()}.
     * <p>
     * This method shall be called when AutoCompletion is started for a
     * new buffer and perhaps also when the buffer name changes (e.g.
     * after the change from 'Untitled-1' to 'SourceCode.java' we will
     * want to load default words for Java files).
     * <p>
     *
     * @param bufferName (required) a name of the buffer that AutoComplete has
     * just been started for; examples: 'SourceCode.java', 'Untitled-1'.
     * It's used to find a suitable word list, usually based on the file
     * name extension. To get the default, not buffer-specific word list you can pass
     * an empty string ("") as the buffer name.
     * @param onlyIfExists (required) If false then the word list file path is
     * returned even if the file doesn't currently exist othewrise null is
     * returned if the file doesn't exist
     * @return either URL of a list of default words as in
     * {@link AutoComplete#importWordList()} or null if there're no defaults.
     * Beware that the URL may point to a non-existing file.
     */
	public URL getDefaultWordListForBuffer(String bufferName, boolean onlyIfExists) {
		if (bufferName == null) {
			throw new IllegalArgumentException("The param 'bufferName' may not be null.");
		}

		URL wordListUrl = null;
		final String settingsDir = getDefaultWordListsDir();
    	if (settingsDir != null)
    	{
    		String wordListPath = MiscUtilities.constructPath(
    				settingsDir, "autocompleteDefaultWordList");

    		try {

    			// Buffer file name extension
    			final String bufferExtension = MiscUtilities.getFileExtension(bufferName).toLowerCase();
    			if (bufferExtension.length() > 0)
    			{
    				final String extSpecificWordListPath = wordListPath + bufferExtension;
    				final File extSpecificWordListFile = new File(extSpecificWordListPath);
    				if (extSpecificWordListFile.canRead())
    				{
    					wordListPath = extSpecificWordListPath;
    				}
    			}
    			File wordListFile = new File(wordListPath);
    			if (!onlyIfExists || wordListFile.canRead())
    				wordListUrl = wordListFile.toURI().toURL();

			} catch (MalformedURLException e) {
				Log.log(Log.ERROR, TextAutocompletePlugin.class, "getDefaultWordListForBuffer: Failed " +
						"to construct a URL from the default word list path '" +
						wordListPath + "'. Reason: " + e);
			}

    	}
    	else
    	{
    		Log.log(Log.DEBUG, TextAutocompletePlugin.class
    				, "getDefaultWordListForBuffer: No word list read because jEdit settings dir is unset." +
    				"(Perhaps running with -nosetting or as a -server.)");
    	} // if-else settings dir set

    	Log.log(Log.DEBUG, TextAutocompletePlugin.class, "getDefaultWordListForBuffer('" +
    			bufferName + "'): returning " + wordListUrl);

    	return wordListUrl;
    } /* getDefaultWordListForBuffer */

	/**
	 * The directory where default word lists are stored.
	 */
	private String getDefaultWordListsDir() {
		if (defaultWordListsDir == null)
		{
			defaultWordListsDir = jEdit.getSettingsDirectory();
		}
		return defaultWordListsDir;
	}

	/**
	 * Set path to the directory where default word lists are stored.
	 * This method is intended to override the default location for
	 * JUnit testing purposes.
	 * @param defaultWordListsDir E.g. /tmp/myTestDir
	 */
	void setDefaultWordListsDir(String defaultWordListsDir) {
		this.defaultWordListsDir = defaultWordListsDir;
	}

	/** Characters that do not end word such as '@' or '_' in PHP. */
	public String getNoWordSeparators() {
		return noWordSeparators;
	}

	/**
	 * Characters that do not end word such as '@' or '_' in PHP.
	 * @param noWordSeparators (required) ex.: "_@-"
	 */
	public void setNoWordSeparators(String noWordSeparators) {
		if (noWordSeparators == null) {
			throw new IllegalArgumentException("The argument noWordSeparators may not be null.");
		}
		this.noWordSeparators = noWordSeparators;
	}

    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
} // class
