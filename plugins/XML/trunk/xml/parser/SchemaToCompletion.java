package xml.parser;

import java.util.*;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import com.thaiopensource.relaxng.edit.*;
import com.thaiopensource.xml.util.Name;
import com.thaiopensource.resolver.xml.sax.SAX;


import org.gjt.sp.util.Log;

import xml.completion.CompletionInfo;
import xml.completion.ElementDecl;
import static xml.completion.ElementDecl.AttributeDecl;

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
		
		com.thaiopensource.relaxng.input.parse.ParseInputFormat pif;
		
		if(schemaFileNameOrURL.endsWith(".rnc")){
			pif = new com.thaiopensource.relaxng.input.parse.compact.CompactParseInputFormat();
		}else{
			pif = new com.thaiopensource.relaxng.input.parse.sax.SAXParseInputFormat();
		}
		
		try{
			// TODO: Resolver
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
				SAX.createResolver(xml.Resolver.instance(),false));
			
			SchemaDocument mainSchema = schemas.getSchemaDocumentMap().get(schemas.getMainUri());
			
			Pattern p = mainSchema.getPattern();
			
			System.out.println(mainSchema);
			
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
	extends AbstractPatternVisitor< Map<String,DefineComponent> >
	implements ComponentVisitor < Map<String,DefineComponent> >
	{
		private Map<String,DefineComponent> comps = new HashMap<String,DefineComponent>();
		public Map<String,DefineComponent> visitGrammar(GrammarPattern p){
			p.componentsAccept(this);
			return comps;
		}
		
		public Map<String,DefineComponent> visitDefine(DefineComponent c){
			Log.log(Log.DEBUG,GrabDefinesVisitor.class,"visitDefine("+c.getName()+")");
			comps.put(c.getName(),c);
			return comps;
		}
		
		public Map<String,DefineComponent> visitDiv(DivComponent c){
			Log.log(Log.DEBUG,GrabDefinesVisitor.class,"visitDiv()");
			return comps;
		}
		
		public Map<String,DefineComponent> visitInclude(IncludeComponent c){
			Log.log(Log.DEBUG,GrabDefinesVisitor.class,"visitInclude("+c.getHref()+")");
			return comps;
		}
		
		public Map<String,DefineComponent> visitPattern(Pattern p){
			throw new UnsupportedOperationException("visitPattern("+p+")");
		}
	}
	
	private static class MyAttributeVisitor extends AbstractPatternVisitor< List<AttributeDecl> >{
		private Map<String,String> values;
		private List<String> data;
		private Map<String,DefineComponent> defined;
		
		MyAttributeVisitor(Map<String,DefineComponent> defined){
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
			Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitChoice()");
			p.childrenAccept(this);
			return null;
		}
				   
		public List<AttributeDecl> visitElement(ElementPattern p){
			throw new IllegalArgumentException("attribute//element isn't allowed");
		}
				   
		public List<AttributeDecl> visitEmpty(EmptyPattern p){
			Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitEmpty()");
			return null;
		}
				   
		public List<AttributeDecl> visitExternalRef(ExternalRefPattern p){
			throw new IllegalArgumentException("attribute//externalRef isn't allowed");
		}
				   
		public List<AttributeDecl> visitGrammar(GrammarPattern p){
			throw new IllegalArgumentException("attribute//grammar isn't allowed");
		}
				   
		public List<AttributeDecl> visitGroup(GroupPattern p){
			Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitGroup()");
			p.childrenAccept(this);
			return null;
		}
				   
		public List<AttributeDecl> visitInterleave(InterleavePattern p){
			Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitInterleave()");
			p.childrenAccept(this);
			return null;
		}
				   
		public List<AttributeDecl> visitList(ListPattern p){
			Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitList()");
			p.getChild().accept(this);
			return null;
		}
				   
		public List<AttributeDecl> visitMixed(MixedPattern p){
			throw new IllegalArgumentException("attribute//mixed doesn't make sense");
		}
				   
		public List<AttributeDecl> visitNotAllowed(NotAllowedPattern p){
			Log.log(Log.DEBUG,MyAttributeVisitor.class,"voidVisitNotAllowed()");
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
				   
		public List<AttributeDecl> visitRef(RefPattern p){
			Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitRef()");
			if(defined.containsKey(p.getName()))
			{
				defined.get(p.getName()).getBody().accept(this);
				return null;
			}
			else throw new IllegalArgumentException("unknown define : "+p.getName());
		}
				   
		public List<AttributeDecl>	visitText(TextPattern p){
			Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitText()");
			// TODO: not empty
			return null;
		}
				   
		public List<AttributeDecl> visitValue(ValuePattern p){
			Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitValue("+p.getPrefixMap()+")");
			values.put(p.getValue(),p.getType());
			return null;
		}

		public List<AttributeDecl> visitData(DataPattern p){
			Log.log(Log.DEBUG,MyAttributeVisitor.class,"visitData()");
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
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitName("+nc.getNamespaceUri()+","+nc.getPrefix()+":"+nc.getLocalName()+")");
			names.add(new Name(nc.getNamespaceUri(),nc.getLocalName()));
			return names;
		}
		
		public List<Name> visitNsName(NsNameNameClass nc)
		{
			Log.log(Log.DEBUG,MyNameClassVisitor.class,"voidVisitNsName("+nc.getNs()+")");
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
			Log.log(Log.DEBUG,MyNameClassVisitor.class,"visitAnyName()");
			any = true;
			return names;
		}
		
		public List<Name> visitChoice(ChoiceNameClass nc)
		{
			Log.log(Log.DEBUG,MyNameClassVisitor.class,"voidVisitChoiceNameClass()");
			nc.childrenAccept(this);
			return names;
		}
	}
	
	
	private static class MyPatternVisitor extends VoidVisitor
	{
		private Map<String,CompletionInfo> info;
		private SchemaCollection schemas;
		private boolean empty = false;
		private Map<String,DefineComponent> defines;
		private ElementDecl parent;
		
		MyPatternVisitor(Map<String,CompletionInfo> info,SchemaCollection schemas){
			this.info = info;
			this.schemas = schemas;
			parent = null;
		}
		
		
		public void voidVisitAttribute(AttributeAnnotation a)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitAttributeAnnotation()");
		}
		
		public void voidVisitAttribute(AttributePattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitAttribute()");

			MyAttributeVisitor visitor = new MyAttributeVisitor(defines);
			
			for(AttributeDecl attr:p.accept(visitor))
			{
				parent.addAttribute(attr);
			}

		}
		
		
		public void voidVisitChoice(ChoicePattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitChoice()");
			p.childrenAccept(this);
		}
		
		public void voidVisitComment(Comment c)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitComment()");
		}
		
		public void voidVisitComponent(Component c)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitComponent()");
		}
		
		public void voidVisitData(DataPattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitData()");
		}
		
		
		public void voidVisitDefine(DefineComponent c)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitDefine("+c.getName()+","+c.getCombine()+")");
			if(DefineComponent.START.equals(c.getName())){
				c.getBody().accept(this);
			}
		}
		
		public void voidVisitDiv(DivComponent c)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitDiv()");
		}
		
		public void voidVisitElement(ElementAnnotation ea)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitElementAnnotation()");
		}
		
		public void voidVisitElement(ElementPattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitElement()");
			
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
					Log.log(Log.DEBUG,SchemaToCompletion.class,"setting completionInfo for "+myInfo.namespace);
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
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitEmpty()");
			empty=true;
		}
		
		public void voidVisitExternalRef(ExternalRefPattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitExternalRef()");
		}
		
		public void voidVisitGrammar(GrammarPattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitGrammar()");
			defines = p.accept(new GrabDefinesVisitor());
			p.componentsAccept(this);
		}
		
		public void voidVisitGroup(GroupPattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitGroup()");
			p.childrenAccept(this);
		}
		
		public void voidVisitInclude(IncludeComponent c)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitInclude()");
		}
		
		public void voidVisitInterleave(InterleavePattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitInterleave()");
		}
		
		public void voidVisitList(ListPattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitList()");
		}
		
		public void voidVisitMixed(MixedPattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitMixed()");
		}
		
		
		public void voidVisitNameClass(NameClass nc)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitNameClass()");
		}
		
		public void voidVisitNotAllowed(NotAllowedPattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitNotAllowed()");
		}
		
		
		public void voidVisitOneOrMore(OneOrMorePattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitOneOrMore()");
			p.getChild().accept(this);
		}
		
		public void voidVisitOptional(OptionalPattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitOptional()");
			p.getChild().accept(this);
		}
		
		public void voidVisitParentRef(ParentRefPattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitParentRef()");
		}
		
		public void voidVisitPattern(Pattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitPattern()");
		}
		
		public void voidVisitRef(RefPattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitRef("+p.getName()+")");
			// TODO: prevent loops when expanding
			if(!defines.containsKey(p.getName()))
				throw new IllegalArgumentException("Undefined reference :"+p.getName());
			DefineComponent dc = defines.get(p.getName());
			dc.getBody().accept(this);
		}
		
		public void voidVisitText(TextAnnotation ta)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitText()");
		}
		
		public void voidVisitText(TextPattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitText("+p+")");
			
		}
		
		public void voidVisitValue(ValuePattern p)
		{
			Log.log(Log.DEBUG,SchemaToCompletion.class,"voidVisitValue()");
		}
		
	}
}
