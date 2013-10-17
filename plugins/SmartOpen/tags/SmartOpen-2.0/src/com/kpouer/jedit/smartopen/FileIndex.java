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
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.apache.lucene.index.MultiFields;
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
import org.apache.lucene.util.Bits;
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
public class FileIndex implements Closeable
{
	private static final Pattern DOTSPLIT = Pattern.compile("\\.");
	private static final Pattern CAMELCASE = Pattern.compile("(?<!^)(?=[A-Z])");
	private final Directory directory;
	private final Object LOCK = new Object();
	@Nullable
	private final VPTProject project;

	private DirectoryReader reader;

	private final DocumentFactory documentFactory;
	private final IndexWriterConfig indexWriterConfig;

	//{{{ FileIndex constructor
	public FileIndex(@Nullable VPTProject project)
	{
		this.project = project;
		directory = getDirectory();
		indexWriterConfig = new IndexWriterConfig(Version.LUCENE_42, new StandardAnalyzer(Version.LUCENE_42));
		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		documentFactory = new DocumentFactory();
	} //}}}

	private void initReader() throws IOException
	{
		synchronized (LOCK)
		{
			if (reader == null)
				reader = DirectoryReader.open(directory);
			else
			{
				DirectoryReader newReader = DirectoryReader.openIfChanged(reader);
				if (newReader != null)
				{
					reader.close();
					reader = newReader;
				}
			}
		}
	}

