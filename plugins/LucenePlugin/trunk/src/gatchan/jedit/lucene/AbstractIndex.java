package gatchan.jedit.lucene;

import org.gjt.sp.util.Log;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;

import java.io.IOException;
import java.io.File;

import lucene.SourceCodeAnalyzer;

/**
 * Created by IntelliJ IDEA.
 * User: kpouer
 * Date: 11 juin 2009
 * Time: 17:27:30
 * To change this template use File | Settings | File Templates.
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
