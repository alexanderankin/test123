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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSFileFilter;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;

import java.io.*;
import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class IndexImpl extends AbstractIndex implements Index
{
	private final String name;
	private static final VFSFileFilter filter = new MyVFSFilter();
	private int writerCount = 0;

	public IndexImpl(String name, File path)
	{
		super(path);
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	private void startActivity()
	{
		synchronized (this)
		{
			writerCount++;
		}
		for (ActivityListener al: listeners)
			al.indexingStarted(this);
	}
	private void endActivity()
	{
		synchronized (this)
		{
			writerCount--;
		}
		for (ActivityListener al: listeners)
			al.indexingEnded(this);
	}
	public synchronized boolean isChanging()
	{
		return (writerCount > 0);
	}

	public void addFiles(VFSFile[] files)
	{
		if (files.length == 0)
			return;
		openWriter();
		if (writer == null)
			return;
		startActivity();
		String path = files[0].getPath();
		VFS vfs = VFSManager.getVFSForPath(path);
		View view = jEdit.getActiveView();
		Object session = vfs.createVFSSession(path, view);
		for (VFSFile file: files)
			addDocument(file, session);
		try
		{
			vfs._endVFSSession(session, view);
		}
		catch (IOException e)
		{
		}
		LucenePlugin.CENTRAL.commit();
		endActivity();
	}

	public void addFile(String path)
	{
		openWriter();
		if (writer == null)
			return;
		Object session = null;
		startActivity();
		VFS vfs = VFSManager.getVFSForPath(path);
		try
		{
			session = vfs.createVFSSession(path, jEdit.getActiveView());

			VFSFile vfsFile = vfs._getFile(session, path, jEdit.getActiveView());
			if (vfsFile == null)
			{
				Log.log(Log.ERROR, this, "Unable to add document " + path + " the file doesn't exists");
				return;
			}
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
			LucenePlugin.CENTRAL.commit();
			endActivity();
		}
	}

	private void addFile(VFSFile file, Object session)
	{
		if (file.getType() == VFSFile.DIRECTORY)
		{
			try
			{
				VFS vfs = file.getVFS();
				String[] files = vfs._listDirectory(session, file.getPath(), filter, true, jEdit.getActiveView(), false, false);
				for (String f : files)
				{
					VFSFile vfsFile = vfs._getFile(session, f, jEdit.getActiveView());
					addFile(vfsFile, session);
				}
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, "Unable to list directory " + file.getPath(), e);
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
			writer.deleteDocuments(new Term("_path", path));
			LucenePlugin.CENTRAL.removeFile(path, name);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Unable to delete document " + path, e);
		}
	}

	public void reindex()
	{
		Log.log(Log.DEBUG,this, "reindex()");
		openWriter();
		if (writer == null)
			return;
		List<String> allDocuments = LucenePlugin.CENTRAL.getAllDocuments(name);
		for (String allDocument : allDocuments)
		{
			addFile(allDocument);
		}
	}

	public void search(String query, int max, ResultProcessor processor)
	{
		if (max < 1)
			max = 1;
		Searcher searcher = getSearcher();
		if (searcher == null)
			return;
		QueryParser parser = new MultiFieldQueryParser(new String[]{"path", "content"}, getAnalyzer());
		try
		{
			Query _query = parser.parse(query);
			TopDocs docs = searcher.search(_query, max);
			ScoreDoc[] scoreDocs = docs.scoreDocs;
			Result result = getResultInstance();
			for (ScoreDoc doc : scoreDocs)
			{
				Document document = searcher.doc(doc.doc);
				result.setDocument(document);
				if (!processor.process(_query, doc.score, result))
				{
					break;
				}
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
		finally
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

	protected Result getResultInstance()
	{
		return new Result();
	}

	protected void addDocument(VFSFile file, Object session)
	{
		Log.log(Log.DEBUG, this, "Index:" + name + " add " + file.getPath());
		Document doc = getEmptyDocument(file);
		Reader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(file.getVFS()._createInputStream(session, file.getPath(),
			                                                                                   false,
			                                                                                   jEdit.getActiveView())));
			doc.add(new Field("content", reader));
			LucenePlugin.CENTRAL.addFile(file.getPath(), name);
			writer.updateDocument(new Term("_path", file.getPath()), doc);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Unable to read file " + file.getPath(), e);
		}
		finally
		{
			IOUtilities.closeQuietly(reader);
		}
	}

	protected Document getEmptyDocument(VFSFile file)
	{
		Document doc = new Document();
		doc.add(new Field("path", file.getPath(), Field.Store.NO, Field.Index.ANALYZED));
		doc.add(new Field("_path", file.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		String extension = MiscUtilities.getFileExtension(file.getPath());
		if (extension.length() != 0)
		{
			doc.add(new Field("filetype", extension.substring(1), Field.Store.NO, Field.Index.NOT_ANALYZED));
		}

		return doc;
	}

	private static class MyVFSFilter implements VFSFileFilter
	{
		public boolean accept(VFSFile file)
		{
			String name = file.getName();
			if (file.getType() == VFSFile.DIRECTORY
			    || file.getType() == VFSFile.FILESYSTEM)
			{
				return !(name.equals(".svn") || name.equals("CVS"));
			}
			else
			{
				return accept(name);
			}
		}

		public boolean accept(String url)
		{
			return OptionPane.accept(url);
		}

		public String getDescription()
		{
			return null;
		}
	}
}
