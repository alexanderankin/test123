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

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

import errorlist.ErrorSource;
import errorlist.DefaultErrorSource;

/**
 * EditPlugin implementation for the XSLT plugin.
 *
 * @author Greg Merrill
 * @author Robert McKinnon - robmckinnon@users.sourceforge.net
 */
public class XSLTPlugin extends EBPlugin implements EBComponent{

	private static XSLTProcessor processor;
	private static DefaultErrorSource errorSource;
	
	int ii;
	
	public XSLTPlugin() {
		ii = 10;
	}

	/**
	 * Register xerces as the SAX Parser provider
	 */
	public void start() {
		String transformerFactory = jEdit.getProperty(XSLTUtilities.TRANSFORMER_FACTORY);
		String saxParserFactory = jEdit.getProperty(XSLTUtilities.SAX_PARSER_FACTORY);
		String saxDriver = jEdit.getProperty(XSLTUtilities.SAX_DRIVER);
		String indentAmount = jEdit.getProperty("xslt.transform.indent-amount");

		XSLTUtilities.setXmlSystemProperties(transformerFactory, saxParserFactory, saxDriver);
		XSLTUtilities.setIndentAmount(indentAmount);
	}

	public void stop() {
		if (jEdit.getFirstView() == null)
			return;
        XPathTool xpathTool = (XPathTool)jEdit.getFirstView().getDockableWindowManager().getDockable("xpath-tool");
        if (xpathTool != null)
        {
        	xpathTool.stop();
        }
        if(errorSource != null)
        {
			ErrorSource.unregisterErrorSource(errorSource);
			errorSource = null;
		}
	}
	
	static DefaultErrorSource getErrorSource() {
		if(errorSource==null){
			errorSource=new DefaultErrorSource("XSLTPlugin");
			ErrorSource.registerErrorSource(errorSource);
		}
		return errorSource;	
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


	static void setProcessor(XSLTProcessor processor) {
		XSLTPlugin.processor = processor;
	}


	static void displayOldXalanJarMessage() {
		String message = getOldXalanJarMessage();
		JOptionPane.showMessageDialog(XSLTPlugin.processor, message);
	}


	static String getOldXalanJarMessage() {
		String userPluginsDir = MiscUtilities.constructPath(jEdit.getSettingsDirectory(), "jars");
		String userEndorsedDir = MiscUtilities.constructPath(userPluginsDir, "endorsed");

		String systemPluginsDir = MiscUtilities.constructPath(jEdit.getJEditHome(), "jars");
		String systemEndorsedDir = MiscUtilities.constructPath(systemPluginsDir, "endorsed");

		String[] args = {userPluginsDir, systemPluginsDir, userEndorsedDir, systemEndorsedDir};
		String message = jEdit.getProperty("xslt.old-jar.message", args);
		return message;
	}

	/* (non-Javadoc)
	 * @see org.gjt.sp.jedit.EBComponent#handleMessage(org.gjt.sp.jedit.EBMessage)
	 */
	public void handleMessage(EBMessage message) {
		// TODO Auto-generated method stub
		if (jEdit.getFirstView() == null)
			return;
        XPathTool xpathTool = (XPathTool)jEdit.getFirstView().getDockableWindowManager().getDockable("xpath-tool");
        if (xpathTool == null)
        	return;
        xpathTool.handleMessage(message);
	}


}
