/*
 * AbstractIndex.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, 2011 Matthieu Casanova
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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

import gatchan.jedit.lucene.Index.ActivityListener;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Matthieu Casanova
 */
public class AbstractIndex
{
	protected IndexWriter writer;
	private IndexReader reader;
	protected File path;
	protected Analyzer analyzer;
	protected Vector<ActivityListener> listeners = new Vector<ActivityListener>();
	private final Map<IndexReader, Integer> readerMap = new ConcurrentHashMap<IndexReader, Integer>();

	public AbstractIndex(File path)
	{
		this.path = path;
	}

	public void close()
	{
		Log.log(Log.DEBUG, this, "close()");
		closeWriter();
		Set<IndexReader> readerSet = readerMap.keySet();
		Iterator<IndexReader> it = readerSet.iterator();
		while (it.hasNext())
		{
			IndexReader indexReader = it.next();
			try
			{
				indexReader.close();
				it.remove();
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Unable to close reader", e);
			}
		}
		if (reader != null)
		{
			try
			{
				reader.close();
				reader = null;
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Unable to close reader", e);
			}
		}
		if (!readerMap.isEmpty() || reader != null)
		{
			Log.log(Log.DEBUG, this, "The index was not closed");
		}
	}

	protected void openWriter()
	{
		if (writer != null)
			return;
		try
		{
			path.mkdirs();
			FSDirectory directory = FSDirectory.open(path);
			if (IndexWriter.isLocked(directory))
			{
				Log.log(Log.WARNING, this, "The lucene index at " + path + " is locked");
				int ret = GUIUtilities.confirm(jEdit.getActiveView(), "lucene.index.locked",
				                               new Object[]{path}, JOptionPane.YES_NO_OPTION,
				                               JOptionPane.ERROR_MESSAGE);
				if (ret == JOptionPane.YES_OPTION)
				{
					IndexWriter.unlock(directory);
				}
			}
			writer = new IndexWriter(directory, getAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Unable to open IndexWriter", e);
		}
	}

	protected IndexSearcher getSearcher()
	{
		initReader();
		return new MyIndexSearcher(reader);
	}

	private void initReader()
	{
		if (reader == null)
		{
			try
			{
				reader = IndexReader.open(FSDirectory.open(path));
				readerMap.put(reader, 0);
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Unable to open IndexReader", e);
			}
		}
		else
		{
			try
			{
				IndexReader reader = this.reader.reopen();
				if (reader != this.reader)
				{
					IndexReader oldReader = this.reader;
					readerMap.put(reader, 0);
					this.reader = reader;
					int count = readerMap.get(oldReader);
					if (count == 0)
					{
						try
						{
							readerMap.remove(oldReader);
							oldReader.close();
						}
						catch (IOException e)
						{
							Log.log(Log.ERROR, "Error while closing the previous reader", e);
						}
					}
				}
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Unable to open reopen IndexReader", e);
			}
		}
	}

	protected void closeWriter()
	{
		if (writer != null)
		{
			try
			{
				writer.close();
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Unable to close IndexWriter", e);
			}
			writer = null;
		}
	}

	public boolean isOptimized()
	{
		initReader();
		return reader == null || reader.isOptimized();
	}

	/**
	 * Optimize the index.
	 * It is necessary to commit after that
	 */
	public void optimize()
	{
		openWriter();
		if (writer != null)
		{
			try
			{
				writer.optimize();
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Error while optimizing index", e);
			}
		}
	}

	public void commit()
	{
		if (writer != null)
		{
			try
			{
				writer.commit();
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Error while commiting index", e);
			}
		}
	}

	private void readPlus(IndexReader reader)
	{
		int count = readerMap.get(reader);
		readerMap.put(reader, count + 1);
	}

	private void readMinus(IndexReader reader)
	{
		int count = readerMap.get(reader) - 1;
		readerMap.put(reader, count);
		if (count == 0 && reader != this.reader)
		{
			IOUtilities.closeQuietly(reader);
			readerMap.remove(reader);
		}
	}

	private class MyIndexSearcher extends IndexSearcher
	{
		private MyIndexSearcher(IndexReader r)
		{
			super(r);
			readPlus(r);
		}

		@Override
		public void close() throws IOException
		{
			super.close();
			readMinus(reader);
		}
	}

	public void setAnalyzer(Analyzer analyzer)
	{
		this.analyzer = analyzer;
	}

	public Analyzer getAnalyzer()
	{
		if (analyzer == null)
			analyzer = new SourceCodeAnalyzer();
		return analyzer;
	}

	protected static void closeSearcher(IndexSearcher searcher)
	{
		try
		{
			searcher.close();
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, AbstractIndex.class, e, e);
		}
	}

	public void addActivityListener(ActivityListener al)
	{
		listeners.add(al);
	}

	public void removeActivityListener(ActivityListener al)
	{
		listeners.remove(al);
	}
}
