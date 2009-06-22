package gatchan.jedit.lucene;

import java.util.HashMap;
import java.util.Map;

import lucene.SourceCodeAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class AnalyzerFactory
{
	private static final Map<String, Analyzer> analyzers = new HashMap<String, Analyzer>();

	static
	{
		analyzers.put("Standard", new StandardAnalyzer());
		analyzers.put("Simple", new SimpleAnalyzer());
		analyzers.put("Java identifier", new SourceCodeAnalyzer());
		analyzers.put("Whitespace", new WhitespaceAnalyzer());
		analyzers.put("Keyword", new KeywordAnalyzer());
		analyzers.put("Stop", new StopAnalyzer());
	}

	static String[] getAnalyzerNames()
	{
		String[] names = new String[analyzers.size()];
		analyzers.keySet().toArray(names);
		return names;
	}

	static String getAnalyzerName(Analyzer analyzer)
	{
		for (String name : analyzers.keySet())
		{
			if (analyzers.get(name).getClass() == analyzer.getClass())
				return name;
		}
		return null;
	}

	static Analyzer getAnalyzer(String name)
	{
		return analyzers.get(name);
	}
}
