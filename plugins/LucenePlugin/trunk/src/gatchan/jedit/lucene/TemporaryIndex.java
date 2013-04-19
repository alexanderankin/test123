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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ProgressObserver;

import javax.swing.text.Segment;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author Matthieu Casanova
 */
public class TemporaryIndex implements Index
{
	protected IndexWriter writer;
	protected Analyzer analyzer;

	private Directory directory;
	private final String name;

	public TemporaryIndex(String name)
	{
		this.name = name;
		analyzer = new StandardAnalyzer(Version.LUCENE_42);
		directory = new RAMDirectory();
		try
		{
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_42, analyzer);
			writer = new IndexWriter(directory, indexWriterConfig);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
	}

	@Override
	public void clear()
	{
		if (writer != null)
		{
			try
			{
				writer.close();
				directory.close();
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, e);
			}
		}
		directory = new RAMDirectory();
		try
		{
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_42, analyzer);
			writer = new IndexWriter(directory, indexWriterConfig);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
	}

	@Override
	public void close()
	{
		try
		{
			if (writer != null)
				writer.close();
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
	}

	@Override
	public void commit()
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

	@Override
	public void reindex(ProgressObserver progressObserver)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public Analyzer getAnalyzer()
	{
		return analyzer;
	}

	@Override
	public void addFile(String path)
	{
		Buffer buffer = jEdit.getBuffer(path);
		if (buffer != null)
		{
			Document doc = getEmptyDocument(buffer);
			try
			{
				writer.addDocument(doc);
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, e);
			}
		}
	}

	@Override
	public void addFiles(FileProvider files, ProgressObserver progressObserver)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeFile(String path)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAnalyzer(Analyzer analyzer)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void search(String query, String fileType, int max, ResultProcessor processor)
	{
		if (max < 1)
			max = 1;
		IndexSearcher searcher = null;
		DirectoryReader directoryReader = null;
		try
		{
			directoryReader = DirectoryReader.open(directory);
			searcher = new IndexSearcher(directoryReader);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		if (searcher == null)
			return;
		QueryParser parser =
			new MultiFieldQueryParser(Version.LUCENE_42, new String[] { "path", "content" }, getAnalyzer());
		try
		{
			Query parsedQuery = parser.parse(query);

			BooleanQuery _query = new BooleanQuery();
			_query.add(parsedQuery, BooleanClause.Occur.MUST);
			if (!fileType.isEmpty())
			{
				_query.add(new TermQuery(new Term("filetype", fileType)), BooleanClause.Occur.MUST);
			}
			_query.add(parsedQuery, BooleanClause.Occur.MUST);
			TopDocs docs = searcher.search(_query, max);

			ScoreDoc[] scoreDocs = docs.scoreDocs;
			Result result = new Result();
			Query _textQuery = parser.parse(query);
			for (ScoreDoc doc : scoreDocs)
			{
				Document document = searcher.doc(doc.doc);
				result.setDocument(document);
				if (!processor.process(_textQuery, doc.score, result))
				{
					break;
				}
			}
		}
		catch (ParseException e)
		{
			Log.log(Log.ERROR, this, e, e);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e, e);
		}
		finally
		{
			try
			{
				directoryReader.close();
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Error while closing searcher", e);
			}
		}
	}

	@Override
	public void addActivityListener(ActivityListener al)
	{
	}

	@Override
	public void removeActivityListener(ActivityListener al)
	{
	}

	@Override
	public boolean isChanging()
	{
		return false;
	}

	protected static Document getEmptyDocument(Buffer buffer)
	{
		Document doc = new Document();
		doc.add(new TextField("path", buffer.getPath(), Field.Store.NO));
		doc.add(new StringField("_path", buffer.getPath(), Field.Store.YES));
		String extension = MiscUtilities.getFileExtension(buffer.getPath());
		if (!extension.isEmpty())
		{
			doc.add(new StringField("filetype", extension.substring(1), Field.Store.NO));
		}
		Segment segment = new Segment();
		buffer.getText(0, buffer.getLength(), segment);
		Reader reader = new CharArrayReader(segment.array, segment.offset, segment.count);
		doc.add(new TextField("content", reader));
		return doc;
	}
}
