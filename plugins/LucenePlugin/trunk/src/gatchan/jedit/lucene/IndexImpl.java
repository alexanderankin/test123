/*
 * IndexImpl.java - The Index implementation
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
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanScorer;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;

import java.io.*;

/**
 * @author Matthieu Casanova
 */
public class IndexImpl implements Index
{
	private IndexWriter writer;

	private Searcher searcher;

	private String name;
	private File path;


	public IndexImpl(String name, File path)
	{
		this.name = name;
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

	public String getName()
	{
		return name;
	}

	public void addFile(String path)
	{
		openWriter();
		if (writer == null)
			return;
		Object session = null;
		VFS vfs = VFSManager.getVFSForPath(path);
		try
		{
			session = vfs.createVFSSession(path, jEdit.getActiveView());

			VFSFile vfsFile = vfs._getFile(session, path, jEdit.getActiveView());
			addFile(vfsFile, session);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Unable to add document " + path, e);
		}
		finally
		{
			try
			{
				vfs._endVFSSession(session, jEdit.getActiveView());
			}
			catch (IOException e)
			{
			}
		}
	}

	private void addFile(VFSFile file, Object session)
	{
		if (file.getType() == VFSFile.DIRECTORY)
		{
			try
			{
				VFSFile[] vfsFiles = file.getVFS()._listFiles(session, file.getPath(), jEdit.getActiveView());
				for (VFSFile vfsFile : vfsFiles)
				{
					addFile(vfsFile, session);
				}
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Unable to lsit directory " + file.getPath(), e);
			}
		}
		else if (file.getType() == VFSFile.FILE)
		{
			addDocument(file, session);
		}
	}

	public void removeFile(String path)
	{
		openWriter();
		if (writer == null)
			return;
		try
		{
			writer.deleteDocuments(new Term("path", path));
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Unable to delete document " + path, e);
		}
	}

	public void search(String query)
	{
		openSearcher();
		if (searcher == null)
			return;
		SourceCodeAnalyzer analyzer = new SourceCodeAnalyzer();
		QueryParser parser = new MultiFieldQueryParser(new String[]{"path", "content"}, analyzer);
		try
		{
			Query _query = parser.parse(query);
			TopDocs docs = searcher.search(_query, 100);
			ScoreDoc[] scoreDocs = docs.scoreDocs;
			Highlighter highlighter = new Highlighter(new QueryScorer(_query));
			for (ScoreDoc doc : scoreDocs)
			{
				Document document = searcher.doc(doc.doc);
				Field field = document.getField("content");
//				analyzer.tokenStream("content", new StringReader())
				System.out.println(document.getField("path").stringValue());
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
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
			}
			catch (IOException e)
			{
				e.printStackTrace();  
			}
		}
	}

	private void addDocument(VFSFile file, Object session)
	{
		Document doc = new Document();
		doc.add(new Field("path", file.getPath(), Field.Store.YES, Field.Index.ANALYZED));
		Reader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(file.getVFS()._createInputStream(session, file.getPath(),
			                                                                                   false,
			                                                                                   jEdit.getActiveView())));
			doc.add(new Field("content", reader));
			writer.updateDocument(new Term("path", file.getPath()), doc);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Unable to read file " + path, e);
		}
		finally
		{
			IOUtilities.closeQuietly(reader);
		}
	}

	private void openWriter()
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

	private void openSearcher()
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

	private void closeWriter()
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
}
