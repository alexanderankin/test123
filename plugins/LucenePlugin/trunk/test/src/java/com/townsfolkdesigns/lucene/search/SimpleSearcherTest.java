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
package com.townsfolkdesigns.lucene.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.IndexSearcher;

import com.townsfolkdesigns.lucene.jedit.LucenePlugin;
import com.townsfolkdesigns.lucene.jedit.LucenePluginIndexer;
import com.townsfolkdesigns.lucene.jedit.manager.OptionsManager;

/**
 * @author elberry
 * 
 */
public class SimpleSearcherTest extends TestCase {

	@Override
	protected void tearDown() throws Exception {
		File pluginHome = new LucenePlugin().getPluginHome();
		File testFile1 = new File(pluginHome, "test1.txt");
		File testFile2 = new File(pluginHome, "test2.txt");
		if (testFile1.exists()) {
			testFile1.delete();
		}
		if (testFile2.exists()) {
			testFile2.delete();
		}
	}

	@Override
	protected void setUp() throws Exception {
		File pluginHome = new LucenePlugin().getPluginHome();
		File testFile1 = new File(pluginHome, "test1.txt");
		File testFile2 = new File(pluginHome, "test2.txt");
		if (!testFile1.exists()) {
			testFile1.createNewFile();
		}
		if (!testFile2.exists()) {
			testFile2.createNewFile();
		}
		List<String> directories = new ArrayList<String>();
		directories.add(pluginHome.getPath());
		OptionsManager optionsManager = OptionsManager.getInstance();
		optionsManager.clear();
		optionsManager.setDirectories(directories);
		optionsManager.save();
		LucenePluginIndexer indexer = new LucenePluginIndexer();
		indexer.init();
		indexer.index();
	}

	public void testSimpleSearcher() throws Exception {
		LucenePluginIndexer indexer = new LucenePluginIndexer();
		indexer.init();
		File indexStoreDirectory = indexer.getIndexStoreDirectory();
		IndexSearcher indexSearcher = new IndexSearcher(indexStoreDirectory.getPath());
		QueryParser queryParser = new QueryParser("type", new StandardAnalyzer());
		SimpleSearcher searcher = new SimpleSearcher();
		searcher.setIndexStoreDirectory(indexStoreDirectory);
		searcher.setIndexSearcher(indexSearcher);
		searcher.setQueryParser(queryParser);
		Query query = new Query();
		query.setText("txt");
		QueryResults results = searcher.search(query);
		assertNotNull(results);
		assertNotNull(results.getQuery());
		assertEquals(query, results.getQuery());
		assertNotNull(results.getQuery().getLuceneQuery());
		assertNotNull(results.getHits());
		assertNotSame(0, results.getHitCount());
		assertEquals(2, results.getHitCount());
		assertNotNull(results.getErrors());
		assertNotNull(results.getHitIterator());
		assertNotNull(query.getExecutionDate());
		assertNotNull(query.getCompletionDate());
		assertNotSame(query.getExecutionDate(), query.getCompletionDate());
		Iterator<Hit> iterator = results.getHitIterator();
		Hit hit = null;
		while (iterator.hasNext()) {
			hit = iterator.next();
			System.out.println(hit.get("file-name"));
		}

	}

}
