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

	Vector attributes;

	ElementDecl(String name, String model)
	{
		this.name = name;
		this.empty = (model.equals("EMPTY"));

		attributes = new Vector();
	}

	void addAttribute(AttributeDecl attribute)
	{
		attributes.addElement(attribute);
	}

	public String toString()
	{
		return getClass().getName() + "[" + name + (empty ? ",empty" : "")
			+ "," + attributes + "]";
	}

	static class AttributeDecl
	{
		String name;
		String valueDefault;
		String value;

		Vector values;

		AttributeDecl(String name, String type,
			String valueDefault, String value)
		{
			this.name = name;
			this.valueDefault = valueDefault;
			this.value = value;

			if(type != null && type.startsWith("("))
			{
				values = new Vector();

				StringTokenizer st = new StringTokenizer(
					type.substring(0,type.length() - 1),"|");
				while(st.hasMoreTokens())
				{
					values.addElement(st.nextToken());
				}
			}
		}

		public String toString()
		{
			return "[" + name + "," + valueDefault + "," + value
				+ "," + values + "]";
		}
	}
}
