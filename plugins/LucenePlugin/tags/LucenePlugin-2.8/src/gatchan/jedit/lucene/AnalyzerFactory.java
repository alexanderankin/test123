/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, 2011 Matthieu Casanova
 * Copyright (C) 2009, 2011 Shlomy Reinstein
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
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
		analyzers.put("Standard", new StandardAnalyzer(Version.LUCENE_34));
		analyzers.put("Simple", new SimpleAnalyzer(Version.LUCENE_34));
		analyzers.put("Java identifier", new SourceCodeAnalyzer());
		analyzers.put("Whitespace", new WhitespaceAnalyzer(Version.LUCENE_34));
		analyzers.put("Keyword", new KeywordAnalyzer());
		analyzers.put("Stop", new StopAnalyzer(Version.LUCENE_34));
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
		for (Map.Entry<String, Analyzer> stringAnalyzerEntry : analyzers.entrySet())
		{
			if (stringAnalyzerEntry.getValue().getClass() == analyzer.getClass())
				return stringAnalyzerEntry.getKey();
		}
		return null;
	}

	static Analyzer getAnalyzer(String name)
	{
		return analyzers.get(name);
	}

	private AnalyzerFactory()
	{
	}
}
