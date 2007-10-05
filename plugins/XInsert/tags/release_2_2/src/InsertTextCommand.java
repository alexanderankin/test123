/*
 * InsertTextCommand.java
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


/**
*@author Dominic Stolerman
*/

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;

public class InsertTextCommand extends java.lang.Object implements Command {

  public static void insertText(String text, ScriptContext context) {
    InsertTextCommand cmd = new InsertTextCommand(text);
    cmd.run(context);
    }

  /* constructor */
  public InsertTextCommand(String text) {
    this.text = text;
    // this.src = MiscUtilities.escapesToChars(text);
    this.src = text;
    this.result = new StringBuffer(src.length());
    this.i = 0;
    }

  public void run(ScriptContext context) {
    XTreeNode node = context.getNode();
    View view = context.getView();
    JEditTextArea textarea = view.getTextArea();
    Buffer buffer = view.getBuffer();
    int tabSize = buffer.getTabSize();

    char c;
    boolean braced;
    int caretpos = -1;

    for (i = 0; i < src.length(); i++) {
      c = src.charAt(i);
      switch(c) {
      case '|':
        // double '|'
        if(nextCharIs('|')) {
            i++;
            result.append(c);
            }
        // insert selection
        else {
            result.append(context.getSelection());
            caretpos = result.length();
            }
        // pos = j;
        break;
      case '\\':
        // new line
        if(nextCharIs('n')) {
           i++;
           result.append('\n');
           }
        // tab
        else if(nextCharIs('t')) {
           i++;
           if(jEdit.getBooleanProperty("buffer.noTabs", false))
             for(int k = 0; k < tabSize; k++)
               result.append(" ");
           else
             result.append("\t");
             }
        // escaped dollar sign
        else if(nextCharIs('$')) {
           i++;
           result.append('$');
           }
        // escaped pipe
        else if(nextCharIs('|')) {
           i++;
           result.append('|');
           }
        // escaped backslash
        else if(nextCharIs('\\')) {
           i++;
           result.append(c);
           }
	    else
           result.append(c);
        break;
        // insert variable
      case '$':
	        braced = nextCharIs('{');
            // insertText ${varname}
	        if(braced) i++;
            i++;
            int temp = i;
            while(i < src.length() && Character.isLetterOrDigit(src.charAt(i)))
              i++;
            // Log.log(Log.DEBUG, this, "$ = " + src.substring(temp, i));
            String val = XScripter.getSubstituteFor(view, src.substring(temp, i), node);
            if(val == null)
              result.append(src.substring(temp, i));
            else
              result.append(val);
            if(!braced) i--;
        break;
      default:
        result.append(c);
        break;
      }
    }
    if(jEdit.getBooleanProperty("xtree.carriage", false))
      result.append('\n');
    int caret = textarea.getCaretPosition();
    int len = result.length();
    if(len > 0) {
      textarea.setSelectedText(result.toString());
      // set caret to end of insert or last "|" position
      textarea.setCaretPosition(caretpos > -1
	      ? caret + caretpos
	      : caret + len
	      );
      }
    }

  // enhances source readability
  private boolean nextCharIs(char ch) {
      return (i < src.length() - 1) && (src.charAt(i + 1) == ch);
      }

  private int i;
  private String text;
  private String src;
  private StringBuffer result;
}

