/*
 * jEdit - Programmer's Text Editor
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2012 Patrick Eibl
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
package gatchan.jedit.hyperlinks.configurable;

import java.util.List;
import java.util.regex.Matcher;

import gatchan.jedit.hyperlinks.Hyperlink;
import gatchan.jedit.hyperlinks.HyperlinkSource;
import gatchan.jedit.hyperlinks.HyperlinksPlugin;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;

/**
 * This hyperlink source will search for the provided pattern.
 *
 * @author Patrick Eibl
 */
public class ConfigurableHyperlinkSource implements HyperlinkSource
{
	private Hyperlink currentLink;
	private final String name;
	private final List<ConfigurableHyperlinkData> dataArray;
	
	public ConfigurableHyperlinkSource(String name) {
		this.name = name;
		HyperlinksPlugin plugin = (HyperlinksPlugin)jEdit.getPlugin("gatchan.jedit.hyperlinks.HyperlinksPlugin");
		dataArray = plugin.getHyperlinkData(name);
	}

	@Override
	public Hyperlink getHyperlink(Buffer buffer, int caretPosition)
	{
		Hyperlink h;
		for(ConfigurableHyperlinkData data : dataArray)
		{
			h = getHyperlink(buffer, caretPosition, data);
			if(h!=null)
				return currentLink = h;
		}
		return currentLink = null;
	}
	
	private Hyperlink getHyperlink(Buffer buffer, int caretPosition, ConfigurableHyperlinkData data)
	{
		if (currentLink != null)
		{
			if (currentLink.getStartOffset() <= caretPosition && currentLink.getEndOffset() >= caretPosition)
			{
				return currentLink;
			}
		}
		int line = buffer.getLineOfOffset(caretPosition);
		int lineStart = buffer.getLineStartOffset(line);
		int lineLength = buffer.getLineLength(line);
		if (lineLength == 0)
		{
			currentLink = null;
			return null;
		}
		int offset = caretPosition - lineStart;
		String lineText = buffer.getLineText(line);
		if (offset == lineLength) offset--;
		Matcher matcher = data.pattern.matcher(lineText);
		
		while(matcher.find()) {
			if(offset > matcher.start() && offset < matcher.end()) {
				currentLink = new ConfigurableHyperlink(lineStart + matcher.start(), 
					lineStart + matcher.end(), line, matcher, data.code, data.tooltip);
				return currentLink;
			}
		}
		
		return currentLink = null;
	}
}
