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
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

import errorlist.ErrorSource;
import errorlist.DefaultErrorSource;

import javax.xml.transform.TransformerException;

/**
 * EditPlugin implementation for the XSLT plugin.
 *
 * @author Greg Merrill
 * @author Robert McKinnon - robmckinnon@users.sourceforge.net
 */
public class XSLTPlugin extends EBPlugin implements EBComponent{

	private static DefaultErrorSource errorSource;
	public static String COMPILE_ON_SAVE_PROP="xslt.compile-on-save";
	
	private boolean compileOnSave = false;
	
	public XSLTPlugin() {
	}

	/**
	 * Register xerces as the SAX Parser provider
	 */
	public void start() {
		String saxParserFactory = jEdit.getProperty(XSLTUtilities.SAX_PARSER_FACTORY);
		String saxDriver = jEdit.getProperty(XSLTUtilities.SAX_DRIVER);
		String indentAmount = jEdit.getProperty("xslt.transform.indent-amount");

		// avoid setting system properties
		//XSLTUtilities.setXmlSystemProperties(saxParserFactory, saxDriver);
		XSLTUtilities.setIndentAmount(indentAmount);
		
		compileOnSave = jEdit.getBooleanProperty(COMPILE_ON_SAVE_PROP);
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
		Log.log(Log.ERROR, XSLTPlugin.class, "complete exception:"+e.toString());
		while(e.getCause() != null && e.getCause() instanceof TransformerException){
			System.out.println("exception : "+e);
			e = (Exception) e.getCause();
		}
		Log.log(Log.ERROR, XSLTPlugin.class, e);
		String msg = MessageFormat.format(jEdit.getProperty("xslt.message.error"),
				new Object[]{message, e.getMessage()});
		JOptionPane.showMessageDialog(component, msg.toString());
	}


	static void showMessageDialog(String property, Component component) {
		String message = jEdit.getProperty(property);
		JOptionPane.showMessageDialog(component, message);
	}

	public void handleMessage(EBMessage message) {
		if (jEdit.getFirstView() == null)
			return;
        XPathTool xpathTool = (XPathTool)jEdit.getFirstView().getDockableWindowManager().getDockable("xpath-tool");
        if (xpathTool != null){
        	xpathTool.handleMessage(message);
        }
        if(message instanceof PropertiesChanged){
        	compileOnSave = jEdit.getBooleanProperty(COMPILE_ON_SAVE_PROP);
        }else if(compileOnSave && (message instanceof BufferUpdate)){
        	final BufferUpdate buMessage = (BufferUpdate)message;
        	if(BufferUpdate.SAVED == buMessage.getWhat()){
        		final Buffer b = buMessage.getBuffer();
        		if("xsl".equals(b.getMode().getName())){
        			SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								XsltActions.compileStylesheet(buMessage.getView(),b);
							}
						});
					
        		}
        	}
        }
	}
	
	
}
