/*
 * XmlIndenterPlugin.java - EditPlugin implementation for the XML Indenter plugin
 *
 * Copyright (c) 2003 Robert McKinnon
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
package xmlindenter;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

/**
 * EditPlugin implementation for the XML Indenter plugin.
 *
 * @author Robert McKinnon - robmckinnon@users.sourceforge.net
 */
public class XmlIndenterPlugin extends EditPlugin {

  /**
   * Displays a user-friendly error message to go with the supplied exception.
   */
  static void processException(Exception e, String message, Component component) {
    StringWriter writer = new StringWriter();
    e.printStackTrace(new PrintWriter(writer));
    Log.log(Log.DEBUG, Thread.currentThread(), writer.toString());
    String msg = MessageFormat.format(jEdit.getProperty("xmlindenter.message.error"),
        new Object[]{message, e.getMessage()});
    JOptionPane.showMessageDialog(component, msg.toString());
  }


  static void showMessageDialog(String property, Component component) {
    String message = jEdit.getProperty(property);
    JOptionPane.showMessageDialog(component, message);
  }


  /**
   * Indents XML in current buffer.
   * @param view
   */
  public static void indentXml(View view) {
    Buffer buffer = view.getBuffer();
    boolean indentWithTabs = getIndentWithTabs(buffer);
    int indentAmount = getIndentAmount(indentWithTabs, buffer);

    buffer.writeLock();
    buffer.beginCompoundEdit();

    try {
      String inputString = buffer.getText(0, buffer.getLength());
      String resultString = XmlIndenterPlugin.indent(inputString, indentAmount, indentWithTabs);

      int caretPosition = view.getTextArea().getCaretPosition();
      buffer.remove(0, buffer.getLength());
      buffer.insert(0, resultString);

      if(caretPosition > (buffer.getLength() - 1)) {
        view.getTextArea().setCaretPosition(buffer.getLength() - 1);
      } else {
        char c = resultString.charAt(caretPosition);

        while(caretPosition < buffer.getLength() && !(c == '>' || c == '<')) {
          caretPosition++; //hack to prevent XML autocomplete of end element name
          c = resultString.charAt(caretPosition);
        }

        if(c == '>') {
          caretPosition++;
        }
        view.getTextArea().setCaretPosition(caretPosition);
      }
    } catch(Exception e) {
      Log.log(Log.ERROR, IndentingTransformerImpl.class, e);
      String message = jEdit.getProperty("xmlindenter.indent.message.failure");
      XmlIndenterPlugin.processException(e, message, view);
    } finally {
      if(buffer.insideCompoundEdit()) {
        buffer.endCompoundEdit();
      }
      buffer.writeUnlock();
    }
  }


  private static boolean getIndentWithTabs(Buffer buffer) {
    boolean tabSizeAppropriate = buffer.getTabSize() <= buffer.getIndentSize();
    return !buffer.getBooleanProperty("noTabs") && tabSizeAppropriate;
  }


  private static int getIndentAmount(boolean indentWithTabs, Buffer buffer) {
    if(indentWithTabs) {
      return buffer.getIndentSize() / buffer.getTabSize();
    } else {
      return buffer.getIndentSize();
    }
  }


  private static String indent(String inputString, int indentAmount, boolean indentWithTabs) throws Exception {
    StringWriter writer = new StringWriter();
    IndentingTransformerImpl transformer = new IndentingTransformerImpl(indentAmount, indentWithTabs);
    transformer.indentXml(inputString, writer);
    String resultString = writer.toString();
//    return removeIn(resultString, '\r'); //remove '\r' to temporarily fix a bug in the display of results in Windows
    return resultString;
  }
}
