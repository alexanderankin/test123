/*
 * Prompt.java
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
import javax.swing.JOptionPane;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.parser.node.Node;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import velocity.VelocityConstants;

/**
 * A directive to prompt the user for a value.
 */
public class Prompt extends SimpleDirective
    implements VelocityConstants
{

   /**
    * Return name of this directive.
    */
   public String getName()
   {
      return "prompt";
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
      Object prompt = getRequiredValue(node, 0, "label", context);
      if (prompt == null) {
         return false;
      }
      String key = getRequiredVariable(node, 1, "key");
      Object defaultValue = getOptionalValue(node, 2, context);
      boolean overrideContext = getOptionalBoolean(node, 3, context);

      if (!overrideContext && context.getInternalUserContext().get(key) != null) {
         return true;
      }

      JEditTextArea textArea = (JEditTextArea) context.get(TEXT_AREA);
      Object value = JOptionPane.showInputDialog(textArea, prompt,
                                                 "Velocity Prompt",
                                                 JOptionPane.QUESTION_MESSAGE,
                                                 null, null, defaultValue);
      if (value != null) {
         context.getInternalUserContext().put(key, value);
      }
      return true;
   }

}

