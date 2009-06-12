/*
 * CentralIndex.java - The Central Index
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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.msg.BufferUpdate;

import java.io.File;
import java.io.IOException;

/**
 * @author Matthieu Casanova
 */
public class CentralIndex extends AbstractIndex implements EBComponent
{
	public CentralIndex(File indexFile)
	{
		super(indexFile);
	}

	void createIndex(Index index)
	{

	}

	void deleteIndex(String name)
	{
		openWriter();
		try
		{
			writer.deleteDocuments(new Term("indexName", name));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		commit();
	}

	void addFile(String path, String indexName)
	{
		openWriter();
		openSearcher();

		try
		{
			BooleanQuery query = getPathIndexQuery(path, indexName);
			TopDocs docs = searcher.search(query, 1);
			if (docs.scoreDocs.length == 0)
			{
				Document document = new Document();
				document.add(new Field("path", path, Field.Store.YES, Field.Index.NOT_ANALYZED));
				document.add(new Field("indexName", indexName, Field.Store.YES, Field.Index.NOT_ANALYZED));
				writer.addDocument(document);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private BooleanQuery getPathIndexQuery(String path, String indexName)
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
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void handleMessage(EBMessage message)
	{
		if (message instanceof BufferUpdate)
		{
			BufferUpdate bufferUpdate = (BufferUpdate) message;
			if (bufferUpdate.getWhat() == BufferUpdate.SAVED)
			{
				fileUpdated(bufferUpdate.getBuffer());
			}
		}
	}

	private void fileUpdated(Buffer buffer)
	{
		try
		{
			Query query = new TermQuery(new Term("path", buffer.getPath()));
			openSearcher();
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
			e.printStackTrace();
		}
	}
}
