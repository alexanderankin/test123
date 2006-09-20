/*
 *
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
import javax.swing.text.BadLocationException;

public class InsertTextCommand extends java.lang.Object implements Command
{
  public static void insertText(String text, ScriptContext sc)
  {
    InsertTextCommand co = new InsertTextCommand(text);
    co.run(sc);
  }

  /* constructor */
  public InsertTextCommand(String text)
  {
    this.text = text;
  }

  public void run(ScriptContext sc)
  {
    View v = sc.getView();
    Buffer b = v.getBuffer();
    JEditTextArea textArea = v.getTextArea();
    String t = MiscUtilities.escapesToChars(text);
    StringBuffer buf = new StringBuffer(t.length());
    String selected;
    if(textArea.getSelectedText() != null)
      selected = textArea.getSelectedText();
      else selected = "";
      char c;
      for(int i=0; i<t.length(); i++)
      {
        c = t.charAt(i);
        if(c == '|')
        {
          if(i < t.length() -1 && t.charAt(i + 1) == '|')
          {
            i++;
            buf.append(c);
          }
          else
          {
            buf.append(selected);
            charPos = i;
          }
        }
        else
          buf.append(c);
      }
      int pos = textArea.getCaretPosition();
      b.insert(pos, buf.toString());
      if(charPos != -1)
        sc.getCommandQueue().addLast(new SetCursorPositionCommand(pos + charPos));
      textArea.setCaretPosition(pos + buf.length());
  }

  private String text;
  private int charPos = -1;
}

