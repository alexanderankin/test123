package com.illengineer.jcc.jedit;

import com.illengineer.jcc.*;

import java.util.*;
import java.io.*;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.StatusBar;


public class CamelCompletePlugin extends EditPlugin {

	// {{{ Member Variables
	public static final String NAME = "camelcomplete";
	public static final String OPTION_PREFIX = "options.camelcomplete.";

	private static boolean debug = false;
	private static PrintWriter debugWriter;
	
	/*  This Map will contain all the options and configuration set in the OptionPane
	    Keys/Vals:
	      providers -> List<OptionPane.OptionGroup>
	      groups -> Map<String, List<OptionPane.OptionGroup>>
	      transients -> List<OptionPane.OptionGroup>
	*/
	private static HashMap<String,Object> optionsMap;
	
	private static CompletionEngine engine, transientEngine;
	private static ArrayList<ProviderSettings> transientProviderSettings;
	private static boolean modified = false;  // Was the static identifier list changed?
	
	// }}}
	
	// {{{ Lifecycle methods
	
	public void start() {
	    if (debug) {
		try {
		    debugWriter = new PrintWriter(new FileWriter(new File("/Users/jpavel/Prog/JCodeComplete/debug.log")));
		} catch (IOException ex) {}
	    }
	    
	    optionsMap = new HashMap<String,Object>();
	    
	    engine = new CompletionEngine();
	    transientEngine = new CompletionEngine();
	    
	    // Load CompletionEngine data and optionsMap, if present
	    InputStream i = getResourceAsStream(CamelCompletePlugin.class, "cache/engine");
	    if (i != null) {
		try {
		    engine.deserializeData(i);
		    i.close();
		} catch (IOException ex) {
		    engine = new CompletionEngine();
		}
	    }
	    modified = false;
	    
	    i = getResourceAsStream(CamelCompletePlugin.class, "options");
	    if (i != null) {
		boolean failed = false;
		try {
		    ObjectInputStream ois = new ObjectInputStream(i);
		    optionsMap = (HashMap<String,Object>)ois.readObject();
		    ois.close();
		} catch (IOException ex) {
		    failed = true;
		} catch (ClassNotFoundException ex) {
		    failed = true;
		}
		if (failed)
		    optionsMap = new HashMap<String,Object>();
	    }
	    
	    transientProviderSettings = reconstituteTransientProviders
		    ((List<OptionPanel.OptionGroup>)getOption("transients"));
	}
	
	public void stop() {
	    // Persist CompletionEngine data and optionsMap
	    OutputStream out;
	    if (modified) {
		out = getResourceAsOutputStream(CamelCompletePlugin.class, "cache/engine");
		if (out != null) {
		    try {
			engine.serializeData(out);
			out.close();
		    } catch (IOException ex) {}
		}
	    }
	    
	    out = getResourceAsOutputStream(CamelCompletePlugin.class, "options");
	    if (out != null) {
		try {
		    ObjectOutputStream oos = new ObjectOutputStream(out);
		    oos.writeObject(optionsMap);
		    oos.close();
		} catch (IOException ex) {
		}
	    }
	    
	    if (debug)
		debugWriter.close();

	    engine = null;
	    transientEngine = null;
	    transientProviderSettings = null;
	    optionsMap = null;
	}
	
	// }}}
	
	// {{{ processConfiguration()
	
	/**
	    This routine reads all the configuration options in the optionsMap and creates the
	    appropriate IdentifierProviders and Tokenizers, and sends them to the CompletionEngine.
	*/
	static void processConfiguration() {
	    List<OptionPanel.OptionGroup> groups = (List<OptionPanel.OptionGroup>)optionsMap.get("providers");
	    if (groups != null) {
		IdentifierProvider provider = null;
		ArrayList<Tokenizer> tokenizers;
		engine.clearTokens();
		for (OptionPanel.OptionGroup og : groups) {
		    tokenizers = new ArrayList<Tokenizer>(og.tokenizers.size());
		    
		    if (og.provider.equals("ctags"))
			provider = new CTagsFileProvider(new File(og.fileName));
		    else if (og.provider.equals("jar"))
			provider = new JarFileProvider(new File(og.fileName));
			
		    for (String [] t : og.tokenizers) {
			if (t[0].equals("camelcase"))
			    tokenizers.add(new CamelCaseTokenizer());
			else if (t[0].equals("regex")) 
			    tokenizers.add(new RegexpTokenizer(t[1], t[2].equals("y")));
		    }
		    provider.process();
		    engine.loadIdentifiers(provider, tokenizers, og.minparts, 
					   og.ignoreCase, og.filterRegex);
		    provider.forget();
		}
	    }
	    modified = true;
	}  // }}}
	
	// {{{ Option routines
	public static void setOption(String key, Object val) {
	    optionsMap.put(key, val);
	}
	
	public static Object getOption(String key) {
	    return optionsMap.get(key);
	}
	// }}}
	
	// {{{ Routines for transients
	public static void reloadTransientIdentifiers() {
	    transientEngine.clearTokens();
	    for (ProviderSettings is : transientProviderSettings) {
		is.provider.process();
		transientEngine.loadIdentifiers(is.provider, is.tokenizers,
				    is.minparts, is.ignoreCase, is.filterRegex);
		is.provider.forget();
	    }
	}
	
	private static ArrayList<ProviderSettings> 
		reconstituteTransientProviders(List<OptionPanel.OptionGroup> settings) 
	{
	    ArrayList<ProviderSettings> providerSettings = new ArrayList<ProviderSettings>();
	    if (settings != null) {
		for (OptionPanel.OptionGroup og : settings) {
		}
	    }
	    return providerSettings;
	}
	
	// }}}
	
	// {{{ completion methods
	public static void complete(View view, JEditTextArea textArea) {
	    CompleteWord.completeWord(view, engine, transientEngine);
	}
	
	// }}}
	
	// {{{ Debugging methods
	public static void debugPrint(String s) {
	    if (debug) {
		debugWriter.println(s);
		debugWriter.flush();
	    }
	}
	// }}}
	
	// {{{ Inner Classes
	private static class ProviderSettings
	{
	    IdentifierProvider provider;
	    List<Tokenizer> tokenizers;
	    int minparts;
	    boolean ignoreCase;
	    String filterRegex;
	}
	// }}}

}

//:folding=explicit:collapseFolds-1: