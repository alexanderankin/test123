/*
 * CompletionInfo.java
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

import java.util.Hashtable;
import java.util.Vector;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

class CompletionInfo
{
	// if true, HTML syntax is supported (eg, case-insensitive tag names,
	// attributes with no values)
	boolean html;

	Vector elements;
	Hashtable elementHash;
	Vector entities;
	Hashtable entityHash;
	Vector ids;

	CompletionInfo(boolean html, Vector elements, Hashtable elementHash,
		Vector entities, Hashtable entityHash, Vector ids)
	{
		this.html = html;
		this.elements = elements;
		this.elementHash = elementHash;
		this.entities = entities;
		this.entityHash = entityHash;
		this.ids = ids;
	}

	static CompletionInfo getCompletionInfo(EditPane editPane)
	{
		CompletionInfo info = (CompletionInfo)editPane.getClientProperty(
			XmlPlugin.COMPLETION_INFO_PROPERTY);
		if(info != null)
			return info;
		else
			return getCompletionInfo(editPane.getBuffer().getMode().getName());
	}

	static CompletionInfo getCompletionInfo(String mode)
	{
		String resource = jEdit.getProperty("mode." + mode
			+ "." + XmlPlugin.COMPLETION_INFO_PROPERTY);
		if(resource == null)
			return null;

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
	}

	// private members
	private static Hashtable completionInfo = new Hashtable();
}
