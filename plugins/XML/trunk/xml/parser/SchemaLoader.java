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


import xml.Resolver;


//JAXP 1.4
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.gjt.sp.util.Log;
//}}}

/**
 * Utility class to load a schema
 *
 * @author Eric Le Lay
 * @version $Id$
 */
public final class SchemaLoader
{
	
	/** list of factories for XSD and Relax NG*/
	private static Map<String,SchemaFactory> jaxpFactories;
	
	/** key to find the Relax NG factory */
	private static final String RNG_FACTORY_URL = "http://relaxng.org/ns/structure/1.0";

	/** key to find the XSD factory */
	private static final String XSD_FACTORY_URL = "http://www.w3.org/2001/XMLSchema";
	
	/** singleton */
	private static SchemaLoader instance;
	
	/** singleton constructor */
	private SchemaLoader(){}
	
	
	/** load the 2 implementations of SchemaFactory */
	private void initFactories()
	{
		jaxpFactories = new HashMap<String, SchemaFactory>();
		SchemaFactory f;
		
		f = new org.apache.xerces.jaxp.validation.XMLSchemaFactory();
		jaxpFactories.put(XSD_FACTORY_URL,f);
		
		try{
			Class.forName("org.apache.xerces.jaxp.SAXParserFactoryImpl").newInstance();
		}catch(Exception e){
			Log.log(Log.ERROR,SchemaLoader.class,e);
		}
		
		// FIXME: this is a modified version of the constructor, import the sources in SVN
		f = new org.iso_relax.verifier.jaxp.validation.RELAXNGSchemaFactoryImpl(new com.thaiopensource.relaxng.jarv.VerifierFactoryImpl());
		jaxpFactories.put(RNG_FACTORY_URL,f);
		
	}
	
	/**
	 * TODO: test it with Relax NG compact syntax 
	 *
	 * @param	current	systemId of the parsed document
	 * @param	schemaFileNameOrURL	identifier of the schema to load
	 * @param	handler	channel to report errors
	 */
	public ValidatorHandler loadJaxpGrammar(String current,String schemaFileNameOrURL, ErrorHandler handler )
			throws SAXException, IOException
	{
		Log.log(Log.DEBUG,SchemaLoader.class,"loadJaxpGrammar("+current+","+schemaFileNameOrURL+")");

		if(jaxpFactories == null)initFactories();

		// get the factory
		SchemaFactory factory;

		/* TODO: should add the information in the mapping.xml file,
		   but it's obvious from the extension... */
		if(schemaFileNameOrURL.endsWith(".xsd"))factory = jaxpFactories.get(XSD_FACTORY_URL);
		else factory = jaxpFactories.get(RNG_FACTORY_URL);
		
		
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
