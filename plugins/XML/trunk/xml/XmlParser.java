/*
 * XmlParser.java
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

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.tree.*;
import javax.swing.SwingUtilities;
import java.io.*;
import java.util.*;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

class XmlParser
{
	XmlParser(View view)
	{
		this.view = view;

		elements = new Vector();
		elementHash = new Hashtable();
		entities = new Vector();
		ids = new Vector();

		errorSource = new DefaultErrorSource("XML");

		handler = new Handler();
	}

	void parse(Buffer buffer)
	{
		// prepare for parsing
		elements.removeAllElements();
		elementHash.clear();
		entities.removeAllElements();
		entities.addElement(new EntityDecl(EntityDecl.INTERNAL,"lt","<"));
		entities.addElement(new EntityDecl(EntityDecl.INTERNAL,"gt",">"));
		entities.addElement(new EntityDecl(EntityDecl.INTERNAL,"amp","&"));
		ids.removeAllElements();
		handler.currentNodeStack.removeAllElements();

		root = new DefaultMutableTreeNode(buffer.getName());
		model = new DefaultTreeModel(root);

		errorSource.clear();

		parser = new org.apache.xerces.parsers.SAXParser();
		try
		{
			parser.setFeature("http://apache.org/xml/features/validation/dynamic",
				jEdit.getBooleanProperty("xml.validate"));
			parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error",true);
			parser.setErrorHandler(handler);
			parser.setEntityResolver(handler);
			parser.setContentHandler(handler);
			parser.setProperty("http://xml.org/sax/properties/declaration-handler",handler);
		}
		catch(SAXException se)
		{
			Log.log(Log.ERROR,this,se);
		}

		// get buffer text
		this.buffer = buffer;

		try
		{
			text = buffer.getText(0,buffer.getLength());
		}
		catch(BadLocationException ble)
		{
			Log.log(Log.ERROR,this,ble);
			return;
		}

		// start parser thread
		stopThread();

		thread = new ParseThread();
		thread.start();
	}

	void stopThread()
	{
		if(thread != null)
		{
			thread.stop();
			thread = null;
		}
	}

	void addError(int type, String path, int line, String message)
	{
		// FIXME?
		if(path.startsWith("file://"))
			path = path.substring(7);
		path.replace('/',File.separatorChar);

		errorSource.addError(type,path,line,0,0,message);
	}

	void addNotify()
	{
		EditBus.addToNamedList(ErrorSource.ERROR_SOURCES_LIST,errorSource);
		EditBus.addToBus(errorSource);
	}

	void removeNotify()
	{
		stopThread();

		errorSource.clear();
		EditBus.removeFromNamedList(ErrorSource.ERROR_SOURCES_LIST,errorSource);
		EditBus.removeFromBus(errorSource);
	}

	ErrorSource getErrorSource()
	{
		return errorSource;
	}

	static class ElementDeclCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			return ((ElementDecl)obj1).name.toLowerCase()
				.compareTo(((ElementDecl)obj2).name.toLowerCase());
		}
	}

	static class EntityDeclCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			EntityDecl entity1 = (EntityDecl)obj1;
			EntityDecl entity2 = (EntityDecl)obj2;

			if(entity1.type != entity2.type)
				return entity2.type - entity1.type;
			else
			{
				return entity1.name.toLowerCase()
					.compareTo(entity2.name
					.toLowerCase());
			}
		}
	}

	// private members
	private View view;
	private Buffer buffer;

	private Vector elements;
	private Hashtable elementHash;
	private Vector entities;
	private Vector ids;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;

	private ParseThread thread;

	private String text;

	private XMLReader parser;
	private Handler handler;

	private DefaultErrorSource errorSource;

	private void doParse()
	{
		try
		{
			InputSource source = new InputSource(new StringReader(text));
			source.setSystemId(buffer.getPath());

			parser.parse(source);
		}
		catch(SAXParseException spe)
		{
			// fatal error, already handled
		}
		catch(SAXException se)
		{
			Log.log(Log.ERROR,this,se.getException());
			if(se.getMessage() != null)
			{
				addError(ErrorSource.ERROR,buffer.getPath(),
				0,se.getMessage());
			}
		}
		catch(IOException ioe)
		{
			Log.log(Log.ERROR,this,ioe);
			addError(ErrorSource.ERROR,buffer.getPath(),0,
			ioe.toString());
		}
	}

	private void finish()
	{
		if(view.isClosed())
			return;

		thread = null;

		// to avoid keeping pointers to stale objects.
		// we could reuse a single parser instance to preserve
		// performance, but it eats too much memory, especially
		// if the file being parsed has an associated DTD.
		buffer = null;
		text = null;
		parser = null;

		MiscUtilities.quicksort(elements,new ElementDeclCompare());
		MiscUtilities.quicksort(entities,new EntityDeclCompare());
		MiscUtilities.quicksort(ids,new MiscUtilities.StringICaseCompare());

		view.getEditPane().putClientProperty(
			XmlPlugin.ELEMENTS_PROPERTY,
			elements);
		view.getEditPane().putClientProperty(
			XmlPlugin.ELEMENT_HASH_PROPERTY,
			elementHash);
		view.getEditPane().putClientProperty(
			XmlPlugin.ENTITIES_PROPERTY,
			entities);
		view.getEditPane().putClientProperty(
			XmlPlugin.IDS_PROPERTY,
			ids);

		XmlTree tree = (XmlTree)view.getDockableWindowManager()
			.getDockableWindow(XmlPlugin.TREE_NAME);
		if(tree != null)
		{
			model.reload(root);
			tree.parsingComplete(model);
		}

		XmlInsert insert = (XmlInsert)view.getDockableWindowManager()
			.getDockableWindow(XmlPlugin.INSERT_NAME);
		if(insert != null)
		{
			insert.setDeclaredElements(elements);
			insert.setDeclaredEntities(entities);
		}
	}

	class Handler extends DefaultHandler implements DeclHandler
	{
		Stack currentNodeStack = new Stack();
		Locator loc = null;

		// DTD cache
		private String lastDTD;

		public void setDocumentLocator(Locator locator)
		{
			loc = locator;
		}

		public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException
		{
			try
			{
				return EntityManager.resolveEntity(
					loc.getSystemId(),publicId,systemId);
			}
			catch(IOException io)
			{
				error(new SAXParseException(io.toString(),loc));
			}

			return new InputSource(new StringReader("<!-- -->"));
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

			String id = attrs.getValue("id");
			if(id == null)
				id = attrs.getValue("Id");
			if(id == null)
				id = attrs.getValue("ID");

			if(id != null && ids.indexOf(id) == -1)
				ids.addElement(id);

			// ignore tags inside nested files for now
			//if(!buffer.getPath().equals(loc.getSystemId()))
			//	return;

			try
			{
				buffer.readLock();

				Element map = buffer.getDefaultRootElement();
				int line = Math.min(map.getElementCount() - 1,
					loc.getLineNumber() - 1);
				int column = loc.getColumnNumber() - 1;
				int offset = Math.min(buffer.getLength() - 1,
					map.getElement(line).getStartOffset()
					+ column - 1);

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
			finally
			{
				buffer.readUnlock();
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

				buffer.readLock();

				Element map = buffer.getDefaultRootElement();
				int line = Math.min(map.getElementCount() - 1,
					loc.getLineNumber() - 1);
				int column = loc.getColumnNumber() - 1;
				int offset = Math.min(buffer.getLength() - 1,
					map.getElement(line).getStartOffset()
					+ column);

				tag.end = buffer.createPosition(offset);

				currentNodeStack.pop();
			}
			catch(BadLocationException ble)
			{
				throw new SAXException(ble);
			}
			finally
			{
				buffer.readUnlock();
			}
		}

		public void error(SAXParseException spe)
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			addError(ErrorSource.ERROR,spe.getSystemId(),
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		}

		public void warning(SAXParseException spe)
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			addError(ErrorSource.WARNING,spe.getSystemId(),
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		}

		public void fatalError(SAXParseException spe)
			throws SAXParseException
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			addError(ErrorSource.ERROR,spe.getSystemId(),
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		}

		// DeclHandler implementation
		public void elementDecl(String name, String model)
		{
			boolean empty = "EMPTY".equals(model);
			ElementDecl elementDecl = new ElementDecl(name,empty,false);
			elementHash.put(name,elementDecl);
			elements.addElement(elementDecl);
		}

		public void attributeDecl(String eName, String aName,
			String type, String valueDefault, String value)
		{
			ElementDecl element = (ElementDecl)elementHash.get(eName);
			if(element == null)
				return;

			element.addXMLAttribute(aName,type,valueDefault,value);
		}

		public void internalEntityDecl(String name, String value)
		{
			// this is a bit of a hack
			if(name.startsWith("%"))
				return;

			entities.addElement(new EntityDecl(
				EntityDecl.INTERNAL,name,value));
		}

		public void externalEntityDecl(String name, String publicId,
			String systemId)
		{
			// this is a bit of a hack
			if(name.startsWith("%"))
				return;

			entities.addElement(new EntityDecl(
				EntityDecl.EXTERNAL,name,publicId,systemId));
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

	class ParseThread extends Thread
	{
		ParseThread()
		{
			super("XML parser thread");
			setPriority(Thread.MIN_PRIORITY);
		}

		public void run()
		{
			doParse();

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					finish();
				}
			});
		}
	}
}
