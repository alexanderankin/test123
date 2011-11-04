/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import gatchan.jedit.ancestor.AncestorPlugin;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
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
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ProgressObserver;

/**
 * @author Matthieu Casanova
 */
public class FileIndex
{
	private static final Pattern CAMELCASE = Pattern.compile("(?<!^)(?=[A-Z])");
	private Directory directory;
	private final Object LOCK = new Object();
	private final String[] indexes = { "index1", "index2" };
	private int indexPos;

	public FileIndex()
	{
		EditPlugin plugin = jEdit.getPlugin(AncestorPlugin.class.getName());
		File pluginHome = plugin.getPluginHome();
		String indexName = getIndexName();
		File index = new File(pluginHome, indexName);
		index.mkdirs();
		try
		{
			directory = FSDirectory.open(index);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
			directory = new RAMDirectory();
		}
	}

	private String getIndexName()
	{
		indexPos = (indexPos + 1) % 2;
		String indexName = indexes[indexPos];
		return indexName;
	}

	public List<String> getFiles(String s)
	{
		if (s == null || s.isEmpty())
			return Collections.emptyList();
		try
		{
			if (!IndexReader.indexExists(directory))
				return Collections.emptyList();
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
			return Collections.emptyList();
		}
		IndexSearcher searcher = null;
		List<String> l = new ArrayList<String>();
		try
		{
			s = s.toLowerCase();
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
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			IOUtilities.closeQuietly(searcher);
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
				EditPlugin plugin = jEdit.getPlugin(AncestorPlugin.class.getName());
				File pluginHome = plugin.getPluginHome();
				File index = new File(pluginHome, getIndexName());
				Directory tempDirectory = FSDirectory.open(index);
				observer.setMaximum(fileProvider.size());
				Analyzer analyzer = new KeywordAnalyzer();
				IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_34, analyzer);
				conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
				writer = new IndexWriter(tempDirectory, conf);
				for (int i = 0; i < fileProvider.size(); i++)
				{
					observer.setValue(i);
					VFSFile next = fileProvider.next();
					observer.setStatus(next.getPath());
					Document document = new Document();
					document.add(
						new Field("path", next.getPath().toLowerCase(), Field.Store.YES, Field.Index.NO));

					document.add(new Field("name", next.getName().toLowerCase(), Field.Store.NO,
							       Field.Index.ANALYZED));
					writer.addDocument(document);
				}
				directory = tempDirectory;
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, e);
			}
			finally
			{
				IOUtilities.closeQuietly(writer);
			}
		}
	}

	interface FileProvider
	{
		VFSFile next();

		int size();
	}
}
