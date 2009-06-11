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

import org.gjt.sp.util.Log;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;

import java.io.IOException;
import java.io.File;

import lucene.SourceCodeAnalyzer;

/**
 * @author Matthieu Casanova
 */
public class AbstractIndex
{
	protected IndexWriter writer;
	protected Searcher searcher;
	protected File path;

	public AbstractIndex(File path)
	{
		this.path = path;
	}

	public void close()
	{
		closeWriter();
		if (searcher != null)
		{
			try
			{
				searcher.close();
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Unable to close searcher", e);
			}
			searcher = null;
		}
	}

	protected void openWriter()
	{
		if (writer != null)
			return;
		try
		{
			path.mkdirs();
			writer = new IndexWriter(path, new SourceCodeAnalyzer(), IndexWriter.MaxFieldLength.LIMITED);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Unable to open IndexWriter", e);
		}
	}

	protected void openSearcher()
	{
		if (searcher == null)
		{
			try
			{
				searcher = new IndexSearcher(path.getPath());
			}
			catch (IOException e)
			{
				e.printStackTrace();
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

	public void commit()
	{
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
			closeWriter();
		}
		if (searcher != null)
		{
			try
			{
				searcher.close();
				searcher = null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
