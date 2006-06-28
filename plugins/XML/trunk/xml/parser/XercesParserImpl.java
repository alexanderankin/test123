package xml.parser;

import org.gjt.sp.jedit.Buffer;

import sidekick.SideKickParsedData;
import errorlist.DefaultErrorSource;

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

import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;


import errorlist.ErrorSource;

import xml.CatalogManager;
import xml.Resolver;
import xml.XmlParsedData;
import xml.XmlPlugin;
import xml.completion.CompletionInfo;
import xml.completion.ElementDecl;
import xml.completion.EntityDecl;
import xml.completion.IDDecl;
//}}}

/**
 * This class should eventually replace SAXParserImpl.
 * The design goal is to use more recent APIs in Xerces, and to avoid
 * using internal or native interface classes, including the Grammar
 * class. 
 * 
 *  
 *     
 *     
 */

public class XercesParserImpl extends XmlParser
{
	
	
	//{{{ SAXParserImpl constructor
	public XercesParserImpl()
	{
		super("xml");
		
	} //}}}
	
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
	

		Handler handler = new Handler(buffer,text,errorSource,data);


		XMLReader reader = null;
		try
		{ 
			
			
			reader = XMLReaderFactory.createXMLReader();
			reader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", 
				  handler);
			reader.setFeature("http://xml.org/sax/features/validation",
				buffer.getBooleanProperty("xml.validate"));
			reader.setFeature("http://apache.org/xml/features/validation/dynamic",true);
			reader.setFeature("http://apache.org/xml/features/validation/schema",
				buffer.getBooleanProperty("xml.validate"));
			reader.setFeature("http://xml.org/sax/features/namespaces",true);
			//reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error",true);
			reader.setErrorHandler(handler);
			reader.setContentHandler(handler);
			reader.setProperty("http://xml.org/sax/properties/declaration-handler",handler);
		}
		catch(SAXException se)
		{
			se.printStackTrace();
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
	private void xsElementToElementDecl(XSNamedMap elements, CompletionInfo info,
		XSElementDeclaration element, ElementDecl parent)
	{


		if(parent != null && parent.content == null)
			parent.content = new HashSet();
		
		String name = element.getName();
		if(info.elementHash.get(name) != null)
			return;
		ElementDecl elementDecl = null;
		
		if ( element.getAbstract() || element.getName().endsWith(".class")) {
			
			for (int j=0; j<elements.getLength(); ++j) {
				XSElementDeclaration decl = (XSElementDeclaration)elements.item(j);
				XSElementDeclaration group = decl.getSubstitutionGroupAffiliation();
				if (group != null && group.getName().equals(name)) {
					info.addElement(new ElementDecl(info, decl.getName(), null));
					if (parent != null) parent.content.add(decl.getName());
				}
			}
		}
		else {
			elementDecl = new ElementDecl(info, name, null);
			info.addElement(elementDecl);
			if (parent != null) parent.content.add(name);
		}
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
					xsTermToElementDecl(elements, info,particleTerm,elementDecl);
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
	private void xsTermToElementDecl(XSNamedMap elements, CompletionInfo info, XSTerm term,
		ElementDecl parent)
	{
		
		if(term instanceof XSElementDeclaration)
		{
			xsElementToElementDecl(elements, info,
				(XSElementDeclaration)term,
				parent);
		}
		else if(term instanceof XSModelGroup)
		{
			XSObjectList content = ((XSModelGroup)term).getParticles();
			for(int i = 0; i < content.getLength(); i++)
			{
				XSTerm childTerm = ((XSParticleDecl)content.item(i)).getTerm();
				xsTermToElementDecl(elements, info,childTerm,parent);
			}
		}
	}
	//}}}

	//}}}

	//{{{ Handler class
	class Handler extends DefaultHandler implements DeclHandler, XMLEntityResolver
	{
		Buffer buffer;

		DefaultErrorSource errorSource;
		String text;
		XmlParsedData data;
		

		HashMap activePrefixes;
		Stack currentNodeStack;
		Locator loc;
		boolean empty;

		//{{{ Handler constructor
		Handler(Buffer buffer, String text, DefaultErrorSource errorSource,
			XmlParsedData data)
		{
			this.buffer = buffer;
			this.text = text;
			this.errorSource = errorSource;
			this.data = data;
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

		private XSLoader xsLoader = null;

		//{{{ getGrammarForNamespace() method
		private XSModel getModelForNamespace(String uri)
		{
			if (xsLoader == null) try {

			           DOMImplementationRegistry dir = DOMImplementationRegistry.newInstance();
			           XSImplementation xsi = (XSImplementation) dir.getDOMImplementation("XS-Loader");
			           xsLoader = xsi.createXSLoader(null);
			}
			catch (Exception e) {e.printStackTrace();}
			XSModel model = xsLoader.loadURI(uri);
			return model;
		}

		//{{{ grammarToCompletionInfo() method
		private CompletionInfo modelToCompletionInfo(XSModel model)
		{

			CompletionInfo info = new CompletionInfo();
			
			XSNamedMap elements = model.getComponents(XSConstants.ELEMENT_DECLARATION);
			for(int i = 0; i < elements.getLength(); i++)
			{
				XSElementDeclaration element = (XSElementDeclaration)
					elements.item(i);
				
				xsElementToElementDecl(elements, info, element, null);
			}

			XSNamedMap attributes = model.getComponents(XSConstants.ATTRIBUTE_DECLARATION);
			for(int i = 0; i < attributes.getLength(); i++)
			{
				XSObject attribute = attributes.item(i);
				System.err.println("look! " + attribute.getName());
			}

			return info;
		} //}}}

		//{{{ endDocument() method
		public void endDocument() throws SAXException
		{
			XSModel model = getModelForNamespace(null);

			if(model != null)
			{
				CompletionInfo info = modelToCompletionInfo(model);
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
		/**
		 * If you do this:
		 * reader.setProperty("http://apache.org/xml/properties/internal/entity-resolver",
		 * 	  handler);
		 * Then this method should be called.
		 */
		public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier)
		        throws XNIException, IOException {
			String publicId = resourceIdentifier.getPublicId();
			String systemId = resourceIdentifier.getExpandedSystemId();
			XMLInputSource source = null;
			
			try {
				source = Resolver.instance().resolveEntity(resourceIdentifier);
			}
			catch(Exception e)
			{
				errorSource.addError(ErrorSource.ERROR,
					buffer.getPath(),
					Math.max(0,loc.getLineNumber()-1),0,0,
					e.getMessage());
			}

			if(source == null)
			{
				Log.log(Log.DEBUG,this,"PUBLIC=" + publicId
					+ ", SYSTEM=" + systemId
					+ " cannot be resolved");
				XMLInputSource dummy = new XMLInputSource(publicId, systemId, null);
				dummy.setCharacterStream(new StringReader("<!-- -->"));
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

			XSModel model = getModelForNamespace(uri);

			if(model != null)
			{
				CompletionInfo info = modelToCompletionInfo(model);
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
