package com.illengineer.jcc;

import java.util.*;
import java.util.regex.*;
import java.util.zip.*;
import java.io.*;

public class CompletionEngine
{
    // {{{ member variables
    private static int HASH_CAPACITY = 30;
    private static float HASH_LOAD_FACTOR = 0.8f;
    
    // lengthArray[n] will contain the tree of HashMaps for identifiers with n
    // prefixes. It will be resized as necessary
    private HashMap<?,?>[] lengthArray;
    
    /*  I'm going to cache all the identifiers that this CompletionEngine knows about,
	in case we want to retokenize, or maybe for some future reason. I can't see
	this list taking more than 100K of memory or so.
    */
    private ArrayList<String> allIdentifiers;
    
    private boolean ignoreCase;
    
    // }}}
    
    // {{{ constructor(s)
    public CompletionEngine() {
	initDataStore();
    }
    // }}}

    // {{{ public methods
    
    //	  {{{ loadIdentifiers()
    /**
	Reads in a list of identifiers from provider, and generates
	its internal data structures using the splitting rules given
	by each tokenizer in tokenizers. Note that you can call this method
	multiple times to build up a big store of identifiers.
	
	@param provider Used to load a list of identifiers.
	@param tokenizers Used to split identifiers into their constituent parts.
	@param minparts Any identifiers with fewer than minparts tokens are discarded from
			our internal store.
	@param ignoreCase Should our matching be case insensitive? If subsequent calls
			  to this method pass different values of ignoreCase, completion
			  probabaly won't work well.
	@param filterRegex If this is not-null, any identifier matching this regular expression
			   will not be processed.
    */
    public void loadIdentifiers(IdentifierProvider provider,
    				List<Tokenizer> tokenizers,
				int minparts,
				boolean ignoreCase,
				String filterRegex) {
	if (provider == null) {
	    provider = new InternalIdentifierProvider(allIdentifiers);
	    initDataStore();
	}
	    
	this.ignoreCase = ignoreCase;
	
	Pattern pattern = null;
	Matcher matcher;
	
	if (filterRegex != null) {
	    try {
		pattern = Pattern.compile(filterRegex);
	    } catch (Exception ex) {
		pattern = null;
	    }
	}
	
	for(String identifier : provider) {
	    if (pattern != null) {
		matcher = pattern.matcher(identifier);
		if (matcher.lookingAt())
		    continue;  // on to the next identifier
	    }
	
	    for(Tokenizer t : tokenizers) {
		char[] prefixes = t.splitIdentifer(identifier);
		if (prefixes.length >= minparts) {
		    if (ignoreCase) {
			for (int i = 0; i < prefixes.length; i++)
			    prefixes[i] = Character.toLowerCase(prefixes[i]);
		    }
		    addIdentifer(identifier, prefixes);
		    allIdentifiers.add(identifier);
		}
	    }
	}
    }
    //		}}}
        
    //	  {{{ retokenize()
    /**
	If you want to change the method of tokenizing the identifiers,
	but don't have the original IdentifierProvider(s) hanging around,
	you can call this method to retokenize all the existing identifiers
	in this CompletionEngine.
	
	@see loadIdentifiers
    */
    public void retokenize(List<Tokenizer> tokenizers, int minparts, boolean ignoreCase,
    				String filterRegex) {
	loadIdentifiers(null, tokenizers, minparts, ignoreCase, filterRegex);
    }
    //    }}}
    
    //    {{{ clearTokens()
    /**
	Makes this CompletionEngine forget all the tokens that were fed to it
	using loadIdentifiers(). In effect, it resets it to its newly created
	state.
    */
    public void clearTokens() {
	initDataStore();
    }
    //    }}}
    
    //	  {{{ complete()
    /**
	Completes the identifier suggested by acronym.
	
	@param acronym The acronym of the identifier one wishes to find.
			For instance, acronym would be "AIOOBE" if you wanted
			ArrayIndexOutOfBoundsException.
	@param sort Should we sort the resulting list in natural order?
	@return A list of possible completions, or null if there are none.
    */
    public List<String> complete(String acronym, boolean sort) {
	int n = acronym.length();
	if (lengthArray.length < (n+1) || lengthArray[n] == null)
	    return null;
	HashMap<Character,Object> currentMap = (HashMap<Character,Object>)lengthArray[n];
	ArrayList<String> completions = null;
	
	if (ignoreCase)
	    acronym = acronym.toLowerCase();
	
	for (int cntr = 0; cntr < n; cntr++) {
	    Character c = new Character(acronym.charAt(cntr));
	    if (!currentMap.containsKey(c))
		return null;
	    if (cntr < (n-1))
		currentMap = (HashMap<Character,Object>)currentMap.get(c);
	    else  // we're at the end
		completions = (ArrayList<String>)currentMap.get(c);
	}
	if (sort) {
	    // Yes, we're sorting the list in place, but it doesn't matter if the original
	    // list is modified in this way.
	    Collections.sort(completions);
	}
	return completions;
    }
    //	   }}}
    
