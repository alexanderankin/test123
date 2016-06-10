/*
 * jEdit - Programmer's Text Editor
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011-2016 Matthieu Casanova
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
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.kpouer.jedit.smartopen.indexer.FileProvider;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Bits;
import org.gjt.sp.jedit.EditPlugin;
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

	//{{{ FileIndex constructor
	public FileIndex(@Nullable VPTProject project)
	{
		this.project = project;
		directory = getDirectory();
		documentFactory = new DocumentFactory();
	} //}}}

  private IndexWriterConfig getIndexWriterConfig()
  {
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
    indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    return indexWriterConfig;
  }

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
			Query queryCaps = new WildcardQuery(new Term(DocumentFactory.FIELD_NAME_CAPS, builder.toString()));
			queryCaps.setBoost(10.0F);
			s = s.toLowerCase();
			Query queryNoCaps = new WildcardQuery(new Term(DocumentFactory.FIELD_NAME, '*' + s + '*'));

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
				query.add(new TermQuery(new Term(DocumentFactory.FIELD_EXTENSION, extension.toLowerCase())), BooleanClause.Occur.MUST);
			}
			if (reader == null)
				initReader();
			IndexSearcher searcher = new IndexSearcher(reader);

            SortField sortField = new SortedNumericSortField(DocumentFactory.FIELD_FREQUENCY, SortField.Type.LONG, true);
            Sort sort = new Sort(sortField);

            TopDocs search = searcher.search(query, 100, sort);
			ScoreDoc[] scoreDocs = search.scoreDocs;
			Set<String> fields = Collections.singleton(DocumentFactory.FIELD_PATH);
			for (ScoreDoc scoreDoc : scoreDocs)
			{
				Document doc = searcher.doc(scoreDoc.doc, fields);
				String path = doc.get(DocumentFactory.FIELD_PATH);
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
				return 0L;
			}
		}
		return new FrequencySearch(reader).getFrequency(path);
	} //}}}

	//{{{ addFiles() method
	public void addFiles(FileProvider fileProvider, ProgressObserver observer)
	{
		addFiles(fileProvider, false);
	}
	/**
	 * Index files.
     * @param fileProvider the file provider to index
     */
	public void addFiles(FileProvider fileProvider, boolean append)
	{
        long added = 0;
		long start = System.currentTimeMillis();
		Pattern exclude = SmartOpenOptionPane.globToPattern(jEdit.getProperty("options.smartopen.ExcludeGlobs"));
		synchronized (LOCK)
		{
			try(IndexWriter writer = new IndexWriter(directory, getIndexWriterConfig()))
			{
				Collection<String> knownFiles;
				if (append)
				{
					knownFiles = Collections.emptyList();
				}
				else
					knownFiles = Collections.synchronizedCollection(getExistingFiles());

                added = fileProvider.stream().parallel().filter(path -> !exclude.matcher(path).matches()).map(new Function<String, Void>()
                {
                    @Override
                    public Void apply(String path)
                    {
                        if (knownFiles.contains(path))
                            knownFiles.remove(path);
                        else
                        {
                            try
                            {
                                writer.addDocument(documentFactory.createDocument(path, 1));
                            }
                            catch (IOException e1)
                            {
                                Log.log(Log.ERROR, this, e1);
                            }
                        }
                        return null;
                    }
                }).count();

                // iterate over documents that are still here but are not part of the project anymore
                knownFiles.stream().parallel().map(new Function<String, Void>()
                {
                    @Override
                    public Void apply(String remainingFile)
                    {
                        try
                        {
                            writer.deleteDocuments(new Term(DocumentFactory.FIELD_PATH, remainingFile));
                        }
                        catch (IOException e)
                        {
                            Log.log(Log.ERROR, this, e);
                        }
                        return null;
                    }
                }).count();
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, e);
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
		Log.log(Log.MESSAGE, this, "Added " + added + " files in "+(end - start) + "ms");
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
			Set<String> fields = Collections.singleton(DocumentFactory.FIELD_PATH);
			Bits liveDocs = MultiFields.getLiveDocs(reader);
			for (int i = 0; i < reader.maxDoc(); i++)
			{
				if (liveDocs == null || liveDocs.get(i))
				{
					Document doc = reader.document(i, fields);
					String path = doc.get(DocumentFactory.FIELD_PATH);
					knownFiles.add(path);
				}
			}
		}
		return knownFiles;
	}

	//{{{ removeFiles() method
	public void removeFiles(FileProvider fileProvider, ProgressObserver observer)
	{
        long removed = 0;
		long start = System.currentTimeMillis();
		synchronized (LOCK)
		{
			try(IndexWriter writer = new IndexWriter(directory, getIndexWriterConfig()))
			{
                removed = fileProvider.stream().parallel().map(new Function<String, Void>()
                {
                    @Override
                    public Void apply(String path)
                    {
                        observer.setStatus(path);
                        try
                        {
                            writer.deleteDocuments(new Term(DocumentFactory.FIELD_PATH, path));
                        }
                        catch (IOException e)
                        {
                            Log.log(Log.ERROR, this, e);
                        }
                        return null;
                    }
                }).count();
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, e);
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
		Log.log(Log.MESSAGE, this, "Removed " + removed + " files in "+(end - start) + "ms");
	} //}}}

	//{{{ updateFrequency() method
	public void updateFrequency(String path)
	{
		Term term = new Term(DocumentFactory.FIELD_PATH,path);
		synchronized (LOCK)
		{
			try(IndexWriter writer = new IndexWriter(directory, getIndexWriterConfig()))
			{
				long frequency = getFrequency(path);
				if (frequency == 0L)
					return;
				writer.deleteDocuments(term);
				writer.addDocument(documentFactory.createDocument(path, frequency + 1L));
			}
			catch (Exception e)
			{
				Log.log(Log.ERROR, this, e);
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
				tempDirectory = FSDirectory.open(index.toPath());
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
				try(IndexWriter writer = new IndexWriter(directory, getIndexWriterConfig()))
				{
					for (String path : existingFiles)
					{
						Term term = new Term(DocumentFactory.FIELD_PATH, path);
						writer.deleteDocuments(term);
						writer.addDocument(documentFactory.createDocument(path, 1L));
					}
				}
				catch (Exception e)
				{
					Log.log(Log.ERROR, this, e);
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
			frequencyField = Collections.singleton(DocumentFactory.FIELD_FREQUENCY_STORED);
		}

		public long getFrequency(String path) throws IOException
		{
			long frequency = 0L;
			try
			{
				TopDocs search = searcher.search(new TermQuery(new Term(DocumentFactory.FIELD_PATH, path)),1);
				if (search.scoreDocs.length == 1)
				{
					Document doc = searcher.doc(search.scoreDocs[0].doc, frequencyField);
					IndexableField frequencyField = doc.getField(DocumentFactory.FIELD_FREQUENCY_STORED);
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

}
