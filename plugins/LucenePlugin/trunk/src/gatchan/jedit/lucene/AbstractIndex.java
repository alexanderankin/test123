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

//{{{ Imports
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//}}}

/**
 * @author Matthieu Casanova
 */
public abstract class AbstractIndex
{
	protected IndexWriter writer;
	private DirectoryReader reader;
	protected File path;
	protected Analyzer analyzer;
	protected List<Index.ActivityListener> listeners = new ArrayList<Index.ActivityListener> ();

	//{{{ AbstractIndex constructor
	protected AbstractIndex(File path)
	{
		this.path = path;
	} //}}}

	public void clear()
	{
		close();
	}

	//{{{ close() method
	public void close()
	{
		Log.log(Log.DEBUG, this, "close()");
		closeWriter();

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
	} //}}}

	//{{{ openWriter() method
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
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_42, getAnalyzer());
			writer = new IndexWriter(directory, indexWriterConfig);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Unable to open IndexWriter", e);
		}
	} //}}}

	//{{{ getSearcher() method
	protected IndexSearcher getSearcher()
	{
		initReader();
		return new IndexSearcher(reader);
	} //}}}

	//{{{ initReader() method
	private void initReader()
	{
		if (reader == null)
		{
			try
			{
				reader = DirectoryReader.open(FSDirectory.open(path));
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
				DirectoryReader reader = DirectoryReader.openIfChanged(this.reader);
				if (reader != null)
				{
					this.reader = reader;
				}
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Unable to open reopen IndexReader", e);
			}
		}
	} //}}}

	//{{{ closeWriter() method
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
	} //}}}

	//{{{ commit() method
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
	} //}}}

	//{{{ setAnalyzer() method
	public void setAnalyzer(Analyzer analyzer)
	{
		this.analyzer = analyzer;
	} //}}}

	//{{{ getAnalyzer() method
	public Analyzer getAnalyzer()
	{
		if (analyzer == null)
		{
			analyzer = new SourceCodeAnalyzer();
		}
		return analyzer;
	} //}}}

	//{{{ addActivityListener() method
	public void addActivityListener(Index.ActivityListener al)
	{
		listeners.add(al);
	} //}}}

	//{{{ removeActivityListener() method
	public void removeActivityListener(Index.ActivityListener al)
	{
		listeners.remove(al);
	} //}}}

}
