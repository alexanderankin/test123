/*
 * XSDSchemaToCompletion.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010-2011 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.parser;

// {{{ imports
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.io.IOException;

import javax.xml.XMLConstants;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSNamespaceItemList;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.Buffer;

import xml.completion.CompletionInfo;
import xml.completion.ElementDecl;
import static xml.Debug.*;
import xml.Resolver;
import xml.cache.Cache;
import xml.cache.CacheEntry;

// }}}

// {{{ class XSDSchemaToCompletion
/**
 * turns a Xerces XSD object model into CompletionInfo 
 *
 * @author kerik-sf
 * @version $Id$
 */
public class XSDSchemaToCompletion{
	
	//{{{ xsElementToElementDecl() method
	private static void xsElementToElementDecl(XSNamedMap elements, Map<String,CompletionInfo> infos,
		XSElementDeclaration element, ElementDecl parent, Map<XSComplexTypeDefinition, List<ElementDecl>> seenComplexTypes)
	{
		if(parent != null && parent.content == null)
			parent.content = new HashSet<String>();
		if(parent != null && parent.elementHash == null)
			parent.elementHash = new HashMap<String,ElementDecl>();

		String name = element.getName();
		String namespace = element.getNamespace();
		if(namespace == null)namespace = "";
		
		if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,XSDSchemaToCompletion.class,"xsElementToElementDecl("+namespace+":"+name+")");
		
		CompletionInfo info;
		if(infos.containsKey(namespace)){
			info = infos.get(namespace);
		}else{
			info = new CompletionInfo();
			info.namespace = namespace;
			infos.put(namespace, info);
		}
		
		if(info.elementHash.containsKey(name))
		{
			// one must add the element to its parent's content, even if
			// one knows the element already
			if(parent!=null)
			{
				parent.content.add(name);
				parent.elementHash.put(name,info.elementHash.get(name));
			}
			return;
		}

		ElementDecl elementDecl = null;


