/*
 * SchemaToCompletion.java
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

import java.util.*;
import java.io.IOException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import com.thaiopensource.relaxng.edit.*;
import com.thaiopensource.xml.util.Name;
import com.thaiopensource.resolver.xml.sax.SAX;
import com.thaiopensource.util.VoidValue;


import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.Buffer;

import xml.cache.Cache;
import xml.cache.CacheEntry;
import xml.completion.CompletionInfo;
import xml.completion.ElementDecl;
import static xml.completion.ElementDecl.AttributeDecl;
import static xml.Debug.*;

/**
 * converts the RNG object model to a list of CompletionInfo
 */
public class SchemaToCompletion
{
	private static final String RNG_DTD_COMPATIBILITY_NS =
		"http://relaxng.org/ns/compatibility/annotations/1.0";
		
	/** the empty namespace */
	private static final String INHERIT = "#inherit";
	
	/**
	* @param	current	systemId of the mapping document, to resolve the schema URL
	* @param	schemaFileNameOrURL	identifier of the schema to load
	* @param	handler	channel to report errors
	* @param	requestingBuffer	Buffer requesting the CompletionInfo (for caching)
	*/
	public static Map<String,CompletionInfo> rngSchemaToCompletionInfo(String current, String schemaFileNameOrURL, ErrorHandler handler, Buffer requestingBuffer){
		Map<String,CompletionInfo> infos = new HashMap<String,CompletionInfo>();

		String realLocation = null;
		
		try{
			realLocation = xml.Resolver.instance().resolveEntityToPath(
				/*name*/null,
				/*publicId*/null,
				current,
				schemaFileNameOrURL);
		}catch(IOException ioe){
			Log.log(Log.ERROR,SchemaToCompletion.class,"error resolving schema location",ioe);
			return infos;
		}
		
		CacheEntry en = Cache.instance().get(realLocation,"CompletionInfo");
		if(en == null){
			if(DEBUG_CACHE)Log.log(Log.DEBUG, SchemaToCompletion.class,"CompletionInfo not found in cache for "+schemaFileNameOrURL);

			com.thaiopensource.relaxng.input.InputFormat pif;
			
			if(schemaFileNameOrURL.endsWith(".rnc")){
				pif = new xml.translate.BufferCompactParseInputFormat();
			}else{
				pif = new xml.translate.BufferSAXParseInputFormat();
			}
			
			try{
				InputSource is = xml.Resolver.instance().resolveEntity(
					/*name*/null,
					/*publicId*/null,
					current,
					schemaFileNameOrURL);
				
				SchemaCollection schemas = pif.load(
					is.getSystemId(),
					new String[]{},
					"unused",
					handler,
					new xml.translate.EntityResolverWrapper(xml.Resolver.instance(),false));
				
				SchemaDocument mainSchema = schemas.getSchemaDocumentMap().get(schemas.getMainUri());
				
				Pattern p = mainSchema.getPattern();
				
				MyPatternVisitor v = new MyPatternVisitor(infos,schemas);
				p.accept(v);
				
				v.resolveRefs();
				// use a more intuitive '' key for the completion info
				// of the no-namespace elements
				if(infos.containsKey(INHERIT)){
					infos.put("",infos.get(INHERIT));
					infos.remove(INHERIT);
				}
				
				
				//{{{ put everything in cache
				List<CacheEntry> related = new ArrayList<CacheEntry>(schemas.getSchemaDocumentMap().size());
				
				for(String url : schemas.getSchemaDocumentMap().keySet()){
					if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,SchemaToCompletion.class,"grammar is composed of "+url);
					try{
						String realLoc = xml.Resolver.instance().resolveEntityToPath(null, null, current, url);
						CacheEntry ce = Cache.instance().put(realLoc,"GrammarComponent","Dummy");
						related.add(ce);
					}catch(IOException ioe){
						Log.log(Log.ERROR, SchemaToCompletion.class, "error resolving path for "+url, ioe);
					}
				}
				
				related.add(Cache.instance().put(realLocation,"CompletionInfo",infos));
				// mark all components related and requested by the buffer
				for(CacheEntry ce : related){
					ce.getRelated().addAll(related);
					ce.getRelated().remove(ce);
					ce.getRequestingBuffers().add(requestingBuffer);
				}
				//}}}
				
			}catch(Exception e){
				// FIXME: handle exceptions
				Log.log(Log.ERROR, SchemaToCompletion.class, e);
			}
		}else{
			if(DEBUG_CACHE)Log.log(Log.DEBUG, SchemaToCompletion.class,"found CompletionInfo in cache for "+schemaFileNameOrURL);
			infos = (Map<String,CompletionInfo>)en.getCachedItem();
		}
		return infos;
	}
	
	private static class GrabDefinesVisitor
	extends AbstractPatternVisitor< Map<String,List<DefineComponent>> >
	implements ComponentVisitor < Map<String,List<DefineComponent>> >
	{
		private Map<String,List<DefineComponent>> comps = new HashMap<String,List<DefineComponent>>();
		public Map<String,List<DefineComponent>> visitGrammar(GrammarPattern p){
			p.componentsAccept(this);
			return comps;
		}
		
		public Map<String,List<DefineComponent>> visitDefine(DefineComponent c){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,GrabDefinesVisitor.class,"visitDefine("+c.getName()+")");
			if(!comps.containsKey(c.getName())){
				comps.put(c.getName(),new ArrayList<DefineComponent>());
			}
			comps.get(c.getName()).add(c);
			return comps;
		}
		
		public Map<String,List<DefineComponent>> visitDiv(DivComponent c){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,GrabDefinesVisitor.class,"visitDiv()");
			c.componentsAccept(this);
			return comps;
		}
		
		public Map<String,List<DefineComponent>> visitInclude(IncludeComponent c){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,GrabDefinesVisitor.class,"visitInclude("+c.getHref()+")");
			// do not grab definitions inside the include element
			return comps;
		}
		
		public Map<String,List<DefineComponent>> visitPattern(Pattern p){
			throw new UnsupportedOperationException("visitPattern("+p+")");
		}
	}
	/** add-hock collection to gather definitions in nested grammars.
	 */
	private static class StackableMap<K,V>
	{
		private List<Map<K,V>> contents;
		
		StackableMap(int initialSize)
		{
			contents = new ArrayList<Map<K,V>>(initialSize);
		}
		
		public void push(Map<K,V> step)
		{
			contents.add(0,step);
		}

		public Map<K,V> pop()
		{
			return contents.remove(0);
		}
		
		public V get(K key)
		{
			if(contents.isEmpty())return null;
			return contents.get(0).get(key);
		}
		
		public V getFromParent(K key)
		{
			if(contents.size()<2)return null;
			return contents.get(1).get(key);
		}

		public boolean containsKey(K key)
		{
			if(contents.isEmpty())return false;
			return contents.get(0).containsKey(key);
		}
		
		public boolean parentContainsKey(K key)
		{
			if(contents.size()<2)return false;
			return contents.get(1).containsKey(key);
		}
		
		public String toString(){
			return "StackableMap{"+contents+"}";
		}
	}
	
	private static class MyAttributeVisitor extends AbstractPatternVisitor< List<AttributeDecl> >{
		private Map<String,String> values;
		private List<String> data;
		private StackableMap<String,List<DefineComponent>> defined;
		private boolean required;
		MyAttributeVisitor(StackableMap<String,List<DefineComponent>> defined,boolean required){
			this.defined=defined;
			this.required = required;
		}
		
		public List<AttributeDecl> visitAttribute(AttributePattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitAttribute(req="+required+")");
			if(values!=null)throw new IllegalArgumentException("attribute//attribute isn't allowed");
			values = new HashMap<String,String>();
			data = new ArrayList<String>();
			boolean savedRequired = required;
			
			List<Name> names = p.getNameClass().accept(new MyNameClassVisitor());

			p.getChild().accept(this);

			
			List<AttributeDecl> attrs = new ArrayList<AttributeDecl>(names.size());
			
			String value;
			String type;
			
			
			if(values.isEmpty())
			{
				if(data.isEmpty()) type = "";
				else type = data.get(0);
				
				value = "";
			}
			else
			{
				value = values.keySet().iterator().next();
				type = values.get(value);
			}
			
			if(p.getAttributeAnnotation(RNG_DTD_COMPATIBILITY_NS,"defaultValue")!=null){
				value = p.getAttributeAnnotation(RNG_DTD_COMPATIBILITY_NS,"defaultValue");
			}
			
			for(Name name:names){
				attrs.add(new AttributeDecl(name.getLocalName(),
					name.getNamespaceUri(),
					value,
					new ArrayList<String>(values.keySet()),
					type,
					savedRequired));
			}
			required = savedRequired;
			return attrs;
		}
				   
		public List<AttributeDecl> visitChoice(ChoicePattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitChoice()");
			boolean savedRequired = required;
			// only one of the choice is required : don't mark any required
			required = false;
			List res = new ArrayList<AttributeDecl>();
			for(Pattern c: p.getChildren())
			{
				res.addAll(c.accept(this));
			}
			required = savedRequired;
			return res;
		}
				   
		public List<AttributeDecl> visitElement(ElementPattern p){
			throw new IllegalArgumentException("attribute//element isn't allowed");
		}
				   
		public List<AttributeDecl> visitEmpty(EmptyPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitEmpty()");
			return Collections.emptyList();
		}
				   
		public List<AttributeDecl> visitExternalRef(ExternalRefPattern p){
			throw new IllegalArgumentException("attribute//externalRef isn't allowed");
		}
				   
		public List<AttributeDecl> visitGrammar(GrammarPattern p){
			throw new IllegalArgumentException("attribute//grammar isn't allowed");
		}
				   
		public List<AttributeDecl> visitGroup(GroupPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitGroup()");
			
			List res = new ArrayList<AttributeDecl>();
			for(Pattern c: p.getChildren())
			{
				res.addAll(c.accept(this));
			}
			return res;
		}
				   
		public List<AttributeDecl> visitInterleave(InterleavePattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitInterleave()");
			List res = new ArrayList<AttributeDecl>();
			for(Pattern c: p.getChildren())
			{
				res.addAll(c.accept(this));
			}
			return res;
		}
				   
		public List<AttributeDecl> visitList(ListPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitList()");
			return p.getChild().accept(this);
		}
				   
		public List<AttributeDecl> visitMixed(MixedPattern p){
			throw new IllegalArgumentException("attribute//mixed doesn't make sense");
		}
				   
		public List<AttributeDecl> visitNotAllowed(NotAllowedPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitNotAllowed()");
			return Collections.emptyList();
		}
				   
		public List<AttributeDecl> visitOneOrMore(OneOrMorePattern p){
			throw new IllegalArgumentException("attribute//oneOrMore doesn't make sense to me");
		}
				   
		public List<AttributeDecl> visitOptional(OptionalPattern p){
			throw new IllegalArgumentException("attribute//optional doesn't make sense to me");
		}
				   
		public List<AttributeDecl> visitParentRef(ParentRefPattern p){
			throw new IllegalArgumentException("attribute//parentRef isn't allowed");
		}
				 
		/** 
		 *  no endless loop here, since definitions in attributes can't be recursive
		 */
		public List<AttributeDecl> visitRef(RefPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitRef()");
			if(defined.containsKey(p.getName()))
			{
				List res = new ArrayList<AttributeDecl>();
				for(DefineComponent dc: defined.get(p.getName())){
					res.addAll(dc.getBody().accept(this));
				}
				return res;
			}
			else throw new IllegalArgumentException("unknown define : "+p.getName());
		}
				   
		public List<AttributeDecl>	visitText(TextPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitText()");
			return Collections.emptyList();
		}
				   
		public List<AttributeDecl> visitValue(ValuePattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitValue("+p.getPrefixMap()+")");
			values.put(p.getValue(),p.getType());
			return Collections.emptyList();
		}

		public List<AttributeDecl> visitData(DataPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitData()");
			data.add(p.getType());
			return Collections.emptyList();
		}
				   
				   
		public List<AttributeDecl> visitZeroOrMore(ZeroOrMorePattern p){
			throw new IllegalArgumentException("attribute//zeroOrMore doesn't make sense to me");
		}

		public List<AttributeDecl> visitPattern(Pattern p){
			throw new IllegalArgumentException("which pattern did I forget ? "+p);
		}

	}
	
	private static class MyNameClassVisitor implements NameClassVisitor<List<Name> >
	{
		private boolean any = false;
		private List<Name> names = new ArrayList<Name>();
		
		public List<Name> visitName(NameNameClass nc)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitName("+nc.getNamespaceUri()+","+nc.getPrefix()+":"+nc.getLocalName()+")");
			names.add(new Name(nc.getNamespaceUri(),nc.getLocalName()));
			return names;
		}
		
		public List<Name> visitNsName(NsNameNameClass nc)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyNameClassVisitor.class,"voidVisitNsName("+nc.getNs()+")");
			names.add(new Name(nc.getNs(),null));
			if(nc.getExcept()!=null)
			{
				Log.log(Log.WARNING,MyNameClassVisitor.class,
					"doesn't handle except clause in RNG schema");
			}
			return names;
		}
		
		public List<Name> visitAnyName(AnyNameNameClass nc)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyNameClassVisitor.class,"visitAnyName()");
			any = true;
			return names;
		}
		
		public List<Name> visitChoice(ChoiceNameClass nc)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyNameClassVisitor.class,"voidVisitChoiceNameClass()");
			nc.childrenAccept(this);
			return names;
		}
	}
	
	private static class MyPatternVisitor extends AbstractPatternVisitor<List>
	implements ComponentVisitor<List>
	{
		private Map<String,CompletionInfo> info;
		private SchemaCollection schemas;
		private boolean empty = false;
		private boolean required = true;
		private StackableMap<String,List<DefineComponent>> defines;
		private Map<DefineComponent,List> definesContents;
		private ElementDecl parent;
		
		MyPatternVisitor(Map<String,CompletionInfo> info,SchemaCollection schemas){
			this.info = info;
			this.schemas = schemas;
			parent = null;
			defines = new StackableMap<String,List<DefineComponent>>(2);
			definesContents = new HashMap<DefineComponent,List>(); 
		}
		
		
		public List visitAttribute(AttributeAnnotation a)
		{
			throw new UnsupportedOperationException("visitAttribute(Annotation)");
		}
		
		public List visitAttribute(AttributePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitAttribute()");

			MyAttributeVisitor visitor = new MyAttributeVisitor(defines,required);
			
			List<AttributeDecl> attrs = p.accept(visitor);
			if(parent!=null)
			{
				for(AttributeDecl d:attrs)
				{
					parent.addAttribute(d);
				}
			}
			return attrs;
		}
		
		
		public List visitChoice(ChoicePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitChoice()");
			boolean savedRequired = required;
			required = false;
			List res = new ArrayList<Object>();
			for(Pattern c: p.getChildren())
			{
				res.addAll(c.accept(this));
			}
			required = savedRequired;
			return res;
		}
		
		public List visitComment(Comment c)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitComment()");
			return Collections.emptyList();
		}
		
		public List visitComponent(Component c)
		{
			throw new UnsupportedOperationException(" voidVisitComponent() should not be called");
		}
		
		public List visitData(DataPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitData()");
			// TODO: display the data type of an element
			return Collections.emptyList();
		}
		
		
		public List visitDefine(DefineComponent c)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitDefine("+c.getName()+","+c.getCombine()+")");
			return Collections.emptyList();
		}
		
		public List visitDiv(DivComponent p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitDiv()");
			// may be used to add documentation to a group of elements.
			List res = new ArrayList<Object>();
			for(Component c: p.getComponents())
			{
				res.addAll(c.accept(this));
			}
			return res;
		}
		
		public List visitElement(ElementAnnotation ea)
		{
			throw new UnsupportedOperationException("visitElement(Annotation)");
		}
		
		public List visitElement(ElementPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitElement()");
			
			ElementDecl myParent = parent;
			empty = false;
			
			MyNameClassVisitor nameVisitor = new MyNameClassVisitor();
			List<Name> myNames = p.getNameClass().accept(nameVisitor);
			
			boolean isEmpty = empty;
			boolean isRequired = required;
			required = true;// by default, attributes are required
			
			if(parent != null && parent.content == null)
				parent.content = new HashSet<String>();
			if(parent != null && parent.elementHash == null)
				parent.elementHash = new HashMap<String,ElementDecl>();
			
			// grab all variants of this element and return them
			List res = new ArrayList();
			for(Name name:myNames)
			{
				CompletionInfo myInfo = info.get(name.getNamespaceUri());
				
				if(myInfo==null)
				{
					myInfo = new CompletionInfo();
					myInfo.namespace = name.getNamespaceUri();
					info.put(name.getNamespaceUri(),myInfo);
					if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"setting completionInfo for "+myInfo.namespace);
				}
				
				ElementDecl me = myInfo.elementHash.get(name.getLocalName());
				

				if(me == null){
					me = new ElementDecl(myInfo,name.getLocalName(),"");
					
					// only start elements are added to the elementHash of the info,
					// so that only start elements show up before root node
					if(parent == null)
					{
						myInfo.addElement(me);
					}
					else
					{
						myInfo.elements.add(me);
					}
					parent = me;
					p.getChild().accept(this);
					parent = myParent;
				}
				
				res.add(me);
				if(myParent!=null)
				{
					myParent.content.add(name.getLocalName());
					myParent.elementHash.put(name.getLocalName(),me);
				}
			}
			if(myParent!=null){
				myParent.any   = nameVisitor.any;
				myParent.empty = isEmpty;
			}
			required=isRequired;
			return res;
		}
		
		public List visitEmpty(EmptyPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitEmpty()");
			empty=true;
			return Collections.emptyList();
		}

		public List visitExternalRef(ExternalRefPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitExternalRef("+p.getUri()+")");
			// "The externalRef matches if the pattern contained in the specified URL matches" [RNG tutorial]
			SchemaDocument sc = schemas.getSchemaDocumentMap().get(p.getUri());
			// no risk of endless loop since externalRefs are not allowed to be recursive
			return sc.getPattern().accept(this);
		}
		
		public List visitGrammar(GrammarPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitGrammar()");
			defines.push(p.accept(new GrabDefinesVisitor()));
			//for the include
			p.componentsAccept(this);
			
			List res = new ArrayList();
			// explore the tree from <start>
			if(defines.containsKey(DefineComponent.START)){
				for(DefineComponent dc: defines.get(DefineComponent.START)){
					res.addAll(dc.getBody().accept(this));
				}
			}else{
				throw new IllegalArgumentException("Grammar without a start element !");
			}
			defines.pop();
			return res;
		}
		
		public List visitGroup(GroupPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitGroup()");

			List res = new ArrayList<Object>();
			for(Pattern c: p.getChildren())
			{
				res.addAll(c.accept(this));
			}
			return res;
		}
		
		public List visitInclude(IncludeComponent c)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitInclude("+c.getUri()+")");
			SchemaDocument sc = schemas.getSchemaDocumentMap().get(c.getUri());

			// the included element MUST be a grammar per the spec
			GrammarPattern g = (GrammarPattern)sc.getPattern();
			
			// a grammar contains only start, define, div, include elements 
			Map<String,List<DefineComponent>> grammarDefinitions = g.accept(new GrabDefinesVisitor());
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"grammar definitions: "+grammarDefinitions.keySet());
			// an include contains only start, define, div, elements 
			GrabDefinesVisitor v = new GrabDefinesVisitor();
			c.componentsAccept(v);
			Map<String,List<DefineComponent>> includeDefinitions = v.comps;
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"include definitions: "+includeDefinitions.keySet());
			// override each define and the start in the grammar with the include's contents 
			grammarDefinitions.putAll(includeDefinitions);
			
			// the included grammar is then like a div : definitions should be merged
			Map<String,List<DefineComponent>> parentDefinitions = defines.pop();
			for(Map.Entry<String,List<DefineComponent>> e:grammarDefinitions.entrySet()){
				if(!parentDefinitions.containsKey(e.getKey())){
					parentDefinitions.put(e.getKey(),new ArrayList<DefineComponent>());
				}
				parentDefinitions.get(e.getKey()).addAll(e.getValue());
			}
			defines.push(parentDefinitions);
			
			// proceed with the grammar's content
			if(defines.containsKey(DefineComponent.START)){
				List res = new ArrayList();
				for(DefineComponent dc: defines.get(DefineComponent.START)){
					res.addAll(dc.getBody().accept(this));
				}
				return res;
			}else{
				throw new UnsupportedOperationException("included grammar without a start element !");
			}
		}
		
		public List visitInterleave(InterleavePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitInterleave()");

			List res = new ArrayList<Object>();
			for(Pattern c: p.getChildren())
			{
				res.addAll(c.accept(this));
			}
			return res;
		}
		
		public List visitZeroOrMore(ZeroOrMorePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitZeroOrMore()");
			boolean savedRequired = required;
			required = false;
			List res =  p.getChild().accept(this);
			savedRequired = required;
			return res;
		}

		
		public List visitList(ListPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitList()");
			//TODO: text completion inside this element
			return Collections.emptyList();
		}
		
		public List visitMixed(MixedPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitMixed()");
			//indicates that the element may contain text
			return Collections.emptyList();
		}
		
		
		public List visitNameClass(NameClass nc)
		{
			throw new UnsupportedOperationException("voidVisitNameClass() shouldn't be called");
			
		}
		
		public List visitNotAllowed(NotAllowedPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitNotAllowed()");
			// not interesting
			return Collections.emptyList();
		}
		
		
		public List visitOneOrMore(OneOrMorePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitOneOrMore()");
			return p.getChild().accept(this);
		}
		
		public List visitOptional(OptionalPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitOptional()");
			boolean savedRequired = required;
			required = false;
			List res =  p.getChild().accept(this);
			required = savedRequired;
			return res;
		}
		
		public List visitParentRef(ParentRefPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitParentRef("+p.getName()+")");
			if(!defines.parentContainsKey(p.getName()))
				throw new IllegalArgumentException("Undefined reference :"+p.getName());

			List<DefineComponent> parentRef = defines.getFromParent(p.getName());
			// must pop the definition to be in the context of the parent grammar
			// if the referenced contents also reference sthing
			Map<String,List<DefineComponent>> myDefinitions = defines.pop();
			
			List res = handleRef(parentRef);
			
			// restore the current grammar"s context
			defines.push(myDefinitions);
			return res;
		}
		
		/** handle ref and parentRef all the same */
		private List handleRef(List<DefineComponent> refs){
			List res = new ArrayList();
			for(DefineComponent dc : refs){
				if(!definesContents.containsKey(dc)){
					// let the attributes get their required flag from the body
					// of the definition only
					boolean savedRequired = required;
					required = true;
					
					//don't recurse indefinitely :
					//  - mark that this reference is explored
					definesContents.put(dc,null);
					//  - explore it
					List r = dc.getBody().accept(this);
					if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,dc.getName()+" defined as "+r);
					//  - set the actual value
					definesContents.put(dc,r);
					
					// correct the requiredness of attributes
					if(parent!=null && !savedRequired){
						for(Object o: r){
							if(o instanceof AttributeDecl){
								AttributeDecl newO = ((AttributeDecl)o).copy();
								newO.required = false;
								parent.attributes.remove((AttributeDecl)o);
								parent.addAttribute(newO);
							}
						}
					}
					required = savedRequired;
				}else{
					//first time we see a reference to this define
					ElementDecl e = new ElementRefDecl(dc,required);
					res.add(e);
					if(parent != null){
						// add the reference to the element as a normal child element
						if(parent.content == null)
							parent.content = new HashSet<String>();
						if(parent.elementHash == null)
							parent.elementHash = new HashMap<String,ElementDecl>();
						parent.elementHash.put(e.name,e);
						parent.content.add(e.name);
					}
				}
			}
			return res;
		}
		
		public List visitPattern(Pattern p)
		{
			// see http://code.google.com/p/jing-trang/issues/detail?id=102
			if(p instanceof ZeroOrMorePattern)
			{
				return visitZeroOrMore((ZeroOrMorePattern)p);
			}
			else
			{
				// we visit everything, so it shouldn't be called
				throw new UnsupportedOperationException("visitPattern("+p+")");
			}
		}
		
		public List visitRef(RefPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitRef("+p.getName()+")");
			if(!defines.containsKey(p.getName())){
				throw new IllegalArgumentException("Undefined reference :"+p.getName());
			}
			return handleRef(defines.get(p.getName()));
		}
		
		public List visitText(TextAnnotation ta)
		{
			throw new UnsupportedOperationException("voidVisitText(Annotation)");
		}	
		
		public List visitText(TextPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitText("+p+")");
			//throwing away : there is no 'mixed' information in the ElementDecl
			return Collections.emptyList();
		}
		
		public List visitValue(ValuePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitValue()");
			//TODO : text completion inside an element
			return Collections.emptyList();
		}
		
	/** replace all ElementRefDecls by their value */
	void resolveRefs(){
		boolean b = true;
			while(b){ // while not stable, apply resolveRefs(ElementDecl)
				b = false;
				if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"resolving references...");
				for(CompletionInfo i : info.values()){
					for(ElementDecl e:i.elements){
						b = b | resolveRefs(e);
					}
				}
			}
	}

	/** replace ElementRefDecls by their value once */
	private boolean resolveRefs(ElementDecl e){
		boolean b = false;
		if(e.elementHash != null){
			Map<String,ElementDecl> copy = new HashMap<String,ElementDecl>(e.elementHash);
			for(Map.Entry<String,ElementDecl> en:copy.entrySet()){
				ElementDecl childDecl = en.getValue();
				if(childDecl instanceof ElementRefDecl){
					if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"resolveRefs("+e.name+")=>"+childDecl.name);
					e.elementHash.remove(en.getKey());
					e.content.remove(en.getKey());
					b = true;
					ElementRefDecl ref = (ElementRefDecl)childDecl;
					DefineComponent dc = ref.ref;
					List res = definesContents.get(dc);
					if(res == null)throw new IllegalArgumentException("can't find definitions for "+dc.getName()+"="+dc);
					else {
						for(Object o:res){
							if(o instanceof ElementDecl){
								ElementDecl oo = (ElementDecl)o;
								if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"adding "+oo);
								if(oo.name != null){
									e.elementHash.put(oo.name,oo);
									e.content.add(oo.name);
								}
							}else if(o instanceof AttributeDecl){
								AttributeDecl oo = (AttributeDecl)o;
								if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"adding "+oo);
								if(ref.required){
									e.addAttribute(oo);
								}else{
									e.addAttribute(oo.copy());
								}
							}else{
								throw new IllegalArgumentException("what's this : "+o);
							}
						}
					}
				}
			}
		}
		return b;
	}
	
	/** intermediate placeholder for a reference */
	private static class ElementRefDecl extends ElementDecl{
		DefineComponent ref;
		static int i = 0;
		boolean required;
		
		ElementRefDecl(DefineComponent ref, boolean required){
			super(null,"_:"+ref.getName()+"_"+(i++),null);
			this.ref = ref;
			this.required = required;
		}
		
		public String toString(){
			return "ref to "+ref.getName()+"="+ref.toString();
		}
	}
	
	}
}
