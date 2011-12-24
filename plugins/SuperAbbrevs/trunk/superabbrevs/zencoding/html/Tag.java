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

/**
 * @author Matthieu Casanova
 */
public class Tag implements Cloneable
{
	final String name;
	final LinkedList<Object> subTags;

	final Map<String, String> attributes;

	private String lastAttribute;
	
	//{{{ Tag constructors
	Tag()
	{
		this(null);
	}

	public Tag(String name)
	{
		this.name = name;
		subTags = new LinkedList<Object>();
		attributes = new HashMap();
	} //}}}

	public void addSubtag(Object o)
	{
		subTags.add(o);
	}

	public void removeSubtag(Object o)
	{
		subTags.remove(o);
	}
	
	public boolean hasAttributes()
	{
		return !attributes.isEmpty();
	}

	public String getName()
	{
		return name;
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
		cloned.subTags.addAll(subTags);
		return cloned;
	}

	@Override
	public String toString()
	{
		return name + '>';
	}
}
