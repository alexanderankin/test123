/*
 * AbstractIndex.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009 Matthieu Casanova
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

import lucene.SourceCodeAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.FSDirectory;
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
		closeWriter();
		Set<IndexReader> readerSet = readerMap.keySet();
		for (IndexReader indexReader : readerSet)
		{
			try
			{
				indexReader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if (reader != null)
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Unable to close reader", e);
			}
		}
	}

	protected void openWriter()
	{
		if (writer != null)
			return;
		try
		{
			path.mkdirs();
			if (IndexWriter.isLocked(path.getAbsolutePath()))
			{
				Log.log(Log.WARNING, this, "The lucene index at " + path + " is locked");
				int ret = GUIUtilities.confirm(jEdit.getActiveView(), "lucene.index.locked",
				                               new Object[]{path}, JOptionPane.YES_NO_OPTION,
				                               JOptionPane.ERROR_MESSAGE);
				if (ret == JOptionPane.YES_OPTION)
				{
					IndexWriter.unlock(FSDirectory.getDirectory(path));
				}
			}
			writer = new IndexWriter(path, getAnalyzer(), IndexWriter.MaxFieldLength.LIMITED);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Unable to open IndexWriter", e);
		}
	}

	protected IndexSearcher getSearcher()
	{
		if (reader == null)
		{
			try
			{
				reader = IndexReader.open(path.getPath());
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
					readerMap.put(reader, 0);
					this.reader = reader;
				}
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Unable to open reopen IndexReader", e);
			}
		}
		return new MyIndexSearcher(reader);
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
				Log.log(Log.ERROR, this, "Error while optimizing index", e);
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
			try
			{
				reader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
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

	protected static void closeSearcher(Searcher searcher)
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
