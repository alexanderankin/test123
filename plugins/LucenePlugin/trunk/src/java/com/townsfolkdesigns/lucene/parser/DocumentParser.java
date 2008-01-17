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
package com.townsfolkdesigns.lucene.parser;

import java.util.Collection;

import org.apache.lucene.document.Document;


/**
 * A DocumentParser parses a source given to it in the parse method. The parsed
 * source is injected into the given Document.
 * 
 * @author eberry
 */
public interface DocumentParser<T> {

    /**
     * Get the fields this DocumentParser adds to the given document in the parse method.
     *
     * @return the fields added to the document given in the parse method.
     */
    public Collection<SearchField> getFields();

    /**
     * The type that this DocumentParser is responsible for.
     *
     * @return
     */
    public String[] getTypes();

    /**
     * Parses the given source into the given document.
     *
     * @param source
     * @param document
     */
    public void parse(T source,
                      Document document);

}
