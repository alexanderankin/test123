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

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import javax.swing.JOptionPane;

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


}
