/*
 * XSLTPlugin.java - XSLT Plugin
 *
 * Copyright (c) 2002 Greg Merrill
 *               2003 Robert McKinnon
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

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Vector;

/**
 * EditPlugin implementation for the XSLT plugin.
 *
 * @author Greg Merrill
 * @author Robert McKinnon - robmckinnon@users.sourceforge.net
 */
public class XSLTPlugin extends EditPlugin {

  /**
   * Register xerces as the SAX Parser provider
   */
  public void start () {
    String transformerFactory = jEdit.getProperty(XSLTUtilities.TRANSFORMER_FACTORY);
    String saxParserFactory = jEdit.getProperty(XSLTUtilities.SAX_PARSER_FACTORY);
    String saxDriver = jEdit.getProperty(XSLTUtilities.SAX_DRIVER);
    String indentAmount = jEdit.getProperty("xslt.transform.indent-amount");

    XSLTUtilities.setXmlSystemProperties(transformerFactory, saxParserFactory, saxDriver);
    XSLTUtilities.setIndentAmount(indentAmount);
  }


  /**
   * Adds appropriate actions to the plugins menu
   */
  public void createMenuItems (Vector menuItems) {
		menuItems.addElement(GUIUtilities.loadMenu("xslt-menu"));
  }


  /**
   * Displays a user-friendly error message to go with the supplied exception.
   */
  static void processException(Exception e, String message, Component component) {
    StringWriter writer = new StringWriter();
    e.printStackTrace(new PrintWriter(writer));
    Log.log(Log.DEBUG, Thread.currentThread(), writer.toString());
    String msg = MessageFormat.format(jEdit.getProperty("xslt.message.error"),
      new Object[]{message, e.getMessage()});
    JOptionPane.showMessageDialog(component, msg.toString());
  }


  static void showMessageDialog(String property, Component component) {
    String message = jEdit.getProperty(property);
    JOptionPane.showMessageDialog(component, message);
  }


  static void showMessageDialog(String property, String args[], Component component) {
    String message = jEdit.getProperty(property, args);
    JOptionPane.showMessageDialog(component, message);
  }
}

