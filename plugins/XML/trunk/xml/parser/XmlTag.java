/*
 * XmlTag.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
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

//{{{ Imports
import javax.swing.text.Position;
import org.xml.sax.Attributes;
//}}}

public class XmlTag
{
	//{{{ Instance variables
	public String name;
	public Position start, end;
	public Attributes attributes;
	public String attributeString;
	public String idAttributeString;
	public boolean empty;
	//}}}

	//{{{ XmlTag constructor
	public XmlTag(String name, Position start, Attributes attributes)
	{
		this.name = name;
		this.start = start;
		this.attributes = attributes;

		StringBuffer buf = new StringBuffer();

		buf.append(name);

		String idName = null;
		String idValue = null;

		for(int i = 0; i < attributes.getLength(); i++)
		{
			buf.append(' ');

			String aname = attributes.getQName(i);
			String value = attributes.getValue(i);
			buf.append(aname);
			buf.append("=\"");
			buf.append(value);
			buf.append('"');

			if(attributes.getLocalName(i).equalsIgnoreCase("id")
				|| attributes.getType(i).equals("ID"))
			{
				idName = aname;
				idValue = value;
			}
		}

		attributeString = buf.toString();

		if(idName == null)
			idAttributeString = name;
		else
			idAttributeString = name + ' ' + idName + "=\"" + idValue + '"';
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		return attributeString;
	} //}}}
}
