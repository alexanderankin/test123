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
	public XmlParser(View view, Buffer buffer)
	{
		this.tree = (XmlTree)view.getDockableWindowManager()
			.getDockableWindow(XmlPlugin.TREE_NAME);
		this.insert = (XmlInsert)view.getDockableWindowManager()
			.getDockableWindow(XmlPlugin.INSERT_NAME);

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

	public void parse()
	{
		elements = new Vector();
		elementHash = new Hashtable();
		entities = new Vector();
		entities.addElement(new EntityDecl(EntityDecl.INTERNAL,"lt","<"));
		entities.addElement(new EntityDecl(EntityDecl.INTERNAL,"gt",">"));
		entities.addElement(new EntityDecl(EntityDecl.INTERNAL,"amp","&"));

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
			parser.setProperty("http://xml.org/sax/properties/declaration-handler",handler);
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
			Log.log(Log.ERROR,this,ioe);
			tree.addError(ErrorSource.ERROR,buffer.getPath(),0,
				ioe.toString());
		}
	}

	public void finish()
	{
		if(tree != null)
		{
			model.reload(root);
			tree.parsingComplete(model);
		}

		MiscUtilities.quicksort(elements,new ElementDeclCompare());
		MiscUtilities.quicksort(entities,new EntityDeclCompare());

		buffer.putProperty(XmlPlugin.DECLARED_ELEMENTS_PROPERTY,
			elements);
		buffer.putProperty(XmlPlugin.DECLARED_ENTITIES_PROPERTY,
			entities);

		if(insert != null)
		{
			insert.setDeclaredElements(elements);
			insert.setDeclaredEntities(entities);
		}
	}

	static class ElementDeclCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			return ((ElementDecl)obj1).name
				.compareTo(((ElementDecl)obj2).name);
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
				return entity1.name.compareTo(entity2.name);
		}
	}

	// private members
	private XmlTree tree;
	private XmlInsert insert;
	private Vector elements;
	private Hashtable elementHash;
	private Vector entities;
	private View view;
	private Buffer buffer;
	private String text;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private XMLReader parser;

	class Handler extends DefaultHandler implements DeclHandler
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
			try
			{
				InputSource in = EntityManager.resolveEntity(
					loc.getSystemId(),publicId,systemId);
				if(in == null)
				{
					error(new SAXParseException("Not found: "
						+ publicId + ":" + systemId,
						loc));
				}
				else
					return in;
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

			// ignore tags inside nested files for now
			//if(!buffer.getPath().equals(loc.getSystemId()))
			//	return;

			try
			{
				int line = loc.getLineNumber() - 1;
				int column = loc.getColumnNumber() - 1;
				int offset = buffer.getDefaultRootElement()
					.getElement(line).getStartOffset() + column - 1;

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
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		}

		public void warning(SAXParseException spe)
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			tree.addError(ErrorSource.WARNING,spe.getSystemId(),
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

			tree.addError(ErrorSource.ERROR,spe.getSystemId(),
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		}

		// DeclHandler implementation
		public void elementDecl(String name, String model)
		{
			ElementDecl elementDecl = new ElementDecl(name,model);
			elementHash.put(name,elementDecl);
			elements.addElement(elementDecl);
		}

		public void attributeDecl(String eName, String aName,
			String type, String valueDefault, String value)
		{
			ElementDecl element = (ElementDecl)elementHash.get(eName);
			if(element == null)
				return;

			element.addAttribute(new ElementDecl.AttributeDecl(
				aName,type,valueDefault,value));
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

	static class ParseThread extends Thread
	{
		XmlParser parser;

		ParseThread(View view, Buffer buffer)
		{
			super("XML parser thread");
			setPriority(Thread.MIN_PRIORITY);
			parser = new XmlParser(view,buffer);
		}

		public void run()
		{
			parser.parse();
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					parser.finish();
				}
			});
		}
	}
}
