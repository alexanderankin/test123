package xml.parser;

// {{{ imports
import java.io.File;
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

import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.PSVIProvider;
import org.apache.xerces.xs.StringList;
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
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
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

import sidekick.SideKickParsedData;
import xml.Resolver;
import xml.XmlParsedData;
import xml.XmlPlugin;
import xml.SchemaMappingManager;
import xml.completion.CompletionInfo;
import xml.completion.ElementDecl;
import xml.completion.EntityDecl;
import xml.completion.IDDecl;
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import static xml.Debug.*;
// }}}
// {{{ class XercesParserImpl
/**
 * A SideKick XML parser that uses this under the covers:
 * reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
 *
 * @author kerik-sf
 * @version $Id$
 */

public class XercesParserImpl extends XmlParser
{
	//{{{ XercesParserImpl constructor
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

		
		XmlParsedData data = new XmlParsedData(buffer.getName(),false);
		
		if(text.length() == 0)return data;
		
		SchemaMapping mapping;
		if(SchemaMappingManager.isSchemaMappingEnabled(buffer))
		{
			mapping = SchemaMappingManager.getSchemaMappingForBuffer(buffer);
		}
		else
		{
			mapping = null;
		}

		ErrorListErrorHandler errorHandler = new ErrorListErrorHandler(
				 errorSource
				,buffer.getPath()
			);
		Handler handler = new Handler(buffer,text,errorHandler,data);


		XMLReader reader = null;
		SchemaAutoLoader schemaLoader = null;
		try
		{
			// One has to explicitely require the parser from XercesPlugin, otherwise
			// one gets the crimson version bundled in the JRE and the rest fails
			// miserably (see Plugin Bug #2950392)
			reader = new org.apache.xerces.parsers.SAXParser();
			reader.setFeature("http://xml.org/sax/features/validation",
				buffer.getBooleanProperty("xml.validate"));
			reader.setFeature("http://apache.org/xml/features/validation/dynamic",true);
			reader.setFeature("http://apache.org/xml/features/validation/schema",
				buffer.getBooleanProperty("xml.validate"));
			reader.setFeature("http://xml.org/sax/features/namespaces",
				!buffer.getBooleanProperty("xml.namespaces.disable"));
			reader.setFeature("http://xml.org/sax/features/use-entity-resolver2", true);
			reader.setFeature("http://apache.org/xml/features/xinclude",
				buffer.getBooleanProperty("xml.xinclude"));
			reader.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris",
				buffer.getBooleanProperty("xml.xinclude.fixup-base-uris"));
			//reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error",true);
			
			//get access to the DTD
			reader.setProperty("http://xml.org/sax/properties/declaration-handler",handler);
			//get access to the schema
			handler.setPSVIProvider((PSVIProvider)reader);

			schemaLoader = new SchemaAutoLoader(reader,mapping);

			schemaLoader.setErrorHandler(errorHandler);
			schemaLoader.setContentHandler(handler);
			schemaLoader.setEntityResolver(handler);

			//get access to the RNG schema
			handler.setSchemaAutoLoader(schemaLoader);
			reader = schemaLoader;
			
