package gatchan.jedit.lucene;

import java.util.HashMap;
import java.util.Map;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

public class AnalyzerFactory
{
	private static final Map<String, Analyzer> analyzers = new HashMap<String, Analyzer>();

	static
	{
		analyzers.put("Standard", new StandardAnalyzer(Version.LUCENE_30));
		analyzers.put("Simple", new SimpleAnalyzer());
		analyzers.put("Java identifier", new SourceCodeAnalyzer());
		analyzers.put("Whitespace", new WhitespaceAnalyzer());
		analyzers.put("Keyword", new KeywordAnalyzer());
		analyzers.put("Stop", new StopAnalyzer(Version.LUCENE_30));
	}

	static void dispose()
	{
		analyzers.clear();
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
