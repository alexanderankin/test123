/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package superabbrevs.zencoding.html;

import java.util.Map;
import java.util.Set;

/**
 * @author Matthieu Casanova
 */
public class XMLSerializer implements ZenSerializer
{
	private int index;

	protected Map<String,String> getAttributes(Tag tag)
	{
		return tag.attributes;
	}
	
	public void toString(Tag tag, int indent, StringBuilder builder, boolean skipIndent)
	{
		String tabs = getTabs(indent);
		if (tag.name != null)
		{
			if (!skipIndent)
				builder.append(tabs);
			builder.append('<').append(tag.name);
			Map<String, String> attributes = getAttributes(tag);
			for (Map.Entry<String, String> stringStringEntry : attributes.entrySet())
			{
				builder.append(' ');
				builder.append(stringStringEntry.getKey()).append("=\"");
				String value = stringStringEntry.getValue();
				if (value == null)
				{
					builder.append('$').append(index++);
				}
				else
				{
					builder.append(value);
				}

				builder.append('"');
			}
			builder.append('>');
		}

		if (tag.subTags.isEmpty())
		{
			builder.append('$').append(index++);
		}
		else
		{
			int i = 0;
			boolean prevNodeIsText = false;
			for (Object o : tag.subTags)
			{
				if (o instanceof Tag)
				{

					Tag subTag = (Tag) o;
					if ((indent != -1 || i != 0) && !prevNodeIsText)
						builder.append('\n');
					toString(subTag, indent + 1, builder, prevNodeIsText);
					i++;
					prevNodeIsText = false;
				}
				else if (o instanceof String)
				{
					// inner text
					prevNodeIsText = true;
					builder.append(o);
				}
			}
		}

		if (tag.name != null)
		{
			if (!tag.subTags.isEmpty() && tag.subTags.getLast() instanceof Tag)
				builder.append('\n').append(tabs);
			builder.append("</").append(tag.name).append('>');
		}
	}
	
	
	@Override
	public String serialize(Tag tag)
	{
		index = 1;
		StringBuilder builder = new StringBuilder();
		toString(tag, -1, builder, false);
		return builder.toString();
	}


	//{{{ getTabs() method
	private static String getTabs(int indent)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < indent; i++)
		{
			builder.append('\t');
		}
		return builder.toString();
	} //}}}
}
