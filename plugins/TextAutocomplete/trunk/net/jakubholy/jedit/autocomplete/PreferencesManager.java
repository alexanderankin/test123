/* 
 * PreferencesManager.java
 * Created on 6.2.2005 by aja
 * $id
 */
package net.jakubholy.jedit.autocomplete;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.List;
import java.util.StringTokenizer;

import net.jakubholy.jedit.autocomplete.WordTypedListener.Filter;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.BeanShellErrorDialog;
import org.gjt.sp.util.Log;

import bsh.BshMethod;
import bsh.NameSpace;
import bsh.UtilEvalError;


/**
 * @author aja
 *
 */
public class PreferencesManager {
    
	/** Single instance */
    private static PreferencesManager thePreferencesManager = null;
    
    BshMethod[] cachedCodes = new BshMethod[]{null, null};
    int IS_WORD					= 0;
    int IS_WORD_TO_REMEMBER		= 1;
    //int IS_WORD_ELEMENT			= 2;
    NameSpace bsNameSpace		= new NameSpace(BeanShell.getNameSpace(), "PreferencesManager");
    
    /** List of keys used to accept the selected completion. */
    List acceptKeys 			= null;
    List disposeKeys 			= null;
    List selectionUpKeys 		= null;
    List selectionDownKeys 		= null;

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
    
    /** Create the PrefMngr and read saved options. Private - use getPreferencesManager(). */
    private PreferencesManager() {
    	optionsChanged();
	}

	/////////////////////////////////////////////////////////////////////////////////////
    // getPreferencesManager {{{
    /** Called to notify the PreferencesManager that preferences have changed. */
    synchronized public void 
    optionsChanged()
    {
    	// Reset cached blocks of code 
        for (int i = 0; i < this.cachedCodes.length; i++) {
        	cachedCodes[i] = null;
		}
        
        // (Re)set key codes
        acceptKeys 			= propertyToKeyCodes(TextAutocompletePlugin.PROPS_PREFIX + "acceptKey"); 
        disposeKeys 		= propertyToKeyCodes(TextAutocompletePlugin.PROPS_PREFIX + "disposeKey");
        selectionUpKeys 	= propertyToKeyCodes(TextAutocompletePlugin.PROPS_PREFIX + "selectionUpKey");
        selectionDownKeys	= propertyToKeyCodes(TextAutocompletePlugin.PROPS_PREFIX + "selectionDownKey");
    } // }}} getPreferencesManager
    

    /////////////////////////////////////////////////////////////////////////////////////
    //							isWordFilter						
    /////////////////////////////////////////////////////////////////////////////////////
    protected Filter isWordFilter = new Filter() {
		/** Return true if the insertion appended to the word is still a word (to remember/to complete...). */
    	public boolean accept(StringBuffer word, char insertion){
			String code = jEdit.getProperty(TextAutocompletePlugin.PROPS_PREFIX + "isWord-code");
	    	if(code != null)
	    	{
	    		try 
	    		{ 
	    			bsNameSpace.setTypedVariable("prefix", String.class, word.toString(), null); 
	    			bsNameSpace.setTypedVariable("insertion", Character.class, new Character(insertion), null);
	    		}
				catch (UtilEvalError e) { throw new RuntimeException(e); }
	    		boolean isWord = evaluateCode(code, "isWord", IS_WORD, bsNameSpace);
	    		bsNameSpace.unsetVariable("prefix");	// clean up for better robustness
	    		bsNameSpace.unsetVariable("insertion");
	    		return isWord;
	    	}
	    	else
	    	{ return (Character.isLetter( insertion )); }
		}
	};
	
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
    
    /** Checker that determines what is a word separator and what is not. */
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
    {
    	return jEdit.getIntegerProperty(TextAutocompletePlugin.PROPS_PREFIX + "minPrefixLength", 2);
    } // minPrefixLength }}}
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
    	String code = jEdit.getProperty(TextAutocompletePlugin.PROPS_PREFIX + "isWordToRemember-code");
    	if(code != null)
    	{
    		try 
    		{ bsNameSpace.setTypedVariable("word", String.class, word.toString(), null); }
			catch (UtilEvalError e) { throw new RuntimeException(e); }
			doRemember = evaluateCode(code, "isWordToRemember", IS_WORD_TO_REMEMBER, bsNameSpace);
			bsNameSpace.unsetVariable("word");
    	}
    	
