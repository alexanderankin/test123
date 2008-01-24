/*
 * BeanShell.java
 * Copyright (c) 2002 Calvin Yu
 * Copyright (c) 2008 Steve Jakob
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

import org.gjt.sp.jedit.bsh.UtilEvalError;
import org.gjt.sp.jedit.bsh.NameSpace;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import velocity.VelocityConstants;

/**
 * A directive to execute a beanshell script.
 */
public class BeanShell extends SimpleDirective
    implements VelocityConstants
{

   /**
    * Return name of this directive.
    */
   public String getName()
   {
      return "beanshell";
   }

   /**
    * Return type of this directive.
    */
   public int getType()
   {
      return BLOCK;
   }

   /**
    * Execute the bean shell script.
    */
   public boolean render(InternalContextAdapter context,
                         Writer writer, Node node)
   throws MethodInvocationException, IOException
   {
      if (node.jjtGetChild(0) == null) {
         rsvc.error("#beanshell() error :  null script");
         return false;
      }
      boolean writeResult = getOptionalBoolean(node, 0, context, true);

      View view = (View) context.get(VIEW);
      NameSpace contextNS = new NameSpace(org.gjt.sp.jedit.BeanShell.getNameSpace(),
                                          "velocity_context");
      try {
         contextNS.setVariable("context", context.getInternalUserContext());
         Object result = org.gjt.sp.jedit.BeanShell.eval(view, contextNS,
                                                         getBlockNode(node).literal());
         if (writeResult && result != null) {
            writer.write(result.toString());
         }
         return true;
      } catch (UtilEvalError e) {
         Log.log(Log.ERROR, this, "Error evaluating template script");
         Log.log(Log.ERROR, this, e);
         return false;
      }
   }

}

