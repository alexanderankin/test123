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
	      engines -> Map<String (enginename), (providers)>
	      	(providers) -> List<OptionPanel.OptionGroup>
	      groups -> Map<String (groupname), List<OptionPanel.OptionGroup>>
	*/
	private static HashMap<String,Object> optionsMap;
	
	private static HashMap<String,EngineGroup> engineMap;
	private static ArrayList<CompletionEngine> engines;
	
	// }}}
	
	// {{{ Lifecycle methods
	
	public void start() {
	    if (debug) {
		try {
		    debugWriter = new PrintWriter(new FileWriter(new File("/Users/jpavel/Prog/JCodeComplete/debug.log")));
		} catch (IOException ex) {}
	    }
	    
	    optionsMap = null;
	    InputStream i;

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
		    optionsMap = null;
	    }
	    if (optionsMap == null) {
		optionsMap = new HashMap<String,Object>();
		HashMap<String,List<OptionPanel.OptionGroup>> _enginesMap = 
		    new HashMap<String,List<OptionPanel.OptionGroup>>();
		_enginesMap.put("default", new ArrayList<OptionPanel.OptionGroup>());
		optionsMap.put("engines", _enginesMap);
	    }
	    
	    engineMap = new HashMap<String,EngineGroup>();
	    Map<String,List<OptionPanel.OptionGroup>> _enginesMap = 
		(Map<String,List<OptionPanel.OptionGroup>>)optionsMap.get("engines");
	    for (String engineName : _enginesMap.keySet()) {
		EngineGroup eg = new EngineGroup();
		eg.engine = new CompletionEngine();
		eg.modified = false;
		
		i = getResourceAsStream(CamelCompletePlugin.class, "cache/engine-"+engineName);
		if (i != null) {
		    try {
			eg.engine.deserializeData(i);
			i.close();
		    } catch (IOException ex) {
			eg.engine = new CompletionEngine();
		    }
		}
		engineMap.put(engineName, eg);
		engines.add(eg.engine);
	    }
	}
	
	public void stop() {
	    // Persist CompletionEngine data and optionsMap
	    OutputStream out;
	    for (String engineName : engineMap.keySet()) {
		EngineGroup eg = engineMap.get(engineName);
		if (eg.modified) {
		    out = getResourceAsOutputStream(CamelCompletePlugin.class, "cache/engine-"+engineName);
		    if (out != null) {
			try {
			    eg.engine.serializeData(out);
			    out.close();
			} catch (IOException ex) {}
		    }
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

	    engineMap = null;
	    engines = null;
	    optionsMap = null;
	}
	
	// }}}
	
	// {{{ processConfiguration()
	
	/**
	    This routine reads all the configuration options in the optionsMap and creates the
	    appropriate IdentifierProviders and Tokenizers, and sends them to the CompletionEngine.
	*/
	static void processConfiguration() {
	    Map<String,List<OptionPanel.OptionGroup>> _enginesMap = 
		(Map<String,List<OptionPanel.OptionGroup>>)optionsMap.get("engines");
	    for (String engineName : _enginesMap.keySet())
		processConfiguration(engineName);
	    
	}  
	
	static void processConfiguration(String engineName) {
	    EngineGroup eg;
	    if (engineMap.containsKey(engineName))
		eg = engineMap.get(engineName);
	    else {
		eg = new EngineGroup();
		eg.engine = new CompletionEngine();
		eg.modified = false;
		engineMap.put(engineName, eg);
		engines.add(eg.engine);
	    }

	    Map<String,List<OptionPanel.OptionGroup>> _enginesMap = 
		(Map<String,List<OptionPanel.OptionGroup>>)optionsMap.get("engines");
	    List<OptionPanel.OptionGroup> groups = 
			(List<OptionPanel.OptionGroup>)_enginesMap.get(engineName);
	    if (groups != null) {
		IdentifierProvider provider = null;
		ArrayList<Tokenizer> tokenizers;
		eg.engine.clearTokens();
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
		    eg.engine.loadIdentifiers(provider, tokenizers, og.minparts, 
					      og.ignoreCase, og.filterRegex);
		    provider.forget();
		}
	    }
	    eg.modified = true;
	}
	
	// }}}
	
	// {{{ Option routines
	public static void setOption(String key, Object val) {
	    optionsMap.put(key, val);
	}
	
	public static Object getOption(String key) {
	    return optionsMap.get(key);
	}
	// }}}
	
	// {{{ completion methods
	public static void complete(View view, JEditTextArea textArea) {
	    // TODO: CompleteWord.completeWord(view, engines);
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
	
	private static class EngineGroup
	{
	    CompletionEngine engine;
	    boolean modified;
	}
	// }}}

}

//:folding=explicit:collapseFolds-1: