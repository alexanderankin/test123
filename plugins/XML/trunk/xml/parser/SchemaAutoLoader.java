/*
 * SchemaAutoLoader.java
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009 Eric Le Lay
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
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;

import javax.xml.validation.ValidatorHandler;
import javax.xml.validation.TypeInfoProvider;

import java.util.Map;
import java.util.Enumeration;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.Buffer;

import xml.Resolver;
import xml.completion.CompletionInfo;
import static xml.Debug.*;
//}}}

/**
 * XMLFilter inserting a Validator based upon rules defined in a SchemaMapping.
 * The schema is inserted between the parser and this SchemaAutoLoader in the chain.
 * If a schema is found, a RewindException is thrown, because it saves a tedious 
 * replay mecanism (see the commented out Replay class).
 * If this is not acceptable, don't forget to call startElement with the document
 * element, and to filter out the startDocument(), startElement() events.
 *
 * This component is only interested in introducing Validators, not providing
 * completion.
 *
 * TODO: if the inserted VerifierFilter doesn't implement EntityResolver2,
 *       entity resolution might fail. finding a test case is not easy,
 *       since resolution is mainly used to load a schema...
 *
 * @author Eric Le Lay
 * @version $Id$
 */
public class SchemaAutoLoader extends XMLFilterImpl implements EntityResolver2
{
	/** flag: are we still waiting for the document element ? */
	private boolean documentElement;
	/** document-schema mapping rules */
	private SchemaMapping mapping;
	
	/** requesting buffer, for caching */
	private Buffer requestingBuffer;
	
	/** saved publicId from parse() */
	private String publicId;
	/** saved systemId from parse() */
	private String systemId;
	
	/** saved locator from setLocator() */
	private Locator locator;
	
	/** saved namespaces before root element */
	private NamespaceSupport docElementNamespaces = new NamespaceSupport();

	/** URL of the installed schema, if any */
	private String schemaURL;
	
	/** CompletionInfo constructed from the installed schema, if any */
	private Map<String,CompletionInfo> completions;

	//{{{ Constructors
	/**
	 * @param	parent	parent in the XML parsing chain
	 * @param	mapping	schema-mapping rules or null if you plan to force the schema
	 */
	public SchemaAutoLoader(XMLReader parent,SchemaMapping mapping, Buffer requestingBuffer)
	{
		super(parent);
		this.mapping=mapping;
		this.requestingBuffer = requestingBuffer;
	}
	//}}}



	/**
	 * force the schema to use for validation and CompletionInfo. 
	 * It disables autodiscovery and doesn't cancel any existing verifier,
	 * so it should be called before parsing.
	 * @param	baseURI	baseURI to resolve the schemaURI against (may be null if schemaURI is absolute)
	 * @param	schemaURI	URI of the schema to install
	 */
	public void forceSchema(String baseURI, String schemaURI)
	throws SAXException, IOException, MalformedURLException
	{
		this.mapping = null;
		installJaxpGrammar(new URL(baseURI), schemaURI, false);
	}

	/**
	 * this doesn't return the schema bound using xsi:schemalocation nor the 
	 * DTD file : only a schema discovered via the SchemaMapping instance.
	 * @return	URL of the schema used for validation or null if no schema was installed
	 */
	public String getSchemaURL(){
		return schemaURL;
	}

	/**
	 * only Relax NG schemas are supported for the moment
	 * @return	CompletionInfo constructed from the schema or null if no Relax NG schema was used
	 */
	public Map<String,CompletionInfo> getCompletionInfo(){
		return completions;
	}
	
