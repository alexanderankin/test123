/*
 * Index.java - The Index interface
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, 2020 Matthieu Casanova
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
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.util.ProgressObserver;

/**
 * @author Matthieu Casanova
 */
public interface Index
{
	/**
	 * Release resources.
	 */
	void close() throws IndexInterruptedException;

	void commit();
	void reindex(ProgressObserver progressObserver) throws IndexInterruptedException;

	String getName();
	Analyzer getAnalyzer();
	void setAnalyzer(Analyzer analyzer);

	/**
	 * Add a single file to the index.
	 * @param path the path of the added file.
	 * @throws IndexInterruptedException 
	 */
	void addFile(String path) throws IndexInterruptedException;

	void clear() throws IndexInterruptedException;

	interface FileProvider
	{
		VFSFile next();
		int size();
	}

	/**
	 * Add several files to an index
	 * @param files the file provider
	 * @throws IndexInterruptedException 
	 */
	void addFiles(FileProvider files, ProgressObserver progressObserver) throws IndexInterruptedException;
	void removeFile(String path);

	/**
	 * Execute the given search query.
	 *
	 * @param query the query to execute
	 * @param max the maximal number of results to search for
	 * @param processor the processor that will get the results
	 * @throws IndexInterruptedException 
	 */
	void search(String query, String fileType, int max, ResultProcessor processor) throws IndexInterruptedException;

	interface ActivityListener
	{
		void indexingStarted(Index index);
		void indexingEnded(Index index);
	}
	void addActivityListener(ActivityListener activityListener);
	void removeActivityListener(ActivityListener activityListener);
	boolean isChanging();
}
