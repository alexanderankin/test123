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

import com.townsfolkdesigns.lucene.util.GenericDocumentFactory;
import com.townsfolkdesigns.lucene.util.NullStreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;


/**
 * @author  eberry
 */
public class TilesDefinitionFileDocumentParser extends DefaultFileDocumentParser {

   private IndexWriter indexWriter;
   private Log log = LogFactory.getLog(getClass());
   private Result xslResult = new NullStreamResult();
   private Source xslSource = new StreamSource(getClass().getResourceAsStream("/tiles_defs.xsl"));

   public TilesDefinitionFileDocumentParser() {

      super();

      Collection<SearchField> fields = getFields();
      Document generatedDocument = GenericDocumentFactory.createDocument(new TilesDefinition());
      Field field = null;

      for (Object fieldObj : generatedDocument.getFields()) {

         field = (Field) fieldObj;
         String displayName = field.name();
         displayName = StringUtils.replace(displayName, "-", " ");
         displayName = WordUtils.capitalizeFully(displayName);
         fields.add(createSearchField(field.name(), displayName, true));
         System.out.println(displayName);

      }

   }

   public IndexWriter getIndexWriter() {

      return indexWriter;

   }

   @Override
   public String getType() {

      return "xml";

   }

   @Override
   public void parse(File source, Document document) {

      // get the defaults for the tiles def document.
      super.parse(source, document);

      Transformer transformer = null;
      Source xslSource = new StreamSource(getClass().getResourceAsStream("/tiles_defs.xsl"));
      List<TilesDefinition> definitions = new ArrayList<TilesDefinition>();

      try {

         transformer = TransformerFactory.newInstance().newTransformer(xslSource);

      } catch (Exception e) {

         log.error("Error getting an xsl transformer for source", e);

      }

      if (transformer != null) {

         Source xmlSource = new StreamSource(source);

         try {

            transformer.transform(xmlSource, xslResult);
            definitions = (List<TilesDefinition>) transformer.getParameter("definitionsParam");

         } catch (Exception e) {

            log.error("Error transforming source file: " + source.getPath(), e);

         }

         if (log.isDebugEnabled()) {

            log.debug("File: " + source.getName() + " | definitions found: " + definitions.size());

         }

         for (TilesDefinition definition : definitions) {

            Document definitionDoc = GenericDocumentFactory.createDocument(definition);
            definitionDoc.add(new Field("type", "tiles-definition", Store.YES, Index.TOKENIZED));
            definitionDoc.add(new Field("path", document.get("path"), Store.YES, Index.TOKENIZED));

            try {

               getIndexWriter().addDocument(definitionDoc);

            } catch (Exception e) {

               log.error("Error writing definition document to index.", e);

            }

         }

      }

   }

   public void setIndexWriter(IndexWriter indexWriter) {

      this.indexWriter = indexWriter;

   }

}
