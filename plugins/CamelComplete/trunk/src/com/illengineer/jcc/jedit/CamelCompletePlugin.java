package com.illengineer.jcc.jedit;

import com.illengineer.jcc.*;

import java.util.*;
import java.io.*;

import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.StatusBar;
import org.gjt.sp.jedit.gui.BeanShellErrorDialog;

import bsh.Interpreter;

public class CamelCompletePlugin extends EditPlugin {

	// {{{ Member Variables
	public static final String NAME = "camelcomplete";
	public static final String OPTION_PREFIX = "options.camelcomplete.";

	public static final String DEFAULT_ENGINE_NAME = "default";
	
	private static boolean debug = false;
	private static PrintWriter debugWriter;
	
	/*  This Map will contain all the options and configuration set in the OptionPane
	    Keys/Vals:
	      engines -> Map<String (enginename), (providers)>
	      	(providers) -> List<OptionPanel.OptionGroup>
	      groups -> Map<String (groupname), List<OptionPanel.OptionGroup>>
	      cache -> Boolean.(TRUE|FALSE)
	      update -> Boolean.(TRUE|FALSE)
	      popup-rows -> Integer
	*/
	private static HashMap<String,Object> optionsMap;
	
	private static HashMap<String,List<OptionPanel.OptionGroup>> enginesOptionsMap;
	private static HashMap<String,EngineGroup> engineMap;
	private static ArrayList<CompletionEngine> engines;
	
	
	private static File homeDir;
	// }}}
	
	// {{{ Lifecycle methods
	
	public void start() {
	    if (debug) {
		try {
		    debugWriter = new PrintWriter(new FileWriter(new File("/Users/jpavel/Prog/JCodeComplete/debug.log")));
		} catch (IOException ex) {}
	    }
	    
	    /* For debugging! */
	    // try {
	    
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
	    if (optionsMap == null || !optionsMap.containsKey("engines")) {
		optionsMap = new HashMap<String,Object>();
		enginesOptionsMap = new HashMap<String,List<OptionPanel.OptionGroup>>();
		enginesOptionsMap.put(DEFAULT_ENGINE_NAME, new ArrayList<OptionPanel.OptionGroup>());
		optionsMap.put("engines", enginesOptionsMap);
	    } else {
		enginesOptionsMap = (HashMap<String,List<OptionPanel.OptionGroup>>)optionsMap.get("engines");
	    }
	    
	    engineMap = new HashMap<String,EngineGroup>();
	    engines = new ArrayList<CompletionEngine>();

	    if (!optionsMap.containsKey("cache"))
		optionsMap.put("cache", Boolean.TRUE);
	    if (!optionsMap.containsKey("update"))
		optionsMap.put("update", Boolean.FALSE);
	    if (!optionsMap.containsKey("popup-rows"))
		optionsMap.put("popup-rows", new Integer(12));

	    
	    for (String engineName : enginesOptionsMap.keySet()) {
		EngineGroup eg = new EngineGroup();
		eg.engine = new CompletionEngine();
		eg.modified = false;
		
		if (((Boolean)optionsMap.get("cache")).booleanValue()) {
		    i = getResourceAsStream(CamelCompletePlugin.class, "cache/engine-"+engineName);
		    if (i != null) {
			try {
			    eg.engine.deserializeData(i);
			    i.close();
			} catch (IOException ex) {
			    eg.engine = new CompletionEngine();
			}
		    }
		}
		engineMap.put(engineName, eg);
		engines.add(eg.engine);
	    }
	    
	    homeDir = getPluginHome();
	    
	    if (((Boolean)optionsMap.get("update")).booleanValue())
		processConfiguration();
	    
	    /* For debugging! */
	    // } catch (Exception ex) {
		// debugPrintStacktrace(ex);
	    // }
	}
	