	@Override
	public void close()
	{
		IOUtilities.closeQuietly (reader);
		reader = null;
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
	public List<String> getFiles(String s, String extension)
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
			queryCaps.setBoost(10.0F);
			s = s.toLowerCase();
			Query queryNoCaps = new WildcardQuery(new Term("name", '*' + s + '*'));

			BooleanQuery nameQuery = new BooleanQuery();
			nameQuery.add(queryCaps, BooleanClause.Occur.SHOULD);
			nameQuery.add(queryNoCaps, BooleanClause.Occur.SHOULD);

			BooleanQuery query;
			if (extension.isEmpty())
			{
				query = nameQuery;
			}
			else
			{
				query = new BooleanQuery();
				query.add(nameQuery, BooleanClause.Occur.MUST);
				query.add(new TermQuery(new Term("extension", extension.toLowerCase())), BooleanClause.Occur.MUST);
			}
			if (reader == null)
				initReader();
			IndexSearcher searcher = new IndexSearcher(reader);
			TopDocs search = searcher.search(query, 100, sort);
			ScoreDoc[] scoreDocs = search.scoreDocs;
			Set<String> fields = Collections.singleton("path");
			for (ScoreDoc scoreDoc : scoreDocs)
			{
				Document doc = searcher.doc(scoreDoc.doc, fields);
				String path = doc.get("path");
				l.add(path);
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
		if (reader == null)
		{
			try
			{
				initReader();
			}
			catch (IOException e)
			{
				return 0;
			}
		}
		return new FrequencySearch(reader).getFrequency(path);
	} //}}}

	//{{{ addFiles() method
	public void addFiles(FileProvider fileProvider, ProgressObserver observer)
	{
		addFiles(fileProvider, observer, false);
	}
	/**
	 * Index files.
	 * @param fileProvider the file provider to index
	 * @param observer the progress observer
	 */
	public void addFiles(FileProvider fileProvider, ProgressObserver observer, boolean append)
	{
		long start = System.currentTimeMillis();
		observer.setMaximum(fileProvider.size());
		Pattern exclude = SmartOpenOptionPane.globToPattern(jEdit.getProperty("options.smartopen.ExcludeGlobs"));
		synchronized (LOCK)
		{
			IndexWriter writer = null;
			try
			{
				writer = new IndexWriter(directory, indexWriterConfig);

				Collection<String> knownFiles;
				if (append)
				{
					knownFiles = Collections.emptyList();
				}
				else
					knownFiles = getExistingFiles();

				for (int i = 0; i < fileProvider.size(); i++)
				{
					String path = fileProvider.next();

					observer.setValue(i);
					if (i % 10 == 0)
						observer.setStatus(path);

					if (!exclude.matcher(path).matches())
					{
						if (knownFiles.contains(path))
						{
							knownFiles.remove(path);
						}
						else
						{
							writer.addDocument(documentFactory.createDocument(path, 1));
						}
					}

					// iterate over documents that are still here but are not part of the project anymore
				}
				for (String remainingFile : knownFiles)
				{
					writer.deleteDocuments(new Term("path", remainingFile));
				}
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
		try
		{
			initReader();
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		long end = System.currentTimeMillis();
		Log.log(Log.MESSAGE, this, "Added " + fileProvider.size()+" files in "+(end - start) + "ms");
	} //}}}

	private Collection<String> getExistingFiles() throws IOException
	{
		Collection<String> knownFiles = new HashSet<>(10000);
		try
		{
			initReader();
		}
		catch (IOException e)
		{
			// ignore
		}
		if (reader != null)
		{
			Set<String> fields = Collections.singleton("path");
			Bits liveDocs = MultiFields.getLiveDocs(reader);
			for (int i = 0; i < reader.maxDoc(); i++)
			{
				if (liveDocs == null || liveDocs.get(i))
				{
					Document doc = reader.document(i, fields);
					String path = doc.get("path");
					knownFiles.add(path);
				}
			}
		}
		return knownFiles;
	}

	//{{{ removeFiles() method
	public void removeFiles(FileProvider fileProvider, ProgressObserver observer)
	{
		long start = System.currentTimeMillis();
		IndexWriter writer = null;
		observer.setMaximum(fileProvider.size());

		synchronized (LOCK)
		{
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
		try
		{
			initReader();
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		long end = System.currentTimeMillis();
		Log.log(Log.MESSAGE, this, "Removed " + fileProvider.size()+" files in "+(end - start) + "ms");
	} //}}}

	//{{{ updateFrequency() method
	public void updateFrequency(String path)
	{
		Term term = new Term("path",path);
		synchronized (LOCK)
		{
			IndexWriter writer = null;
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
			try
			{
				initReader();
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, e);
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

	public void resetFrequency()
	{
		long start = System.currentTimeMillis();
		try
		{
			Collection<String> existingFiles = getExistingFiles();

			synchronized (LOCK)
			{
				IndexWriter writer = null;
				try
				{
					writer = new IndexWriter(directory, indexWriterConfig);
					for (String path : existingFiles)
					{
						Term term = new Term("path", path);
						writer.deleteDocuments(term);
						writer.addDocument(documentFactory.createDocument(path, 1));
					}
				}
				catch (Exception e)
				{
					Log.log(Log.ERROR, this, e);
				}
				finally
				{
					IOUtilities.closeQuietly(writer);
				}
				try
				{
					initReader();
				}
				catch (IOException e)
				{
					Log.log(Log.ERROR, this, e);
				}
			}
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		long end = System.currentTimeMillis();
		Log.log(Log.MESSAGE, this, "Frequency cache resetted in "+(end - start) + "ms");
	}



	private static class FrequencySearch
	{
		private final IndexSearcher searcher;
		private final Set<String> frequencyField;

		private FrequencySearch(IndexReader reader)
		{
			searcher = new IndexSearcher(reader);
			frequencyField = Collections.singleton("frequency");
		}

		public long getFrequency(String path) throws IOException
		{
			long frequency = 0L;
			try
			{
				TopDocs search = searcher.search(new TermQuery(new Term("path", path)),1);
				if (search.scoreDocs.length == 1)
				{
					Document doc = searcher.doc(search.scoreDocs[0].doc, frequencyField);
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
		private final StringField fileExtension;

		private DocumentFactory()
		{
			document = new Document();
			path = new StringField("path", "", Field.Store.YES);
			document.add(path);
			name = new TextField("name", "", Field.Store.NO);
			document.add(name);
			name_caps = new StringField("name_caps", "", Field.Store.NO);
			document.add(name_caps);
			fileExtension = new StringField("extension", "", Field.Store.NO);
			document.add(fileExtension);
			frequency = new LongField("frequency", 1L, Field.Store.YES);
			document.add(frequency);
		}

		// createDocument() method
		public Document createDocument(String path, long frequency)
		{
			String fileName = MiscUtilities.getFileName(path);
			this.path.setStringValue(path);
			name.setStringValue(fileName);
			name_caps.setStringValue(fileName);
			String extension = MiscUtilities.getFileExtension(path).toLowerCase();
			if (extension.startsWith("."))
				extension = extension.substring(1);
			fileExtension.setStringValue(extension);
			this.frequency.setLongValue(frequency);
			return document;
		} //}}}
	}
}
