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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.gjt.sp.util.Log;

import com.townsfolkdesigns.lucene.jedit.manager.OptionsManager;

/**
 * @author elberry
 * 
 */
public class IndexerExecutor extends ThreadPoolExecutor {

	private static final IndexerExecutor instance = new IndexerExecutor();

	private static final OptionsManager optionManager = OptionsManager.getInstance();
	private Map<JEditIndexer, Runner> executors;

	private IndexerExecutor() {
		// core size = 1 - will start with 1 thread.
		// max size = 5
		// idle time out = 5 minutes.
		super(1, 5, 5 * 60 * 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		// class is a singleton.
		executors = new ConcurrentHashMap<JEditIndexer, Runner>();
		initialize();
	}

	public static IndexerExecutor getInstance() {
		return instance;
	}

	public void addIndexer(JEditIndexer indexer) {
		Runner runner = new Runner(indexer);
		executors.put(indexer, runner);
		execute(runner);
	}

	public boolean removeIndexer(JEditIndexer indexer) {
		boolean existed = false;
		if (executors.containsKey(indexer)) {
			existed = true;
			Runner runner = executors.remove(indexer);
			remove(runner);
		}
		return existed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable,
	 *      java.lang.Throwable)
	 */
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		// after runner is done running, just add it back to the pool. It'll take
		// care of waiting for the correct amount of time.
		if (t == null) {
			execute(r);
		}
	}

	private void initialize() {
		// use optionManager to get the pool size and such.
	}

	private class Runner implements Runnable {
		private JEditIndexer indexer;
		private long lastIndexTime = 0;

		public Runner(JEditIndexer indexer) {
			this.indexer = indexer;
		}

		public void run() {
			if (lastIndexTime != 0) {
				long diff = System.currentTimeMillis() - lastIndexTime;
				try {
					Log.log(Log.DEBUG, this, "Not time to run this indexer, going to wait for "
					      + (indexer.getIndexInterval() - diff) + " ms.");
					Thread.sleep(indexer.getIndexInterval() - diff);
				} catch (Exception e) {
					Log.log(Log.ERROR, this, "Error trying to wait till next time indexer needs to be run", e);
				}
			}
			if (!indexer.isInitialized()) {
				Log.log(Log.DEBUG, this, "Initializing Indexer - class: " + indexer.getClass().getName());
				indexer.init();
			}
			Log.log(Log.DEBUG, this, "Starting Indexer - class: " + indexer.getClass().getName());
			lastIndexTime = System.currentTimeMillis();
			indexer.index();
		}
	}
}
