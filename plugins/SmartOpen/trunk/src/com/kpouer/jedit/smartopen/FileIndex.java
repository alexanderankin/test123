/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011-2012 Matthieu Casanova
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

package com.kpouer.jedit.smartopen;

//{{{ Imports
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.kpouer.jedit.smartopen.indexer.FileProvider;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ProgressObserver;
//}}}

/**
 * @author Matthieu Casanova
 */
public class FileIndex
{
	private static final Pattern CAMELCASE = Pattern.compile("(?<!^)(?=[A-Z])");
	private Directory directory;
	private final Object LOCK = new Object();

	//{{{ FileIndex constructor
	public FileIndex()
	{
		directory = new RAMDirectory();
	} //}}}

	//{{{ getFiles() method
	public List<String> getFiles(String s)
	{
		if (s == null || s.isEmpty())
			return Collections.emptyList();

		IndexReader indexReader = null;
		List<String> l = new ArrayList<String>();
		try
		{
			String[] split = CAMELCASE.split(s);
			StringBuilder builder = new StringBuilder(s.length() + split.length + 2);
			for (int i = 0; i < split.length; i++)
			{
				builder.append(split[i]).append('*');
			}
			builder.append('*');
			Query queryCaps = new WildcardQuery(new Term("name_caps", builder.toString()));
			s = s.toLowerCase();
			Query queryNoCaps = new WildcardQuery(new Term("name", '*' + s + '*'));

			BooleanQuery query = new BooleanQuery();
			queryCaps.setBoost(10);
			query.add(queryCaps, BooleanClause.Occur.SHOULD);
			query.add(queryNoCaps, BooleanClause.Occur.SHOULD);

			indexReader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			TopDocs search = searcher.search(query, 100);
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc scoreDoc : scoreDocs)
			{
				Document doc = searcher.doc(scoreDoc.doc);
				IndexableField path = doc.getField("path");
				l.add(path.stringValue());
			}
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			IOUtilities.closeQuietly(indexReader);
		}
		return l;
	} //}}}

	//{{{ addFiles() method
	/**
	 * Index files.
	 * @param fileProvider the file provider to index
	 * @param observer the progress observer
	 * @param reset true if you want to reset the previous index
	 */
	public void addFiles(FileProvider fileProvider, ProgressObserver observer, boolean reset)
	{
		long start = System.currentTimeMillis();
		synchronized (LOCK)
		{
			IndexWriter writer = null;
			try
			{
				Directory tempDirectory;
				IndexWriterConfig.OpenMode openMode;
				if (reset)
				{

					tempDirectory = new RAMDirectory();
					openMode = IndexWriterConfig.OpenMode.CREATE;
				}
				else
				{
					tempDirectory = directory;
					openMode = IndexWriterConfig.OpenMode.CREATE_OR_APPEND;
				}
				observer.setMaximum(fileProvider.size());
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
				IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_40, analyzer);
				conf.setOpenMode(openMode);
				writer = new IndexWriter(tempDirectory, conf);
				for (int i = 0; i < fileProvider.size(); i++)
				{
					String path = fileProvider.next();
					observer.setValue(i);
					observer.setStatus(path);
					Document document = new Document();
					document.add(new StringField("path", path, Field.Store.YES));

					String fileName = MiscUtilities.getFileName(path);
					document.add(new TextField("name", fileName, Field.Store.NO));
					document.add(new StringField("name_caps", fileName, Field.Store.NO));
					writer.addDocument(document);
				}
				if (reset)
				{
					try
					{
						directory.close();
					}
					catch (IOException e)
					{
						Log.log(Log.ERROR, this, e);
					}
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
		long end = System.currentTimeMillis();
		Log.log(Log.MESSAGE, this, "Added " + fileProvider.size()+" files in "+(end - start) + "ms");
	} //}}}

	//{{{ removeFiles() method
	public void removeFiles(FileProvider fileProvider, ProgressObserver observer)
	{
		long start = System.currentTimeMillis();
		synchronized (LOCK)
		{
			IndexWriter writer = null;
			try
			{
				observer.setMaximum(fileProvider.size());
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
				IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_40, analyzer);
				conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
				writer = new IndexWriter(directory, conf);
				for (int i = 0; i < fileProvider.size(); i++)
				{
					String path = fileProvider.next();
					observer.setValue(i);
					observer.setStatus(path);
					writer.deleteDocuments(new Term("path", path));
				}
				writer.commit();
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
		long end = System.currentTimeMillis();
		Log.log(Log.MESSAGE, this, "Removed " + fileProvider.size()+" files in "+(end - start) + "ms");
	} //}}}
}
