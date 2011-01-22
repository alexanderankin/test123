/*
 * ElementDecl.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml.completion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.gjt.sp.util.StandardUtilities;

public class ElementDecl
{
	public CompletionInfo completionInfo;

	public String name;

	public boolean empty;
	public boolean any;

	public List<AttributeDecl> attributes;
	public Map<String, AttributeDecl> attributeHash;
	public Set<String> content;
	public Map<String, ElementDecl> elementHash;

	//{{{ ElementDecl constructor
	public ElementDecl(CompletionInfo completionInfo, String name, String content)
	{
		this.completionInfo = completionInfo;

		this.name = name;

		if(content != null)
			setContent(content);

		attributes = new ArrayList<AttributeDecl>();
		attributeHash = new HashMap<String, AttributeDecl>();
	} //}}}

	//{{{ ElementDecl constructor
	private ElementDecl(CompletionInfo completionInfo, String name,
		boolean empty, boolean any, List<AttributeDecl> attributes, Map<String, AttributeDecl> attributeHash,
		Set<String> content)
	{
		this.completionInfo = completionInfo;
		this.name = name;
		this.empty = empty;
		this.any = any;
		this.attributes = attributes;
		this.attributeHash = attributeHash;
		this.content = content;
	} //}}}

	/**
	 * @return true if this is an abstract element, representing a class of
	 * 	other elements (used in w3c xsd)
	 */
	public boolean isAbstract() {
		return false;
	}
	//{{{ setContent() method
	public void setContent(String content)
	{
		if(content.equals("EMPTY"))
			empty = true;
		else if(content.equals("ANY"))
			any = true;
		else
		{
			this.content = new HashSet<String>();

			StringTokenizer st = new StringTokenizer(content,
				"(?*+|,) \t\n");
			while(st.hasMoreTokens())
			{
				String element = st.nextToken();
				if(element.equals("#PCDATA"))
					continue;

				this.content.add(element);
			}
		}
	} //}}}

	//{{{ withPrefix()
	public ElementDecl withPrefix(String prefix)
	{
		if(prefix.equals(""))
			return this;
		else
		{
			ElementDecl d =  new ElementDecl(completionInfo, prefix + ':' + name,
				empty, any, attributes, attributeHash, content);
			d.elementHash = elementHash;
			return d;
		}
	} //}}}

	//{{{ withContext()
	public ElementDecl withContext(Map<String,String> context)
	{
		String ns = completionInfo.namespace;
		String prefix = context.get(ns);
		if(prefix == null || prefix.equals(""))
			return this;
		else
		{
			ElementDecl d =  new ElementDecl(completionInfo, prefix + ':' + name,
				empty, any, attributes, attributeHash, content);
			d.elementHash = elementHash;
			return d;
		}
	} //}}}

	//{{{ getChildElements() method
	public List<ElementDecl> getChildElements(String prefix)
	{
		ArrayList<ElementDecl>children = new ArrayList<ElementDecl>(100);

		if(any)
		{
			for(int i = 0; i < completionInfo.elements.size(); i++)
			{
				children.add(((ElementDecl)completionInfo.elements.get(i)).withPrefix(prefix));
			}
		}
		else
		{
			for(int i = 0; i < completionInfo.elementsAllowedAnywhere.size(); i++)
			{
				children.add(((ElementDecl)completionInfo.elementsAllowedAnywhere.get(i))
					.withPrefix(prefix));
			}

			if(content != null)
			{
				Iterator iter = content.iterator();
				while(iter.hasNext())
				{
					ElementDecl decl = null;
					Object n = (String)iter.next();
					if(elementHash == null){
						//backward compatible
						decl = (ElementDecl)completionInfo
							.elementHash.get(n);
					}else{
						decl = elementHash.get(n);
					}
						
					if(decl != null) {
						if (decl.isAbstract())
							children.addAll(decl.findReplacements(prefix));
						else 
							children.add(decl.withPrefix(prefix));
					}
				}
			}
		}
		
		return children;
	} //}}}

	//{{{ getChildElements() method
	public List<ElementDecl> getChildElements(Map<String,String> namespaceContext)
	{
		ArrayList<ElementDecl>children = new ArrayList<ElementDecl>(100);

		if(any)
		{
			for(int i = 0; i < completionInfo.elements.size(); i++)
			{
				children.add(((ElementDecl)completionInfo.elements.get(i)).withContext(namespaceContext));
			}
		}
		else
		{
			for(int i = 0; i < completionInfo.elementsAllowedAnywhere.size(); i++)
			{
				children.add(((ElementDecl)completionInfo.elementsAllowedAnywhere.get(i))
					.withContext(namespaceContext));
			}

			if(content != null)
			{
				Iterator iter = content.iterator();
				while(iter.hasNext())
				{
					ElementDecl decl = null;
					Object n = (String)iter.next();
					if(elementHash == null){
						//backward compatible
						decl = (ElementDecl)completionInfo
							.elementHash.get(n);
					}else{
						decl = elementHash.get(n);
					}
						
					if(decl != null) {
						if (decl.isAbstract())
							children.addAll(decl.findReplacements(namespaceContext));
						else 
							children.add(decl.withContext(namespaceContext));
					}
				}
			}
		}
		
		return children;
	} //}}}

	/**
	 * Finds all elements which can be replaced by this one. 
	 *  
	 * @return a list of all elements with matching substitutionGroup, or null if there are
	 * none.
	 * 
	 */
	public List<ElementDecl> findReplacements(String prefix) {
		return null;
	}
	
	/**
	 * Finds all elements which can be replaced by this one. 
	 *  
	 * @return a list of all elements with matching substitutionGroup, or null if there are
	 * none.
	 * 
	 */
	public List<ElementDecl> findReplacements(Map<String,String> prefix) {
		return null;
	}
	
	//{{{ getAttribute() method
	public AttributeDecl getAttribute(String attrname)
	{
		return attributeHash.get(attrname);
	} //}}}

	//{{{ addAttribute() method
	public void addAttribute(AttributeDecl attribute)
	{
		attributeHash.put(attribute.name, attribute);

		for(int i = 0; i < attributes.size(); i++)
		{
			AttributeDecl attr = (AttributeDecl)attributes.get(i);
			if(attr.name.compareTo(attribute.name) > 0)
			{
				attributes.add(i, attribute);
				return;
			}
		}

		attributes.add(attribute);
	} //}}}

	//{{{ getRequiredAttributesString()
	public String getRequiredAttributesString()
	{
		StringBuffer buf = new StringBuffer();

		for(int i = 0; i < attributes.size(); i++)
		{
			AttributeDecl attr = (AttributeDecl)attributes.get(i);

			if(attr.required)
			{
				buf.append(' ');
				buf.append(attr.name);
				buf.append("=\"");
				if(attr.value != null)
					buf.append(attr.value);
				buf.append('"');
			}
		}

		return buf.toString();
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<element name=\"");
		buf.append(name);
		buf.append('"');

		buf.append("\ncontent=\"");

		if(empty)
			buf.append("EMPTY");
		else if(content != null)
		{
			buf.append('(');

			Iterator iter = content.iterator();
			while(iter.hasNext())
			{
				buf.append(iter.next());
				if(iter.hasNext())
					buf.append('|');
			}

			buf.append(')');
		}

		buf.append('"');

		if(attributes.size() == 0)
			buf.append(" />");
		else
		{
			buf.append(">\n");
			for(int i = 0; i < attributes.size(); i++)
			{
				buf.append(attributes.get(i));
				buf.append('\n');
			}
			buf.append("</element>");
		}

		return buf.toString();
	} //}}}

	//{{{ AttributeDecl class
	public static class AttributeDecl
	{
		public String name;
		public String namespace;
		public String value;
		public ArrayList values;
		public String type;
		public boolean required;
		
		public AttributeDecl(String name, String namespace, String value, ArrayList<String> values,
			String type, boolean required)
		{
			this.name = name;
			this.namespace = namespace;
			this.value = value;
			this.values = values;
			this.type = type;
			this.required = required;
		}

		public AttributeDecl copy()
		{
			return new AttributeDecl(name,
				namespace,value,
				(values == null ? null : new ArrayList(values)),
				type,
				required);
		}
		
		public String toString()
		{
			StringBuffer buf = new StringBuffer("<attribute name=\"");
			buf.append(name);
			buf.append('"');

			if(value != null)
			{
				buf.append(" value=\"");
				buf.append(value);
				buf.append('"');
			}

			buf.append(" type=\"");
			buf.append(type);
			buf.append('"');

			if(required)
				buf.append(" required=\"true\"");

			buf.append(" />");
			return buf.toString();
		}
	} //}}}

	//{{{ Compare class
	public static class Compare implements java.util.Comparator<ElementDecl>
	{
		public int compare(ElementDecl obj1, ElementDecl obj2)
		{
			return StandardUtilities.compareStrings(obj1.name, obj2.name, true);
		}
	} //}}}
}
