/*
 * XmlTag.java
 * Copyright (C) 2000, 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml.parser;

import javax.swing.text.Position;
import org.xml.sax.Attributes;

public class XmlTag
{
	public String name;
	public Position start, end;
	public Attributes attributes;
	public String attributeString;

	public XmlTag(String name, Position start, Attributes attributes)
	{
		this.name = name;
		this.start = start;
		this.attributes = attributes;

		StringBuffer buf = new StringBuffer();

		buf.append(name);

		for(int i = 0; i < attributes.getLength(); i++)
		{
			buf.append(' ');

			buf.append(attributes.getQName(i));
			buf.append("=\"");
			buf.append(attributes.getValue(i));
			buf.append('"');
		}

		attributeString = buf.toString();
	}

	public String toString()
	{
		return attributeString;
	}
}
