/*
 * Today.java
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * A directive assign a variable a today's date value.
 */
public class Today extends SimpleDirective
{

   /**
    * Return name of this directive.
    */
   public String getName()
   {
      return "today";
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
      String variable = getRequiredVariable(node, 0, "date variable");
      if (variable == null) {
         return false;
      }
      Object pattern = getOptionalValue(node, 1, context);

      Calendar cal = Calendar.getInstance();
      String dateString = null;
      if (pattern == null) {
         dateString = cal.getTime().toString();
      } else {
         SimpleDateFormat format = new SimpleDateFormat(pattern.toString());
         dateString = format.format(cal.getTime());
      }
      context.getInternalUserContext().put(variable, dateString);
      return true;
   }

}

