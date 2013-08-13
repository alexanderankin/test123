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
import org.apache.lucene.util.BytesRef;
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
	private final DocumentFactory documentFactory;
	private final IndexWriterConfig indexWriterConfig;

	//{{{ FileIndex constructor
	public FileIndex(@Nullable VPTProject project)
	{
		this.project = project;
		directory = getDirectory();
		indexWriterConfig = new IndexWriterConfig(Version.LUCENE_42, new StandardAnalyzer(Version.LUCENE_42));
		documentFactory = new DocumentFactory();
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
		String indexName = project == null ? "fileIndex" : project.getName() + "-index";
		return indexName;
	} //}}}

	//{{{ getFiles() method
	public List<String> getFiles(String s)
	{
		if (s == null || s.isEmpty())
			return Collections.emptyList();

		SortField sortField = new SortField("frequency", SortField.Type.LONG,true);
		Sort sort = new Sort(sortField);
		List<String> l = new ArrayList<>();
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
			queryCaps.setBoost(10.0F);
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
	private long getFrequency(CharSequence path) throws IOException
	{
		initReader();
		return new FrequencySearch(reader).getFrequency(path);
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
		IndexWriter writer = null;
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

		synchronized (LOCK)
		{
			indexWriterConfig.setOpenMode(openMode);
			try
			{
				writer = new IndexWriter(tempDirectory, indexWriterConfig);

				initReader();
				FrequencySearch frequencySearch = new FrequencySearch(reader);
				for (int i = 0; i < fileProvider.size(); i++)
				{
					String path = fileProvider.next();
					observer.setValue(i);
					if (i % 10 == 0)
						observer.setStatus(path);

					long frequency = frequencySearch.getFrequency(path);
					writer.addDocument(documentFactory.createDocument(path, frequency + 1L));
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
		IndexWriter writer = null;
		observer.setMaximum(fileProvider.size());

		synchronized (LOCK)
		{
			indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			try
			{
				writer = new IndexWriter(directory, indexWriterConfig);
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
		BooleanQuery query = new BooleanQuery();
		Term term = new Term("path",path);
		query.add(new TermQuery(term),BooleanClause.Occur.MUST);
		IndexWriter writer = null;
		synchronized (LOCK)
		{
			indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			try
			{
				writer = new IndexWriter(directory, indexWriterConfig);
				long frequency = getFrequency(path);
				if (frequency == 0)
					return;
				writer.deleteDocuments(term);
				writer.addDocument(documentFactory.createDocument(path, frequency + 1));
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

	private static class FrequencySearch
	{
		private final BytesRef bytes;
		private final IndexSearcher searcher;
		private final BooleanQuery query;
		private final Term term;

		private FrequencySearch(IndexReader reader)
		{
			searcher = new IndexSearcher(reader);
			bytes = new BytesRef(0);
			term = new Term("path", bytes);
			query = new BooleanQuery();
			query.add(new TermQuery(term), BooleanClause.Occur.MUST);
		}

		public long getFrequency(CharSequence path) throws IOException
		{
			long frequency = 0L;
			try
			{
				TopDocs search = searcher.search(query,1);
				bytes.copyChars(path);
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
		}
	}

	private static class DocumentFactory
	{
		private final LongField frequency;
		private final StringField name_caps;
		private final TextField name;
		private final StringField path;
		private final Document document;

		private DocumentFactory()
		{
			document = new Document();
			path = new StringField("path", "", Field.Store.YES);
			document.add(path);
			name = new TextField("name", "", Field.Store.NO);
			document.add(name);
			name_caps = new StringField("name_caps", "", Field.Store.NO);
			document.add(name_caps);
			frequency = new LongField("frequency", 0L, Field.Store.YES);
			document.add(frequency);
		}

		// createDocument() method
		public Document createDocument(String path, long frequency)
		{
			String fileName = MiscUtilities.getFileName(path);
			this.path.setStringValue(path);
			name.setStringValue(fileName);
			name_caps.setStringValue(fileName);
			this.frequency.setLongValue(frequency);
			return document;
		} //}}}
	}
}
