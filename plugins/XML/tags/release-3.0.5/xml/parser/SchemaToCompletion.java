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
@SuppressWarnings({"rawtypes", "unchecked"}) // whole class is littered with lists of AttributeDecl,ElementDecl which do not share common super type
public class SchemaToCompletion
{
	private static final String RNG_DTD_COMPATIBILITY_NS =
		"http://relaxng.org/ns/compatibility/annotations/1.0";

	/** the empty namespace */
	private static final String INHERIT = "#inherit";
	/** any local name... */
	private static final String ANY = "__WHAT_YOU_WANT_IN_NS__";
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
				List roots  = p.accept(v);

				v.addRootsToCompletionInfos(roots);
				v.resolveRefs();
				// use a more intuitive '' key for the completion info
				// of the no-namespace elements
				if(infos.containsKey(INHERIT)){
					CompletionInfo nons = infos.get(INHERIT);
					nons.namespace = "";
					infos.put("",nons);
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
		private Map<String,List<DefineComponent>> comps;
		private SchemaCollection schemas;
		private Map<String, String> urisDefaultNamespace;


		public GrabDefinesVisitor(SchemaCollection schemas, Map<String, String> urisDefaultNamespace) {
			this.schemas = schemas;
			this.urisDefaultNamespace = urisDefaultNamespace;
			this.comps = new HashMap<String,List<DefineComponent>>();
		}

		@Override
		public Map<String,List<DefineComponent>> visitGrammar(GrammarPattern p){
			p.componentsAccept(this);
			return comps;
		}

		@Override
		public Map<String,List<DefineComponent>> visitDefine(DefineComponent c){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,GrabDefinesVisitor.class,"visitDefine("+c.getName()+")");
			if(!comps.containsKey(c.getName())){
				comps.put(c.getName(),new ArrayList<DefineComponent>());
			}
			comps.get(c.getName()).add(c);
			return comps;
		}

