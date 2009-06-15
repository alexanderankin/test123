/*
 * LucenePlugin.java - The Lucene plugin
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

import org.apache.lucene.analysis.Analyzer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import javax.swing.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

/**
 * @author Matthieu Casanova
 */
public class LucenePlugin extends EditPlugin
{
	static CentralIndex CENTRAL;
	private static final String CENTRAL_INDEX_NAME = "__CENTRAL__";
	private Map<String, Index> indexMap = new HashMap<String, Index>();

	public static LucenePlugin instance;

	@Override
	public void start()
	{
		instance = this;
		File home = getPluginHome();
		CENTRAL = new CentralIndex(new File(home, CENTRAL_INDEX_NAME));
		EditBus.addToBus(CENTRAL);
	}

	@Override
	public void stop()
	{
		EditBus.removeFromBus(CENTRAL);
		CENTRAL.close();
		CENTRAL = null;
		instance = null;
		Collection<Index> indexCollection = indexMap.values();
		for (Index index : indexCollection)
		{
			index.close();
		}
	}

	/**
	 * Return an index, or null.
	 *
	 * @param name the name of the index
	 * @return the index or null if there is no settings directory.
	 */
	public Index getIndex(String name)
	{
		if (getIndexFile(name) == null)
			return null;
		return indexMap.get(name);
	}

	public Index createIndex(String name, String type, String analyzerName)
	{
		Index index = getIndex(name);
		if (index != null)
			return index;

		File path = getIndexFile(name);
		index = IndexFactory.createIndex(type);
		if (index == null)
			return null;
		index.setData(name, path);
		Analyzer analyzer = AnalyzerFactory.getAnalyzer(analyzerName);
		if (analyzer != null)
			index.setAnalyzer(analyzer);
		indexMap.put(name, index);
		if (!path.exists())
		{
			path.mkdirs();
			CENTRAL.createIndex(index);
		}
		return index;
	}

	/**
	 * Delete an index.
	 *
	 * @param name the name of the index.
	 */
	public void removeIndex(String name)
	{
		CENTRAL.removeIndex(name);
		Index index = indexMap.remove(name);
		if (index != null)
			index.close();
		File indexFile = getIndexFile(name);
		if (indexFile == null)
			return;

		VFS vfs = VFSManager.getVFSForPath(name);
		Object session = vfs.createVFSSession(indexFile.getAbsolutePath(), jEdit.getActiveView());
		try
		{
			vfs._delete(session, indexFile.getAbsolutePath(), jEdit.getActiveView());
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			try
			{
				vfs._endVFSSession(session, jEdit.getActiveView());
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, e);
			}
		}
	}

	public String chooseIndex()
	{
		String[] names = getIndexes();
		String name = (String) JOptionPane.showInputDialog(jEdit.getActiveView(), "Choose an index", "Choose an index",
		                                                   JOptionPane.QUESTION_MESSAGE, null, names, null);
		return name;
	}

	/*
	 * Open the new index dialog.
	 * Returns the name of the new index, or null if cancelled.
	 */
	public String createNewIndex()
	{
		NewIndexDialog dlg = new NewIndexDialog(jEdit.getActiveView());
		dlg.setVisible(true);
		if (! dlg.accepted())
			return null;
		Index index = createIndex(dlg.getIndexName(), dlg.getIndexType(),
			dlg.getIndexAnalyzer());
		if (index == null)
			return null;
		return index.getName();
	}

	/**
	 * Add some files to the given index.
	 *
	 * @param indexName the index name
	 * @param files     the file array to add
	 */
	public void addToIndex(final String indexName, final VFSFile[] files)
	{
		VFSManager.runInWorkThread(new Runnable()
		{
			public void run()
			{
				Index index = getIndex(indexName);
				if (index == null)
				{
					Log.log(Log.ERROR, this, "Unable to get index " + indexName);
					return;
				}
				for (VFSFile file : files)
				{
					index.addFile(file.getPath());
				}
				index.commit();
			}
		});
	}

	/**
	 * Returns the index list.
	 * @return the index list
	 */
	public String[] getIndexes()
	{
		File home = getPluginHome();
		File indexFolder = new File(home, "indexes");
		File[] indexes = indexFolder.listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				return pathname.isDirectory();
			}
		});

		if (indexes == null || indexes.length == 0)
			return new String[0];
		List<String> names = new ArrayList<String>(indexes.length);
		for (File index : indexes)
		{
			names.add(index.getName());
		}
		return names.toArray(new String[names.size()]);
	}

	private File getIndexFile(String name)
	{
		File home = getPluginHome();
		if (home == null)
			return null;
		File indexFolder = new File(home, "indexes");
		return new File(indexFolder, name);
	}
}
