/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011-2013 Matthieu Casanova
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.kpouer.jedit.smartopen.indexer.FileProvider;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
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
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ProgressObserver;
import projectviewer.vpt.VPTProject;
//}}}

/**
 * @author Matthieu Casanova
 */
public class FileIndex
{
	private static final Pattern DOTSPLIT = Pattern.compile("\\.");
	private static final Pattern CAMELCASE = Pattern.compile("(?<!^)(?=[A-Z])");
	private Directory directory;
	private final Object LOCK = new Object();
	@Nullable
	private final VPTProject project;

	private IndexReader reader;

	//{{{ FileIndex constructor
	public FileIndex(@Nullable VPTProject project)
	{
		this.project = project;
		directory = getDirectory();
	} //}}}

	private void setDirectory(Directory directory)
	{
		synchronized (LOCK)
		{
			if (reader != null)
			{
				IOUtilities.closeQuietly(reader);
				reader = null;
			}
			this.directory = directory;
		}
	}

	private void initReader() throws IOException
	{
		synchronized (LOCK)
		{
			if (reader == null)
				reader = DirectoryReader.open(directory);
		}
	}

	@Nullable
	public VPTProject getProject()
	{
		return project;
	}

	//{{{ getIndexName() method
	private String getIndexName()
	{
		String indexName = (project == null) ? "fileIndex" : project.getName() + "-index";
		return indexName;
	} //}}}

	//{{{ getFiles() method
	public List<String> getFiles(String s)
	{
		if (s == null || s.isEmpty())
			return Collections.emptyList();

		SortField sortField = new SortField("frequency", SortField.Type.LONG,true);
		Sort sort = new Sort(sortField);
		List<String> l = new ArrayList<String>();
		try
		{
			String[] dotSplit = DOTSPLIT.split(s);
			StringBuilder builder = new StringBuilder(500);
			for (String token : dotSplit)
			{
				String[] split = CAMELCASE.split(token);
				for (int i = 0; i < split.length; i++)
				{
					builder.append(split[i]).append('*');
				}
			}
			Query queryCaps = new WildcardQuery(new Term("name_caps", builder.toString()));
			s = s.toLowerCase();
			Query queryNoCaps = new WildcardQuery(new Term("name", '*' + s + '*'));

			BooleanQuery query = new BooleanQuery();
			queryCaps.setBoost(10);
			query.add(queryCaps, BooleanClause.Occur.SHOULD);
			query.add(queryNoCaps, BooleanClause.Occur.SHOULD);

			initReader();
			IndexSearcher searcher = new IndexSearcher(reader);
			TopDocs search = searcher.search(query, 100, sort);
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
		return l;
	} //}}}

	//{{{ getFrequency() method
	private long getFrequency(String path) throws IOException
	{
		long frequency = 0L;
		try
		{
			initReader();
			IndexSearcher searcher = new IndexSearcher(reader);
			BooleanQuery query = new BooleanQuery();
			Term term = new Term("path",path);
			query.add(new TermQuery(term),BooleanClause.Occur.MUST);
			TopDocs search = searcher.search(query,1);
			if (search.scoreDocs.length == 1)
			{
				Document doc = searcher.doc(search.scoreDocs[0].doc);
				IndexableField frequencyField = doc.getField("frequency");
				frequency = frequencyField.numericValue().longValue();
			}
		}
		catch(IndexNotFoundException infe)
		{
		//ok
		}
		return frequency;
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
					tempDirectory = getDirectory();
					openMode = IndexWriterConfig.OpenMode.CREATE;
				}
				else
				{
					tempDirectory = directory;
					openMode = IndexWriterConfig.OpenMode.CREATE_OR_APPEND;
				}
				observer.setMaximum(fileProvider.size());
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
				IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_42, analyzer);
				conf.setOpenMode(openMode);
				writer = new IndexWriter(tempDirectory, conf);

				for (int i = 0; i < fileProvider.size(); i++)
				{
					String path = fileProvider.next();
					observer.setValue(i);
					if (i % 10 == 0)
						observer.setStatus(path);

					long frequency = getFrequency(path);
					writer.addDocument(createDocument(path, frequency + 1));
				}
				if (reset)
				{
					IOUtilities.closeQuietly(directory);
				}
				setDirectory(tempDirectory);
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
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);
				IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_41, analyzer);
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

	//{{{ updateFrequency() method
	public void updateFrequency(String path)
	{
		synchronized (LOCK)
		{
			BooleanQuery query = new BooleanQuery();
			Term term = new Term("path",path);
			query.add(new TermQuery(term),BooleanClause.Occur.MUST);
			IndexWriter writer = null;
			try
			{
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
				IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_42, analyzer);
				conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
				writer = new IndexWriter(directory, conf);
				long frequency = getFrequency(path);
				if (frequency == 0)
					return;
				writer.deleteDocuments(term);
				writer.addDocument(createDocument(path, frequency + 1));
			}
			catch (Exception e)
			{
				Log.log(Log.ERROR, this, e);
			}
			finally
			{
				IOUtilities.closeQuietly(writer);
			}
		}
	} //}}}

	// createDocument() method
	private static Document createDocument(String path, long frequency)
	{
		Document document = new Document();
		String fileName = MiscUtilities.getFileName(path);
		document.add(new StringField("path", path, Field.Store.YES));
		document.add(new TextField("name", fileName, Field.Store.NO));
		document.add(new StringField("name_caps", fileName, Field.Store.NO));
		document.add(new LongField("frequency", frequency, Field.Store.YES));
		return document;
	} //}}}

	//{{{ getDirectory() method
	private Directory getDirectory()
	{
		Directory tempDirectory;
		if (jEdit.getBooleanProperty("options.smartopen.memoryindex"))
		{
			tempDirectory = new RAMDirectory();
		}
		else
		{
			try
			{
				EditPlugin plugin = jEdit.getPlugin(SmartOpenPlugin.class.getName());
				File pluginHome = plugin.getPluginHome();
				File index = new File(pluginHome, getIndexName());
				index.mkdirs();
				tempDirectory = FSDirectory.open(index);
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, e);
				tempDirectory = new RAMDirectory();
			}
		}
		return tempDirectory;
	} //}}}
}
