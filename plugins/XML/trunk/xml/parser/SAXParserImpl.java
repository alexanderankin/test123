/*
 * SAXParserImpl.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
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
import java.util.*;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import errorlist.*;
import xml.completion.*;
import xml.*;

// Xerces dependencies for schema introspection
import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.grammars.Grammar;
//}}}

class SAXParserImpl implements XmlParser.Impl
{
	//{{{ parse() method
	public XmlParsedData parse(XmlParser parser, String text)
	{
		data = new XmlParsedData(false);

		if(text.length() == 0)
			return data;

		this.parser = parser;
		this.text = text;

		SymbolTable symbolTable = new SymbolTable();
		grammarPool = new XMLGrammarPoolImpl();

		Handler handler = new Handler();

		XMLReader reader = new org.apache.xerces.parsers.SAXParser(symbolTable,grammarPool);
		try
		{
			reader.setFeature("http://xml.org/sax/features/validation",
				jEdit.getBooleanProperty("xml.validate"));
			reader.setFeature("http://apache.org/xml/features/validation/schema",
				true);
			reader.setFeature("http://xml.org/sax/features/namespaces",true);
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

		buffer = parser.getBuffer();

		CompletionInfo info = CompletionInfo.getCompletionInfoForBuffer(
			buffer);
		if(info != null)
			data.mappings.put("",info);

		root = new DefaultMutableTreeNode(buffer.getName());

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
		catch(Exception e)
		{
			Log.log(Log.ERROR,this,e);
		}

		data.tree = new DefaultTreeModel(root);

		return data;
	} //}}}

	//{{{ Package-private members
	// package-private for speedy access by Handler inner class
	String text;
	Buffer buffer;
	XmlParsedData data;
	DefaultMutableTreeNode root;
	XMLGrammarPoolImpl grammarPool;

	//{{{ addError() method
	void addError(int type, String uri, int line, String message)
	{
		if(uri == null)
		{
			System.err.println("null uri: " + message);
			return;
		}

		// FIXME?
		if(uri.startsWith("file:///") && OperatingSystem.isDOSDerived())
			uri = uri.substring(8);
		else if(uri.startsWith("file://"))
			uri = uri.substring(7);
		uri.replace('/',File.separatorChar);

		parser.addError(type,uri,line,message);
	} //}}}

	//}}}

	//{{{ Private members
	private XmlParser parser;
	//}}}

	//{{{ Handler class
	class Handler extends DefaultHandler implements DeclHandler
	{
		Stack currentNodeStack = new Stack();
		Locator loc = null;
		boolean empty = true;

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
			catch(SAXException s)
			{
				parser.addError(ErrorSource.ERROR,
					buffer.getPath(),
					Math.max(0,loc.getLineNumber()-1),
					s.getMessage());
			}
			catch(Exception e)
			{
				error(new SAXParseException(e.toString(),loc));
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

		//{{{ startPrefixMapping() method
		public void startPrefixMapping(String prefix, String uri)
		{
			// check for built-in completion info for this URI
			// (eg, XSL, XSD, XHTML has this).
			if(uri != null)
			{
				CompletionInfo info = CompletionInfo
					.getCompletionInfoForNamespace(uri);
				if(info != null)
				{
					System.err.println("got a loaded ci for " + uri);
					data.mappings.put(prefix,info);
					return;
				}
			}

			/* XSDDescription schemaDesc = new XSDDescription();
			schemaDesc.setTargetNamespace(uri);
			System.err.println("uri = " + uri);
			System.err.println("description = " + schemaDesc);
			Grammar grammar = grammarPool.getGrammar(schemaDesc);
			System.err.println("grammar = " + grammar);

			CompletionInfo info = grammarToCompletionInfo(grammar);
			data.mappings.put(prefix,info); */

			//if(uri != null)
			//	CompletionInfo.setCompletionInfoForNamespace(uri,info);
		} //}}}

		//{{{ startElement() method
		public void startElement(String namespaceURI,
			String lName, // local name
			String qName, // qualified name
			Attributes attrs) throws SAXException
		{
			empty = true;

			// ignore tags inside nested files for now
			//if(!buffer.getPath().equals(loc.getSystemId()))
			//	return;

			buffer.readLock();

			try
			{
				int line = Math.min(buffer.getLineCount() - 1,
					loc.getLineNumber() - 1);
				int column = loc.getColumnNumber() - 1;
				int offset = Math.min(text.length() - 1,
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
						data.ids.add(new IDDecl(attrs.getValue(i),qName,pos));
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
				tag.empty = empty;

				currentNodeStack.pop();
			}
			finally
			{
				buffer.readUnlock();
			}

			empty = false;
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
			ElementDecl element = data.getElementDecl(name);
			if(element == null)
			{
				CompletionInfo info = data.getNoNamespaceCompletionInfo();
				element = new ElementDecl(info,name,model);
				info.addElement(element);
			}
			else
				element.setContent(model);
		} //}}}

		//{{{ attributeDecl() method
		public void attributeDecl(String eName, String aName,
			String type, String valueDefault, String value)
		{
			ElementDecl element = data.getElementDecl(eName);
			if(element == null)
			{
				CompletionInfo info = data.getNoNamespaceCompletionInfo();
				element = new ElementDecl(info,eName,null);
				info.addElement(element);
			}

			// as per the XML spec
			if(element.getAttribute(aName) != null)
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

			data.getNoNamespaceCompletionInfo()
				.addEntity(EntityDecl.INTERNAL,name,value);
		} //}}}

		//{{{ externalEntityDecl() method
		public void externalEntityDecl(String name, String publicId,
			String systemId)
		{
			if(name.startsWith("%"))
				return;

			data.getNoNamespaceCompletionInfo()
				.addEntity(EntityDecl.EXTERNAL,name,
				publicId,systemId);
		} //}}}

		/* //{{{ grammarToCompletionInfo() method
		private CompletionInfo grammarToCompletionInfo(Grammar grammar)
		{
			return new CompletionInfo();
		} //}}} */

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
