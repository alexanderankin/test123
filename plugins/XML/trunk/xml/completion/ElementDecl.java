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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.gjt.sp.jedit.MiscUtilities;

public class ElementDecl
{
	public CompletionInfo completionInfo;

	public String name;

	public boolean empty;
	public boolean any;

	public List attributes;
	public Map attributeHash;
	public Set content;

	//{{{ ElementDecl constructor
	public ElementDecl(CompletionInfo completionInfo, String name, String content)
	{
		this.completionInfo = completionInfo;

		this.name = name;

		if(content != null)
			setContent(content);

		attributes = new ArrayList();
		attributeHash = new HashMap();
	} //}}}

	//{{{ ElementDecl constructor
	public ElementDecl(CompletionInfo completionInfo, String name,
		boolean empty, boolean any, List attributes, Map attributeHash,
		Set content)
	{
		this.completionInfo = completionInfo;
		this.name = name;
		this.empty = empty;
		this.any = any;
		this.attributes = attributes;
		this.attributeHash = attributeHash;
		this.content = content;
	} //}}}

	//{{{ setContent() method
	public void setContent(String content)
	{
		if(content.equals("EMPTY"))
			empty = true;
		else if(content.equals("ANY"))
			any = true;
		else
		{
			this.content = new HashSet();

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
			return new ElementDecl(completionInfo,prefix + ':' + name,
				empty,any,attributes,attributeHash,content);
		}
	} //}}}

	//{{{ getChildElements() method
	public List getChildElements(String prefix)
	{
		ArrayList children = new ArrayList(100);

		if(any)
		{
			for(int i = 0; i < completionInfo.elements.size(); i++)
			{
				children.add(((ElementDecl)completionInfo.elements.get(i))
					.withPrefix(prefix));
			}
		}
		else
		{
			for(int i = 0; i < completionInfo.elementsAllowedAnywhere.size(); i++)
			{
				children.add(((ElementDecl)completionInfo
					.elementsAllowedAnywhere.get(i))
					.withPrefix(prefix));
			}

			if(content != null)
			{
				Iterator iter = content.iterator();
				while(iter.hasNext())
				{
					ElementDecl decl = (ElementDecl)completionInfo
						.elementHash.get(iter.next());
					if(decl != null)
						children.add(decl.withPrefix(prefix));
				}
			}
		}

		return children;
	} //}}}

	/**
	 * Finds all elements belonging to a substitution group.
	 *  
	 * @return a list of all elements with matching substitutionGroup.
	 * 
	 */
	public List findReplacements() {
		LinkedList retval = new LinkedList();
		// find all elements whose substitutionGroup = the decl.name
		String subGroupName = name;
		Iterator itr = completionInfo.elements.iterator();
		while (itr.hasNext()) {
			ElementDecl element = (ElementDecl) itr.next();
			AttributeDecl attr = element.getAttribute("substitutionGroup");
			if (attr != null && attr.name.equals(subGroupName)) {
				retval.add(element);
			}
		}
		return retval;
	}
	/**
	 * 
	 * @param elementDecls a list of elements
	 * @return a list of elements, with the abstract ones replaced by their expansions. 
	 */
	public static List expandAbstractElements(List elementDecls) 
	{
		LinkedList retval = new LinkedList();
		Iterator itr = elementDecls.iterator() ;
		while (itr.hasNext()) 
		{
			ElementDecl decl = (ElementDecl) itr.next();
			
			AttributeDecl abstractAttr = decl.getAttribute("abstract"); 
			/* XXX: XSD handling - the attributes of the element should be here, but due
			 * to a bug, they are not. 
			 */
			if (decl.name.endsWith(".class") || 
			    (abstractAttr != null && abstractAttr.value.equals("true"))) 
				retval.addAll( decl.findReplacements());
           	     // else 
			retval.add(decl);
		}
		return retval;
	}
	
	//{{{ getAttribute() method
	public AttributeDecl getAttribute(String name)
	{
		return (AttributeDecl)attributeHash.get(name);
	} //}}}

	//{{{ addAttribute() method
	public void addAttribute(AttributeDecl attribute)
	{
		attributeHash.put(attribute.name,attribute);

		for(int i = 0; i < attributes.size(); i++)
		{
			AttributeDecl attr = (AttributeDecl)attributes.get(i);
			if(attr.name.compareTo(attribute.name) > 0)
			{
				attributes.add(i,attribute);
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
		public String value;
		public ArrayList values;
		public String type;
		public boolean required;

		public AttributeDecl(String name, String value, ArrayList values,
			String type, boolean required)
		{
			this.name = name;
			this.value = value;
			this.values = values;
			this.type = type;
			this.required = required;
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
	public static class Compare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			return MiscUtilities.compareStrings(
				((ElementDecl)obj1).name,
				((ElementDecl)obj2).name,true);
		}
	} //}}}
}
