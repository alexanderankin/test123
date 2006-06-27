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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gjt.sp.jedit.Buffer;

import sidekick.SideKickParsedData;
import xml.completion.CompletionInfo;
import xml.completion.ElementDecl;
import xml.completion.EntityDecl;
import xml.parser.TagParser;
//}}}

/**
 * Encapsulates the results of parsing a buffer, either using Xerces or the
 * Swing HTML parser.
 */
public class XmlParsedData extends SideKickParsedData
{
	
	public boolean html;
	/**
	 * A mapping of namespace to CompletionInfo objects.
	 *  namespace of "" is the default namespace.
	 */
	private Map mappings;
	
	/**
	 *  A list of all identifiers encountered during the parse?
	 */
	public List ids;

	public void setCompletionInfo(String namespace, CompletionInfo info) {
		mappings.put(namespace, info);
	}
	
	//{{{ XmlParsedData constructor
	public XmlParsedData(String fileName, boolean html)
	{
		super(fileName);
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

		String prefix = getElementNamePrefix(name);
		CompletionInfo info = (CompletionInfo)mappings.get(prefix);

		if(info == null)
			return null;
		else
		{
			String lName;
			int prefixLen = prefix.length();
			if(prefixLen == 0)
				lName = name;
			else
				lName = name.substring(prefixLen + 1);

			ElementDecl decl = (ElementDecl)info.elementHash.get(lName);
			if(decl == null)
				return null;
			else
				return decl.withPrefix(prefix);
		}
	} //}}}


	//{{{ getAllowedElements() method
	public List getAllowedElements(Buffer buffer, int pos)
	{
		List returnValue = new LinkedList();

		TagParser.Tag parentTag = null;
		try {
			parentTag = TagParser.findLastOpenTag(buffer.getText(0,pos),pos,this);
		}
		catch (Exception e) {}
			

		if(parentTag == null)
		{
			// add everything
			Iterator iter = mappings.keySet().iterator();
			while(iter.hasNext())
			{
				String prefix = (String)iter.next();
				CompletionInfo info = (CompletionInfo)
				mappings.get(prefix);
				info.getAllElements(prefix,returnValue);
			}
		}
		else
		{
			String parentPrefix = getElementNamePrefix(parentTag.tag);
			ElementDecl parentDecl = getElementDecl(parentTag.tag);
			if(parentDecl != null)
				returnValue.addAll(parentDecl.getChildElements(parentPrefix));

			// add everything but the parent's prefix now
			Iterator iter = mappings.keySet().iterator();
			while(iter.hasNext())
			{
				String prefix = (String)iter.next();
				if(!prefix.equals(parentPrefix))
				{
					CompletionInfo info = (CompletionInfo)
						mappings.get(prefix);
					info.getAllElements(prefix,returnValue);
				}
			}
		}
		Collections.sort(returnValue,new ElementDecl.Compare());
		return returnValue;
	} //}}}

	//{{{ getAllowedElements() method
	/* called by updateTagList only */
	public List getAllowedElements(Buffer buffer, int startPos, int endPos)
	{
		ArrayList returnValue = new ArrayList();

		// make sure we are not inside a tag
		if(TagParser.isInsideTag(buffer.getText(0,startPos),startPos)) {
			return returnValue;
		}

		// make sure we are not inside a tag
		if(TagParser.isInsideTag(buffer.getText(0,endPos),endPos)) {
			return returnValue;
		}

		TagParser.Tag startParentTag = TagParser.findLastOpenTag(
			buffer.getText(0,startPos),startPos,this);

		TagParser.Tag endParentTag = TagParser.findLastOpenTag(
			buffer.getText(0,endPos),endPos,this);

		if(startParentTag == null) { 
			if(endParentTag == null) {
				// add everything
				Iterator iter = mappings.keySet().iterator();
				while(iter.hasNext())
				{
					String prefix = (String)iter.next();
					CompletionInfo info = (CompletionInfo)
					mappings.get(prefix);
					info.getAllElements(prefix,returnValue);
				}
			}
			else
				return returnValue;
		}
		else if(endParentTag == null) {
			return returnValue;
		}
		else
		{
			String startParentPrefix = getElementNamePrefix(startParentTag.tag);
			ElementDecl startParentDecl = getElementDecl(startParentTag.tag);

			String endParentPrefix = getElementNamePrefix(endParentTag.tag);
			ElementDecl endParentDecl = getElementDecl(endParentTag.tag);

			if(startParentDecl == null)
				return returnValue;
			else if(endParentDecl == null)
				return returnValue;
			else if(!startParentPrefix.equals(endParentPrefix))
				return returnValue;
			else
			{

				if(startParentDecl != null)
					returnValue.addAll(startParentDecl.getChildElements(startParentPrefix));

				// add everything but the parent's prefix now
				Iterator iter = mappings.keySet().iterator();
				while(iter.hasNext())
				{
					String prefix = (String)iter.next();
					if(!prefix.equals(startParentPrefix))
					{
						CompletionInfo info = (CompletionInfo)
							mappings.get(prefix);
						info.getAllElements(prefix,returnValue);
					}
				}
			}
		}

		Collections.sort(returnValue,new ElementDecl.Compare());
		return returnValue;
	} //}}}

	//{{{ Private members

	//{{{ getElementPrefix() method
	private static String getElementNamePrefix(String name)
	{
		int index = name.indexOf(':');
		if(index == -1)
			return "";
		else
			return name.substring(0,index);
	} //}}}

	//}}}
}
