/*
 *  XsltActions.java - Contains static action methods for XSLT plugin
 *
 *  Copyright (C) 2003 Robert McKinnon
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package xslt;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

import javax.swing.*;
import java.io.StringWriter;

/**
 * Contains static action methods for XSLT plugin
 *
 * @author Robert McKinnon - robmckinnon@users.sourceforge.net
 */
public class XsltActions {

  /**
   * Performs XSLT transformation.
   */
  public static void transformXml(View view) {
    XSLTProcessor xsltProcessor = getXsltProcessor(view);

    if(xsltProcessor != null) {
      xsltProcessor.clickTransformButton();
    }
  }


  /**
   * Attempts to load XSLT settings from a user specified file.
   */
  public static void loadSettings(View view) {
    XSLTProcessor xsltProcessor = getXsltProcessor(view);

    if(xsltProcessor != null) {
      xsltProcessor.clickLoadSettingsButton();
    }
  }


  /**
   * Attempts to save XSLT settings to a user specified file.
   */
  public static void saveSettings(View view) {
    XSLTProcessor xsltProcessor = getXsltProcessor(view);

    if(xsltProcessor != null) {
      xsltProcessor.clickSaveSettingsButton();
    }
  }


  private static XSLTProcessor getXsltProcessor(View view) {
    XSLTProcessor xsltProcessor = (XSLTProcessor)view.getDockableWindowManager().getDockable("xslt-processor");

    if(xsltProcessor == null) {
      JOptionPane.showMessageDialog(view, jEdit.getProperty("xslt.message.dock-first"));
    }

    return xsltProcessor;
  }


  /**
   * Evaluates XPath expression.
   * @param view
   */
  public static void evaluateXpath(View view) {
    XPathTool xpathTool = (XPathTool)view.getDockableWindowManager().getDockable("xpath-tool");

    if(xpathTool == null) {
      JOptionPane.showMessageDialog(view, jEdit.getProperty("xpath.message.dock-first"));
    } else {
      xpathTool.clickEvaluateButton();
    }
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
      String resultString = XsltActions.indent(inputString, indentAmount, indentWithTabs);
      buffer.remove(0, buffer.getLength());
      buffer.insert(0, resultString);
      view.getTextArea().setCaretPosition(0);
    } catch(Exception e) {
      Log.log(Log.ERROR, IndentingTransformerImpl.class, e);
      String message = jEdit.getProperty("xslt.indent.message.failure");
      XSLTPlugin.processException(e, message, view);
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
      return  buffer.getIndentSize();
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
