/*
 * XmlParsedData.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

//{{{ Imports
import javax.swing.tree.TreeModel;
import java.util.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import xml.completion.*;
import xml.parser.*;
//}}}

/**
 * Encapsulates the results of parsing a buffer, either using Xerces or the
 * Swing HTML parser.
 */
public class XmlParsedData
{
	public boolean html;
	public Map mappings;
	public List ids;
	public TreeModel tree;

	//{{{ XmlParsedData constructor
	public XmlParsedData(boolean html)
	{
		this.html = html;
		mappings = new HashMap();
		ids = new ArrayList();
	} //}}}

	//{{{ getNoNamespaceCompletionInfo() method
	public CompletionInfo getNoNamespaceCompletionInfo()
	{
		CompletionInfo info = (CompletionInfo)mappings.get("");
		if(info == null)
		{
			info = new CompletionInfo();
			mappings.put("",info);
		}

		return info;
	} //}}}

	//{{{ getElementDecl() method
	public ElementDecl getElementDecl(String name)
	{
		if(html)
			name = name.toLowerCase();

		CompletionInfo info = (CompletionInfo)mappings.get(
			getElementNamePrefix(name));

		if(info == null)
			return null;
		else
			return (ElementDecl)info.elementHash.get(name);
	} //}}}

	//{{{ getAllowedElements() method
	public List getAllowedElements(Buffer buffer, int pos)
	{
		TagParser.Tag parentTag = TagParser.findLastOpenTag(
			buffer.getText(0,pos),pos,this);

		ArrayList returnValue = new ArrayList();

		if(parentTag == null)
		{
			// everything
		}
		else
		{
			String parentPrefix = getElementNamePrefix(parentTag.tag);
			ElementDecl parentDecl = getElementDecl(parentTag.tag);
			if(parentDecl != null)
				returnValue.addAll(parentDecl.getChildElements());

			// add everything but the parent's prefix now
			Iterator iter = mappings.keySet().iterator();
			while(iter.hasNext())
			{
				String prefix = (String)iter.next();
				if(!prefix.equals(parentPrefix))
				{
					CompletionInfo info = (CompletionInfo)
						mappings.get(prefix);
					info.elements.addAll(returnValue);
				}
			}
		}

		return returnValue;
	} //}}}

	//{{{ getParsedData() method
	public static XmlParsedData getParsedData(EditPane editPane)
	{
		return (XmlParsedData)editPane.getClientProperty(
			XmlPlugin.PARSED_DATA_PROPERTY);
	} //}}}

	//{{{ getElementPrefix() method
	public static String getElementNamePrefix(String name)
	{
		int index = name.indexOf(':');
		if(index == -1)
			return "";
		else
			return name.substring(0,index);
	} //}}}
}
