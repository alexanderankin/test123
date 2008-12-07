/*
 * Caret.java
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
package velocity.directives;

import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import velocity.BufferWriter;
import velocity.VelocityConstants;

/**
 * A directive to mark the current position so that the caret could be placed
 * at this position after the templates is fully processed.
 */
public class Caret extends Directive
   implements VelocityConstants
{

   /**
    * Return name of this directive.
    */
   public String getName()
   {
      return "caret";
   }

   /**
    * Returns type of this directive.
    */
   public int getType()
   {
      return LINE;
   }

   /**
    * Save the current caret position.
    */
   public boolean render(InternalContextAdapter context,
                         Writer writer, Node node)
   {
      if (! (writer instanceof BufferWriter)) {
         rsvc.error("#caret() error :  writer is not a buffer writer");
         return false;
      }
      BufferWriter bufferWriter = (BufferWriter) writer;
	  int caretPos = bufferWriter.getOffset();
	  if (caretPos < 0) {
	  	caretPos = 0;
	  }
      context.getInternalUserContext().put(CARET, new Integer(caretPos));
      return true;
   }

}

