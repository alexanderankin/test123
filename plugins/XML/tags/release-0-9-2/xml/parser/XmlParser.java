/*
 * XmlParser.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2001, 2002 Slava Pestov
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
	public static final int MAX_ERRORS = 100;

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

		bufferHandler = new BufferChangeHandler();

		propertiesChanged();

		keystrokeTimer = new Timer(0,new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				parse(false);
			}
		});

		EditBus.addToBus(this);
	} //}}}

	//{{{ parse() method
	public void parse(final boolean showParsingMessage)
	{
		stopThread();

		maxErrors = false;

		buffer = view.getBuffer();
		this.showParsingMessage = showParsingMessage;

		//{{{ Run this when I/O is complete
		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				if(thread != null)
					return;

				EditPane editPane = view.getEditPane();
				editPane.putClientProperty(XmlPlugin.ELEMENT_TREE_PROPERTY,null);
				editPane.putClientProperty(XmlPlugin.COMPLETION_INFO_PROPERTY,null);
				editPane.putClientProperty(XmlPlugin.IDS_PROPERTY,null);

				//{{{ check for non-XML file
				if(XmlPlugin.getParserType(buffer) == null)
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

	//{{{ dispose() method
	public void dispose()
	{
		stopThread();

		errorSource.clear();
		ErrorSource.unregisterErrorSource(errorSource);

		EditBus.removeFromBus(this);

		removeBufferChangeListener(buffer);
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		//{{{ BufferUpdate
		if(msg instanceof BufferUpdate)
		{
			BufferUpdate bmsg = (BufferUpdate)msg;
			if(bmsg.getBuffer() != buffer)
				return;

			if(bmsg.getWhat() == BufferUpdate.SAVED)
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
			else if(bmsg.getWhat() == BufferUpdate.PROPERTIES_CHANGED
				|| bmsg.getWhat() == BufferUpdate.LOADED)
			{
				if(XmlPlugin.getParserType(buffer) == null)
					removeBufferChangeListener(buffer);
				else
					addBufferChangeListener(buffer);

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
			else if(bmsg.getWhat() == BufferUpdate.CLOSED)
			{
				errorSource.clear();
			}
		} //}}}
		//{{{ EditPaneUpdate
		else if(msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate)msg;
			EditPane editPane = epu.getEditPane();
			if(editPane.getView() != view)
				return;

			if(epu.getWhat() == EditPaneUpdate.CREATED)
				editPane.getTextArea().addFocusListener(new FocusHandler());
			else if(epu.getWhat() == EditPaneUpdate.DESTROYED)
			{
				// check if this is the currently focused edit pane
				if(editPane == editPane.getView().getEditPane())
					removeBufferChangeListener(this.buffer);
			}
			else if(epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
			{
				// check if this is the currently focused edit pane
				if(editPane == editPane.getView().getEditPane())
				{
					removeBufferChangeListener(this.buffer);

					if(XmlPlugin.getParserType(buffer) != null)
						addBufferChangeListener(editPane.getBuffer());

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
						XmlPlugin.COMPLETION_INFO_PROPERTY,
						getUnparsedCompletionInfo(buffer));
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

	//{{{ addError() method
	public boolean addError(int type, String path, int line, String message)
	{
		if(errorSource.getErrorCount() >= MAX_ERRORS)
		{
			maxErrors = true;
			return false;
		}
		else
		{
			errorSource.addError(type,path,line,0,0,message);
			return true;
		}
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

	private boolean maxErrors;

	private BufferChangeHandler bufferHandler;
	private boolean addedBufferChangeHandler;
	//}}}

	//{{{ addBufferChangeListener() method
	private void addBufferChangeListener(Buffer buffer)
	{
		if(!addedBufferChangeHandler)
		{
			buffer.addBufferChangeListener(bufferHandler);
			addedBufferChangeHandler = true;
		}
	} //}}}

	//{{{ removeBufferChangeListener() method
	private void removeBufferChangeListener(Buffer buffer)
	{
		if(addedBufferChangeHandler)
		{
			buffer.removeBufferChangeListener(bufferHandler);
			addedBufferChangeHandler = false;
		}
	} //}}}

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

	//{{{ showNotParsedMessage() method
	private void showNotParsedMessage()
	{
		errorSource.clear();

		stopThread();

		buffer = view.getBuffer();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(buffer.getName());
		model = new DefaultTreeModel(root);

		root.insert(new DefaultMutableTreeNode(
			jEdit.getProperty("xml-tree.not-parsed")),0);
		model.reload(root);

		view.getEditPane().putClientProperty(XmlPlugin.ELEMENT_TREE_PROPERTY,model);
		view.getEditPane().putClientProperty(XmlPlugin.COMPLETION_INFO_PROPERTY,
			getUnparsedCompletionInfo(buffer));
		view.getEditPane().putClientProperty(XmlPlugin.IDS_PROPERTY,null);

		finish();
		return;
	} //}}}

	//{{{ parseWithDelay() method
	private void parseWithDelay()
	{
		if(keystrokeTimer.isRunning())
			keystrokeTimer.stop();

		keystrokeTimer.setInitialDelay(delay);
		keystrokeTimer.setRepeats(false);
		keystrokeTimer.start();
	} //}}}

	//{{{ _parse() method
	private void _parse()
	{
		errorSource.clear();

		// get buffer text
		text = buffer.getText(0,buffer.getLength());

		// start parser thread
		stopThread();

		if(XmlPlugin.getParserType(buffer).equals("xml"))
			parserImpl = new SAXParserImpl();
		else if(XmlPlugin.getParserType(buffer).equals("html"))
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

		XmlTree tree = (XmlTree)view.getDockableWindowManager()
			.getDockable(XmlPlugin.TREE_NAME);
		if(tree != null)
			tree.update();

		XmlInsert insert = (XmlInsert)view.getDockableWindowManager()
			.getDockable(XmlPlugin.INSERT_NAME);
		if(insert != null)
			insert.update();
	} //}}}

	//{{{ getUnparsedCompletionInfo() method
	private CompletionInfo getUnparsedCompletionInfo(Buffer buffer)
	{
		// this silly little hack lets us use closing tag completion
		// and a few other miscellaneous features in the few seconds
		// before an XML buffer has been parsed. can be convinient
		// sometimes.
		if("xml".equals(buffer.getProperty(XmlPlugin.PARSER_PROPERTY)))
			return new CompletionInfo();
		else
			return null;
	} //}}}

	//}}}

	//{{{ Inner classes

	//{{{ Impl interface
	interface Impl
	{
		void parse(XmlParser parser, String text);
		TreeNode getElementTree();
		CompletionInfo getCompletionInfo();
		ArrayList getIDs();
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
			synchronized(XmlParser.this)
			{
				if(parserImpl == null)
					return;

				parserImpl.parse(XmlParser.this,text);
			}

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					synchronized(XmlParser.this)
					{
						if(thread == null)
							return;

						int errorCount = errorSource.getErrorCount();

						if(showParsingMessage || errorCount != 0)
						{
							if(parserImpl instanceof SwingHTMLParserImpl)
							{
								view.getStatus().setMessageAndClear(
									jEdit.getProperty(
									"xml-tree.parsing-complete-html"));
							}
							else if(maxErrors)
							{
								Object[] pp = { new Integer(errorCount) };
								view.getStatus().setMessageAndClear(jEdit.getProperty(
									"xml-tree.parsing-complete-errors",pp));
							}
							else
							{
								Object[] pp = { new Integer(errorCount) };
								view.getStatus().setMessageAndClear(jEdit.getProperty(
									"xml-tree.parsing-complete",pp));
							}
						}

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

						view.getEditPane().putClientProperty(
							XmlPlugin.IDS_PROPERTY,
							parserImpl.getIDs());

						thread = null;

						// to avoid keeping pointers to stale objects.
						// we could reuse a single parser instance to preserve
						// performance, but it eats too much memory, especially
						// if the file being parsed has an associated DTD.
						text = null;

						parserImpl = null;

						finish();
					}
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
				&& XmlPlugin.getParserType(buffer) != null
				&& buffer.getBooleanProperty("xml.keystroke-parse"))
				parseWithDelay();
		} //}}}

		//{{{ contentRemoved() method
		public void contentRemoved(Buffer buffer, int startLine, int offset,
			int numLines, int length)
		{
			if(buffer == XmlParser.this.buffer
				&& buffer.isLoaded()
				&& XmlPlugin.getParserType(buffer) != null
				&& buffer.getBooleanProperty("xml.keystroke-parse"))
				parseWithDelay();
		} //}}}
	} //}}}

	//{{{ FocusHandler class
	class FocusHandler extends FocusAdapter
	{
		public void focusGained(FocusEvent evt)
		{
			removeBufferChangeListener(XmlParser.this.buffer);

			Buffer buffer = view.getBuffer();

			if(XmlPlugin.getParserType(buffer) != null)
				addBufferChangeListener(buffer);

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
