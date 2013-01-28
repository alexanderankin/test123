/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2010-2013 Matthieu Casanova
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

package gatchan.jedit.rfcreader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * @author Matthieu Casanova
 */
public abstract class AbstractRFCIndex implements RFCIndex
{
	protected IndexSearcher searcher;
	protected Directory directory;
	protected DirectoryReader directoryReader;
	protected StandardAnalyzer analyzer;
	private QueryParser parser;
	protected Map<Integer, RFC> rfcs;

	protected AbstractRFCIndex()
	{
		RFCReaderPlugin plugin = (RFCReaderPlugin) jEdit.getPlugin("gatchan.jedit.rfcreader.RFCReaderPlugin");
		rfcs = plugin.getRfcList();
	}

	@Override
	public List<RFC> search(String query)
	{
		if (parser == null)
			parser = new MultiFieldQueryParser(Version.LUCENE_41,
				getFields(),
				analyzer);

		return _search(query, parser);
	}

	protected abstract String[] getFields();

	private List<RFC> _search(String query, QueryParser parser)
	{
		try
		{
			Query q = parser.parse(query);
			return _search(q);
		}
		catch (ParseException e)
		{
			return null;
		}
	}

	private List<RFC> _search(Query query)
	{
		try
		{
			TopDocs docs = searcher.search(query, 5000);
			ScoreDoc[] scoreDocs = docs.scoreDocs;
			List<RFC> rfcList = new ArrayList<RFC>(scoreDocs.length);
			for (ScoreDoc doc : scoreDocs)
			{
				Document document = searcher.doc(doc.doc);
				String v = document.get("number");
				int number = Integer.parseInt(v);
				rfcList.add(rfcs.get(number));
			}
			return rfcList;
		}
		catch (CorruptIndexException e)
		{
			return null;
		}
		catch (IOException e)
		{
			return null;
		}
	}

	@Override
	public void close()
	{
		try
		{
			directoryReader.close();
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		searcher = null;
		directoryReader = null;
		directory = null;
		analyzer = null;
		parser = null;
		rfcs = null;
	}
}