	public void stop() {
	    // Persist CompletionEngine data and optionsMap
	    OutputStream out;
	    
	    if ((((Boolean)optionsMap.get("cache")).booleanValue())) {
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

	    optionsMap = null;
	    enginesOptionsMap = null;
	    engineMap = null;
	    engines = null;
	}
	
	// }}}
	
	// {{{ processConfiguration() & helpers
	
	/**
	    This routine reads all the configuration options in the optionsMap and creates the
	    appropriate IdentifierProviders and Tokenizers, and sends them to the CompletionEngine.
	*/
	static void processConfiguration() {
	    for (String engineName : enginesOptionsMap.keySet())
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

	    List<OptionPanel.OptionGroup> groups = 
			(List<OptionPanel.OptionGroup>)enginesOptionsMap.get(engineName);
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
		    else if (og.provider.equals("text"))
			provider = new TextFileProvider(new File(og.fileName));
		    else if (og.provider.equals("code")) {
			boolean failed = false;
			Interpreter bsh = new Interpreter();
			try {
			    bsh.eval("import com.illengineer.jcc.*;");
			    bsh.eval("IdentifierProvider __ip;");
			    bsh.eval("__ip = (IdentifierProvider)" + og.extra);
			    provider = (IdentifierProvider)bsh.get("__ip");
			} catch (bsh.EvalError ex) {
			    failed = true;
			    View v = jEdit.getActiveView();
			    if (v != null)
				new BeanShellErrorDialog(v, ex);
			    debugPrintStacktrace(ex);
			} catch (Exception ex) {
			    failed = true;
			    View v = jEdit.getActiveView();
			    if (v != null)
				new BeanShellErrorDialog(v, ex);
			    debugPrintStacktrace(ex);
			}
			if (failed)
			    provider = new NullProvider();
		    } else if (og.provider.equals("buffer")) {
			provider = new BufferWordsProvider(og.extra);
		    }
		    for (String [] t : og.tokenizers) {
			if (t[0].equals("camelcase"))
			    tokenizers.add(new CamelCaseTokenizer());
			else if (t[0].equals("regex")) 
			    tokenizers.add(new RegexpTokenizer(t[1], t[2].equals("y")));
		    }
		    provider.process();
		    eg.engine.loadIdentifiers(provider, tokenizers, og.minparts, og.maxparts,
					      og.ignoreCase, og.filterRegex);
		    provider.forget();
		}
	    }
	    eg.modified = true;
	}
	
	static void deleteEngine(String engineName) {
	    if (enginesOptionsMap.size() <= 1)
		return;
		
	    enginesOptionsMap.remove(engineName);
	    EngineGroup eg = engineMap.remove(engineName);
	    if (eg != null)
		engines.remove(eg.engine);
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
	    CompleteWord.completeWord(view, engines);
	}
	
	public static List<String> getCompletions(String word) {
	    ArrayList<String> completions = new ArrayList<String>();
	    for (CompletionEngine engine : engines) {
		List<String> c = engine.complete(word, true);
		if (c != null)
		    completions.addAll(c);
	    }
	    return completions;
	}
	
	// }}}
	
	// {{{ Debugging methods
	public static void debugPrint(String s) {
	    if (debug) {
		debugWriter.println(s);
		debugWriter.flush();
	    }
	}

	public static void debugPrintStacktrace(Exception ex) {
	    if (debug) {
		ex.printStackTrace(debugWriter);
		debugWriter.flush();
	    }
	}

	// }}}
	
	// {{{ Inner Classes
	private static class EngineGroup
	{
	    CompletionEngine engine;
	    boolean modified;
	}
	// }}}

	// {{{ Misc public methods
	public static Set<String> getEngineNames() {
	    return enginesOptionsMap.keySet();
	}
	
	public static void clearCacheDir() {
	    if (homeDir != null) {
		File cacheDir = new File(homeDir, "cache");
		if (cacheDir.exists()) {
		    File [] caches = cacheDir.listFiles();
		    for (File f : caches)
			f.delete();
		}
	    }
	}
	// }}}
}

//:folding=explicit:collapseFolds=1: