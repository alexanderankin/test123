/*
 * XmlParser.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2001 Slava Pestov
 * Portions copyright (C) 2001 David Walend
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml.parser;

//{{{ Imports
import javax.swing.tree.*;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.*;
import java.awt.Component;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;
import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import errorlist.*;
import xml.completion.*;
import xml.*;
//}}}

public class XmlParser implements EBComponent
{
	//{{{ XmlParser constructor
	public XmlParser(View view)
	{
		this.view = view;

		errorSource = new DefaultErrorSource("XML");
		ErrorSource.registerErrorSource(errorSource);

		FocusHandler focusHandler = new FocusHandler();
		EditPane[] editPanes = view.getEditPanes();
		for(int i = 0; i < editPanes.length; i++)
		{
			editPanes[i].getTextArea().addFocusListener(
				focusHandler);
		}

		BufferChangeHandler changeHandler = new BufferChangeHandler();
		Buffer[] buffers = jEdit.getBuffers();
		for(int i = 0; i < buffers.length; i++)
		{
			buffers[i].addBufferChangeListener(changeHandler);
		}

		propertiesChanged();

		EditBus.addToBus(this);
	} //}}}

	//{{{ parse() method
	public void parse(final boolean showParsingMessage)
	{
		stopThread();

		buffer = view.getBuffer();
		this.showParsingMessage = showParsingMessage;

		//{{{ Run this when I/O is complete
		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				EditPane editPane = view.getEditPane();
				editPane.putClientProperty(XmlPlugin.ELEMENT_TREE_PROPERTY,null);
				editPane.putClientProperty(XmlPlugin.COMPLETION_INFO_PROPERTY,null);

				//{{{ check for non-XML file
				if(buffer.getProperty("xml.parser") == null)
				{
					showNotParsedMessage();
					return;
				} //}}}
				//{{{ Show 'parsing in progress' message
				else if(showParsingMessage)
				{
					DefaultMutableTreeNode root = new DefaultMutableTreeNode(buffer.getName());
					model = new DefaultTreeModel(root);

					root.insert(new DefaultMutableTreeNode(
						jEdit.getProperty("xml-tree.parsing")),0);
					model.reload(root);

					editPane.putClientProperty(XmlPlugin.ELEMENT_TREE_PROPERTY,model);

					XmlTree tree = (XmlTree)view.getDockableWindowManager()
						.getDockable(XmlPlugin.TREE_NAME);
					if(tree != null)
						tree.update();
				} //}}}

				_parse();
			}
		}); //}}}
	} //}}}

	//{{{ showNotParsedMessage() method
	public void showNotParsedMessage()
	{
		stopThread();

		buffer = view.getBuffer();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(buffer.getName());
		model = new DefaultTreeModel(root);

		root.insert(new DefaultMutableTreeNode(
			jEdit.getProperty("xml-tree.not-parsed")),0);
		model.reload(root);

		view.getEditPane().putClientProperty(XmlPlugin.COMPLETION_INFO_PROPERTY,null);
		view.getEditPane().putClientProperty(XmlPlugin.ELEMENT_TREE_PROPERTY,model);

		finish();
		return;
	} //}}}

	//{{{ dispose() method
	public void dispose()
	{
		stopThread();

		errorSource.clear();
		ErrorSource.unregisterErrorSource(errorSource);

		EditBus.removeFromBus(this);
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		//{{{ BufferUpdate
		if(msg instanceof BufferUpdate)
		{
			BufferUpdate bmsg = (BufferUpdate)msg;
			if(bmsg.getWhat() == BufferUpdate.CREATED)
			{
				bmsg.getBuffer().addBufferChangeListener(
					new BufferChangeHandler());
			}
			else if(bmsg.getWhat() == BufferUpdate.SAVED
				&& bmsg.getBuffer() == buffer)
			{
				if(buffer.getBooleanProperty(
					"xml.buffer-change-parse")
					|| buffer.getBooleanProperty(
					"xml.keystroke-parse"))
				{
					parse(true);
				}
				else
					showNotParsedMessage();
			}
			else if((bmsg.getWhat() == BufferUpdate.MODE_CHANGED
				|| bmsg.getWhat() == BufferUpdate.LOADED)
				&& bmsg.getBuffer() == buffer)
			{
				if(thread != null)
					return;

				if(buffer.getBooleanProperty(
					"xml.buffer-change-parse")
					|| buffer.getBooleanProperty(
					"xml.keystroke-parse"))
				{
					parse(true);
				}
				else
					showNotParsedMessage();
			}
		} //}}}
		//{{{ EditPaneUpdate
		else if(msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate)msg;
			EditPane editPane = epu.getEditPane();

			if(epu.getWhat() == EditPaneUpdate.CREATED)
				editPane.getTextArea().addFocusListener(new FocusHandler());
			else if(epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
			{
				// check if this is the currently focused edit pane
				if(editPane == editPane.getView().getEditPane())
				{
					Buffer buffer = editPane.getBuffer();

					if(buffer.getBooleanProperty(
						"xml.buffer-change-parse")
						|| buffer.getBooleanProperty(
						"xml.keystroke-parse"))
					{
						parse(true);
					}
					else
						showNotParsedMessage();
				}
				else
				{
					view.getEditPane().putClientProperty(
						XmlPlugin.COMPLETION_INFO_PROPERTY,null);
					view.getEditPane().putClientProperty(
						XmlPlugin.ELEMENT_TREE_PROPERTY,null);
				}
			}
		} //}}}
	} //}}}

	//{{{ getBuffer() method
	public Buffer getBuffer()
	{
		return buffer;
	} //}}}

	//{{{ getText() method
	public String getText()
	{
		return text;
	} //}}}

	//{{{ addError() method
	public void addError(int type, String path, int line, String message)
	{
		errorSource.addError(type,path,line,0,0,message);
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private View view;
	private Buffer buffer;

	private DefaultTreeModel model;

	private ParseThread thread;

	private String text;

	private Impl parserImpl;

	private DefaultErrorSource errorSource;

	private boolean showParsingMessage;

	private int delay;
	private Timer keystrokeTimer;
	//}}}

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		try
		{
			delay = Integer.parseInt(jEdit.getProperty("xml.auto-parse-delay"));
		}
		catch(NumberFormatException nf)
		{
			delay = 1500;
		}
	} //}}}

	//{{{ parseWithDelay() method
	private void parseWithDelay()
	{
		if(keystrokeTimer != null)
			keystrokeTimer.stop();

		keystrokeTimer = new Timer(0,new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				parse(false);
			}
		});

		keystrokeTimer.setInitialDelay(delay);
		keystrokeTimer.setRepeats(false);
		keystrokeTimer.start();
	} //}}}

	//{{{ _parse() method
	private void _parse()
	{
		errorSource.clear();

		// get buffer text
		this.buffer = buffer;
		text = buffer.getText(0,buffer.getLength());

		// start parser thread
		stopThread();

		if(buffer.getProperty("xml.parser").equals("xml"))
			parserImpl = new SAXParserImpl();
		else if(buffer.getProperty("xml.parser").equals("html"))
			parserImpl = new SwingHTMLParserImpl();

		thread = new ParseThread();
		thread.start();
	} //}}}

	//{{{ stopThread() method
	private void stopThread()
	{
		if(thread != null)
		{
			thread.stop();
			thread = null;
		}
	} //}}}

	//{{{ finish() method
	private void finish()
	{
		if(view.isClosed())
			return;

		thread = null;

		// to avoid keeping pointers to stale objects.
		// we could reuse a single parser instance to preserve
		// performance, but it eats too much memory, especially
		// if the file being parsed has an associated DTD.
		text = null;
		parserImpl = null;

		XmlTree tree = (XmlTree)view.getDockableWindowManager()
			.getDockable(XmlPlugin.TREE_NAME);
		if(tree != null)
			tree.update();

		XmlInsert insert = (XmlInsert)view.getDockableWindowManager()
			.getDockable(XmlPlugin.INSERT_NAME);
		if(insert != null)
			insert.update();
	} //}}}

	//}}}

	//{{{ Inner classes

	//{{{ Impl interface
	interface Impl
	{
		void parse(XmlParser parser, Reader in) throws IOException;
		TreeNode getElementTree();
		CompletionInfo getCompletionInfo();
	} //}}}

	//{{{ ParseThread class
	class ParseThread extends Thread
	{
		ParseThread()
		{
			super("XML parser thread");
			setPriority(Thread.MIN_PRIORITY);
		}

		public void run()
		{
			try
			{
				StringReader in = new StringReader(text);

				parserImpl.parse(XmlParser.this,in);
			}
			catch(IOException ioe)
			{
				Log.log(Log.ERROR,this,ioe);
				addError(ErrorSource.ERROR,buffer.getPath(),0,
				ioe.toString());
			}

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					int errorCount = errorSource.getErrorCount();

					if(showParsingMessage || errorCount != 0)
					{
						Object[] pp = { new Integer(errorCount) };
						view.getStatus().setMessageAndClear(jEdit.getProperty(
							"xml-tree.parsing-complete",pp));
					}

					if(parserImpl == null)
						model = null;
					else
						model = new DefaultTreeModel(parserImpl.getElementTree());

					view.getEditPane().putClientProperty(
						XmlPlugin.ELEMENT_TREE_PROPERTY,
						model);

					CompletionInfo completionInfo = parserImpl.getCompletionInfo();
					if(completionInfo != null)
					{
						view.getEditPane().putClientProperty(
							XmlPlugin.COMPLETION_INFO_PROPERTY,
							completionInfo);
					}

					finish();
				}
			});
		}
	} //}}}

	//{{{ BufferChangeHandler class
	class BufferChangeHandler extends BufferChangeAdapter
	{
		//{{{ contentInserted() method
		public void contentInserted(Buffer buffer, int startLine, int offset,
			int numLines, int length)
		{
			if(buffer == XmlParser.this.buffer
				&& buffer.isLoaded()
				&& buffer.getProperty("xml.parser") != null
				&& buffer.getBooleanProperty("xml.keystroke-parse"))
				parseWithDelay();
		} //}}}

		//{{{ contentRemoved() method
		public void contentRemoved(Buffer buffer, int startLine, int offset,
			int numLines, int length)
		{
			if(buffer == XmlParser.this.buffer
				&& buffer.isLoaded()
				&& buffer.getProperty("xml.parser") != null
				&& buffer.getBooleanProperty("xml.keystroke-parse"))
				parseWithDelay();
		} //}}}
	} //}}}

	//{{{ FocusHandler class
	class FocusHandler extends FocusAdapter
	{
		public void focusGained(FocusEvent evt)
		{
			View view = GUIUtilities.getView((Component)evt.getSource());
			Buffer buffer = view.getBuffer();

			if(buffer.getBooleanProperty(
				"xml.buffer-change-parse")
				|| buffer.getBooleanProperty(
				"xml.keystroke-parse"))
			{
				if(buffer != XmlParser.this.buffer)
					parse(true);
				else
				{
					// XXX: expand tree to caret pos
				}
			}
			else
				showNotParsedMessage();
		}
	} //}}}

	//}}}
}
