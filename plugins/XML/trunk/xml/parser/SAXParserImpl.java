/*
 * SAXParserImpl.java
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
import javax.swing.text.Position;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import errorlist.*;
import xml.completion.*;
import xml.*;
//}}}

class SAXParserImpl implements XmlParser.Impl
{
	//{{{ SAXParserImpl constructor
	SAXParserImpl()
	{
		elements = new ArrayList();
		elementHash = new HashMap();
		entities = new ArrayList();
		entityHash = new HashMap();
		ids = new ArrayList();

		addEntity(new EntityDecl(EntityDecl.INTERNAL,"lt","<"));
		addEntity(new EntityDecl(EntityDecl.INTERNAL,"gt",">"));
		addEntity(new EntityDecl(EntityDecl.INTERNAL,"amp","&"));
		addEntity(new EntityDecl(EntityDecl.INTERNAL,"quot","\""));
		addEntity(new EntityDecl(EntityDecl.INTERNAL,"apos","'"));

		Handler handler = new Handler();

		reader = new org.apache.xerces.parsers.SAXParser();
		try
		{
			reader.setFeature("http://apache.org/xml/features/validation/dynamic",
				jEdit.getBooleanProperty("xml.validate"));
			reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error",true);
			reader.setErrorHandler(handler);
			reader.setEntityResolver(handler);
			reader.setContentHandler(handler);
			reader.setProperty("http://xml.org/sax/properties/declaration-handler",handler);
		}
		catch(SAXException se)
		{
			Log.log(Log.ERROR,this,se);
		}
	} //}}}

	//{{{ parse() method
	public void parse(XmlParser parser, String text)
	{
		this.parser = parser;
		this.text = text;
		buffer = parser.getBuffer();
		root = new DefaultMutableTreeNode(buffer.getName());

		if(buffer.getLength() == 0)
			return;

		try
		{
			InputSource source = new InputSource(
				new StringReader(text));
			source.setSystemId(buffer.getPath());
			reader.parse(source);
		}
		catch(IOException ioe)
		{
			Log.log(Log.ERROR,this,ioe);
			parser.addError(ErrorSource.ERROR,buffer.getPath(),0,
				ioe.toString());
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
	} //}}}

	//{{{ getElementTree() method
	public TreeNode getElementTree()
	{
		return root;
	} //}}}

	//{{{ getCompletionInfo() method
	public CompletionInfo getCompletionInfo()
	{
		MiscUtilities.quicksort(elements,new ElementDecl.Compare());
		MiscUtilities.quicksort(entities,new EntityDecl.Compare());

		return new CompletionInfo(false,elements,elementHash,entities,
			entityHash,new ArrayList());
	} //}}}

	//{{{ getIDs() method
	public ArrayList getIDs()
	{
		MiscUtilities.quicksort(ids,new MiscUtilities.StringICaseCompare());
		return ids;
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private String text;
	private XmlParser parser;
	private XMLReader reader;
	private Buffer buffer;
	private ArrayList elements;
	private HashMap elementHash;
	private ArrayList entities;
	private HashMap entityHash;
	private ArrayList ids;
	private DefaultMutableTreeNode root;
	//}}}

	//{{{ addError() method
	private void addError(int type, String uri, int line, String message)
	{
		// FIXME?
		if(uri.startsWith("file://"))
			uri = uri.substring(7);
		uri.replace('/',File.separatorChar);

		parser.addError(type,uri,line,message);
	} //}}}

	//{{{ addEntity() method
	private void addEntity(EntityDecl entity)
	{
		entities.add(entity);
		if(entity.type == EntityDecl.INTERNAL
			&& entity.value.length() == 1)
		{
			Character ch = new Character(entity.value.charAt(0));
			entityHash.put(entity.name,ch);
			entityHash.put(ch,entity.name);
		}
	} //}}}

	//}}}

	//{{{ Handler class
	class Handler extends DefaultHandler implements DeclHandler
	{
		Stack currentNodeStack = new Stack();
		Locator loc = null;

		//{{{ setDocumentLocator() method
		public void setDocumentLocator(Locator locator)
		{
			loc = locator;
		} //}}}

		//{{{ resolveEntity() method
		public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException
		{
			InputSource source = null;

			try
			{
				source = CatalogManager.resolve(
					loc.getSystemId(),publicId,systemId);
			}
			catch(IOException io)
			{
				error(new SAXParseException(io.toString(),loc));
			}

			if(source == null)
				return new InputSource(new StringReader("<!-- -->"));
			else
			{
				Log.log(Log.DEBUG,this,publicId + "::" + systemId
					+ " resolved to " + source.getSystemId());
				return source;
			}
		} //}}}

		//{{{ startElement() method
		public void startElement(String namespaceURI,
			String lName, // local name
			String qName, // qualified name
			Attributes attrs) throws SAXException
		{
			// ignore tags inside nested files for now
			//if(!buffer.getPath().equals(loc.getSystemId()))
			//	return;

			buffer.readLock();

			try
			{
				int line = Math.min(buffer.getLineCount() - 1,
					loc.getLineNumber() - 1);
				int column = loc.getColumnNumber() - 1;
				int offset = Math.min(buffer.getLength() - 1,
					buffer.getLineStartOffset(line)
					+ column - 1);

				offset = findTagStart(offset);
				Position pos = buffer.createPosition(offset);

				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
					new XmlTag(qName,pos,attrs));

				if(!currentNodeStack.isEmpty())
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						currentNodeStack.peek();

					node.insert(newNode,node.getChildCount());
				}
				else
					root.insert(newNode,0);

				currentNodeStack.push(newNode);

				// add all attributes with type "ID" to the ids vector
				for(int i = 0; i < attrs.getLength(); i++)
				{
					if(attrs.getType(i).equals("ID")
						|| attrs.getLocalName(i).equalsIgnoreCase("id"))
						ids.add(new IDDecl(attrs.getValue(i),qName,pos));
				}
			}
			finally
			{
				buffer.readUnlock();
			}
		} //}}}

		//{{{ endElement() method
		public void endElement(String namespaceURI,
			String sName, // simple name
			String qName  // qualified name
			) throws SAXException
		{
			// ignore tags inside nested files for now
			//if(!buffer.getPath().equals(loc.getSystemId()))
			//	return;

			buffer.readLock();

			try
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					currentNodeStack.peek();
				XmlTag tag = (XmlTag)node.getUserObject();

				int line = Math.min(buffer.getLineCount() - 1,
					loc.getLineNumber() - 1);
				int column = loc.getColumnNumber() - 1;
				int offset = Math.min(buffer.getLength() - 1,
					buffer.getLineStartOffset(line)
					+ column);

				tag.end = buffer.createPosition(offset);

				currentNodeStack.pop();
			}
			finally
			{
				buffer.readUnlock();
			}
		} //}}}

		//{{{ error() method
		public void error(SAXParseException spe)
		{
			addError(ErrorSource.ERROR,spe.getSystemId(),
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		} //}}}

		//{{{ warning() method
		public void warning(SAXParseException spe)
		{
			addError(ErrorSource.WARNING,spe.getSystemId(),
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		} //}}}

		//{{{ fatalError() method
		public void fatalError(SAXParseException spe)
			throws SAXParseException
		{
			addError(ErrorSource.ERROR,spe.getSystemId(),
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		} //}}}

		//{{{ elementDecl() method
		public void elementDecl(String name, String model)
		{
			ElementDecl elementDecl = new ElementDecl(name,model,false);
			elementHash.put(name,elementDecl);
			elements.add(elementDecl);
		} //}}}

		//{{{ attributeDecl() method
		public void attributeDecl(String eName, String aName,
			String type, String valueDefault, String value)
		{
			ElementDecl element = (ElementDecl)elementHash.get(eName);
			if(element == null)
				return;

			ArrayList values;

			if(type.startsWith("("))
			{
				values = new ArrayList();

				StringTokenizer st = new StringTokenizer(
					type.substring(1,type.length() - 1),"|");
				while(st.hasMoreTokens())
				{
					values.add(st.nextToken());
				}
			}
			else
				values = null;

			boolean required = "#REQUIRED".equals(valueDefault);

			element.addAttribute(new ElementDecl.AttributeDecl(
				aName,value,values,type,required));
		} //}}}

		//{{{ internalEntityDecl() method
		public void internalEntityDecl(String name, String value)
		{
			// this is a bit of a hack
			if(name.startsWith("%"))
				return;

			addEntity(new EntityDecl(
				EntityDecl.INTERNAL,name,value));
		} //}}}

		//{{{ externalEntityDecl() method
		public void externalEntityDecl(String name, String publicId,
			String systemId)
		{
			// this is a bit of a hack
			if(name.startsWith("%"))
				return;

			addEntity(new EntityDecl(
				EntityDecl.EXTERNAL,name,publicId,systemId));
		} //}}}

		//{{{ findTagStart() method
		private int findTagStart(int offset)
		{
			for(int i = offset; i >= 0; i--)
			{
				if(text.charAt(i) == '<')
					return i;
			}

			return 0;
		} //}}}
	} //}}}
}