			//schemas.xml are disabled
			if(!SchemaMappingManager.isSchemaMappingEnabled(buffer)){
				String schemaFromProp = buffer.getStringProperty(SchemaMappingManager.BUFFER_SCHEMA_PROP);
				if(schemaFromProp != null){
					// the user has set the schema manually
					String baseURI = xml.PathUtilities.pathToURL(buffer.getPath());
					Log.log(Log.DEBUG, this,"forcing schema to {"+baseURI+","+schemaFromProp+"}");
					// schemas URLs are resolved against the buffer
					try
					{
						schemaLoader.forceSchema( baseURI,schemaFromProp);
					}
					catch(IOException ioe)
					{
						ioe.printStackTrace();
						Log.log(Log.ERROR,this,ioe);
					}
				}
			}
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
			se.printStackTrace();
			if(se.getMessage() != null)
			{
				errorSource.addError(ErrorSource.ERROR,buffer.getPath(),
					0,0,0,se.getMessage());
			}
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,this,e);
			e.printStackTrace();
		}
		finally
		{
			//set this property for the xml-open-schema action to work
			if(schemaLoader != null && schemaLoader.getSchemaURL()!=null)
			{
				buffer.setStringProperty(SchemaMappingManager.BUFFER_AUTO_SCHEMA_PROP,
					schemaLoader.getSchemaURL());
			}
		}

		Collections.sort(data.ids,new IDDecl.Compare());
		return data;
	} //}}}

	//{{{ Private members

	//{{{ ...
	public XmlTag findParent(EditPane editPane, int caret){

		if (caret == 0)
			return null;
		SideKickParsedData _data = SideKickParsedData
			.getParsedData(editPane.getView());
		if(!(_data instanceof XmlParsedData))
			return null;
		if(XmlPlugin.isDelegated(editPane.getTextArea()))
			return null;
		XMLReader reader = null;
		try
		{
			// One has to explicitely require the parser from XercesPlugin, otherwise
			// one gets the crimson version bundled in the JRE and the rest fails
			// miserably (at least on Mac OS X, JDK 5)
			reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			reader.setFeature("http://xml.org/sax/features/validation",false);
			reader.setFeature("http://xml.org/sax/features/namespaces",true);
			reader.setFeature("http://xml.org/sax/features/use-entity-resolver2", true);
			reader.setFeature("http://apache.org/xml/features/xinclude",false);
			//reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error",true);
		}
		catch(SAXException se)
		{
			se.printStackTrace();
			Log.log(Log.ERROR,this,se);
		}


		XmlParsedData data = (XmlParsedData)_data;

		Buffer buffer = editPane.getBuffer();
		
		String text = buffer.getText(0,caret);

		FastHandler handler = new FastHandler(buffer,data);

		InputSource source = new InputSource();

		source.setCharacterStream(new StringReader(text));
		source.setSystemId(buffer.getPath());

		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);
		reader.setEntityResolver(handler);
		try
		{
			reader.parse(source);
		}
		catch(StoppedException e)
		{
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		catch(SAXParseException spe)
		{
			// already handled
		}
		catch(SAXException se)
		{
			se.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	} //}}}
	
	
	//{{{ xsElementToElementDecl() method
	private void xsElementToElementDecl(XSNamedMap elements, CompletionInfo info,
		XSElementDeclaration element, ElementDecl parent)
	{
		if(parent != null && parent.content == null)
			parent.content = new HashSet<String>();

		String name = element.getName();
		
		if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,XercesParserImpl.class,"xsElementToElementDecl("+element.getNamespace()+":"+name+")");
		
		if(info.elementHash.get(name) != null)
		{
			// one must add the element to its parent's content, even if
			// one knows the element already
			if(parent!=null) parent.content.add(name);
			return;
		}

		ElementDecl elementDecl = null;


		if ( element.getAbstract()
			/* I don't understand this condition.
		       As far as I understand, every top level element can be
			   head of a substitution group.
			   An algorithm showing quadratic performance :
			   		for each element e as argument to this method,
					  for each element f in elements
					    verify the substitution group of f and if e is the head, add f to parent
			   TODO: write an example, fix the code
		       || element.getName().endsWith(".class") */
		   )
		{

			for (int j=0; j<elements.getLength(); ++j) {
				XSElementDeclaration decl = (XSElementDeclaration)elements.item(j);
				XSElementDeclaration group = decl.getSubstitutionGroupAffiliation();
				if (group != null && group.getName().equals(name)) {
					if(info.elementHash.get(decl.getName()) == null){
						//only add it if it's undeclared or we'll get it twice in XML Insert
						info.addElement(new ElementDecl(info, decl.getName(), null));
					}
					if (parent != null) parent.content.add(decl.getName());
				}
			}

			/* we shouldn't care about the type of an abstract element,
			   as it's not allowed in a document. Would it be the case,
			   one should not forget to fix the NullPointerException on elementDecl
			   that will arise when setting the attributes. Maybe use the type declaration
			   for every element...*/
			return;
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
					xsAttributeToElementDecl(elementDecl,attr.getAttrDeclaration(),attr.getRequired());
			}
		}
	} //}}}

	private void xsAttributeToElementDecl(ElementDecl elementDecl,XSAttributeDeclaration decl, boolean required){
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
	    // {{{ members
		Buffer buffer;

		ErrorListErrorHandler errorHandler;
		String text;
		XmlParsedData data;

		//not used anymore : everything is done in startPrefixMapping
		//HashMap<String, String> activePrefixes;
		Stack<DefaultMutableTreeNode> currentNodeStack;
		Locator loc;
		boolean empty;
		/** at root of document (no startElement() seen yet)*/
		boolean root = true;
		
		//used to retrieve the XSModel for a particular node
		PSVIProvider psviProvider;
		
		/** used to retrieve CompletionInfos for different namespaces (RNG) */
		SchemaAutoLoader schemaAutoLoader;
		
		// }}}
		// {{{ Handler constructor
		Handler(Buffer buffer, String text, ErrorListErrorHandler errorHandler,
			XmlParsedData data)
		{
			this.buffer = buffer;
			this.text = text;
			this.errorHandler = errorHandler;
			this.data = data;
			//this.activePrefixes = new HashMap<String, String>();
			this.currentNodeStack = new Stack<DefaultMutableTreeNode>();
			this.empty = true;
			this.psviProvider = null;
			this.schemaAutoLoader = null;

		} // }}}

		private void setPSVIProvider(PSVIProvider psviProvider){
			this.psviProvider = psviProvider;
		}

		private void setSchemaAutoLoader(SchemaAutoLoader sal){
			this.schemaAutoLoader = sal;
		}
		
		//{{{ grammarToCompletionInfo() method
		private CompletionInfo modelToCompletionInfo(XSModel model)
		{

			if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,XercesParserImpl.this,"modelToCompletionInfo("+model+")");
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
				XSAttributeDeclaration attribute = (XSAttributeDeclaration)attributes.item(i);
				//indeed, it's possible (like for XMLSchema-instance),
				//when one uses getModelForNamespace("http://www.w3.org/2001/XMLSchema-instance")
				//or http://www.w3.org/XML/1998/namespace : see network.xml : base, lang, space
				// FIXME: now the attributes appear in the edit tag dialog, but in the same namespace
				// as the other attributes (no mixed namespace support for elements either)
				System.err.println("look! " + attribute.getName());
				for(ElementDecl e:info.elements){
					xsAttributeToElementDecl(e,attribute,false);
				}
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

			if(DEBUG_RESOLVER)Log.log(Log.DEBUG,this,"resolveEntity("+name+","+publicId+","+baseURI+","+systemId+")");

			InputSource source = null;

			try {
				source = Resolver.instance().resolveEntity(name, publicId, baseURI, systemId);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				errorHandler.getErrorSource().addError(ErrorSource.ERROR,
					buffer.getPath(),
					Math.max(0,loc.getLineNumber()-1),0,0,
					e.getMessage());
			}

			if(source == null)
			{
				Log.log(Log.ERROR,this,"PUBLIC=" + publicId
					+ ", SYSTEM=" + systemId
					+ " cannot be resolved");
				// TODO: not sure whether it's the best thing to do :
				// it prints a cryptic "premature end of file"
				// error message
				InputSource dummy = new InputSource(systemId);
				dummy.setPublicId(publicId);
				dummy.setCharacterStream(new StringReader("<!-- -->"));
				return dummy;
			}
			else
			{
				if(DEBUG_RESOLVER)Log.log(Log.DEBUG,this,"PUBLIC=" + publicId
					+ ", SYSTEM=" + systemId
					+ " resolved to " + source.getSystemId());
				return source;
			}
		} //}}}

		//{{{ startPrefixMapping() method
		public void startPrefixMapping(String prefix, String uri)
		{
			//not used anymore activePrefixes.put(prefix,uri);
			// check for built-in completion info for this URI
			// (eg, XSL, XSD, XHTML has this).
			if(uri != null)
			{
				CompletionInfo info = CompletionInfo
					.getCompletionInfoForNamespace(uri);
				if(info != null)
				{
					Log.log(Log.DEBUG,XercesParserImpl.class,"using built-in completion info for "+uri);
					data.setCompletionInfo(prefix,info);
					return;
				}
				if(schemaAutoLoader != null 
					&& schemaAutoLoader.getCompletionInfo() != null
						&& schemaAutoLoader.getCompletionInfo().containsKey(uri))
				{
					info = schemaAutoLoader.getCompletionInfo().get(uri);
					Log.log(Log.DEBUG,XercesParserImpl.class,"setting completionInfo for '"+prefix+"' : "+info.namespace+")");
					if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,XercesParserImpl.class,info);
					data.setCompletionInfo(prefix,info);
				}
			}
			
			
			// don't retrieve schema based on prefix mapping anymore (for XSD).
			// see endElement(), where we use the PSVI
			// that's wrong because CompletionInfos are no more associated with
			// prefixes
			// TODO: test this assertion
		} //}}}

		//{{{ endPrefixMapping() method
		public void endPrefixMapping(String prefix)
		{
			// moved everything to startPrefixMapping()
			// then we get completion even if there is an error afterward
			
		} //}}}

		//{{{ startElement() method
		public void startElement(String namespaceURI,
			String lName, // local name
			String qName, // qualified name
			Attributes attrs) throws SAXException
		{
			if(stopped)
				throw new StoppedException();

			if(root){
				// root element : 
				// retrieve no-namespace CompletionInfo
				if(schemaAutoLoader != null 
					&& schemaAutoLoader.getCompletionInfo() != null
						&& schemaAutoLoader.getCompletionInfo().containsKey(""))
				{
					data.setCompletionInfo("",
						schemaAutoLoader.getCompletionInfo().get(""));
					// TODO: what about no-namespace ?
				}
				
				// unset root flag
				root = false;
			}
			
			empty = true;

			String currentURI = xml.PathUtilities.urlToPath(loc.getSystemId());

			// what do we do in this case?
			if(loc.getLineNumber() == -1){
				Log.log(Log.WARNING,XercesParserImpl.class,"no location for "+qName);
				return;
			}

			// add all attributes with type "ID" to the ids vector
			for(int i = 0; i < attrs.getLength(); i++)
			{
				if(attrs.getType(i).equals("ID")
					// as in http://www.w3.org/TR/xml-id/
					|| attrs.getQName(i).equals("xml:id"))
				{
					data.ids.add(new IDDecl(currentURI,
						attrs.getValue(i),qName,
						loc.getLineNumber() - 1,
						loc.getColumnNumber() - 1));
				}
			}

			if(!buffer.getPath().equals(currentURI))
				return;

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
					new XmlTag(qName,namespaceURI==null ? "" : namespaceURI,pos,attrs));

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
					String prefix;
					if (qName.length() == sName.length())
						prefix = "";
					else
						prefix=qName.substring(0,qName.length()-sName.length()-1);

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

			if(!buffer.getPath().equals(xml.PathUtilities.urlToPath(loc.getSystemId())))
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

		//{{{ elementDecl() method
		public void elementDecl(String name, String model)
		{
			if(DEBUG_DTD)Log.log(Log.DEBUG,XercesParserImpl.class,"elementDecl("+name+","+model+")");
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

			ArrayList<String> values;

			if(type.startsWith("("))
			{
				values = new ArrayList<String>();

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

		// {{{
		/* (non-Javadoc)
		 * @see org.xml.sax.ext.DefaultHandler2#resolveEntity(java.lang.String, java.lang.String)
		 */
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
		{
			if(DEBUG_RESOLVER)Log.log(Log.DEBUG,XercesParserImpl.class,"simple resolveEnt("+publicId+","+systemId+")");
			return resolveEntity(null, publicId, null, systemId);
		}// }}}
	}// }}}

	//{{{ FastHandler class
	class FastHandler extends DefaultHandler2 implements DeclHandler, ErrorHandler
	{
	    // {{{ members
		Buffer buffer;

		XmlParsedData data;

		HashMap<String, String> activePrefixes;
		Stack<DefaultMutableTreeNode> currentNodeStack;
		Locator loc;
		boolean empty;
		//used to retrieve the XSModel for a particular node
		PSVIProvider psviProvider;
		// }}}
		// {{{ FastHandler constructor
		FastHandler(Buffer buffer,XmlParsedData data)
		{
			this.buffer = buffer;
			this.data = data;
			this.activePrefixes = new HashMap<String, String>();
			this.currentNodeStack = new Stack<DefaultMutableTreeNode>();
		} // }}}

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

			if(DEBUG_RESOLVER)Log.log(Log.DEBUG,this,"resolveEntity("+name+","+publicId+","+baseURI+","+systemId+")");

			InputSource source = null;

			try {
				source = Resolver.instance().resolveEntity(name, publicId, baseURI, systemId);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			if(source == null)
			{
				Log.log(Log.ERROR,this,"PUBLIC=" + publicId
					+ ", SYSTEM=" + systemId
					+ " cannot be resolved");
				// TODO: not sure whether it's the best thing to do :
				// it prints a cryptic "premature end of file"
				// error message
			}
			else
			{
				if(DEBUG_RESOLVER)Log.log(Log.DEBUG,this,"PUBLIC=" + publicId
					+ ", SYSTEM=" + systemId
					+ " resolved to " + source.getSystemId());
			}
			return source;
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
		} //}}}

		//{{{ startElement() method
		public void startElement(String namespaceURI,
			String lName, // local name
			String qName, // qualified name
			Attributes attrs) throws SAXException
		{
			if(stopped)
				throw new StoppedException();
		} //}}}

		//{{{ endElement() method
		public void endElement(String namespaceURI,
			String sName, // simple name
			String qName  // qualified name
			) throws SAXException
		{
			if(stopped)
				throw new StoppedException();

		} //}}}

		//{{{ characters() method
		public void characters (char ch[], int start, int length)
			throws SAXException
		{
			if(stopped)
				throw new StoppedException();

			empty = false;
		} //}}}

		//{{{ findTagStart() method
		private int findTagStart(int offset)
		{
			/*for(int i = offset; i >= 0; i--)
			{
				if(text.charAt(i) == '<')
					return i;
			}
			*/
			return 0;
		} //}}}

		// {{{
		/* (non-Javadoc)
		 * @see org.xml.sax.ext.DefaultHandler2#resolveEntity(java.lang.String, java.lang.String)
		 */
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
		{
			// TODO: check wether it's actually called
			Log.log(Log.DEBUG,XercesParserImpl.class,"simple resolveEnt("+publicId+","+systemId+")");
			return resolveEntity(null, publicId, null, systemId);
		}// }}}
	}// }}}

	//{{{ StoppedException class
	static class StoppedException extends SAXException
	{
		StoppedException()
		{
			super("Parsing stopped");
		}
	} //}}}
} // }}}
