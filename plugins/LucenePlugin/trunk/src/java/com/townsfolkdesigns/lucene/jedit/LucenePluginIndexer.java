/*
 * Copyright (c) 2008 Eric Berry <elberry@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 * 
 */
package com.townsfolkdesigns.lucene.jedit;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gjt.sp.util.Log;

import com.townsfolkdesigns.lucene.jedit.manager.IndexManager;
import com.townsfolkdesigns.lucene.jedit.manager.IndexStatsManager;
import com.townsfolkdesigns.lucene.jedit.manager.OptionsManager;
import com.townsfolkdesigns.lucene.parser.DefaultFileDocumentParser;

/**
 * @author elberry
 * 
 */
public class LucenePluginIndexer extends JEditFileTypeDelegatingIndexer {

	private static final OptionsManager optionsManager = OptionsManager.getInstance();

	private IndexStatsManager indexStatsManager;

	public LucenePluginIndexer() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.townsfolkdesigns.lucene.jedit.JEditIndexer#getIndexInterval()
	 */
	public long getIndexInterval() {
		return optionsManager.getIndexInterval();
	}

	public IndexStatsManager getIndexStatsManager() {
		return indexStatsManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.townsfolkdesigns.lucene.indexer.AbstractFileIndexer#index()
	 */
	@Override
	public void index() {
		// get locations from the options manager.
		List<String> directories = optionsManager.getDirectories();
		String[] locations = directories.toArray(new String[0]);
		setLocations(locations);
		Log.log(Log.DEBUG, this, "Indexing - locations: " + Arrays.toString(locations));
		// index method overridden so that the stats can be saved in the manager.
		Date startDate = new Date();
		long startTime = startDate.getTime();
		indexStatsManager.setIndexStartTime(startDate);
		indexStatsManager.setIndexing(true);
		super.index();
		Date endDate = new Date();
		long endTime = endDate.getTime();
		long indexingTime = endTime - startTime;
		indexStatsManager.setIndexEndTime(endDate);
		indexStatsManager.setDirectoriesIndexed(getDirectoriesIndexed());
		indexStatsManager.setFilesIndexed(getFilesIndexed());
		Log.log(Log.DEBUG, this, "Indexing complete - time: " + indexingTime + " | directories: "
		      + getDirectoriesIndexed() + " | files: " + getFilesIndexed());
		// overwrite the old index.
		File indexStoreDir = new LucenePlugin().getIndexStoreDirectory();
		File indexStoreFile = new File(indexStoreDir, LucenePlugin.class.getName());
		File tempIndexStoreFile = new File(indexStoreFile, "temp");
		if (indexStoreFile.exists()) {
			Log.log(Log.DEBUG, this, "Replacing old index files with new ones.");
			// get a write lock so no searches can be done.
			IndexManager.getInstance().aquireWriteLock();
			// delete old index.
			indexStoreFile.delete();
			// replace it with the new one.
			tempIndexStoreFile.renameTo(indexStoreFile);
			// delete the temp directory.
			tempIndexStoreFile.delete();
			// release the write lock so searches can resume.
			IndexManager.getInstance().releaseWriteLock();
		} else {
			// something happened to the old index or this is the first time
			// indexing, just move the new one over.
			Log.log(Log.DEBUG, this, "Couldn't find old index files, moving new ones in place.");

			// get a write lock so no searches can be done.
			IndexManager.getInstance().aquireWriteLock();
			// replace it with the new one.
			tempIndexStoreFile.renameTo(indexStoreFile);
			// delete the temp directory.
			tempIndexStoreFile.delete();
			// release the write lock so searches can resume.
			IndexManager.getInstance().releaseWriteLock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.townsfolkdesigns.lucene.indexer.FileTypeDelegatingIndexer#init()
	 */
	@Override
	public void init() {
		File indexStoreDir = new LucenePlugin().getIndexStoreDirectory();
		if (!indexStoreDir.exists()) {
			indexStoreDir.mkdirs();
		}
		File indexStoreFile = new File(indexStoreDir, LucenePlugin.class.getName());
		// actually write the index out to a temp directory so the reader can read
		// the old index while this indexer is creating the new one. The reading
		// index is the same as above.
		indexStoreFile = new File(indexStoreFile, "temp");
		setIndexStoreDirectory(indexStoreFile);
		setDefaultDocumentParser(new DefaultFileDocumentParser());
		setIndexStatsManager(new IndexStatsManager());
		setRecursivelyIndexDirectoriesOn(true);
		super.init();
	}

	public void setIndexStatsManager(IndexStatsManager indexStatsManager) {
		this.indexStatsManager = indexStatsManager;
	}
}
