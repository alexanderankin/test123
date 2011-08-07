/*
 * Index.java - The Index interface
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
import org.gjt.sp.jedit.io.VFSFile;

/**
 * @author Matthieu Casanova
 */
public interface Index
{
	/**
	 * Release resources.
	 */
	void close();

	boolean isOptimized();
	void optimize();
	void commit();
	void reindex();

	String getName();
	Analyzer getAnalyzer();
	void addFile(String path);
	void addFiles(VFSFile[] files);

	interface FileProvider
	{
		VFSFile next();
	}
	void addFiles(FileProvider files);
	void removeFile(String path);

	void setAnalyzer(Analyzer analyzer);

	/**
	 * Execute the given search query.
	 *
	 * @param query the query to execute
	 * @param max the maximal number of results to search for
	 * @param processor the processor that will get the results
	 */
	void search(String query, String fileType, int max, ResultProcessor processor);

	interface ActivityListener
	{
		void indexingStarted(Index index);
		void indexingEnded(Index index);
	}
	void addActivityListener(ActivityListener l);
	void removeActivityListener(ActivityListener l);
	boolean isChanging();
}
