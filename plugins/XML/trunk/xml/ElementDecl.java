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

	ArrayList attributes;

	ElementDecl(String name, boolean empty, boolean html)
	{
		this.name = name;
		this.empty = empty;
		this.html = html;

		attributes = new ArrayList();
	}

	void addAttribute(AttributeDecl attribute)
	{
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
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<element name=\"");
		buf.append(name);
		buf.append('"');

		if(empty)
			buf.append(" empty=\"true\"");

		if(true /*html*/)
			buf.append(" html=\"true\"");

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
	}

	static class AttributeDecl
	{
		String name;
		String value;
		ArrayList values;
		String type;
		boolean required;

		AttributeDecl(String name, String value, ArrayList values,
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
	}
}
