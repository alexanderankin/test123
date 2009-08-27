/*
 * SchemaMapping.java
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

import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;
import java.io.*;

import javax.xml.parsers.*; // JAXP
import org.xml.sax.SAXException;
import javax.xml.validation.ValidatorHandler;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.IOException;

import org.gjt.sp.util.Log;

/**
 * keeps rules to map a schema to a given instance document.
 * The schema mapping can be serialized in a "schemas.xml" file,
 * compatible with the nXML emacs mode written by James Clark.
 * TODO: some features are still missing (include, apply following rule, transform uri)
 * TODO: with XSD, one has to specify one schema per namespace,
 * it is not supported by this implementation.
 *
 * @author Eric Le Lay
 * @version $Id$
 */
public final class SchemaMapping
{
	/** namespace to use for these rules in schemas.xml */
	private static final String LOCATING_RULES_NS = "http://thaiopensource.com/ns/locating-rules/1.0";
	
	/** default name of the file containing schema mapping rules */
	public static final String SCHEMAS_FILE = "schemas.xml"; 
	
	/** type IDs : typeId -> schema URL or typeId -> typeId */
	private Map<String,String> typeIds;
	
	/** mapping rules */
	private List<Rule> rules;
	
	/** this schema mapping file's URI */
	private String baseURI;
	
	/**
	 * empty mapping
	 */
	public SchemaMapping()
	{
		typeIds = new HashMap<String,String>();
		rules = new ArrayList<Rule>();
		baseURI = null;
	}
	
	/**
	 * @return this schema mapping's URL or null if it's totally in memory
	 */
	public String getBaseURI(){
		return baseURI;
	}
	
	/**
	 * manually add a rule
	 * @param	r	rule to add
	 */
	public void addRule(Rule r){
		rules.add(r);
	}
	
	/**
	 * manually add a typeId mapping.
	 * If the typeId mapping exists already, it is overriden
	 *
	 * @param	typeId	typeId
	 * @param	target	target url or typeId
	 */
	public void addTypeId(String typeId, String target)
	{
		typeIds.put(typeId,target);
	}
	
	/**
	 * iterate over the mappings and return the first hit.
	 * all the parameters are given the same priority : it's really the ordering
	 * of rules which defines a priority order.
	 * Any of the paremeters can be null.
	 * @param	publicId	public ID of the parsed document
	 * @param	systemId	system ID of the parsed document
	 * @param	namespace	namespace of the root element of the parsed document
	 * @param	prefix		prefix of the root element of the parsed document
	 * @param	localName	localName of the root element of the parsed document
	 * @return	schema URL for given document or null if not found
	 */
	public String getSchemaForDocument(String publicId, String systemId,
		String namespace,String prefix,String localName)
	{
		Log.log(Log.DEBUG, SchemaMapping.class,"getSchemaForDocumentElement("+publicId+","+systemId
			+","+namespace+","+prefix+","+localName+")");
		
		String url = null;
		
		for(Rule r:rules)
		{
			if(r.matchURL(systemId) || r.matchNamespace(namespace)
				|| r.matchDocumentElement(prefix,localName))
			{
				url = r.getTarget();
				break;
			}
		}

		// typeIds can be chained, so follow them in the map until no more
		// can be found and return the last one
		if(url!=null)
		{
			while(typeIds.containsKey(url))url = typeIds.get(url);
			System.out.println("found URL:"+url);
		}
		return url;
	}
	
	/**
	 * prefix + localName -> typeId or URL
	 */
	private static class DocumentElementRule extends Rule
	{
		/** matched prefix (can be null) */
		private String prefix;
		/** matched local name (can be null) */
		private String localName;
		
		/**
		 * @param	prefix	matched prefix (can be null)
		 * @param	localName	matched local name (can be null)
		 * @param	target	typeID or URL
		 * @param	targetIsTypeId	typeID / URL ?
		 */
		DocumentElementRule(String prefix, String localName, String target, boolean targetIsTypeId)
		{
			super(target,targetIsTypeId);
			this.prefix = prefix;
			this.localName = localName;
		}
		
