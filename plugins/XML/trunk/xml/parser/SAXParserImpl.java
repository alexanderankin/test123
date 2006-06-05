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
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XSGrammar;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import sidekick.SideKickParsedData;

import xml.CatalogManager;
import xml.XmlParsedData;
import xml.XmlPlugin;
import xml.completion.CompletionInfo;
import xml.completion.ElementDecl;
import xml.completion.EntityDecl;
import xml.completion.IDDecl;
//}}}

public class SAXParserImpl extends XmlParser
{
	//{{{ SAXParserImpl constructor
	public SAXParserImpl()
	{
		super("xml");
	} //}}}
	protected SAXParserImpl(String name) {
		super(name);
	}
	//{{{ parse() method
	public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource)
	{
		stopped = false;

		String text;

		try
		{
			buffer.readLock();
			text = buffer.getText(0,buffer.getLength());
		}
		finally
		{
			buffer.readUnlock();
		}

		if(text.length() == 0)
			return new XmlParsedData(buffer.getName(),false);

		XmlParsedData data = new XmlParsedData(buffer.getName(),false);

		SymbolTable symbolTable = new SymbolTable();
		XMLGrammarPoolImpl grammarPool = new XMLGrammarPoolImpl();

		Handler handler = new Handler(buffer,text,errorSource,data,grammarPool);

		XMLReader reader = new org.apache.xerces.parsers.SAXParser(symbolTable,grammarPool);
		try
		{
			reader.setFeature("http://xml.org/sax/features/validation",
				buffer.getBooleanProperty("xml.validate"));
			reader.setFeature("http://apache.org/xml/features/validation/dynamic",true);
			reader.setFeature("http://apache.org/xml/features/validation/schema",
				buffer.getBooleanProperty("xml.validate"));
			reader.setFeature("http://xml.org/sax/features/namespaces",true);
			//reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error",true);
			reader.setErrorHandler(handler);
			reader.setEntityResolver(handler);
			reader.setContentHandler(handler);
			reader.setProperty("http://xml.org/sax/properties/declaration-handler",handler);
		}
		catch(SAXException se)
		{
			Log.log(Log.ERROR,this,se);
		}

		//TODO
		CompletionInfo info = CompletionInfo.getCompletionInfoForBuffer(
			buffer);
		if(info != null)
			data.setCompletionInfo("",info);

		InputSource source = new InputSource();

		String rootDocument = buffer.getStringProperty("xml.root");
		if(rootDocument != null)
		{
			Log.log(Log.NOTICE,this,"rootDocument specified; "
				+ "parsing " + rootDocument);
			rootDocument = MiscUtilities.constructPath(
				MiscUtilities.getParentOfPath(
				buffer.getPath()),rootDocument);
			source.setSystemId(rootDocument);
		}
		else
		{
			source.setCharacterStream(new StringReader(text));
			source.setSystemId(buffer.getPath());
		}

		try
		{
			reader.parse(source);
		}
		catch(StoppedException e)
		{
		}
		catch(IOException ioe)
		{
			Log.log(Log.ERROR,this,ioe);
			errorSource.addError(ErrorSource.ERROR,buffer.getPath(),0,0,0,
				ioe.toString());
		}
		catch(SAXParseException spe)
		{
			// already handled
		}
		catch(SAXException se)
		{
			Log.log(Log.ERROR,this,se.getException());
			if(se.getMessage() != null)
			{
				errorSource.addError(ErrorSource.ERROR,buffer.getPath(),
					0,0,0,se.getMessage());
			}
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,this,e);
		}

		Collections.sort(data.ids,new IDDecl.Compare());

		return data;
	} //}}}

	//{{{ Private members

	//{{{ xsElementToElementDecl() method
	private void xsElementToElementDecl(CompletionInfo info,
		XSElementDeclaration element, ElementDecl parent)
	{
		String name = element.getName();

		if(parent != null)
		{
			if(parent.content == null)
				parent.content = new HashSet();
			parent.content.add(name);
		}

		if(info.elementHash.get(name) != null)
			return;

		ElementDecl elementDecl = new ElementDecl(info,name,null);
		info.addElement(elementDecl);

		XSTypeDefinition typedef = element.getTypeDefinition();

		if(typedef.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE)
		{
			XSComplexTypeDefinition complex = (XSComplexTypeDefinition)typedef;

			XSParticle particle = complex.getParticle();
			if(particle != null)
			{
				XSTerm particleTerm = particle.getTerm();
				if(particleTerm instanceof XSWildcard)
					elementDecl.any = true;
				else
					xsTermToElementDecl(info,particleTerm,elementDecl);
			}

			XSObjectList attributes = complex.getAttributeUses();
			for(int i = 0; i < attributes.getLength(); i++)
			{
				XSAttributeUse attr = (XSAttributeUse)
					attributes.item(i);
				boolean required = attr.getRequired();
				XSAttributeDeclaration decl = attr.getAttrDeclaration();
				String attrName = decl.getName();
				String value = decl.getConstraintValue();
				// TODO: possible values
				String type = decl.getTypeDefinition().getName();
				if(type == null)
					type = "CDATA";
				elementDecl.addAttribute(new ElementDecl.AttributeDecl(
					attrName,value,null,type,required));
			}
		}
	} //}}}

	//{{{ xsTermToElementDecl() method
	private void xsTermToElementDecl(CompletionInfo info, XSTerm term,
		ElementDecl parent)
	{
		if(term instanceof XSElementDeclaration)
		{
			xsElementToElementDecl(info,
				(XSElementDeclaration)term,
				parent);
		}
		else if(term instanceof XSModelGroup)
		{
			XSObjectList content = ((XSModelGroup)term).getParticles();
			for(int i = 0; i < content.getLength(); i++)
			{
				XSTerm childTerm = ((XSParticleDecl)content.item(i)).getTerm();
				xsTermToElementDecl(info,childTerm,parent);
			}
		}
	}
	//}}}

	//}}}

	//{{{ Handler class
	class Handler extends DefaultHandler implements DeclHandler
	{
		Buffer buffer;

		DefaultErrorSource errorSource;
		String text;
		XmlParsedData data;
		XMLGrammarPoolImpl grammarPool;

		HashMap activePrefixes;
		Stack currentNodeStack;
		Locator loc;
		boolean empty;

		//{{{ Handler constructor
		Handler(Buffer buffer, String text, DefaultErrorSource errorSource,
			XmlParsedData data, XMLGrammarPoolImpl grammarPool)
		{
			this.buffer = buffer;
			this.text = text;
			this.errorSource = errorSource;
			this.data = data;
			this.grammarPool = grammarPool;
			this.activePrefixes = new HashMap();
			this.currentNodeStack = new Stack();
			this.empty = true;

		} //}}}

		//{{{ addError() method
		private void addError(int type, String uri, int line, String message)
		{
			errorSource.addError(type,XmlPlugin.uriToFile(uri),line,
				0,0,message);
		} //}}}

		//{{{ getGrammarForNamespace() method
		private Grammar getGrammarForNamespace(String uri)
		{
			XSDDescription schemaDesc = new XSDDescription();
			schemaDesc.setTargetNamespace(uri);
			Grammar grammar = grammarPool.getGrammar(schemaDesc);
			return grammar;
		} //}}}

		//{{{ grammarToCompletionInfo() method
		private CompletionInfo grammarToCompletionInfo(Grammar grammar)
		{
			if(!(grammar instanceof XSGrammar))
				return null;

			CompletionInfo info = new CompletionInfo();

			XSModel model = ((XSGrammar)grammar).toXSModel();

			XSNamedMap elements = model.getComponents(XSConstants.ELEMENT_DECLARATION);
			for(int i = 0; i < elements.getLength(); i++)
			{
				XSElementDeclaration element = (XSElementDeclaration)
					elements.item(i);

				xsElementToElementDecl(info,element,null);
			}

			XSNamedMap attributes = model.getComponents(XSConstants.ATTRIBUTE_DECLARATION);
			for(int i = 0; i < attributes.getLength(); i++)
			{
				XSObject attribute = attributes.item(i);
				System.err.println("look! " + attribute);
			}

			return info;
		} //}}}

		//{{{ endDocument() method
		public void endDocument() throws SAXException
		{
			Grammar grammar = getGrammarForNamespace(null);

			if(grammar != null)
			{
				CompletionInfo info = grammarToCompletionInfo(grammar);
				if(info != null)
					data.setCompletionInfo("",info);
			}
		} //}}}

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
				errorSource.addError(ErrorSource.ERROR,
					buffer.getPath(),
					Math.max(0,loc.getLineNumber()-1),0,0,
					s.getMessage());
			}
			catch(Exception e)
			{
				error(new SAXParseException(e.toString(),loc));
			}

			if(source == null)
			{
				Log.log(Log.DEBUG,this,"PUBLIC=" + publicId
					+ ", SYSTEM=" + systemId
					+ " cannot be resolved");
				InputSource dummy = new InputSource(new StringReader("<!-- -->"));
				dummy.setSystemId(systemId);
				dummy.setPublicId(publicId);
				return dummy;
			}
			else
			{
				Log.log(Log.DEBUG,this,"PUBLIC=" + publicId
					+ ", SYSTEM=" + systemId
					+ " resolved to " + source.getSystemId());
				return source;
			}
		} //}}}

		//{{{ startPrefixMapping() method
		public void startPrefixMapping(String prefix, String uri)
		{
			activePrefixes.put(prefix,uri);
		} //}}}

		//{{{ endPrefixMapping() method
		public void endPrefixMapping(String prefix)
		{
			String uri = (String)activePrefixes.get(prefix);
			// check for built-in completion info for this URI
			// (eg, XSL, XSD, XHTML has this).
			if(uri != null)
			{
				CompletionInfo info = CompletionInfo
					.getCompletionInfoForNamespace(uri);
				if(info != null)
				{
					data.setCompletionInfo(prefix,info);
					return;
				}
			}

			Grammar grammar = getGrammarForNamespace(uri);

			if(grammar != null)
			{
				CompletionInfo info = grammarToCompletionInfo(grammar);
				if(info != null)
					data.setCompletionInfo(prefix,info);
			}
		} //}}}

		//{{{ startElement() method
		public void startElement(String namespaceURI,
			String lName, // local name
			String qName, // qualified name
			Attributes attrs) throws SAXException
		{
			if(stopped)
				throw new StoppedException();

			empty = true;

			String currentURI = XmlPlugin.uriToFile(loc.getSystemId());

			if(!buffer.getPath().equals(currentURI))
				return;

			// what do we do in this case?
			if(loc.getLineNumber() == -1)
				return;

			// add all attributes with type "ID" to the ids vector
			for(int i = 0; i < attrs.getLength(); i++)
			{
				if(attrs.getType(i).equals("ID")
					|| attrs.getLocalName(i).equalsIgnoreCase("id"))
				{
					data.ids.add(new IDDecl(currentURI,
						attrs.getValue(i),qName,
						loc.getLineNumber() - 1,
						loc.getColumnNumber() - 1));
				}
			}

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
					data.root.insert(newNode,0);

				currentNodeStack.push(newNode);
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
			if(stopped)
				throw new StoppedException();

			if(!buffer.getPath().equals(XmlPlugin.uriToFile(loc.getSystemId())))
				return;

			// what do we do in this case?
			if(loc.getLineNumber() == -1)
				return;

			buffer.readLock();

			try
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					currentNodeStack.peek();
				XmlTag tag = (XmlTag)node.getUserObject();
				if(tag.name.equals(qName))
				{
					int line = Math.min(buffer.getLineCount() - 1,
						loc.getLineNumber() - 1);
					int column = loc.getColumnNumber() - 1;
					int offset = Math.min(buffer.getLength(),
						buffer.getLineStartOffset(line)
						+ column);

					tag.end = buffer.createPosition(offset);
					tag.empty = empty;
					currentNodeStack.pop();
				}
			}
			finally
			{
				buffer.readUnlock();
			}

			empty = false;
		} //}}}

		//{{{ characters() method
		public void characters (char ch[], int start, int length)
			throws SAXException
		{
			if(stopped)
				throw new StoppedException();

			empty = false;
		} //}}}

		//{{{ error() method
		public void error(SAXParseException spe)
		{
			String systemId = spe.getSystemId();
			if(systemId == null)
				systemId = buffer.getPath();
			addError(ErrorSource.ERROR,spe.getSystemId(),
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		} //}}}

		//{{{ warning() method
		public void warning(SAXParseException spe)
		{
			String systemId = spe.getSystemId();
			if(systemId == null)
				systemId = buffer.getPath();
			addError(ErrorSource.WARNING,spe.getSystemId(),
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		} //}}}

		//{{{ fatalError() method
		public void fatalError(SAXParseException spe)
			throws SAXParseException
		{
			String systemId = spe.getSystemId();
			if(systemId == null)
				systemId = buffer.getPath();
			addError(ErrorSource.ERROR,systemId,
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

	//{{{ StoppedException class
	static class StoppedException extends SAXException
	{
		StoppedException()
		{
			super("Parsing stopped");
		}
	} //}}}
}
