/*
 * ConfigurableHyperlinkData.java - Wraps up data for a hyperlink
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
import java.util.regex.Pattern;
 
public class ConfigurableHyperlinkData
{
	public final String code;
	public final String regex;
	public final String tooltip;
	public final Pattern pattern;
	
	public ConfigurableHyperlinkData(String code, String regex, String tooltip)
	{
		this.code = code;
		this.regex = regex;
		this.tooltip = tooltip;
		this.pattern = Pattern.compile(regex);
	}
}
