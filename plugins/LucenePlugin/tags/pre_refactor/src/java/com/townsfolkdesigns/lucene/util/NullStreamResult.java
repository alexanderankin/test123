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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.transform.stream.StreamResult;


/**
 *
 * @author eberry
 */
/**
 * NullStreamResult directs all output nowhere. This class can be used when you want to do an XSL transform and you
 * don't want the output.
 *
 * @author eberry
 */
public class NullStreamResult extends StreamResult {

   public NullStreamResult() {

      // set the output stream and writer to "null" so the output won't go anywhere.
      super.setOutputStream(new NullOutputStream());
      super.setWriter(new NullWriter());

   }

   @Override
   public OutputStream getOutputStream() {

      return super.getOutputStream();

   }

   @Override
   public Writer getWriter() {

      // TODO Auto-generated method stub
      return super.getWriter();

   }

   @Override
   public void setOutputStream(OutputStream outputStream) {

      throw new UnsupportedOperationException("The output stream cannot be set.");

   }

   @Override
   public void setWriter(Writer writer) {

      throw new UnsupportedOperationException("The writer cannot be set.");

   }

   private class NullOutputStream extends OutputStream {

      public NullOutputStream() {

      }

      @Override
      public void write(int b) throws IOException {

         // write to no where.
      }

   }

   private class NullWriter extends Writer {

      public NullWriter() {

      }

      @Override
      public void close() throws IOException {

         // nothing to close.
      }

      @Override
      public void flush() throws IOException {

         // nothing to flush.
      }

      @Override
      public void write(char[] cbuf, int off, int len) throws IOException {

         // write to no where.
      }

   }

}
