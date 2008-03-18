// $Id$
/*
 * BufferWriter.java
 * Copyright (c) 2002 Calvin Yu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package velocity;

import java.io.Writer;
import javax.swing.text.Segment;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;

/**
 * A writer that writes text to a jEdit buffer.
 */
public class BufferWriter extends Writer
{

   private Buffer buffer;
   private int offset;

   /**
    * Create a new <code>BufferWriter</code>.
    */
   public BufferWriter(Buffer aBuffer, int anOffset)
   {
      buffer = aBuffer;
      offset = anOffset;
   }

   /**
    * Returns the current offset.
    */
   public int getOffset()
   {
      return offset;
   }

   /**
    * Does nothing.
    */
   public void close() {}

   /**
    * Does nothing.
    */
   public void flush() {}

   /**
    * Applies characters to buffer.
    */
   public void write(char[] cbuf, int off, int len)
   {
      offset += stripCarriageReturns(cbuf, off, len);
   }

   /**
    * Strip any '\r\n" sequences to "\n"
    */
   private int stripCarriageReturns(char[] cbuf, int off, int len)
   {
      StringBuffer buf = null;
      for (int i=off; i<len; i++) {
         if (cbuf[i] == '\r') {
            if (buf == null) {
               buf = new StringBuffer();
               buf.append(cbuf, off, i - off);
            }
         } else if (buf != null) {
            buf.append(cbuf[i]);
         }
      }
      if (buf == null) {
         buffer.insert(offset, new Segment(cbuf, off, len));
         return len;
      } else {
         buffer.insert(offset, buf.toString());
         return buf.length();
      }
   }
   
   /**
    * Set the buffer mode to the requested value, if valid.
	* @param newModeStr The string representation of the desired mode.
	* @return <code>true</code> if the mode change was successful,
	* <code>false</code> otherwise.
    */
   public boolean setMode(String newModeStr) {
	   Mode newMode = jEdit.getMode(newModeStr);
	   if (newMode == null) {
		   return false;
	   }
	   buffer.setMode(newMode);
	   return true;
   }

}

