/*
 * ConfigurableHyperlinksHandler.java - Handles hyperlinks.xml file
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012 Patrick Eibl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package gatchan.jedit.hyperlinks.configurable;

//{{{ Imports
import java.net.URL;
import java.util.*;
import java.io.StringReader;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import org.gjt.sp.util.XMLUtilities;
import org.gjt.sp.util.Log;
//}}}

/**
 * @author Patrick Eibl
 */
public class ConfigurableHyperlinksHandler extends DefaultHandler
{

	//{{{ resolveEntity() method
	public InputSource resolveEntity(String publicId, String systemId) 
	{
		// could have used EntityResolver2.resolveEntity(4),
		// but it's not always available 
		if(systemId.endsWith("hyperlinks.dtd"))
		{
			return new InputSource(new StringReader("<!-- -->"));
		}

		return null;
	} //}}}

	//{{{ characters() method
	public void characters(char[] c, int off, int len)
	{
		String tag = peekElement();
		if (tag.equals("CODE"))
			code.append(c, off, len);
		else if (tag.equals("REGEX"))
			regex.append(c, off, len);
		else if (tag.equals("TOOLTIP"))
			tooltip.append(c,off,len);
	} //}}}

	//{{{ startElement() method
	public void startElement(String uri, String localName,
				 String tag, Attributes attrs)
	{
		pushElement(tag);
		if(tag.equals("HYPERLINKSOURCE"))
		{
			sourceName = attrs.getValue("NAME");
			if(sources.containsKey(sourceName))
				Log.log(Log.ERROR, this, "Multiple HYPERLINKSOURCES with the same name");
		}
	} //}}}

	//{{{ endElement() method
	public void endElement(String uri, String localName, String name)
	{
		String tag = peekElement();

		if(name.equals(tag))
		{
			if (tag.equals("HYPERLINK"))
			{
				if(code.length()==0 || regex==null)
				{
					Log.log(Log.ERROR, this, "A HYPERLINK is missing code or regex tag");
					code.setLength(0);
					regex.setLength(0);
					tooltip.setLength(0);
					return;
				}
				Log.log(Log.MESSAGE, this, "New hyperlink for "+sourceName+"; regex:"+regex+", code:"+code+", tooltip:"+tooltip);
				ConfigurableHyperlinkData hyperlink =
					new ConfigurableHyperlinkData(code.toString(), regex.toString(), tooltip.toString());
				source.add(hyperlink);
				code.setLength(0);
				tooltip.setLength(0);
				regex.setLength(0);
			} else if (tag.equals("HYPERLINKSOURCE")) {
				source.trimToSize();
				sources.put(sourceName,source);
				source = new ArrayList<ConfigurableHyperlinkData>();
			}

			popElement();
		}
		else
		{
			// can't happen
			throw new InternalError();
		}
	} //}}}

	//{{{ startDocument() method
	public void startDocument()
	{
		code = new StringBuilder();
		regex = new StringBuilder();
		tooltip = new StringBuilder();
		stateStack = new Stack<String>();
		sources = new HashMap<String,ArrayList<ConfigurableHyperlinkData>>();
		source = new ArrayList<ConfigurableHyperlinkData>();
		try
		{
			pushElement(null);
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, e, e);
		}
	} //}}}
	
	//{{{ getSources() method
	public HashMap<String, ArrayList<ConfigurableHyperlinkData>> getSources()
	{
		return sources;
	}
	//}}}

	//{{{ Private members

	//{{{ Instance variables

	private String sourceName;
	private StringBuilder code;
	private StringBuilder regex;
	private StringBuilder tooltip;

	private Stack<String> stateStack;
	
	private HashMap<String,ArrayList<ConfigurableHyperlinkData>> sources;
	private ArrayList<ConfigurableHyperlinkData> source;

	//}}}

	//{{{ pushElement() method
	private String pushElement(String name)
	{
		name = (name == null) ? null : name.intern();

		stateStack.push(name);

		return name;
	} //}}}

	//{{{ peekElement() method
	private String peekElement()
	{
		return stateStack.peek();
	} //}}}

	//{{{ popElement() method
	private String popElement()
	{
		return stateStack.pop();
	} //}}}

	//}}}
}
