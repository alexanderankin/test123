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

import org.w3c.dom.DOMImplementationSource;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

import org.apache.xerces.impl.xs.XSParticleDecl;

import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.PSVIProvider;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.StringList;
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
 * It is not fully working yet.
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
			// One has to explicitly require the parser from XercesPlugin, otherwise
			// one gets the crimson version bundled in the JRE and the rest fails
			// miserably (at least on Mac OS X, JDK 5)
			reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			reader.setFeature("http://xml.org/sax/features/validation",
				buffer.getBooleanProperty("xml.validate"));
			reader.setFeature("http://apache.org/xml/features/validation/dynamic",true);
			reader.setFeature("http://apache.org/xml/features/validation/schema",
				buffer.getBooleanProperty("xml.validate"));
			reader.setFeature("http://xml.org/sax/features/namespaces",true);
			reader.setFeature("http://xml.org/sax/features/use-entity-resolver2", true);
			reader.setFeature("http://apache.org/xml/features/xinclude",
				buffer.getBooleanProperty("xml.xinclude"));
			reader.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris",
				buffer.getBooleanProperty("xml.xinclude.xmlbase"));
			//reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error",true);
			reader.setErrorHandler(handler);
			reader.setContentHandler(handler);
			reader.setEntityResolver(handler);

			//get access to the schema
			handler.setPSVIProvider((PSVIProvider)reader);
			//get access to the DTD
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
				MiscUtilities.getParentOfPath(buffer.getPath()), rootDocument);
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
			ioe.printStackTrace();
			errorSource.addError(ErrorSource.ERROR, buffer.getPath(), 0, 0, 0,
				ioe.toString());
		}
		catch(SAXParseException spe)
		{
			// already handled
		}
		catch(SAXException se)
		{
			Log.log(Log.ERROR, this, se.getException());
			if(se.getMessage() != null)
			{
				se.printStackTrace();
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
			parent.content = new HashSet<String>();

		String name = element.getName();
		if(info.elementHash.get(name) != null)
		{
			// one must add the element to its parent's content, even if
			// one knows the element already
			if(parent!=null) parent.content.add(name);
			return;
		}

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
				XSSimpleTypeDefinition typeDef = decl.getTypeDefinition();
				String type = typeDef.getName();
				StringList valueStringList = typeDef.getLexicalEnumeration();
				ArrayList<String> values = new ArrayList<String>();
				for (int j = 0; j < valueStringList.getLength(); j++) {
				    values.add(valueStringList.item(j));
				}

				if(type == null)
					type = "CDATA";
				elementDecl.addAttribute(new ElementDecl.AttributeDecl(
					attrName,value,values,type,required));
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
				(XSElementDeclaration)term, parent);
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
	class Handler extends DefaultHandler2 implements DeclHandler, ErrorHandler
	{
		Buffer buffer;

		DefaultErrorSource errorSource;
		String text;
		XmlParsedData data;

		HashMap<String, String> activePrefixes;
		Stack<DefaultMutableTreeNode> currentNodeStack;
		Locator loc;
		boolean empty;
		//used to retrieve the XSModel for a particular node
		PSVIProvider psviProvider;

		//{{{ Handler constructor
		Handler(Buffer buffer, String text, DefaultErrorSource errorSource,
			XmlParsedData data)
		{
			this.buffer = buffer;
			this.text = text;
			this.errorSource = errorSource;
			this.data = data;
			this.activePrefixes = new HashMap<String, String>();
			this.currentNodeStack = new Stack<DefaultMutableTreeNode>();
			this.empty = true;
			this.psviProvider = null;

		} //}}}

		private void setPSVIProvider(PSVIProvider psviProvider){
			this.psviProvider = psviProvider;
		}

		//{{{ addError() method
		private boolean ignoreMessage(String message) {
			if (message.startsWith("More pseudo attributes are expected")) return true;
			if (message.startsWith("Content is not allowed in prolog")) return true;
			return false;

		}
		private void addError(int type, String uri, int line, String message)
		{
//			if (ignoreMessage(message)) return;
			errorSource.addError(type,XmlPlugin.uriToFile(uri),line,
				0,0,message);
		} //}}}

		private XSLoader xsLoader = null;

		//{{{ getGrammarForNamespace() method
		// TODO: this is no more called, should remove it !
		private XSModel getModelForNamespace(String uri)
		{
			Log.log(Log.DEBUG,XercesParserImpl.this,"getModelForNamespace("+uri+")");
			// this method has one big inconvenient : it can't deal with
			// "xsi:noNamespaceSchemaLocation"
			// - (the namespace is null, so xsLoader.loadURI can't guess which schema to load)
			if (xsLoader == null) try {
						//switched to explicit class, to avoid System.setProperty()
						//see http://xerces.apache.org/xerces2-j/faq-xs.html#faq-10
			           DOMImplementationSource dir = new org.apache.xerces.dom.DOMXSImplementationSourceImpl();
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

			Log.log(Log.DEBUG,XercesParserImpl.this,"modelToCompletionInfo("+model+")");
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
				//indeed, it's possible (like for XMLSchema-instance),
				//when one uses getModelForNamespace("http://www.w3.org/2001/XMLSchema-instance")
				System.err.println("look! " + attribute.getName());
			}

			return info;
		} //}}}

		//{{{ endDocument() method
		public void endDocument() throws SAXException
		{
			// don't retrieve the null namespace schema
			// via getModelForNamespace(null), as it returns null, anyway !
		} //}}}

		//{{{ setDocumentLocator() method
		public void setDocumentLocator(Locator locator)
		{
			loc = locator;
		} //}}}

		//{{{ resolveEntity() method
		/**
		 * If you do this:
		 * reader.setProperty("use-entity-resolver2", true)
		 * Then this method should be called.
		 */
		public InputSource resolveEntity (String name, String publicId, String baseURI, String systemId)
			throws SAXException, java.io.IOException {

			Log.log(Log.DEBUG,this,"resolveEntity PUBLIC=" + publicId
				+ ", SYSTEM=" + systemId);
			InputSource source = null;

			try {
				source = Resolver.instance().resolveEntity(name, publicId, baseURI, systemId);
			}
			catch(Exception e)
			{
				e.printStackTrace();
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
				// TODO: not sure wether it's the best thing to do :
				//it prints a cryptic "premature end of file"
				// error message
				InputSource dummy = new InputSource(systemId);
				dummy.setPublicId(publicId);
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
			// don't retrieve schema based on prefix mapping anymore.
			// see endElement()
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

			/* retrieve the schema used to validate this element :
			   PROS:
				- already present in memory
				- don't have to worry about fetching the null namespace Schema
				- sure to get the actual schema used to validate the element !
			   CONS:
			    - doesn't fetch validation information for attribute-only
				  schemas (like the one for XML-SchemaInstance)
			 */
			ElementPSVI psvi = psviProvider.getElementPSVI();
			if(psvi!=null)
			{
				XSModel model = psvi.getSchemaInformation();

				//model is present only for top-level schema elements
				//like ACTIONS vs ACTION in actions.xsd
				if(model != null)
				{
					//get the prefix
					String prefix=qName.substring(0,qName.length()-sName.length());

					//convert to Completion info
					CompletionInfo info = modelToCompletionInfo(model);

					//set Completion Info
					// TODO: what happens when reusing the same prefix for several
					//       namespaces
					if(info != null){
						Log.log(Log.DEBUG,this,"setting completion info for :"+prefix);
						data.setCompletionInfo(prefix,info);
					}
				}
			}

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
				if(tag.getName().equals(qName))
				{
					int line = Math.min(buffer.getLineCount() - 1,
						loc.getLineNumber() - 1);
					int column = loc.getColumnNumber() - 1;
					int offset = Math.min(buffer.getLength(),
						buffer.getLineStartOffset(line)
						+ column);

					tag.setEnd(buffer.createPosition(offset));
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

		/* (non-Javadoc)
		 * @see org.xml.sax.ext.DefaultHandler2#resolveEntity(java.lang.String, java.lang.String)
		 */
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
		{
			return resolveEntity(null, publicId, null, systemId);
		}
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