		@Override
		public Map<String,List<DefineComponent>> visitDiv(DivComponent c){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,GrabDefinesVisitor.class,"visitDiv()");
			c.componentsAccept(this);
			return comps;
		}

		@Override
		public Map<String,List<DefineComponent>> visitInclude(IncludeComponent c)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,GrabDefinesVisitor.class,"visitInclude("+c.getHref()+")");
			SchemaDocument sc = schemas.getSchemaDocumentMap().get(c.getUri());

			urisDefaultNamespace.put(c.getUri(), c.getNs());

			// the included element MUST be a grammar per the spec
			GrammarPattern g = (GrammarPattern)sc.getPattern();

			// a grammar contains only start, define, div, include elements
			Map<String,List<DefineComponent>> grammarDefinitions = g.accept(new GrabDefinesVisitor(schemas, urisDefaultNamespace));
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,GrabDefinesVisitor.class,"grammar definitions: "+grammarDefinitions.keySet());
			// an include contains only start, define, div, elements
			GrabDefinesVisitor overridesVisitor = new GrabDefinesVisitor(schemas, urisDefaultNamespace);
			c.componentsAccept(overridesVisitor);
			Map<String,List<DefineComponent>> includeDefinitions = overridesVisitor.comps;
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"include definitions: "+includeDefinitions.keySet());
			// override each define and the start in the grammar with the include's contents
			grammarDefinitions.putAll(includeDefinitions);

			for(Map.Entry<String, List<DefineComponent>> en: grammarDefinitions.entrySet()){
				if(!comps.containsKey(en.getKey())){
					comps.put(en.getKey(),new ArrayList<DefineComponent>());
				}
				comps.get(en.getKey()).addAll(en.getValue());
			}
			return comps;

		}

		@Override
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

	/**
	 * only visitAttribute returns a non empty list of AttributeDecl
	 */
	private static class MyAttributeVisitor extends AbstractPatternVisitor< List<AttributeDecl> >{
		private Map<String,String> values;
		private List<String> data;
		private StackableMap<String,List<DefineComponent>> defined;
		private boolean required;
		private Map<String, String> urisDefaultNamespace;

		MyAttributeVisitor(StackableMap<String,List<DefineComponent>> defined,boolean required, Map<String, String> urisDefaultNamespace){
			this.defined=defined;
			this.required = required;
			this.urisDefaultNamespace = urisDefaultNamespace;
		}

		public List<AttributeDecl> visitAttribute(AttributePattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitAttribute(req="+required+")");
			if(values!=null)throw new IllegalArgumentException("attribute//attribute isn't allowed");
			values = new HashMap<String,String>();
			data = new ArrayList<String>();

			List<Name> names = p.getNameClass().accept(new MyNameClassVisitor(urisDefaultNamespace));

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

			// as soon as there are more than one name required, can't mark each attributeDecl as required !
			required &= names.size() < 2;

			for(Name name:names){
				attrs.add(new AttributeDecl(name.getLocalName(),
					name.getNamespaceUri(),
					value,
					new TreeSet<String>(values.keySet()),
					type,
					required));
			}
			return attrs;
		}

		public List<AttributeDecl> visitChoice(ChoicePattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitChoice()");
			// here is only the choice of values, so it doesn't change the requiredness of the attribute
			for(Pattern c: p.getChildren())
			{
				c.accept(this);
			}
			return Collections.emptyList();
		}

		public List<AttributeDecl> visitElement(ElementPattern p){
			throw new IllegalArgumentException("attribute//element isn't allowed");
		}

		public List<AttributeDecl> visitEmpty(EmptyPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitEmpty()");
			return Collections.emptyList();
		}

		public List<AttributeDecl> visitExternalRef(ExternalRefPattern p){
			throw new IllegalArgumentException("attribute//externalRef isn't allowed");
		}

		public List<AttributeDecl> visitGrammar(GrammarPattern p){
			throw new IllegalArgumentException("attribute//grammar isn't allowed");
		}

		public List<AttributeDecl> visitGroup(GroupPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitGroup()");

			for(Pattern c: p.getChildren())
			{
				c.accept(this);
			}
			return Collections.emptyList();
		}

		public List<AttributeDecl> visitInterleave(InterleavePattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitInterleave()");
			for(Pattern c: p.getChildren())
			{
				c.accept(this);
			}
			return Collections.emptyList();
		}

		public List<AttributeDecl> visitList(ListPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitList()");
			return p.getChild().accept(this);
		}

		public List<AttributeDecl> visitMixed(MixedPattern p){
			throw new IllegalArgumentException("attribute//mixed doesn't make sense");
		}

		public List<AttributeDecl> visitNotAllowed(NotAllowedPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitNotAllowed()");
			return Collections.emptyList();
		}

		public List<AttributeDecl> visitOneOrMore(OneOrMorePattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitOneOrMore()");
			return p.getChild().accept(this);
		}

		public List<AttributeDecl> visitOptional(OptionalPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitOptional()");
			return p.getChild().accept(this);
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
				for(DefineComponent dc: defined.get(p.getName())){
					dc.getBody().accept(this);
				}
				return Collections.emptyList();
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
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitZeroOrMore()");
			return p.getChild().accept(this);
		}

		public List<AttributeDecl> visitPattern(Pattern p){
			throw new IllegalArgumentException("which pattern did I forget ? "+p);
		}

	}

	private static class MyNameClassVisitor implements NameClassVisitor<List<Name> >
	{
		private boolean any = false;
		private List<Name> names = new ArrayList<Name>();
		private Map<String, String> urisDefaultNamespace;

		public MyNameClassVisitor(Map<String, String> urisDefaultNamespace) {
			this.urisDefaultNamespace = urisDefaultNamespace;
		}

		public List<Name> visitName(NameNameClass nc)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,SchemaToCompletion.class,"visitName("+nc.getNamespaceUri()+","+nc.getPrefix()+":"+nc.getLocalName()+")");

			String namespaceUri = nc.getNamespaceUri();
			if(INHERIT.equals(namespaceUri)){
				if(nc.getSourceLocation() != null){
					String def = urisDefaultNamespace.get(nc.getSourceLocation().getUri());
					if(def != null){
						namespaceUri = def;
					}
				}
			}

			names.add(new Name(namespaceUri,nc.getLocalName()));
			return names;
		}

		public List<Name> visitNsName(NsNameNameClass nc)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyNameClassVisitor.class,"visitNsName("+nc.getNs()+")");

			names.add(new Name(nc.getNs(),ANY));
			Log.log(Log.WARNING,MyNameClassVisitor.class,
					"doesn't handle \"any element\" in namespace in RNG schema (namespace is "+nc.getNs()+")");
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
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyNameClassVisitor.class,"visitChoiceNameClass()");
			nc.childrenAccept(this);
			return names;
		}
	}

	/**
	 * main visitor of the RNG grammar.
	 * Most of the complexity comes from handling defines/ref patterns
	 * the list returned is used to cache the translation of the content of definitions.
	 * @see MyPatternVisitor#handleRef(List)
	 * */
	private static class MyPatternVisitor extends AbstractPatternVisitor<List>
	implements ComponentVisitor<List>
	{
		/** all the CompletionInfo discovered so far,
		 *  one by target namespace.
		 * */
		private Map<String,CompletionInfo> info;
		/**
		 * for grammars defined in multiple files
		 * */
		private SchemaCollection schemas;
		/**
		 * all the defines pattern curently visible (StackeableMap because of parentRef)
		 **/
		private StackableMap<String,List<DefineComponent>> defines;
		/**
		 * the content of all defines patterns so far.
		 * for each DefineComponent, list of ElementDecl and AttributeDecl (ElementDecl can be ElementRefDecl)
		 * */
		private Map<DefineComponent,List> definesContents;
		/**
		 * current element must be empty
		 * */
		private boolean empty = false;
		/**
		 * current attribute is required
		 * */
		private boolean required = true;
		/**
		 * current parent
		 * */
		private ElementDecl parent;

		/**
		 * current default namespace
		 **/
		private Map<String, String> urisDefaultNamespace;

		MyPatternVisitor(Map<String,CompletionInfo> info,SchemaCollection schemas){
			this.info = info;
			this.schemas = schemas;
			parent = null;
			defines = new StackableMap<String,List<DefineComponent>>(2);
			definesContents = new HashMap<DefineComponent,List>();
			urisDefaultNamespace = new HashMap<String, String>();
		}

		@Override
		public List visitComposite(CompositePattern p) {
			// TODO Auto-generated method stub
			return super.visitComposite(p);
		}

		@Override
		public List visitNameClassed(NameClassedPattern p) {
			// TODO Auto-generated method stub
			return super.visitNameClassed(p);
		}

		@Override
		public List visitUnary(UnaryPattern p) {
			// TODO Auto-generated method stub
			return super.visitUnary(p);
		}


		@Override
		public List visitAttribute(AttributePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitAttribute()");

			MyAttributeVisitor visitor = new MyAttributeVisitor(defines,required, urisDefaultNamespace);

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


		@Override
		public List visitChoice(ChoicePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitChoice()");
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


		@Override
		public List visitData(DataPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitData()");
			// TODO: display the data type of an element
			return Collections.emptyList();
		}

		/**
		 * define components are ignored by this visitor; GrabDefinesVisitor has collected them already
		 * @see GrabDefinesVisitor
		 * @see MyPatternVisitor#visitGrammar(GrammarPattern)
		 */
		@Override
		public List visitDefine(DefineComponent c)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitDefine("+c.getName()+","+c.getCombine()+")");
			return Collections.emptyList();
		}

		@Override
		public List visitDiv(DivComponent p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitDiv()");
			// may be used to add documentation to a group of elements.
			List res = new ArrayList<Object>();
			for(Component c: p.getComponents())
			{
				res.addAll(c.accept(this));
			}
			return res;
		}


		@SuppressWarnings("unchecked")
		@Override
		public List visitElement(ElementPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitElement()");

			ElementDecl myParent = parent;
			empty = false;

			MyNameClassVisitor nameVisitor = new MyNameClassVisitor(urisDefaultNamespace);
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
				String namespaceUri = name.getNamespaceUri();

				CompletionInfo myInfo = info.get(namespaceUri);

				if(myInfo==null)
				{
					myInfo = new CompletionInfo();
					myInfo.namespace = namespaceUri;
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
					if(myParent.elementHash.containsKey(name.getLocalName()))
					{
						ElementDecl other = myParent.elementHash.get(name.getLocalName());
						other.merge(me);
						res.remove(res.size()-1);
					}
					else
					{
						myParent.elementHash.put(name.getLocalName(),me);
					}
				}
			}
			if(myParent!=null){
				myParent.any   = nameVisitor.any;
				myParent.empty = isEmpty;
			}
			required=isRequired;
			return res;
		}

		@Override
		public List visitEmpty(EmptyPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitEmpty()");
			empty=true;
			return Collections.emptyList();
		}

		@Override
		public List visitExternalRef(ExternalRefPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitExternalRef("+p.getUri()+")");
			// "The externalRef matches if the pattern contained in the specified URL matches" [RNG tutorial]
			SchemaDocument sc = schemas.getSchemaDocumentMap().get(p.getUri());
			// no risk of endless loop since externalRefs are not allowed to be recursive
			return sc.getPattern().accept(this);
		}

		@Override
		public List visitGrammar(GrammarPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitGrammar()");

			defines.push(p.accept(new GrabDefinesVisitor(schemas, urisDefaultNamespace)));
			//for the include
			// p.componentsAccept(this);

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

		@Override
		public List visitGroup(GroupPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitGroup()");

			List res = new ArrayList<Object>();
			for(Pattern c: p.getChildren())
			{
				res.addAll(c.accept(this));
			}
			return res;
		}

		@Override
		public List visitInclude(IncludeComponent c)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitInclude("+c.getUri()+")");
			SchemaDocument sc = schemas.getSchemaDocumentMap().get(c.getUri());

			urisDefaultNamespace.put(c.getUri(), c.getNs());

			// the included element MUST be a grammar per the spec
			GrammarPattern g = (GrammarPattern)sc.getPattern();

			// a grammar contains only start, define, div, include elements
			Map<String,List<DefineComponent>> grammarDefinitions = g.accept(new GrabDefinesVisitor(schemas, urisDefaultNamespace));
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"grammar definitions: "+grammarDefinitions.keySet());
			// an include contains only start, define, div, elements
			GrabDefinesVisitor v = new GrabDefinesVisitor(schemas, urisDefaultNamespace);
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

		@Override
		public List visitInterleave(InterleavePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitInterleave()");

			List res = new ArrayList<Object>();
			for(Pattern c: p.getChildren())
			{
				res.addAll(c.accept(this));
			}
			return res;
		}

		@Override
		public List visitZeroOrMore(ZeroOrMorePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitZeroOrMore()");
			boolean savedRequired = required;
			required = false;
			List res =  p.getChild().accept(this);
			required = savedRequired;
			return res;
		}


		@Override
		public List visitList(ListPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitList()");
			//TODO: text completion inside this element
			return Collections.emptyList();
		}

		@Override
		public List visitMixed(MixedPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitMixed()");
			//indicates that the element may contain text
			return Collections.emptyList();
		}


		@Override
		public List visitNotAllowed(NotAllowedPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitNotAllowed()");
			// not interesting
			return Collections.emptyList();
		}


		@Override
		public List visitOneOrMore(OneOrMorePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitOneOrMore()");
			return p.getChild().accept(this);
		}

		@Override
		public List visitOptional(OptionalPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitOptional()");
			boolean savedRequired = required;
			required = false;
			List res =  p.getChild().accept(this);
			required = savedRequired;
			return res;
		}

		@Override
		public List visitParentRef(ParentRefPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitParentRef("+p.getName()+")");
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

		/** handle ref and parentRef all the same.
		 * 	The first time that a reference to a define is visited,
		 *    visit its body (so the current parent gets its content),
		 *    save the content (return of the visitXXX methods) in definesContents;
		 *  Otherwise, don't do it.
		 *  Always return an ElementRefDecl anyway.
		 *  */
		private List handleRef(List<DefineComponent> refs){
			List res = new ArrayList();
			for(DefineComponent dc : refs){
				// will return it anyway !
				ElementDecl e = new ElementRefDecl(dc,required);
				res.add(e);

				if(!definesContents.containsKey(dc)){

					//first time we see a reference to this define
					// we will not add the ElementRefDecl to the parent, since it will get the real content
					// via dc.getBody.accept(this)     (see bellow)

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

		@Override
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

		@Override
		public List visitRef(RefPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitRef("+p.getName()+")");
			if(!defines.containsKey(p.getName())){
				throw new IllegalArgumentException("Undefined reference :"+p.getName());
			}
			return handleRef(defines.get(p.getName()));
		}

		@Override
		public List visitText(TextPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitText("+p+")");
			//throwing away : there is no 'mixed' information in the ElementDecl
			return Collections.emptyList();
		}

		@Override
		public List visitValue(ValuePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"visitValue()");
			//TODO : text completion inside an element
			return Collections.emptyList();
		}

	/** replace all ElementRefDecls by their value,
	 *  going through all elements of all CompletionInfo */
	void resolveRefs(){
		if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"resolving references...");
		for(CompletionInfo i : info.values()){
			for(ElementDecl e:i.elements){
				resolveRefs(e);
			}
		}
		for(CompletionInfo i : info.values()){
			for(ElementDecl e:i.elements){
				if(e.elementHash!=null){
					for(ElementDecl d:e.elementHash.values()){
						if(d instanceof ElementRefDecl){
							throw new IllegalStateException("still some undereferenced ElementRefDecl: "+d.name);
						}
					}
				}
			}
		}
	}

	/**
	 * if start elements are define components (like article, book in Docbook 5 grammar),
	 * their content must be added to CompletionInfo.elementHash because they are really top level elements.
	 * and were not visited as such before.
	 * Beware: unbounded recursion here, so defines may not be mutually recursive !
	 * @param	roots	list returned by visitGrammar
	 * */
	void addRootsToCompletionInfos(List roots){
		for(Object o: roots){
			if(o instanceof ElementRefDecl){
				ElementRefDecl e = (ElementRefDecl)o;
				List res = definesContents.get(e.ref);
				if(res == null)throw new IllegalArgumentException("can't find definitions for "+e.ref.getName()+"="+e.ref);
				else {
					for(Object r:res){
						if(r instanceof ElementDecl){
							if(r instanceof ElementRefDecl){
								addRootsToCompletionInfos(Collections.singletonList(r));
							}else{
								ElementDecl oo = (ElementDecl)r;
								if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"adding root "+oo);
								if(oo.name != null){
									oo.completionInfo.elementHash.put(oo.name, oo);
								}
							}
						}
					}
				}
			}
		}

	}

	/** replace all ElementRefDecls children in this ElementDecl by their content,
	 *  recursively dereferencing if necessary
	 *  @see MyPatternVisitor#resolveRefs(List, boolean)
	 */
	private void resolveRefs(ElementDecl e){
		if(e.elementHash != null){
			List res = resolveRefs(new ArrayList(e.elementHash.values()), true);
			e.elementHash.clear();
			e.content.clear();
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
					//requiredness has already been take care of in resolveRefs(List,boolean)
					if(e.attributeHash.containsKey(oo.name)){
						AttributeDecl existing = e.attributeHash.get(oo.name);
						existing.values.addAll(oo.values);
					}else{
						e.addAttribute(oo);
					}
				}else{
					throw new IllegalArgumentException("what's this : "+o);
				}
			}
		}
	}

	/**
	 * recursively resolve references in res
	 * @param	required	are referenced attributes required.
	 * 						If only 1 reference in the chain of references marks attributes as non required (field ElementRefDecl.required),
	 *						resulting attributes will be non required
	 * @return	list of ElementDecl or AttributeDecl
	 */
	private List resolveRefs(List res, boolean required){
		List l = new ArrayList(res.size());
		for(Object o:res){
			if(o instanceof ElementRefDecl){
				ElementRefDecl oref = (ElementRefDecl)o;
				DefineComponent odc = oref.ref;
				List ores = definesContents.get(odc);
				if(ores == null)throw new IllegalArgumentException("can't find definitions for "+odc.getName()+"="+odc);
				// it only takes 1 ref in <optional> to make attributes non required
				l.addAll(resolveRefs(ores,required && oref.required));

			} else if(o instanceof ElementDecl){
				l.add(o);
			}else if(o instanceof AttributeDecl){
				AttributeDecl oo = (AttributeDecl)o;
				if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"adding "+oo);
				// must make a copy with correct requiredness because attribute could be referenced somewhere else
				// where it is required
				if(!required){
					oo = oo.copy();
					oo.required = false;
				}
				l.add(oo);
			}else{
				throw new IllegalArgumentException("what's this : "+o);
			}
		}
		return l;
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

		@Override
		public void merge(ElementDecl other) {
			throw new UnsupportedOperationException("merge define with another ElementDecl");
		}

		public String toString(){
			return "ref to "+ref.getName()+"="+ref.toString();
		}
	}

	}
}
