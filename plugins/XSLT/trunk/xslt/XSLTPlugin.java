/*
 * XSLTPlugin.java - XSLT Plugin
 *
 * Copyright (c) 2002 Greg Merrill
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

package xslt;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

/**
 * EditPlugin implementation for the XSLT plugin.
 */
public class XSLTPlugin extends EditPlugin {

  /**
   * Register xerces as the SAX Parser provider
   */
  public void start () {
    System.setProperty("javax.xml.parsers.SAXParserFactory", 
      "org.apache.xerces.jaxp.SAXParserFactoryImpl");
  }

  /**
   * Adds appropriate actions to the plugins menu
   */
  public void createMenuItems (Vector menuItems) {
		menuItems.addElement(GUIUtilities.loadMenu("xslt-menu"));
  }

  /**
   * @return shared XSLTProcessor instance for the supplied view
   */
  public static synchronized XSLTProcessor getXSLTProcessorInstance (View view) {
    XSLTProcessor xsltProcessor = (XSLTProcessor)xsltProcessors.get(view);
    if (xsltProcessor == null) {
      xsltProcessor = new XSLTProcessor(view);
      xsltProcessors.put(view, xsltProcessor);
    }
    return xsltProcessor;
  }

  /**
   * @return shared XPathTool instance for the supplied view
   */
  public static synchronized XPathTool getXPathToolInstance (View view) {
    XPathTool xPathTool = (XPathTool)xPathTools.get(view);
    if (xPathTool == null) {
      xPathTool = new XPathTool(view);
      xPathTools.put(view, xPathTool);
    }
    return xPathTool;
  }

  /**
   * Displays a user-friendly error message to go with the supplied exception.
   */
  static void processException (Exception e, String message, Component component) {
    StringWriter writer = new StringWriter();
    e.printStackTrace(new PrintWriter(writer));
    Log.log(Log.DEBUG, Thread.currentThread(), writer.toString());
    String msg = MessageFormat.format(jEdit.getProperty("xslt.error"),
      new Object[]{message, e.getMessage()});
    JOptionPane.showMessageDialog(component, msg.toString());
  }

  private static Map xsltProcessors = new HashMap();
  private static Map xPathTools = new HashMap();

}

