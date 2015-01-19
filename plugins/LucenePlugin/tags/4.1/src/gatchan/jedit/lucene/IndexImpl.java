/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, 2014 Matthieu Casanova
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
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSFileFilter;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ProgressObserver;
import org.gjt.sp.util.Task;
import org.gjt.sp.util.ThreadUtilities;

import java.io.*;
import java.nio.channels.ClosedByInterruptException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
//}}}

/**
 * @author Matthieu Casanova
 */
public class IndexImpl extends AbstractIndex implements Index
{
	private final String name;
	private final VFSFileFilter filter = new MyVFSFilter();
	private int writerCount;

	private boolean closeWriter;

	//{{{ IndexImpl constructor
	public IndexImpl(String name, File path)
	{
		super(path);
		this.name = name;
	} //}}}

	//{{{ getName() method
	@Override
	public String getName()
	{
		return name;
	} //}}}

	//{{{ startActivity() method
	private void startActivity()
	{
		synchronized (this)
		{
			writerCount++;
		}
		for (ActivityListener al : listeners)
			al.indexingStarted(this);
	} //}}}

	//{{{ endActivity() method
	private void endActivity(boolean close) throws IndexInterruptedException
	{
		synchronized (this)
		{
			writerCount--;
			closeWriter = close;

			if (closeWriter && writerCount == 0)
			{
				closeWriter();
				closeWriter = false;
			}
		}
		for (ActivityListener al : listeners)
			al.indexingEnded(this);
	} //}}}

	//{{{ isChanging() method
	@Override
	public synchronized boolean isChanging()
	{
		return writerCount > 0;
	} //}}}

	//{{{ addFiles() method
	@Override
	public void addFiles(FileProvider files, ProgressObserver progressObserver) throws IndexInterruptedException
	{
		try
		{
			if (progressObserver != null)
				progressObserver.setMaximum(files.size());
			startActivity();
			VFSFile file = files.next();
			if (file == null)
				return;
			
			Log.log(Log.DEBUG, this, "IndexImpl Index Filename = " + file.getPath());

			openWriter();
			if (writer == null)
				return;
			String path = file.getPath();
			VFS vfs = VFSManager.getVFSForPath(path);
			View view = jEdit.getActiveView();
			Object session = vfs.createVFSSession(path, view);
			int i = 0;
			for (; file != null; file = files.next())
			{
				if (progressObserver != null)
				{
					progressObserver.setStatus(file.getPath());
					progressObserver.setValue(i++);
				}
				addDocument(file, session);
			}
			try
			{
				vfs._endVFSSession(session, view);
			}
			catch (IOException e)
			{
			}
			LucenePlugin.CENTRAL.commit();
		}
		finally
		{
			endActivity(true);
		}
	} //}}}

	//{{{ addFile() methods
	@Override
	public void addFile(String path) throws IndexInterruptedException
	{
		openWriter();
		if (writer == null)
			return;
		Object session = null;
		VFS vfs = VFSManager.getVFSForPath(path);
		try
		{
			startActivity();
			session = vfs.createVFSSession(path, jEdit.getActiveView());

			VFSFile vfsFile = vfs._getFile(session, path, jEdit.getActiveView());
			if (vfsFile == null)
			{
				Log.log(Log.ERROR, this, "Unable to add document " + path + " the file doesn't exist");
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
			endActivity(false);
		}
	}

	private void addFile(final VFSFile file, final Object session) throws IndexInterruptedException
	{
		if (file.getType() == VFSFile.DIRECTORY)
		{
			try
			{
				final CountDownLatch latch = new CountDownLatch(1);
				Task task = new Task()
				{
					@Override
					public void _run()
					{
						try
						{
							setStatus("Listing directory " + file.getPath());
							VFS vfs = file.getVFS();
							String[] files =
								vfs._listDirectory(session, file.getPath(), filter,
										   true, jEdit.getActiveView(), false,
										   false);
							String suffix = "/" + files.length;
							setMaximum(files.length);
							for (int i = 0, filesLength = files.length;
							     i < filesLength;
							     i++)
							{
								setValue(i);
								String f = files[i];
								setStatus(i + suffix);
								VFSFile vfsFile =
									vfs._getFile(session, f, jEdit.getActiveView());
								addFile(vfsFile, session);
							}
						}
						catch (IOException e)
						{
							Log.log(Log.ERROR, this,
								"Unable to list directory " + file.getPath(), e);
						}
						catch (IndexInterruptedException e) 
						{
							Log.log(Log.WARNING, this, "Indexing Halted by user");
							Thread.currentThread().interrupt();
							return;
						}
						finally
						{
							latch.countDown();
						}
					}
				};
				ThreadUtilities.runInBackground(task);
				latch.await();
			}
			catch (InterruptedException e)
			{
				Log.log(Log.ERROR, this, e);
			}
		}
		else if (file.getType() == VFSFile.FILE)
		{
			addDocument(file, session);
		}
	} //}}}

	//{{{ removeFile() method
	@Override
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
	} //}}}