		@Override
		boolean matchDocumentElement(String prefix, String localName)
		{
			return
			(this.prefix==null || this.prefix.equals(prefix))&&
			(this.localName==null || this.localName.equals(localName));
		}

		/** @return xml serialization */
		public String toString(){
			return "<documentElement "
			+(prefix!=null ? ("prefix=\""+prefix+"\" ") : "")
			+(localName!=null ? ("localName=\""+localName+"\" ") : "")
			+(targetIsTypeId? "typeId" : "uri")
				+"=\""+target+"\"/>";
		}
	}
	
	/** namespace -> typeId or URL */
	private static class NamespaceRule extends Rule{
		/** matched namespace */
		private String namespace;
		
		/**
		 * @param	ns	matched namespace
		 * @param	target	typeID or URL
		 * @param	targetIsTypeId	typeID / URL ?
		 * @throws	IllegalArgumentException	if ns is null
		 */
		NamespaceRule(String ns, String target, boolean targetIsTypeId){
			super(target,targetIsTypeId);
			if(ns == null)throw new IllegalArgumentException("namespace can't be null");
			namespace=ns;
		}
		
		@Override
		boolean matchNamespace(String namespace){
			return this.namespace.equals(namespace);
		}
		
		/** @return xml serialization */
		public String toString(){
			return "<namespace ns=\""+namespace+"\" "+
			(targetIsTypeId? "typeId" : "uri")
				+"=\""+target+"\"/>";
		}
	}
	
	
	/** URI pattern -> typeID or URL.
	 * The pattern is in glob syntax :
	 *  - star matches any sequence of character except slashes
	 *  - dot really means a dot.
	 * e.g. :
	 *  *.rng -> relax NG schema
	 *  *.xsd -> Schema for Schemas
	 */
	private static class URIPatternRule extends Rule
	{
		
		/** matched pattern : can't be compiled yet because it depends on the URL */
		private String pattern;
		
		/**
		 * @param	pattern	matched pattern
		 * @param	target	typeID or URL
		 * @param	targetIsTypeId	typeID / URL ?
		 * @throws	IllegalArgumentException if pattern is null
		 */
		URIPatternRule(String pattern, String target, boolean targetIsTypeId)
		{
			super(target,targetIsTypeId);
			if(pattern == null)throw new IllegalArgumentException("pattern can't be null");
			this.pattern = pattern;
		}
		
		/**
		 * FIXME: why is it so complicated ?
		 */
		@Override
		boolean matchURL(String url)
		{
			try
			{
				//TODO : what about files over ftp or archive:?
				URL u = new URL("file:///"+url);
				System.out.println("URL="+url);
				URL patternU = new URL(u,pattern);
				String finalPat = patternU.toExternalForm();
				System.out.println("final pat="+finalPat);
				String pat = finalPat.replace("*","[^/]*").replace(".","\\.");
				System.out.println("final final pat="+pat);
				return Pattern.matches(pat,u.toExternalForm());
			}catch(MalformedURLException mue){
				System.err.println("Malformed:"+url);
				return Pattern.matches(pattern.replace("*","[^/]*"),url);
			}
		}

		/**@return xml serialization */
		@Override
		public String toString(){
			return "<uri pattern=\""+pattern+"\" "+
			(targetIsTypeId? "typeId" : "uri")
				+"=\""+target+"\"/>";
		}
	}
	
	/**
	 * URI -> typeId or URL.
	 * This form is useful to map one schema to one particular file.
	 * e.g. : file:///tmp/myfile.xml  -> mycustomschema.xsd
	 */
	private	static class URIResourceRule extends Rule
	{
		/** matched URI */
		private String resource;
		
		/**
		 * FIXME: relative URI should be resolved to absolute URI based upon URI of the schemas.xml
		 * @param	resource	matched resource
		 * @param	target	typeID or URL
		 * @param	targetIsTypeId	typeID / URL ?
		 * @throws	IllegalArgumentException	if resource is null
		 */
		URIResourceRule(String resource,String target, boolean targetIsTypeId)
		{
			super(target,targetIsTypeId);
			if(resource == null)throw new IllegalArgumentException("resource can't be null");
			this.resource = resource;
		}
		
