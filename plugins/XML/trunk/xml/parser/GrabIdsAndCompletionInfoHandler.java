/*
 * GrabIdsAndCompletionInfo.java - grab CompletionInfo and id declarations from SAX parser
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2011 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.parser;

import static xml.Debug.DEBUG_CACHE;
import static xml.Debug.DEBUG_DTD;
import static xml.Debug.DEBUG_RNG_SCHEMA;
import static xml.Debug.DEBUG_XSD_SCHEMA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.LexicalHandler;

import xml.Resolver;
import xml.XmlParsedData;
import xml.cache.Cache;
import xml.cache.CacheEntry;
import xml.completion.CompletionInfo;
import xml.completion.ElementDecl;
import xml.completion.EntityDecl;
import xml.completion.IDDecl;

//{{{ GrabIdsAndCompletionInfoHandler class
/**
 * ContentHandler, DeclHandler and LexicalHandler to construct CompletionInfo from the DTD declaration or
 * pull CompletionInfo from SchemaAutoLoader.
 * It also grabs IDs in parsed documents.  
 **/
class GrabIdsAndCompletionInfoHandler extends DefaultHandler2 implements ContentHandler, DeclHandler, LexicalHandler
{
    /**
	 * reference to xercesParserImpl for its stopped field
	 */
	private final XercesParserImpl xercesParserImpl;

	// {{{ members
	/**
	 * the buffer being parsed
	 */
	Buffer buffer;

	ErrorListErrorHandler errorHandler;
	XmlParsedData data;
	
	/** used to access the type of attributes
	 *  to know if they are IDs in case of XSD and RNG
	 */
	Stack<ElementDecl> elementDeclStack;
	
	Locator loc;
	/** at root of document (no startElement() seen yet)*/
	boolean root = true;
	
	/** used to retrieve CompletionInfos for different namespaces (RNG) */
	SchemaAutoLoader schemaAutoLoader;
	
	/** used to register entities with the XmlParsedData at endDTD() */
	CompletionInfo dtdCompletionInfo;

	/** used to install the locator in the resolver */
	private MyEntityResolver resolver;
	
	// }}}
	// {{{ Handler constructor
	GrabIdsAndCompletionInfoHandler(XercesParserImpl xercesParserImpl, Buffer buffer, ErrorListErrorHandler errorHandler,
		XmlParsedData data, MyEntityResolver resolver)
	{
		this.xercesParserImpl = xercesParserImpl;
		this.buffer = buffer;
		this.errorHandler = errorHandler;
		this.data = data;
		this.elementDeclStack = new Stack<ElementDecl>();
		this.schemaAutoLoader = null;
		this.dtdCompletionInfo = null;
		this.resolver = resolver;
	} // }}}

	void setSchemaAutoLoader(SchemaAutoLoader sal){
		this.schemaAutoLoader = sal;
	}
	
	//{{{ setDocumentLocator() method
	public void setDocumentLocator(Locator locator)
	{
		loc = locator;
		resolver.setDocumentLocator(locator);
	} //}}}

