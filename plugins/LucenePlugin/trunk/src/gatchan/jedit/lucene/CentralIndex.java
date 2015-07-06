/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, 2013 Matthieu Casanova
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ThreadUtilities;

/**
 * This index will contains documents to link files to indexes. Each document has the following fields : "indexName" and
 * "path"
 *
 * @author Matthieu Casanova
 */
public class CentralIndex extends AbstractIndex
{
	CentralIndex(File indexFile)
	{
		super(indexFile);
	}

	@EBHandler
	public void handleBufferUpdate(BufferUpdate message)
	{
		/* test if the central index exists. If it doesnt exist, no need to start working
		   it is possible that it doesn't exist if the plugin is newly installed
		   and/or no index has been created yet */
		try
		{
			if (DirectoryReader.indexExists(FSDirectory.open(path.toPath())))
			{
				final BufferUpdate bufferUpdate = message;
				if (bufferUpdate.getWhat() == BufferUpdate.SAVED)
				{
					ThreadUtilities.runInBackground(new Runnable()
					{
						@Override
						public void run()
						{
							try 
							{
								fileUpdated(bufferUpdate.getBuffer());
							} 
							catch (IndexInterruptedException e) 
							{
								Log.log(Log.WARNING, this, "Indexing Halted by user");
								Thread.currentThread().interrupt();
								return;
							}
						}
					});
				}
			}
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
	}

	static void createIndex(Index index)
	{
		EditBus.send(new LuceneIndexUpdate(index.getName(), LuceneIndexUpdate.What.CREATED));
	}

	void removeIndex(String name)
	{
		openWriter();
		try
		{
			if (writer == null)
			{
				JOptionPane.showMessageDialog(jEdit.getActiveView(),
							      "Error: Can't complete removal of index " + name
							      + ": Could not open meta-index.");
			}
			else
			{
				writer.deleteDocuments(new Term("indexName", name));
			}
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		commit();
		EditBus.send(new LuceneIndexUpdate(name, LuceneIndexUpdate.What.DESTROYED));
	}

	/**
	 * Add a file to the index.
	 *
	 * @param path      the path to add
	 * @param indexName the index that contains this path
	 * @throws IndexInterruptedException 
	 */
	void addFile(String path, String indexName) throws IndexInterruptedException
	{
		openWriter();

		IndexSearcher searcher = getSearcher();
		try
		{
			BooleanQuery query = getPathIndexQuery(path, indexName);
			TopDocs docs = searcher.search(query, 1);
			if (docs.scoreDocs.length == 0)
			{
				Log.log(Log.DEBUG, this, "Adding to Central Index Filename = " + path);
				Document document = new Document();
				document.add(new StringField("path", path, Field.Store.YES));
				document.add(new StringField("indexName", indexName, Field.Store.YES));
				writer.addDocument(document);
			}
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
	}

	List<String> getAllDocuments(String indexName) throws IndexInterruptedException
	{
		final IndexSearcher searcher = getSearcher();
		final List<String> documents = new ArrayList<String>();
		try
		{
			searcher.search(new TermQuery(new Term("indexName", indexName)), new SimpleCollector()
			{
				@Override
				public void setScorer(Scorer scorer)
				{
				}

				@Override
				public void collect(int doc)
				{
					try
					{
						Document document = searcher.doc(doc);
						documents.add(document.getField("path").stringValue());
					}
					catch (IOException e)
					{
						Log.log(Log.ERROR, this, e);
					}
				}

				@Override
				public boolean needsScores()
				{
					return false;
				}
			});
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		return documents;
	}

	private static BooleanQuery getPathIndexQuery(String path, String indexName)
	{
		BooleanQuery query = new BooleanQuery();
		query.add(new BooleanClause(new TermQuery(new Term("path", path)), BooleanClause.Occur.MUST));
		query.add(new BooleanClause(new TermQuery(new Term("indexName", indexName)), BooleanClause.Occur.MUST));
		return query;
	}

	void removeFile(String path, String indexName)
	{
		openWriter();
		try
		{
			Query query = getPathIndexQuery(path, indexName);
			writer.deleteDocuments(query);
			writer.forceMergeDeletes(false);
			writer.commit();
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
	}

	private void fileUpdated(Buffer buffer) throws IndexInterruptedException
	{
		IndexSearcher searcher = getSearcher();
		try
		{
			Query query = new TermQuery(new Term("path", buffer.getPath()));
			TopDocs docs = searcher.search(query, 100);
			ScoreDoc[] scoreDocs = docs.scoreDocs;
			for (ScoreDoc doc : scoreDocs)
			{
				Document document = searcher.doc(doc.doc);
				String indexName = document.getField("indexName").stringValue();
				Index index = LucenePlugin.instance.getIndex(indexName);
				if (index != null)
				{
					index.addFile(document.getField("path").stringValue());
					index.commit();
				}
			}
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
	}
}