    //	  {{{ numIdentifiers()
    public int numIdentifiers() {
	return allIdentifiers.size();
    }
    //    }}}
    
    //	  {{{ getIdentifiers()
    public List<String> getIdentifiers() {
	return allIdentifiers;
    }
    //	  }}}
    
    //	  {{{ Serialization Routines
    /**
	This method writes the data store to stream in some undefined
	serialization format.
	
	@param stream The OutputStream to which we'll save our data store.
    */
    public void serializeData(OutputStream stream) throws IOException {
	GZIPOutputStream gzipper = new GZIPOutputStream(stream);
	ObjectOutputStream o = new ObjectOutputStream(gzipper);
	o.writeObject(lengthArray);
	o.writeObject(allIdentifiers);
	gzipper.finish();
    }
    
    /**
	Reconstructs the data store from stream, which should be opened to
	a resource previously written by serializeData().
	
	@param stream The InputStream from which to read serialized data.
    */
    public void deserializeData(InputStream stream) throws IOException  {
	ObjectInputStream i = new ObjectInputStream(new GZIPInputStream(stream));
	try {
	    lengthArray = (HashMap<?,?>[])i.readObject();
	    allIdentifiers = (ArrayList<String>)i.readObject();
	} catch (ClassNotFoundException ex) {
	    // This shouldn't happen unless we're hopelessly borked, in which
	    // case it doesn't matter too much.
	    initDataStore();
	}
    }
    //    }}}
    
    
    // }}}
    
    // {{{ support methods
    private void addIdentifer(String identifier, char[] prefixes) {
	int n = prefixes.length;
	if (n == 0) return;
	ensureLengthMapSize(n+1);
	if (lengthArray[n] == null)
	    lengthArray[n] = new HashMap<Character,Object>(HASH_CAPACITY, HASH_LOAD_FACTOR);
	HashMap<Character,Object> currentMap = (HashMap<Character,Object>)lengthArray[n];
	
	// Every prefix except for the last will map to a new HashMap.
	for (int cntr = 0; cntr < (prefixes.length-1); cntr++) {
	    Character c = new Character(prefixes[cntr]);
	    if (!currentMap.containsKey(c))
		currentMap.put(c, new HashMap<Character,Object>(HASH_CAPACITY, HASH_LOAD_FACTOR));
	    currentMap = (HashMap<Character,Object>)currentMap.get(c);
	}
	
	// The last prefix will map to a List of Strings
	Character c = new Character(prefixes[prefixes.length-1]);
	if (!currentMap.containsKey(c))
	    currentMap.put(c, new ArrayList<String>());
	ArrayList<String> identifiers = (ArrayList<String>)currentMap.get(c);
	if (!identifiers.contains(identifier))
	    identifiers.add(identifier);
    }
    
    
    private void ensureLengthMapSize(int size) {
	if (lengthArray.length >= size)
	    return;
	HashMap<?,?>[] newMap = new HashMap<?,?>[(int)(size*1.4)];
	System.arraycopy(lengthArray, 0, newMap, 0, lengthArray.length);
	lengthArray = newMap;
    }
    
    private void initDataStore() {
	lengthArray = new HashMap<?,?>[10];
	allIdentifiers = new ArrayList<String>();
    }
    
    // }}}

    // {{{ inner classes
    
    // An implementation of IdentifierProvider that collects and iterates through
    // all of the identifiers passed to it.
    private class InternalIdentifierProvider implements IdentifierProvider
    {
	private ArrayList<String> identifiers;
	
	InternalIdentifierProvider(ArrayList<String> ids) {
	    identifiers = ids;
	}
	
	public void process() {};
    
	public Iterator<String> iterator() {
	    return identifiers.iterator();
	}
	
	public void forget() {};
    }
    
    // }}}
}

// :folding=explicit:
