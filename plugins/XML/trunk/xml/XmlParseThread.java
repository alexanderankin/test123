/*
 * XmlParseThread.java
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

package xml;

import org.xml.sax.*;

import org.xml.sax.helpers.DefaultHandler;

import javax.swing.text.BadLocationException;
import javax.swing.tree.*;
import javax.swing.SwingUtilities;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Stack;
import java.util.Vector;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class XmlParseThread extends Thread
{
	public XmlParseThread(XmlTree tree, Buffer buffer)
	{
		super("XML parser thread");
		setPriority(Thread.MIN_PRIORITY);

		this.tree = tree;
		this.buffer = buffer;

		root = new DefaultMutableTreeNode(buffer.getName());
		model = new DefaultTreeModel(root);

		try
		{
			text = buffer.getText(0,buffer.getLength());
		}
		catch(BadLocationException ble)
		{
			Log.log(Log.ERROR,this,ble);
			return;
		}
	}

	public void run()
	{
		parse();

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				model.reload(root);
				tree.parsingComplete(model);
			}
		});
	}

	// private members
	private XmlTree tree;
	private Buffer buffer;
	private String text;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private XMLReader parser;

	private void parse()
	{
		parser = new org.apache.xerces.parsers.SAXParser();

		try
		{
			parser.setFeature("http://apache.org/xml/features/validation/dynamic",true);
			parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error",true);
		}
		catch(SAXException se)
		{
			Log.log(Log.ERROR,this,se);
		}

		try
		{
			InputSource source = new InputSource(new StringReader(text));
			source.setSystemId(buffer.getPath());

			Handler handler = new Handler();
			parser.setErrorHandler(handler);
			parser.setEntityResolver(handler);
			parser.setContentHandler(handler);
			parser.parse(source);
		}
		catch(SAXParseException spe)
		{
			// fatal error, already handled
		}
		catch(SAXException se)
		{
			tree.addError(ErrorSource.ERROR,buffer.getPath(),
				0,se.getMessage());
		}
		catch(IOException ioe)
		{
			tree.addError(ErrorSource.ERROR,buffer.getPath(),0,
				ioe.toString());
			Log.log(Log.ERROR,this,ioe);
		}
	}

	class Handler extends DefaultHandler
	{
		Stack currentNodeStack = new Stack();
		Locator loc = null;

		public void setDocumentLocator(Locator locator)
		{
			loc = locator;
		}

		public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException
		{
			// FIXME
			// Xerces automatically prepends the current system id's
			// path to the system id to resolve.
			// this causes problems for jEdit DTDs that are not in
			// the current directory, but rather must be resolved
			// through the entity manager.
			//if(systemId.startsWith("file://"))
			//	systemId = systemId.substring(7);
			//else if(systemId.startsWith("file:"))
			//	systemId = systemId.substring(5);

			String parent = MiscUtilities.getParentOfPath(
				loc.getSystemId());

			if(systemId.startsWith(parent))
				systemId = MiscUtilities.getFileName(systemId);

			try
			{
				if(publicId != null)
				{
					return EntityManager.resolvePublicId(
						loc.getSystemId(),publicId);
				}
				else if(systemId != null)
				{
					return EntityManager.resolveSystemId(
						loc.getSystemId(),systemId);
				}
				else
					return null;
			}
			catch(MalformedURLException mu)
			{
				throw new SAXException(mu);
			}
			catch(IOException io)
			{
				throw new SAXException(io);
			}
		}

		public void startElement(String namespaceURI,
			String lName, // local name
			String qName, // qualified name
			Attributes attrs) throws SAXException
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			// ignore tags inside nested files for now
			//if(!buffer.getPath().equals(loc.getSystemId()))
			//	return;

			try
			{
				int line = loc.getLineNumber() - 1;
				int column = loc.getColumnNumber() - 1;
				int offset = buffer.getDefaultRootElement()
					.getElement(line).getStartOffset() + column;

				offset = findTagStart(offset);

				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
					new XmlTag(qName,buffer.createPosition(offset),attrs));

				if(!currentNodeStack.isEmpty())
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						currentNodeStack.peek();

					node.insert(newNode,node.getChildCount());
				}
				else
					root.insert(newNode,0);

				currentNodeStack.push(newNode);
			}
			catch(BadLocationException ble)
			{
				throw new SAXException(ble);
			}
		}

		public void endElement(String namespaceURI,
			String sName, // simple name
			String qName  // qualified name
			) throws SAXException
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			// ignore tags inside nested files for now
			//if(!buffer.getPath().equals(loc.getSystemId()))
			//	return;

			try
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					currentNodeStack.peek();
				XmlTag tag = (XmlTag)node.getUserObject();

				int line = loc.getLineNumber()-1;
				int column = loc.getColumnNumber()-1;

				int offset = buffer.getDefaultRootElement()
					.getElement(line).getStartOffset() + column;

				tag.end = buffer.createPosition(offset);

				currentNodeStack.pop();
			}
			catch(BadLocationException ble)
			{
				throw new SAXException(ble);
			}
		}
		
		public void error(SAXParseException spe)
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			tree.addError(ErrorSource.ERROR,spe.getSystemId(),
				spe.getLineNumber()-1,spe.getMessage());
		}

		public void warning(SAXParseException spe)
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			tree.addError(ErrorSource.WARNING,spe.getSystemId(),
				spe.getLineNumber()-1,spe.getMessage());
		}

		public void fatalError(SAXParseException spe)
			throws SAXParseException
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			tree.addError(ErrorSource.ERROR,spe.getSystemId(),
				spe.getLineNumber()-1,spe.getMessage());
		}

		private int findTagStart(int offset)
		{
			for(int i = offset; i >= 0; i--)
			{
				if(text.charAt(i) == '<')
					return i;
			}

			return 0;
		}
	}
}