	//{{{ startPrefixMapping() method
	public void startPrefixMapping(String prefix, String uri)
	{
		if(!root){
			data.allNamespacesBindingsAtTop = false;
		}
		
		// check for built-in completion info for this URI
		// (eg, XSL, XSD, XHTML has this).
		if(uri != null)
		{
			CompletionInfo info = CompletionInfo
				.getCompletionInfoForNamespace(uri);
			if(info != null)
			{
				Log.log(Log.DEBUG,XercesParserImpl.class,"using built-in completion info for "+uri);
				data.setCompletionInfo(uri,info);
				return;
			}
			if(schemaAutoLoader != null 
				&& schemaAutoLoader.getCompletionInfo() != null
					&& schemaAutoLoader.getCompletionInfo().containsKey(uri))
			{
				info = schemaAutoLoader.getCompletionInfo().get(uri);
				Log.log(Log.DEBUG,XercesParserImpl.class,"setting completionInfo for '"+prefix+"' : "+info.namespace+")");
				if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,XercesParserImpl.class,info);
				data.setCompletionInfo(uri,info);
			}
		}
	} //}}}

	private void setCompletionInfoFromSchema(String ns, String location, String schemaLocation, String nonsSchemaLocation){
		if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,GrabIdsAndCompletionInfoHandler.class,"setCompletionInfoFromSchema("+ns+","+location+","+schemaLocation+","+nonsSchemaLocation+")");
		Map<String,CompletionInfo> infos = XSDSchemaToCompletion.getCompletionInfoFromSchema(location,schemaLocation, nonsSchemaLocation, errorHandler, buffer);
		for(Map.Entry<String,CompletionInfo> en: infos.entrySet()){
			String nsC = en.getKey();
			Log.log(Log.DEBUG,GrabIdsAndCompletionInfoHandler.class,"setting completion info for :'"+nsC+"'");
			data.setCompletionInfo(nsC,en.getValue());
		}
	}

	//{{{ startElement() method
	public void startElement(String namespaceURI,
		String lName, // local name
		String qName, // qualified name
		Attributes attrs) throws SAXException
	{
		if(this.xercesParserImpl.stopped)
			throw new XercesParserImpl.StoppedException();

		if(root){
			// root element :
			
			// retrieve no-namespace CompletionInfo
			if(schemaAutoLoader != null 
				&& schemaAutoLoader.getCompletionInfo() != null
					&& schemaAutoLoader.getCompletionInfo().containsKey(""))
			{
				data.setCompletionInfo("",schemaAutoLoader.getCompletionInfo().get(""));
				// TODO: what about no-namespace ?
			}
			
			// retrieve schema grammar if available
			String schemaLocation = attrs.getValue("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
			String noNamespaceSchemaLocation = attrs.getValue("http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation");
			// there may be a better implementation in Xerces in XMLSchemaLoader
			if(schemaLocation != null){
				String[] nsLocationPairs = schemaLocation.split("\\s+");
				if(nsLocationPairs.length % 2 == 0){
					
					for(int i=0;i<nsLocationPairs.length;i+=2){
						String ns = nsLocationPairs[i];
						String location = nsLocationPairs[i+1];
						setCompletionInfoFromSchema(ns,location, schemaLocation,noNamespaceSchemaLocation);
					}
				}
			}
			if(noNamespaceSchemaLocation != null){
				setCompletionInfoFromSchema(null,noNamespaceSchemaLocation, schemaLocation,noNamespaceSchemaLocation);
			}


			// unset root flag
			root = false;
		}
		

		String currentURI = xml.PathUtilities.urlToPath(loc.getSystemId());

		// what do we do in this case?
		if(loc.getLineNumber() == -1){
			Log.log(Log.WARNING,XercesParserImpl.class,"no location for "+qName);
			return;
		}
		
		ElementDecl cDecl = null;
		
		if(elementDeclStack.isEmpty()
			|| elementDeclStack.peek() == null
		   /* happens for DTD based elements */
			|| elementDeclStack.peek().elementHash == null){
			CompletionInfo i = data.getCompletionInfo(namespaceURI);
			if(i == null){
				if(schemaAutoLoader != null 
					&& schemaAutoLoader.getCompletionInfo() != null
						&& schemaAutoLoader.getCompletionInfo().containsKey(namespaceURI))
				{
					i = schemaAutoLoader.getCompletionInfo().get(namespaceURI);
				}
			}
			
			if(i != null){
				cDecl  = i.elementHash.get(lName);
				if(cDecl == null){
					// fallback to any element ...
					cDecl = i.getElementDeclLocal(lName);
				}
			}
		}else{
			cDecl = elementDeclStack.peek().elementHash.get(lName);
		}
		elementDeclStack.push(cDecl);
		
		// add all attributes with type "ID" to the ids vector
		for(int i = 0; i < attrs.getLength(); i++)
		{
			if(attrs.getType(i).equals("ID")
				// as in http://www.w3.org/TR/xml-id/
				|| attrs.getQName(i).equals("xml:id")
				|| (cDecl != null 
					&& cDecl.getAttribute(attrs.getLocalName(i)) != null
					&& "ID".equals(cDecl.getAttribute(attrs.getLocalName(i)).type)))
			{
				data.ids.put(attrs.getValue(i), new IDDecl(currentURI,
					attrs.getValue(i),qName,
					loc.getLineNumber() - 1,
					loc.getColumnNumber() - 1));
			}
		}

		if(!buffer.getPath().equals(currentURI))
			return;

	} //}}}
	
	//{{{ endElement() method
	public void endElement(String namespaceURI,
		String sName, // simple name
		String qName  // qualified name
		) throws SAXException
	{
		if(this.xercesParserImpl.stopped)
			throw new XercesParserImpl.StoppedException();

		if(!buffer.getPath().equals(xml.PathUtilities.urlToPath(loc.getSystemId())))
			return;
              
		// what do we do in this case?
		if(loc.getLineNumber() == -1)
			return;

		elementDeclStack.pop();
		
	} //}}}

	//{{{ DTD related methods
	
	//{{{ startDTD() method
	/**
	 * cache CompletionInfo for DTD (doesn't work for composite DTDs if a part changes)
	 */
	@Override
	public void startDTD(String name, String publicId, String systemId) throws SAXException
	{
		if(DEBUG_DTD)Log.log(Log.DEBUG,GrabIdsAndCompletionInfoHandler.class,"startDTD("+name+","+publicId+","+systemId+")");
		if(publicId == null && systemId == null)
		{
			// DTD in the document itself, don't cache it as it will parsed again anyway
			if(DEBUG_CACHE)Log.log(Log.DEBUG,GrabIdsAndCompletionInfoHandler.class,"DTD in the document, not caching");
			dtdCompletionInfo = data.getNoNamespaceCompletionInfo();
		}
		else
		{
			try
			{
				String realLocation = Resolver.instance().resolveEntityToPath(null, publicId, buffer.getPath(), systemId);
				CacheEntry ce = Cache.instance().get(realLocation,XercesParserImpl.COMPLETION_INFO_CACHE_ENTRY);
				if(ce == null)
				{
					if(DEBUG_CACHE)Log.log(Log.DEBUG,GrabIdsAndCompletionInfoHandler.class,"CompletionInfo not in cache for DTD, caching");
					dtdCompletionInfo = data.getNoNamespaceCompletionInfo();
					ce = Cache.instance().put(realLocation,XercesParserImpl.COMPLETION_INFO_CACHE_ENTRY,dtdCompletionInfo);
					ce.getRequestingBuffers().add(buffer);
				}
				else
				{
					if(DEBUG_CACHE)Log.log(Log.DEBUG,GrabIdsAndCompletionInfoHandler.class,"CompletionInfo in cache for DTD, reusing");
					ce.getRequestingBuffers().add(buffer);
					dtdCompletionInfo = (CompletionInfo)ce.getCachedItem();
					data.setCompletionInfo("", dtdCompletionInfo);
				}
			}
			catch(IOException ioe)
			{
				throw new SAXException("error resolving DTD path",ioe);
			}
		}
	}
	//}}}
	
	//{{{ endDTD() method
	/**
	 * register the entities in XmlParsedData
	 */
	@Override
	public void endDTD()
	{
		data.setCompletionInfo("", dtdCompletionInfo);
	}
	//}}}
	
	//{{{ elementDecl() method
	public void elementDecl(String name, String model)
	{
		if(DEBUG_DTD)Log.log(Log.DEBUG,XercesParserImpl.class,"elementDecl("+name+","+model+")");
		ElementDecl element = data.getElementDecl(name,0);
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
		if(DEBUG_DTD)Log.log(Log.DEBUG,XercesParserImpl.class,"attributeDecl("+eName+","+aName+","+type+","+valueDefault+","+value+")");
		ElementDecl element = data.getElementDecl(eName,0);
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
			aName,null,value,values,type,required));
	} //}}}

	//{{{ internalEntityDecl() method
	public void internalEntityDecl(String name, String value)
	{
		if(DEBUG_DTD)Log.log(Log.DEBUG,XercesParserImpl.class,"internalEntityDecl("+name+","+value+")");
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
		if(DEBUG_DTD)Log.log(Log.DEBUG,XercesParserImpl.class,"externalEntityDecl("+name+","+publicId+","+systemId+")");
		if(name.startsWith("%"))
			return;

		data.getNoNamespaceCompletionInfo()
			.addEntity(EntityDecl.EXTERNAL,name,
			publicId,systemId);
	} //}}}
	//}}}
	
}// }}}