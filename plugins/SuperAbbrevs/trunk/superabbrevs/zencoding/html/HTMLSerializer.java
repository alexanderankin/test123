/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 jEdit contributors
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
import java.util.Map;
import java.util.Properties;

/**
 * @author Matthieu Casanova
 */
public class HTMLSerializer extends XMLSerializer
{
	private final Properties props;

	public HTMLSerializer(Properties props)
	{
		this.props = props;
	}

	@Override
	protected Map<String, String> getAttributes(Tag tag)
	{
		Map<String, String> attributes = new HashMap<String, String>();
		String baseName = "defaultattributes.html." + tag.name + '.';
		for (int i = 0; i < 100; i++)
		{
			String baseProperty = baseName + i;
			String attribute = props.getProperty(baseProperty);
			if (attribute != null)
			{
				String value = props.getProperty(baseProperty + ".value");
				attributes.put(attribute, value);
			}
			else break;
		}
		/*if ("a".equals(tag.name))
			attributes.put("href", null);
		else if ("select".equals(tag.name))
		{
			attributes.put("name", null);
			attributes.put("id", null);
		}
		else if ("option".equals(tag.name))
			attributes.put("value", null);
		else if ("base".equals(tag.name))
			attributes.put("href", null);
		else if ("acronym".equals(tag.name))
			attributes.put("title", null);
		else if ("bdo".equals(tag.name))
			attributes.put("dir", null);
		else if ("style".equals(tag.name))
			attributes.put("type", "text/css");
		else if ("script".equals(tag.name))
			attributes.put("type", "text/javascript");
		else if ("img".equals(tag.name))
		{
			attributes.put("src", null);
			attributes.put("alt", null);
		}
		else if ("iframe".equals(tag.name))
		{
			attributes.put("src", null);
			attributes.put("frameborder", "0");
		}
		else if ("embed".equals(tag.name))
		{
			attributes.put("src", null);
			attributes.put("type", null);
		}
		else if ("object".equals(tag.name))
		{
			attributes.put("data", null);
			attributes.put("type", null);
		}
		else if ("param".equals(tag.name))
		{
			attributes.put("name", null);
			attributes.put("value", null);
		}
		else if ("map".equals(tag.name))
			attributes.put("name", null);
		else if ("area".equals(tag.name))
		{
			attributes.put("shape", null);
			attributes.put("coords", null);
			attributes.put("href", null);
			attributes.put("alt", null);
		}
		else if ("link".equals(tag.name))
		{
			attributes.put("shape", null);
			attributes.put("coords", null);
			attributes.put("href", null);
			attributes.put("alt", null);
		}  */
		attributes.putAll(super.getAttributes(tag));
		return attributes;
	}
}
