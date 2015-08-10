/*
 * SchemaLoader.java
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
 *
 */
 
package xml.parser;

//{{{ Imports
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;


import java.io.IOException;


// jEdit
import xml.Resolver;
import xml.cache.CacheEntry;
import xml.cache.Cache;
import static xml.Debug.*;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.Buffer;


//JAXP 1.4
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;


// Xerces
import org.apache.xerces.impl.Constants;
import org.apache.xerces.parsers.XMLGrammarPreparser;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.EntityResolver2Wrapper;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.util.SAXInputSource;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;

//}}}



/**
 * Utility class to load a schema
 *
 * @author Eric Le Lay
 * @version $Id$
 */
public final class SchemaLoader
{
	
	/** factory for XML Schema */
	private final SchemaFactory xsdFactory;

	/** factory for Relax NG (XML syntax) */
	private final SchemaFactory rngFactory;

	/** factory for Relax NG (Compact syntax) */
	private final SchemaFactory rncFactory;
	
	/** singleton */
	private static SchemaLoader instance;
	
	/** singleton constructor : init the factories */
	private SchemaLoader(){
		xsdFactory = new org.apache.xerces.jaxp.validation.XMLSchemaFactory();
		rngFactory = new com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory();
		rncFactory = new com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory();
	}
	
	
	/**
	 *
	 * @param	current	systemId of the parsed document
	 * @param	schemaFileNameOrURL	identifier of the schema to load
	 * @param	handler	channel to report errors
	 * @param	requestingBuffer	buffer requesting the ValidatorHandler, for caching
	 */
	public ValidatorHandler loadJaxpGrammar(String current,String schemaFileNameOrURL, ErrorHandler handler, Buffer requestingBuffer)
			throws SAXException, IOException, IllegalArgumentException
	{
		if(DEBUG_SCHEMA_MAPPING)Log.log(Log.DEBUG,SchemaLoader.class,"loadJaxpGrammar("+current+","+schemaFileNameOrURL+")");

		if(schemaFileNameOrURL == null)throw new IllegalArgumentException("schemaFileNameOrURL may not be null");
		
		javax.xml.validation.Schema schema;

		String realLocation = Resolver.instance().resolveEntityToPath(/*name*/null,
			/*publicId*/null,current,schemaFileNameOrURL);

		CacheEntry en = Cache.instance().get(realLocation,"Schema");
		
		if(en == null){
			if(DEBUG_CACHE)Log.log(Log.DEBUG,SchemaLoader.class,"not found schema in cache for "+schemaFileNameOrURL);

			// get the factory
			SchemaFactory factory;
	
			if(schemaFileNameOrURL.endsWith(".xsd"))factory = xsdFactory;
			else if(schemaFileNameOrURL.endsWith(".rnc"))factory = rncFactory;
			else if(schemaFileNameOrURL.endsWith(".rng")
				|| schemaFileNameOrURL.endsWith(".xml"))factory = rngFactory;
			else throw new IOException("can't guess schema type based on extension : "+schemaFileNameOrURL);
			
			
			factory.setResourceResolver(Resolver.instance());
			factory.setErrorHandler(handler);
			
			
			// compile the schema
			
			// resolve it again in case the reported systemId is not the schemaFileNameOrURL
			// see test_data/parentRef for instance, where schemaFileNameOrURL is 'actual_table.rng'
			// reported systemId is the complete path to actual_table.rng, and the resolver must resolve
			// 'table.rng' given the systemId of 'actual_table.rng', so it needs the complete path to succeed
			InputSource is = Resolver.instance().resolveEntity(/*name*/null,
				/*publicId*/null,current,schemaFileNameOrURL);
			
		
			javax.xml.transform.Source s = new javax.xml.transform.sax.SAXSource(is);
			schema = factory.newSchema(s);
			// FIXME: can't get the actual components of the schema this way
			//        so the cache isn't cleared when a schema component has changed !
			en = Cache.instance().put(realLocation,"Schema",schema);
			en.getRequestingBuffers().add(requestingBuffer);
		} else {
			if(DEBUG_CACHE)Log.log(Log.DEBUG,SchemaLoader.class,"found schema in cache for "+schemaFileNameOrURL);
			schema = (Schema)en.getCachedItem();
			en.getRequestingBuffers().add(requestingBuffer);
		}
		
		// get the verifier
		ValidatorHandler verifier = schema.newValidatorHandler();
		
		return verifier;
	}
	
    /** Property identifier: symbol table. */
    public static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;

    /** Property identifier: grammar pool. */
    public static final String GRAMMAR_POOL =
        Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;

    // feature ids

    /** Namespaces feature id (http://xml.org/sax/features/namespaces). */
    protected static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";

    /** Property identifier: namespace-schema location pairs. */
    protected static final String SCHEMA_LOCATION =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_LOCATION;
    
    /** Property identifier: no namespace schema location. */
    protected static final String SCHEMA_NONS_LOCATION =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_NONS_LOCATION;

	/**
	 * load a native Xerces Grammar, which can be used to get an XSModel for CompletionInfo
	 * @param	schemaLocation	required to find imported grammars (see test_data/import_schema)
	 */
    /* inspired from samples/xni/XMLGrammarBuilder.java in the Xerces 2.9.0 binary distribution
       some code also existed to load DTDs.
     */
	public Grammar loadXercesGrammar(Buffer current, String systemId, String schemaLocation, String nonsSchemaLocation, ErrorHandler handler)
	{
        SymbolTable sym = new SymbolTable(2031);
        XMLGrammarPreparser preparser = new XMLGrammarPreparser(sym);
        CachedGrammarPool grammarPool = new CachedGrammarPool(current);
        
		preparser.registerPreparser(XMLGrammarDescription.XML_SCHEMA, null);
        preparser.setProperty(SCHEMA_LOCATION, schemaLocation);
        preparser.setProperty(SCHEMA_NONS_LOCATION, nonsSchemaLocation);
        preparser.setGrammarPool(grammarPool);
        preparser.setFeature(NAMESPACES_FEATURE_ID, true);
        preparser.setEntityResolver(new EntityResolver2Wrapper(Resolver.instance()));
        preparser.setErrorHandler(new ErrorHandlerWrapper(handler));

        try {
            InputSource in = Resolver.instance().resolveEntity(null,null,current.getPath(),systemId);
            if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,SchemaLoader.class,"going to preparse "+systemId);
            Grammar g = preparser.preparseGrammar(XMLGrammarDescription.XML_SCHEMA,
            				new SAXInputSource(in));
            	//doesn't work : it's not resolved (see XMLEntityManager during step by step debugging)
            	// new XMLInputSource(null,systemId,current.getPath()));
            if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,SchemaLoader.class,"preparsed grammar="+g);
            return g;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}

	/** @return singleton instance */
	public static SchemaLoader instance()
	{
		if(instance == null)instance = new SchemaLoader();
		return instance;
	}
	
}
