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

import java.util.Date;

/**
 * Abstracted Query from the Lucene one to make it easier to use.
 * 
 * @author elberry
 * 
 */
public class Query {

	private Date completionDate;
	private Date executionDate;
	private org.apache.lucene.search.Query luceneQuery;
	private String text;

	public Query() {
	}

	/**
	 * The Completion Date is the date this query finished executing.
	 * 
	 * @return the completionDate
	 */
	public Date getCompletionDate() {
		return completionDate;
	}

	/**
	 * The Execution Date is when this query actually gets Executed.
	 * 
	 * @return the executionDate
	 */
	public Date getExecutionDate() {
		return executionDate;
	}

	public org.apache.lucene.search.Query getLuceneQuery() {
		return luceneQuery;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param completionDate
	 *           the completionDate to set
	 */
	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}

	/**
	 * @param executionDate
	 *           the executionDate to set
	 */
	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public void setLuceneQuery(org.apache.lucene.search.Query luceneQuery) {
		this.luceneQuery = luceneQuery;
	}

	/**
	 * @param text
	 *           the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

}