		@Override
		boolean matchURL(String url){
			return resource.equals(url);
		}

		/** xml serialization */
		@Override
		public String toString(){
			return "<uri resource=\""+resource+"\" "+
			(targetIsTypeId? "typeId" : "uri")
				+"=\""+target+"\"/>";
		}
	}
	
	/**
	 * always matches : not really useful for us, is it ?
	 */
	private static class DefaultRule extends Rule{
		
		DefaultRule(String target,boolean targetIsTypeId){
			super(target,targetIsTypeId);
		}
		
		@Override
		boolean matchNamespace(String namespace){
			return true;
		}
		
		@Override
		boolean matchDocumentElement(String prefix,String localName){
			return true;
		}

		@Override
		boolean matchURL(String url){
			return true;
		}

		@Override
		public String toString(){
			return "<default "+(targetIsTypeId? "typeId" : "uri")
				+"=\""+target+"\"/>";
		}
	}
	
	/**
	 * doctype -> typeID or URL.
	 * not used yet.
	 */
	private static class DoctypeRule extends Rule{
		/** matched doctype */
		private String doctype;
		
		/**
		 * @param	doctype	matched doctype
		 * @param	target	typeID or URL
		 * @param	targetIsTypeId	typeID / URL ?
		 * @throws	IllegalArgumentException	if doctype is null
		 */
		DoctypeRule(String doctype, String target,boolean targetIsTypeId){
			super(target,targetIsTypeId);
			if(doctype==null)throw new IllegalArgumentException("doctype can't be null");
			this.doctype=doctype;
		}
		
		@Override
		boolean matchDoctype(String dt){
			return doctype.equals(dt);
		}
		
		/**@return xml serialization */
		@Override
		public String toString(){
			return "<doctypePublicId publicId=\""+doctype+"\" "+
				(targetIsTypeId? "typeId" : "uri")
				+"=\""+target+"\"/>";
		}
	}
	
	/**
	 * base class for all mapping rules.
	 * this allows to keep a list of the rules and try each of them,
	 * regardless of their type.
	 */
	static abstract class Rule{
		
		/** typeId or URL of the schema to use if this rule matches */
		protected String target;
		
		/** for serialisation : output typeId="..." or url="..." */
		protected boolean targetIsTypeId;
		
		/**
		 * @param	target	typeID or URL
		 * @param	targetIsTypeId	typeID / URL ?
		 * @throws	IllegalArgumentException	if target is null
		 */
		Rule(String target,boolean targetIsTypeId){
			if(target==null)throw new IllegalArgumentException("target can't be null");
			this.target=target;
			this.targetIsTypeId = targetIsTypeId;
		}
		
		/**
		 * does the rule match for this namespace of the root element ?
		 * @return false
		 */
		boolean matchNamespace(String namespace){
			return false;
		}
		
		/**
		 * does the rule match for this prefix and local name of the root element ?
		 * @return false
		 */
		boolean matchDocumentElement(String prefix,String localName){
			return false;
		}
		
		/**
		 * does the rule match for this url of the document ?
		 * @return false
		 */
		boolean matchURL(String url){
			return false;
		}
		
		/**
		 * does the rule match for this doctype of the document ?
		 * @return false
		 */
		boolean matchDoctype(String dt){
			return false;
		}

		/** @return target */
		String getTarget(){return target;}
	}
	
