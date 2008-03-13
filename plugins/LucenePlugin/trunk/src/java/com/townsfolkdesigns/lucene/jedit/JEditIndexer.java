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

import com.townsfolkdesigns.lucene.indexer.Indexer;

/**
 * The JEditIndexer is used with jEdit's Service API, as well as it provides a
 * method to retrieve the interval between indexes for this Indexer.
 * 
 * @author elberry
 * 
 */
public interface JEditIndexer extends Indexer {

	/**
	 * Gets the interval between this Indexer should index. The interval will act
	 * as the maximum time between indexes.<br>
	 * <br>
	 * Eg. If Indexer A has a index interval of 5 minutes, and the actual
	 * indexing takes 1 minute, the indexer will reindex 4 minutes after it
	 * finishes.<br>
	 * <br>
	 * If Indexer B has an index interval of 5 minutes, and the actual indexing
	 * takes 6 minutes Indexer B will start indexing again as soon as it finishes
	 * the first time.
	 * 
	 * @return The maximum amount of time between indexes in milliseconds.
	 * @see java.util.concurrent.ScheduledThreadPoolExecutor#scheduleAtFixedRate(Runnable,
	 *      long, long, java.util.concurrent.TimeUnit)
	 */
	public long getIndexInterval();

}