	//{{{ reindex() method
	@Override
	public void reindex(ProgressObserver progressObserver) throws IndexInterruptedException
	{
		Log.log(Log.DEBUG, this, "reindex()");
		openWriter();
		if (writer == null)
		{
			Log.log(Log.ERROR, this, "Unable to open writer to reindex");
			return;
		}
		List<String> allDocuments = LucenePlugin.CENTRAL.getAllDocuments(name);
		if (progressObserver != null)
		{
			progressObserver.setMaximum(allDocuments.size());
		}
		String suffix = "/" + allDocuments.size();
		for (int i = 0, allDocumentsSize = allDocuments.size(); i < allDocumentsSize; i++)
		{
			String path = allDocuments.get(i);
			if (progressObserver != null)
			{
				progressObserver.setValue(i);
				progressObserver.setStatus(i + suffix);
			}
			removeFile(path);
			addFile(path);
		}
	} //}}}

	//{{{ search() method
	@Override
	public void search(String query, String fileType, int max, ResultProcessor processor) throws IndexInterruptedException
	{
		if (max < 1)
			max = 1;
		IndexSearcher searcher = getSearcher();
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
			Result result = getResultInstance();
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
		catch (ParseException | IOException e)
		{
			Log.log(Log.ERROR, this, e, e);
		}
	} //}}}

	//{{{ getResultInstance() method
	protected Result getResultInstance()
	{
		return new Result();
	} //}}}

	//{{{ addDocument() method
	protected void addDocument(VFSFile file, Object session) throws IndexInterruptedException
	{
		if (file.getPath() == null)
			return;
		Log.log(Log.DEBUG, this, "Index:" + name + " add " + file.getPath());
		Document doc = getEmptyDocument(file);
		if (doc == null)
			return;
		Reader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(file.getVFS()._createInputStream(session,
													   file.getPath(),
													   false,
													   jEdit.getActiveView())));
			doc.add(new TextField("content", reader));
			LucenePlugin.CENTRAL.addFile(file.getPath(), name);
			writer.updateDocument(new Term("_path", file.getPath()), doc);
		}
		catch (ClosedByInterruptException e)
		{
			Log.log(Log.WARNING, this, "Halting due to Interrupt");
			throw new IndexInterruptedException("Halting due to Interrupt");
		}
		catch (IOException e)
		{
			Log.log(Log.WARNING, this, "Unable to read file " + file.getPath());
		}
		finally
		{
			IOUtilities.closeQuietly((Closeable) reader);
		}
	} //}}}

	//{{{ getEmptyDocument() method
	protected static Document getEmptyDocument(VFSFile file)
	{
		Document doc = new Document();
		if (file.getPath() == null)
			return null;
		doc.add(new TextField("path", file.getPath(), Field.Store.NO));
		doc.add(new StringField("_path", file.getPath(), Field.Store.YES));
		String extension = MiscUtilities.getFileExtension(file.getPath());
		if (!extension.isEmpty())
		{
			doc.add(new StringField("filetype", extension.substring(1), Field.Store.NO));
		}

		return doc;
	} //}}}

	//{{{ MyVFSFilter class
	private static class MyVFSFilter implements VFSFileFilter
	{
		private final String[] excludedDirectories;

		private MyVFSFilter()
		{
			String property = jEdit.getProperty("lucene.options.ExcludeDirectories", "CVS .svn .git");
			excludedDirectories = property.split(" ");
			Arrays.sort(excludedDirectories);
		}

		@Override
		public boolean accept(VFSFile file)
		{
			String name = file.getName();
			if (file.getType() == VFSFile.DIRECTORY || file.getType() == VFSFile.FILESYSTEM)
			{
				return Arrays.binarySearch(excludedDirectories, name) < 0;
			}
			else
			{
				return accept(name);
			}
		}

		@Override
		public boolean accept(String url)
		{
			return OptionPane.accept(url);
		}

		@Override
		public String getDescription()
		{
			return null;
		}
	} //}}}
}