	/** deserialize an xml document describing the mapping rules (schemas.xml)
	 * @param	url	url of the document to read
	 * @return	new SchemaMapping taken from the document at url, or null
	 * @throws	IllegalArgumentException	if the url is null
	 */
	public static SchemaMapping fromDocument(String url){
		if(url==null)throw new IllegalArgumentException("url can't be null");

		InputSource input = new InputSource(url);

		final SchemaMapping mapping = new SchemaMapping();
		
		try {
			
			XMLReader reader = XMLReaderFactory.createXMLReader();
						
			DefaultHandler handler = new DefaultHandler()
			{
				
				public void startElement(String uri, String localName, String qName, Attributes attributes)
				{
					if(!LOCATING_RULES_NS.equals(uri))return;//ignore everything in a different namespace

					String target = null;
					boolean targetIsTypeId=false;
					if(attributes.getIndex("","typeId")!=-1)
					{
						target = attributes.getValue("","typeId");
						targetIsTypeId=true;
					}
					else if(attributes.getIndex("","uri")!=-1)
					{
						target = attributes.getValue("","uri");
						targetIsTypeId=false;
					}
					
					if("transformURI".equals(localName))
					{
						System.err.println("SchemaMapping : transformURI not handled !");
						
					}
					else if("uri".equals(localName))
					{
						if(attributes.getIndex("","pattern")!=-1)
						{
							mapping.rules.add(new URIPatternRule(attributes.getValue("","pattern"),target,targetIsTypeId));
						}
						else if(attributes.getIndex("","resource")!=-1)
						{
							mapping.rules.add(new URIResourceRule(attributes.getValue("","resource"),target,targetIsTypeId));
						}
					}
					else if("namespace".equals(localName))
					{
						if(attributes.getIndex("","ns")!=-1)
						{
							mapping.rules.add(new NamespaceRule(attributes.getValue("","ns"),target,targetIsTypeId));
						}
					}
					else if("documentElement".equals(localName))
					{
						String prefix= attributes.getValue("","prefix");
						String name= attributes.getValue("","localName");
						
						mapping.rules.add(new DocumentElementRule(prefix,name,target,targetIsTypeId));
					}
					else if("doctypePublicId".equals(localName))
					{
						mapping.rules.add(new DoctypeRule(attributes.getValue("","publicId"),target,targetIsTypeId));
					}
					else if("include".equals(localName))
					{
						// TODO: xml:base 
						if(attributes.getIndex("","rules")!=-1)
						{
							SchemaMapping map = SchemaMapping.fromDocument(attributes.getValue("","rules"));
							for(Rule r:map.rules)
							{
								mapping.rules.add(r);
							}
							for(String s:map.typeIds.keySet())
							{
								mapping.typeIds.put(s,map.typeIds.get(s));
							}
						}
					}
					else if("typeId".equals(localName))
					{
						if(attributes.getIndex("","id")!=-1)
						{
							// TODO: check unicity ?
							String id = attributes.getValue("","id");
							mapping.typeIds.put(id,target);
						}
					}
				}
			};

			javax.xml.parsers.SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
			factory.setNamespaceAware(true);
			
			// the input document will be validated, so no error message in the handler
			// TODO: what happens when there are errors in the document ?
			ValidatorHandler verifierFilter = SchemaLoader.instance().loadJaxpGrammar(
				null/*current*/,
				"locate.rng",
				handler);

			// ValidatorHandler is only the content handler
			// but it can forward the augmented event to another handler
			reader.setContentHandler(verifierFilter);
			verifierFilter.setContentHandler(handler);
			
			verifierFilter.setErrorHandler(handler);
			
			reader.parse(input);
			mapping.baseURI = url;

		}
		catch (SAXException e)
		{
			e.printStackTrace();
			System.out.println(e);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			System.out.println(ioe);
		}
		return mapping;
	}
	
	/**
	 * serialize to an XML document
	 * @param	output	path to the output file
	 * @throws	IOException	if there is an error during serialization
	 * FIXME: serialized document could be invalid. Is it worth using proper XML serialization ?
	 */
	public void toDocument(String output)throws IOException{
		FileOutputStream fos = new FileOutputStream(output);
		Writer out = new OutputStreamWriter(fos,"UTF-8");
		out.write("<?xml version=\"1.0\" ?>\n");
		out.write("<locatingRules xmlns=\"http://thaiopensource.com/ns/locating-rules/1.0\">\n");
		for(Rule r:rules)
		{
			out.write(r.toString());
		}
		for(String s:typeIds.keySet())
		{
			out.write("<typeId id=\""+s+"\" ");
			String v=typeIds.get(s);
			if(typeIds.containsKey(v))
			{
				/* points to a typeId */
				out.write("typeId");
			}
			else
			{
				/* points to an URL */
				out.write("url");
			}
			out.write("=\""+v+"\"/>\n");
		}
		out.write("</locatingRules>");
		out.close();
	}
}
