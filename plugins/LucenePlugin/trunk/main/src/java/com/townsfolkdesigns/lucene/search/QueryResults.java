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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;

/**
 * @author elberry
 * 
 */
public class QueryResults {

	private Collection<Throwable> errors;
	private int hitCount;
	private Hits hits;
	private Query query;

	public QueryResults() {
		setErrors(new ArrayList<Throwable>());
	}

	public void addError(Throwable error) {
		getErrors().add(error);
	}

	public Document getDocument(int index) throws CorruptIndexException, IOException {
		return hits.doc(index);
	}

	/**
	 * @return the errors
	 */
	public Collection<Throwable> getErrors() {
		return errors;
	}

	/**
	 * @return the hitCount
	 */
	public int getHitCount() {
		return hitCount;
	}

	@SuppressWarnings("unchecked")
	public Iterator<Hit> getHitIterator() {
		return hits.iterator();
	}

	/**
	 * @return the hits
	 */
	public Hits getHits() {
		return hits;
	}

	/**
	 * @return the query
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * @param errors
	 *           the errors to set
	 */
	public void setErrors(Collection<Throwable> errors) {
		this.errors = errors;
	}

	/**
	 * @param hitCount
	 *           the hitCount to set
	 */
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	/**
	 * @param hits
	 *           the hits to set
	 */
	public void setHits(Hits hits) {
		this.hits = hits;
	}

	/**
	 * @param query
	 *           the query to set
	 */
	public void setQuery(Query query) {
		this.query = query;
	}

}
