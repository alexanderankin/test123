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

import java.io.File;
import java.io.FileReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.townsfolkdesigns.lucene.util.FileUtils;

/**
 * 
 * @author eberry
 */
public class DefaultFileDocumentParser implements FileDocumentParser {

	public static final String[] DEFAULT_TYPES = new String[] {
		""
	};
	private Collection<SearchField> fields;
	private Log log = LogFactory.getLog(getClass());

	public DefaultFileDocumentParser() {
		ArrayList<SearchField> fields = new ArrayList<SearchField>();
		fields.add(createSearchField("content", "Content", true));
		fields.add(createSearchField("path", "Path", true));
		fields.add(createSearchField("last-modified", "Last Modified", true));
		fields.add(createSearchField("type", "File Type", true));
		fields.add(createSearchField("file-name", "File Name", true));
		setFields(fields);
	}

	public Collection<SearchField> getFields() {
		return fields;
	}

	public String[] getTypes() {
		return DEFAULT_TYPES;
	}

	public void parse(File source, Document document) {

		try {

			// reading the file contents into a string.
			FileReader reader = new FileReader(source);
			CharBuffer buffer = CharBuffer.allocate(1024);
			reader.read(buffer);
			buffer.flip();

			String fileContent = buffer.toString();
			String filePath = source.getPath();
			String fileType = FileUtils.getFileType(source);

			// ++: These Fields could be instance variables of this document
			// parser.
			// Sharing instances across multiple documents is allowed, and can
			// speed up indexing.

			// add omni-present fields.
			document.add(new Field("file-name", source.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED));

			document.add(new Field("last-modified", String.valueOf(source.lastModified()), Field.Store.YES,
			      Field.Index.UN_TOKENIZED));

			if (StringUtils.isNotBlank(fileContent)) {
				document.add(new Field("content", fileContent, Field.Store.YES, Field.Index.TOKENIZED));
			}

			if (StringUtils.isNotBlank(filePath)) {
				document.add(new Field("path", filePath, Field.Store.YES, Field.Index.TOKENIZED));
			}

			// file type is the same as the extension.
			if (StringUtils.isNotBlank(fileType)) {
				document.add(new Field("type", fileType, Field.Store.YES, Field.Index.TOKENIZED));
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void setFields(Collection<SearchField> fields) {
		this.fields = fields;
	}

	protected SearchField createSearchField(String name, String displayName, boolean text) {
		SearchField searchField = new SearchField();
		searchField.setDisplayName(displayName);
		searchField.setName(name);
		searchField.setText(text);

		return searchField;
	}
}
