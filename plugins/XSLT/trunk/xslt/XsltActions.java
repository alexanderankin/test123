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
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.MiscUtilities;

import javax.swing.JOptionPane;
import java.util.Hashtable;

import org.xml.sax.SAXParseException;

import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

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
  
  public static void compileStylesheet(View view, Buffer buffer){
	  String path = buffer.getPath();
  	  ErrorListenerToErrorList listener = new ErrorListenerToErrorList("");
  	  try {
  	  	  
  	  	  // clear any existing error
  	  	  XSLTPlugin.getErrorSource().removeFileErrors(path);
  	  	  
  	  	  XSLTUtilities.compileStylesheet(path, listener);
  	  	  
  	  } catch (Exception e) {
  	  	  Log.log(Log.ERROR,XsltActions.class,e);
  	  }
  	  int nbErrors = XSLTPlugin.getErrorSource().getFileErrorCount(path);
  	  //can be pretty annoying... if(nbErrors > 0)java.awt.Toolkit.getDefaultToolkit().beep();
  	  String message = jEdit.getProperty("xslt.compile.finished",new Object[]{nbErrors});
  	  view.getStatus().setMessage(message);
  }

  public static void initThreeWayMode(View view){
  	  if(view.getEditPanes().length != 3)
  	  {
  	  	  InputStream in = null;
  	  	  String xmlTemplate = "<?xml version=\"1.0\" ?>";
  	  	  String xslTemplate = null;
  	  	  try{
  	  	  	  URL xslTemplateURL;
			  if(XSLTUtilities.getXSLTProcessorVersion()==1) {
				  xslTemplateURL = XSLTPlugin.class.getResource("/templates/xslt-1.0.vm");
			  } else {
				  xslTemplateURL = XSLTPlugin.class.getResource("/templates/xslt-2.0.vm");
			  }
  	  	  	  
			  in = xslTemplateURL.openStream();
			  ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
  	  	  	  
			  if(IOUtilities.copyStream(1024,null,in,out,false)){
			  	  xslTemplate = out.toString();
			  }
			  
  	  	  }catch(IOException ioe){
  	  	  	  Log.log(Log.ERROR,XsltActions.class,"error copying template");
  	  	  	  Log.log(Log.ERROR,XsltActions.class,ioe);
		  }finally{
		  	  if(in != null){
				  try{
					  in.close();
				  }catch(IOException ioe){
					  Log.log(Log.ERROR,XsltActions.class,"error copying template");
					  Log.log(Log.ERROR,XsltActions.class,ioe);
				  }
			  }
		  }
  	  	  String dir = MiscUtilities.getParentOfPath(view.getBuffer().getPath());
  	  	  // set up 3 untitled buffers
  	  	  view.unsplit();
  	  	  view.splitHorizontally();
  	  	  view.splitVertically();
  	  	  EditPane[] editPanes = view.getEditPanes();
  	  	  String[]modes = { "xml", "xsl", "text" };
  	  	  String[]templates = { xmlTemplate, xslTemplate, ""};
  	  	  
  	  	  for(int i=0;i<modes.length;i++){
  	  	  	  Buffer b = jEdit.newFile(editPanes[i]);
  	  	  	  b.setMode(modes[i]);
  	  	  	  b.insert(0,templates[i]);
  	  	  }
  	  	  
  	  	  
  	  }
  	  DockableWindowManager dwm = view.getDockableWindowManager();
  	  dwm.showDockableWindow("xslt-processor");
  	  XSLTProcessor processor = getXsltProcessor(view);
  	  processor.setThreeWay(true);
  }
}
