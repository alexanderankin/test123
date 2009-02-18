/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.library;

import java.io.*;

/**
 * Some file copy utilities. These are rock solid.
 * @author Dale Anson, danson@germane-software.com
 */
public class FileUtilities {
   /**
    * Buffer size for read and write operations. Increasing may improve
    * performance.
    */
   public final static int BUFFER_SIZE = 8192;

   /**
    * Copies one file to another. If destination file exists, it will be
    * overwritten.
    *
    * @param from           file to copy
    * @param to             where to put it
    * @exception Exception  most likely an IOException
    */
   public static void copy(File from, File to) throws Exception {
      copyFile(from, to);
   }

   /**
    * Copies a stream to a file. If destination file exists, it will be
    * overwritten. The input stream will be closed when this method returns.
    *
    * @param is           stream to copy from
    * @param to             file to write
    * @exception Exception  most likely an IOException
    */
   public static void copy(InputStream is, File to) throws Exception {
      copyToFile(is, to);
   }

   /**
    * Copies a stream to a file. If destination file exists, it will be
    * overwritten. The input stream may be closed when this method returns.
    *
    * @param is           stream to copy from
    * @param to             file to write
    * @param close          whether to close the input stream when done
    * @exception Exception  most likely an IOException
    */
   public static void copy(InputStream is, boolean close, File to) throws Exception {
      copyToFile(is, close, to);
   }

   /**
    * Copies a stream to another stream. The input stream will be closed when
    * this method returns.
    *
    * @param is           stream to copy from
    * @param os           stream to copy to
    * @exception Exception  most likely an IOException
    */
   public static void copy(InputStream is, OutputStream os) throws Exception {
      copyToStream(is, os);
   }

   /**
    * Copies a reader to a writer. The reader will be closed when
    * this method returns.
    *
    * @param r           Reader to read from
    * @param w             Writer to write to
    * @exception Exception  most likely an IOException
    */
   public static void copy(Reader r, Writer w) throws Exception {
      copyToWriter(r, w);
   }

   /**
    * Copies one file to another. If destination file exists, it will be
    * overwritten.
    *
    * @param from           file to copy
    * @param to             where to put it
    * @exception Exception  most likely an IOException
    */
   public static void copyFile( File from, File to ) throws Exception {
      if ( !from.exists() )
         return ;
      FileInputStream in = new FileInputStream( from );
      FileOutputStream out = new FileOutputStream( to );
      byte[] buffer = new byte[ BUFFER_SIZE ];
      int bytes_read;
      while ( true ) {
         bytes_read = in.read( buffer );
         if ( bytes_read == -1 )
            break;
         out.write( buffer, 0, bytes_read );
      }
      out.flush();
      out.close();
      in.close();
   }

   /**
    * Copies a stream to a file. If destination file exists, it will be
    * overwritten. The input stream will be closed when this method returns.
    *
    * @param from           stream to copy from
    * @param to             file to write
    * @exception Exception  most likely an IOException
    */
   public static void copyToFile( InputStream from, File to ) throws Exception {
      copyToFile( from, true, to );
   }

   /**
    * Copies a stream to a file. If destination file exists, it will be
    * overwritten. The input stream may be closed when this method returns.
    *
    * @param from           stream to copy from
    * @param to             file to write
    * @param close          whether to close the input stream when done
    * @exception Exception  most likely an IOException
    */
   public static void copyToFile( InputStream from, boolean close, File to ) throws Exception {
      FileOutputStream out = new FileOutputStream( to );
      byte[] buffer = new byte[ BUFFER_SIZE ];
      int bytes_read;
      while ( true ) {
         bytes_read = from.read( buffer );
         if ( bytes_read == -1 )
            break;
         out.write( buffer, 0, bytes_read );
      }
      out.flush();
      out.close();
      if ( close )
         from.close();
   }

   /**
    * Copies a stream to another stream. The input stream will be closed when
    * this method returns.
    *
    * @param from           stream to copy from
    * @param to             file to write
    * @exception Exception  most likely an IOException
    */
   public static void copyToStream( InputStream from, OutputStream to ) throws Exception {
      byte[] buffer = new byte[ BUFFER_SIZE ];
      int bytes_read;
      while ( true ) {
         bytes_read = from.read( buffer );
         if ( bytes_read == -1 )
            break;
         to.write( buffer, 0, bytes_read );
      }
      to.flush();
      from.close();
   }

   /**
    * Copies a reader to a writer. The reader will be closed when
    * this method returns.
    *
    * @param from           Reader to read from
    * @param to             Writer to write to
    * @exception Exception  most likely an IOException
    */
   public static void copyToWriter( Reader from, Writer to ) throws Exception {
      char[] buffer = new char[ BUFFER_SIZE ];
      int chars_read;
      while ( true ) {
         chars_read = from.read( buffer );
         if ( chars_read == -1 )
            break;
         to.write( buffer, 0, chars_read );
      }
      to.flush();
      from.close();
   }
}

