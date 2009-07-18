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
import org.xml.sax.XMLFilter;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLFilterImpl;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;

import javax.xml.validation.ValidatorHandler;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;

import org.gjt.sp.util.Log;

import xml.Resolver;
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

	/** saved publicId from parse() */
	private String publicId;
	/** saved systemId from parse() */
	private String systemId;
	
	/** saved locator from setLocator() */
	private Locator locator;
	
	//{{{ Constructors
	
	/**
	 * @param	mapping	schema-mapping rules
	 * @throws	IllegalArgumentException	if mapping is null
	 */
	public SchemaAutoLoader(SchemaMapping mapping)
	{
		super();
		if(mapping == null)throw new IllegalArgumentException("schema mapping may not be null");
		this.mapping=mapping;
	}
	
	
	/**
	 * @param	parent	parent in the XML parsing chain
	 * @param	mapping	schema-mapping rules
	 * @throws	IllegalArgumentException	if mapping is null
	 */
	public SchemaAutoLoader(XMLReader parent,SchemaMapping mapping)
	{
		super(parent);
		if(mapping == null)throw new IllegalArgumentException("schema mapping may not be null");
		this.mapping=mapping;
	}
	//}}}
	
	/**
	 * load a Validator from the url schema and install it after this SchemaAutoLoader
	 * in the parsing chain.
	 * @param	schema	URL or path to the schema
	 */
	private void installJaxpGrammar(String schema) throws SAXException,IOException
	{
			final ValidatorHandler verifierFilter =
				SchemaLoader.instance().loadJaxpGrammar(systemId,schema,getErrorHandler());
			XMLReader parent = getParent();
		
			verifierFilter.setContentHandler(new org.xml.sax.helpers.DefaultHandler());
			if(locator == null)throw new IllegalStateException("LOCATOR");
			verifierFilter.setDocumentLocator(locator);
			verifierFilter.startDocument();
			verifierFilter.setContentHandler(getContentHandler());
			verifierFilter.setErrorHandler(getErrorHandler());
			verifierFilter.setResourceResolver(Resolver.instance());
			
			setContentHandler(verifierFilter);
	}
	
	/**
	 * capture system and public ID to find a matching schema mapping,
	 * @param	input	input to parse
	 */
	@Override
	public void parse(InputSource input)throws SAXException,IOException
	{
		Log.log(Log.DEBUG,SchemaAutoLoader.this,"PARSE input ("+input.getPublicId()+","+input.getSystemId()+")");
		documentElement=true;
		publicId = input.getPublicId();
		systemId = input.getSystemId();
		super.parse(input);
	}
	
	/**
	 * capture sytem  ID to find a matching schema mapping
	 * @param	systemId	systemId of the input to parse
	 */
	@Override
	public void parse(String systemId)throws SAXException,IOException
	{
		Log.log(Log.DEBUG,SchemaAutoLoader.this,"PARSE systemId "+systemId);
		documentElement=true;
		publicId = null;
		systemId = systemId;
		super.parse(systemId);
	}
	
	/**
	 * if this is the root element, try to find a matching schema,
	 * instantiate it and insert it in the parsing chain.
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts)throws SAXException
	{
		if(documentElement)
		{
			Log.log(Log.DEBUG,SchemaAutoLoader.this,"DOC element  ("+uri+","+localName+","+qName+")");
			
			String prefix = qName.equals(localName)? "" : qName.substring(0,qName.indexOf(":"));
			
			String schema = mapping.getSchemaForDocument(
				publicId, systemId,
				uri,prefix,localName);

			if(schema!=null)
			{
				Log.log(Log.DEBUG,SchemaAutoLoader.this,"FOUND SCHEMA: "+schema);
				try
				{
					installJaxpGrammar(schema);
				}
				catch(IOException ioe)
				{
					throw new SAXException("unable to install schema "+schema,ioe);
				}
			}
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
     * debug : test if document is valid 
     */
    @Override
    public void endDocument()
    {
    	// TODO: isValid()
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
