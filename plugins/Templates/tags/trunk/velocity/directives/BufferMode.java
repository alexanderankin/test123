/*
 * BufferMode.java
 * Copyright (c) 2002 Steve Jakob
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
package velocity.directives;

import velocity.BufferWriter;
import java.io.Writer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * A directive to programmatically change the mode of a jEdit buffer. */
public class BufferMode extends SimpleDirective
{

   /**
    * Return name of this directive.
    */
   public String getName()
   {
      return "buffermode";
   }

   /**
    * Return type of this directive.
    */
   public int getType()
   {
      return LINE;
   }

   /**
    * Prompt the user for a value.
    */
   public boolean render(InternalContextAdapter context,
                         Writer writer, Node node)
   throws MethodInvocationException
   {
	  Object modeName = getRequiredValue(node, 0, "buffer mode", context);
      if (modeName == null) {
         return false;
      }
	  
      if (! (writer instanceof BufferWriter)) {
         rsvc.error("#buffermode() error :  writer is not a buffer writer");
         return false;
      }
      BufferWriter bufferWriter = (BufferWriter) writer;
	  if (!bufferWriter.setMode((String)modeName)) {
         rsvc.error("#buffermode() error :  Mode change was unsuccessful");
		  return false;
	  }
	  return true;

   }

}

