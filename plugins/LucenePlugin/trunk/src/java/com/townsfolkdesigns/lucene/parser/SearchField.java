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


/**
 * A SearchField is a field that is indexed by a DocumentParser. It contains references to the name of the field, a
 * "Display Name", whether it is a text field, or if there are choices available for this field.
 *
 * @author eberry
 */
public class SearchField {

   /** If there this is not a "text" field, then there will be choices available to use in searches. */
   private Collection<Choice> choices;

   /** The display name for this Field. This can be used for presenting this Field. */
   private String displayName;

   /**
    * The name of this field. Should be the same as the field added to the Document in the
    * DocumentParser.parse method.
    *
    * @see com.eharmony.ehweb.search.parser.DocumentParser#parse(Object, org.apache.lucene.document.Document)
    */
   private String name;

   /**
    * Is this search field a "text" field, or does it have choices. If this returns false, then getChoices
    * should be called to get a collection of the possible choices for this particular field.
    */
   private boolean text;

   public SearchField() {

   }

   public Collection<Choice> getChoices() {

      return choices;

   }

   public String getDisplayName() {

      return displayName;

   }

   public String getName() {

      return name;

   }

   public boolean isText() {

      return text;

   }

   public void setChoices(Collection<Choice> choices) {

      this.choices = choices;

   }

   public void setDisplayName(String displayName) {

      this.displayName = displayName;

   }

   public void setName(String name) {

      this.name = name;

   }

   public void setText(boolean text) {

      this.text = text;

   }

   public static class Choice {
      private String name;

      private Object value;

      public Choice() {

      }

      public String getName() {

         return name;

      }

      public Object getValue() {

         return value;

      }

      public void setName(String name) {

         this.name = name;

      }

      public void setValue(Object value) {

         this.value = value;

      }

   }

}
