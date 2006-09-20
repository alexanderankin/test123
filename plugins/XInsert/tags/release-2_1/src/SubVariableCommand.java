/*
 *
 * SubVariableCommand.java
 * Copyright (C) 2001 Dominic Stolerman
 * dstolerman@jedit.org
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

 
import org.gjt.sp.jedit.*;

/**
 *
 * @author  Dominic Stolerman
 */
public class SubVariableCommand extends Object implements Command
{

    /** Creates new SubVariableCommand */
  public SubVariableCommand(String variable) 
  {
    this.variable = variable;
  }

    public void run(ScriptContext sc)
    {
      View parent = sc.getView();
      XTreeNode node = sc.getNode();
      String key = variable.substring(2, XScripter.findWordEnd(variable, 3, null));
      String value;
       boolean isTempValue = true;
       if(variable.charAt(0) == '=')
       {
         value = XScripter.getSubstituteFor(parent, key, node);
         if (value == null)
         value = "";
       }
       else if(variable.charAt(0) == '?')
       {
         value = XScripter.getSubstituteFor(parent, key, (XTreeNode)node.getParent());
         if(value == null)
         {
           value = XScripter.showInputDialog(parent, key, node.getVariable(key));
           node.addVariable(key, value);
         }
       }
       else
       {
         value = XScripter.getSubstituteFor(parent, key, node);
         if(value == null)
           value = key;
       }
       InsertTextCommand.insertText(value, sc);
    }
    
    private final String variable;
}