    	// Check length if the word has been accepted so far
        if(doRemember)
    	{ return word.length() >= jEdit.getIntegerProperty(TextAutocompletePlugin.PROPS_PREFIX + "minWordToRememberLength", 5); }
        else
        { return false; }
    } // isWordToRemember }}}
    
    // TODO (low): add method storeWordList into a file
    // TODO (low): add method loadWordList from a file
    
    /////////////////////////////////////////////////////////////////////////////////////


    /**
     * Read a list of keys from the given property and return a list of their key codes. 
     * @param propertyName Name of the plugin's property that contains a list of names of 
     * KeyEvent's constants separated by a space or a comma. Ex.: "VK_ESCAPE VK_ENTER".
     * @return List of Integers holding key codes of the keys or null if the property isn't 
     * set or contains no valid keys.
     * @see KeyEvent
     */
	private java.util.ArrayList propertyToKeyCodes(String propertyName) {
		java.util.ArrayList keyCodes = null;
    	String acceptkeysProp = jEdit.getProperty(propertyName);
    	if(acceptkeysProp != null)
    	{
    		StringTokenizer token = new StringTokenizer( acceptkeysProp, ", ");
    		keyCodes = new java.util.ArrayList( token.countTokens() );
    		Field key = null;
    		while(token.hasMoreTokens())
    		{
    			// Get the Field corresponding to the given constant name
    			String tokenValue = token.nextToken();
    			try {
					key = KeyEvent.class.getDeclaredField(tokenValue);
				} catch (SecurityException e) {
					throw new RuntimeException(e);
				} catch (NoSuchFieldException e) {
					GUIUtilities.error(null, TextAutocompletePlugin.PROPS_PREFIX + "errorMessage", 
							new Object[]{ "Invalid key '"+tokenValue+"' in the property " + propertyName }); 
					continue;	// next token == key ...
				}
				
				// Store the Field's value for later use
				try {
					keyCodes.add(new Integer( key.getInt(KeyEvent.class) ));
				} catch (IllegalArgumentException e) { // this cannot occur
				} catch (IllegalAccessException e) {} // this cannot occur
				
    		} // while more keys
    		
    		// If no (valid) keys => use the default
    		if(keyCodes.size() == 0)
    		{ keyCodes = null; }
    	}
		return keyCodes;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Evaluate the BeanShell code and return its return value. Show an error dialog if 
	 * its return value doesn't evaluate to a boolean.
	 * @param code The code to evaluate
	 * @param codeName Calling method ^ code name such as "isWordElement". Must be valid java identifier.
	 * @param cachedCodeIndex One of IS_WORD, IS_WORD_ELEMENT, ...
	 * @param nameSpace NameSpace containing arguments for the code to be invoked.
	 * @return Return value of the code
	 */
	private boolean evaluateCode(String code, String codeName, int cachedCodeIndex, NameSpace nameSpace) {
		
		// Make sure that tha code ends with ';' == that it is a valid java statement
		code = sanitizeCode(code);
		
		try 
		{
			if(cachedCodes[cachedCodeIndex] == null)
			{ cachedCodes[cachedCodeIndex] = BeanShell.cacheBlock("textautocomplete_" + codeName,code,true); }
			
			Object retVal = BeanShell.runCachedBlock(cachedCodes[cachedCodeIndex], null, nameSpace);
			
			if (retVal instanceof Boolean) 
			{ return ((Boolean) retVal).booleanValue(); }
			else
			{
				throw new IllegalArgumentException("The beanshell code for "+codeName+" doesn't return "
						+ " a boolean but " + ((retVal == null)? null : retVal.getClass()));
			}
			
		}
		catch(Throwable e)
		{
			Log.log(Log.ERROR,this,e);
			new BeanShellErrorDialog(null,e);
			return false;
		}
	}

	/** Make sure that the code ends with ';' i.e. that it is a valid java statement */
	public static String sanitizeCode(final String code) {
		String sanitizedCode = code.trim();
		if( !sanitizedCode.endsWith(";") && !sanitizedCode.endsWith("}") )
		{ sanitizedCode += ";"; }
		return sanitizedCode;
	}
    
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
} // class
