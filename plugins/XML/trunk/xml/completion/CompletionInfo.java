/*
 * CompletionInfo.java
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

//{{{ Imports
import gnu.regexp.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import xml.parser.*;
import xml.*;
//}}}

public class CompletionInfo
{
	// if true, HTML syntax is supported (eg, case-insensitive tag names,
	// attributes with no values)
	public boolean html;

	public ArrayList elements;
	public HashMap elementHash;
	public ArrayList entities;
	public HashMap entityHash;
	public ArrayList elementsAllowedAnywhere;

	//{{{ CompletionInfo constructor
	public CompletionInfo()
	{
		this(false,new ArrayList(), new HashMap(),
			new ArrayList(), new HashMap(),
			new ArrayList());
	} //}}}

	//{{{ CompletionInfo constructor
	public CompletionInfo(boolean html, ArrayList elements,
		HashMap elementHash, ArrayList entities, HashMap entityHash,
		ArrayList elementsAllowedAnywhere)
	{
		this.html = html;
		this.elements = elements;
		this.elementHash = elementHash;
		this.entities = entities;
		this.entityHash = entityHash;
		this.elementsAllowedAnywhere = elementsAllowedAnywhere;
	} //}}}

	//{{{ getAllowedElements() method
	public ArrayList getAllowedElements(Buffer buffer, int pos)
	{
		TagParser.Tag parentTag = TagParser.findLastOpenTag(
			buffer.getText(0,pos),pos,elementHash);

		ArrayList returnValue;

		if(parentTag == null)
			returnValue = elements;
		else
		{
			ElementDecl parentDecl = (ElementDecl)elementHash.get(
				parentTag.tag);
			if(parentDecl == null)
				returnValue = new ArrayList();
			else
			{
				returnValue = parentDecl.getChildElements(this);
			}
		}

		return returnValue;
	} //}}}

	//{{{ getCompletionInfo() method
	public static CompletionInfo getCompletionInfo(EditPane editPane)
	{
		Buffer buffer = editPane.getBuffer();

		String mode;

		Iterator iter = globs.keySet().iterator();
		while(iter.hasNext())
		{
			RE re = (RE)iter.next();
			if(re.isMatch(buffer.getName()))
				return getCompletionInfo((String)globs.get(re));
		}

		String resource = jEdit.getProperty("mode."
			+ buffer.getMode().getName()
			+ "." + XmlPlugin.COMPLETION_INFO_PROPERTY);
		if(resource != null)
		{
			CompletionInfo info = getCompletionInfo(resource);

			if(info != null)
				return info;
		}

		return (CompletionInfo)editPane.getClientProperty(
			XmlPlugin.COMPLETION_INFO_PROPERTY);
	} //}}}

	//{{{ getCompletionInfo() method
	public static CompletionInfo getCompletionInfo(String resource)
	{
		CompletionInfo info = (CompletionInfo)completionInfo.get(resource);
		if(info != null)
			return info;

		Log.log(Log.DEBUG,CompletionInfo.class,"Loading " + resource);

		CompletionInfoHandler handler = new CompletionInfoHandler();

		try
		{
			XMLReader parser = new org.apache.xerces.parsers.SAXParser();
			parser.setFeature("http://apache.org/xml/features/validation/dynamic",true);
			parser.setErrorHandler(handler);
			parser.setEntityResolver(handler);
			parser.setContentHandler(handler);
			parser.parse(resource);
		}
		catch(SAXException se)
		{
			Throwable e = se.getException();
			if(e == null)
				e = se;
			Log.log(Log.ERROR,CompletionInfo.class,e);
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,CompletionInfo.class,e);
		}

		info = handler.getCompletionInfo();
		completionInfo.put(resource,info);

		return info;
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < elements.size(); i++)
		{
			buf.append(elements.get(i));
			buf.append('\n');
		}
		return buf.toString();
	} //}}}

	//{{{ clone() method
	public Object clone()
	{
		return new CompletionInfo(
			html,
			(ArrayList)elements.clone(),
			(HashMap)elementHash.clone(),
			(ArrayList)entities.clone(),
			(HashMap)entityHash.clone(),
			(ArrayList)elementsAllowedAnywhere.clone()
		);
	} //}}}

	//{{{ Private members
	private static HashMap globs;
	private static HashMap completionInfo;

	//{{{ Class initializer
	static
	{
		globs = new HashMap();
		int i = 0;
		String glob;
		while((glob = jEdit.getProperty("xml.completion." + i + ".glob")) != null)
		{
			String info = jEdit.getProperty("xml.completion." + i + ".info");
			try
			{
				globs.put(new RE(MiscUtilities.globToRE(glob),
					RE.REG_ICASE),info);
			}
			catch(REException re)
			{
				Log.log(Log.ERROR,CompletionInfo.class,re);
			}

			i++;
		}

		completionInfo = new HashMap();
	} //}}}

	//}}}
}
