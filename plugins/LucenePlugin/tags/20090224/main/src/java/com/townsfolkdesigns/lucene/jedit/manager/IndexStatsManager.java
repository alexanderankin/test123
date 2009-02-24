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
package com.townsfolkdesigns.lucene.jedit.manager;

import java.util.Date;

public class IndexStatsManager {

	private int directoriesIndexed;
	private int filesIndexed;
	private Date indexEndTime;
	private boolean indexing;
	private Date indexStartTime;

	public IndexStatsManager() {

	}

	public synchronized int getDirectoriesIndexed() {
		return directoriesIndexed;
	}

	public synchronized int getFilesIndexed() {
		return filesIndexed;
	}

	public synchronized Date getIndexEndTime() {
		return indexEndTime;
	}

	public synchronized Date getIndexStartTime() {
		return indexStartTime;
	}

	public synchronized boolean isIndexing() {
		return indexing;
	}

	public synchronized void setDirectoriesIndexed(int directoriesIndexed) {
		this.directoriesIndexed = directoriesIndexed;
	}

	public synchronized void setFilesIndexed(int filesIndexed) {
		this.filesIndexed = filesIndexed;
	}

	public synchronized void setIndexEndTime(Date indexEndTime) {
		this.indexEndTime = indexEndTime;
	}

	public synchronized void setIndexing(boolean indexing) {
		this.indexing = indexing;
	}

	public synchronized void setIndexStartTime(Date indexStartTime) {
		this.indexStartTime = indexStartTime;
	}

}
