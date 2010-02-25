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
import org.xml.sax.XMLFilter;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;


// jEdit
import xml.Resolver;
import org.gjt.sp.util.Log;


//JAXP 1.4
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

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
	 * TODO: test it with Relax NG compact syntax 
	 *
	 * @param	current	systemId of the parsed document
	 * @param	schemaFileNameOrURL	identifier of the schema to load
	 * @param	handler	channel to report errors
	 */
	public ValidatorHandler loadJaxpGrammar(String current,String schemaFileNameOrURL, ErrorHandler handler )
			throws SAXException, IOException, IllegalArgumentException
	{
		Log.log(Log.DEBUG,SchemaLoader.class,"loadJaxpGrammar("+current+","+schemaFileNameOrURL+")");

		if(schemaFileNameOrURL == null)throw new IllegalArgumentException("schemaFileNameOrURL may not be null");
		
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
		InputSource is = Resolver.instance().resolveEntity(/*name*/null,
			/*publicId*/null,current,schemaFileNameOrURL);
		
		javax.xml.validation.Schema schema;
		
		javax.xml.transform.Source s = new javax.xml.transform.sax.SAXSource(is);
		schema = factory.newSchema(s);
		
		// get the verifier
		ValidatorHandler verifier = schema.newValidatorHandler();
		
		return verifier;
	}

	/** @return singleton instance */
	public static SchemaLoader instance()
	{
		if(instance == null)instance = new SchemaLoader();
		return instance;
	}
}
