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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * @author Matthieu Casanova
 */
public class Tag implements Cloneable
{
	private final String name;
	private final LinkedList<Tag> subTag;

	private final Map<String, String> attributes;

	private String lastAttribute;
	
	private static int index;

	//{{{ Tag constructors
	public Tag()
	{
		this(null);
	}

	public Tag(String name)
	{
		this.name = name;
		subTag = new LinkedList<Tag>();
		attributes = new HashMap();
	} //}}}

	public void addSubtag(Tag tag)
	{
		subTag.add(tag);
	}

	public void addAttribute(String name, String value)
	{
		lastAttribute = name;
		attributes.put(name, value);
	}

	public void appendAttribute(String name, String value, boolean space)
	{
		lastAttribute = name;
		String oldValue = attributes.get(name);
		if (oldValue == null)
			addAttribute(name, value);
		else
		{
			if (space)
				attributes.put(name, oldValue + ' ' + value);
			else
				attributes.put(name, oldValue + value);

		}
	}

	public void appendAttribute(String name, String value)
	{
		appendAttribute(name, value, false);
	}

	public String getLastAttribute()
	{
		return lastAttribute;
	}

	@Override
	public Tag clone()
	{
		Tag cloned = new Tag(name);
		cloned.attributes.putAll(attributes);
		cloned.lastAttribute = lastAttribute;
		cloned.subTag.addAll(subTag);
		return cloned;
	}

	@Override
	public String toString()
	{
		index = 1;
		StringBuilder builder = new StringBuilder();
		toString(-1, builder);
		return builder.toString();
	}

	public void toString(int indent, StringBuilder builder)
	{
		String tabs = getTabs(indent);
		if (name != null)
		{
//			if (indent > 0)
//			{
//				builder.append('\n').append(tabs);
//			}
			builder.append(tabs);
			builder.append('<').append(name);
			Set<Map.Entry<String, String>> entries = attributes.entrySet();
			for (Map.Entry<String, String> entry : entries)
			{
				builder.append(' ');
				builder.append(entry.getKey()).append("=\"");
				if (entry.getValue() == null)
				{
					builder.append('$').append(index++);
				}
				else
				{
					builder.append(entry.getValue());
				}

				builder.append('"');
			}
			builder.append('>');
		}



		if (subTag.isEmpty())
		{
			builder.append('$').append(index++);
		}
		else
		{
			int i = 0;
			for (Tag tag : subTag)
			{
				if (indent != -1 || i != 0)
					builder.append('\n');
				tag.toString(indent + 1, builder);
				i++;
			}
		}

		if (name != null)
		{
			if (!subTag.isEmpty())
				builder.append('\n').append(tabs);
			builder.append("</").append(name).append('>');
		}
	}

	//{{{ getTabs() method
	private String getTabs(int indent)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < indent; i++)
		{
			builder.append('\t');
		}
		return builder.toString();
	} //}}}
}
