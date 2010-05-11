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

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import com.thaiopensource.relaxng.edit.*;
import com.thaiopensource.xml.util.Name;
import com.thaiopensource.resolver.xml.sax.SAX;
import com.thaiopensource.util.VoidValue;


import org.gjt.sp.util.Log;

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
	*/
	public static Map<String,CompletionInfo> rngSchemaToCompletionInfo(String current, String schemaFileNameOrURL, ErrorHandler handler){
		Map<String,CompletionInfo> infos = new HashMap<String,CompletionInfo>();

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
			
			p.accept(new MyPatternVisitor(infos,schemas));
			
			// use a more intuitive '' key for the completion info
			// of the no-namespace elements
			if(infos.containsKey(INHERIT)){
				infos.put("",infos.get(INHERIT));
				infos.remove(INHERIT);
			}
		}catch(Exception e){
			// FIXME: handle exceptions
			Log.log(Log.ERROR, SchemaToCompletion.class, e);
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
		
		MyAttributeVisitor(StackableMap<String,List<DefineComponent>> defined){
			this.defined=defined;
		}
		
		public List<AttributeDecl> visitAttribute(AttributePattern p){
			if(values!=null)throw new IllegalArgumentException("attribute//attribute isn't allowed");
			values = new HashMap<String,String>();
			data = new ArrayList<String>();
			
			
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
				// TODO: namespaces in attributes ?
				attrs.add(new AttributeDecl(name.getLocalName(),
					value,
					new ArrayList<String>(values.keySet()),
					type,
					true));//always required
			}
			return attrs;
		}
				   
		public List<AttributeDecl> visitChoice(ChoicePattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitChoice()");
			p.childrenAccept(this);
			return null;
		}
				   
		public List<AttributeDecl> visitElement(ElementPattern p){
			throw new IllegalArgumentException("attribute//element isn't allowed");
		}
				   
		public List<AttributeDecl> visitEmpty(EmptyPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitEmpty()");
			return null;
		}
				   
		public List<AttributeDecl> visitExternalRef(ExternalRefPattern p){
			throw new IllegalArgumentException("attribute//externalRef isn't allowed");
		}
				   
		public List<AttributeDecl> visitGrammar(GrammarPattern p){
			throw new IllegalArgumentException("attribute//grammar isn't allowed");
		}
				   
		public List<AttributeDecl> visitGroup(GroupPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitGroup()");
			p.childrenAccept(this);
			return null;
		}
				   
		public List<AttributeDecl> visitInterleave(InterleavePattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitInterleave()");
			p.childrenAccept(this);
			return null;
		}
				   
		public List<AttributeDecl> visitList(ListPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitList()");
			p.getChild().accept(this);
			return null;
		}
				   
		public List<AttributeDecl> visitMixed(MixedPattern p){
			throw new IllegalArgumentException("attribute//mixed doesn't make sense");
		}
				   
		public List<AttributeDecl> visitNotAllowed(NotAllowedPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitNotAllowed()");
			return null;
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
		 *  FIXME: endless loop here, since references are allowed to be recursive.
		 */
		public List<AttributeDecl> visitRef(RefPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitRef()");
			if(defined.containsKey(p.getName()))
			{
				for(DefineComponent dc: defined.get(p.getName())){
					dc.getBody().accept(this);
				}
				return null;
			}
			else throw new IllegalArgumentException("unknown define : "+p.getName());
		}
				   
		public List<AttributeDecl>	visitText(TextPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitText()");
			// TODO: not empty
			return null;
		}
				   
		public List<AttributeDecl> visitValue(ValuePattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitValue("+p.getPrefixMap()+")");
			values.put(p.getValue(),p.getType());
			return null;
		}

		public List<AttributeDecl> visitData(DataPattern p){
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitData()");
			data.add(p.getType());
			return null;
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
	
	private static class MyPatternVisitor extends VoidVisitor
	{
		private Map<String,CompletionInfo> info;
		private SchemaCollection schemas;
		private boolean empty = false;
		private StackableMap<String,List<DefineComponent>> defines;
		private ElementDecl parent;
		
		MyPatternVisitor(Map<String,CompletionInfo> info,SchemaCollection schemas){
			this.info = info;
			this.schemas = schemas;
			parent = null;
			defines = new StackableMap<String,List<DefineComponent>>(2);
		}
		
		
		public void voidVisitAttribute(AttributeAnnotation a)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitAttributeAnnotation()");
		}
		
		public void voidVisitAttribute(AttributePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitAttribute()");

			MyAttributeVisitor visitor = new MyAttributeVisitor(defines);
			
			for(AttributeDecl attr:p.accept(visitor))
			{
				parent.addAttribute(attr);
			}

		}
		
		
		public void voidVisitChoice(ChoicePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitChoice()");
			p.childrenAccept(this);
		}
		
		public void voidVisitComment(Comment c)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitComment()");
		}
		
		public void voidVisitComponent(Component c)
		{
			throw new UnsupportedOperationException(" voidVisitComponent() should not be called");
		}
		
		public void voidVisitData(DataPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitData()");
			// TODO: display the data type of an element
		}
		
		
		public void voidVisitDefine(DefineComponent c)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitDefine("+c.getName()+","+c.getCombine()+")");
		}
		
		public void voidVisitDiv(DivComponent c)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitDiv()");
			// may be used to add documentation to a group of elements.
			c.componentsAccept(this);
		}
		
		public void voidVisitElement(ElementAnnotation ea)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitElementAnnotation()");
		}
		
		public void voidVisitElement(ElementPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitElement()");
			
			ElementDecl myParent = parent;
			empty = false;
			
			MyNameClassVisitor nameVisitor = new MyNameClassVisitor();
			List<Name> myNames = p.getNameClass().accept(nameVisitor);
			
			boolean isEmpty = empty;
			
			if(parent != null && parent.content == null)
				parent.content = new HashSet<String>();
			
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
					myInfo.addElement(me);
					parent = me;
					p.getChild().accept(this);
					parent = myParent;
				}
				if(myParent!=null)
				{
					// TODO: localName??? this is a show stopper for the equivalent of
					// test_data/multiple_name
					myParent.content.add(name.getLocalName());
					//not the same namespace and not yet added
					// so an element with local name x in namespace ns1
					// will take precedence over x in namespace ns2 if
					// we are considering ns1
					if(myParent.completionInfo != myInfo
						&& !myParent.completionInfo.elementHash.containsKey(name.getLocalName())){
						myParent.completionInfo.addElement(me);
					}
				}
			}
			if(myParent!=null){
				myParent.any   = nameVisitor.any;
				myParent.empty = isEmpty;
			}
		}
		
		public void voidVisitEmpty(EmptyPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitEmpty()");
			empty=true;
		}

		public void voidVisitExternalRef(ExternalRefPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitExternalRef("+p.getUri()+")");
			// "The externalRef matches if the pattern contained in the specified URL matches" [RNG tutorial]
			SchemaDocument sc = schemas.getSchemaDocumentMap().get(p.getUri());
			// no risk of endless loop since externalRefs are not allowed to be recursive
			sc.getPattern().accept(this);
		}
		
		public void voidVisitGrammar(GrammarPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitGrammar()");
			defines.push(p.accept(new GrabDefinesVisitor()));
			//for the include
			p.componentsAccept(this);
			
			// explore the tree from <start>
			if(defines.containsKey(DefineComponent.START)){
				Log.log(Log.ERROR,MyPatternVisitor.class,"THERE IS A START");
				for(DefineComponent dc: defines.get(DefineComponent.START)){
					dc.getBody().accept(this);
				}
			}else{
				throw new IllegalArgumentException("Grammar without a start element !");
			}
			defines.pop();
		}
		
		public void voidVisitGroup(GroupPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitGroup()");
			p.childrenAccept(this);
		}
		
		public void voidVisitInclude(IncludeComponent c)
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
				for(DefineComponent dc: defines.get(DefineComponent.START)){
					dc.getBody().accept(this);
				}
			}else{
				throw new UnsupportedOperationException("included grammar without a start element !");
			}
		}
		
		public void voidVisitInterleave(InterleavePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitInterleave()");
			p.childrenAccept(this);
		}
		
		public void voidVisitZeroOrMore(ZeroOrMorePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitZeroOrMore()");
			// TODO: dont' need this until jing issue is fixed
			// see http://code.google.com/p/jing-trang/issues/detail?id=102
			//p.getChild().accept(this);
		}

		
		public void voidVisitList(ListPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitList()");
			//TODO: text completion inside this element
		}
		
		public void voidVisitMixed(MixedPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitMixed()");
			//indicates that the element may contain text
		}
		
		
		public void voidVisitNameClass(NameClass nc)
		{
			throw new UnsupportedOperationException("voidVisitNameClass() shouldn't be called");
			
		}
		
		public void voidVisitNotAllowed(NotAllowedPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitNotAllowed()");
			// not interesting
		}
		
		
		public void voidVisitOneOrMore(OneOrMorePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitOneOrMore()");
			p.getChild().accept(this);
		}
		
		public void voidVisitOptional(OptionalPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitOptional()");
			p.getChild().accept(this);
		}
		
		public void voidVisitParentRef(ParentRefPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitParentRef("+p.getName()+")");
			if(!defines.parentContainsKey(p.getName()))
				throw new IllegalArgumentException("Undefined reference :"+p.getName());
			// FIXME: loops ?
			List<DefineComponent> parentRef = defines.getFromParent(p.getName());
			for(DefineComponent dc : parentRef){
				dc.getBody().accept(this);
			}
		}
		
		public void voidVisitPattern(Pattern p)
		{
			if(p instanceof ZeroOrMorePattern)
			{
				voidVisitZeroOrMore((ZeroOrMorePattern)p);
			}
			else
			{
				// we visit everything, so it shouldn't be called
				throw new UnsupportedOperationException("visitPattern("+p+")");
			}
		}
		
		public void voidVisitRef(RefPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitRef("+p.getName()+")");
			if(!defines.containsKey(p.getName()))
				throw new IllegalArgumentException("Undefined reference :"+p.getName());
			// FIXME: prevent loops when expanding
			for(DefineComponent dc : defines.get(p.getName())){
				//we do not differentiate between choice and interleave
				dc.getBody().accept(this);
			}
		}
		
		public void voidVisitText(TextAnnotation ta)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitText(Annotation)");
		}	
		
		public void voidVisitText(TextPattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitText("+p+")");
			//throwing away : there is no 'mixed' information in the ElementDecl
		}
		
		public void voidVisitValue(ValuePattern p)
		{
			if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,MyPatternVisitor.class,"voidVisitValue()");
			//TODO : text completion inside an element 
		}
		
	}
}
