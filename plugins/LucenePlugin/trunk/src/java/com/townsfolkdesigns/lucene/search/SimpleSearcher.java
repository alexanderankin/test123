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
package com.townsfolkdesigns.lucene.search;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;

/**
 * A simple implementation of a Searcher.
 * 
 * @author elberry
 * 
 */
public class SimpleSearcher implements Searcher {

	private IndexSearcher indexSearcher;
	private File indexStoreDirectory;
	private Log log = LogFactory.getLog(getClass());
	private QueryParser queryParser;

	public SimpleSearcher() {

	}

	public IndexSearcher getIndexSearcher() {
		return indexSearcher;
	}

	public File getIndexStoreDirectory() {
		return indexStoreDirectory;
	}

	public QueryParser getQueryParser() {
		return queryParser;
	}

	public QueryResults search(Query query) {
		QueryResults results = null;
		if (getIndexSearcher() != null && getQueryParser() != null) {
			results = new QueryResults();
			parseQuery(query, results);
			executeQuery(query, results);
		} else if (getIndexSearcher() == null) {
			log.error("No IndexSearcher provided, cannot search for query: " + query.getText());
		} else {
			log.error("No QueryParser provided, cannot search for query: " + query.getText());
		}
		return results;
	}

	/**
	 * @param indexSearcher
	 *           the indexSearcher to set
	 */
	public void setIndexSearcher(IndexSearcher indexSearcher) {
		this.indexSearcher = indexSearcher;
	}

	/**
	 * @param indexStoreDirectory
	 *           the indexStoreDirectory to set
	 */
	public void setIndexStoreDirectory(File indexStoreDirectory) {
		this.indexStoreDirectory = indexStoreDirectory;
	}

	public void setQueryParser(QueryParser queryParser) {
		this.queryParser = queryParser;
	}

	/**
	 * Executes the given Query in the provided IndexSearcher and puts the
	 * results in the given QueryResults.
	 * 
	 * @param query
	 * @param results
	 */
	protected void executeQuery(Query query, QueryResults results) {
		if (query != null) {
			try {
				results.setHits(getIndexSearcher().search(query.getLuceneQuery()));
			} catch (Exception e) {
				results.addError(e);
				log.error("Error searching for query: " + query.getText(), e);
			}
		}
	}

	/**
	 * Parses the given Query using the provided QueryParser and puts the Lucene
	 * Query into the given Query. Any errors that occur during parsing will
	 * 
	 * @param query
	 * @param results
	 */
	protected void parseQuery(Query query, QueryResults results) {
		try {
			query.setLuceneQuery(getQueryParser().parse(query.getText()));
		} catch (Exception e) {
			results.addError(e);
			log.error("Error parsing query: " + query.getText(), e);
		}
	}

}
