/*
 * XSLTProcessor.java - Actual XSLT Processor
 * Copyright (C) 2001 Carlos Quiroz
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

import gnu.regexp.REException;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
import org.apache.xalan.xslt.*;
import org.apache.xalan.transformer.*;
import org.apache.xalan.trace.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.*;
import java.io.*;


public class XSLTProcessor extends java.lang.Object
{

	private static XSLTProcessor instance;

	private File lastPath;
	private XSLTFileFilter xslFilter;
	private XSLTFileFilter xmlFilter;
	private File lastXML, lastXSL;
	private javax.xml.transform.TransformerFactory tFactory;
	//private XSLTProgressDialog progress;

	private XSLTProcessor() {
		xslFilter = new XSLTFileFilter("xsl", "XSL Files (*.xsl)");
		xmlFilter = new XSLTFileFilter("xml", "XML Files (*.xml)");
		//progress = new XSLTProgressDialog(jEdit.getFirstView(), jEdit.getProperty("xslt.progressdialog.title"));

		tFactory = javax.xml.transform.TransformerFactory.newInstance();
		tFactory.setURIResolver(new PluginURIResolver());
	}

	public static synchronized XSLTProcessor getInstance() {
		if (instance == null) {
			instance = new XSLTProcessor();
		}
		return instance;
	}

	public void processFile(View view, Buffer buffer) {
		if (buffer.getMode().getName().equals("xml")) {

			// Discover if there are implicit stylesheet
			Source stylesheet = null;
			try {
				String media = null , title = null, charset = null;
				stylesheet = tFactory.getAssociatedStylesheet
					(new StreamSource(buffer.getFile().getAbsolutePath()),media, title, charset);
			} catch (TransformerConfigurationException e) {
				// no styleshee, ingnore
				Macros.error(view, e.getMessage());
			}

			String xslFilename = null;
			if (stylesheet == null) {
				JFileChooser chooser = new JFileChooser();
				if (lastPath != null) {
					chooser.setCurrentDirectory(lastPath);
				}
				chooser.setFileFilter(xslFilter);
				int returnVal = chooser.showOpenDialog(view);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					 xslFilename = chooser.getSelectedFile().getAbsolutePath();
					 lastPath = chooser.getSelectedFile();
				} else {
					return;
				}
			} else {
				Log.log(Log.WARNING, this, stylesheet.toString());
			}

			try {
				if (stylesheet == null) {
					doTransformation(view, xslFilename, buffer.getPath());
				} else {
					doTransformation(view, stylesheet, buffer.getPath());
				}
			} catch (javax.swing.text.BadLocationException e) {
			} catch (TransformerException e) {
				Macros.error(view, "Exception processing file: " + e.getMessageAndLocation());
			}
		}
		if (buffer.getMode().getName().equals("xsl")) {
			JFileChooser chooser = new JFileChooser();
			if (lastPath != null) {
				chooser.setCurrentDirectory(lastPath);
			}
			chooser.setFileFilter(xmlFilter);
			int returnVal = chooser.showOpenDialog(view);
			String xmlFilename = null;
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				 xmlFilename = chooser.getSelectedFile().getAbsolutePath();
				 lastPath = chooser.getSelectedFile();
			} else {
				return;
			}

			try {
				doTransformation(view, buffer.getPath(), xmlFilename);
			} catch (javax.swing.text.BadLocationException e) {
				Macros.error(view, "Exception processing file: " + e.getMessage());
			} catch (TransformerException e) {
				Macros.error(view, "Exception processing file: " + e.getMessageAndLocation());
			}
		}
	}

	public static void repeatLast(View view) {
		getInstance().processLast(view);
	}

	protected void doTransformation(View view, String xslPath, String xmlPath)
		throws TransformerException, javax.swing.text.BadLocationException {
		lastXSL = new File(xslPath);


		Source stylesheet = new StreamSource(xslPath);
		doTransformation(view, stylesheet, xmlPath);
	}

	protected void doTransformation(View view, Source stylesheet, String xmlPath)
		throws TransformerException, javax.swing.text.BadLocationException {
		try {
			//progress.reset();
			//progress.setVisible(true);
			//progress.toFront();
			lastXML = new File(xmlPath);

			TransformerImpl transformer = (TransformerImpl)tFactory.newTransformer(stylesheet);
			/*TraceManager traceManager = new TraceManager(transformer);
			try {
				traceManager.addTraceListener(progress);
			} catch (TooManyListenersException e) {
				// nothing to do
			}*/

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			transformer.transform
				(new StreamSource(xmlPath),
					new StreamResult(output));
			Buffer transformed = jEdit.newFile(view);
			transformed.setMode(jEdit.getMode("xml"));
			transformed.insertString(0, output.toString(), null);
		} finally {
			//progress.setVisible(false);
		}
	}

	public void processLast(View view) {
		if (lastXSL != null && lastXML != null) {

			try {
				doTransformation(view, lastXSL.getAbsolutePath(), lastXML.getAbsolutePath());
			} catch (javax.swing.text.BadLocationException e) {
			} catch (TransformerException e) {
				Macros.error(view, "Exception processing file: " + e.getMessageAndLocation());
			}
		}
	}

	public String checkEnvironment() {
		boolean environmentOk = false;
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(buffer));
			environmentOk = (new EnvironmentCheck()).checkEnvironment(writer);

			if (environmentOk) {
				return "Environment Ok";
			} else {
				Properties props = new Properties();
				try {
					ByteArrayInputStream input = new ByteArrayInputStream(buffer.toByteArray());

					props.load(input);
				} catch (IOException e) {
				}
				StringBuffer result = new StringBuffer();
				Enumeration lines = props.propertyNames();
				while (lines.hasMoreElements()) {
					String key = (String)lines.nextElement();
					result.append(key);
					result.append(" = ");
					result.append(props.getProperty(key));
					result.append('\n');
				}

				return props.toString();
			}
		} catch (NoClassDefFoundError e) {
		}
		return "No class found";
	}


	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu("XSLT"));
	}


	public void handleMessage(EBMessage msg)
	{
	}

	private class XSLTFileFilter extends javax.swing.filechooser.FileFilter {
		private String ext, textExt;

		public XSLTFileFilter(String ext, String textExt) {
			this.ext = ext;
			this.textExt = textExt;
		}

		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String fext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 &&  i < s.length() - 1) {
				fext = s.substring(i+1).toLowerCase();
			}

			if (fext != null && fext.equals(ext)) {
				return true;
			} else {
				return false;
			}
		}

		public String getDescription() {
			return textExt;
		}
	}


}