	/**
	 * load a Validator from the url schema and install it after this SchemaAutoLoader
	 * in the parsing chain.
	 * @param	baseURI	URL to resolve schemaURL against (may be null if schemaURL is absolute)
	 * @param	schemaURL	URL to the schema	
	 * @param	needReplay	is parsing started and we need to replay events to the verifier
	 */
	private void installJaxpGrammar(final URL baseURI, final String schemaURL, boolean needReplay) throws SAXException,IOException
	{
		// schemas URLs are resolved against the schema mapping file
		final ValidatorHandler verifierFilter =
		SchemaLoader.instance().loadJaxpGrammar(baseURI.toString(),schemaURL,getErrorHandler(),requestingBuffer);
		this.schemaURL = new URL(baseURI,schemaURL).toString();
		
		if(needReplay){
			// replay setDocumentLocator() and startDocument(), but only for the new
			// filter since other components have already received it
			verifierFilter.setContentHandler(new org.xml.sax.helpers.DefaultHandler());
			if(locator == null)throw new IllegalStateException("LOCATOR");
			verifierFilter.setDocumentLocator(locator);
			verifierFilter.startDocument();
		}
		
		verifierFilter.setContentHandler(getContentHandler());
		verifierFilter.setErrorHandler(getErrorHandler());
		verifierFilter.setResourceResolver(Resolver.instance());
		
		setContentHandler(verifierFilter);
		
		// FIXME: very add-hoc, but who uses other extensions for one's schema ?
		if(schemaURL.endsWith("rng") || schemaURL.endsWith("rnc")){
			Map<String,CompletionInfo> info = SchemaToCompletion.rngSchemaToCompletionInfo(baseURI.toString(),schemaURL,getErrorHandler(),requestingBuffer);
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,SchemaAutoLoader.class,"constructed CompletionInfos : "+info);
			completions = info;
		}else if(schemaURL.endsWith("xsd")){
			Map<String,CompletionInfo> infos = XSDSchemaToCompletion.getCompletionInfoFromSchema(schemaURL,null,null,getErrorHandler(),requestingBuffer);
			if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,SchemaAutoLoader.class,"constructed CompletionsInfos : "+infos);
			completions = infos;
		}
	}
	
	/**
	 * capture system and public ID to find a matching schema mapping,
	 * @param	input	input to parse
	 */
	@Override
	public void parse(InputSource input)throws SAXException,IOException
	{
		if(DEBUG_SCHEMA_MAPPING)Log.log(Log.DEBUG,SchemaAutoLoader.this,"PARSE input ("+input.getPublicId()+","+input.getSystemId()+")");
		documentElement=true;
		publicId = input.getPublicId();
		systemId = input.getSystemId();
		docElementNamespaces.pushContext();

		super.parse(input);
	}
	
	/**
	 * capture sytem  ID to find a matching schema mapping
	 * @param	systemId	systemId of the input to parse
	 */
	@Override
	public void parse(String systemId)throws SAXException,IOException
	{
		if(DEBUG_SCHEMA_MAPPING)Log.log(Log.DEBUG,SchemaAutoLoader.this,"PARSE systemId "+systemId);
		documentElement=true;
		publicId = null;
		systemId = systemId;
		docElementNamespaces.pushContext();

		super.parse(systemId);
	}
	
	@Override
	public void startPrefixMapping(String prefix, String ns)throws SAXException {
		/* delay startPrefixMapping, to be able to retrieve the schema in XercesParser.startPrefixMapping
		   and this is not until startElement("root element")*/ 
		if(documentElement && mapping!=null)
		{
			if(DEBUG_SCHEMA_MAPPING)Log.log(Log.DEBUG,SchemaAutoLoader.this,"Prefix Mapping  ("+prefix+","+ns+")");
			docElementNamespaces.declarePrefix(prefix,ns);

		}
		else
		{
		   super.startPrefixMapping(prefix,ns);
		}
	}

	/**
	 * if this is the root element, try to find a matching schema,
	 * instantiate it and insert it in the parsing chain.
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts)throws SAXException
	{
		if(documentElement && mapping != null)
		{
			if(DEBUG_SCHEMA_MAPPING)Log.log(Log.DEBUG,SchemaAutoLoader.this,"DOC element  ("+uri+","+localName+","+qName+")");
			
			String prefix;
			
			if("".equals(localName)){
				//namespaces are off
				prefix = "";
			}else{
				prefix = qName.equals(localName)? "" : qName.substring(0,qName.indexOf(":"));
			}
			
			String politeSystemId = xml.PathUtilities.pathToURL(systemId);
			
			SchemaMapping.Result schema = mapping.getSchemaForDocument(
				publicId, politeSystemId,
				uri,prefix,localName, true);

			if(schema!=null)
			{
				if(DEBUG_SCHEMA_MAPPING)Log.log(Log.DEBUG,SchemaAutoLoader.this,"FOUND SCHEMA: "+schema);
				try
				{
					installJaxpGrammar(schema.baseURI, schema.target,true);
				}
				catch(IOException ioe)
				{
					throw new SAXException("unable to install schema "+schema,ioe);
				}
			}

			//replay the namespace declarations
			for(Enumeration e = docElementNamespaces.getDeclaredPrefixes(); e.hasMoreElements();){
				String pre = (String)e.nextElement();
				super.startPrefixMapping(pre,
					docElementNamespaces.getURI(pre));
			}
			docElementNamespaces.reset();
			
			//root element has been seen
			documentElement=false;
		}
		super.startElement(uri,localName,qName,atts);
	}
	
	/**
	 * manually implement EntityResolver2 because XMLFilterImpl only
	 * implements EntityResolver, and we need EntityResolver2 for Resolver
	 * to work properly
	 * @throws UnsupportedOperationException if getEntityResolver() doesn't implement EntityResolver2
	 */
	public InputSource resolveEntity(String name,String publicId,
                          String baseURI,String systemId)
        throws SAXException, IOException
    {
    	EntityResolver r = getEntityResolver();
    	if(r instanceof EntityResolver2){
    		return ((EntityResolver2)r).resolveEntity(name,publicId,baseURI,systemId);
    	}else{
    		throw new UnsupportedOperationException("SchemaAutoLoader needs EntityResolver2");
    	}
    }

	/**
	 * manually implement EntityResolver2 because XMLFilterImpl only
	 * implements EntityResolver, and we need EntityResolver2 for Resolver
	 * to work properly
	 * @throws UnsupportedOperationException if getEntityResolver() doesn't implement EntityResolver2
	 */
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException,IOException
    {
    	EntityResolver r = getEntityResolver();
    	if(r instanceof EntityResolver2){
    		return ((EntityResolver2)r).getExternalSubset(name,baseURI);
    	}else{
    		throw new UnsupportedOperationException("SchemaAutoLoader needs EntityResolver2");
    	}
    }

    /**
     * capture the locator, in case we need to pass it to a schema
     * @see installJaxpGrammar(String)
     */
    @Override
    public void setDocumentLocator(Locator l)
    {
    	this.locator = l;
    	super.setDocumentLocator(l);
    }
    
}
