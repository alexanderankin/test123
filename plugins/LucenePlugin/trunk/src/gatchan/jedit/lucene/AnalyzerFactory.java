package gatchan.jedit.lucene;

import java.util.HashMap;

import lucene.SourceCodeAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class AnalyzerFactory {
	static final HashMap<String, Analyzer> analyzers = new HashMap<String, Analyzer>();
	
	static {
		analyzers.put("Standard", new StandardAnalyzer());
		analyzers.put("Simple", new SimpleAnalyzer());
		analyzers.put("Java identifier", new SourceCodeAnalyzer());
		analyzers.put("Whitespace", new WhitespaceAnalyzer());
		analyzers.put("Keyword", new KeywordAnalyzer());
		analyzers.put("Stop", new StopAnalyzer());
	}
	
	static String [] getAnalyzerNames()
	{
		String [] names = new String[analyzers.size()];
		analyzers.keySet().toArray(names);
		return names;
	}
	static Analyzer getAnalyzer(String name)
	{
		return analyzers.get(name);
	}
}
