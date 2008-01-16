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
package com.townsfolkdesigns.lucene.util;

import org.apache.commons.lang.StringUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author eberry
 */
public abstract class GenericDocumentFactory {
   
   private static final Log log = LogFactory.getLog(GenericDocumentFactory.class);

   private static final String FIELD_NAME_SEPARATOR = "-";
   private static final Pattern GROUP_PATTERN = Pattern.compile("[a-z]+|[A-Z]+");

   public static Document createDocument(Object source) {

      Document document = new Document();
      Class sourceClass = source.getClass();
      java.lang.reflect.Field[] fields = sourceClass.getFields();
      String fieldName = null;
      Object fieldValue = null;

      for (java.lang.reflect.Field field : fields) {
         fieldName = field.getName();

         if (!StringUtils.equalsIgnoreCase(fieldName, "class")) {
            fieldName = normalizeFieldName(fieldName);
            try {
               fieldValue = field.get(source);
               document.add(new Field(fieldName, convertFieldValueToString(fieldValue), Field.Store.YES,
                     Field.Index.TOKENIZED));
            } catch(Exception e) {
               log.error("Error getting field value for field: " + field.getName() + " on object: " + source, e);
            }
         }
      }

      return document;
   }

   private static String convertFieldValueToString(Object fieldValue) {
      StringBuilder builder = new StringBuilder();

      if (fieldValue instanceof Collection) {
         Collection collection = (Collection) fieldValue;

         for (Object value : collection) {
            builder.append(fieldValue.toString()).append(",");
         }
      } else {
         builder.append(fieldValue.toString());
      }

      return builder.toString();
   }

   private static String normalizeFieldName(String fieldName) {
      Matcher groupMatcher = GROUP_PATTERN.matcher(fieldName);
      StringBuilder builder = new StringBuilder();
      String group = null;

      while (groupMatcher.find()) {

         // group 0 is the entire match. Which in this case, is any group of
         // lower case letters, or upper case letters.
         group = groupMatcher.group(0);

         if (Character.isUpperCase(group.charAt(0))) {

            // if the group starts with an uppercased letter, then it's an upper group.
            if (builder.length() > 0) {

               // if the group isn't empty, then append an separator first.
               builder.append(FIELD_NAME_SEPARATOR);
            }

            group = group.toLowerCase();
         }

         builder.append(group);
      }

      return builder.toString();
   }
}