		if ( element.getAbstract()
			/* I don't understand this condition.
		       As far as I understand, every top level element can be
			   head of a substitution group.
			   An algorithm showing quadratic performance :
			   		for each element e as argument to this method,
					  for each element f in elements
					    verify the substitution group of f and if e is the head, add f to parent
			   TODO: write an example, fix the code
		       || element.getName().endsWith(".class") */
		   )
		{
			if( parent != null ) {
				for (int j=0; j<elements.getLength(); ++j) {
					XSElementDeclaration decl = (XSElementDeclaration)elements.item(j);
					XSElementDeclaration group = decl.getSubstitutionGroupAffiliation();
					if (group != null && group.getName().equals(name)) {
						// allows to handle elements which are themselves abstract
						// see otherComment in abstract_substitution/comments.xsd
						xsElementToElementDecl(elements, infos, decl, parent, seenComplexTypes); 
					}
				}
			}
			/* we shouldn't care about the type of an abstract element,
			   as it's not allowed in a document. Would it be the case,
			   one should not forget to fix the NullPointerException on elementDecl
			   that will arise when setting the attributes. Maybe use the type declaration
			   for every element...*/
			return;
		}
		else {
			elementDecl = new ElementDecl(info, name, null);

			/* prevent infinite recursion caused by complex types referencing themselves
			    following code is awkward :
			    - if element is of complex type and this complex type is being
			      expandend (ie is in seenComplexTypes)
			       - if it's used with the same name:
			          - reuse the ElementDecl,
			          - add the ElementDecl to parent
			          - return
			       - if it's used with a different name
			          - copy its content into the new ElementDecl
			          - add the new ElementDecl to parent and CompletionInfo as
			            in the normal case
			          - don't expand the complextype, since it's been already seen
			    - otherwise add to parent and CompletionInfo, expand
			*/
			
			XSTypeDefinition typedef = element.getTypeDefinition();
			XSComplexTypeDefinition complex = null;
			
			boolean needToCalcContent = true;
			
			// {{{ prevent infinite recursion caused by complex types referencing themselves
			if(typedef.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE)
			{
				complex = (XSComplexTypeDefinition)typedef;
				
				if(seenComplexTypes.containsKey(complex)){

					ElementDecl same = null;
					for(ElementDecl decl:seenComplexTypes.get(complex)){
						if(decl.completionInfo == info
							&& decl.name.equals(name))
						{
							same = decl;
							break;
						}
					}
					if(same == null){
						// same complextype but different names
						ElementDecl sameContent = seenComplexTypes.get(complex).get(0);
						elementDecl.attributes = sameContent.attributes;
						elementDecl.attributeHash = sameContent.attributeHash;
						elementDecl.content = sameContent.content;
						elementDecl.elementHash = sameContent.elementHash;
						// don't need to expand, but need to be add it to parent and CompletionInfo
						needToCalcContent = false;
					} else {
						// really same element
						if (parent != null) {
							parent.elementHash.put(name,same);
							parent.content.add(name);
						}
						// it's already in CompletionInfo
						return;
					}
				}
			}// }}}
			
			// {{{ add new ElementDecl to parent and CompletionInfo

			// don't let locally defined elements take precedence other global elements
			// see test_data/multiple_name
			if(element.getScope() == XSConstants.SCOPE_LOCAL)
			{ // FIXME: why not use a second elementHash (containing local elements) instead of looping through elements ?
			  // yes, it's another data structure, but it would speed this up !
				if(!info.nameConflict){
					for(ElementDecl e:info.elements){
						if(e.name.equals(name)){
							info.nameConflict = true;
							//Log.log(Log.DEBUG,XSDSchemaToCompletion.class,
							//	"conflict in "+namespace+" between 2 "+name);
						}
					}
				}
				info.elements.add(elementDecl);
			}
			else
			{
				info.addElement(elementDecl);
			}
			
			if (parent != null) {
				parent.elementHash.put(name,elementDecl);
				parent.content.add(name);
			}
			//}}}
			
			// {{{ Expand complex type
			if(complex != null && needToCalcContent)
			{
				// push seen complex type
				List<ElementDecl> l = new ArrayList<ElementDecl>();
				l.add(elementDecl);
				seenComplexTypes.put(complex,l);
				
				XSParticle particle = complex.getParticle();
				if(particle != null)
				{
					XSTerm particleTerm = particle.getTerm();
					if(particleTerm instanceof XSWildcard)
						elementDecl.any = true;
					else
						xsTermToElementDecl(elements, infos,particleTerm,elementDecl,seenComplexTypes);
				}
				
				// FIXME: not removing from seenComplexTypes gives a smaller CompletionInfo
				// but is it correct ?

				XSObjectList attributes = complex.getAttributeUses();
				for(int i = 0; i < attributes.getLength(); i++)
				{
					XSAttributeUse attr = (XSAttributeUse)
						attributes.item(i);
						xsAttributeToElementDecl(elementDecl,attr.getAttrDeclaration(),attr.getRequired());
				}
			} //}}}

		} 
		
		
	} //}}}

	private static void xsAttributeToElementDecl(ElementDecl elementDecl,XSAttributeDeclaration decl, boolean required){
				String attrName = decl.getName();
				String attrNamespace = decl.getNamespace();
				String value = decl.getConstraintValue();
				XSSimpleTypeDefinition typeDef = decl.getTypeDefinition();
				String type = typeDef.getName();
				StringList valueStringList = typeDef.getLexicalEnumeration();
				ArrayList<String> values = new ArrayList<String>();
				for (int j = 0; j < valueStringList.getLength(); j++) {
				    values.add(valueStringList.item(j));
				}

				if(type == null)
					type = "CDATA";
				elementDecl.addAttribute(new ElementDecl.AttributeDecl(
					attrName,attrNamespace,value,values,type,required));
	}
	
	//{{{ xsTermToElementDecl() method
	private static void xsTermToElementDecl(XSNamedMap elements, Map<String,CompletionInfo> infos, XSTerm term,
		ElementDecl parent, Map<XSComplexTypeDefinition, List<ElementDecl>> seenComplexTypes)
	{

		if(term instanceof XSElementDeclaration)
		{
			xsElementToElementDecl(elements, infos,
				(XSElementDeclaration)term, parent, seenComplexTypes);
		}
		else if(term instanceof XSModelGroup)
		{
			XSObjectList content = ((XSModelGroup)term).getParticles();
			for(int i = 0; i < content.getLength(); i++)
			{
				XSTerm childTerm = ((XSParticleDecl)content.item(i)).getTerm();
				xsTermToElementDecl(elements, infos,childTerm,parent, seenComplexTypes);
			}
		}
	}
	//}}}
	
	//{{{ modelToCompletionInfo() method
	public static Map<String,CompletionInfo> modelToCompletionInfo(XSModel model)
	{

		if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,XSDSchemaToCompletion.class,"modelToCompletionInfo("+model+")");
		Map<String,CompletionInfo> infos = new HashMap<String,CompletionInfo>();

		XSNamedMap elements = model.getComponents(XSConstants.ELEMENT_DECLARATION);
		Map<XSComplexTypeDefinition, List<ElementDecl>> seenComplexTypes = new HashMap<XSComplexTypeDefinition, List<ElementDecl>>();
		for(int i = 0; i < elements.getLength(); i++)
		{
			XSElementDeclaration element = (XSElementDeclaration)
				elements.item(i);

			xsElementToElementDecl(elements, infos, element, null, seenComplexTypes);
		}

		/* // don't need them : they are declared for each element
		   // tested with xml:base in user-guide.xml 
		/*XSNamedMap attributes = model.getComponents(XSConstants.ATTRIBUTE_DECLARATION);
		for(int i = 0; i < attributes.getLength(); i++)
		{
			XSAttributeDeclaration attribute = (XSAttributeDeclaration)attributes.item(i);
			//indeed, it's possible (like for XMLSchema-instance),
			//when one uses getModelForNamespace("http://www.w3.org/2001/XMLSchema-instance")
			//or http://www.w3.org/XML/1998/namespace : see network.xml : base, lang, space
			System.err.println("look! " + attribute.getName());
			for(CompletionInfo info : infos.values()){
				for(ElementDecl e:info.elements){
					xsAttributeToElementDecl(e,attribute,false);
				}
			}
		}*/

		return infos;
	} //}}}

	// {{{ getCompletionInfoFromSchema() method
	/**
	 * parse a schema and return CompletionInfos for all its target namespaces
	 * @param	location			location of the schema to parse
	 * @param	schemaLocation		namespace-location pairs found in xsi:schemaLocation attribute. Used to resolve imported schema
	 * @param	nonsSchemaLocation	location found in xsi:noNamespaceSchemaLocation attribute. Used to resolve imported schema
	 * @param	errorHandler		to report errors while parsing the schema
	 * @param	buffer				requesting buffer, for caching
	 */
	public static Map<String,CompletionInfo> getCompletionInfoFromSchema(String location, String schemaLocation, String nonsSchemaLocation, ErrorHandler errorHandler, Buffer buffer)
	throws IOException, SAXException{
		if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,XSDSchemaToCompletion.class,"getCompletionInfoFromSchema("+location+","+schemaLocation+","+nonsSchemaLocation+","+buffer.getPath()+")");
		String realLocation = Resolver.instance().resolveEntityToPath(
				null,//name
				null,//public Id
				buffer.getPath(),//current
				location// systemId
				);
		if(realLocation==null){
			// resolved location really shouldn't be null
			throw new IOException("unable to resolve grammar location for : "+location);
		}else{
			CacheEntry entry = Cache.instance().get(realLocation,XercesParserImpl.COMPLETION_INFO_CACHE_ENTRY);
			if(entry == null){
				entry = Cache.instance().get(realLocation,XMLConstants.W3C_XML_SCHEMA_NS_URI);
				org.apache.xerces.xni.grammars.Grammar grammar = null;
				if(entry == null){
					if(DEBUG_CACHE)Log.log(Log.DEBUG,XSDSchemaToCompletion.class,"no Grammar from cache for "+location);
					grammar = SchemaLoader.instance().loadXercesGrammar(buffer, location, schemaLocation, nonsSchemaLocation, errorHandler);
				}else{
					if(DEBUG_CACHE)Log.log(Log.DEBUG,XSDSchemaToCompletion.class,"got Grammar from cache for "+location);
					grammar = (org.apache.xerces.xni.grammars.Grammar)entry.getCachedItem();
				}
				
				if(grammar == null){
					throw new SAXException("couldn't load grammar "+location+" from "+realLocation);
				}else{
					
					XSModel model = ((org.apache.xerces.xni.grammars.XSGrammar)grammar).toXSModel();
					
					XSNamespaceItemList namespaces = model.getNamespaceItems();
					
					List<CacheEntry> related = new ArrayList<CacheEntry>(namespaces.getLength());
					
					for(int i=0;i<namespaces.getLength();i++){
						XSNamespaceItem namespace = namespaces.item(i);
						if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,XSDSchemaToCompletion.class,"grammar is composed of "
							+namespace.getSchemaNamespace());
						StringList l = namespace.getDocumentLocations();
						for(int j=0;j<l.getLength();j++){
							String loc = l.item(j);
							if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,XSDSchemaToCompletion.class," @"+loc);
							try{
								String realLoc = Resolver.instance().resolveEntityToPath(null, null, buffer.getPath(), l.item(j));
								CacheEntry ce = Cache.instance().put(realLoc,"GrammarComponent","Dummy");
								related.add(ce);
							}catch(IOException ioe){
								Log.log(Log.ERROR, XSDSchemaToCompletion.class, "error resolving path for "+loc, ioe);
							}
						}
					}
					
					Map<String,CompletionInfo> infos = XSDSchemaToCompletion.modelToCompletionInfo(model);
					
					related.add(Cache.instance().put(realLocation,XercesParserImpl.COMPLETION_INFO_CACHE_ENTRY,infos));
					
					// mark all components related and requested by the buffer
					for(CacheEntry ce : related){
						ce.getRelated().addAll(related);
						ce.getRelated().remove(ce);
						ce.getRequestingBuffers().add(buffer);
					}
					
					return infos;
				}
			}else{
				if(DEBUG_CACHE)Log.log(Log.DEBUG,XSDSchemaToCompletion.class,"got CompletionInfo from cache for "+location);
				entry.addRequestingBuffer(buffer);
				Map<String,CompletionInfo> infos = (Map<String,CompletionInfo>)entry.getCachedItem();
				return infos;
			}
		}
	} //}}}
}
