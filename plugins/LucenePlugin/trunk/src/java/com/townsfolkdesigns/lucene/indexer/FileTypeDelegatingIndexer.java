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
package com.townsfolkdesigns.lucene.indexer;

import com.townsfolkdesigns.lucene.parser.FileDocumentParser;
import com.townsfolkdesigns.lucene.util.FileUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.document.Document;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author  eberry
 */
public class FileTypeDelegatingIndexer extends Indexer {

   private FileDocumentParser defaultDocumentParser;
   private Map<String, Collection<FileDocumentParser>> documentParsers;
   private Log log = LogFactory.getLog(getClass());

   public FileTypeDelegatingIndexer() {
      
      documentParsers = new LinkedHashMap<String, Collection<FileDocumentParser>>();
   }

   public FileDocumentParser getDefaultDocumentParser() {
      
      return defaultDocumentParser;
   }

   public Collection<FileDocumentParser> getDocumentParsers() {
      
      Collection<FileDocumentParser> documentParserCollection = new ArrayList<FileDocumentParser>();

      for (Collection<FileDocumentParser> documentParsers : this.documentParsers.values()) {
         documentParserCollection.addAll(documentParsers);
      }

      return documentParserCollection;
   }

   public void setDefaultDocumentParser(FileDocumentParser defaultDocumentParser) {
      
      this.defaultDocumentParser = defaultDocumentParser;
   }

   public void setDocumentParsers(Collection<FileDocumentParser> indexers) {
      
      Collection<FileDocumentParser> knownParsers = null;

      for (FileDocumentParser parser : indexers) {
         knownParsers = this.documentParsers.get(parser.getType());

         if (knownParsers == null) {
            knownParsers = new ArrayList<FileDocumentParser>();
            this.documentParsers.put(parser.getType(), knownParsers);
         }

         knownParsers.add(parser);
      }
   }

   @Override
   protected Document indexFile(File file) {

      Document document = new Document();
      String fileType = FileUtils.getFileType(file);
      Collection<FileDocumentParser> documentParsers = getDocumentParsers(fileType);

      if (documentParsers != null) {

         if (log.isDebugEnabled()) {
            log.debug("File type: " + fileType + " | parser count: " + documentParsers.size());
         }

         for (FileDocumentParser documentParser : documentParsers) {
            documentParser.parse(file, document);
         }
      } else {

         if (log.isDebugEnabled()) {
            log.debug("No parsers found for file type, \"" + fileType + "\" using default parser.");
         }

         // no parsers given, just add the default attributes.
         getDefaultDocumentParser().parse(file, document);
      }

      return document;
   }

   private Collection<FileDocumentParser> getDocumentParsers(String fileType) {
      
      return documentParsers.get(fileType);
   }
}
