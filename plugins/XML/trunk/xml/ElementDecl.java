/*
 * ElementDecl.java
 * Copyright (C) 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

import java.util.*;

class ElementDecl
{
	String name;
	boolean empty;
	boolean html;

	Vector attributes;

	ElementDecl(String name, boolean empty, boolean html)
	{
		this.name = name;
		this.empty = empty;
		this.html = html;

		attributes = new Vector();
	}

	void addAttribute(AttributeDecl attribute)
	{
		for(int i = 0; i < attributes.size(); i++)
		{
			AttributeDecl attr = (AttributeDecl)attributes.elementAt(i);
			if(attr.name.compareTo(attribute.name) > 0)
			{
				attributes.insertElementAt(attribute,i);
				return;
			}
		}

		attributes.addElement(attribute);
	}

	public String toString()
	{
		return getClass().getName() + "[" + name + (empty ? ",empty" : "")
			+ "," + attributes + "]";
	}

	static class AttributeDecl
	{
		static int CDATA = 0;
		static int CHOICE = 1;
		static int IDREF = 2;

		String name;
		String value;
		Vector values;
		int type;
		boolean required;

		AttributeDecl(String name, String value, Vector values,
			int type, boolean required)
		{
			this.name = name;
			this.value = value;
			this.values = values;
			this.type = type;
			this.required = required;
		}

		public String toString()
		{
			return "[" + name + "=" + value
				+ "," + values + (required ? ",required" : "" )
				+ "]";
		}
	}
}
