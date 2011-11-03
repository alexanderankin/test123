/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 jEdit contributors
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

package gatchan.jedit.smartopen;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import gatchan.jedit.ancestor.AncestorPlugin;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.ReusableAnalyzerBase;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.ProgressObserver;

/**
 * @author Matthieu Casanova
 */
public class FileIndex
{
	private static final Pattern CAMELCASE = Pattern.compile("(?<!^)(?=[A-Z])");
	private Directory directory;
	private final Object LOCK = new Object();

	public FileIndex()
	{
		EditPlugin plugin = jEdit.getPlugin(AncestorPlugin.class.getName());
		File pluginHome = plugin.getPluginHome();
		File index = new File(pluginHome, "index");
		index.mkdirs();
		try
		{
			directory = FSDirectory.open(index);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			directory = new RAMDirectory();
		}
	}

	public List<String> getFiles(String s)
	{
		if (s == null || s.isEmpty())
			return Collections.emptyList();
		IndexSearcher searcher = null;
		List<String> l = new ArrayList<String>();
		try
		{
			Query query = new PrefixQuery(new Term("name", s));


			searcher = new IndexSearcher(directory);

			TopDocs search = searcher.search(query, 100);
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc scoreDoc : scoreDocs)
			{
				Document doc = searcher.doc(scoreDoc.doc);
				Fieldable path = doc.getFieldable("path");
				l.add(path.stringValue());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				searcher.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return l;
	}

	public void addFiles(FileProvider fileProvider, ProgressObserver observer)
	{
		synchronized (LOCK)
		{
			IndexWriter writer = null;
			try
			{
				observer.setMaximum(fileProvider.size());
				writer = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_34,
											  new StandardAnalyzer(
												  Version.LUCENE_34)));
				for (int i = 0; i < fileProvider.size(); i++)
				{
					observer.setValue(i);
					VFSFile next = fileProvider.next();
					observer.setStatus(next.getPath());
					Document document = new Document();
					document.add(
						new Field("path", next.getPath(), Field.Store.YES, Field.Index.NO));

					document.add(new Field("name", next.getName(), Field.Store.NO,
							       Field.Index.ANALYZED));
					writer.addDocument(document);
				}
				writer.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	interface FileProvider
	{
		VFSFile next();

		int size();
	}
}
