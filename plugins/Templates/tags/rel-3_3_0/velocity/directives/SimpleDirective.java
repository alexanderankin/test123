/*
 * SimpleDirective.java
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

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * A base class for custom directives.
 */
public abstract class SimpleDirective extends Directive
{

   /**
    * Returns the indexed argument as a required value.
    */
   protected Object getRequiredValue(Node node, int idx, String argumentName,
                                     InternalContextAdapter context)
   throws MethodInvocationException
   {
      if (!requireArgument(node, idx, argumentName)) {
         return null;
      }
      Object obj = node.jjtGetChild(idx).value(context);
      if (obj == null) {
         rsvc.error("#" + getName() + "() error :  value of " + argumentName + " is null");
         return null;
      }
      return obj;
   }

   /**
    * Returns the indexed argument as a required literal.
    */
   protected String getRequiredLiteral(Node node, int idx, String argumentName)
   {
      if (!requireArgument(node, idx, argumentName)) {
         return null;
      }
      return node.jjtGetChild(idx).literal();
   }

   /**
    * Returns the index argument as a required variable name.
    */
   protected String getRequiredVariable(Node node, int idx, String argumentName)
   {
      String var = getRequiredLiteral(node, idx, argumentName);
      return var == null ? null : var.substring(1);
   }

   /**
    * Returns the indexed argument as an optional boolean.
    */
   protected boolean getOptionalBoolean(Node node, int idx,
                                        InternalContextAdapter ctx)
   throws MethodInvocationException
   {
      return getOptionalBoolean(node, idx, ctx, false);
   }

   /**
    * Returns the indexed argument as an optional boolean.
    */
   protected boolean getOptionalBoolean(Node node, int idx,
                                        InternalContextAdapter ctx,
                                        boolean defaultValue)
   throws MethodInvocationException
   {
      Object obj = getOptionalValue(node, idx, ctx);
      if (obj == null || !(obj instanceof Boolean))
         return defaultValue;
      return ((Boolean) obj).booleanValue();
   }
   
   /**
    * Returns an optional argument as a value.
    */
   protected Object getOptionalValue(Node node, int idx,
                                     InternalContextAdapter context)
   throws MethodInvocationException
   {
      Node target = getOptionalNode(node, idx);
      if (target == null) {
         return null;
      }
      return target.value(context);
   }

   /**
    * Returns an optional node.
    */
   protected Node getOptionalNode(Node parent, int idx)
   {
      if (hasArgument(parent, idx)) {
         return parent.jjtGetChild(idx);
      }
      return null;
   }

   /**
    * Validates that a required argument is available.
    */
   protected boolean requireArgument(Node node, int idx, String argName)
   {
      if (!hasArgument(node, idx)) {
         rsvc.error("#" + getName() + "() error :  " + argName + " argument required");
         return false;
      }
      return true;
   }

   /**
    * Returns <code>true</code> if the given specified argument exists.
    */
   protected boolean hasArgument(Node node, int idx)
   {
      return idx < node.jjtGetNumChildren();
   }

   /**
    * Returns the block child node.
    */
   protected Node getBlockNode(Node parent)
   {
      for (int i=0; i<parent.jjtGetNumChildren(); i++) {
         if (parent.jjtGetChild(i) instanceof ASTBlock)
            return parent.jjtGetChild(i);
      }
      return null;
   }

}

