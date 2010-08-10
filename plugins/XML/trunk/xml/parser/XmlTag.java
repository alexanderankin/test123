/*
 * XmlTag.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
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
import javax.swing.Icon;
import org.gjt.sp.jedit.jEdit;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import sidekick.Asset;
import xml.XmlListCellRenderer;
import java.util.Map;
import java.util.HashMap;
//}}}

public class XmlTag extends Asset
{
	//{{{ Instance variables
	public Attributes attributes;
	public String attributeString;
	public String idAttributeString;
	public boolean empty;
	public String namespace;
	/** namespace -> prefix */
	public Map<String,String> namespaceBindings;
	private StringBuilder characters = new StringBuilder();
	//}}}

	//{{{ XmlTag constructor
	public XmlTag(String name,String namespace, Position start, Attributes attributes)
	{
		super(name);
		this.namespace = namespace;
		this.namespaceBindings = new HashMap<String,String>();
		this.start = this.end = start;
		this.attributes = new AttributesImpl(attributes);

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

	//{{{ getIcon() method
	public Icon getIcon()
	{
		return (empty ? XmlListCellRenderer.EMPTY_ELEMENT_ICON
			: XmlListCellRenderer.ELEMENT_ICON);
	} //}}}

	//{{{ getLongString() method
	public String getLongString()
	{
		return attributeString;
	} //}}}

	//{{{ getShortString() method
	public String getShortString()
	{
		int showAttributes = jEdit.getIntegerProperty("xml.show-attributes",1);
		switch(showAttributes)
		{
		case 0:
			return name;
		case 1:
			return idAttributeString;
		case 2:
			return attributeString;
		default:
			return null;
		}
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		return attributeString;
	} //}}}
	
	//{{{ getLocalName() method
	public String getLocalName(){
		return name.contains(":") ? name.substring(name.indexOf(":")+1) : name;
	}
	//}}}
	
	//{{{ getPrefix() method
	public String getPrefix(){
		return name.contains(":") ? name.substring(0,name.indexOf(":")) : "";
	}
	//}}}
	
	//{{{ canAddCharacters() method
	/**
 	 * @return <code>true</code> if this tag should accumulate the characters from 
 	 * the body of a tag.  This default implementation returns <code>false</code>.
 	 * Subclasses may override to return <code>true</code>.
 	 */
	public boolean canAddCharacters(){
		return false;
	}
	//}}}
	
	
	//{{{ addCharacters() method
	/**
	 * If <code>canAddCharacters</code> returns true, characters from the body of
 	 * the tag will be accumulated.
 	 */
	public void addCharacters(char[] chars){
		characters.append(chars);
	}
	//}}}
	
	//{{{ getCharacters() method
	/**
 	 * @return Any characters accumulated from the body of the tag.  Will return
 	 * an empty String if <code>canAddCharacters</code> returns <code>false</code>.
 	 */
	public String getCharacters(){
		return characters.toString();
	}
	//}}}
}
